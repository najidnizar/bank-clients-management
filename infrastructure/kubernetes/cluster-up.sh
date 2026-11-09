#!/usr/bin/env bash
#
# Creates the local kind cluster "bank" (if it does not exist yet) and deploys the infrastructure
# in the namespace "bank": 4 PostgreSQL servers, Kafka, Keycloak and S3 storage, plus the HTTP entry point
# (Gateway API with Traefik). Can be run again safely.
#
# Requirements: Docker running, kind, kubectl and helm installed (brew install kind kubectl helm).
# Delete everything:  kind delete cluster --name bank

set -euo pipefail          # stop at the first error, on an unset variable, or on a failure inside a pipe
cd "$(dirname "$0")"       # run from this folder, wherever the script is called from

# 1. Cluster (skipped if it already exists)
if ! kind get clusters | grep -qx bank; then
  kind create cluster --config kind-config.yaml
fi

# 2. Keycloak realm: a ConfigMap built from the same JSON file as Docker Compose.
#    "--dry-run=client -o yaml | kubectl apply" = create it, or update it if it already exists.
kubectl apply -f infra/namespace.yaml
kubectl create configmap keycloak-realm -n bank \
  --from-file=realm-bank.json=../docker/keycloak/realm-bank.json \
  --dry-run=client -o yaml | kubectl apply -f -

# 3. Infrastructure (all manifests listed in infra/kustomization.yaml)
kubectl apply -k infra

# 4. Wait until every StatefulSet and Deployment is ready (first run: images are downloaded)
for workload in $(kubectl get statefulset,deployment -n bank -o name); do
  kubectl rollout status "$workload" -n bank --timeout=300s
done

# 5. HTTP entry point:
#    - the Gateway API resource definitions (CRDs): not built into Kubernetes, and not installed by the Traefik chart;
#      version supported by Traefik (see its documentation)
#    - Traefik, the implementation of the Gateway API
#    - our Gateway and routes
kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.6.3/standard-install.yaml
helm repo add traefik https://traefik.github.io/charts >/dev/null 2>&1 || true
helm upgrade --install traefik traefik/traefik --version 42.3.1 --namespace traefik --create-namespace \
  --values gateway/traefik-values.yaml --wait
kubectl apply -f gateway/gateway.yaml

kubectl get pods -n bank
echo "✅ Ready. Keycloak: http://keycloak.localhost  —  API: http://api.localhost (after ./deploy-services.sh)"

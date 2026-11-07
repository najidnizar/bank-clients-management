#!/usr/bin/env bash
#
# Creates the local kind cluster "bank" (if it does not exist yet) and deploys the infrastructure
# in the namespace "bank": 4 PostgreSQL servers, Kafka, Keycloak and S3 storage. Can be run again safely.
#
# Requirements: Docker running, kind and kubectl installed (brew install kind kubectl).
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

kubectl get pods -n bank
echo "✅ Ready. Keycloak: kubectl port-forward -n bank svc/keycloak 8180:8080"

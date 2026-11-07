#!/usr/bin/env bash
#
# Deploys (or updates) the 6 microservices in the namespace "bank" with Helm:
# one release per file in helm/values/, all using the generic chart helm/bank-service.
#
# Requirements: the cluster and its infrastructure (./cluster-up.sh), helm installed (brew install helm),
# and the images published on GitHub Container Registry (done by the CI on main).

set -euo pipefail
cd "$(dirname "$0")"

# "upgrade --install": installs the release the first time, upgrades it afterwards
for values in helm/values/*.yaml; do
  service=$(basename "$values" .yaml)
  helm upgrade --install "$service" helm/bank-service --namespace bank --values "$values"
done

# Wait until every microservice is ready (the readiness probes pass)
for values in helm/values/*.yaml; do
  kubectl rollout status "deployment/$(basename "$values" .yaml)" -n bank --timeout=300s
done

kubectl get pods -n bank
echo "✅ Services deployed. Gateway: kubectl port-forward -n bank svc/customer-api-gateway 8088:8088"

#!/usr/bin/env bash
#
# Creates the local kind cluster "bank" (if needed) and deploys the infrastructure in the namespace "bank":
# 4 PostgreSQL servers, Kafka, Keycloak and MinIO. Safe to run several times.
#
# Requirements: Docker running, kind and kubectl installed (brew install kind kubectl).

set -euo pipefail
cd "$(dirname "$0")"

CLUSTER=bank
NAMESPACE=bank

if ! kind get clusters | grep -qx "$CLUSTER"; then
  echo "▶ Creating kind cluster '$CLUSTER'..."
  kind create cluster --config kind-config.yaml
else
  echo "▶ Cluster '$CLUSTER' already exists"
fi
kubectl config use-context "kind-$CLUSTER" >/dev/null

echo "▶ Namespace and Keycloak realm"
kubectl apply -f infra/namespace.yaml
# The realm file is shared with Docker Compose; "create --dry-run | apply" makes the command repeatable
kubectl create configmap keycloak-realm -n "$NAMESPACE" \
  --from-file=realm-bank.json=../docker/keycloak/realm-bank.json \
  --dry-run=client -o yaml | kubectl apply -f -

echo "▶ Infrastructure"
kubectl apply -k infra

echo "▶ Waiting for the infrastructure to be ready (first run: images are downloaded, a few minutes)..."
for sts in customer-onboarding-db customer-kyc-db customer-document-db customer-account-db kafka minio; do
  kubectl rollout status "statefulset/$sts" -n "$NAMESPACE" --timeout=300s
done
kubectl rollout status deployment/keycloak -n "$NAMESPACE" --timeout=300s

echo
kubectl get pods -n "$NAMESPACE"
echo
echo "✅ Infrastructure ready. Keycloak: kubectl port-forward -n $NAMESPACE svc/keycloak 8180:8080"

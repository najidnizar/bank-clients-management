#!/usr/bin/env bash
# Loads demo customers through the public API (gateway), like a real client would:
# validation, security, Kafka events (a KYC case per customer) and traces all apply.
# Idempotent: an existing customer (same email -> HTTP 409) is skipped, so it can be run again.
#
# Usage (from the repository root, with the stack running in Docker Compose):
#   ./scripts/seed-demo-data.sh
#   GATEWAY_URL=http://api.localhost ./scripts/seed-demo-data.sh   # other environment
set -euo pipefail

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8088}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CUSTOMERS_FILE="$SCRIPT_DIR/demo-data/customers.csv"

command -v jq >/dev/null || { echo "jq is required (macOS: brew install jq)" >&2; exit 1; }

ADVISOR_TOKEN="$("$SCRIPT_DIR/get-token.sh" advisor)"
[[ -n "$ADVISOR_TOKEN" ]] || { echo "Could not get a token for 'advisor' (is Keycloak running?)" >&2; exit 1; }

created=0
skipped=0

# tail -n +2 skips the CSV header; process substitution keeps the counters in this shell.
while IFS=, read -r first_name last_name date_of_birth email phone_number; do
  body="$(jq -n \
    --arg firstName "$first_name" --arg lastName "$last_name" \
    --arg dateOfBirth "$date_of_birth" --arg email "$email" --arg phoneNumber "$phone_number" \
    '{firstName: $firstName, lastName: $lastName, dateOfBirth: $dateOfBirth,
      email: $email, phoneNumber: $phoneNumber}')"

  status="$(curl -s -o /dev/null -w '%{http_code}' -X POST "$GATEWAY_URL/api/v1/customers" \
    -H "Authorization: Bearer $ADVISOR_TOKEN" \
    -H 'Content-Type: application/json' \
    -d "$body")"

  case "$status" in
    201) created=$((created + 1)); echo "created   $email" ;;
    409) skipped=$((skipped + 1)); echo "existing  $email" ;;
    *)   echo "error: HTTP $status for $email" >&2; exit 1 ;;
  esac
done < <(tail -n +2 "$CUSTOMERS_FILE")

echo "Customers: $created created, $skipped already present."

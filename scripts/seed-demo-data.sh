#!/usr/bin/env bash
# Loads demo customers and accounts through the public API (gateway), like a real client would:
# validation, security, Kafka events (a KYC case per customer) and traces all apply.
# Idempotent: existing customers (same email -> HTTP 409) and customers that already have
# accounts are skipped, so it can be run again.
#
# Usage (from the repository root, with the stack running in Docker Compose):
#   ./scripts/seed-demo-data.sh
#   GATEWAY_URL=http://api.localhost ./scripts/seed-demo-data.sh   # other environment
set -euo pipefail

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8088}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CUSTOMERS_FILE="$SCRIPT_DIR/demo-data/customers.csv"
ACCOUNTS_FILE="$SCRIPT_DIR/demo-data/accounts.csv"

command -v jq >/dev/null || { echo "jq is required (macOS: brew install jq)" >&2; exit 1; }

ADVISOR_TOKEN="$("$SCRIPT_DIR/get-token.sh" advisor)"
[[ -n "$ADVISOR_TOKEN" ]] || { echo "Could not get a token for 'advisor' (is Keycloak running?)" >&2; exit 1; }
ADMIN_TOKEN="$("$SCRIPT_DIR/get-token.sh" admin)"

trap 'echo "error: line $LINENO failed" >&2' ERR

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

# A new account is PENDING; an admin moves it to its demo status.
status_path() {
  case "$1" in
    PENDING) ;;
    ACTIVE) echo ACTIVE ;;
    BLOCKED) echo ACTIVE BLOCKED ;;
    CLOSED) echo ACTIVE CLOSED ;;
  esac
}

accounts_created=0
accounts_skipped=0

while IFS=, read -r email accounts; do
  email_param="$(jq -rn --arg email "$email" '$email | @uri')"
  customer_id="$(curl -sf "$GATEWAY_URL/api/v1/customers?email=$email_param&page=0&size=1" \
    -H "Authorization: Bearer $ADVISOR_TOKEN" | jq -r '.content[0].id // empty')"
  [[ -n "$customer_id" ]] || { echo "error: customer $email not found" >&2; exit 1; }

  existing="$(curl -sf "$GATEWAY_URL/api/v1/accounts/customer/$customer_id?page=0&size=1" \
    -H "Authorization: Bearer $ADVISOR_TOKEN" | jq -r '.totalElements')"
  if [[ "$existing" != "0" ]]; then
    accounts_skipped=$((accounts_skipped + 1))
    echo "existing  accounts of $email"
    continue
  fi

  IFS=';' read -ra specs <<< "$accounts"
  for spec in "${specs[@]}"; do
    IFS=: read -r account_type currency_code status <<< "$spec"
    body="$(jq -n --arg customerId "$customer_id" --arg accountType "$account_type" \
      --arg currencyCode "$currency_code" \
      '{customerId: $customerId, accountType: $accountType, currencyCode: $currencyCode}')"

    account_id="$(curl -sf -X POST "$GATEWAY_URL/api/v1/accounts" \
      -H "Authorization: Bearer $ADVISOR_TOKEN" \
      -H 'Content-Type: application/json' \
      -d "$body" | jq -r '.id')"

    for next_status in $(status_path "$status"); do
      curl -sf -o /dev/null -X PATCH "$GATEWAY_URL/api/v1/accounts/$account_id/status?status=$next_status" \
        -H "Authorization: Bearer $ADMIN_TOKEN"
    done

    accounts_created=$((accounts_created + 1))
    echo "created   $account_type $currency_code account ($status) for $email"
  done
done < <(tail -n +2 "$ACCOUNTS_FILE")

echo "Accounts: $accounts_created created, $accounts_skipped customers already had accounts."

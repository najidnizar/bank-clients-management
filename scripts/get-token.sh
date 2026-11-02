#!/usr/bin/env bash
#
# Get an access token from the local Keycloak (realm "bank") for a demo user.
#
# Usage:
#   ./scripts/get-token.sh [user] [--decode]
#
#   user       advisor (default) | compliance | admin   (password = user name)
#   --decode   print the token claims (issuer, audience, roles...) instead of the token
#
# Examples:
#   TOKEN=$(./scripts/get-token.sh advisor)
#   curl -H "Authorization: Bearer $TOKEN" http://localhost:8088/api/v1/customers
#
#   ./scripts/get-token.sh compliance --decode
#
# How it works:
#   1. POST to the OpenID Connect token endpoint of the realm with the "password" grant:
#      the client "bank-cli" (development only) exchanges a username/password for a token.
#   2. The JSON response contains "access_token", a JWT made of three Base64URL parts:
#      header.claims.signature. The claims are only encoded, not encrypted: anyone can
#      read them, nobody can change them without breaking the signature.

set -euo pipefail

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8180}"
REALM="bank"
CLIENT_ID="bank-cli"
USER="${1:-advisor}"

response=$(curl -s -X POST "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
  -d grant_type=password \
  -d client_id="$CLIENT_ID" \
  -d username="$USER" \
  -d password="$USER")

if [[ "${2:-}" == "--decode" ]]; then
  echo "$response" | python3 -c '
import sys, json, base64
data = json.load(sys.stdin)
if "access_token" not in data:
    sys.exit("Keycloak error: " + json.dumps(data))
claims_part = data["access_token"].split(".")[1]
claims = json.loads(base64.urlsafe_b64decode(claims_part + "=="))   # restore Base64 padding
for key in ("iss", "aud", "preferred_username", "exp"):
    print(f"{key}: {claims.get(key)}")
print("roles:", claims.get("realm_access", {}).get("roles"))
'
else
  echo "$response" | python3 -c '
import sys, json
data = json.load(sys.stdin)
if "access_token" not in data:
    sys.exit("Keycloak error: " + json.dumps(data))
print(data["access_token"])
'
fi

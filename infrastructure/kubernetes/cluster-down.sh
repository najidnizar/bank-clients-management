#!/usr/bin/env bash
# Deletes the local kind cluster "bank" and everything in it (including the database volumes).
set -euo pipefail
kind delete cluster --name bank

#!/usr/bin/env bash
# Opens a pull request from the current branch to main with the GitHub CLI, then optionally merges it.
#
# Usage (from anywhere in the repository): ./scripts/create-pr.sh
# Requires the GitHub CLI: brew install gh && gh auth login
set -euo pipefail

BASE_BRANCH="main"

command -v gh >/dev/null || { echo "GitHub CLI not found: brew install gh && gh auth login" >&2; exit 1; }
gh auth status >/dev/null 2>&1 || { echo "Not logged in to GitHub: gh auth login" >&2; exit 1; }

branch="$(git branch --show-current)"
if [[ -z "$branch" || "$branch" == "$BASE_BRANCH" ]]; then
  echo "Switch to a feature branch first (current: ${branch:-detached HEAD})." >&2
  exit 1
fi

if [[ -n "$(git status --porcelain)" ]]; then
  echo "Warning: you have uncommitted changes; they will not be part of the pull request."
fi

git push -u origin "$branch"

default_title="$(git log -1 --pretty=%s)"
read -r -p "Title [$default_title]: " title
title="${title:-$default_title}"

echo "Description (finish with an empty line):"
body=""
while IFS= read -r line; do
  [[ -z "$line" ]] && break
  body+="$line"$'\n'
done

gh pr create --base "$BASE_BRANCH" --head "$branch" --title "$title" --body "$body"

read -r -p "Merge it now? [y/N] " answer
if [[ "$answer" == "y" || "$answer" == "Y" ]]; then
  gh pr merge "$branch" --merge --delete-branch
  git pull
  echo "Merged; you are now on $(git branch --show-current)."
else
  echo "Pull request left open."
fi

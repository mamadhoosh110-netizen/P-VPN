#!/usr/bin/env bash
set -euo pipefail

# The source ZIP cannot carry Git's mode-160000 submodule entries. When those
# entries are absent, fetch the exact upstream revisions used by this fork.
ensure_checkout() {
  local path="$1"
  local repository="$2"
  local revision="$3"
  local sentinel="$4"

  if [[ -f "$path/$sentinel" ]]; then
    return
  fi

  rm -rf "$path"
  git clone --filter=blob:none --no-checkout "$repository" "$path"
  git -C "$path" checkout --detach "$revision"
}

ensure_checkout \
  "AndroidLibXrayLite" \
  "https://github.com/2dust/AndroidLibXrayLite.git" \
  "b5a377087e74eaa34e86718710b04c50f4b767f5" \
  "go.mod"

ensure_checkout \
  "hev-socks5-tunnel" \
  "https://github.com/heiher/hev-socks5-tunnel.git" \
  "c5584feca8f6df70ee53eb2879e034b849a3d15a" \
  "build.mk"

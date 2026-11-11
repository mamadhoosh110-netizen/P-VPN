
#!/usr/bin/env bash
set -euo pipefail

ensure_checkout() {
  local path="$1"
  local repository="$2"
  local revision="$3"
  local sentinel="$4"

  # Reuse only a real Git checkout with its required source file.
  if [[ -d "$path/.git" && -f "$path/$sentinel" ]]; then
    git -C "$path" submodule update --init --recursive
    return
  fi

  # ZIP-extracted folders are not valid Git submodule checkouts.
  rm -rf "$path"

  git clone --filter=blob:none --no-checkout "$repository" "$path"
  git -C "$path" checkout --detach "$revision"
  git -C "$path" submodule update --init --recursive

  test -f "$path/$sentinel"
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

# Verify required native dependencies.
for module in yaml lwip hev-task-system; do
  test -f "hev-socks5-tunnel/third-part/$module/Android.mk" || {
    echo "Missing native dependency: $module"
    exit 1
  }
done

echo "All native submodules are ready."

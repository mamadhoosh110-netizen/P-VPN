
#!/usr/bin/env bash
set -euo pipefail

ensure_checkout() {
  local path="$1"
  local repository="$2"
  local revision="$3"
  local sentinel="$4"

  if [[ ! -e "$path/.git" || ! -f "$path/$sentinel" ]]; then
    rm -rf "$path"
    git clone --filter=blob:none "$repository" "$path"
  fi

  git -C "$path" checkout --detach "$revision"

  echo "Syncing nested submodules in $path"

  git -C "$path" submodule sync --recursive

  git -C "$path" submodule update \
    --init --recursive --force --checkout --jobs 4

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

echo "Verifying Hev native dependencies..."

for module in yaml lwip hev-task-system; do
  module_dir="hev-socks5-tunnel/third-part/$module"

  if [[ ! -f "$module_dir/Android.mk" ]]; then
    echo "::error::Missing $module_dir/Android.mk"
    git -C hev-socks5-tunnel submodule status --recursive || true
    exit 1
  fi
done

echo "All pinned upstream submodules and native dependencies are ready."

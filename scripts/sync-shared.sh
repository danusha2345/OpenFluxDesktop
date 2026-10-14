#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
expected=1259fe0afebd231e1c44688ba38551e1a86354b0
[[ "$(git -C shared rev-parse HEAD)" = "$expected" ]] || { echo 'Unsupported shared revision' >&2; exit 1; }
patch="$PWD/patches/shared-full-tunnel.patch"
if git -C shared apply --reverse --check "$patch" 2>/dev/null; then exit 0; fi
git -C shared apply --check "$patch"
git -C shared apply "$patch"

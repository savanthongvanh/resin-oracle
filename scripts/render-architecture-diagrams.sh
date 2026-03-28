#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ARCH_DIR="${ROOT_DIR}/docs/architecture"

shopt -s nullglob
sources=("${ARCH_DIR}"/*.mmd)

if [[ ${#sources[@]} -eq 0 ]]; then
  echo "No Mermaid diagram sources found in ${ARCH_DIR}" >&2
  exit 1
fi

for source in "${sources[@]}"; do
  output="${source%.mmd}.png"
  echo "Rendering $(basename "${source}") -> $(basename "${output}")"
  npx -y @mermaid-js/mermaid-cli \
    -i "${source}" \
    -o "${output}" \
    -w 1800 \
    -b white
done

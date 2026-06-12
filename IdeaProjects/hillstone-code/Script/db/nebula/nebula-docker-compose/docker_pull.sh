#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

set -a
source "${script_dir}/.env"
set +a

: "${NEBULA_TAG:?}"

images=(
  nebula-metad
  nebula-storaged
  nebula-graphd
  nebula-console
)

for image in "${images[@]}"; do
  docker pull "vesoft/${image}:${NEBULA_TAG}"
done

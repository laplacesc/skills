#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

set -a
source "${script_dir}/.env"
set +a

: "${NEBULA_TAG:?}"
: "${NEBULA_PASSWORD:?}"
: "${NODE1_IP:?}"

docker run --rm -it --network=host \
  "vesoft/nebula-console:${NEBULA_TAG}" \
  -u "${USER}" \
  -p "${NEBULA_PASSWORD}" \
  --addr="${NODE1_IP}" --port=9669

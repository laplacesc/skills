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
: "${NODE2_IP:?}"
: "${NODE3_IP:?}"

docker run --rm -it --network=host \
  "vesoft/nebula-console:${NEBULA_TAG}" \
  -u "${USER}" \
  -p "${NEBULA_PASSWORD}" \
  --addr="${NODE1_IP}" --port=9669 \
  -e "ADD HOSTS ${NODE1_IP}:9779,${NODE2_IP}:9779,${NODE3_IP}:9779"

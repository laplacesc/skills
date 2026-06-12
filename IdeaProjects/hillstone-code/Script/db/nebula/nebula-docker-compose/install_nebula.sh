#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

set -a
source "${script_dir}/.env"
set +a

: "${NEBULA_TAG:?}"

envsubst < "${script_dir}/docker-stack-template.yaml" > "${script_dir}/docker-stack.yaml"
docker stack deploy -c "${script_dir}/docker-stack.yaml" "${NEBULA_STACK:-nebula}"

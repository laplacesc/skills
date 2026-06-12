#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

set -a
source "${script_dir}/.env"
set +a

docker stack rm "${NEBULA_STACK:-nebula}"

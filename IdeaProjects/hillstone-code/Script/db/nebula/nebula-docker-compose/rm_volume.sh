#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

set -a
source "${script_dir}/.env"
set +a

rm_cmd='docker volume ls -q --filter name=nebula | xargs -r docker volume rm'

ssh_opts=(
  -o StrictHostKeyChecking=no
  -o UserKnownHostsFile=/dev/null
)

run_remote() {
  local node_name="$1"
  local node_ip="$2"
  local node_user="$3"
  local node_password="${4:-}"

  if [[ -n "${node_password}" ]]; then
    if ! command -v sshpass >/dev/null 2>&1; then
      echo "sshpass not found; cannot use password auth for ${node_name} (${node_ip})." >&2
      return 1
    fi
    sshpass -p "${node_password}" ssh "${ssh_opts[@]}" "${node_user}@${node_ip}" "${rm_cmd}"
  else
    ssh -o BatchMode=yes "${ssh_opts[@]}" "${node_user}@${node_ip}" "${rm_cmd}"
  fi
}

run_remote "${NODE1_NAME:-node1}" "${NODE1_IP:?}" "${NODE1_USER:-${USER}}" "${NODE1_PASSWORD:-}"
run_remote "${NODE2_NAME:-node2}" "${NODE2_IP:?}" "${NODE2_USER:-${USER}}" "${NODE2_PASSWORD:-}"
run_remote "${NODE3_NAME:-node3}" "${NODE3_IP:?}" "${NODE3_USER:-${USER}}" "${NODE3_PASSWORD:-}"

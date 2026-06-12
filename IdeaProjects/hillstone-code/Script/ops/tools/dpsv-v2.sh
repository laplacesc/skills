#!/bin/bash

# author JunXian Wu
# created 2024-12-04

log() {
  printf "%s - %s\n" "$(date '+%Y-%m-%d %H:%M:%S')" "$*"
}

uninstall_pv() {
  local service
  service="$(basename "$1")"
  log "开始卸载pv"
  local pv
  for pv in $(kubectl get pv --no-headers | grep -E "^$service" | grep Released | awk '{print $1}'); do
    kubectl patch pv "$pv" -p '{"metadata":{"finalizers":null}}'
    kubectl delete pv "$pv"
  done
}

install_pv() {
  local service_dir=$1
  local service_address=$2
  local service_path=$3

  #  ssh root@"$service_address" <<EOF
  #mkdir -p $service_path$(basename "$service_dir")
  #EOF

  if [[ -f "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml ]]; then
    log "开始安装pv"
    local pv
    local vm
    local sc
    local am
    pv=$(yq '.spec.volumeName' "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml)
    vm=$(yq '.spec.volumeMode' "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml)
    sc=$(yq '.spec.storageClassName' "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml)
    am=$(yq '.spec.accessModes[0]' "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml)
    mkdir -p /u01/prod/"$(basename "$service_dir")"
    kubectl apply -f - <<EOF
apiVersion: v1
kind: PersistentVolume
metadata:
  name: $pv
spec:
  accessModes:
  - $am
  capacity:
    storage: 500Gi
  nfs:
    path: $service_path$(basename "$service_dir")
    server: $service_address
  persistentVolumeReclaimPolicy: Retain
  storageClassName: $sc
  volumeMode: $vm
EOF
  fi
}

recycling_pv() {
  local service
  local pv
  service="$(basename "$1")"
  log "开始回收pv"
  for pv in $(kubectl get pv --no-headers | grep -E "^$service" | grep Released | awk '{print $1}'); do
    kubectl patch pv "$pv" -p '{"spec":{"claimRef": null}}'
    kubectl patch pv "$pv" -p '{"status":{"phase":"Available"}}'
  done
}

uninstall_pvc() {
  local service_dir=$1
  local service
  service="$(basename "$1")"
  local namespace
  namespace1=$(yq e '.namespace.*' "$service_dir"/chart/"$(basename "$service_dir")"/values.yaml)
  namespace2=$(yq e '.namespace' "$service_dir"/chart/"$(basename "$service_dir")"/values.yaml)
  namespace=${namespace1:-$namespace2}
  log "开始卸载pvc. namespace: " "$namespace"
  local pvc
  for pvc in $(kubectl get pvc -n "$namespace" | grep -E "^$service" | awk '{print $1}'); do
    kubectl patch pvc "$pvc" -n "$namespace" -p '{"metadata":{"finalizers":null}}'
    kubectl delete pvc "$pvc" -n "$namespace"
  done
}

install_pvc() {
  local service_dir=$1
  local namespace
  namespace1=$(yq e '.namespace.*' "$service_dir"/chart/"$(basename "$service_dir")"/values.yaml)
  namespace2=$(yq e '.namespace' "$service_dir"/chart/"$(basename "$service_dir")"/values.yaml)
  namespace=${namespace1:-$namespace2}
  if [[ -f "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml ]]; then
    log "开始安装pvc"
    yq e ".metadata.namespace = \"$namespace\"" -i "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml
    kubectl apply -f "$service_dir"/chart/"$(basename "$service_dir")"/templates/pvc.yaml
  fi
}

delete_probe() {
  local service_dir=$1
  if [ "$flag_save_probe" == false ]; then
    log "开始删除探针"
    local deployment
    deployment="$service_dir"/chart/"$(basename "$service_dir")"/templates/deployment.yaml
    # 备份原文件
    if [ ! -f "${deployment}.bak" ]; then
      cp -f "$deployment" "${deployment}.bak"
    fi
    # 删除livenessProbe行及之后的17行
    local lpRowCount
    local rpRowCount
    lpRowCount=$(yq e '.spec.template.spec.containers[0].livenessProbe' "$deployment" | wc -l)
    sed -i "/livenessProbe:/,+${lpRowCount}d" "$deployment"
    rpRowCount=$(yq e '.spec.template.spec.containers[0].readinessProbe' "$deployment" | wc -l)
    sed -i "/readinessProbe:/,+${rpRowCount}d" "$deployment"
    log "探针删除完成，原文件备份为 ${deployment}.bak"
  else
    log "跳过删除探针"
  fi
}

assign_replica_count() {
  local service_dir=$1
  if [[ -n "$flag_replica_count" ]]; then
    log "修改副本数" "$flag_replica_count"
    sed -i "s/replicaCount: [0-9]\+/replicaCount: $flag_replica_count/" "$service_dir"/chart/"$(basename "$service_dir")"/values.yaml
  else
    log "跳过修改副本数"
  fi
}

assign_node() {
  local service_dir=$1
  if [[ -n "$flag_node_name" ]]; then
    log "指定节点" "$flag_node_name"
    if [ "$(yq '.spec.template.spec.nodeName' "$service_dir"/chart/"$(basename "$service_dir")"/templates/deployment.yaml)" != "null" ]; then
      sed -i "s/^      nodeName:\s\+\S\+/      nodeName: $flag_node_name/" "$service_dir"/chart/"$(basename "$service_dir")"/templates/deployment.yaml
    else
      sed -i "s/^    spec:/    spec:\n      nodeName: $flag_node_name/" "$service_dir"/chart/"$(basename "$service_dir")"/templates/deployment.yaml
    fi
  else
    log "未指定节点"
  fi
}

uninstall_service() {
  log "开始卸载service"
  helm uninstall "$(basename "$1")"
}

build_image() {
  local service_dir=$1
  local image
  image=$(grep -oP '(?<=image:\s)\K[\S]+' "$service_dir"/chart/"$(basename "$service_dir")"/values.yaml)
  docker build -t "$image" "$service_dir"/docker
}

deploy_service() {
  local service_dir=$1
  log service_dir: "$service_dir"
  uninstall_service "$service_dir"
  log "开始在本节点构造镜像"
  build_image "$service_dir"
  log "开始部署服务"
  assign_replica_count "$service_dir"
  delete_probe "$service_dir"
  assign_node "$service_dir"
  helm install "$(basename "$service_dir")" "$service_dir"/chart/"$(basename "$service_dir")"
}

build_image_on_node2() {
  local service_dir=$1
  log "开始在node2节点构造镜像"
  ssh root@node2 <<EOF
rm -rf "$service_dir"
mkdir -p $(dirname "$service_dir")
EOF
  scp -rpq "$service_dir" root@node2:"$(dirname "$service_dir")"
  ssh root@node2 <<EOF
    $(declare -f build_image)
    build_image "$service_dir"
EOF
}

deploy_service97() {
  local service_dir=$1
  log service_dir: "$service_dir"
  uninstall_service "$service_dir"
  uninstall_pvc "$service_dir"
  if [ "$flag_reinstall_pvc" == true ]; then
    uninstall_pv "$service_dir"
    # 如果目录不存在，跑到指定的 server 上执行 mkdir -p /u01/prod/ti-library
    install_pv "$service_dir" 10.182.139.97 /u01/prod/
  else
    log "跳过卸载重装pv"
    recycling_pv "$service_dir"
  fi
  # install_pvc "$service_dir"
  build_image_on_node2 "$service_dir"
  deploy_service "$service_dir"
}

deploy_service41() {
  local service_dir=$1
  log service_dir: "$service_dir"
  local pwd
  pwd=$(pwd)
  uninstall_service "$service_dir"
  uninstall_pvc "$service_dir"
  if [ "$flag_reinstall_pvc" == true ]; then
    uninstall_pv "$service_dir"
    # 如果目录不存在，跑到指定的 server 上执行 mkdir -p /u01/prod/ti-library
    install_pv "$service_dir" 10.185.232.21 /u01/prod/
  else
    log "跳过卸载重装pv"
    recycling_pv "$service_dir"
  fi
  install_pvc "$service_dir"
  assign_replica_count "$service_dir"
  delete_probe "$service_dir"
  assign_node "$service_dir"
  cd "$service_dir"/chart && dwne "$(basename "$service_dir")" "$(basename "$service_dir")" && cd "$pwd"
}

get_env() {
  local ifconfig_output
  # 获取 ifconfig 的输出
  ifconfig_output=$(ifconfig)
  # 检查 IP 地址 10.185.224.41 是否存在
  if echo "$ifconfig_output" | grep -oP 'inet \K[\d.]+' | grep -qx "10.185.224.41"; then
    echo "41"
  # 检查 IP 地址 10.182.139.97 是否存在
  elif echo "$ifconfig_output" | grep -oP 'inet \K[\d.]+' | grep -qx "10.180.139.97"; then
    echo "97"
  else
    # 处理未匹配的情况
    echo "没有匹配到环境"
  fi
}

deploy_service_with_env() {
  local env
  env=$(get_env)
  log "匹配环境: ""$env"
  if [[ $env == "41" ]]; then
    deploy_service41 "$*"
  elif [[ $env == "97" ]]; then
    deploy_service97 "$*"
  else
    deploy_service "$*"
  fi
}

# ---------- 主方法 ----------

flag_reinstall_pvc=false
flag_save_probe=false
flag_replica_count=1
flag_node_name=

# 使用 getopts 解析参数
while getopts "hvpr:n:" opt; do
  case $opt in
  h)
    printf "使用方法：\n"
    printf "  [-h] 显示帮助信息\n"
    printf "  [-v] 卸载重装PV，默认不重装回收原来的PV\n"
    printf "  [-p] 保留探针，默认删除\n"
    printf "  [-r replicaCount] 指定副本数，默认为1\n"
    printf "  [-n nodeName] 指定节点\n"
    printf "  folder1/zip1 ... folder2/zip2 目录或压缩文件路径，支持相对路径，支持多个\n"
    printf "\n如需指定节点，从以下选择输入，以下为当前K8S集群的所有节点名称\n"
    kubectl get nodes --no-headers | awk -F'   ' '{print $1}'
    exit 0
    ;;
  v)
    printf "参数启用，卸载重装PV\n"
    flag_reinstall_pvc=true
    ;;
  p)
    printf "参数启用，保留探针\n"
    flag_save_probe=true
    ;;
  r)
    printf "参数启用，指定副本数：%s\n" "$OPTARG"
    flag_replica_count=$OPTARG
    ;;
  n)
    printf "参数启用，指定节点：%s\n" "$OPTARG"
    flag_node_name=$OPTARG
    ;;
  *)
    printf "未知参数：%s\n" "$opt"
    exit 1
    ;;
  esac
done

# 移除已解析的参数
shift $((OPTIND - 1))

for arg in "$@"; do
  if [ -d "$arg" ]; then
    deploy_service_with_env "$(realpath "$arg")"
    log
  elif unzip -tq "$arg"; then
    unzip -oq "$arg" -d "$(dirname "$(realpath "$arg")")"
    deploy_service_with_env "$(dirname "$(realpath "$arg")")"/"$(basename -s .zip "$arg")"
    log
  fi
done

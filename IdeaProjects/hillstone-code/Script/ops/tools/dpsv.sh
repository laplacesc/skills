#!/bin/bash

# author JunXian Wu
# created 2024-12-04

uninstall_service97() {
  service_arr=("$@")
  for service in "${service_arr[@]}"; do
    echo "开始卸载service"
    helm uninstall "$service"
    # kubectl patch pvc {} -p "{\"metadata\":{\"finalizers\":null}}" &&
    echo "开始卸载pvc"
    kubectl get pvc -n cloud-ti | grep "$service" | awk '{print $1}' | xargs -I{} bash -c 'kubectl delete pvc {} -n cloud-ti'
    echo "开始卸载pv"
    kubectl get pv | grep "$service" | awk '{print $1}' | xargs -I{} bash -c 'kubectl delete pv {}'
  done
}

# 在service文件夹内
install_pvc97() {
  echo "开始安装pv"
  service_arr=("$@")
  for service in "${service_arr[@]}"; do
    if [[ -f "$service"/templates/pvc.yaml ]]; then
      pv=$(grep -oP "volumeName: \K[\w-]+" "$service"/templates/pvc.yaml)
      echo pv: "$pv"
      mkdir -p /u01/prod/$service
      kubectl apply -f - <<EOF
apiVersion: v1
kind: PersistentVolume
metadata:
  name: $pv
spec:
  accessModes:
  - ReadWriteMany
  capacity:
    storage: 500Gi
  nfs:
    path: /u01/prod/$service
    server: 10.182.139.97
  persistentVolumeReclaimPolicy: Retain
  storageClassName: inexpensive-nas
  volumeMode: Filesystem
EOF
      # kubectl apply -f "$service"/templates/pvc.yaml
    fi
  done
}

delete_probe() {
  echo "开始删除探针"
  local service=$1
  # 定义目标文件路径
  local deployment="$service"/templates/deployment.yaml
  # 备份原文件
  cp "$deployment" "${deployment}.bak"
  # 删除livenessProbe行及之后的17行
  sed -i '/livenessProbe:/,+17d' "$deployment"
  echo "探针删除完成，原文件已备份为 ${deployment}.bak"
}

deploy_service97() {
  dir=$1
  service=$2
  scp -r "$service".zip root@node2:"$dir"
  uninstall_service97 "$service"
  cd "$dir" && rm -rf "$service" && unzip -oq "$service".zip && image=$(grep 'image: ' "$service"/chart/"$service"/values.yaml | sed 's/image: //g;s/\r//g') && cd "$service"/docker && docker build -t "$image" .
  ssh root@node2 <<EOF
cd "$dir" && rm -rf "$service" && unzip -oq "$service".zip && cd "$service"/docker && docker build -t "$image" .
EOF
  cd ../chart/ && install_pvc97 "$service" && delete_probe "$service" && helm install "$service" "$service"
}

deploy_service41() {
  dir=$1
  service=$2
  cd "$dir" && rm -rf "$service" && unzip -oq "$service".zip && cd "$service"/chart && dwne "$service" "$service"
}

deploy_service() {
  dir=$1
  service=$2
  helm uninstall "$service"
  cd "$dir" && rm -rf "$service" && unzip -oq "$service".zip && image=$(grep 'image: ' "$service"/chart/"$service"/values.yaml | sed 's/image: //g;s/\r//g') && cd "$service"/docker && docker build -t "$image" .
  cd ../chart/ && helm install "$service" "$service"
}

get_env() {
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
  dir=$1
  service=$2
  env=$(get_env)
  echo "匹配环境: ""$env"
  if [[ $env == "41" ]]; then
    deploy_service41 "$dir" "$service"
  elif [[ $env == "97" ]]; then
    deploy_service97 "$dir" "$service"
  else
    deploy_service "$dir" "$service"
  fi
}

pwd=$(pwd)
for arg in "$@"; do
  cd "$pwd" && argDir=$(dirname "$(realpath "$arg")")
  if [ -d "$arg" ]; then
    argDir=$(realpath "$arg")
    readarray -t service_arr < <(find "$argDir" -type f -name "*.zip" -exec basename -s .zip {} +)
    echo arg: "$arg", argDir: "$argDir", service_arr: "${service_arr[@]}"
    for service in "${service_arr[@]}"; do
      if unzip -tq "$argDir"/"$service"".zip"; then
        deploy_service_with_env "$argDir" "$service"
        echo
      fi
    done
  else
    service=$(basename -s .zip "$arg")
    echo arg: "$arg", argDir: "$argDir", service: "$service"
    deploy_service_with_env "$argDir" "$service"
    echo
  fi
done

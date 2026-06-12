#!/bin/bash

# author JunXian Wu
# created 2024-12-04

# 将arthas.sh与需要热更新的Java文件传至指定Pod内

# kubectl cp arthas.sh cloud-ti/ti-library-deployment-57bc79bcc5-sbdvv:/tmp/ -c library
# kubectl cp xxx.java cloud-ti/ti-library-deployment-57bc79bcc5-sbdvv:/tmp/ -c library

# 在Pod中运行脚本

# sh arthas.sh gateway /test/ApiCheckService.java
# arg 1: jar包名 支持模糊查询 例如 analysis、gateway 即可
# arg 2: java file path，本地java文件路径，文件名必须和类同名

# kubectl exec -it ti-library-deployment-57bc79bcc5-sbdvv -n cloud-ti -c library sh /tmp/arthas.sh library /tmp/xxx.java

service_name=$1
java_path=$(realpath "$2")

arthas_dir=$(dirname "$java_path")/arthas-output
package_path=$(sed -n '/^package/p' "$java_path" | sed 's/package //g;s/;//g;s/\r//g').$(basename -s .java "$java_path")
rm -rf "${arthas_dir:?}" && mkdir -p "${arthas_dir:?}"
if [[ ! -f arthas-boot.jar ]]; then
  echo "----------------------------------------- downloading arthas-boot.jar -----------------------------------------"
  curl -O https://arthas.aliyun.com/arthas-boot.jar
  sleep 5s
fi
rm -f tmp_in
mknod tmp_in p
exec 8<>tmp_in
sh -c "java -jar arthas-boot.jar --select $service_name <&8 &"
echo "----------------------------------------- Arthas HotSwap The Java Class -----------------------------------------"
sleep 2s
echo "
" >>tmp_in
sleep 2s
echo "----------------------------------------- 开热加载 -----------------------------------------"
echo "sc -d $package_path | grep classLoaderHash > $arthas_dir/class_loader_hash.txt" >>tmp_in
sleep 2s
loader=$(awk '{print $2}' "$arthas_dir"/class_loader_hash.txt | head -n 1)
echo "class_loader_hash: $loader"
echo "mc -c $loader $java_path -d $arthas_dir > $arthas_dir/class_path.txt" >>tmp_in
echo "----------------------------------------- 热加载 -----------------------------------------"
while [[ ! -f $class_path ]]; do
  if [[ -f $arthas_dir/class_path.txt ]]; then
    class_path=$(sed -n '2p' "$arthas_dir"/class_path.txt)
  fi
  sleep 1s
done
echo "class_path: $class_path"
echo "retransform $class_path" >>tmp_in
sleep 2s
echo "quit" >>tmp_in
pgrep -f "java -jar arthas-boot.jar --select" | xargs -I{} kill -9 {}

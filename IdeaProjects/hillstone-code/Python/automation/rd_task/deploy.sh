#!/bin/bash
# RD 任务排期助手 - 一键部署脚本
# 用法: 将整个 rd_task 目录传到 CentOS 7 服务器，执行 bash deploy.sh

set -e

IMAGE_NAME="rd-task"
CONTAINER_NAME="rd-task"
PORT=8550

echo ">>> 构建镜像..."
docker build -t $IMAGE_NAME .

echo ">>> 停止旧容器（如有）..."
docker rm -f $CONTAINER_NAME 2>/dev/null || true

echo ">>> 启动容器..."
docker run -d \
  --name $CONTAINER_NAME \
  --restart=always \
  --add-host rd.hillstonenet.com:10.100.7.41 \
  -p $PORT:$PORT \
  $IMAGE_NAME

echo ">>> 部署完成！访问 http://$(hostname -I | awk '{print $1}'):$PORT"

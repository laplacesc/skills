#!/bin/bash

docker run -it --rm --name tencent-query \
  -v "$(pwd)":/tencent-query \
  -p 8080:8080 \
  -e LANG=en_US.UTF-8 \
  -e TZ="Asia/Shanghai" \
  docker.hillstonenet.com.cn/hillstone/almalinux-jdk8:2.0 \
  sh -c "cd /tencent-query && java -jar TencentQuery-0.0.1-SNAPSHOT.jar"

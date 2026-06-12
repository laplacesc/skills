#!/bin/bash

# DSGP 开启黑盒 UT
# sh openJacoco.sh -p [asset|config|insight|device|ops]
cd /usr/local/hillstone/dsgp-insight/bin/ && sh openJacoco.sh -p asset

# DSGP 获取扫描结果
# sh jacocoReport.sh -b [git分支名] -f [代码起始时间] -t [代码结束时间] -i [是否分析增量代码] -p [项目名]
# -b需指明代码在git服务器上对应的完整分支名，如果分支中有/，则需用=代替，如feature/123，需改为feature=123。
# -f表示代码起始时间，传时间戳，如1693968877
# -t表示代码终止时间，必须为时间戳，如1693968877123
# -f和-t结合使用可以分析两个时间段内的增量代码，非增量分析代码时，无需传-f。
# -i表示是否增量分析代码，默认为false，即全量分析。
# 数据存入 userout@10.182.142.81:/dsgp/jacoco/html

cd /usr/local/hillstone/dsgp-insight/bin/ && sh jacocoReport.sh -b feature=42210 -p asset
cd /usr/local/hillstone/dsgp-insight/bin/ && sh jacocoReport.sh -b feature=42210 -f 1718121600 -t 1718204400 -i true -p asset

# ssh root@10.10.4.66
# hillstone
# cd /home/jacoco/logs

# DSGP 关闭黑盒 UT
cd /usr/local/hillstone/dsgp-insight/bin/ && sh closeJacoco.sh -p asset

# 覆盖率结果
# 开发机访问 http://10.10.4.47:9000 DSGP/hillstone
# 只需关注覆盖率

#!/bin/bash

# author JunXian Wu
# created 2025-01-17

exception_path="/dev/null"

deployment_dir="/home/deployment_properity"
install_sql_dir="/data/install_data/sql"
flink_deployment_dir="/jar"

deploy_resource_dir="/root/deploy-resource"/$(date +%s%3N)
manifest_dir=$deploy_resource_dir"/manifest"
sqls_dir=$deploy_resource_dir"/resource/sqls"
flink_dir=$deploy_resource_dir"/resource/vvp-deployments"

tip_version="1.9.14"
cv_version="3.3.2"

printf "\ngit本地目录准备中：%s\n" $deploy_resource_dir

git clone https://jxwu:1F91huGqLWMhhQMMcuCL@gitlab.hillstonenet.com/cloud-ops/deploy-resource.git "$deploy_resource_dir" && cd "$deploy_resource_dir"
# git fetch --all && git reset --hard origin/main && git clean -f -d

printf "\n1 china\n2 eu\n3 usa\n4 yundi\n"
read -rp "请输入序号选择环境 (回车默认 1) < " pj_choice
case ${pj_choice:="1"} in
1)
  echo "选择环境: china"
  env_name="china"
  ;;
2)
  echo "选择环境: eu"
  env_name="eu"
  ;;
3)
  echo "选择环境: usa"
  env_name="usa"
  ;;
4)
  echo "选择环境: yundi"
  env_name="yundi"
  ;;
*)
  echo "异常输入"
  exit 1
  ;;
esac

printf "\n1 tip\n2 ng-cloudview\n3 cps\n4 cdcv\n5 vod\n"
read -rp "请输入序号选择项目 (回车默认 1) < " pj_choice
case ${pj_choice:="1"} in
1)
  echo "选择项目: tip"
  pj_name="tip"
  pj_deploy_dir=$manifest_dir/$env_name/"tip"
  pj_sql_dir=$install_sql_dir/"ti"
  pj_flink_dir=$flink_deployment_dir/"tip"
  final_sql_dir=$sqls_dir/"tip"
  final_flink_dir=$flink_dir/"tip"
  default_version=$tip_version
  ;;
2)
  echo "选择项目: ng-cloudview"
  pj_name="ng-cloudview"
  pj_deploy_dir=$manifest_dir/$env_name/"cv"
  pj_sql_dir=$install_sql_dir/"ng-cloudview"
  pj_flink_dir=$flink_deployment_dir/"ngcv"
  final_sql_dir=$sqls_dir/"cv"
  final_flink_dir=$flink_dir/"cv"
  default_version=$cv_version
  ;;
3)
  echo "选择项目: cps"
  pj_name="cps"
  pj_deploy_dir=$manifest_dir/$env_name/"cv"
  pj_sql_dir=$install_sql_dir/"cps"
  pj_flink_dir=$flink_deployment_dir/"cps"
  final_sql_dir=$sqls_dir/"cv"
  final_flink_dir=$flink_dir/"cv"
  default_version=$cv_version
  ;;
4)
  echo "选择项目: cdcv"
  pj_name="cd-cloudview"
  pj_deploy_dir=$manifest_dir/$env_name/"cv"
  pj_sql_dir=$install_sql_dir/"cdcv"
  pj_flink_dir=$flink_deployment_dir/"cv"
  final_sql_dir=$sqls_dir/"cv"
  final_flink_dir=$flink_dir/"cv"
  default_version=$cv_version
  ;;
5)
  echo "选择项目: vod"
  pj_name="vod"
  pj_deploy_dir=$manifest_dir/$env_name/"cv"
  pj_sql_dir=$install_sql_dir/"vod"
  pj_flink_dir=$flink_deployment_dir/"vod"
  final_sql_dir=$sqls_dir/"cv"
  final_flink_dir=$flink_dir/"cv"
  default_version=$cv_version
  ;;
*)
  echo "异常输入"
  exit 1
  ;;
esac

print_info() {
  printf "\n"
  git add . && git commit --author=jxwu -m "项目名称: $pj_name, 目标环境: $env_name, 版本号: $version, 迭代号: $specified_version" && git push --quiet -u https://jxwu:1F91huGqLWMhhQMMcuCL@gitlab.hillstonenet.com/cloud-ops/deploy-resource.git main

  printf "
上线信息
本地项目目录: %s
1. 项目名称: %s (只能填tip和cv，根据项目所在集群选择，例如sandbox->tip, vod->cv)
2. 目标环境: %s
3. 版本号: %s
4. 迭代号: %s
5. 流水线版本: %s
   详细信息: %s
6. 脚本版本: %s
   详细信息: %s
7. flink: %s
" "$specified_version_dir" $pj_name $env_name "$version" "${specified_version:-"无"}" "${pipeline_version:-"无"}" "${deployment_info:-"无"}" "${sql_version:-"无"}" "${final_sql_arr[*]:-"无"}" "${flink_yml_arr[0]:-"无"}"
}

echo
read -rp "请输入当前项目版本 (回车默认 $default_version) < " version
version=${version:=$default_version}
mkdir -p "$pj_deploy_dir"/"$version"
latest_version=$(find "$pj_deploy_dir/$version" -maxdepth 1 -mindepth 1 -type d -printf "%f\n" | sort -nr | head -n 1)
echo "该项目版本下最新上线版本是: $latest_version"

if [ -z "$latest_version" ]; then
  new_version="0"
else
  new_version=$((latest_version + 1))
fi

echo "下一个上线版本是: $new_version"
echo

read -rp "是否需要使用已存在的上线版本? 请输入需要覆盖的上线版本号 (无需覆盖请直接回车) < " specified_version

read -rp "是否需要删除旧上线版本文件夹? (y/n) (回车默认 n) < " delete_dir
delete_dir=${delete_dir:='n'}

if [[ "$delete_dir" == "y" ]]; then
  if [ -n "$specified_version" ]; then
    printf "删除旧上线版本文件夹: %s\n" "${pj_deploy_dir:=$exception_path}/$version/$specified_version"
    rm -rf "${pj_deploy_dir:=$exception_path}/$version/$specified_version"
    git rm -rf "${pj_deploy_dir:=$exception_path}/$version/$specified_version"
  fi
elif [[ "$delete_dir" == "n" ]]; then
  printf "无需删除旧上线版本文件夹\n"
else
  echo "异常输入"
  exit 1
fi

echo "指定后的当前上线版本是: ${specified_version:=$new_version}"
echo

specified_version_dir=${pj_deploy_dir:=$exception_path}/$version/$specified_version
mkdir -p "$specified_version_dir"
touch "$specified_version_dir"/".gitkeep"

read -rp "是否存在服务? (y/n) (回车默认 n) < " service_exists
service_exists=${service_exists:='n'}

if [[ "$service_exists" == "n" ]]; then
  printf "无服务需要部署\n\n"
elif [[ "$service_exists" == "y" ]]; then
  printf "\n最新的部分流水线版本如下 (按时间倒序): \n"
  readarray -t pipeline_arr < <(find "$deployment_dir" -maxdepth 1 -mindepth 1 -type f -name "$pj_name*" -printf "%T+ %f\n" | sort -r | head | nl -s' ' -n rz -w2)
  printf "%s\n" "${pipeline_arr[@]}"
  read -rp "请输入序号选择流水线版本 (回车默认 1) < " pipeline_choice
  pipeline_version=$(echo "${pipeline_arr[${pipeline_choice:=1} - 1]}" | awk '{print $3}')
  echo "选择的流水线版本是: $pipeline_version"

  printf "\n版本详情: \n"
  cat "$deployment_dir"/"$pipeline_version"
  echo

  read -rp "请核对服务并选择是否继续? (y/n) (回车默认 n) < " deploy_continue
  deploy_continue="${deploy_continue:="n"}"
  if [[ "$deploy_continue" == "n" ]]; then
    echo "执行结束"
    exit 0
  elif [[ "$deploy_continue" == "y" ]]; then
    echo "继续执行"
  else
    echo "异常输入"
    exit 1
  fi
  echo

  properity_file_dir=$specified_version_dir"/properity_file"

  echo "拷贝服务部署相关文件 ..."
  mkdir -p "$properity_file_dir"
  cp -rf $deployment_dir/"$pipeline_version" "$properity_file_dir"/
  echo
  deployment_info=$(cat "$deployment_dir"/"$pipeline_version")
else
  echo "异常输入"
  exit 1
fi

read -rp "是否存在脚本? (y/n) (回车默认 n) < " script_exists
script_exists=${script_exists:="n"}
if [[ "$script_exists" == "n" ]]; then
  printf "无脚本需要部署\n\n"
elif [[ "$script_exists" == "y" ]]; then
  printf "\n最新的部分脚本版本如下 (按时间倒序): \n"
  readarray -t sql_arr < <(find "$pj_sql_dir" -maxdepth 1 -mindepth 1 -type d -printf "%T+ %f\n" | sort -r | head | nl -s' ' -n rz -w2)
  printf "%s\n" "${sql_arr[@]}"
  read -rp "请输入序号选择脚本版本 (回车默认 1) < " sql_choice
  sql_version=$(echo "${sql_arr[${sql_choice:=1} - 1]}" | awk '{print $3}')
  echo "选择的脚本版本是: $sql_version"

  mkdir -p "$final_sql_dir"

  printf "\n最新的部分脚本如下 (按时间倒序): \n"
  readarray -t sql_dir_arr < <(find "$pj_sql_dir"/"$sql_version" -maxdepth 1 -mindepth 1 -type d ! -name "init" ! -name "tools" ! -name "clickhouse" ! -name "elasticsearch" ! -name "hbase" ! -name "hdfs" ! -name "mysql" -printf "%f\n" | sort -r | head | nl -s' ' -n rz -w2)
  printf "%s\n" "${sql_dir_arr[@]}"
  read -rp "请输入序号 (多个按空格分隔) < " sql_dir_choices
  IFS=' ' read -r -a sql_dir_choices_arr <<<"$sql_dir_choices"
  echo

  for sql_dir_choice in "${sql_dir_choices_arr[@]}"; do
    sql_dir=$(echo "${sql_dir_arr[$sql_dir_choice - 1]}" | awk '{print $2}')
    printf "拷贝脚本 %s ...\n" "$sql_dir"
    cp -rf "$pj_sql_dir"/"$sql_version"/"$sql_dir" "$final_sql_dir"/
    final_sql_arr[sql_dir_choice]=$sql_dir
  done
  echo
else
  echo "异常输入"
  exit 1
fi

read -rp "是否存在flink job? (y/n) (回车默认 n) < " flink_exists
flink_exists=${flink_exists:="n"}

if [[ "$flink_exists" == "n" ]]; then
  echo "执行结束"
  print_info
  exit 0
elif [[ "$flink_exists" == "y" ]]; then
  echo "继续执行"
else
  echo "异常输入"
  exit 1
fi
echo

printf "\n最新的部分flink目录如下 (按时间倒序): \n"
readarray -t flink_arr < <(find "$pj_flink_dir" -maxdepth 1 -mindepth 1 -type d -printf "%T+ %f\n" | sort -r | head | nl -s' ' -n rz -w2)
printf "%s\n" "${flink_arr[@]}"
read -rp "请输入序号选择脚flink (回车默认 1) < " sql_choice
flink_name=$(echo "${flink_arr[${sql_choice:=1} - 1]}" | awk '{print $3}')
echo "选择的flink是: $flink_name"

mkdir -p "$final_flink_dir"

printf "\n最新的部分flink版本如下 (按时间倒序): \n"
readarray -t flink_dir_arr < <(find "$pj_flink_dir"/"$flink_name" -maxdepth 1 -mindepth 1 -type d -printf "%T+ %f\n" | sort -r | head | nl -s' ' -n rz -w2)
printf "%s\n" "${flink_dir_arr[@]}"
read -rp "请输入序号 (回车默认 1) < " flink_dir_choice
echo
flink_version=$(echo "${flink_dir_arr[${flink_dir_choice:=1} - 1]}" | awk '{print $3}')
printf "拷贝flink配置 ...\n"
readarray -t flink_yml_arr < <(find "$pj_flink_dir"/"$flink_name"/"$flink_version" -maxdepth 1 -mindepth 1 -type f -name "*.yml")
cp -rf "${flink_yml_arr[0]}" "$final_flink_dir"/
printf "\n"

print_info

exit 0

#!/bin/bash

# author JunXian Wu
# created 2024-12-04

exception_path="/dev/null"
deployment_dir="/home/deployment_properity"
demo_app_dir="/root/cloud_deploy/demo/app"
demo_database_dir="/root/cloud_deploy/demo/database"

cloud_deploy_dir="/root/cloud_deploy"
install_sql_dir="/data/install_data/sql"

tip_version="tip-1.9.12"
cv_version="cloudview-3.3.0"
cps_version="cps-1.2.1"

printf "\n1 tip\n2 ng-cloudview\n3 cps\n"
read -rp "请输入序号选择项目 (回车默认 1) < " pj_choice
case ${pj_choice:="1"} in
1)
  echo "选择项目: tip"
  pj_name="tip"
  pj_deploy_dir=$cloud_deploy_dir"/tip"
  pj_sql_dir=$install_sql_dir"/ti"
  default_version=$tip_version
  ;;
2)
  echo "选择项目: ng-cloudview"
  pj_name="ng-cloudview"
  pj_deploy_dir=$cloud_deploy_dir"/cv"
  pj_sql_dir=$install_sql_dir"/ng-cloudview"
  default_version=$cv_version
  ;;
3)
  echo "选择项目: cps"
  pj_name="cps"
  pj_deploy_dir=$cloud_deploy_dir"/cps"
  pj_sql_dir=$install_sql_dir"/cps"
  default_version=$cps_version
  ;;
*)
  echo "异常输入"
  exit 1
  ;;
esac

print_info() {
  printf "\n---------------------------------------------------------------------------------------------------------\n"
  printf "\n!!! 检查服务部署配置中的hosts, 配置路径为: %s\n" "${app_deploy_yaml:-"无"}"
  yq "${app_deploy_yaml:-$exception_path}"
  printf "\n!!! 检查脚本部署配置中的hosts, 配置路径为: %s\n" "${database_deploy_yaml:-"无"}"
  yq "${database_deploy_yaml:-$exception_path}"

  printf "
上线信息确认
1. 流水线版本: %s
版本信息: \n%s
2. 上线版本: %s
3. 脚本版本: %s
\n" "$pipeline_version" "$(cat "$deployment_dir"/"$pipeline_version")" "${specified_version:-"无"}" "${sql_version:-"无"}"
  printf "上线服务命令: %s\n" "ansible-playbook -i /root/cloud_deploy/inventory ${app_deploy_yaml:-"无"} -vv"
  printf "上线脚本命令: %s\n" "ansible-playbook -i /root/cloud_deploy/inventory ${database_deploy_yaml:-"无"} -vv"
}

check_input_ny() {
  if [[ "$1" == "n" ]]; then
    echo "执行结束"
    print_info
    exit 0
  elif [[ "$1" == "y" ]]; then
    echo "继续执行"
  else
    echo "异常输入"
    exit 1
  fi
  echo
}

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
check_input_ny "${deploy_continue:="n"}"

read -rp "请输入当前项目版本 (回车默认 $default_version) < " version
version=${version:=$default_version}
latest_version=$(find "$pj_deploy_dir" -maxdepth 1 -mindepth 1 -type d -name "$version*" -printf "%f\n" | sort -t'.' -k 4 -nr | head -n 1)
echo "该项目版本下最新上线版本是: $latest_version"

if [ -z "$latest_version" ]; then
  new_version=$version".0"
else
  new_version=$(awk -F. '{print $1"."$2"."$3"."$4+1}' <<<"$latest_version")
fi

echo "当前上线版本是: $new_version"
echo

read -rp "是否需要使用已存在的上线版本? 请输入需要覆盖的上线版本号 (无需覆盖请直接回车) < " specified_version

if [ -n "$specified_version" ]; then
  echo "删除旧上线版本文件夹: ""${pj_deploy_dir:=$exception_path}"/"$specified_version"
  rm -rf "${pj_deploy_dir:=$exception_path}"/"$specified_version"
fi

echo "指定后的当前上线版本是: ${specified_version:=$new_version}"
echo

app_deploy_yaml=$pj_deploy_dir/"$specified_version""/app/deploy.yaml"
app_dir=$pj_deploy_dir/"$specified_version""/app/roles/deploy_app/files/properity_path"

echo "拷贝服务部署相关文件 ..."
mkdir -p "$pj_deploy_dir"/"$specified_version"
cp -rf $demo_app_dir "$pj_deploy_dir"/"$specified_version"/
cp -rf $deployment_dir/"$pipeline_version" "$app_dir"/
echo

#while :; do
#  printf "服务当前hosts配置: \n"
#  yq '[.[] | {"name": .name, "hosts": .hosts}]' "$app_deploy_yaml"
#  echo
#  read -rp "是否需要修改服务部署配置中的hosts? (y/n) (回车默认 n) < " need_to_be_modified
#  if [[ "${need_to_be_modified:="n"}" == "y" && -n "$app_deploy_yaml" ]]; then
#    read -rp "请输入需要修改的name < " app_deploy_name
#    read -rp "请输入替换的hosts < " app_deploy_hosts
#    yq -i ".[] | select(.name==\"$app_deploy_name\") | .hosts=\"$app_deploy_hosts\" | parent" "$app_deploy_yaml"
#  else
#    break
#  fi
#done
#echo

read -rp "是否存在脚本? (y/n) (回车默认 n) < " script_exists
check_input_ny "${script_exists:="n"}"

echo "拷贝数据库相关模板 ..."
cp -rf $demo_database_dir "$pj_deploy_dir"/"$specified_version"/

printf "\n最新的部分脚本版本如下 (按时间倒序): \n"
readarray -t sql_arr < <(find "$pj_sql_dir" -maxdepth 1 -mindepth 1 -type d -printf "%T+ %f\n" | sort -r | head | nl -s' ' -n rz -w2)
printf "%s\n" "${sql_arr[@]}"
read -rp "请输入序号选择脚本版本 (回车默认 1) < " sql_choice
sql_version=$(echo "${sql_arr[${sql_choice:=1} - 1]}" | awk '{print $3}')
echo "选择的脚本版本是: $sql_version"

database_deploy_yaml=$pj_deploy_dir/"$specified_version""/database/deploy.yaml"
database_sql_dir=$pj_deploy_dir/"$specified_version""/database/roles/prepare/files/sql"
database_roles_dir=$pj_deploy_dir/"$specified_version""/database/roles"
# 全部脚本类型 hdfs 不符合其他脚本规则 且不让直接执行脚本 写在这儿是为了后面统一删除未使用脚本目录
sql_roles=(clickhouse elasticsearch hbase hdfs mysql)
yaml_path="/tasks/main.yml"

printf "\n最新的部分脚本如下 (按时间倒序): \n"
readarray -t sql_dir_arr < <(find "$pj_sql_dir"/"$sql_version" -maxdepth 1 -mindepth 1 -type d ! -name "init" ! -name "tools" ! -name "clickhouse" ! -name "elasticsearch" ! -name "hbase" ! -name "hdfs" ! -name "mysql" -printf "%f\n" | sort -r | head | nl -s' ' -n rz -w2)
printf "%s\n" "${sql_dir_arr[@]}"
read -rp "请按需要执行的顺序输入序号 (多个按空格分隔) < " sql_dir_choices
IFS=' ' read -r -a sql_dir_choices_arr <<<"$sql_dir_choices"
echo

declare -A flag
for role in "${sql_roles[@]}"; do
  flag["$role"]=0
done

for sql_dir_choice in "${sql_dir_choices_arr[@]}"; do
  sql_dir=$(echo "${sql_dir_arr[$sql_dir_choice - 1]}" | awk '{print $2}')
  printf "拷贝脚本 %s ...\n" "$sql_dir"
  cp -rf "$pj_sql_dir"/"$sql_version"/"$sql_dir" "$database_sql_dir"/

  for role in "${sql_roles[@]}"; do
    if [ -d "$database_sql_dir/$sql_dir/$role" ]; then
      printf "\t存在 %s 脚本 进行配置, flag: %s\n" "$role" "${flag["$role"]}"
      yq -i ".[] | select(.name == \"exec $role script\") | .loop[$((flag["$role"]++))] = \"$sql_dir\" | parent" "$database_roles_dir"/"$role"$yaml_path
    fi
    if [ -d "$database_sql_dir/$sql_dir/aliyun/$role" ]; then
      printf "\taliyun 存在 %s 脚本 进行配置, flag: %s\n" "$role" "${flag["$role"]}"
      yq -i ".[] | select(.name == \"exec $role script\") | .loop[$((flag["$role"]++))] = \"$sql_dir\" | parent" "$database_roles_dir"/"$role"$yaml_path
    fi
    if [ -d "$database_sql_dir/$sql_dir/yundi/$role" ]; then
      printf "\tyundi 存在 %s 脚本 进行配置, flag: %s\n" "$role" "${flag["$role"]}"
      yq -i ".[] | select(.name == \"exec $role script\") | .loop[$((flag["$role"]++))] = \"$sql_dir\" | parent" "$database_roles_dir"/"$role"$yaml_path
    fi
  done
done

declare -A name
name=(
  ["mysql"]="mysql"
  ["clickhouse"]="ck"
  ["hbase"]="hbase"
  ["elasticsearch"]="elasticsearch"
)

printf "\n配置完成 删除未使用脚本目录和脚本配置并打印相关脚本数量\n"
for role in "${sql_roles[@]}"; do
  if [[ "${flag["$role"]}" -eq 0 ]]; then
    printf "role: %s, no script\n" "$role"
    rm -rf "${database_roles_dir:-$exception_path}"/"$role"
    if [[ -n ${name[$role]} ]]; then
      printf "\tdelete database deploy: %s\n" "${name["$role"]}"
      yq -i ".[] | select(.name == \"install ${name["$role"]} source\") | del . | parent" "$database_deploy_yaml"
    fi
  else
    printf "role: %s, scripts number: %s\n" "$role" "${flag["$role"]}"
  fi
done
echo

#while :; do
#  printf "脚本当前hosts配置: \n"
#  yq '[.[] | {"name": .name, "hosts": .hosts}]' "$database_deploy_yaml"
#  echo
#  read -rp "是否需要修改脚本部署配置中的hosts? (y/n) (回车默认 n) < " need_to_be_modified
#  if [[ "${need_to_be_modified:="n"}" == "y" && -n "$database_deploy_yaml" ]]; then
#    read -rp "请输入需要修改的name < " database_deploy_name
#    read -rp "请输入替换的hosts < " database_deploy_hosts
#    yq -i ".[] | select(.name==\"$database_deploy_name\") | .hosts[0]=\"$database_deploy_hosts\" | parent" "$database_deploy_yaml"
#  else
#    break
#  fi
#done
#echo

print_info

exit 0

#!/bin/bash

function update_feature_jq() {
  local new_patch=$1
  local old_full=$2
  local new_full=$3

  local del_file=$new_patch".del"

  jq -c 'select(.patch_mark == "-") | .ioc' "$new_patch" >"$del_file"

  {
    jq -cr 'select(.patch_mark == "+") | del(.patch_mark) | "\(.ioc):\(.subtype|tostring)\t\(.)"' "$new_patch"
    jq -crn --slurpfile del_file "$del_file" '
      ($del_file | INDEX(.)) as $del
      | inputs
      | select(has("ioc") and ($del[.ioc] | not))
      | "\(.ioc):\(.subtype|tostring)\t\(.)"
    ' "$old_full"
  } | sort -t $'\t' -k1,1 -u | cut -d $'\t' -f2- >"$new_full"

  rm -f "$del_file"
}

delete_patch_ioc() {
  awk 'ARGIND==1 {del[$0]++;next}
    {
      match($0, /"ioc":"([^"]+)"/, arr);
      if (arr[1] && !del[arr[1]]) print $0
    }' "$1" "$2" >"$3"
}

delete_patch_ioc "$@"

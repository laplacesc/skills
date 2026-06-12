#!/bin/sh

# author: JunXian Wu

dir_path=$(realpath "$1")

test_db="ldbc"

arangosh --server.authentication false --server.database _system --javascript.execute-string "db._createDatabase('$test_db', {replicationFactor: 3})"

find "$dir_path" -type f -name "*.csv" | while read -r file; do
  prefix=$(basename -s _0_0.csv "$file")
  parts_count=$(echo "$prefix" | awk -F'_' '{print NF}')
  if [ "$parts_count" -eq 1 ]; then
    arangosh --server.authentication false --server.database $test_db --javascript.execute-string "db._createDocumentCollection('$prefix', {numberOfShards: 24, replicationFactor: 3,  shardKeys: ['_key']})"
    part_1=$(echo "$prefix" | awk -F'_' 'NF==1 {print $1} NF==3 {print $1}')
    printf "\nStart importing document data: %s\n" "$part_1"
    arangoimport --server.authentication false --auto-rate-limit true --on-duplicate update --threads 8 --server.database $test_db --type csv --separator "|" --create-collection-type "document" --collection "$prefix" --file "$file" --translate "id=_key"
    echo
  fi
done

find "$dir_path" -type f -name "*.csv" | while read -r file; do
  prefix=$(basename -s _0_0.csv "$file")
  parts_count=$(echo "$prefix" | awk -F'_' '{print NF}')
  if [ "$parts_count" -eq 3 ]; then
    arangosh --server.authentication false --server.database $test_db --javascript.execute-string "db._createEdgeCollection('$prefix', {numberOfShards: 24, replicationFactor: 3,  shardKeys: ['_from', '_to']})"
    part_1=$(echo "$prefix" | awk -F'_' 'NF==1 {print $1} NF==3 {print $1}')
    part_2=$(echo "$prefix" | awk -F'_' 'NF==1 {print $1} NF==3 {print $2}')
    part_3=$(echo "$prefix" | awk -F'_' 'NF==1 {print $1} NF==3 {print $3}')
    printf "\nStart importing edge data: %s %s %s\n" "$part_1" "$part_2" "$part_3"
    head_1=$(head -n 1 "$file" | awk -F'|' '{print $1}')
    head_2=$(head -n 1 "$file" | awk -F'|' '{print $2}')
    if [ "$head_1" = "$head_2" ]; then
      head_1=$head_1"1"
      head_2=$head_2"2"
      sed -i "1s/^[^|]*|[^|]*/$head_1|$head_2/" "$file"
    fi
    echo "head_1: $head_1, head_2: $head_2"
    arangoimport --server.authentication false --auto-rate-limit true --on-duplicate update --threads 8 --server.database $test_db --type csv --separator "|" --create-collection-type "edge" --collection "$prefix" --file "$file" --merge-attributes "_from=$part_1/[$head_1]" --merge-attributes "_to=$part_3/[$head_2]"
    echo
  fi
done

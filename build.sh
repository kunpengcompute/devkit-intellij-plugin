#!/bin/bash
# Compile source code.
# Copyright © Huawei Technologies Co., Ltd. 2010-2020. All rights reserved.
set -e

# 模块名称
module_name=$1

cd ${WORKSPACE}/KunpengIntellIJPlugin/ && gradle clean build -s -Dfile.encoding=utf-8 \
&& cd $module_name && gradle dependencies --write-locks && gradle build -s -Dfile.encoding=utf-8 \

if [ ! -d "../output/distributions" ]; then
  mkdir -p ../output/distributions
fi

cp -rf build/distributions/* ../output/distributions

result=$?
exit ${result}

function clean() {
  echo $1
}

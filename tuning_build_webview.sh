#!/bin/bash
# Copyright Huawei Technologies Co., Ltd. 2010-2018. All rights reserved.
# 编译webview脚本
set -e
echo "**************start build vscode-webview to intellIJ-webview!*********"

export WORKSPACE=D:/huawei-devkit-plugins
#vscode_path=${WORKSPACE}/Kunpeng_DevKit_Frontend
#intellij_path=${WORKSPACE}/KunpengIntellIJPlugin

vscode_path=${WORKSPACE}/devkit-vscode-plugin
intellij_path=${WORKSPACE}/devkit-intellij-plugin

echo $vscode_path
echo $intellij_paths

vscode_npm_path=$vscode_path/workspace
vscode_webview_path=$vscode_path/extension/tuning-intellIJ/out
intellij_webview_path=$intellij_path/hypertuner/src/main/resources/webview
echo $vscode_webview_path
echo $intellij_webview_path

echo "**************start npm install !*********"
cd $vscode_npm_path
npm install --unsafe-perm --legacy-peer-deps
echo "**************end npm install !*********"

echo "**************start build webview!*********"
cd $vscode_path
npm run package:intellij:tuning
cd $vscode_webview_path

echo "**************Start Copy zip tuning files******"
cp tuning.zip $intellij_webview_path
echo "**************Finish build vscode-webview to Intellj*********"


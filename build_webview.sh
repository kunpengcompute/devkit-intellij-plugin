#!/bin/bash
# Copyright Huawei Technologies Co., Ltd. 2010-2018. All rights reserved.
# 编译webview脚本
set -e
echo "**************start build vscode-webview to intellIJ-webview!*********"
vscode_path=${WORKSPACE}/Kunpeng_DevKit_Frontend
intellij_path=${WORKSPACE}/KunpengIntellIJPlugin

vscode_npm_path=$vscode_path/workspace
vscode_webview_path=$vscode_path/extension/porting-intellIJ/out
intellij_webview_path=$intellij_path/portingadvisor/src/main/resources/webview
echo $vscode_webview_path
echo $intellij_webview_path

echo "**************start npm install !*********"
cd $vscode_npm_path
npm install --unsafe-perm
echo "**************end npm install !*********"

echo "**************start build webview!*********"
cd $vscode_path
npm run package:intellij:porting
cd $vscode_webview_path

echo "**************Start Copy zip porting files******"
cp porting.zip $intellij_webview_path
echo "**************Finish build vscode-webview to Intellj*********"


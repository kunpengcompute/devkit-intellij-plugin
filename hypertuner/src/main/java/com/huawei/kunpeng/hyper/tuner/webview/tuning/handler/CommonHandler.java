/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.kunpeng.hyper.tuner.webview.tuning.handler;

import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.FileManageConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.*;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 公共的function处理器
 *
 * @since 2020-11-18
 */
public class CommonHandler extends FunctionHandler {
    /**
     * webview通用请求函数
     *
     * @param message 数据
     * @param module  模块
     */
    public void getData(MessageBean message, String module) {
        Logger.info("CommonHandler function getData() is invoked! message=", message);
    }

    /**
     * jsToJava函数
     * 在线分析导出报告/下载证书
     *
     * @param message js 传来的参数
     * @param module  js 传来的参数
     */
    public void downloadCertificate(MessageBean message, String module) {
        Logger.info("CommonHandler function downloadFile() is invoked! message=" + message);

        // 获取message传来的参数，进行校验
        Map<String, Map<String, String>> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Map<String, String> data = messageData.get("data");

        String fileName = data.get("fileName");
        // 判读是否为下载证书
        boolean isCertFileDownload = ("ca.crt").equals(fileName);
        String fileContent = data.get("fileContent");
        // 下载内容
        if (fileContent == null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_FAIL, FileManageConstant.DOWNLOAD_EMPTY, NotificationType.ERROR));
            return;
        }
        // 弹出选择存储路径弹窗
        String title = FileManageConstant.DOWNLOAD_TITLE;
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(title);
        Project project = CommonUtil.getDefaultProject();
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        try {
            FileUtil.writeFile(fileContent, path + fileName);
        } catch (IOException exception) {
            Logger.error("IOException when writeFile error!!");
            downloadNotify(false, null);
        }
        downloadNotify(true, path);
        if (isCertFileDownload) {
            certFileInstallHandle(fileName, path);
        }
    }

    /**
     * 执行证书安装
     *
     * @param fileName 证书文件名
     * @param path     证书存储路径
     */
    private void certFileInstallHandle(String fileName, String path) {
        IDENotificationUtil.notificationCommon(new NotificationBean(
                InstallManageConstant.IMPORT_CA_TITLE,
                InstallManageConstant.IMPORT_CA_SUCCESS,
                NotificationType.INFORMATION));
        Object osType = IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue());
        if (osType == SystemOS.WINDOWS) {
            ShellTerminalUtil.openInstallCaTerminal(path + File.separator + fileName);
        }
    }

    /**
     * 下载结果右下角提示
     *
     * @param saveFlag 保存结果
     * @param path     保存路径
     */
    private void downloadNotify(boolean saveFlag, String path) {
        if (saveFlag) {
            NotificationBean notificationBean = new NotificationBean(
                    FileManageConstant.DOWNLOAD_SUCCESS,
                    FileManageConstant.DOWNLOAD_FAIL_TIP + "<html> <a href=\"#\">" + path + "</a></html>",
                    NotificationType.INFORMATION);
            IDENotificationUtil.notificationForHyperlink(
                    notificationBean,
                    obj -> CommonUtil.showFileDirOnDesktop(path)
            );
        } else {
            IDENotificationUtil.notificationCommon(new NotificationBean(
                    FileManageConstant.DOWNLOAD_SUCCESS,
                    FileManageConstant.DOWNLOAD_SUCCESS_TIP,
                    NotificationType.ERROR
            ));
        }
    }
}


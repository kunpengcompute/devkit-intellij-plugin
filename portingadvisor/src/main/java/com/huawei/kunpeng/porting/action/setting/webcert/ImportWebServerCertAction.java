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

package com.huawei.kunpeng.porting.action.setting.webcert;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.DiskUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.panel.settings.webservercert.PortingImportWebServerCertPanel;

import com.intellij.notification.NotificationType;

import java.io.File;

/**
 * web服务证书事件处理器
 *
 * @since 2020-10-13
 */
public class ImportWebServerCertAction extends IDEPanelBaseAction {
    private static final String INSUFFICIENT_SPACE = RespondStatus.DISK_NOT_ENOUGH.value();

    /**
     * 上传按钮点击事件对应处理方法
     *
     * @param panel    面板
     * @param file     待上传的文件对象
     * @param fileSize 文件大小
     */
    public void uploadButtonClicked(PortingImportWebServerCertPanel panel, File file, long fileSize) {
        uploadWebServerCertFile(panel, file);
    }

    /**
     * 上传web服务证书文件
     *
     * @param panel 面板
     * @param file  文件对象
     */
    public void uploadWebServerCertFile(PortingImportWebServerCertPanel panel, File file) {
        String title = I18NServer.toLocale("plugins_porting_certificate_import_file");
        if (StringUtil.verifyFileSuffix(file.getName(), new String[] {"crt", "cert", "perm"})) {
            // 弹出上传文件进度条
            RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/cert/cert/", HttpMethod.POST.vaLue(), "");
            message.setFile(new File[] {file});
            message.setNeedUploadFile(true);
            message.setNeedProcess(true);
            ResponseBean responseData = PortingHttpsServer.INSTANCE.requestData(message);
            if (responseData == null) {
                return;
            }
            if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseData.getStatus())) {
                // 上传文件成功
                IDENotificationUtil.notificationCommon(new NotificationBean(title,
                    CommonUtil.getRspTipInfo(responseData), NotificationType.INFORMATION));
            } else if (INSUFFICIENT_SPACE.equals(responseData.getStatus())) {
                DiskUtil.sendDiskAlertMessage();
            } else {
                IDENotificationUtil.notificationCommon(
                    new NotificationBean(title, CommonUtil.getRspTipInfo(responseData), NotificationType.ERROR));
            }
        } else {
            IDENotificationUtil.notificationCommon(new NotificationBean(title,
                I18NServer.toLocale("plugins_porting_certificate_import_placeholder"), NotificationType.ERROR));
        }
    }
}
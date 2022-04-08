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

package com.huawei.kunpeng.hyper.tuner.action.panel.webservercert;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert.TuningImportWebServerCertPanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.intellij.notification.NotificationType;

import java.io.File;
import java.util.HashMap;

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
    public void uploadButtonClicked(TuningImportWebServerCertPanel panel, File file, long fileSize) {
        uploadWebServerCertFile(panel, file);
    }

    /**
     * 上传web服务证书文件
     *
     * @param panel 面板
     * @param file  文件对象
     */
    public void uploadWebServerCertFile(TuningImportWebServerCertPanel panel, File file) {
        String title = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file");
        if (StringUtil.verifyFileSuffix(file.getName(), new String[]{"crt", "cer", "cert", "perm", "pem"})) {
            // 弹出上传文件进度条
            RequestDataBean message =
                    new RequestDataBean(
                            TuningIDEConstant.TOOL_NAME_TUNING,
                            "user-management/api/v2.2/certificates/",
                            HttpMethod.PUT.vaLue(),
                            "");
            message.setFile(new File[]{file});
            message.setNeedUploadFile(true);
            message.setNeedProcess(true);
            ResponseBean responseData = TuningHttpsServer.INSTANCE.requestData(message);
            if (responseData == null) {
                return;
            }
            Logger.info("responseData", responseData);
            uploadWebServerCertFileJudgment(title, responseData);
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            title, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_placeholder"),
                            NotificationType.ERROR));
        }
    }

    /**
     * 上传web服务证书文件结果通知
     *
     * @param title        主题
     * @param responseData 响应报文
     */
    public void uploadWebServerCertFileJudgment(String title, ResponseBean responseData) {
        HashMap<String, String> coeTipsMap = getCodeTipMap();
        String code = responseData.getCode();
        String tips;
        NotificationType type;
        if (!(coeTipsMap.get(code) == null)) {
            tips = coeTipsMap.get(code);
        } else {
            tips = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_installFailedError");
        }
        if ("UserManage.Success".equals(code)) {
            type = NotificationType.INFORMATION;
        } else {
            type = NotificationType.ERROR;
        }
        IDENotificationUtil.notificationCommon(new NotificationBean(title, tips, type));
    }

    private HashMap<String, String> getCodeTipMap() {
        HashMap<String, String> codeTipMap = new HashMap<String, String>();
        codeTipMap.put("UserManage.Success",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_success"));
        codeTipMap.put("UserManage.Certificates.Put.FileTypeError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_fileTypeError"));
        codeTipMap.put("UserManage.Certificates.Put.PrivateKeyNotExistError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_privateKeyNotExistErrorr"));
        codeTipMap.put("UserManage.Certificates.Put.PrivateKeyMatchError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_privateKeyMatchError"));
        codeTipMap.put("UserManage.Certificates.Put.CertExpiredError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_certExpiredError"));
        codeTipMap.put("UserManage.Certificates.Put.CertStandardError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_certStandardError"));
        codeTipMap.put("UserManage.Certificates.Put.FileSizeError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_fileSizeError"));
        codeTipMap.put("UserManage.Certificates.Put.FileNullError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_fileNullError"));
        codeTipMap.put("UserManage.Certificates.Put.KeyLenError",
                TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file_keyLenError"));
        return codeTipMap;
    }
}

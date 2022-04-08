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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.webservercert;

import com.huawei.kunpeng.hyper.tuner.action.panel.webservercert.ImportWebServerCertAction;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert.TuningImportWebServerCertPanel;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.ImportWebServerCertDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.awt.Rectangle;
import java.io.File;

/**
 * 导入服务证书弹框
 *
 * @since 2012-10-12
 */
public class TuningImportWebServerCertDialog extends ImportWebServerCertDialog {
    /**
     * 帮助文档
     */
    private static final String HELP_URL = TuningI18NServer.toLocale("plugins_hyper_tuner_import_csr_help_url");

    private TuningImportWebServerCertPanel webServerCertPanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public TuningImportWebServerCertDialog(String title, String dialogName, IDEBasePanel panel, Rectangle rectangle) {
        this.title =
                ValidateUtils.isEmptyString(title)
                        ? TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_import_file")
                        : title;
        this.dialogName =
                ValidateUtils.isEmptyString(dialogName)
                        ? Dialogs.IMPORT_WEB_SERVER_CERTIFICATE.dialogName()
                        : dialogName;
        this.mainPanel = panel;
        if (mainPanel instanceof TuningImportWebServerCertPanel) {
            webServerCertPanel = (TuningImportWebServerCertPanel) mainPanel;
            mainPanel.setParentComponent(this);
        }

        // 无位置信息时居中显示
        this.rectangle = rectangle;

        setOKAndCancelName(
                TuningI18NServer.toLocale("plugins_porting_common_button_upload"),
                TuningI18NServer.toLocale("plugins_common_button_cancel"));
        // 设置帮助
        setHelp(TuningI18NServer.toLocale("plugins_hyper_tuner_generate_csr_file_help"), HELP_URL);

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public TuningImportWebServerCertDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, null);
    }

    /**
     * 确认新增弱口令
     */
    @Override
    protected void onOKAction() {
        // 生成CSR文件
        if (mainPanel.getAction() instanceof ImportWebServerCertAction
                && mainPanel instanceof TuningImportWebServerCertPanel) {
            TuningImportWebServerCertPanel tuningImportWebServerCertPanel = (TuningImportWebServerCertPanel) mainPanel;
            String localFilePath = tuningImportWebServerCertPanel.getPackUploadField().getText(); // 获取选中的本地文件路径
            if ("".equals(localFilePath)) {
                Logger.info("Please upload web server cert file firstly."); // 弹窗提示等待统一方式，暂未提供
            } else {
                uploadFile(tuningImportWebServerCertPanel, localFilePath);
            }
        }
    }

    /**
     * 文件上传前置判断处理
     *
     * @param tuningImportWebServerCertPanel 导入panel
     * @param filePath                       本地文件路径
     */
    public void uploadFile(TuningImportWebServerCertPanel tuningImportWebServerCertPanel, String filePath) {
        if (!FileUtil.isValidateFile(filePath, 500)) {
            return;
        }
        File file = new File(filePath);
        ImportWebServerCertAction action = (ImportWebServerCertAction) mainPanel.getAction();
        action.uploadButtonClicked(tuningImportWebServerCertPanel, file, file.length() / MB_TO_KB / MB_TO_KB);
    }

    /**
     * 判断当前面板是否可以进行下一步
     *
     * @return ValidationInfo
     */
    @Override
    public ValidationInfo doValidate() {
        return webServerCertPanel.doValidate();
    }

    /**
     * 刷新弹框
     */
    public void updateDialog() {
        // 检查是否已成功上传文件
        if (ValidateUtils.isEmptyString(webServerCertPanel.getPackUploadField().getText())) {
            getOKAction().setEnabled(false);
        } else {
            getOKAction().setEnabled(true);
        }
    }
}

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

package com.huawei.kunpeng.porting.ui.dialog.crl;

import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.porting.ui.panel.CertRovListPanel;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.CertificationRevocationListAction;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.ImportCRLPanel;

import com.intellij.notification.NotificationType;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;


/**
 * The class: ImportCRLDialog
 *
 * @since 2021-8-12
 */
public class ImportCRLDialog extends IdeaDialog {
    private CertificationRevocationListAction action;
    private CertRovListPanel srcPanel;

    /**
     * Constructor
     *
     * @param panel panel
     */
    public ImportCRLDialog(ImportCRLPanel panel, CertRovListPanel srcPanel) {
        this.srcPanel = srcPanel;
        this.title = I18NServer.toLocale("plugins_porting_setting_crl_title_import_crl");
        this.dialogName = I18NServer.toLocale("plugins_porting_setting_crl_title_import_crl");
        this.mainPanel = panel;
        this.action = CertificationRevocationListAction.getInstance();
        setOKAndCancelName(I18NServer.toLocale("plugins_porting_common_button_upload"),
            I18NServer.toLocale("plugins_common_button_cancel"));
        // 初始化弹框内容
        initDialog();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        if (mainPanel instanceof ImportCRLPanel) {
            return ((ImportCRLPanel) mainPanel).getPanel();
        }
        return mainPanel;
    }

    /**
     * 执行上传CRL文件动作
     */
    @Override
    protected void onOKAction() {
        if (mainPanel instanceof ImportCRLPanel) {
            String path = ((ImportCRLPanel) mainPanel).getCrlUploadField().getText();
            if (!FileUtil.validateFilePath(path)) {
                return;
            }
            File uploadFile = new File(path);

            ResponseBean responseBean = action.perCheckForImportCRL(uploadFile, "normal");
            if (responseBean == null) {
                Logger.error("Import CRL file preCheck error.");
                return;
            }
            // 根据后端状态来显示通知
            if ("0".equals(responseBean.getStatus())) {
                handleFirstImportCRL(uploadFile);
                return;
            }
            if ("0x010115".equals(responseBean.getStatus())) {
                handleDuplicateCRLFile(uploadFile);
                return;
            }
            if ("0x060714".equals(responseBean.getStatus())) {
                handleMaxNumCRLFile();
                return;
            }
            IDENotificationUtil.notifyInfo("", CommonUtil.getRspTipInfo(responseBean), NotificationType.ERROR);
        }
    }

    @Override
    protected void onCancelAction() {
    }

    /**
     * 处理之前没有上传过CRL文件的情况
     *
     * @param file file
     */
    public void handleFirstImportCRL(File file) {
        handleImportCRLFile(this.action, file, this.srcPanel);
    }

    /**
     * 处理文件重复的情况
     *
     * @param file file
     */
    private void handleDuplicateCRLFile(File file) {
        DuplicateCRLDialog dialog = new DuplicateCRLDialog(srcPanel, file);
        dialog.displayPanel();
    }

    /**
     * 抽取出一个上传CRL的方法
     *
     * @param action   action
     * @param file     file
     * @param srcPanel srcPanel
     */
    public static void handleImportCRLFile(CertificationRevocationListAction action, File file,
        CertRovListPanel srcPanel) {
        ResponseBean importResponseBean = action.importCertRevList(file);
        NotificationType type = NotificationType.ERROR;
        if ("0x010101".equals(importResponseBean.getStatus())) {
            type = NotificationType.INFORMATION;
            // 更新 CRL列表
            srcPanel.updateTable();
        }
        IDENotificationUtil.notifyInfo("", CommonUtil.getRspTipInfo(importResponseBean), type);
    }

    /**
     * 证书数量达到3份时的处理逻辑
     */
    private void handleMaxNumCRLFile() {
        ReachMaxNumCRLDialog dialog = new ReachMaxNumCRLDialog();
        dialog.displayPanel();
    }
}

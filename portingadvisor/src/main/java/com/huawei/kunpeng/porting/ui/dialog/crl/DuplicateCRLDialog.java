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
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.porting.ui.panel.CertRovListPanel;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.CertificationRevocationListAction;

import com.intellij.notification.NotificationType;

import java.awt.Dimension;
import java.io.File;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * The class: DuplicateCRLDialog
 *
 * @since 2021-08-12
 */
public class DuplicateCRLDialog extends IdeaDialog {
    private static final String DUPLICATE_CRL_FILE = "DUPLICATE_CRL_FILE";
    private CertRovListPanel srcPanel;
    private String certName;
    private CertificationRevocationListAction action;
    private File file;

    /**
     * Constructor
     *
     * @param srcPanel srcPanel
     * @param file     file
     */
    public DuplicateCRLDialog(CertRovListPanel srcPanel, File file) {
        this.file = file;
        this.certName = file.getName();
        this.srcPanel = srcPanel;
        this.dialogName = DUPLICATE_CRL_FILE;
        this.action = CertificationRevocationListAction.getInstance();
        setOKAndCancelName(I18NServer.toLocale("plugins_common_replace"),
            I18NServer.toLocale("plugins_common_button_cancel"));
        initDialog();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JLabel label = new JLabel();
        label.setText(I18NServer.toLocale("plugins_porting_duplicate_crl", certName));
        label.setIcon(BaseIntellijIcons.load(IDEConstant.WARN_INFO));
        label.setPreferredSize(new Dimension(509, 34));
        return label;
    }

    @Override
    protected void onOKAction() {
        ResponseBean responseBean = action.perCheckForImportCRL(file, "override");
        if ("0".equals(responseBean.getStatus())) {
            // 执行上传
            ImportCRLDialog.handleImportCRLFile(action, file, srcPanel);
            return;
        }
        IDENotificationUtil.notifyInfo("", CommonUtil.getRspTipInfo(responseBean), NotificationType.ERROR);
    }

    @Override
    protected void onCancelAction() {
    }
}

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
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.porting.ui.panel.CertRovListPanel;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.CertificationRevocationListAction;

import com.intellij.notification.NotificationType;

import java.awt.Dimension;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * The class: DeleteCRLDialog
 *
 * @since 2021-8-12
 */
public class DeleteCRLDialog extends IdeaDialog {
    private static final String DELETE_CRL_FILE = "DELETE_CRL_FILE";
    private CertificationRevocationListAction action;
    private String certName;
    private CertRovListPanel srcPanel;

    public DeleteCRLDialog(String certName, CertRovListPanel srcPanel) {
        this.certName = certName;
        this.srcPanel = srcPanel;
        this.dialogName = DELETE_CRL_FILE;
        this.title = I18NServer.toLocale("plugins_porting_setting_crl_title_delete_crl");
        this.action = CertificationRevocationListAction.getInstance();
        initDialog();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JLabel label = new JLabel();
        label.setText(I18NServer.toLocale("plugins_porting_setting_delete_crl_content"));
        label.setIcon(BaseIntellijIcons.load(IDEConstant.WARN_INFO));
        label.setPreferredSize(new Dimension(509, 17));
        return label;
    }

    @Override
    protected void onOKAction() {
        ResponseBean responseBean = action.deleteCertRevList(certName);
        if (responseBean == null) {
            Logger.error("Delete CRL error");
            return;
        }
        // 根据后端状态来显示通知
        NotificationType type = NotificationType.ERROR;
        if ("0".equals(responseBean.getStatus())) {
            type = NotificationType.INFORMATION;
            // 更新 CRL列表
            srcPanel.updateTable();
        }
        IDENotificationUtil.notifyInfo("", CommonUtil.getRspTipInfo(responseBean), type);
    }

    @Override
    protected void onCancelAction() {
    }
}

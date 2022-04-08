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

package com.huawei.kunpeng.intellij.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.RespondStatus;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import java.awt.Rectangle;
import java.util.List;

import javax.swing.JComponent;

/**
 * 修改密码的弹窗
 *
 * @since 2021-04-10
 */
public abstract class ChangePasswordDialog extends IdeaDialog {
    /**
     * changePasswdStatus
     */
    protected String changePasswdStatus = RespondStatus.PROCESS_STATUS_ERROR.value();

    private String fileName;


    /**
     * ChangePasswordDialog
     *
     * @param title     title
     * @param panel     panel
     * @param resizable resizable
     * @param fileName  fileName
     */
    public ChangePasswordDialog(String title, IDEBasePanel panel, boolean resizable, String fileName) {
        this.title = CommonI18NServer.toLocale("common_change_password");
        this.dialogName = "CHANGE_PWD";
        this.mainPanel = panel;
        this.resizable = resizable;
        this.fileName = fileName;
        rectangle = new Rectangle(0, 0, -1, -1);
        setOKAndCancelName(CommonI18NServer.toLocale("common_term_operate_confirm"),
                CommonI18NServer.toLocale("common_term_operate_cancel"));
        // 初始化弹框内容
        initDialog();
    }

    /**
     * ChangePasswordDialog
     *
     * @param title title
     * @param panel panel
     */
    public ChangePasswordDialog(String title, IDEBasePanel panel) {
        this(title, panel, false, "file");
    }

    /**
     * 初始化弹框
     */
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    /**
     * getFileName
     *
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected boolean okVerify() {
        ChangePasswordPanel panel;
        if (mainPanel instanceof ChangePasswordPanel) {
            panel = (ChangePasswordPanel) mainPanel;
        } else {
            return false;
        }
        // reset password
        return resetUserPwd(new String(panel.getResetOldPwdField().getPassword()),
                new String(panel.getResetNewPwdField().getPassword()),
                new String(panel.getResetConfirmPwdField().getPassword()));
    }

    @Override
    protected void onOKAction() {
        customizeChangePwdOKAction();
    }

    /**
     * 自定义修改密码弹框OKAction
     */
    protected abstract void customizeChangePwdOKAction();

    @Override
    protected void onCancelAction() {
        Logger.info("change password onCancelAction");
        mainPanel.clearPwd();
    }

    /**
     * 重置口令
     *
     * @param pwd        口令
     * @param newPwd     新口令
     * @param confirmPwd 二次确认口令
     * @return boolean 更新结果
     */
    protected abstract boolean resetUserPwd(String pwd, String newPwd, String confirmPwd);

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected List<ValidationInfo> doValidateAll() {
        return this.mainPanel.doValidateAll();
    }
}

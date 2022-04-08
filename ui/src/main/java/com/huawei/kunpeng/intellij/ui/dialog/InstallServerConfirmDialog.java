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

package com.huawei.kunpeng.intellij.ui.dialog;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.InstallServerConfirmPanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.swing.JComponent;

/**
 * 安装成功后登录弹框
 *
 * @since 2020/12/16
 */
public abstract class InstallServerConfirmDialog extends IdeaDialog {
    /**
     * 响应成功状态
     */
    public static final String SUCCESS = "0";

    // 安装时指定/修改的ip
    private static String verifyIP;

    // 初始化为默认端口
    private static String verifyPort;

    // ssh连接时输入的ip
    private static String sshIP;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param dialogName 弹框名称
     * @param panel 需要展示的面板之一
     * @param resizable 大小是否可变
     */
    public InstallServerConfirmDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? "Install Tool" : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName)
            ? Dialogs.INSTALL_SERVER_CONFIRM.dialogName() : dialogName;
        this.resizable = resizable;
        this.mainPanel = panel;
        initDialog();
    }

    /**
     * 代理生成时使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板
     * */
    public InstallServerConfirmDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 更新配置的ip
     *
     * @param ip IP
     * */
    public static void setVerifyIP(String ip) {
        verifyIP = ip;
    }

    /**
     * 更新配置的端口
     *
     * @param port 端口
     * */
    public static void setVerifyPort(String port) {
        verifyPort = port;
    }

    /**
     * 初始化面板容器
     *
     * */
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void onOKAction() {
        if (mainPanel instanceof InstallServerConfirmPanel) {
            InstallServerConfirmPanel confirmPanel = (InstallServerConfirmPanel)mainPanel;
            int select = confirmPanel.getRadioSelect();
            if (select == InstallServerConfirmPanel.SELECT_VERIFY) {
                jumpLogin(verifyIP, verifyPort);
            } else if (select == InstallServerConfirmPanel.SELECT_SSH) {
                jumpLogin(sshIP, verifyPort);
            } else {
                String ip = confirmPanel.getOtherIPText();
                jumpLogin(ip, verifyPort);
            }
        } else {
            Logger.error("The panel is not InstallServerConfirmPanel.");
        }
    }

    @Override
    protected void onCancelAction() {
        Logger.info("Cancel install login confirm.");
    }

    @NotNull
    @Override
    protected DialogStyle getStyle() {
        return DialogStyle.COMPACT;
    }

    /**
     * 跳转登录
     *
     * @param ip ip
     * @param port port
     */
    public abstract void jumpLogin(String ip, String port);

    /**
     * 更新面板内容
     *
     * @param panel 需要更新的面板
     * */
    public static void updateText(IDEBasePanel panel) {
        if (panel instanceof InstallServerConfirmPanel) {
            InstallServerConfirmPanel confirmPanel = (InstallServerConfirmPanel)panel;
            confirmPanel.setTips(verifyIP);
            confirmPanel.setVerifyRadioButtonText(verifyIP);
            sshIP = InstallUpgradeWrapDialog.params.get("ip");
            confirmPanel.setSSHRadioButtonText(sshIP);
            if (sshIP.equals(verifyIP)) {
                confirmPanel.setSshVisible(false);
                confirmPanel.setVerifySelect();
            }
            confirmPanel.updateUI();
        }
    }

    @NotNull
    @Override
    protected List<ValidationInfo> doValidateAll() {
        return mainPanel.doValidateAll();
    }
}

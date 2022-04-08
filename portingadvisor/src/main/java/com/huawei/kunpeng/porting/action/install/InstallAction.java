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

package com.huawei.kunpeng.porting.action.install;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ShellTerminalUtil;
import com.huawei.kunpeng.intellij.ui.action.SshAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.InstallServerConfirmDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.InstallServerConfirmPanel;
import com.huawei.kunpeng.intellij.ui.panel.InstallUpgradePanel;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.ui.dialog.PortingInstallServerConfirmDialog;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingInstallUpgradeWrapDialog;

import com.intellij.notification.NotificationType;
import com.jcraft.jsch.Session;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Timer;

import javax.swing.event.HyperlinkEvent;

/**
 * 安装事件处理器
 *
 * @since 2020-10-08
 */
public class InstallAction extends SshAction {
    /**
     * 点击取消按钮响应
     *
     * @param params        取消配置信息参数
     * @param actionOperate 自定义操作
     */
    public void onCancelAction(Map params, ActionOperate actionOperate) {
    }

    @Override
    public void upload(Session session, String dir) {
        // 上传脚本至dir下
        DeployUtil.upload(session, I18NServer.toLocale("plugins_porting_shell_install"),
            dir + "install_porting.sh");
        Logger.info("scp shellFile success!");
        DeployUtil.upload(session, I18NServer.toLocale("plugins_porting_shell_installRun"),
            dir + "install_porting_run.sh");
        Logger.info("scp logShellFile success!");
    }

    @Override
    public void openTerminal(Map<String, String> param, String dir) {
        // 打开终端执行脚本
        Map url = CommonUtil.getUrl();
        ShellTerminalUtil.openTerminal(param, " bash "
            + dir + "install_porting.sh" + " -a " + url.get("arm") + " -b " + url.get("x86")
            + " -c \"" + url.get("key") + "\"");
    }

    @Override
    public void checkStatus(Session session, Timer timer, String dir) {
        DeployUtil.checkInstallLog(session, timer, dir + "install_porting.log",
            this::failedHandle, this::successHandle);
    }

    @Override
    protected void failedHandle() {
        String failedContent = I18NServer.toLocale("plugins_porting_install_failedPrefix") + I18NServer.toLocale(
            "plugins_porting_install_failedLink") + I18NServer.toLocale("plugins_porting_install_failedSuffix");
        IDENotificationUtil.notificationForHyperlink(
            new NotificationBean(I18NServer.toLocale("plugins_porting_install_title"), failedContent,
                NotificationType.ERROR), getActionOperate());
    }

    private ActionOperate getActionOperate() {
        return data -> {
            if (data instanceof HyperlinkEvent) {
                getInstallUpgradePanel((HyperlinkEvent) data);
            }
        };
    }

    private void getInstallUpgradePanel(HyperlinkEvent linkEvent) {
        if (linkEvent != null && linkEvent.getURL() != null) {
            try {
                Desktop.getDesktop().browse(new URI(linkEvent.getURL().toString()));
            } catch (IOException | URISyntaxException e) {
                Logger.error("An exception occurred when executing ActionOperateHandler.");
            }
        } else {
            InstallAction installAction = new InstallAction();
            InstallUpgradePanel up = new InstallUpgradePanel(
                null, PortingUserManageConstant.INSTALL_TITLE, false, installAction);
            InstallUpgradeWrapDialog dialog = new PortingInstallUpgradeWrapDialog(
                PortingUserManageConstant.INSTALL_TITLE, up);
            dialog.displayPanel();
        }
    }

    @Override
    protected void successHandle() {
        IDEBasePanel panel = new InstallServerConfirmPanel(null);
        IDEBaseDialog dialog = new PortingInstallServerConfirmDialog(null, panel);
        InstallServerConfirmDialog.updateText(panel);
        dialog.displayPanel();
    }
}

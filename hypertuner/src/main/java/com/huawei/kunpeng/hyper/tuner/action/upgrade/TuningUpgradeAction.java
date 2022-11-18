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

package com.huawei.kunpeng.hyper.tuner.action.upgrade;

import com.huawei.kunpeng.hyper.tuner.action.install.TuningInstallAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningInstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.SftpAction;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.SshAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.MaintenanceResponse;
import com.huawei.kunpeng.intellij.ui.panel.InstallUpgradePanel;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;

import com.intellij.notification.NotificationType;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.event.HyperlinkEvent;

/**
 * 升级事件处理器
 *
 * @since @since 2021-01-28
 */
public class TuningUpgradeAction extends SshAction {
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
        DeployUtil.upload(
                session, TuningI18NServer.toLocale("plugins_hyper_tuner_shell_upgradel"), dir + "upgrade_tuning.sh");
        DeployUtil.upload(
                session,
                TuningI18NServer.toLocale("plugins_hyper_tuner_shell_upgradelRun"),
                dir + "upgrade_tuning_run.sh");
    }

    public void newUpload(Session session, String dir, ActionOperate actionOperate) {
        // 上传脚本至dir下
        DeployUtil.newUpload(
                session, TuningI18NServer.toLocale("plugins_hyper_tuner_shell_upgradel"), dir + "upgrade_tuning.sh", actionOperate);
        DeployUtil.newUpload(
                session,
                TuningI18NServer.toLocale("plugins_hyper_tuner_shell_upgradelRun"),
                dir + "upgrade_tuning_run.sh",actionOperate);
    }

    @Override
    public void openTerminal(Map<String, String> param, String dir) {
        // 打开终端执行脚本
        Map url = CommonUtil.getUrl();
        DeployUtil.openTerminal(
                param,
                " bash "
                        + dir
                        + "upgrade_tuning.sh"
                        + " -a "
                        + url.get("arm")
                        + " -b "
                        + url.get("x86")
                        + " -c \""
                        + url.get("key")
                        + "\"");
    }

    @Override
    protected void failedHandle() {
        String failedContent =
                TuningI18NServer.toLocale("plugins_hyper_tuner_upgrade_failedPrefix")
                        + TuningI18NServer.toLocale("plugins_hyper_tuner_upgrade_failedLink")
                        + TuningI18NServer.toLocale("plugins_hyper_tuner_upgrade_failedSuffix");
        IDENotificationUtil.notificationForHyperlink(
                new NotificationBean(
                        TuningI18NServer.toLocale("plugins_tuning_upgrade_title"), failedContent, NotificationType.ERROR),
                data -> {
                    HyperlinkEvent linkEvent = null;
                    if (data instanceof HyperlinkEvent) {
                        linkEvent = (HyperlinkEvent) data;
                    }
                    if (linkEvent != null && linkEvent.getURL() != null
                            && linkEvent.getURL().toString().startsWith(TuningIDEConstant.URL_PREFIX)) {
                        try {
                            Desktop.getDesktop().browse(new URI(linkEvent.getURL().toString()));
                        } catch (IOException | URISyntaxException e) {
                            Logger.error("An exception occurred when executing ActionOperateHandler.");
                        }
                    } else {
                        TuningInstallAction installAction = new TuningInstallAction();
                        InstallUpgradePanel up =
                                new InstallUpgradePanel(null, InstallManageConstant.UPGRADE_TITLE, true, installAction);
                        InstallUpgradeWrapDialog dialog =
                                new TuningInstallUpgradeWrapDialog(InstallManageConstant.UPGRADE_TITLE, up);
                        dialog.displayPanel();
                    }
                });
    }

    @Override
    protected void successHandle() {
        IDENotificationUtil.notificationCommon(
                new NotificationBean(
                        InstallManageConstant.UPGRADE_TITLE,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_upgrade_success"),
                        NotificationType.INFORMATION));
        if (TuningIDEContext.checkServerConfig()) {
            Logger.info("checkServerConfig success");
        }
    }

    @Override
    public void checkStatus(Session session, Timer timer, String dir) {
        DeployUtil.checkUpgradeLog(session, timer, dir + "upgrade_tuning.log", this::failedHandle, this::successHandle);
    }

    public void newCheckStatus(Session session, Timer timer, String dir, ActionOperate actionOperate, String tabName) {
        DeployUtil.newCheckLog(session, timer, dir + "upgrade_tuning.log", actionOperate, tabName);
    }

    public void newOKAction(Map params, ActionOperate actionOperate) {
        Map<String, String> param = JsonUtil.getValueIgnoreCaseFromMap(params, "param", Map.class);
        SshConfig config = DeployUtil.getConfig(param);
        Session session = DeployUtil.getSession(config);
        if (Objects.isNull(session)) {
            actionOperate.actionOperate(MaintenanceResponse.CLOSE_LOADING);
            actionOperate.actionOperate(MaintenanceResponse.SSH_ERROR);
            return;
        }
        try {
            DeployUtil.setUserInfo(session, config);
            session.connect(30000);
        } catch (JSchException e) {
            Logger.error("ssh session connect error: {}", e.getMessage());
            actionOperate.actionOperate(MaintenanceResponse.CLOSE_LOADING);
            actionOperate.actionOperate(MaintenanceResponse.SSH_ERROR);
            return;
        }
        String dir = TMP_PATH + new SimpleDateFormat(TMP_FORMAT).format(new Date(System.currentTimeMillis()))
                + IDEConstant.PATH_SEPARATOR;
        // 创建dir目录
        DeployUtil.sftp(session, dir, SftpAction.MKDIR);
        // 上传脚本至dir下
        newUpload(session, dir, actionOperate);
        if (!session.isConnected()) {
            return;
        }
        // 打开终端执行脚本
        openTerminal(param, dir);
        // 检查脚本执行状态
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                newCheckStatus(session, timer, dir, actionOperate, InstallManageConstant.UPGRADE_TITLE);
            }
        };
        timer.schedule(task, 0, 1000);
    }
}

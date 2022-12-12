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

package com.huawei.kunpeng.hyper.tuner.action.uninstall;

import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningUninstallWrapDialog;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.SftpAction;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.SshAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.UninstallWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.MaintenanceResponse;
import com.huawei.kunpeng.intellij.ui.panel.UninstallPanel;
import com.huawei.kunpeng.intellij.ui.utils.DeployUtil;
import com.intellij.notification.NotificationType;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安装事件处理器
 *
 * @since 2020-10-08
 */
public class TuningUninstallAction extends SshAction {
    /**
     * 取消配置信息
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
                session, TuningI18NServer.toLocale("plugins_hyper_tuner_shell_uninstall"),
                dir + "uninstall_tuning.sh");
        Logger.info("scp shellFile success!");
        DeployUtil.upload(
                session,
                TuningI18NServer.toLocale("plugins_hyper_tuner_shell_uninstallLog"),
                dir + "uninstall_tuning_log.sh");
        Logger.info("scp logShellFile success!");
    }

    public void newUpload(Session session, String dir, ActionOperate actionOperate) {
        // 上传脚本至dir下
        DeployUtil.newUpload(
                session, TuningI18NServer.toLocale("plugins_hyper_tuner_shell_uninstall"),
                dir + "uninstall_tuning.sh", actionOperate);
        Logger.info("scp shellFile success!");
        DeployUtil.newUpload(
                session,
                TuningI18NServer.toLocale("plugins_hyper_tuner_shell_uninstallLog"),
                dir + "uninstall_tuning_log.sh", actionOperate);
        Logger.info("scp logShellFile success!");
    }

    @Override
    public void openTerminal(Map<String, String> param, String dir) {
        // 打开终端执行脚本
        DeployUtil.openTerminal(param, " \"bash " + dir + "uninstall_tuning.sh\"");
    }

    @Override
    public void checkStatus(Session session, Timer timer, String dir) {
        DeployUtil.checkUninstallLog(
                session, timer, dir + "uninstall_tuing.log", this::failedHandle, this::successHandle);
    }

    @Override
    protected void failedHandle() {
        String failedContent = TuningI18NServer.toLocale("plugins_hyper_tuner_uninstall_failedPrefix")
                + TuningI18NServer.toLocale("plugins_hyper_tuner_uninstall_failedLink")
                + TuningI18NServer.toLocale("plugins_hyper_tuner_uninstall_failedSuffix");
        String uninstallTitle = TuningI18NServer.toLocale("plugins_hyper_tuner_uninstall_title");
        IDENotificationUtil.notificationForHyperlink(
                new NotificationBean(uninstallTitle, failedContent, NotificationType.ERROR),
                data -> {
                    TuningUninstallAction tuningUninstallAction = new TuningUninstallAction();
                    UninstallPanel up = new UninstallPanel(null, uninstallTitle, tuningUninstallAction);
                    UninstallWrapDialog uninstallWrapDialog = new TuningUninstallWrapDialog(uninstallTitle, up);
                    uninstallWrapDialog.displayPanel();
                });
    }

    @Override
    protected void successHandle() {
        NotificationBean notificationBean =
                new NotificationBean(
                        InstallManageConstant.UNINSTALL_TITLE,
                        InstallManageConstant.UNINSTALL_SUCCESS,
                        NotificationType.INFORMATION);
        IDENotificationUtil.notificationCommon(notificationBean);
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
                newCheckStatus(session, timer, dir, actionOperate, InstallManageConstant.UNINSTALL_TITLE);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    public void newCheckStatus(Session session, Timer timer, String dir, ActionOperate actionOperate, String tabName) {
        DeployUtil.newCheckLog(session, timer, dir + "uninstall_tuning.log", actionOperate, tabName);
    }
}

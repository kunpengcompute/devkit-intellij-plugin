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

package com.huawei.kunpeng.porting.process;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.SETTINGS_COMPRESSING;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.SETTINGS_COMPRESS_LOGS_FAILED;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_OPERATE_CANCEL;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.porting.action.setting.syssetting.SystemConfigAction;
import com.huawei.kunpeng.porting.ui.panel.LogManagerPanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.ProjectManager;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

/**
 * Log日志压缩任务进度条管理
 *
 * @since 2020-11-09
 */
public class LogTaskProcess extends Task.ConditionalModal {
    /**
     * 状态参数
     */
    private static final String STATUS_PARAMS = "status";

    /**
     * 成功状态。
     */
    private static final String STATUS_SUCCEEDED = "0";

    /**
     * 成功状态。
     */
    private static final int STATUS_SUCCEEDED_CODE = 0;

    /**
     * 失败状态。
     */
    private static final int STATUS_FAIL_CODE = 1;

    /**
     * 睡眠时间
     */
    private static final int SLEEP_TIMES = 500;

    /**
     * 日志管理操作类
     */
    private SystemConfigAction systemConfigAction;

    /**
     * 日志管理操作类
     */
    private String taskName;

    /**
     * 日志下载路径
     */
    private String path;

    /**
     * 日志面板
     */
    private LogManagerPanel logManagerPanel;

    /**
     * 构造函数
     *
     * @param systemConfigAction logAction
     */
    public LogTaskProcess(SystemConfigAction systemConfigAction, String taskName, String path, String title,
        LogManagerPanel logManagerPanel) {
        super(ProjectManager.getInstance().getDefaultProject(), title, true,
            PerformInBackgroundOption.ALWAYS_BACKGROUND);
        this.systemConfigAction = systemConfigAction;
        this.taskName = taskName;
        // 目录为空获取插件安装目录作为日志下载目录;
        this.path = ValidateUtils.isNotEmptyString(path) ? path : CommonUtil.getPluginInstalledPath();
        this.logManagerPanel = logManagerPanel;
        setCancelText(TERM_OPERATE_CANCEL);
        setCancelTooltipText(TERM_OPERATE_CANCEL);
    }

    /**
     * 任务取消后的动作
     */
    @Override
    public void onCancel() {
        super.onCancel();
        systemConfigAction.deleteTask(taskName);
        Logger.info("the user delete log task");
        // 取消按钮置灰
        logManagerPanel.cancelGrayed();
    }

    /**
     * 任务执行成功后的动作
     */
    @Override
    public void onSuccess() {
        super.onSuccess();
        systemConfigAction.downloadRunLogByNewWays(path);
        Logger.info("start to download run log");
        // 取消按钮置灰
        logManagerPanel.cancelGrayed();
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        super.onThrowable(error);
        logManagerPanel.cancelGrayed(); // 取消按钮置灰
    }

    @Override
    public void onFinished() {
        super.onFinished();
        logManagerPanel.cancelGrayed(); // 取消按钮置灰
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        while (true) {
            if (indicator.isCanceled()) {
                break;
            }
            indicator.checkCanceled();
            indicator.setText(SETTINGS_COMPRESSING);
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMES);
            } catch (InterruptedException e) {
                Logger.error("Get analysis process failed. exception : InterruptedException");
            }

            ResponseBean responseBean = systemConfigAction.selectCompressesTaskStatus(taskName);
            if (responseBean == null) {
                Logger.error("selectCompressesTaskStatus error! responseBean is null!");
                continue;
            }

            if (!Objects.equals(STATUS_SUCCEEDED, responseBean.getStatus())) {
                Logger.error("selectCompressesTaskStatus fail!!");
                break;
            }

            Map<String, Integer> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (jsonMessage != null) {
                Integer status = jsonMessage.get(STATUS_PARAMS);
                if (status == STATUS_SUCCEEDED_CODE) {
                    Logger.info("Compresses Task Status is succeed");
                    break;
                }

                if (status == STATUS_FAIL_CODE) {
                    IDENotificationUtil.notificationCommon(
                        new NotificationBean(I18NServer.toLocale("plugins_porting_button_download_log"),
                            SETTINGS_COMPRESS_LOGS_FAILED, NotificationType.ERROR));
                    break;
                }
            }
        }
    }
}

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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.enums.CMDFunction;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.module.CommonHttpHandler;
import com.huawei.kunpeng.porting.http.module.enhance.CacheLineAlignmentHandler;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The class CacheLineProgress: show cache line alignment progress
 *
 * @since 2021/10/14
 */

public class CacheLineProgress extends IDEBaseTask {
    private static final int SLEEP_TIMES = 200;

    private static final int CACHE_LINE_TASK_DONE = 2;

    private static final int CACHE_LINE_TASK_ERROR = 3;

    private String notifyMsg;

    private NotificationType notificationType = NotificationType.INFORMATION;

    private final String taskType;

    private final String taskId;

    private final String callbackId;

    private final String status;

    public CacheLineProgress(String taskType, String taskId, String callbackId, String status) {
        this.taskType = taskType;
        this.taskId = taskId;
        this.callbackId = callbackId;
        this.status = status;
    }

    @Override
    protected void runTask(ProgressIndicator indicator) {
        NoFractionWaitingProcess.createTaskLackWorkerNotification(status, "");
        while (!cacheLineTaskIsDone() && !indicator.isCanceled()) {
            indicator.checkCanceled();
            String text = I18NServer.toLocale("plugins_porting_cacheline_alignment_task_process_tips");
            indicator.setText(text);
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMES);
            } catch (InterruptedException e) {
                Logger.error("Thread of cacheLine interrupted cause: {}", e.getMessage());
            }
        }
    }

    @Override
    protected void cancel(ProgressIndicator indicator) {
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(2);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        // 弹出确定删除任务Dialog
        String exitCode = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_porting_cacheline_alignment_task_cancel_tips"), "",
                buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            deleteCacheLineAlignmentTask();
        } else {
            CacheLineProgress process = new CacheLineProgress(taskType, taskId, callbackId, status);
            process.processForCommon(null,
                    I18NServer.toLocale("plugins_enhanced_functions_cacheline_alignment_title"), process);
        }
    }

    @Override
    protected void success(ProgressIndicator indicator) {
        IDENotificationUtil.notificationCommon(new NotificationBean("", notifyMsg, notificationType));
    }

    /**
     * Query current cache line task is done
     *
     * @return true or false
     */
    private boolean cacheLineTaskIsDone() {
        final ResponseBean responseBean = CommonHttpHandler.queryTaskProgress(taskType, taskId);
        if (responseBean == null) {
            return true;
        }

        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (progressData.get("status") instanceof Integer) {
            int respStatus = (Integer) progressData.get("status");
            if (respStatus == CACHE_LINE_TASK_DONE) {
                notifyMsg = CommonUtil.getRspTipInfo(responseBean);
                // 回调
                CommonHandler.invokeCallback(CMDFunction.CACHE_LINE_PROGRESS.functionName(),
                        this.callbackId, responseBean.getResponseJsonStr());
                return true;
            }
            if (!"0".equals(responseBean.getStatus()) || respStatus == CACHE_LINE_TASK_ERROR) {
                notificationType = NotificationType.ERROR;
                notifyMsg = CommonUtil.getRspTipInfo(responseBean);
                // 回调
                CommonHandler.invokeCallback(CMDFunction.CACHE_LINE_PROGRESS.functionName(),
                        this.callbackId, responseBean.getResponseJsonStr());
                return true;
            }
        }
        return false;
    }

    /**
     * Delete cache line alignment tasks in server
     */
    private void deleteCacheLineAlignmentTask() {
        final ResponseBean responseBean = CacheLineAlignmentHandler.getInstance().deleteCacheLineTask(taskId);
        if (responseBean == null) {
            return;
        }
        String notContent = CommonUtil.getRspTipInfo(responseBean);
        NotificationType type = NotificationType.ERROR;
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            type = NotificationType.INFORMATION;
            notContent = I18NServer.toLocale("plugins_porting_settings_task_canceled_success");
        }
        IDENotificationUtil.notificationCommon(new NotificationBean("", notContent, type));
        // 回调
        CommonHandler.invokeCallback(CMDFunction.CACHE_LINE_PROGRESS.functionName(),
                this.callbackId, responseBean.getResponseJsonStr());
    }
}

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
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.CMDFunction;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.ProjectManager;

import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Setter;

import org.jetbrains.annotations.NotNull;

/**
 * 64位迁移预检进度
 *
 * @since 2021/1/8
 */
public class PreCheckProcess extends Task.ConditionalModal {
    private static final String STATUS = "status";

    private static final String SCAN_RESULT = "scan_result";

    private static final String SCAN_CURRENT_FILE = "scan_current_file";

    private static final String PRE_CHECK_TITLE = I18NServer.toLocale("plugins_porting_precheck_tip");

    private static final int BUTTON_NUM = 2;

    private static final int SLEEP_TIMES = 200;

    private static final int STATUS_1 = 1;

    private static final int STATUS_2 = 2;

    private static final int STATUS_3 = 3;

    private static final int STATUS_4 = 4;

    /**
     * 64位迁移预检任务回调ID。
     * 为防止用户将原页面关闭，新打开webview找不到原页面回调id对应的回调函数，
     * 在有任务进度的情况下更新此id，以新打开页面的回调函数完成回调
     */
    @Setter
    private static String preCheckCallBackId = "";

    private final String taskType;

    private final String taskId;

    private NotificationType type;

    private String message;

    private String status;

    private final Window window;

    /**
     * PreCheckProcess
     *
     * @param taskId taskId
     * @param cbid   cbid
     * @param status status
     */
    public PreCheckProcess(String taskId, String cbid, String status) {
        super(ProjectManager.getInstance().getDefaultProject(),
                PRE_CHECK_TITLE + "······", true,
            PerformInBackgroundOption.ALWAYS_BACKGROUND);
        this.taskType = TaskType.MIGRATION_PRE_CHECK.value();
        this.taskId = taskId;
        this.status = status;
        this.window = CommonUtil.getDefaultWindow();
        PreCheckProcess.preCheckCallBackId = cbid;
        CommonUtil.setBackGroundProcessWindowOpen(true, this.window);
    }

    /**
     * 任务取消后的动作
     */
    @Override
    public void onCancel() {
        super.onCancel();
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(BUTTON_NUM);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        // 弹出确定删除任务Dialog
        String exitCode = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_porting_precheck_cancel_alert"),
                "", buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            deleteTask(taskId, taskType, preCheckCallBackId);
            CommonUtil.setBackGroundProcessWindowOpen(false, window);
        } else {
            // 取消删除
            final Task.Backgroundable taskProcess = new PreCheckProcess(taskId, preCheckCallBackId, status);
            final ProgressManager progress = ProgressManager.getInstance();
            progress.run(taskProcess);
        }
    }

    /**
     * 任务执行成功后的动作
     */
    @Override
    public void onSuccess() {
        super.onSuccess();
        IDENotificationUtil.notificationCommon(new NotificationBean("", message, type));
        PortingIDETask.deleteRunTask(taskType);
        CommonUtil.setBackGroundProcessWindowOpen(false, window);
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        super.onThrowable(error);
    }

    @Override
    public void onFinished() {
        super.onFinished();
    }

    /**
     * 分析任务中的循环状态查询
     *
     * @param indicator 自定义参数
     */
    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        String progressInfo = "";
        NoFractionWaitingProcess.createTaskLackWorkerNotification(status,
                PRE_CHECK_TITLE);
        while (true) {
            if (indicator.isCanceled()) {
                break;
            }
            indicator.checkCanceled();
            indicator.setText(PRE_CHECK_TITLE + ": " + progressInfo);
            Map<String, Object> progressData = queryProgress();
            if (!(progressData.get(STATUS) instanceof Integer)) {
                break;
            }
            int statusTemp = (Integer) progressData.get(STATUS);
            if (statusTemp == STATUS_2 || statusTemp == STATUS_3 || statusTemp == STATUS_4) {
                break;
            }
            progressInfo = String.valueOf(progressData.get(SCAN_CURRENT_FILE));
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMES);
            } catch (InterruptedException e) {
                Logger.error("Get pre-check progress failed. exception : InterruptedException");
            }
        }
    }

    /**
     * Query task progress
     *
     * @return progressData
     */
    private Map<String, Object> queryProgress() {
        Language.getRegisteredLanguages();
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean data = new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, url, HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return new HashMap<>();
        }
        Logger.info("Query pre-check task progress info.");
        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (progressData.get(STATUS) instanceof Integer) {
            int statusTemp = (Integer) progressData.get(STATUS);
            type = NotificationType.INFORMATION;
            message = CommonUtil.getRspTipInfo(responseBean);
            if (statusTemp == STATUS_3) {
                type = NotificationType.ERROR;
                message = I18NServer.toLocale("plugins_porting_pre_check_fail_info");
                FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                        preCheckCallBackId, responseBean.getResponseJsonStr());
            }
            if (statusTemp == STATUS_4) {
                type = NotificationType.ERROR;
                message = CommonUtil.getRepDataInfo(responseBean, "error_info", "info_chinese");
                FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                        preCheckCallBackId, responseBean.getResponseJsonStr());
            }
            if (statusTemp == STATUS_2) {
                Object obj = progressData.get(SCAN_RESULT);
                boolean isShowReport = !JsonUtil.getJsonObjFromJsonStr(obj.toString()).isEmpty();
                message = isShowReport ? I18NServer.toLocale("plugins_64_bit_mode_check_tip_status_success",
                    StringUtil.formatCreatedId(taskId)) : message;
                FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                        preCheckCallBackId, responseBean.getResponseJsonStr());
            }
            return progressData;
        }
        Map<String, Object> result = new HashMap<>();
        result.put(SCAN_CURRENT_FILE, "");
        result.put(STATUS, STATUS_1);
        return result;
    }

    /**
     * 取消  Task
     *
     * @param taskId   task id
     * @param taskType task type
     * @param cbid     callBackId
     */
    public static void deleteTask(String taskId, String taskType, String cbid) {
        String url = "/portadv/tasks/migrationscan/" + taskId + "/";
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return;
        }
        Logger.info("delete pre-check task.");
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;

        IDENotificationUtil.notificationCommon(
            new NotificationBean("", CommonUtil.getRspTipInfo(responseBean), notificationType));
        PortingIDETask.deleteRunTask(taskType);
        FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                cbid, responseBean.getResponseJsonStr());
    }
}

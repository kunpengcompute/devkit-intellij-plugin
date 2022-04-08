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
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
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
import com.intellij.openapi.progress.ProgressIndicator;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 弱内存序进度
 *
 * @since 2021/1/7
 */
public class WeakConsistencyProcess extends IDEBaseTask {
    private static final String PROGRESS = "progress";

    private static final String STATUS = "runningstatus";

    private static final int BUTTON_NUM = 2;

    private static final int SLEEP_TIMES = 200;

    private static final int MAX_PROGRESS = 100;

    private static final int STATUS_0 = 0;

    private static final int STATUS_2 = 2;

    private final String taskType;

    private final String taskId;

    private final String cbid;

    private boolean isShowInfo = false;

    private String message;

    private String status;

    /**
     * WeakConsistencyProcess
     *
     * @param taskType taskType
     * @param taskId   taskId
     * @param cbid     cbid
     * @param status   status
     */
    public WeakConsistencyProcess(String taskType, String taskId, String cbid, String status) {
        this.taskType = taskType;
        this.taskId = taskId;
        this.cbid = cbid;
        this.status = status;
    }

    /**
     * 分析任务中的循环状态查询
     *
     * @param indicator 自定义参数
     */
    @Override
    protected void runTask(ProgressIndicator indicator) {
        // 当前进度
        int processCurrentValue = 0;
        NoFractionWaitingProcess.createTaskLackWorkerNotification(status, getProgressShowTitle(taskType));
        try {
            while (processCurrentValue < MAX_PROGRESS) {
                if (indicator.isCanceled()) {
                    break;
                }
                indicator.checkCanceled();
                String progressShowInfo = getProgressShowInfo(taskType) + "···";
                if (!Objects.equals(taskType, TaskType.WEAK_COMPILE.value())) {
                    indicator.setFraction(
                        new BigDecimal(processCurrentValue).divide(new BigDecimal(MAX_PROGRESS)).doubleValue());
                    progressShowInfo = progressShowInfo
                        + processCurrentValue + "%";
                }
                indicator.setText(progressShowInfo);
                processCurrentValue = queryProgress();
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMES);
            }
        } catch (InterruptedException e) {
            Logger.error("Get weak consistency progress failed. exception : InterruptedException");
        }
    }

    /**
     * 取消任务
     *
     * @param indicator 自定义参数
     */
    @Override
    protected void cancel(ProgressIndicator indicator) {
        // 按钮数组
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(BUTTON_NUM);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        // 弹出确定删除任务Dialog
        String exitCode = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(getCancelDialogInfo(taskType), "",
                buttonNames, 0,
                IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            deleteTask(taskId, taskType, cbid);
        } else {
            // 取消删除
            WeakConsistencyProcess process = new WeakConsistencyProcess(taskType, taskId, cbid, status);
            process.processForCommon(null, getProgressShowTitle(taskType), process);
        }
    }

    /**
     * 分析成功
     *
     * @param indicator 自定义参数
     */
    @Override
    protected void success(ProgressIndicator indicator) {
        PortingIDETask.deleteRunTask(taskType);
        if (isShowInfo) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", message, NotificationType.INFORMATION));
        }
    }

    /**
     * Query task progress
     *
     * @return progress
     */
    private int queryProgress() {
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean data = new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, url, HttpMethod.GET.vaLue(), "");
        data.setNeedProcess(true);
        Language.getRegisteredLanguages();
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            isTaskSuccess = false;
            return MAX_PROGRESS;
        }
        // 内存一致性处理失败场景错误码。
        if (Objects.equals(taskType, TaskType.WEAK_COMPILE.value())
            && Objects.equals(responseBean.getRealStatus(), "0x0d0b11")) {
            handlerErrorEvent(responseBean);
            return MAX_PROGRESS;
        }
        Logger.info("Query weak consistency task progress.");
        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (progressData.get(STATUS) instanceof Integer) {
            int statusTemp = (Integer) progressData.get(STATUS);
            if (!Objects.equals(taskType, TaskType.WEAK_COMPILE.value())) {
                isShowInfo = (statusTemp == STATUS_0);
            }

            // 空数据报告状态下，不打开报告页面并由webview侧同意发生提示消息。
            if ((Objects.equals(taskType, TaskType.WEAK_CHECK.value())
                || Objects.equals(taskType, TaskType.BC_CHECK.value()))
                && Objects.equals(responseBean.getRealStatus(), "0x0d0a02")) {
                isShowInfo = false;
            }
            if (statusTemp != STATUS_2) {
                if (isShowInfo) {
                    message = I18NServer.dataToLocale(progressData);
                }
                FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                        cbid, responseBean.getResponseJsonStr());
            }
            return Integer.parseInt(progressData.get(PROGRESS).toString());
        }
        return MAX_PROGRESS;
    }

    private void handlerErrorEvent(ResponseBean responseBean) {
        String tips = MessageFormat.format(
            I18NServer.toLocale("plugins_porting_enhance_function_bc_check_error_tip"),
            CommonUtil.getRspTipInfo(responseBean));
        NotificationBean notificationBean = new NotificationBean("",
            tips,
            NotificationType.ERROR);
        IDENotificationUtil.notificationForHyperlink(notificationBean, data1 -> CommonUtil.openURI(
            I18NServer.toLocale("plugins_porting_enhance_function_bc_check_user_guide_path")));
        FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(), cbid, responseBean.getResponseJsonStr());
    }

    /**
     * 取消 porting Task
     *
     * @param taskId   task id
     * @param taskType task type
     * @param cbid     callBackId
     */
    public static void deleteTask(String taskId, String taskType, String cbid) {
        String url = "/portadv/weakconsistency/tasks/" + taskId + "/stop/?task_type=" + taskType;
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return;
        }
        Logger.info("delete weak consistency task.");
        String notContent = CommonUtil.getRspTipInfo(responseBean);
        NotificationType notificationType = NotificationType.ERROR;
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            notificationType = NotificationType.INFORMATION;
            notContent = I18NServer.toLocale("plugins_porting_settings_task_canceled_success");
        }
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", notContent, notificationType));
        PortingIDETask.deleteRunTask(taskType);
        FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(), cbid, responseBean.getResponseJsonStr());
    }

    /**
     * 获取取消确认框中的info
     *
     * @param taskType 任务类型
     * @return info
     */
    private String getCancelDialogInfo(String taskType) {
        if (taskType.equals(TaskType.WEAK_COMPILE.value())) {
            return I18NServer.toLocale("plugins_porting_weak_compile_cancel_alert");
        } else if (taskType.equals(TaskType.WEAK_CHECK.value())) {
            return I18NServer.toLocale("plugins_porting_weak_check_cancel_alert");
        } else {
            return I18NServer.toLocale("plugins_porting_bc_check_cancel_alert");
        }
    }

    /**
     * 获取进度条中展示的info
     *
     * @param taskType 任务类型
     * @return info
     */
    private String getProgressShowInfo(String taskType) {
        if (taskType.equals(TaskType.WEAK_COMPILE.value())) {
            return I18NServer.toLocale("plugins_porting_weak_check_compile_file");
        } else if (taskType.equals(TaskType.WEAK_CHECK.value())) {
            return I18NServer.toLocale("plugins_porting_enhance_function_weak_check_processing_tip");
        } else {
            return I18NServer.toLocale("plugins_porting_enhance_function_bc_check_tip");
        }
    }

    /**
     * 获取进度条中展示的title
     *
     * @param taskType 任务类型
     * @return title
     */
    private String getProgressShowTitle(String taskType) {
        if (taskType.equals(TaskType.WEAK_COMPILE.value())) {
            return I18NServer.toLocale("plugins_porting_weak_check_compile_file") + "······";
        } else if (taskType.equals(TaskType.WEAK_CHECK.value())) {
            return I18NServer.toLocale("plugins_porting_weak_check_progress_label");
        } else {
            return I18NServer.toLocale("plugins_porting_enhance_function_checking") + "······";
        }
    }
}

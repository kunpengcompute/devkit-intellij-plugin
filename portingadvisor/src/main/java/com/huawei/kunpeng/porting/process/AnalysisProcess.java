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

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
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
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;
import com.huawei.kunpeng.porting.webview.handler.PortSourceFunctionHandler;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationAppraiseReportEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingReportPageEditor;

import com.alibaba.fastjson.JSONObject;
import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * The class AnalysisProcess: Handle porting process
 *
 * @since v1.0
 */
public class AnalysisProcess extends IDEBaseTask {
    private static final String TASK_ID = "id";

    private static final String PROGRESS = "progress";

    private static final int BUTTON_NUM = 2;

    private static final int TASK_SUCCESS_PROGRESS = 100;

    private static final String STATUS_OK = "0";

    private String taskType;

    private String taskId;

    private String callBackId;

    private boolean isShowReport = true;

    private String emptyMessage;

    private String status;

    private NotificationType notificationType = NotificationType.INFORMATION;

    /**
     * AnalysisProcess
     *
     * @param taskType taskType
     * @param taskId   taskId
     */
    public AnalysisProcess(String taskType, String taskId) {
        this(taskType, taskId, null, null);
    }

    /**
     * AnalysisProcess
     *
     * @param taskType   taskType
     * @param taskId     taskId
     * @param callBackId callBackId
     */
    public AnalysisProcess(String taskType, String taskId, String callBackId, String status) {
        this.taskType = taskType;
        this.taskId = taskId;
        this.callBackId = callBackId;
        this.status = status;
    }

    /**
     * 设置任务ID
     *
     * @param taskId 任务ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取任务ID
     *
     * @return String
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 状态码
     *
     * @return String
     */
    public String getStatus() {
        return status;
    }

    /**
     * 状态码
     *
     * @param status status
     */
    public void setStatus(String status) {
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
        if (Objects.equals(taskType, TaskType.SOURCE_SCAN.value())) {
            NoFractionWaitingProcess.createTaskLackWorkerNotification(status,
                I18NServer.toLocale("plugins_porting_tip_analysis_title"));
        }
        try {
            while (processCurrentValue < TASK_SUCCESS_PROGRESS) {
                if (indicator.isCanceled()) {
                    break;
                }
                indicator.checkCanceled();
                indicator.setFraction(new BigDecimal(processCurrentValue).divide(new BigDecimal(100)).doubleValue());
                String message = I18NServer.toLocale("plugins_porting_message_sourcecode_porting");
                indicator.setText(message + new BigDecimal(
                    processCurrentValue < 0 ? 0 : Double.valueOf(processCurrentValue)).intValue() + "%");
                processCurrentValue = queryAnalysisProgress(taskType, taskId);
                TimeUnit.MILLISECONDS.sleep(200);
            }
        } catch (InterruptedException e) {
            Logger.error("Get analysis process failed. exception : InterruptedException");
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
        String title = "";
        String message = "";
        if ("7".equals(taskType)) {
            title = I18NServer.toLocale("plugins_port_migration_appraise");
            message = I18NServer.toLocale("plugins_porting_cancel_migration_appraise_alert");
        } else {
            title = I18NServer.toLocale("plugins_porting_tip_analysis_title");
            message = I18NServer.toLocale("plugins_porting_cancel_alert");
        }
        // 弹出确定删除任务Dialog
        String exitCode = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(message, title, buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            if (queryPortingTaskUndone()) {
                deletePortingTask(taskId, taskType, callBackId);
            }
        } else {
            // 取消删除
            AnalysisProcess process = new AnalysisProcess(taskType, taskId, callBackId, status);
            process.processForCommon(null, title, process);
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
        if (!isShowReport) {
            IDENotificationUtil.notificationCommon(new NotificationBean("",
                emptyMessage, notificationType));
            return;
        }
        String content = I18NServer.toLocale("plugins_porting_tip_status_success", StringUtil.formatCreatedId(taskId));
        if ("7".equals(taskType)) {
            content = I18NServer.toLocale("plugins_software_tip_status_success", StringUtil.formatCreatedId(taskId));
        }
        IDENotificationUtil.notificationCommon(new NotificationBean("", content, NotificationType.INFORMATION));
        // 在portingAdvisor左侧导航树添加record
        LeftTreeUtil.refreshReports();
        // 在当前project打开报告页面
        openReport();
    }

    private void openReport() {
        switch (taskType) {
            case "0":
                PortSourceFunctionHandler.closeTaskView();
                PortingReportPageEditor.openPage(taskId);
                break;
            case "7":
                MigrationAppraiseReportEditor.openPage(taskId);
                break;
            default:
                break;
        }
    }

    /**
     * Submit porting task
     *
     * @param params porting task params
     * @return taskId
     */
    public String executeAnalysis(Map params) {
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/",
            HttpMethod.POST.vaLue(), "");
        data.setBodyData(JsonUtil.getJsonStrFromJsonObj(params));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return "";
        }
        Logger.info("Submit porting task, response: {}", responseBean);
        this.status = responseBean.getRealStatus();
        return handlerTaskData(responseBean);
    }

    /**
     * Handle response data for porting task
     *
     * @param responseBean response data
     * @return taskId
     */
    private String handlerTaskData(ResponseBean responseBean) {
        String taskIdDef = null;
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            Map<String, Object> taskData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            taskIdDef = taskData.get(TASK_ID).toString();
        } else {
            if (Objects.equals(responseBean.getRealStatus(), NoFractionWaitingProcess.CREATE_TASK_NO_WORKER)) {
                toDoTip();
            } else {
                IDENotificationUtil.notificationCommon(
                    new NotificationBean(I18NServer.toLocale("plugins_porting_tip_analysis_title"),
                        CommonUtil.getRspTipInfo(responseBean), NotificationType.ERROR));
            }
        }
        return taskIdDef;
    }

    private void toDoTip() {
        NotificationBean notificationBean = new NotificationBean("",
            I18NServer.toLocale("common_term_users_create_task_no_worker_tips"),
            NotificationType.ERROR);
        IDENotificationUtil.notificationForHyperlink(notificationBean, new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                CommonUtil.openURI(CommonHandler.USER_GUIDE_PATH);
            }
        });
    }

    /**
     * Query porting task process
     *
     * @param taskType task type
     * @param taskId   task id
     * @return process
     */
    public int queryAnalysisProgress(String taskType, String taskId) {
        Language.getRegisteredLanguages();
        ResponseBean responseBean = SourcePortingHandler.getInstance().getAnalysisProgress(taskType, taskId);
        if (responseBean == null) {
            isTaskSuccess = false;
            return TASK_SUCCESS_PROGRESS;
        }
        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (progressData.get(PROGRESS) instanceof Integer) {
            int progress = (Integer) progressData.get(PROGRESS);
            if (progress == TASK_SUCCESS_PROGRESS) {
                checkIsShowReport(progressData, taskType);
                emptyMessage = CommonUtil.getRspTipInfo(responseBean);
                if (!Objects.equals(responseBean.getStatus(), STATUS_OK)
                    || RespondStatus.DISK_NOT_ENOUGH.value().equals(responseBean.getStatus())) {
                    notificationType = NotificationType.ERROR;
                }
                // 通过callBackId回调刷新按钮
                if (callBackId != null) {
                    CommonHandler.invokeCallback("getData", callBackId, responseBean.getResponseJsonStr());
                }
            }
            return progress;
        }

        return Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value());
    }

    /**
     * 判断分析扫描完成时。有没有可迁移内容，没有则不需要打开报告
     * 不同类型判断方式不同
     *
     * @param progressData responseBean.getData()
     * @param taskType     分析任务类型
     */
    private void checkIsShowReport(Map<String, Object> progressData, String taskType) {
        switch (taskType) {
            case "0":
                isShowReport = !JSONObject.parseObject(progressData.get("portingresult").toString()).isEmpty();
                break;
            case "7":
                isShowReport = !progressData.get("report_id").toString().isEmpty()
                    && !"999".equals(progressData.get("report_id").toString());
                break;
            default:
                break;
        }
    }

    /**
     * 查询是否有porting正在执行
     *
     * @return boolean
     */
    public boolean queryPortingTaskUndone() {
        String url = "/portadv/tasks/taskundone/";
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return false;
        }
        Logger.info("query Porting Task, Response: {}", responseBean.toString());
        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }

    /**
     * 取消 porting Task
     *
     * @param taskId     task id
     * @param taskType   task type
     * @param callBackId callBackId
     */
    public static void deletePortingTask(String taskId, String taskType, String callBackId) {
        String url = "/portadv/tasks/" + taskId + "/";
        String title = I18NServer.toLocale("plugins_porting_tip_analysis_title");
        if ("7".equals(taskType)) {
            url = "/portadv/binary/" + taskId + "/";
            title = I18NServer.toLocale("plugins_port_migration_appraise");
        }
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return;
        }
        Logger.info("delete porting Task, Response: {}", responseBean.toString());
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;

        IDENotificationUtil.notificationCommon(
            new NotificationBean(title, CommonUtil.getRspTipInfo(responseBean), notificationType));

        PortingIDETask.deleteRunTask(taskType);
        // 通过callBackId回调刷新按钮
        if (callBackId != null) {
            CommonHandler.invokeCallback("getData", callBackId, responseBean.getResponseJsonStr());
        }
    }
}

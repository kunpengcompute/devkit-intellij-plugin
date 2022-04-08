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
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.pkgrebuild.PkgRebuildingHandler;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisReportEditor;

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
 * AnalysisServer: 软件包重构进度条。
 *
 * @since v1.0
 */
public class WorkingProcess extends IDEBaseTask {
    private static final String PROGRESS = "progress";

    private static final int BUTTON_NUM = 2;

    private static final int THRESHOLD = 100;

    private static final int SLEEP_TIME = 200;

    private static final int STATUS_TYPE_SUCCESS = 0;

    private static final int STATUS_TYPE_NO_NEED = 3;

    private final String taskType;

    private String taskId;

    private boolean isNeedOpenReport = true;

    private final String title = I18NServer.toLocale("plugins_porting_software_rebuilding_title");

    /**
     * WorkingProcess
     *
     * @param taskType taskType
     * @param taskId   taskId
     */
    public WorkingProcess(String taskType, String taskId) {
        this.taskType = taskType;
        this.taskId = taskId;
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
     * 分析任务中的循环状态查询
     *
     * @param indicator 自定义参数
     */
    @Override
    protected void runTask(ProgressIndicator indicator) {
        AnalysisEditor.setIsCanOpen(false);
        // 当前进度
        int processCurrentValue = 0;
        try {
            while (processCurrentValue < THRESHOLD) {
                if (indicator.isCanceled() ||
                    processCurrentValue == Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value())) {
                    break;
                }
                indicator.checkCanceled();
                indicator.setFraction(
                    new BigDecimal(processCurrentValue).divide(new BigDecimal(THRESHOLD)).doubleValue());
                indicator.setText(I18NServer.toLocale("plugins_porting_webpack_analysizing")
                    + " " + BigDecimal.valueOf(Double.valueOf(processCurrentValue)).intValue() + "%");
                processCurrentValue = queryAnalysisProgress(taskType, taskId, indicator);
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
            }
        } catch (InterruptedException e) {
            AnalysisEditor.setIsCanOpen(true);
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
        // 弹出确定删除任务Dialog
        String exitCode = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_porting_close_task_confirm_tip"),
                title, buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            deleteAnalysisTask(taskId);
            AnalysisEditor.setIsCanOpen(true);
            AnalysisEditor.openPage(title);
        } else {
            // 取消删除
            WorkingProcess process = new WorkingProcess(taskType, taskId);
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
        // 左侧导航树添加重构结果
        PkgRebuildingHandler.refreshRebuildResults();
        AnalysisEditor.setIsCanOpen(true);

        // 在当前project打开报告页面
        AnalysisEditor.closePage(title);

        // 在当前project打开报告页面
        if (isNeedOpenReport) {
            openReport();
        } else {
            AnalysisEditor.openPage(title);
        }
    }

    private void openReport() {
        AnalysisReportEditor.openPage(taskId);
    }

    /**
     * 查询软件包重构进度。
     *
     * @param taskType  task type
     * @param taskId    task id
     * @param indicator indicator
     * @return process
     */
    private int queryAnalysisProgress(String taskType, String taskId, ProgressIndicator indicator) {
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            url, HttpMethod.GET.vaLue(), "");
        data.setNeedProcess(true);
        Language.getRegisteredLanguages();
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        Logger.info("Query Analysis task process, Response: {}", responseBean);
        if (responseBean == null) {
            isTaskSuccess = false;
            return Integer.valueOf(RespondStatus.PROCESS_STATUS_ERROR.value());
        }
        String msg = CommonUtil.getRspTipInfo(responseBean);
        if (!Objects.equals(responseBean.getStatus(), RespondStatus.PROCESS_STATUS_NORMAL.value())) {
            msg = msg + I18NServer.toLocale("plugins_porting_faq_tips");
            IDENotificationUtil.notificationForHyperlink(new NotificationBean(title, msg, NotificationType.ERROR),
                op -> CommonUtil.openURI(I18NServer.toLocale("plugins_porting_rebuild_failed_faq_url")));
            return THRESHOLD;
        }

        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (progressData.get(PROGRESS) instanceof Integer) {
            int progress = (Integer) progressData.get(PROGRESS);
            indicator.setText2(msg);
            if (progress == THRESHOLD) {
                doEventWhenProgressIsOK(taskId, responseBean, progressData);
            }
            return progress;
        }
        return Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value());
    }

    /**
     * 当进度条是100%时处理
     *
     * @param taskId       taskId
     * @param responseBean responseBean
     * @param progressData progressData
     */
    private void doEventWhenProgressIsOK(String taskId, ResponseBean responseBean, Map<String, Object> progressData) {
        String emptyMessage = CommonUtil.getRspTipInfo(responseBean);

        Object statusObj = progressData.get("status");
        int status = 0;
        if (statusObj instanceof Integer) {
            status = (Integer) statusObj;
        }

        if (status == STATUS_TYPE_NO_NEED || !isHasReport(taskId)) {
            isNeedOpenReport = false;
        } else {
            isNeedOpenReport = true;
        }

        if (status != STATUS_TYPE_SUCCESS) {
            if (status == STATUS_TYPE_NO_NEED) {
                IDENotificationUtil.notificationCommon(new NotificationBean(title,
                        emptyMessage, NotificationType.INFORMATION));
            } else {
                emptyMessage = emptyMessage + I18NServer.toLocale("plugins_porting_faq_tips");
                IDENotificationUtil.notificationForHyperlink(
                    new NotificationBean(title, emptyMessage, NotificationType.ERROR),
                    data -> CommonUtil.openURI(I18NServer.toLocale("plugins_porting_rebuild_failed_faq_url")));
            }
            return;
        } else {
            emptyMessage = emptyMessage + IDEConstant.HTML_HEAD
                + I18NServer.toLocale("plugins_porting_webpack_success_file_download") + IDEConstant.HTML_FOOT;
        }

        IDENotificationUtil.notificationForHyperlink(
            new NotificationBean(title, emptyMessage, NotificationType.INFORMATION),
                data -> downloadFIle(responseBean, progressData, taskId));
    }

    private boolean isHasReport(String taskId) {
        // 获取报告详情信息
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/autopack/history/" + taskId + IDEConstant.PATH_SEPARATOR, HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null || !Objects.equals(response.getStatus(), "0")) {
            Logger.error("get report is fail!");
            return false;
        }
        return true;
    }

    private void downloadFIle(ResponseBean responseBean, Map<String, Object> progressData,
        String taskId) {
        Logger.info("Query Analysis task process, Response: {}", responseBean);
        String packageName = progressData.get("result").toString();
        PkgRebuildingHandler.downloadPackage(taskId, packageName);
    }

    /**
     * 取消 软件包重构任务。
     *
     * @param taskId task id
     */
    public void deleteAnalysisTask(String taskId) {
        String url = "/portadv/autopack/" + taskId + "/";
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            url, HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return;
        }
        Logger.info("delete porting Task, Response: {}", responseBean.toString());
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value()
            .equals(responseBean.getStatus()) ? NotificationType.INFORMATION : NotificationType.ERROR;
        IDENotificationUtil.notificationCommon(
            new NotificationBean(title, CommonUtil.getRspTipInfo(responseBean), notificationType));
    }
}

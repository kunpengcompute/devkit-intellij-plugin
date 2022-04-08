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
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.CMDFunction;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Setter;

/**
 * 字节对齐进度
 *
 * @since 2021/1/8
 */
public class ByteAlignmentProcess extends IDEBaseTask {
    private static final String PROGRESS = "progress";

    private static final String STATUS = "status";

    private static final String TEXT = I18NServer.toLocale("plugins_porting_enhance_function_byte_align_processing");

    private static final int BUTTON_NUM = 2;

    private static final int SLEEP_TIMES = 200;

    private static final int MAX_PROGRESS = 100;

    private static final int STATUS_2 = 2;

    private static final int STATUS_3 = 3;

    /**
     * 字节对齐进度回调id
     * 为防止用户将原页面关闭，新打开webview找不到原页面回调id对应的回调函数，
     * 在有任务进度的情况下更新此id，以新打开页面的回调函数完成回调
     */
    @Setter
    private static String byteAlignmentCallBackId = "";

    private final String taskType;

    private final String taskId;

    private String status;

    /**
     * ByteAlignmentProcess
     *
     * @param taskId taskId
     * @param cbid   cbid
     * @param status status
     */
    public ByteAlignmentProcess(String taskId, String cbid, String status) {
        this.taskType = TaskType.BYTE_ALIGN.value();
        this.taskId = taskId;
        this.status = status;
        ByteAlignmentProcess.byteAlignmentCallBackId = cbid;
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
        NoFractionWaitingProcess.createTaskLackWorkerNotification(status, TEXT);
        try {
            while (processCurrentValue < MAX_PROGRESS) {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMES);
                if (indicator.isCanceled()) {
                    break;
                }
                indicator.checkCanceled();
                indicator.setFraction(
                    new BigDecimal(processCurrentValue).divide(new BigDecimal(MAX_PROGRESS)).doubleValue());
                indicator.setText(TEXT + "···: " +
                    new BigDecimal(Double.valueOf(processCurrentValue)).intValue() + "%");

                processCurrentValue = queryProgress();
            }
        } catch (InterruptedException e) {
            Logger.error("Get byte alignment progress failed. exception : InterruptedException");
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
            new MessageDialogBean(I18NServer.toLocale("plugins_porting_byte_align_cancel_alert"),
                "", buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            deleteTask(taskId, taskType, byteAlignmentCallBackId);
        } else {
            // 取消删除
            ByteAlignmentProcess process = new ByteAlignmentProcess(taskId, byteAlignmentCallBackId, status);
            process.processForCommon(null, TEXT + "······", process);
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
        Logger.info("Query byte alignment task progress.");
        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (progressData.get(STATUS) instanceof Integer) {
            int statusTemp = (Integer) progressData.get(STATUS);
            // statusTemp = 2表示成功
            if (statusTemp == STATUS_2 || statusTemp == STATUS_3) {
                CommonHandler.invokeCallback(CMDFunction.GET_DATA.functionName(), byteAlignmentCallBackId,
                        responseBean.getResponseJsonStr());
            }
            return Integer.parseInt(progressData.get(PROGRESS).toString());
        }
        return MAX_PROGRESS;
    }

    /**
     * 取消 Task
     *
     * @param taskId   taskId
     * @param taskType taskType
     * @param cbid     cbid
     */
    public static void deleteTask(String taskId, String taskType, String cbid) {
        String url = "/portadv/tasks/migration/bytealignment/task/" + taskId + "/";
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return;
        }
        Logger.info("delete byte alignment task.");
        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;

        IDENotificationUtil.notificationCommon(
            new NotificationBean("", CommonUtil.getRspTipInfo(responseBean), notificationType));
        PortingIDETask.deleteRunTask(taskType);
        CommonHandler.invokeCallback(CMDFunction.GET_DATA.functionName(), cbid, responseBean.getResponseJsonStr());
    }
}

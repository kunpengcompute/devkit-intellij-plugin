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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_OPERATE_CANCEL;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.ProjectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

/**
 * 无百分比等待中进度条显示类
 *
 * @since 2.3.1
 */
public class NoFractionWaitingProcess extends Task.ConditionalModal {
    /**
     * 1~3 worker。状态码
     */
    public static final String CREATE_TASK_LACK_WORKER = "0x010a01";

    /**
     * 失败用户提示。状态码
     */
    public static final String CREATE_TASK_NO_WORKER = "0x010a10";

    /**
     * 等待中。状态码（创建任务，调用接口不一样）
     */
    public static final String PROGRESS_WAITE_WORKER = "0x010a00";

    /**
     * 超时30分钟
     */
    private static final String PROGRESS_WAITE_WORKER_TIMEOUT = "0x010a11";

    /**
     * 睡眠时间
     */
    private static final int SLEEP_TIMES = 500;

    private static final int BUTTON_NUM = 2;

    private String title;

    private String taskType;

    private String taskId;

    private String callBackId;

    private String status;

    private String timeOutMsg;

    /**
     * 构造函数
     *
     * @param title      title
     * @param taskType   taskType
     * @param taskId     taskId
     * @param callBackId callBackId
     * @param status     status
     */
    public NoFractionWaitingProcess(String title, String taskType, String taskId, String callBackId, String status) {
        super(ProjectManager.getInstance().getDefaultProject(), title, true,
            PerformInBackgroundOption.ALWAYS_BACKGROUND);
        this.title = title;
        this.taskType = taskType;
        this.taskId = taskId;
        this.callBackId = callBackId;
        this.status = status;
        setCancelText(TERM_OPERATE_CANCEL);
        setCancelTooltipText(TERM_OPERATE_CANCEL);
    }

    /**
     * 任务取消后的动作
     */
    @Override
    public void onCancel() {
        super.onCancel();
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
            doCancel();
            CommonUtil.setBackGroundProcessWindowOpen(false, CommonUtil.getDefaultWindow());
        } else {
            // 取消删除 继续任务
            NoFractionWaitingProcess process = new NoFractionWaitingProcess(title, taskType,
                taskId, callBackId, status);
            final ProgressManager progress = ProgressManager.getInstance();
            progress.run(process);
        }
    }

    /**
     * 任务执行成功后的动作
     */
    @Override
    public void onSuccess() {
        super.onSuccess();
        CommonUtil.setBackGroundProcessWindowOpen(false, CommonUtil.getDefaultWindow());
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        super.onThrowable(error);
        PortingIDETask.deleteRunTask(taskType);
    }

    @Override
    public void onFinished() {
        super.onFinished();
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        createTaskLackWorkerNotification(status, title);
        while (true) {
            if (indicator.isCanceled()) {
                break;
            }
            indicator.checkCanceled();
            indicator.setText(I18NServer.toLocale("common_term_users_create_task_waiting_tips"));
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMES);
            } catch (InterruptedException e) {
                Logger.error("Get analysis process failed. exception : InterruptedException");
            }
            Status resultStatus = doTask();
            if (resultStatus == Status.CANCEL) { // 后端取消
                Logger.info("time out for canceled task");
                IDENotificationUtil.notificationCommon(new NotificationBean("", timeOutMsg,
                    NotificationType.INFORMATION));
                break;
            }
            if (resultStatus == Status.OK) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    doOkEvent();
                });
                break;
            }
        }
    }

    /**
     * 1~3 worker通知提示
     *
     * @param status 任务状态码
     * @param title  启动任务的title
     */
    public static void createTaskLackWorkerNotification(String status, String title) {
        if (Objects.equals(status, CREATE_TASK_LACK_WORKER)) {
            IDENotificationUtil.notificationForHyperlink(new NotificationBean(
                    title,
                    I18NServer.toLocale("common_term_users_create_task_lack_worker_tips"),
                    NotificationType.INFORMATION),
                data -> CommonUtil.openURI(CommonHandler.USER_GUIDE_PATH));
        }
    }

    private void doOkEvent() {
        if (UserInfoContext.getInstance().getUserName() == null) {
            return;
        }
        switch (taskType) {
            case "0":
                // 轮询任务执行进度并更新进度条，若查询到已完成则刷新左侧树+打开报告
                final AnalysisProcess analysisProcess = new AnalysisProcess(taskType, taskId, callBackId, status);
                analysisProcess.processForCommon(CommonUtil.getDefaultProject(),
                    I18NServer.toLocale("plugins_porting_tip_analysis_title"), analysisProcess);
                break;

            case "6":
                final ByteAlignmentProcess byteAlignmentProcess = new ByteAlignmentProcess(taskId, callBackId, status);
                byteAlignmentProcess.processForCommon(null,
                    I18NServer.toLocale("plugins_porting_enhance_function_byte_align_processing")
                        + "······", byteAlignmentProcess);
                break;

            case "10":
                final WeakConsistencyProcess weakConsistencyProcess = new WeakConsistencyProcess(
                    TaskType.WEAK_CHECK.value(), taskId, callBackId, status);
                weakConsistencyProcess.processForCommon(null,
                    I18NServer.toLocale("plugins_porting_weak_check_progress_label"),
                    weakConsistencyProcess);
                break;

            case "5":
                final PreCheckProcess taskProcess = new PreCheckProcess(taskId, callBackId, status);
                final ProgressManager progress = ProgressManager.getInstance();
                progress.run(taskProcess);
                break;

            case "9":
                final WeakConsistencyProcess process = new WeakConsistencyProcess(TaskType.WEAK_COMPILE.value(),
                    taskId, callBackId, status);
                process.processForCommon(null,
                    I18NServer.toLocale("plugins_porting_weak_check_compile_file") + "······", process);
                break;

            default:
                PortingIDETask.deleteRunTask(taskType);
                break;
        }
    }

    /**
     * 具体做任务方法
     *
     * @return 状态
     */
    protected Status doTask() {
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.GET.vaLue(), "");
        data.setNeedProcess(true);
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return Status.CANCEL;
        }
        Logger.info("Query porting task process, Response: {}", responseBean.toString());
        if (Objects.equals(responseBean.getRealStatus(), PROGRESS_WAITE_WORKER)) {
            return Status.CONTINUE;
        }
        if (Objects.equals(responseBean.getRealStatus(), PROGRESS_WAITE_WORKER_TIMEOUT)) {
            timeOutMsg = CommonUtil.getRspTipInfo(responseBean);
            return Status.CANCEL;
        }
        return Status.OK;
    }

    /**
     * 取消方法
     */
    protected void doCancel() {
        switch (taskType) {
            case "0":
                AnalysisProcess.deletePortingTask(taskId, taskType, callBackId);
                break;

            case "6":
                ByteAlignmentProcess.deleteTask(taskId, taskType, callBackId);
                break;

            case "10":
            case "9":
                WeakConsistencyProcess.deleteTask(taskId, taskType, callBackId);
                break;

            case "5":
                PreCheckProcess.deleteTask(taskId, taskType, callBackId);
                break;

            default:
                PortingIDETask.deleteRunTask(taskType);
                break;
        }
    }

    /**
     * 状态枚举
     */
    enum Status {
        OK,
        CANCEL,
        CONTINUE;
    }
}

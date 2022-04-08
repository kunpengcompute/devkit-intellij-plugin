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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.ImpAndExpTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.http.SysperfProjectServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

/**
 * Intellij 导出任务弹框
 *
 * @since 2020-09-25
 */
public class ExportTaskWrapDialog extends IdeaDialog {
    /**
     * 请求成功返回
     */
    public static final String SUCCESS_CODE = "SysPerf.Success";

    private ImpAndExpTaskAction impAndExpTaskAction;

    private boolean finshed = false;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public ExportTaskWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title =
                StringUtil.stringIsEmpty(title) ? CommonI18NServer.toLocale("plugins_hyper_tuner_login_logOut") : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.LOGIN.dialogName() : dialogName;
        mainPanel = panel;

        // 设置弹框大小是否可变
        this.resizable = resizable;

        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName(SysperfContent.EXPORT_TASK_TIP_CONFIRM, SysperfContent.EXPORT_TASK_TIP_CANCEL);

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public ExportTaskWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        uploadProgress(CommonUtil.getDefaultProject());
    }

    private Timer initTimerTask(String exportTaskId, String projectName, String taskName) {
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ResponseBean response2 = SysperfProjectServer.exportTaskRar(exportTaskId);
                if (response2 == null || response2.getData() == null) {
                    timer.cancel();
                    finshed = true;
                    IDENotificationUtil.notificationCommon(new NotificationBean(
                            SysperfContent.EXPORT_TASK, SysperfContent.EXPORT_TASK_ERROR,
                            NotificationType.ERROR));
                    LeftTreeSysperfPanel.updateExportTaskButton(true);
                    return;
                }
                if (response2.getData().contains("\"complete_status\":\"success\"")) {
                    timer.cancel();
                    finshed = true;
                    completeStatusSuccess(projectName, taskName, exportTaskId);
                }
                if ("{}".equals(response2.getData())) {
                    timer.cancel();
                    finshed = true;
                    IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.EXPORT_TASK,
                            SysperfContent.EXPORT_TASK_ERROR, NotificationType.ERROR));
                    LeftTreeSysperfPanel.updateExportTaskButton(true);
                }
            }
        };
        timer.schedule(task, 0, 1000);
        return timer;
    }

    private void completeStatusSuccess(String projectName, String taskName, String exportTaskId) {
        Project project = CommonUtil.getDefaultProject();
        String content = MessageFormat.format(
                SysperfContent.EXPORT_TASK_SUCCESS_DOWNLOAD, projectName, taskName);
        NotificationBean notificationBean =
                new NotificationBean(
                        SysperfContent.EXPORT_TASK_SUCCESS, content, NotificationType.INFORMATION);
        notificationBean.setProject(project);
        ActionOperate actionOperate = new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                test(taskName, exportTaskId);
            }
        };
        IDENotificationUtil.notificationForHyperlink(notificationBean, actionOperate);
        LeftTreeSysperfPanel.updateExportTaskButton(true);
    }

    private void test(String taskName, String exportTaskId) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        // 弹窗标题
        descriptor.setTitle(I18NServer.toLocale("plugins_hyper_tuner_impAndExp_task_download_all"));
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, CommonUtil.getDefaultProject(), null);

        String path = virtualFile.getPath();
        String fileNameAndSuffix = taskName + ".tar";
        new ImpAndExpTaskAction().downloadSelectLog(path, fileNameAndSuffix, Integer.parseInt(exportTaskId), 1);
    }

    private void uploadProgress(Project project) {
        ProgressManager.getInstance().run(
                new Task.Backgroundable(project, SysperfContent.EXPORT_TASK, false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        indicator.setText2("");
                        indicator.setText(SysperfContent.EXPORT_TASK_BAR_PERPARING);
                        indicator.setFraction(0.0);
                        LeftTreeSysperfPanel.updateExportTaskButton(false);
                        String projectName = LeftTreeSysperfPanel.getSelectProject().getProjectName();
                        String taskName = LeftTreeSysperfPanel.getSelectTask().getTaskname();

                        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                                "sys-perf/api/v2.2/import_export_tasks/export/", HttpMethod.POST.vaLue(), "");
                        Map<String, String> map = new HashMap<>();
                        map.put("projectname", projectName);
                        taskName = taskName.replaceAll(" ", "-").replaceAll(":", "-");
                        map.put("taskname", taskName);
                        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));
                        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);

                        if (Objects.isNull(response)) {
                            IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.EXPORT_TASK,
                                    SysperfContent.EXPORT_TASK_ERROR, NotificationType.ERROR));
                        }
                        if (response.getCode().equals(SUCCESS_CODE)) {
                            indicator.setText(SysperfContent.EXPORT_TASK_BAR_EXPORTING);
                            indicator.setText2("");
                            indicator.setFraction(0.4);
                            String dataStr = response.getData();
                            JSONObject dataJSONObj = JsonUtil.getJsonObjectFromJsonStr(dataStr);
                            String exportTaskId = dataJSONObj.getInteger("id") + "";
                            initTimerTask(exportTaskId, projectName, taskName);
                            while (!finshed) {
                                indicator.setText(SysperfContent.EXPORT_TASK_BAR_EXPORTING);
                                indicator.setText2("");
                            }
                            indicator.setFraction(1.0);
                        } else {
                            IDENotificationUtil.notificationCommon(new NotificationBean(
                                    SysperfContent.EXPORT_TASK, response.getMessage(), NotificationType.ERROR));
                            LeftTreeSysperfPanel.updateExportTaskButton(true);
                        }
                    }
                });
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     * 创建面板内容
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}

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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskUpdatePanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.swing.JComponent;

/**
 * 预约任务修改弹窗
 *
 * @since 2012-10-12
 */
public class SchTaskUpdateDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public SchTaskUpdateDialog(String title, String dialogName, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName) ? SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE : dialogName;
        this.mainPanel = panel;
        setOKAndCancelName(SchTaskContent.SCH_TASK_OPERATE_OK, SchTaskContent.SCH_TASK_OPERATE_CANCEL);
        initDialog(); // 初始化弹框内容
    }

    /**
     * 添加弹框内容
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 点击确认操作，根据面板内容，执行相应操作
     */
    @Override
    protected void onOKAction() {
        // 当面板类型为更新面板时，发送请求，更新预约任务
        if (mainPanel instanceof SchTaskUpdatePanel) {
            SchTaskUpdatePanel panel = (SchTaskUpdatePanel) mainPanel;
            JSONObject paramJsonObj = panel.getNewSchTask();
            int id = panel.getCurrentSchTaskId();
            String url = "sys-perf/api/v2.2/schedule-tasks/" + id + "/";
            RequestDataBean message =
                    new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.PUT.vaLue(), "");
            String objStr = JsonUtil.getJsonStrFromJsonObj(paramJsonObj);
            message.setBodyData(objStr);
            ResponseBean rsp = TuningHttpsServer.INSTANCE.requestData(message);
            if (rsp == null) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                                title, SchTaskContent.SCH_TASK_DIALOG_UPDATE_FAIL, NotificationType.ERROR));
                return;
            }
            String title = SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE;
            if ("SysPerf.Success".equals(rsp.getCode())) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                                title, SchTaskContent.SCH_TASK_DIALOG_UPDATE_SUCCESS, NotificationType.INFORMATION));
            } else {
                String errMessage = rsp.getMessage();
                errMessage =
                        errMessage
                                .replaceAll("statistical", TaskManageContent.PARAM_STATISTICAL)
                                .replaceAll("duration", TaskManageContent.PARAM_DURATION)
                                .replaceAll("collect_file_size", TaskManageContent.PARAM_FILE_SIZE);
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                                title,
                                SchTaskContent.SCH_TASK_DIALOG_UPDATE_FAIL + " " + errMessage,
                                NotificationType.ERROR));
            }
        }
    }

    /**
     * 确认删除
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    protected ValidationInfo doValidate() {
        return this.mainPanel.doValidate();
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected List<ValidationInfo> doValidateAll() {
        return this.mainPanel.doValidateAll();
    }
}

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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.WebViewUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.notification.NotificationType;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javax.swing.JComponent;

/**
 * Intellij 删除项目弹框
 *
 * @since 2020-09-25
 */
public class DeleteProjectWrapDialog extends IdeaDialog {
    /**
     * 请求成功返回
     */
    public static final String SUCCESS_CODE = "SysPerf.Success";

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param basePanel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public DeleteProjectWrapDialog(String title, String dialogName, IDEBasePanel basePanel, boolean resizable) {
        this.title =
                StringUtil.stringIsEmpty(title) ? CommonI18NServer.toLocale("plugins_hyper_tuner_login_logOut") : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.LOGIN.dialogName() : dialogName;
        mainPanel = basePanel;

        // 设置弹框大小是否可变
        this.resizable = resizable;

        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName(
                TuningI18NServer.toLocale("plugins_common_button_confirm"),
                TuningI18NServer.toLocale("plugins_common_button_cancel"));

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public DeleteProjectWrapDialog(String title, IDEBasePanel panel) {
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
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        int projectId = LeftTreeSysperfPanel.getSelectProject().getProjectId();
        RequestDataBean request =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/projects/" + projectId + "/",
                        HttpMethod.DELETE.vaLue(),
                        "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        if (Objects.isNull(response)) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            SysperfContent.DELETE_PROJECT,
                            SysperfContent.DELETE_PROJECT_ERROR,
                            NotificationType.ERROR));
            return;
        }

        if (response.getCode().equals(SUCCESS_CODE)) {
            WebViewUtil.closePage(
                    LeftTreeSysperfPanel.getSelectProject().getProjectName()
                            + new StringBuilder().append(".").append(TuningIDEConstant.TUNING_KPHT).toString());
            // 刷新左侧树
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            SysperfContent.DELETE_PROJECT,
                            SysperfContent.DELETE_PROJECT_SUCCESS,
                            NotificationType.INFORMATION));
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            SysperfContent.DELETE_PROJECT,
                            SysperfContent.DELETE_PROJECT_ERROR,
                            NotificationType.ERROR));
        }
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

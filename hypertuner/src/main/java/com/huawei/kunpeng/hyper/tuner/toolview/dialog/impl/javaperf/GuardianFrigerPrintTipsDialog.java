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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.setting.javaperf.guardianmanager.GuardianMangerConfigurable;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import javax.swing.JComponent;

/**
 * 安装账户判断提示的弹窗
 *
 * @since 2020-12-21
 */
public class GuardianFrigerPrintTipsDialog extends IdeaDialog {
    private Map<String, String> params;

    private String host;

    private String sshPort;

    private String sshUsername;

    private String sshPassword;

    // 指纹表格数据
    private String fingerPrint;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public GuardianFrigerPrintTipsDialog(
            String title, IDEBasePanel panel, Map<String, String> params, String fingerPrint) {
        this.title = title;
        this.dialogName = title;
        this.mainPanel = panel;
        this.params = params;
        this.host = params.get("host");
        this.sshPort = params.get("port");
        this.sshUsername = params.get("username");
        this.sshPassword = params.get("password");

        this.fingerPrint = fingerPrint;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
        ProgressManager.getInstance()
                .run(
                        new Task.Backgroundable(
                                CommonUtil.getDefaultProject(),
                                GuardianMangerConstant.GUARDIAN_ADD_TITLE,
                                false,
                                PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                            @Override
                            public void run(@NotNull ProgressIndicator indicator) {
                                sendMessage();
                            }
                        });
    }

    /**
     * 处理添加目标环境返回
     */
    private void sendMessage() {
        JSONObject obj = new JSONObject();
        obj.put("fingerprint", fingerPrint);
        obj.put("host", host);
        obj.put("sshPort", sshPort);
        obj.put("sshPassword", sshPassword);
        obj.put("sshUsername", sshUsername);
        String title = GuardianMangerConstant.GUARDIAN_ADD_TITLE;

        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/api/guardians", HttpMethod.POST.vaLue(), "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseData = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return;
        }
        // 返回code为null则添加失败，请求成功直接刷新表格
        if (responseData.getCode() != null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(title, responseData.getMessage(), NotificationType.ERROR));
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            title,
                            TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_success"),
                            NotificationType.INFORMATION));
            ApplicationManager.getApplication()
                    .invokeLater(
                            () -> {
                                // 登出
                                GuardianMangerConfigurable.getGuardianSettingsComponent().updateGuardianTable();
                            });
        }
    }

    /**
     * onCancelAction 取消安装
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel AccountTipsDialog.");
    }
}

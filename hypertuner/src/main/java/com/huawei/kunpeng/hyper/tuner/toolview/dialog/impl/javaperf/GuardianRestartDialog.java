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

import com.alibaba.fastjson.JSONObject;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.setting.javaperf.guardianmanager.GuardianMangerConfigurable;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianRestartPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.exception.IDEException;
import com.huawei.kunpeng.intellij.common.http.HttpAPIServiceTrust;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.CommonDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * 重启目标环境
 *
 * @since 2021-07-17
 */
public class GuardianRestartDialog extends CommonDialog {
    private GuardianRestartPanel guardianRestartPanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public GuardianRestartDialog(String title, String dialogName, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? GuardianMangerConstant.GUARDIAN_RESTART_TITLE : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName) ? GuardianMangerConstant.GUARDIAN_RESTART_TITLE : dialogName;
        this.mainPanel = panel;
        if (panel instanceof GuardianRestartPanel) {
            guardianRestartPanel = (GuardianRestartPanel) panel;
        }
        // 设置弹框大小是否可变
        this.resizable = resizable;
        // 设置弹框中保存取消按钮的名称
        setButtonName();
        // 无位置信息时居中显示
        this.rectangle = rectangle;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 添加弱口令弹框内容
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认修改节点
     */
    @Override
    protected void onOKAction() {
        Map<String, String> paramMap = this.guardianRestartPanel.getParams();
        String id = paramMap.get("id");
        String username = paramMap.get("username");
        String password = paramMap.get("password");
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/api/guardians/" + id, "PATCH", "");
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, message.getModule());
        JSONObject jsonObj = null;
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue())).map(Object::toString).orElse(null);
            String port =
                    Optional.ofNullable(context.get(BaseCacheVal.PORT.vaLue())).map(Object::toString).orElse(null);
            if (ValidateUtils.isEmptyString(ip) || ValidateUtils.isEmptyString(port)) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean("", I18NServer.toLocale("plugins_common_message_configServer"),
                                NotificationType.WARNING));
                throw new IDEException();
            }
            // 组装完整的url
            String url = IDEConstant.URL_PREFIX + ip + ":" + port + context.get(BaseCacheVal.BASE_URL.vaLue())
                    + message.getUrl();
            JSONObject obj = new JSONObject();
            String token =
                    Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue())).map(Object::toString).orElse(null);
            obj.put("sshUsername", username);
            obj.put("sshPassword", password);
            try {
                String response = HttpAPIServiceTrust.getResponseString(url, obj, "PATCH", token, "TUNING");
                if (response != null) {
                    jsonObj = JSONObject.parseObject(response);
                } else {
                    Logger.info("response is null");
                }
            } catch (IOException e) {
                Logger.error("Patch request exception：message is {}", e.getMessage());
            }
        }
        sendMessage(jsonObj);
        if (JavaPerfToolWindowPanel.isNeedUpdate) {
            ApplicationManager.getApplication().invokeLater(() -> {
                GuardianMangerConfigurable.getGuardianSettingsComponent().updateGuardianTable();
            });
        }
    }

    private void sendMessage(JSONObject jsonObj) {
        if (jsonObj == null) {
            return;
        }
        if (jsonObj.get("state") != null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            GuardianMangerConstant.GUARDIAN_RESTART_TITLE,
                            TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_restart_success"),
                            NotificationType.INFORMATION));
        } else {
            if (jsonObj.get("msg") != null) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                                GuardianMangerConstant.GUARDIAN_RESTART_TITLE,
                                String.valueOf(jsonObj.get("msg")),
                                NotificationType.ERROR));
            } else {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                                GuardianMangerConstant.GUARDIAN_RESTART_TITLE,
                                String.valueOf(jsonObj.get("message")),
                                NotificationType.ERROR));
            }
        }
    }
}
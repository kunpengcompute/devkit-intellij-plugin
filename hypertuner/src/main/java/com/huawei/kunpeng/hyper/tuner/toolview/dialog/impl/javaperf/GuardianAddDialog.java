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

import com.huawei.kunpeng.hyper.tuner.action.javaperf.GuardianManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianAddPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianTipsPanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.CommonDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.intellij.notification.NotificationType;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 添加目标环境弹框
 *
 * @since 2021-7-12
 */
public class GuardianAddDialog extends CommonDialog {
    private GuardianAddPanel guardianAddPanel;

    private GuardianManagerAction guardianManagerAction;

    public GuardianAddDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public GuardianAddDialog(String title, String dialogName, IDEBasePanel panel, Boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? GuardianMangerConstant.GUARDIAN_ADD_TITLE : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? GuardianMangerConstant.GUARDIAN_ADD_TITLE : dialogName;
        this.mainPanel = panel;
        if (panel instanceof GuardianAddPanel) {
            guardianAddPanel = (GuardianAddPanel) panel;
        }
        if (guardianManagerAction == null) {
            guardianManagerAction = new GuardianManagerAction();
        }
        // 设置弹框大小是否可变
        this.resizable = resizable;
        setButtonName();
        // 无位置信息时居中显示
        setHelp(GuardianMangerConstant.GUARDIAN_ADD_TITLE_HELP, getHelpUrl());
        // 初始化弹框内容
        initDialog();
    }


    /**
     * 确认新增目标环境
     */
    @Override
    protected void onOKAction() {
        Map<String, String> paramMap = this.guardianAddPanel.getParams();
        ResponseBean rsp = guardianManagerAction.fetchFingerPrint(paramMap.get("host"),
                paramMap.get("port"), paramMap.get("username"));
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        if (jsonMessage.get("fingerprint") != null) {
            String fingerPrint = String.valueOf(jsonMessage.get("fingerprint"));
            String type = "add";
            String tipContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_frigerTips");
            tipContent = MessageFormat.format(tipContent, paramMap.get("username"), paramMap.get("host"), fingerPrint);
            IDEBasePanel panel = new GuardianTipsPanel(null, tipContent, type);
            GuardianFrigerPrintTipsDialog dialog =
                    new GuardianFrigerPrintTipsDialog(title, panel, guardianAddPanel.getParams(), fingerPrint);
            dialog.displayPanel();
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(title, rsp.getMessage(), NotificationType.ERROR));
        }
    }

    /**
     * 自定义帮助链接
     *
     * @return string 帮助链接
     */
    protected String getHelpUrl() {
        return TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_add_help_url");
    }
}

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

package com.huawei.kunpeng.porting.ui.panel.settings.webservercert;

import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.webservercert.ExportWebServerCertificatePanel;
import com.huawei.kunpeng.porting.action.setting.webcert.PortingWebServerCertificateAction;

import com.intellij.openapi.wm.ToolWindow;

/**
 * ExportWebServerCertificatePanel
 *
 * @since 2020-10-07
 */
public class PortingExportWebServerCertificatePanel extends ExportWebServerCertificatePanel {
    /**
     * 全参构造函数
     *
     * @param toolWindow  工具窗口
     * @param panelName   面板名称
     * @param displayName 展示名字
     * @param isLockable  是否锁定
     */
    public PortingExportWebServerCertificatePanel(
        ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化面板
        initPanel(mainPanel);

        // 初始化content实例
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public PortingExportWebServerCertificatePanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 注册组件事件
     */

    @Override
    protected void registerComponentAction() {
        if (this.action == null) {
            this.action = new PortingWebServerCertificateAction();
        }
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof PortingWebServerCertificateAction) {
            this.action = action;
        }
        registerComponentAction();
    }
}

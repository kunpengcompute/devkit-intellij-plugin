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

package com.huawei.kunpeng.intellij.ui.panel;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * 保存配置面板
 *
 * @since 2021-03-11
 */
public class SaveConfirmPanel extends IDEBasePanel {
    private JPanel mainPanel;

    private JLabel icon;

    private JLabel info;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param params      面板携带参数
     */
    public SaveConfirmPanel(ToolWindow toolWindow, String panelName, String displayName, Map params) {
        setToolWindow(toolWindow);
        this.panelName =   CommonI18NServer.toLocale("common_config_title");
        this.params = params;
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, panelName, false);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public SaveConfirmPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, null);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     * @param params     面板携带参数
     */
    public SaveConfirmPanel(ToolWindow toolWindow, Map params) {
        this(toolWindow, null, null, params);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        info.setText(CommonI18NServer.toLocale("common_config_saveConfirm_again"));
        panel.setPreferredSize(new Dimension(570, 60));
    }

    @Override
    protected void registerComponentAction() {
    }

    /**
     * setAction
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

}


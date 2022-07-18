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
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.util.ui.UIUtil;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 加载面板
 *
 * @since 2021-01-21
 */
public class LeftTreeLoadingPanel extends IDEBasePanel {
    /**
     * 保存当前的加载组件便于随时启动或停止
     */
    private static JBLoadingPanel currentLoadPanel;

    /**
     * 加载组件
     */
    public JBLoadingPanel loadingPanel = new JBLoadingPanel(new BorderLayout(), CommonUtil.getDefaultProject());

    private JPanel mainPanel  = new JPanel();

    private JLabel loadGif = new JLabel();

    private Project project;

    /**
     * 完整构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName 面板名称
     * @param project 项目
     */
    public LeftTreeLoadingPanel(ToolWindow toolWindow, String panelName, Project project, String loadingText) {
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LEFT_TREE_CONFIG.panelName() : panelName;
        initPanel();
        registerComponentAction();
        if (loadingText != null) {
            loadingPanel.setLoadingText(loadingText);
        }
        createContent(loadingPanel, null, false);
    }

    /**
     * 反射调用
     *
     * @param toolWindow 工具窗口
     * @param project 项目
     */
    public LeftTreeLoadingPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project, null);
    }

    /**
     * 反射调用可以设置加载文字
     *
     * @param toolWindow 工具窗口
     * @param param 参数
     */
    public LeftTreeLoadingPanel(ToolWindow toolWindow, Map param) {
        this(toolWindow, null, CommonUtil.getDefaultProject(), param.get("loadingText").toString());
    }

    /**
     * 开始加载
     */
    public static void startLoading() {
        if (currentLoadPanel != null) {
            currentLoadPanel.startLoading();
        }
    }

    /**
     * 停止加载
     */
    public static void stopLoading() {
        if (currentLoadPanel != null) {
            currentLoadPanel.stopLoading();
        }
    }

    private void initPanel() {
        currentLoadPanel = this.loadingPanel;
        loadingPanel.setLoadingText(CommonI18NServer.toLocale("plugins_ui_common_loading"));
        loadingPanel.setFont(new Font("huawei sans", Font.PLAIN, 14));
        // UI线程运行加载态
        UIUtil.invokeLaterIfNeeded(() -> {
            loadingPanel.startLoading();
        });
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

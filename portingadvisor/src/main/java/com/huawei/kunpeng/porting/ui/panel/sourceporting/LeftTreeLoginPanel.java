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

package com.huawei.kunpeng.porting.ui.panel.sourceporting;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.ButtonUtil;
import com.huawei.kunpeng.porting.action.serverconfig.PortingServerConfigAction;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.webview.pageeditor.CloudEnvApplicationProcessEditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 左侧树登录面板
 *
 * @since 2021-02-04
 */
public class LeftTreeLoginPanel extends IDEBasePanel {
    /**
     * 响应成功状态
     */
    private static final String SUCCESS = "0";

    private JPanel serverPanel;

    private JButton loginButton;

    private JLabel decLabel;

    private JPanel mainPanel;
    private JPanel couldEvnPanel;
    private JEditorPane couldEvnDecEditor;
    private JButton couldEvnApplButton;
    private JPanel loginPanel;
    private JPanel couldEvnDecPanel;

    private Project project;

    /**
     * 左侧树登录面板构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     * @param project    当前的项目
     */
    public LeftTreeLoginPanel(ToolWindow toolWindow, String panelName, Project project) {
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LEFT_TREE_LOGIN.panelName() : panelName;
        initPanel();
        registerComponentAction();
        createContent(mainPanel, null, false);
    }

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param project 当前的项目
     */
    public LeftTreeLoginPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
    }

    private void initPanel() {
        super.initPanel(mainPanel);
        String ip = PortingCommonUtil.readCurIpFromConfig();
        decLabel.setText(ip + " " + I18NServer.toLocale("plugins_porting_lefttree_server_connected"));
        loginButton.setText(I18NServer.toLocale("plugins_porting_lefttree_login_now"));
        ButtonUtil.setCommonButtonStyle(loginButton);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(102, 32));
        addEditorPanel(I18NServer.toLocale("plugins_port_remote_lab_desc"), decLabel,
                couldEvnDecPanel, couldEvnDecEditor);
        mainPanel.updateUI();
        ButtonUtil.setCommonButtonStyle(couldEvnApplButton);
        couldEvnApplButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        couldEvnApplButton.setPreferredSize(loginButton.getPreferredSize());
        couldEvnApplButton.setText(I18NServer.toLocale("plugins_port_free_trial"));
    }

    @Override
    protected void registerComponentAction() {
        loginButton.addActionListener(actionEvent -> mouseClickedDisplayPanel());
        couldEvnApplButton.addActionListener(actionEvent -> CloudEnvApplicationProcessEditor.openPage(
                I18NServer.toLocale("plugins_port_cloud_env_application_process_page_name")));
    }

    private void mouseClickedDisplayPanel() {
        PortingServerConfigAction.instance.notificationForHyperlinkAction();
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

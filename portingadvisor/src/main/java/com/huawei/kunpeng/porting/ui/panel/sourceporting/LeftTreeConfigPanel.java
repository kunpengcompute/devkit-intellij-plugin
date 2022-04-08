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
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.ButtonUtil;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingServerConfigWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.PortingServerConfigPanel;
import com.huawei.kunpeng.porting.webview.pageeditor.CloudEnvApplicationProcessEditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 左侧树配置面板
 *
 * @since 2021-02-04
 */
public class LeftTreeConfigPanel extends IDEBasePanel {
    private JLabel decLabel;

    private JButton configServerButton;

    private JPanel mainPanel;

    private JPanel serverPanel;

    // 滚动条面板
    private JScrollPane scrollPanel;
    private JEditorPane couldEvnDecEditor;
    private JButton couldEvnApplButton;
    private JPanel couldEvnPanel;
    private JPanel configPanel;
    private JPanel couldEvnDecPanel;
    private Project project;

    /**
     * 完整的构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     * @param project    当前的项目
     */
    public LeftTreeConfigPanel(ToolWindow toolWindow, String panelName, Project project) {
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LEFT_TREE_CONFIG.panelName() : panelName;
        initPanel();
        registerComponentAction();
        createContent(mainPanel, null, false);
    }

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param project    当前的项目
     */
    public LeftTreeConfigPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
    }

    private void initPanel() {
        super.initPanel(mainPanel);
        decLabel.setText(I18NServer.toLocale("plugins_porting_lefttree_server_not_connected"));
        configServerButton.setText(I18NServer.toLocale("plugins_porting_lefttree_server_config_now"));
        configServerButton.setPreferredSize(new Dimension(128, 32));
        ButtonUtil.setCommonButtonStyle(configServerButton);
        configServerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addEditorPanel(I18NServer.toLocale("plugins_port_remote_lab_desc"), decLabel,
            couldEvnDecPanel, couldEvnDecEditor);
        mainPanel.updateUI();
        ButtonUtil.setCommonButtonStyle(couldEvnApplButton);
        couldEvnApplButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        couldEvnApplButton.setPreferredSize(configServerButton.getPreferredSize());
        couldEvnApplButton.setText(I18NServer.toLocale("plugins_port_free_trial"));
    }

    @Override
    protected void registerComponentAction() {
        configServerButton.addActionListener(actionEvent -> {
            IDEBasePanel panel = new PortingServerConfigPanel(null);
            IDEBaseDialog dialog = new PortingServerConfigWrapDialog(PortingUserManageConstant.CONFIG_TITLE, panel);
            dialog.displayPanel();
        });

        couldEvnApplButton.addActionListener(actionEvent -> CloudEnvApplicationProcessEditor.openPage(
            I18NServer.toLocale("plugins_port_cloud_env_application_process_page_name")));
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

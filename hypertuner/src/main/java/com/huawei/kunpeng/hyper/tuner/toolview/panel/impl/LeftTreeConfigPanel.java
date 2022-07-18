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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningServerConfigWrapDialog;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 左侧树配置面板
 *
 * @since 2021-02-04
 */
public class LeftTreeConfigPanel extends IDEBasePanel {
    private static final long serialVersionUID = 772197307383362340L;

    private JLabel decLabel;

    private JLabel hyperLinkLabel;

    private JPanel mainPanel;

    // 滚动条面板
    private JScrollPane scrollPanel;

    private Project project;

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param project    当前的项目
     */
    public LeftTreeConfigPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
    }

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

    private void initPanel() {
        // 去除滚动条面板的边框
        scrollPanel.setBorder(null);
        decLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_not_connected"));
        hyperLinkLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_config_now"));
        hyperLinkLabel.setForeground(new Color(47, 101, 202));
        hyperLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void registerComponentAction() {
        MouseAdapter configMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                IDEBasePanel panel = new TuningServerConfigPanel(null);
                IDEBaseDialog dialog =
                        new TuningServerConfigWrapDialog(TuningUserManageConstant.CONFIG_TITLE, panel);
                dialog.displayPanel();
            }
        };
        hyperLinkLabel.addMouseListener(configMouseAdapter);
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

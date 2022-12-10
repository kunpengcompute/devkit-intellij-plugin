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

import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PanelType;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ConfigGuideEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.FreeTrialEditor;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.ButtonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * 左侧树配置服务器面板
 *
 * @since 2022-11-15
 */
public class TuningServerConfigPanel extends IDEBasePanel {
    private JLabel configLabel;

    private JLabel freeTrialLabel;

    private JPanel mainPanel;

    // 滚动条面板
    private JScrollPane scrollPanel;

    private JButton configServerButton;

    private JButton freeTrialButton;

    private JPanel contentPanel;


    private Project project;

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param project    当前的项目
     */
    public TuningServerConfigPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
    }

    /**
     * 完整的构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     * @param project    当前的项目
     */
    public TuningServerConfigPanel(ToolWindow toolWindow, String panelName, Project project) {
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? PanelType.TUNING_SERVER_CONFIG.panelName() : panelName;
        initPanel();
        registerComponentAction();
        createContent(mainPanel, null, false);
    }

    private void initPanel() {
        // 去除滚动条面板的边框
        scrollPanel.setBorder(null);
        configLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_not_connected"));
        freeTrialLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_free_trial_text"));
        configServerButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_config_now"));
        configServerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonUtil.setCommonButtonStyle(configServerButton);
        freeTrialButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_free_trial_button"));
        freeTrialButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonUtil.setCommonButtonStyle(freeTrialButton);
    }

    @Override
    protected void registerComponentAction() {
        MouseAdapter configMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 未配置服务器时点击配置按钮打开配置指引页面
                ConfigGuideEditor.openPage();
            }
        };
        MouseAdapter freeTrialMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                FreeTrialEditor.openPage();
            }
        };
        configServerButton.addMouseListener(configMouseAdapter);
        freeTrialButton.addMouseListener(freeTrialMouseAdapter);

    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

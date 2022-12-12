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
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ConfigureServerEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.FreeTrialEditor;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.ButtonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class TuningConnectFailPanel extends IDEBasePanel {
    // 滚动条面板
    private JScrollPane scrollPanel;

    private JPanel mainPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel ipInfoLabel;
    private JLabel portInfoLabel;
    private JLabel connectFailLabel;
    private JButton configServerButton;
    private JLabel freeTrialLabel;
    private JButton freeTrialButton;
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JLabel statusInfoLabel;
    private JLabel failLabel;
    private JLabel failInfoLabel;

    private Project project;

    // 已配置服务器的ip和端口
    private String ip;
    private String port;

    private String failInfo;

    /**
     * 左侧树登录面板构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     * @param project    当前的项目
     */
    public TuningConnectFailPanel(ToolWindow toolWindow, String panelName, Project project, String failInfo) {
        this.failInfo = failInfo;
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? PanelType.TUNING_CONNECT_FAIL.panelName() : panelName;
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
    public TuningConnectFailPanel(ToolWindow toolWindow, Project project, String failInfo) {
        this(toolWindow, null, project, failInfo);
    }

    private void initPanel() {
        // 取消滚动条面板的边框
        scrollPanel.setBorder(null);
        Map<String, String> serverConfig = CommonUtil.readCurIpAndPortFromConfig();
        ip = serverConfig.get("ip");
        port = serverConfig.get("port");
        ipInfoLabel.setText(ip);
        portInfoLabel.setText(port);
        ipLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_ip_address"));
        portLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_port"));
        statusLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_status"));
        statusInfoLabel.setIcon(BaseIntellijIcons.load(IDEConstant.RED_POINT_PATH));
        statusInfoLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_fail_status"));
        failLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_fail_info"));
        failInfoLabel.setText(this.failInfo);
        connectFailLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_connect_server_fail"));
        configServerButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_config_now"));
        configServerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonUtil.setCommonButtonStyle(configServerButton);
        freeTrialLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_free_trial_text"));
        freeTrialButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_free_trial_button"));
        freeTrialButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonUtil.setCommonButtonStyle(freeTrialButton);
    }

    @Override
    protected void registerComponentAction() {
        MouseAdapter configMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ConfigureServerEditor.openPage();
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

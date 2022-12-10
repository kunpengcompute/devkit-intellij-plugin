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
import com.huawei.kunpeng.hyper.tuner.common.utils.NginxUtil;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.FreeTrialEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.IDELoginEditor;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.huawei.kunpeng.intellij.ui.utils.ButtonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.*;

/**
 * 左侧树配置服务器成功面板
 *
 * @since 2022-11-15
 */
public class TuningConfigSuccessPanel extends IDEBasePanel {
    // 滚动条面板
    private JScrollPane scrollPanel;

    private JPanel mainPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel ipInfoLabel;
    private JLabel portInfoLabel;
    private JLabel notLoginLabel;
    private JButton loginButton;
    private JLabel freeTrialLabel;
    private JButton freeTrialButton;
    private JPanel contentPanel;

    private Project project;

    // 已配置服务器的ip和端口
    private String ip;
    private String port;

    /**
     * 左侧树登录面板构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     * @param project    当前的项目
     */
    public TuningConfigSuccessPanel(ToolWindow toolWindow, String panelName, Project project) {
        this.project = project;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? PanelType.TUNING_CONFIG_SUCCESS.panelName() : panelName;
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
    public TuningConfigSuccessPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
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
        notLoginLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_notlogin_text"));
        loginButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_login_button"));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonUtil.setCommonButtonStyle(loginButton);
        freeTrialLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_free_trial_text"));
        freeTrialButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_free_trial_button"));
        freeTrialButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonUtil.setCommonButtonStyle(freeTrialButton);
    }

    @Override
    protected void registerComponentAction() {
        MouseAdapter loginMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (IDELoginEditor.isOpened()) {
                    System.out.println("login page is opened");
                    IDELoginEditor.openPage();
                    return;
                }
                String localPort = NginxUtil.getLocalPort();
                NginxUtil.updateNginxConfig(ip, port, localPort);
                IDELoginEditor.openPage(localPort);
            }
        };
        MouseAdapter freeTrialMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                FreeTrialEditor.openPage();
            }
        };
        loginButton.addMouseListener(loginMouseAdapter);
        freeTrialButton.addMouseListener(freeTrialMouseAdapter);

    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

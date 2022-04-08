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

package com.huawei.kunpeng.porting.ui.panel;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.LoginWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.toolwindow.SettingActionEnum;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingServerConfigWrapDialog;

import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.FormBuilder;

import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Porting Advisor设置顶层节点面板
 *
 * @since 2020-10-07
 */
public class PortingSettingsPanel extends IDEBasePanel {
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JLabel portingSettingsDesc;
    private LinkLabel linkLabel;

    /**
     * 构造函数
     */
    public PortingSettingsPanel() {
        // 初始化面板
        initPanel(new JPanel());

        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        int value = PortingIDEContext.getPortingIDEPluginStatus().value();
        if (value < IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            // 远程IP未配置时处理
            todoWhenNotConfigIP();
        } else if (value < IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            // 还未登录时处理
            todoWhenNotLogin();
        } else {
            // 用户已登录当前显示状态
            todoAfterLogin(panel);
        }
    }

    /**
     * 登录之后操作
     *
     * @param panel panel
     */
    private void todoAfterLogin(JPanel panel) {
        boolean isAdminUser = Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole());
        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder();
        mainFormBuilder.addComponent(new JLabel(PortingUserInfoContext.getInstance().getUserName() + " - "
            + I18NServer.toLocale("plugins_common_porting_settings_portingSettingsDesc")));
        for (SettingActionEnum action : SettingActionEnum.values()) {
            if ((isAdminUser && action.isAdminUserNeed())
                || (!isAdminUser && action.isComUserNeed())) {
                addLinkLabel(action, mainFormBuilder);
            }
        }
        mainFormBuilder.addComponentFillVertically(panel, 0);
        this.mainPanel = mainFormBuilder.getPanel();
        this.mainPanel.updateUI();
    }

    /**
     * 未配置IP时显示
     */
    private void todoWhenNotConfigIP() {
        portingSettingsDesc.setText(I18NServer.toLocale("plugins_porting_lefttree_server_not_connected"));
        linkLabel.setText(I18NServer.toLocale("plugins_porting_lefttree_server_config_now"));
        linkLabel.setIcon(null);
        linkLabel.setListener((linkLabel, o) -> {
            LoginWrapDialog.closeIntellijSettingsDialog();
            ApplicationManager.getApplication().invokeLater(() -> {
                IDEBaseDialog dialog = new PortingServerConfigWrapDialog(PortingUserManageConstant.CONFIG_TITLE,
                    new PortingServerConfigPanel(null));
                dialog.displayPanel();
            });
        }, null);
    }

    /**
     * 配置IP后未登录显示。
     */
    private void todoWhenNotLogin() {
        portingSettingsDesc.setText(I18NServer.toLocale("plugins_porting_title_not_login_desc"));
        linkLabel.setText(I18NServer.toLocale("plugins_porting_title_log_in"));
        linkLabel.setIcon(null);
        linkLabel.setListener((linkLabel, o) -> {
            LoginWrapDialog.closeIntellijSettingsDialog();
            ApplicationManager.getApplication().invokeLater(LoginUtils::gotoLogin);
        }, null);
    }

    /**
     * 添加链接
     *
     * @param action     action
     * @param linksPanel linksPanel
     */
    private void addLinkLabel(SettingActionEnum action, FormBuilder linksPanel) {
        String name = action.getName();
        if (action.getUpdateInterface() != null) {
            name = action.getUpdateInterface().updateName();
        }
        LinkLabel configLinkLabel = new LinkLabel(name, null, (aSource, aLinkData) -> {
            Settings settings = Settings.KEY.getData(DataManager.getInstance().getDataContext(aSource));
            if (settings == null) {
                return;
            }
            Configurable foundConfigurable = settings.find(action.getId());
            if (foundConfigurable == null) {
                return;
            }
            settings.select(foundConfigurable);
        });
        linksPanel.addComponent(configLinkLabel);
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        registerComponentAction();
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }
}

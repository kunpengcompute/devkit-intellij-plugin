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

import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningLoginWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningServerConfigWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings.SettingActionEnum;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;

import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Porting Advisor设置顶层节点面板
 *
 * @since 2020-10-07
 */
public class TuntingSettingsPanel extends SettingCommonConfigPanel {
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JLabel tuningSettingsDesc;
    private LinkLabel linkLabel;

    /**
     * 构造函数
     */
    public TuntingSettingsPanel() {
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
        int value = TuningIDEContext.getTuningIDEPluginStatus().value();
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
        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder();
        mainFormBuilder.addComponent(
            new JLabel(
                UserInfoContext.getInstance().getUserName()
                    + " - "
                    + TuningI18NServer.toLocale("plugins_common_hyper_tuner_settings_tuningSettingsDesc")));
        String role = UserInfoContext.getInstance().getRole();
        for (SettingActionEnum action : SettingActionEnum.values()) {
            if (action.shouldAddBy(role)) {
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
        tuningSettingsDesc.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_not_connected"));
        linkLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_server_config_now"));
        linkLabel.setIcon(null);
        linkLabel.setListener(
            (linkLabel, o) -> {
                TuningLoginWrapDialog.closeIntellijSettingsDialog();
                ApplicationManager.getApplication()
                    .invokeLater(
                        () -> {
                            IDEBaseDialog dialog =
                                new TuningServerConfigWrapDialog(
                                    TuningUserManageConstant.CONFIG_TITLE,
                                    new TuningServerConfigPanel(null));
                            dialog.displayPanel();
                        });
            },
            null);
    }

    /**
     * 配置IP后未登录显示。
     */
    private void todoWhenNotLogin() {
        tuningSettingsDesc.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_title_not_login_desc"));
        linkLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_title_log_in"));
        linkLabel.setIcon(null);
        linkLabel.setListener(
            (linkLabel, o) -> {
                TuningLoginWrapDialog.closeIntellijSettingsDialog();
                ApplicationManager.getApplication().invokeLater(TuningLoginUtils::gotoLogin);
            },
            null);
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
        LinkLabel configLinkLabel =
            new LinkLabel<>(
                name,
                null,
                (aSource, aLinkData) -> {
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

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}

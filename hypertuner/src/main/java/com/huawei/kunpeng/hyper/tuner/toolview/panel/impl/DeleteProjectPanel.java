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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.DeleteProjectWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Dimension;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 删除项目面板
 *
 * @since 2020-04-13
 */
public class DeleteProjectPanel extends IDEBasePanel {
    private JPanel mainPanel;
    private JLabel deleteTip;
    private Project project;
    private DeleteProjectWrapDialog dialog;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public DeleteProjectPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.DELETE_PROJECT.panelName() : panelName;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        String displayNameContent =
                StringUtil.stringIsEmpty(displayName)
                        ? CommonI18NServer.toLocale("plugins_hyper_tuner_login_logOut")
                        : displayName;
        createContent(mainPanel, displayNameContent, isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public DeleteProjectPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public DeleteProjectPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 设置界面
     *
     * @param dialog 界面
     */
    public void setDialog(DeleteProjectWrapDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    protected void initPanel(JPanel panel) {
        mainPanel.setPreferredSize(new Dimension(545, 56));
        String deleteTipContent = SysperfContent.DELETE_PROJECT_TITLE;
        deleteTipContent = MessageFormat.format(deleteTipContent,
                LeftTreeSysperfPanel.getSelectProject().getProjectName());
        deleteTip.setText(deleteTipContent);
        super.initPanel(mainPanel);
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

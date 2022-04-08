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
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.DeleteTaskWrapDialog;
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
 * 删除任务面板
 *
 * @since 2021-01-06
 */
public class DeleteTaskPanel extends IDEBasePanel {
    private JPanel mainPanel;
    private JLabel deleteTip;
    private Project project;
    private DeleteTaskWrapDialog dialog;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public DeleteTaskPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.DELETE_TASK.panelName() : panelName;

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
    public DeleteTaskPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public DeleteTaskPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 设置界面
     *
     * @param dialog 界面
     */
    public void setDialog(DeleteTaskWrapDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    protected void initPanel(JPanel panel) {
        mainPanel.setPreferredSize(new Dimension(545, 56));
        String tipContent = SysperfContent.DELETE_TASK_TIP;
        tipContent = MessageFormat.format(tipContent, LeftTreeSysperfPanel.getSelectTask().getTaskname());
        deleteTip.setText(tipContent);
        super.initPanel(mainPanel);
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

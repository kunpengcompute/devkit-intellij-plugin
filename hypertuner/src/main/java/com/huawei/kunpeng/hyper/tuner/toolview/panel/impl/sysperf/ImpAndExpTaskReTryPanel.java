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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.ImpAndExpTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 导入失败任务重试面板
 *
 * @since 2021-5-19
 */
public class ImpAndExpTaskReTryPanel extends IDEBasePanel {
    private static final long serialVersionUID = 5678649090321044594L;

    private static final String RUN_LOG_NAME = "/log.zip";

    private static final int BUTTON_LENGTH = 2;

    private String logFileName;
    private JLabel iconLabel;
    private JPanel mainPanel;

    private JLabel tipLabel;

    private JLabel projectNameL;
    private JLabel taskNameL;
    private JTextField projectNameText;
    private JTextField taskNameText;

    private ImpAndExpTaskAction impAndExpTaskAction;

    private String projectName;
    private String taskName;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param panelName   面板名称
     * @param displayName 面板显示title
     */
    public ImpAndExpTaskReTryPanel(String panelName, String displayName, String projectName, String taskName) {
        setToolWindow(toolWindow);
        this.panelName = ImpAndExpTaskContent.RETRY_ITEM;
        this.projectName = projectName;
        this.taskName = taskName;
        initPanel(mainPanel); // 初始化面板
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(mainPanel);

        // 设置提示
        tipLabel.setText(ImpAndExpTaskContent.RETRY_ITEM_CONTENT);
        tipLabel.setPreferredSize(new Dimension(545, 30));
        projectNameL.setText(TaskManageContent.PARAM_PROJECT_NAME);
        projectNameText.setText(projectName);
        taskNameL.setText(TaskManageContent.TASK_NAME);
        taskNameText.setText(taskName);
        if (impAndExpTaskAction == null) {
            impAndExpTaskAction = new ImpAndExpTaskAction();
        }
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 获取新的工程名称
     *
     * @return String 工程名称
     */
    public String getNewProjectName() {
        return projectNameText.getText();
    }

    /**
     * 获取新的任务名称
     *
     * @return String 任务名称
     */
    public String getNewTaskName() {
        return taskNameText.getText();
    }
}

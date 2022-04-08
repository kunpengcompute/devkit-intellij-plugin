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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskTemplateContent;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.util.IconLoader;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 任务模板删除 提示面板
 *
 * @since 2021-4-25
 */
public class TaskTemplateDeletePanel extends IDEBasePanel {
    private static final String WARN_PATH = "/assets/img/common/icon_warn.png";
    private JPanel mainPanel;
    private JLabel noticeLabel;
    private String tempName;

    public TaskTemplateDeletePanel(String tempName) {
        this.tempName = tempName;
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DELETE, false);
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        String noticeContent = TaskTemplateContent.TASK_TEMPLATE_DIALOG_CONTENT_DELETE + " \"" + tempName + "\"";
        Icon warnIcon = BaseIntellijIcons.load(WARN_PATH);
        Dimension labelDim = this.noticeLabel.getPreferredSize();
        labelDim.height = 30;
        this.noticeLabel.setPreferredSize(labelDim);
        this.noticeLabel.setText(noticeContent);
        this.noticeLabel.setIcon(warnIcon);
        mainPanel.setPreferredSize(new Dimension(400, 50));
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

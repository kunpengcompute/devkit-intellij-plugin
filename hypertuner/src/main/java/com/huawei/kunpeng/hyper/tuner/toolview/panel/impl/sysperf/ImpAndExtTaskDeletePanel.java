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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.util.IconLoader;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 节点管理常量定义
 *
 * @since 2020-10-15
 */
public class ImpAndExtTaskDeletePanel extends IDEBasePanel {
    private static final String WARN_PATH = "/assets/img/common/icon_warn.png";
    private JPanel mainPanel;
    private JLabel noticeLabel;
    private String taskName;

    public ImpAndExtTaskDeletePanel(String taskName) {
        this.taskName = taskName;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, ImpAndExpTaskContent.DELETE_ITEM, false);
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        String noticeContent = ImpAndExpTaskContent.DELETE_CONTENT;
        Icon warnIcon = BaseIntellijIcons.load(WARN_PATH);
        this.noticeLabel.setIcon(warnIcon);
        this.noticeLabel.setText(noticeContent);
        mainPanel.setPreferredSize(new Dimension(500, 40));
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

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

package com.huawei.kunpeng.intellij.ui.panel;

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;

import com.huawei.kunpeng.intellij.common.bean.SshConfig;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.intellij.openapi.wm.ToolWindow;

import java.awt.Dimension;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 指纹确认弹窗
 *
 * @since 2012-12-05
 */
public class FingerPanel extends IDEBasePanel {
    private JPanel mainPanel;
    private JLabel fingerLabel;
    private JPanel fingerPanel;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param config      config
     * @param isLockable  isLockable
     */
    public FingerPanel(ToolWindow toolWindow, String panelName, String displayName, SshConfig config,
        boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "ERROR_GUIDE" : panelName;

        // 初始化面板
        initPanel(config);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, StringUtil.stringIsEmpty(displayName) ? "ERROR_GUIDE" : displayName, isLockable);
    }


    /**
     * 初始化主面板
     *
     * @param config config
     */
    protected void initPanel(SshConfig config) {
        mainPanel.setMaximumSize(new Dimension(570, 70));
        fingerLabel.setIcon(ICON_INFO_ICON);
        fingerLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        fingerLabel.setVerticalTextPosition(SwingConstants.NORTH); // 垂直方向文本在图片中心
        fingerLabel.setText(MessageFormat.format(CommonI18NServer.toLocale("common_testConn_finger"),
                config.getHost(), config.getFingerprint()));
        super.initPanel(mainPanel);
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

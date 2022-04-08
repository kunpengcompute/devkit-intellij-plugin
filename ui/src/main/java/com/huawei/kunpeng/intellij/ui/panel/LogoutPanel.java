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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 登出面板
 *
 * @since 2020-09-25
 */
public class LogoutPanel extends IDEBasePanel {
    private JPanel mainPanel;

    private JLabel logOutTip;

    private JPanel tipPanel;

    private String logOutTipText;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public LogoutPanel(ToolWindow toolWindow, String panelName, String displayName,
            boolean isLockable, String logOutTipText) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LOGIN.panelName() : panelName;
        this.logOutTipText = logOutTipText;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, StringUtil.stringIsEmpty(displayName) ? CommonI18NServer.toLocale(
                "common_login_logOut") : displayName, isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public LogoutPanel(ToolWindow toolWindow, String displayName, boolean isLockable, String logOutTipText) {
        this(toolWindow, null, displayName, isLockable, logOutTipText);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public LogoutPanel(ToolWindow toolWindow, String logOutTipText) {
        this(toolWindow, null, null, false, logOutTipText);
    }

    @Override
    protected void initPanel(JPanel panel) {
        mainPanel.setPreferredSize(new Dimension(570, 71));
        tipPanel.setPreferredSize(new Dimension(570, 30));
        logOutTip.setIcon(ICON_INFO_ICON);
        logOutTip.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        logOutTip.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        logOutTip.setText(logOutTipText);
        super.initPanel(mainPanel);
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    @Override
    public ValidationInfo doValidate() {
        ValidationInfo validationInfo = null;
        return validationInfo;
    }
}

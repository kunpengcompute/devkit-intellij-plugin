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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_WARN;

import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Dimension;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 登出面板
 *
 * @since 2020-09-25
 */
public class CretConfirmPanel extends IDEBasePanel {
    private JPanel mainPanel;

    private JLabel confirmTip;

    private JPanel tipPanel;

    private final String confirmText;

    private final String ip;

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public CretConfirmPanel(ToolWindow toolWindow, String logOutTipText, String ip) {
        setToolWindow(toolWindow);
        this.panelName = UserManageConstant.CERT_ERROR_TITLE;
        this.confirmText = logOutTipText;
        this.ip = ip;
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, UserManageConstant.CERT_ERROR_TITLE, false);
    }

    @Override
    protected void initPanel(JPanel panel) {
        mainPanel.setPreferredSize(new Dimension(570, 71));
        tipPanel.setPreferredSize(new Dimension(570, 30));
        confirmTip.setIcon(ICON_INFO_WARN);
        confirmTip.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        confirmTip.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        confirmTip.setText(MessageFormat.format(confirmText, ip));
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

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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.intellij.common.constant.CSSConstant;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 提示面板
 *
 * @since 2020-09-25
 */
public class GuardianTipsPanel extends IDEBasePanel {
    private JPanel mainPanel;

    private JLabel contentTip;

    private JPanel tipPanel;

    private String contentTipText;

    private ImageIcon imageIcon;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public GuardianTipsPanel(
            ToolWindow toolWindow, String panelName, String displayName, boolean isLockable, String contentTipText) {
        setToolWindow(toolWindow);
        imageIcon = "add".equals(panelName) ? CSSConstant.ICON_INFO_ICON : CSSConstant.ICON_INFO_WARN;
        this.panelName = StringUtil.stringIsEmpty(panelName) ? GuardianMangerConstant.GUARDIAN_ADD_TITLE : panelName;
        this.contentTipText = contentTipText;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(
                mainPanel,
                StringUtil.stringIsEmpty(displayName) ? GuardianMangerConstant.GUARDIAN_ADD_TITLE : displayName,
                isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public GuardianTipsPanel(ToolWindow toolWindow, String displayName, boolean isLockable, String logOutTipText) {
        this(toolWindow, null, displayName, isLockable, logOutTipText);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public GuardianTipsPanel(ToolWindow toolWindow, String logOutTipText, String type) {
        this(toolWindow, type, null, false, logOutTipText);
    }

    @Override
    protected void initPanel(JPanel panel) {
        mainPanel.setPreferredSize(new Dimension(570, 71));
        tipPanel.setPreferredSize(new Dimension(570, 30));
        contentTip.setIcon(imageIcon);
        contentTip.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        contentTip.setVerticalTextPosition(SwingConstants.TOP); // 垂直方向文本首行与图片平行
        contentTip.setText(contentTipText);
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

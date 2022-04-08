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

import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 添加弱口令面板
 *
 * @since 2012-10-12
 */
public abstract class AddWeakPwdPanel extends IDEBasePanel {
    /**
     * mainPanel
     */
    protected JPanel mainPanel;
    /**
     * weakPasswdTextField
     */
    protected JTextField weakPasswdTextField;
    /**
     * weakPasswdLabel
     */
    protected JLabel weakPasswdLabel;
    /**
     * certTipLabel
     */
    protected JLabel certTipLabel;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param params      面板携带参数
     */
    public AddWeakPwdPanel(ToolWindow toolWindow, String panelName, String displayName, Map params) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.ADD_WEAK_PWD.panelName() : panelName;
        this.params = params;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, null, false);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public AddWeakPwdPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, null);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     * @param params     面板携带参数
     */
    public AddWeakPwdPanel(ToolWindow toolWindow, Map params) {
        this(toolWindow, null, null, params);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    protected void initPanel(JPanel panel) {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(570, 82));
        weakPasswdLabel.setText(WeakPwdConstant.WEAK_PASSWORD);
        certTipLabel.setIcon(BaseIntellijIcons.Settings.CERT_TIPS);
        certTipLabel.setToolTipText(WeakPwdConstant.WEAK_PWD_ADD_TIP);
    }

    /**
     * 获取输入框内的值
     *
     * @return weakPasswdTextField
     */
    public String getInputText() {
        return weakPasswdTextField.getText();
    }

    /**
     * setAction
     *
     * @param action 处理事件
     */
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public ValidationInfo doValidate() {
        ValidationInfo result = null;
        // 旧密码密码校验处理
        if (!CheckedUtils.checkPwdForReg(weakPasswdTextField.getText())) {
            result = new ValidationInfo(UserManageConstant.WEAK_PWD_RULE, weakPasswdTextField);
        }
        return result;
    }
}

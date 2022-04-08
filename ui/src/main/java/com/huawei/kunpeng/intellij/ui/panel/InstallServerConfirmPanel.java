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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * 安装成功后登录面板
 *
 * @since 2020/12/16
 */
public class InstallServerConfirmPanel extends IDEBasePanel {
    /**
     * 确认按钮索引
     */
    public static final int SELECT_VERIFY = 0;

    /**
     * ssh按钮索引
     */
    public static final int SELECT_SSH = 1;

    /**
     * 其他按钮索引
     */
    public static final int SELECT_OTHER = 2;

    /**
     * 未选中索引
     */
    public static final int NOT_SELECT = -1;

    private JPanel tipsPanel;

    private JLabel iconLabel;

    private JRadioButton sshRadioButton;

    private JRadioButton verifyRadioButton;

    private JRadioButton otherRadioButton;

    private JPanel mainPanel;

    private JPanel selectPanel;

    private JTextPane tipsTextPane;

    private JTextField other;

    private JPanel inputPanel;

    /**
     * 带toolWindow的构造参数
     *
     * @param toolWindow  toolWindow
     * @param panelName   面板名称
     * @param displayName 展示名称
     * @param isLockable  是否锁定
     */
    public InstallServerConfirmPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.CHANGE_PWD.panelName() : panelName;
        // 初始化面板
        initPanel(mainPanel);
        // 初始化content实例
        createContent(mainPanel, StringUtil.stringIsEmpty(displayName) ? I18NServer.toLocale(
                "plugins_ui_common_login_title") : displayName, isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public InstallServerConfirmPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public InstallServerConfirmPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        otherRadioButton.setText(CommonI18NServer.toLocale("plugins_common_tips_ipExtra"));
        // 让选择按钮互斥
        ButtonGroup selectGroup = new ButtonGroup();
        selectGroup.add(otherRadioButton);
        selectGroup.add(verifyRadioButton);
        selectGroup.add(sshRadioButton);
        // 默认选中ssh配置
        sshRadioButton.setSelected(true);
        other.setVisible(false);
        otherRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getItemSelectable().getSelectedObjects() != null) {
                    other.setVisible(true);
                } else {
                    other.setVisible(false);
                }
                inputPanel.updateUI();
            }
        });
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 获取输入框组件
     *
     * @return 输入框组件
     */
    public JTextField getOtherField() {
        return other;
    }

    /**
     * 设置提示信息
     *
     * @param ip 安装的服务器IP
     */
    public void setTips(String ip) {
        String text = CommonI18NServer.toLocale("plugins_ui_common_install_login_tip", ip);
        tipsTextPane.setText(text);
    }

    /**
     * 设置Verify文本
     *
     * @param ip 安装的服务器IP
     */
    public void setVerifyRadioButtonText(String ip) {
        String text = CommonI18NServer.toLocale("plugins_common_tips_ipFault", ip);
        this.verifyRadioButton.setText(text);
    }

    /**
     * 设置SSH文本
     *
     * @param ip 通过SSH连接的IP地址
     */
    public void setSSHRadioButtonText(String ip) {
        String text = CommonI18NServer.toLocale("plugins_common_tips_ipSSH", ip);
        this.sshRadioButton.setText(text);
    }

    /**
     * 获取选择框选中的结果
     *
     * @return 选中按钮的索引
     */
    public int getRadioSelect() {
        if (sshRadioButton.isSelected()) {
            return SELECT_SSH;
        } else if (verifyRadioButton.isSelected()) {
            return SELECT_VERIFY;
        } else if (otherRadioButton.isSelected()) {
            return SELECT_OTHER;
        } else {
            return NOT_SELECT;
        }
    }

    /**
     * 设置Verify选择按钮的可见性
     *
     * @param flag true代表可见，反之则然
     */
    public void setSshVisible(boolean flag) {
        sshRadioButton.setVisible(flag);
    }

    /**
     * 获取输入框中IP
     *
     * @return 获取输入框内容
     */
    public String getOtherIPText() {
        return other.getText();
    }

    /**
     * 设置选中确认框
     */
    public void setVerifySelect() {
        verifyRadioButton.setSelected(true);
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();

        // IP校验处理
        if (other.isVisible() && !CheckedUtils.checkIp(other.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_ipError"), other));
        }
        return result;
    }
}

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
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_VALIDATION_NULL_PASSWORD;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 修改密码面板
 *
 * @since 2020-11-13
 */
public class ChangePasswordPanel extends IDEBasePanel {
    private static final String SPACE = " ";

    private JPanel resetPwdPanel;

    private JPanel portOldPwdPanel;

    private JPanel portNewPwdPanel;

    private JPanel portConfirmPwdPanel;

    private JPanel portChangePwdInitPanel;

    private JPasswordField resetNewPwdField;

    private JLabel resetConfirmPwdLabel;

    private JPasswordField resetConfirmPwdField;

    private JPasswordField resetOldPwdField;

    private JLabel resetOldPwdLabel;

    private JLabel resetNewPwdLabel;

    private JPanel userNamePanel;

    private JLabel userNameLabel;

    private JTextArea tipsLabel;

    private JTextField userNameField;

    private JPanel mainPanel;

    private JPanel tipsPanel;

    private JLabel iconLabel;

    private JLabel oldSee;

    private JLabel newSee;

    private JLabel resetSee;

    private JPanel oldLinePanel;

    private JPanel newLinePanel;

    private JPanel confirmLinePanel;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public ChangePasswordPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "CHANGE_PWD" : panelName;
        setLabel();
        // 初始化面板
        initPanel(mainPanel);
        registerAction();
        // 初始化content实例
        createContent(resetPwdPanel, StringUtil.stringIsEmpty(panelName) ?
            CommonI18NServer.toLocale("common_change_password") : displayName, isLockable);
    }

    /**
     * 注册组件事件
     */
    protected void registerAction() {
        passwordFieldAction.pwdDocument(resetOldPwdField);
        passwordFieldAction.pwdDocument(resetNewPwdField);
        passwordFieldAction.pwdDocument(resetConfirmPwdField);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public ChangePasswordPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public ChangePasswordPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
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

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        iconLabel.setIcon(ICON_INFO_ICON);
        iconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        iconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        tipsPanel.setPreferredSize(new Dimension(780, tipsLabel.getFont().getSize() * 2));
        tipsPanel.setVisible(true);
        tipsLabel.setFont(userNameLabel.getFont());
        tipsLabel.setBackground(null);
        tipsLabel.setVisible(true);
        tipsLabel.setLineWrap(false);
        userNameField.setText(UserInfoContext.getInstance().getUserName());
        userNameField.setEditable(false);
        userNameField.setEnabled(false);
        setLabel();
        // 密码显示事件
        passwordFieldAction.registerMouseListener(oldSee, resetOldPwdField);
        passwordFieldAction.registerMouseListenerOne(newSee, resetNewPwdField);
        passwordFieldAction.registerMouseListenerTwo(resetSee, resetConfirmPwdField);
    }

    public JPasswordField getResetNewPwdField() {
        return resetNewPwdField;
    }

    public JPasswordField getResetConfirmPwdField() {
        return resetConfirmPwdField;
    }

    public JPanel getResetPwdPanel() {
        return resetPwdPanel;
    }

    public JPasswordField getResetOldPwdField() {
        return resetOldPwdField;
    }

    /**
     * 设置提示信息
     *
     * @param str 提示信息
     */
    public void setTips(String str) {
        tipsLabel.setText(str);
    }

    private void setLabel() {
        userNameLabel.setText(SPACE + SPACE + SPACE + UserManageConstant.USER_LABEL_NAME);
        resetOldPwdLabel.setText(UserManageConstant.USER_LABEL_OLD_PASSWORD);
        resetNewPwdLabel.setText(UserManageConstant.USER_LABEL_NEW_PASSWORD);
        resetConfirmPwdLabel.setText(UserManageConstant.USER_LABEL_CONFIRM_PWD);
        tipsLabel.setText(UserManageConstant.USER_TIPS_PASSWORD_INIT);
        tipsLabel.setToolTipText(UserManageConstant.USER_TIPS_PASSWORD_INIT);
        oldSee.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        newSee.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        resetSee.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
    }

    /**
     * 全量校验
     *
     * @return 所有校验异常
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();

        // 校验用户名
        if (ValidateUtils.isEmptyString(userNameField.getText())) {
            result.add(new ValidationInfo(UserManageConstant.USER_NAME_EMPTY, userNameField));
        }

        // 重置密码校验处理
        resetPwdValidate(result);

        return result;
    }

    private void resetPwdValidate(List<ValidationInfo> result) {
        // 旧密码只做空校验处理
        String oldPwd = new String(resetOldPwdField.getPassword());
        if (StringUtil.stringIsEmpty(oldPwd)) {
            result.add(new ValidationInfo(TERM_VALIDATION_NULL_PASSWORD, resetOldPwdField));
        }

        String newPwd = new String(resetNewPwdField.getPassword());
        String newConfirmPwd = new String(resetConfirmPwdField.getPassword());

        // 新密码校验处理
        StringBuilder sb = new StringBuilder(newPwd).reverse();
        if (!CheckedUtils.checkPwdForReg(newPwd) || ValidateUtils.equals(sb.toString(), oldPwd)) {
            result.add(new ValidationInfo(UserManageConstant.USER_NEW_PWD_VALIDATE_TIPS, resetNewPwdField));
        }

        // 确认密码校验处理
        if (!CheckedUtils.checkPwdForReg(newConfirmPwd)) {
            result.add(new ValidationInfo(UserManageConstant.TERM_VALIDATION_PASSWORD, resetConfirmPwdField));
        }

        // 俩个密码不相等处理
        if (!ValidateUtils.equals(newPwd, newConfirmPwd)) {
            result.add(new ValidationInfo(UserManageConstant.TERM_NO_SAME, resetConfirmPwdField));
        }
    }

    @Override
    public void clearPwd() {
        if (resetOldPwdField != null) {
            resetOldPwdField.setText("");
            resetOldPwdField = null;
        }

        if (resetNewPwdField != null) {
            resetNewPwdField.setText("");
            resetNewPwdField = null;
        }

        if (resetConfirmPwdField != null) {
            resetConfirmPwdField.setText("");
            resetConfirmPwdField = null;
        }
    }
}

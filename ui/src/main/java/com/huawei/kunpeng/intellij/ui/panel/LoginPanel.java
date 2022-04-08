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

import com.huawei.kunpeng.intellij.common.ConfigInfo;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.LoginWrapDialog;
import com.huawei.kunpeng.intellij.ui.utils.UILoginUtils;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 登录面板
 *
 * @since 2020-09-25
 */

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class LoginPanel extends IDEBasePanel {
    /**
     * 首次登录接口返回值
     */
    protected static final int FIRST_LOGIN = 1;
    /**
     * 主面板
     */
    public JPanel mainPanel;
    /**
     * 管理员用户名
     */
    protected String admin;
    /**
     * isAdminFirstLogin
     */
    protected boolean isAdminFirstLogin = false;

    private boolean isResetPwd = false;

    private JPasswordField userNameField;

    private JLabel userNameLabel;

    private JPasswordField pwdField;

    private JPanel userNamePanel;

    private JPanel pwdPanel;

    private JLabel pwdLabel;

    private JPasswordField setPwdField;

    private JPanel setPwdPanel;

    private JLabel setPwdLabel;

    private JPasswordField setConfirmPwdField;

    private JLabel setConfirmPwdLabel;

    private JPanel checkBoxPanel;

    private JPasswordField resetOldPwdField;

    private JCheckBox savePwdCheckBox;

    private JCheckBox autoLoginCheckBox;

    private JPasswordField resetNewPwdField;

    private JPasswordField resetConfirmPwdField;

    private TextFieldWithBrowseButton pwdFieldBut;

    @Getter
    @Setter
    private JLabel eyePwd;

    private JLabel eyeStartPwd;

    private JLabel eyeConfirm;

    private JPanel pwdPanelLine;

    private JPanel setLinePanel;

    private JPanel confirmLinePanel;

    private JPanel userLinePanel;

    private JPanel boxPanel;

    private JLabel paddingLabel;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    private String lastUsrName = "";

    private String curUsrName = "";

    /**
     * 需传递管理员用户名 admin、帮助链接 helpUrl、面板显示title displayName
     *
     * @param toolWindow 依托的toolWindow窗口，可为空
     * @param isLockable isLockable
     */
    public void createLoginPanel(ToolWindow toolWindow, boolean isLockable) {
        this.admin = userName();
        setToolWindow(toolWindow);
        checkAdminFirstLogin();
        initPanel(mainPanel);
        // 初始化面板内组件事件
        registerComponentAction();
        createContent(mainPanel, displayName(), isLockable);
        // 设置用户名输入监听是否隐藏 checkBoxPanel
        setUserNameFieldListener();
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        panel.setPreferredSize(null);
        pwdLabel.setPreferredSize(userNameLabel.getPreferredSize());
        showPanelLabel();
        // 管理员首次登录，设置密码
        if (isAdminFirstLogin) {
            initAdminFirstLoginPanel();
        } else {
            initOtherUserLoginPanel();
        }
        initEyeIcon();
        registerAction();
    }

    /**
     * 注册面板事件
     */
    private void registerAction() {
        // 监听UserNameFiled事件
        registerUserNameFiledAction();
        // Eye点击事件
        registerShowOrHiddenPasswdAction();
    }

    private void registerUserNameFiledAction() {
        userNameField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                // 失去焦点执行的代码
                if (!isSavedPasswdInConfigFile()) {
                    eyePwd.setVisible(true);
                    lastUsrName = curUsrName;
                    curUsrName = String.valueOf(userNameField.getPassword());
                    if (curUsrName != null && !curUsrName.equals(lastUsrName)) {
                        pwdField.setText("");
                    }
                    savePwdCheckBox.setSelected(false);
                    autoLoginCheckBox.setSelected(false);
                    return;
                }
                curUsrName = String.valueOf(userNameField.getPassword());
                // 更新缓存
                updateIDEContextUserConfig();
                fillPassWdIntoFiledAndChangeCheckBoxStat();
                // 如果当前用户选择了自动登录，直接触发登录操作
                autoLoginForCurUser();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
    }

    private void autoLoginForCurUser() {
        if (!isAutoLogin()) {
            return;
        }
        autoLoginCheckBox.setSelected(true);
        final Object parentComponent = getParentComponent();
        if (parentComponent instanceof LoginWrapDialog) {
            ((LoginWrapDialog) parentComponent).doOkAction();
        }
    }

    private void updateIDEContextUserConfig() {
        ConfigUtils.updateIDEContextConfigInfoByConfigFile(new ConfigInfo("", userNameField.getText()));
    }

    private boolean isSavedPasswdInConfigFile() {
        return ValidateUtils.isNotEmptyString(userNameField.getText()) &&
                ConfigUtils.isContainsCurUser(userNameField.getText())
                && ConfigUtils.isSavePassword(userNameField.getText());
    }

    private boolean isAutoLogin() {
        return ConfigUtils.isAutoLogin(userNameField.getText());
    }

    private void fillPassWdIntoFiledAndChangeCheckBoxStat() {
        String decryptPasswd = UILoginUtils.decrypt();
        if (!StringUtil.stringIsEmpty(decryptPasswd)) {
            eyePwd.setVisible(false);
            pwdField.setEchoChar('*');
        } else {
            eyePwd.setVisible(true);
        }
        pwdField.setText(decryptPasswd);
        savePwdCheckBox.setSelected(true);
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (!isAdminFirstLogin) {
            registerUserNameFieldAction();
        }
        userNameField.setEchoChar('\0');
        passwordFieldAction.pwdDocument(setConfirmPwdField);
        passwordFieldAction.pwdDocument(setPwdField);
        passwordFieldAction.pwdDocument(pwdField);
    }

    /**
     * 用户名注册事件
     */
    protected void registerUserNameFieldAction() {
        userNameField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String textNew, AttributeSet attributeSet)
                    throws BadLocationException {
                String text = userNameField.getText();
                if (text.length() + textNew.length() > 32) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                super.insertString(offs, textNew, attributeSet);
            }
        });
    }

    /**
     * 设置action
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 检查当前是否是第一次登录，设置管理员密码
     */
    public abstract void checkAdminFirstLogin();

    /**
     * initCheckBoxShow
     */
    public void initCheckBoxShow() {
        checkBoxPanel.setPreferredSize(userLinePanel.getPreferredSize());
        registerCheckBoxAction();
    }

    /**
     * setUserNameFieldListener
     */
    protected void setUserNameFieldListener() {
        if (userNameField.getText().equals(admin)) {
            disableCheckBoxAndShowTips();
        }
        userNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent event) {
                addDocumentListener(event);
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                addDocumentListener(event);
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
            }
        });
    }

    private void disableCheckBoxAndShowTips() {
        savePwdCheckBox.setSelected(false);
        savePwdCheckBox.setEnabled(false);
        autoLoginCheckBox.setEnabled(false);
        autoLoginCheckBox.setSelected(false);
        savePwdCheckBox.setToolTipText(CommonI18NServer.toLocale("plugins_common_admin_cannot_remember_password"));
        autoLoginCheckBox.setToolTipText(CommonI18NServer.toLocale("plugins_common_admin_cannot_auto_login"));
        mainPanel.updateUI();
    }

    private void addDocumentListener(DocumentEvent event) {
        try {
            String text = event.getDocument().getText(event.getDocument().getStartPosition().getOffset(),
                    event.getDocument().getLength());
            if (text.equals(admin)) {
                disableCheckBoxAndShowTips();
            } else {
                savePwdCheckBox.setEnabled(true);
                autoLoginCheckBox.setEnabled(true);
                savePwdCheckBox.setToolTipText("");
                autoLoginCheckBox.setToolTipText("");
            }
        } catch (BadLocationException ex) {
            Logger.error("addDocumentListener BadLocationException.");
        }
    }

    /**
     * 是否管理员首次登录
     *
     * @return 是否管理员首次登录
     */
    public boolean isAdminFirstLogin() {
        return isAdminFirstLogin;
    }

    /**
     * 是否重置密码
     *
     * @return 是否重置密码
     */
    public boolean isResetPwd() {
        return isResetPwd;
    }

    /**
     * 设置密码文本
     *
     * @param text 文本
     */
    public void setPwdField(String text) {
        pwdField.setText(text);
    }

    /**
     * 设置 是否第一次登录 是否重置密码 标志
     *
     * @param isAdminFirstLoginFlag 是否展示
     * @param isResetPwdFlag        是否展示
     */
    public void setLoginFlag(boolean isAdminFirstLoginFlag, boolean isResetPwdFlag) {
        isAdminFirstLogin = isAdminFirstLoginFlag;
        isResetPwd = isResetPwdFlag;
    }

    /**
     * 设置记住密码复选框是否展示
     *
     * @param flag 是否展示
     */
    public void setCheckBoxPanelVisible(boolean flag) {
        checkBoxPanel.setVisible(flag);
    }

    /**
     * 普通登录时密码panel是否展示
     *
     * @param flag 是否展示
     */
    public void setSetPwdPanelVisible(boolean flag) {
        setPwdPanel.setVisible(flag);
    }

    /**
     * 普通登录时密码panel是否展示
     *
     * @param flag 是否展示
     */
    public void setPwdPanelVisible(boolean flag) {
        pwdPanel.setVisible(flag);
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    /**
     * 全量校验
     *
     * @return 所有校验异常
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();

        // 校验用户名
        if (ValidateUtils.isEmptyString(userNameField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_login_userNameEmpty"), userNameField));
        }

        // 校验密码是否为空
        if (pwdField.isShowing() && ValidateUtils.isEmptyArray(pwdField.getPassword())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_term_validation_null_password"),
                    pwdField));
        }

        // 设置管理员密码校验处理
        if (isAdminFirstLogin()) {
            setAdminPwsValidate(result);
        }
        return result;
    }

    private void setAdminPwsValidate(List<ValidationInfo> result) {
        String setPwd = new String(setPwdField.getPassword());
        String confirmPwd = new String(setConfirmPwdField.getPassword());
        if (!CheckedUtils.checkPwdForReg(setPwd)) {
            result.add(new ValidationInfo(UserManageConstant.TERM_VALIDATION_PASSWORD, setPwdField));
        }

        if (!CheckedUtils.checkPwdForReg(confirmPwd)) {
            result.add(new ValidationInfo(UserManageConstant.TERM_VALIDATION_PASSWORD, setConfirmPwdField));
        }

        // 俩个密码不相等处理。
        if (!ValidateUtils.equals(setPwd, confirmPwd)) {
            result.add(new ValidationInfo(UserManageConstant.TERM_NO_SAME, setConfirmPwdField));
        }
    }

    /**
     * 安全整改-清空密码。
     */
    @Override
    public void clearPwd() {
        clearField(resetOldPwdField);
        clearField(resetNewPwdField);
        clearField(resetConfirmPwdField);
        clearField(setPwdField);
        clearField(setConfirmPwdField);
        clearField(pwdField);
    }

    private void clearField(JPasswordField field) {
        if (field != null) {
            field.setText("");
            Arrays.fill(field.getPassword(), '0');
        }
    }

    private void registerCheckBoxAction() {
        savePwdCheckBox.addItemListener(event -> {
            if (!savePwdCheckBox.isSelected()) {
                autoLoginCheckBox.setSelected(false);
            }
        });

        autoLoginCheckBox.addItemListener(event -> {
            if (autoLoginCheckBox.isSelected()) {
                savePwdCheckBox.setSelected(true);
            }
        });
    }

    private void initOtherUserLoginPanel() {
        userNameField.setEditable(true);
        setPwdPanel.setVisible(false);
        // checkbox显示
        initCheckBoxShow();
    }

    private void initAdminFirstLoginPanel() {
        initAdminFirstLoginUserNamePanel();
        pwdPanel.setVisible(false);
        checkBoxPanel.setVisible(false);
    }

    private void initEyeIcon() {
        eyePwd.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        eyeConfirm.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        eyeStartPwd.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
    }

    private void registerShowOrHiddenPasswdAction() {
        passwordFieldAction.registerMouseListener(eyePwd, pwdField);
        passwordFieldAction.registerMouseListenerOne(eyeStartPwd, setPwdField);
        passwordFieldAction.registerMouseListenerTwo(eyeConfirm, setConfirmPwdField);
    }

    private void initAdminFirstLoginUserNamePanel() {
        userNameLabel.setPreferredSize(setConfirmPwdLabel.getPreferredSize());
        userNameField.setEditable(false);
        userNameField.setText(admin);
    }

    private void showPanelLabel() {
        userNameLabel.setText(CommonI18NServer.toLocale("common_login_userName"));
        userNameLabel.setToolTipText(CommonI18NServer.toLocale("common_login_userName"));
        pwdLabel.setText(CommonI18NServer.toLocale("common_login_password"));
        pwdLabel.setToolTipText(CommonI18NServer.toLocale("common_login_password"));
        setPwdLabel.setText(CommonI18NServer.toLocale("common_login_password"));
        setConfirmPwdLabel.setText(CommonI18NServer.toLocale("common_login_confirmPassword"));
        setConfirmPwdLabel.setToolTipText(CommonI18NServer.toLocale("common_login_confirmPassword"));
        savePwdCheckBox.setText(CommonI18NServer.toLocale("common_login_savePassword"));
        autoLoginCheckBox.setText(CommonI18NServer.toLocale("common_login_autoLogIn"));
    }

    /**
     * 指定面板名
     *
     * @return string 面板名
     */
    protected abstract String displayName();

    /**
     * 指定默认登录用户
     *
     * @return string 用户名
     */
    protected abstract String userName();
}
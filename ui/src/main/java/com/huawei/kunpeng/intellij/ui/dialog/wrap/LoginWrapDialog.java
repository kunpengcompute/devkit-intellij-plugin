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

package com.huawei.kunpeng.intellij.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.ConfigInfo;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LeftTreeLoadingPanel;
import com.huawei.kunpeng.intellij.ui.panel.LoginPanel;
import com.huawei.kunpeng.intellij.ui.utils.UILoginUtils;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ComponentUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;
import java.awt.Rectangle;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * intellij 类型弹框
 *
 * @since 2020-09-25
 */
public abstract class LoginWrapDialog extends IdeaDialog {
    /**
     * 后台接口返回弱密码字段
     */
    public static final String WEAK_PWD = CommonI18NServer.toLocale("common_response_pwdWeak");
    /**
     * 记录是否需要弹出ChangePasswordPanel
     */
    public static String loginChangeCodeStatus = "0";

    private JButton confirmButton;

    /**
     * 关闭setting界面
     */
    public static void closeIntellijSettingsDialog() {
        ApplicationManager.getApplication().invokeLater(() -> {
            Component focusOwner = ComponentUtil.getActiveWindow().getFocusOwner();
            if (focusOwner == null) {
                focusOwner = ComponentUtil.getActiveWindow();
            }
            DialogWrapper settings = DialogWrapper.findInstance(focusOwner);
            if (settings instanceof SettingsDialog) {
                settings.close(DialogWrapper.CLOSE_EXIT_CODE);
            }
        });
    }

    /**
     * 需在map中传递参数弹窗标题 title、弹框名 dialogName、帮助链接 helpUrl
     *
     * @param panel     需要展示的面板之一
     * @param resizable 大小是否可变
     */
    public void createLoginWrapDialog(IDEBasePanel panel, boolean resizable) {
        this.title = title();
        this.dialogName = dialogName();
        this.mainPanel = panel;
        // 设置弹框大小是否可变
        this.resizable = resizable;
        rectangle = new Rectangle(0, 0, 670, 200);
        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName(CommonI18NServer.toLocale("common_term_operate_ok"),
                CommonI18NServer.toLocale("common_button_cancel"));
        // 设置帮助
        setHelp(CommonI18NServer.toLocale("common_login_help"), helpUrl());
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
        this.mainPanel.setParentComponent(this);
        confirmButton = this.getButton(okAction);
        confirmButton.addActionListener(listener -> doOKListenerAction());
    }

    private void doOKListenerAction() {
        if (okAction instanceof OKAction && !((OKAction) okAction).validate()) {
            return;
        }
        doOkAction();
    }

    /**
     * 确定登录的刷新动作
     */
    public void doOkAction() {
        // 左侧树加载面板刷新
        refreshLeftTreeLoadingPanel();
        // 后台线程执行接口调用
        loginOnBackGround();
    }

    /**
     * 自定义 左侧树加载面板刷新
     */
    protected void refreshLeftTreeLoadingPanel() {
        // 刷新loading状态
        ToolWindow toolWindow =
                ToolWindowManager.getInstance(CommonUtil.getDefaultProject())
                        .getToolWindow(getPluginToolWindowID());
        LeftTreeLoadingPanel leftTreeLoadingPanel =
                new LeftTreeLoadingPanel(toolWindow, CommonUtil.getDefaultProject());
        UIUtils.changeToolWindowToDestPanel(leftTreeLoadingPanel, toolWindow);
    }

    /**
     * 后台线程执行登录接口调用
     */
    private void loginOnBackGround() {
        ProgressManager.getInstance().run(new Task.Backgroundable(CommonUtil.getDefaultProject(),
                CommonI18NServer.toLocale("common_loading"), false,
                PerformInBackgroundOption.ALWAYS_BACKGROUND) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                loginActionLater();
            }
        });
    }

    private void loginActionLater() {
        if (okVerify()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                close(OK_EXIT_CODE);
            });
        } else {
            ((LoginPanel) mainPanel).getPwdField().setText("");
            ((LoginPanel) mainPanel).getEyePwd().setVisible(true);
        }
    }

    @Override
    protected boolean okVerify() {
        LoginPanel panel;
        if (mainPanel instanceof LoginPanel) {
            panel = (LoginPanel) mainPanel;
        } else {
            return false;
        }
        // 设置操作员密码
        if (panel.isAdminFirstLogin()) {
            setAdminPwd(panel, panel.getSetPwdField().getPassword(), panel.getSetConfirmPwdField().getPassword());
            return false;
        } else {
            return loginAndProcessLoginRsp(panel);
        }
    }

    /**
     * processSavePassword
     *
     * @param panel     panel
     * @param configKey configKey
     */
    protected void processSavePassword(LoginPanel panel, String configKey) {
        if (ValidateUtils.equals(UserInfoContext.getInstance().getRole(), "Admin")) {
            ConfigUtils.updateUserConfig(configKey, panel.getUserNameField().getText(), false, false);
        } else {
            updateOrCreateConfigFile(panel, configKey);
            doEncryptForPasswd(panel);
        }
    }

    private void updateOrCreateConfigFile(LoginPanel panel, String configKey) {
        // 更新总的config.json文件
        ConfigUtils.updateUserConfig(configKey, panel.getUserNameField().getText(),
                panel.getSavePwdCheckBox().isSelected(), panel.getAutoLoginCheckBox().isSelected());
        // 更新当前用户的config.json文件
        ConfigUtils.updateCurUserAutoLoginModelInConfig(configKey, panel.getUserNameField().getText(),
                panel.getSavePwdCheckBox().isSelected(), panel.getAutoLoginCheckBox().isSelected());
    }

    private void doEncryptForPasswd(LoginPanel panel) {
        if (panel.getSavePwdCheckBox().isSelected()) {
            final char[] password = panel.getPwdField().getPassword();
            UILoginUtils.encrypt(String.valueOf(password));
            Arrays.fill(password, '0');
        } else {
            String curUserCryptRootPath = CommonUtil.getCurUserCryptRootPath();
            if (!FileUtil.validateFilePath(curUserCryptRootPath)) {
                return;
            }
            if (!FileUtil.deleteDir(null,
                    Paths.get(curUserCryptRootPath, IDEConstant.CRYPT_DIR).toString())) {
                Logger.error("Delete encrypt dir error.");
            }
        }
    }

    /**
     * repaintLogin
     *
     * @param panel 登录面板
     */
    protected void repaintLogin(LoginPanel panel) {
        // 设置完或修改完密码后，展示普通登录页面
        panel.setPwdPanelVisible(true);
        panel.setLoginFlag(false, false);
        panel.setPwdField("");
        panel.setSetPwdPanelVisible(false);
        panel.setCheckBoxPanelVisible(true);
        panel.repaint();
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    @Override
    protected List<ValidationInfo> doValidateAll() {
        // 若校验失败则将面板刷新回未登录状态
        if (this.mainPanel.doValidateAll().size() != 0) {
            refreshLogin();
        }
        return this.mainPanel.doValidateAll();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        customizeLoginOnOKAction();
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
        Logger.info("onCancelAction");
        mainPanel.clearPwd();
        closeIntellijSettingsDialog();
    }

    @Override
    protected boolean isNeedInvokeLaterPanel() {
        return true;
    }

    /**
     * 创建面板内容
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 用户详情
     *
     * @param userName 用户名
     */
    protected void processContextUserInfo(String userName) {
        String key = CommonUtil.getDefaultProject().getName() + "#" + getToolName();
        ConfigInfo configInfo = IDEContext.getProjectConfig().get(key);
        if (configInfo == null) {
            Logger.error("Get current project user config info error.");
            return;
        }
        configInfo.setUserName(userName);
    }


    /**
     * loginAndProcessLoginRsp
     *
     * @param panel 登录面板
     * @return loginAndProcessLoginRsp
     */
    protected boolean loginAndProcessLoginRsp(LoginPanel panel) {
        ResponseBean rsp = doLoginRequest(panel);
        if (rsp == null) {
            return false;
        }
        if (isPwdWeakType(rsp)) {
            loginPwdWeakTypeAction(panel, rsp);
        }
        if (isLoginSucceed(rsp)) {
            return loginSucceedLaterAction(panel, rsp);
        }
        if (isFirstLogin(rsp)) {
            return firstLoginLaterAction(panel, rsp);
        }
        if (isPwdExpired(rsp)) {
            return pwdExpiredLaterAction(panel, rsp);
        }
        if (isPwdWillExpired(rsp)) {
            return pwdWillExpiredLaterAction(panel, rsp);
        }
        if (isOnlineUserMax(rsp)) {
            return onlineUserMaxLaterAction(panel, rsp);
        }
        showLoginErrorInfo(rsp);
        return false;
    }

    /**
     * 检查前后端版本兼容性
     */
    protected void checkToolVersion() {
        ResponseBean rsp = doGetToolVersionRequest();
        if (rsp == null) {
            return;
        }
        String toolVersion = parsePluginVersion();
        showTips(getServerVersion(rsp), toolVersion);
        setGlobalContext(rsp.getVersion());
    }

    private String parsePluginVersion() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        String toolVersion = "";
        if (config.get(ConfigProperty.PORT_VERSION.vaLue()) instanceof List) {
            List temList = (List<?>) config.get(ConfigProperty.PORT_VERSION.vaLue());
            Object versionObj = temList.get(0);
            if (versionObj instanceof String) {
                toolVersion = (String) versionObj;
            }
        }
        return toolVersion;
    }

    /**
     * 显示弹框提示
     *
     * @param version     version
     * @param toolVersion toolVersion
     */
    protected void showTips(String version, String toolVersion) {
        if (version.isEmpty()) {
            IDENotificationUtil.notificationCommon(new NotificationBean("",
                    getToolVersionInfo(), NotificationType.WARNING));
            return;
        }
        if (!toolVersion.equals(version)) {
            // 展示版本信息提示
            showVersionTips(toolVersion, version);
        }
    }

    /**
     * 比较插件和服务端的版本大小
     *
     * @param toolVersion   插件版本
     * @param serverVersion 服务端版本
     */
    protected void showVersionTips(String toolVersion, String serverVersion) {
        String tips;
        if (toolVersion.compareTo(serverVersion) > 0) {
            tips = showServerOldTips();
        } else {
            tips = showPluginOldTips();
        }
        tips = tips.replace("{0}", serverVersion);
        tips = tips.replace("{1}", toolVersion);
        IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.WARNING));
    }

    /**
     * 首次登录，设置操作员密码
     *
     * @param panel      登录面板
     * @param pwd        首次输入
     * @param confirmPwd 二次确认
     */
    protected void setAdminPwd(LoginPanel panel, char[] pwd, char[] confirmPwd) {
        ResponseBean rsp = doSetAdminPwdRequest(pwd, confirmPwd);
        if (rsp == null) {
            return;
        }
        if (!(ValidateUtils.equals(rsp.getStatus(), UserManageConstant.LOGIN_OK) ||
                ValidateUtils.equals(rsp.getCode(), UserManageConstant.TUNING_PWDSETSUCCESS))) {
            // 密码设置失败
            IDENotificationUtil.notificationCommon(new NotificationBean("", setPwdErrorInfo(rsp),
                    NotificationType.ERROR));
        }
        repaintLogin(panel);
    }

    /**
     * 点击确定事件
     */
    protected void customizeLoginOnOKAction() {
        // 普通用户首次登录或者密码超期后，弹出修改密码
        if (isFirstLoginOrPwdExpired()) {
            modifyPwd();
            return;
        }
        doLoginLaterAction();
    }

    private void modifyPwd() {
        ChangePasswordPanel changePwdPanel = new ChangePasswordPanel(null);
        if (isFirstLoginAccordingStatus()) {
            title = CommonI18NServer.toLocale("common_change_password");
            changePwdPanel.setTips(CommonI18NServer.toLocale("common_tips_passwordInit"));
        } else {
            title = CommonI18NServer.toLocale("common_login_resetPassword");
            changePwdPanel.setTips(I18NServer.toLocale("plugins_porting_message_passwordExpired"));
        }
        popChangePwdDialog(changePwdPanel);
        return;
    }

    /**
     * 获取响应的ToolName
     *
     * @return ToolName
     */
    protected abstract String getToolName();

    /**
     * 指定弹框标题
     *
     * @return string 弹框标题
     */
    protected abstract String title();

    /**
     * 指定左下角帮助按钮链接
     *
     * @return string 帮助链接
     */
    protected abstract String dialogName();

    /**
     * 指定左下角帮助按钮链接
     *
     * @return string 帮助链接
     */
    protected abstract String helpUrl();

    /**
     * 左侧树刷新为登录面板
     */
    protected abstract void refreshLogin();

    /**
     * 获取toolWindow id
     *
     * @return id
     */
    protected abstract String getPluginToolWindowID();

    /**
     * Login 操作
     *
     * @param panel panel
     * @return ResponseBean
     */
    protected abstract ResponseBean doLoginRequest(LoginPanel panel);

    /**
     * 判断是否登录成功
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isLoginSucceed(ResponseBean rsp);

    /**
     * 判断当前密码是否为弱口令
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isPwdWeakType(ResponseBean rsp);

    /**
     * 判断用户是否是第一次登录
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isFirstLogin(ResponseBean rsp);

    /**
     * 判断密码是否过期
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isPwdExpired(ResponseBean rsp);

    /**
     * 判断密码是否即将过期
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isPwdWillExpired(ResponseBean rsp);

    /**
     * 判断当前在线用户数是否达到最大
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isOnlineUserMax(ResponseBean rsp);

    /**
     * 登录成功后的系统操作
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean loginSucceedLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * 检查到当前密码为弱密码的后续操作
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean loginPwdWeakTypeAction(LoginPanel panel, ResponseBean rsp);

    /**
     * 首次登陆后动作处理
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean firstLoginLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * 检查到密码将要过期后的处理
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean pwdExpiredLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * 密码即将过期的操作
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean pwdWillExpiredLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * 在线用户达到最大时的操作
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean onlineUserMaxLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * 登录失败错误提示
     *
     * @param rsp rsp
     */
    protected abstract void showLoginErrorInfo(ResponseBean rsp);

    /**
     * 向服务端请求获取版本信息
     *
     * @return ResponseBean
     */
    protected abstract ResponseBean doGetToolVersionRequest();

    /**
     * 获取当前服务端版本信息
     *
     * @param rsp rsp
     * @return String
     */
    protected abstract String getServerVersion(ResponseBean rsp);

    /**
     * 获取当前插件版本信息
     *
     * @return String
     */
    protected abstract String getToolVersionInfo();

    /**
     * 设置上下文信息
     *
     * @param version version
     */
    protected abstract void setGlobalContext(String version);

    /**
     * 服务端版本过老的提示
     *
     * @return info
     */
    protected abstract String showServerOldTips();

    /**
     * 插件版本过老的提示信息
     *
     * @return info
     */
    protected abstract String showPluginOldTips();

    /**
     * 设置密码错误提示信息
     *
     * @param rsp rsp
     * @return String
     */
    protected abstract String setPwdErrorInfo(ResponseBean rsp);

    /**
     * 设置密码请求
     *
     * @param pwd        pwd
     * @param confirmPwd confirmPwd
     * @return ResponseBean
     */
    protected abstract ResponseBean doSetAdminPwdRequest(char[] pwd, char[] confirmPwd);

    /**
     * 检测是否第一次登录或密码过期
     *
     * @return boolean
     */
    protected abstract boolean isFirstLoginOrPwdExpired();

    /**
     * 登录后续操作
     */
    protected abstract void doLoginLaterAction();

    /**
     * 检测是否第一次登录
     *
     * @return boolean
     */
    protected abstract boolean isFirstLoginAccordingStatus();

    /**
     * 弹出更改密码Dialog
     *
     * @param changePwdPanel changePwdPanel
     */
    protected abstract void popChangePwdDialog(ChangePasswordPanel changePwdPanel);
}

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
 * intellij ????????????
 *
 * @since 2020-09-25
 */
public abstract class LoginWrapDialog extends IdeaDialog {
    /**
     * ?????????????????????????????????
     */
    public static final String WEAK_PWD = CommonI18NServer.toLocale("common_response_pwdWeak");
    /**
     * ????????????????????????ChangePasswordPanel
     */
    public static String loginChangeCodeStatus = "0";

    private JButton confirmButton;

    /**
     * ??????setting??????
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
     * ??????map??????????????????????????? title???????????? dialogName??????????????? helpUrl
     *
     * @param panel     ???????????????????????????
     * @param resizable ??????????????????
     */
    public void createLoginWrapDialog(IDEBasePanel panel, boolean resizable) {
        this.title = title();
        this.dialogName = dialogName();
        this.mainPanel = panel;
        // ??????????????????????????????
        this.resizable = resizable;
        rectangle = new Rectangle(0, 0, 670, 200);
        // ??????????????????????????????????????????
        setOKAndCancelName(CommonI18NServer.toLocale("common_term_operate_ok"),
                CommonI18NServer.toLocale("common_button_cancel"));
        // ????????????
        setHelp(CommonI18NServer.toLocale("common_login_help"), helpUrl());
        // ?????????????????????
        initDialog();
    }

    /**
     * ???????????????
     */
    @Override
    protected void initDialog() {
        // ?????????????????????
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
     * ???????????????????????????
     */
    public void doOkAction() {
        // ???????????????????????????
        refreshLeftTreeLoadingPanel();
        // ??????????????????????????????
        loginOnBackGround();
    }

    /**
     * ????????? ???????????????????????????
     */
    protected void refreshLeftTreeLoadingPanel() {
        // ??????loading??????
        ToolWindow toolWindow =
                ToolWindowManager.getInstance(CommonUtil.getDefaultProject())
                        .getToolWindow(getPluginToolWindowID());
        LeftTreeLoadingPanel leftTreeLoadingPanel =
                new LeftTreeLoadingPanel(toolWindow, CommonUtil.getDefaultProject());
        UIUtils.changeToolWindowToDestPanel(leftTreeLoadingPanel, toolWindow);
    }

    /**
     * ????????????????????????????????????
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
        // ?????????????????????
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
        // ????????????config.json??????
        ConfigUtils.updateUserConfig(configKey, panel.getUserNameField().getText(),
                panel.getSavePwdCheckBox().isSelected(), panel.getAutoLoginCheckBox().isSelected());
        // ?????????????????????config.json??????
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
     * @param panel ????????????
     */
    protected void repaintLogin(LoginPanel panel) {
        // ?????????????????????????????????????????????????????????
        panel.setPwdPanelVisible(true);
        panel.setLoginFlag(false, false);
        panel.setPwdField("");
        panel.setSetPwdPanelVisible(false);
        panel.setCheckBoxPanelVisible(true);
        panel.repaint();
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @Override
    protected List<ValidationInfo> doValidateAll() {
        // ???????????????????????????????????????????????????
        if (this.mainPanel.doValidateAll().size() != 0) {
            refreshLogin();
        }
        return this.mainPanel.doValidateAll();
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onOKAction() {
        customizeLoginOnOKAction();
    }

    /**
     * ???????????????????????????
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
     * ??????????????????
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * ????????????
     *
     * @param userName ?????????
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
     * @param panel ????????????
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
     * ??????????????????????????????
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
     * ??????????????????
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
            // ????????????????????????
            showVersionTips(toolVersion, version);
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param toolVersion   ????????????
     * @param serverVersion ???????????????
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
     * ????????????????????????????????????
     *
     * @param panel      ????????????
     * @param pwd        ????????????
     * @param confirmPwd ????????????
     */
    protected void setAdminPwd(LoginPanel panel, char[] pwd, char[] confirmPwd) {
        ResponseBean rsp = doSetAdminPwdRequest(pwd, confirmPwd);
        if (rsp == null) {
            return;
        }
        if (!(ValidateUtils.equals(rsp.getStatus(), UserManageConstant.LOGIN_OK) ||
                ValidateUtils.equals(rsp.getCode(), UserManageConstant.TUNING_PWDSETSUCCESS))) {
            // ??????????????????
            IDENotificationUtil.notificationCommon(new NotificationBean("", setPwdErrorInfo(rsp),
                    NotificationType.ERROR));
        }
        repaintLogin(panel);
    }

    /**
     * ??????????????????
     */
    protected void customizeLoginOnOKAction() {
        // ??????????????????????????????????????????????????????????????????
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
     * ???????????????ToolName
     *
     * @return ToolName
     */
    protected abstract String getToolName();

    /**
     * ??????????????????
     *
     * @return string ????????????
     */
    protected abstract String title();

    /**
     * ?????????????????????????????????
     *
     * @return string ????????????
     */
    protected abstract String dialogName();

    /**
     * ?????????????????????????????????
     *
     * @return string ????????????
     */
    protected abstract String helpUrl();

    /**
     * ??????????????????????????????
     */
    protected abstract void refreshLogin();

    /**
     * ??????toolWindow id
     *
     * @return id
     */
    protected abstract String getPluginToolWindowID();

    /**
     * Login ??????
     *
     * @param panel panel
     * @return ResponseBean
     */
    protected abstract ResponseBean doLoginRequest(LoginPanel panel);

    /**
     * ????????????????????????
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isLoginSucceed(ResponseBean rsp);

    /**
     * ????????????????????????????????????
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isPwdWeakType(ResponseBean rsp);

    /**
     * ????????????????????????????????????
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isFirstLogin(ResponseBean rsp);

    /**
     * ????????????????????????
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isPwdExpired(ResponseBean rsp);

    /**
     * ??????????????????????????????
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isPwdWillExpired(ResponseBean rsp);

    /**
     * ?????????????????????????????????????????????
     *
     * @param rsp rsp
     * @return boolean
     */
    protected abstract boolean isOnlineUserMax(ResponseBean rsp);

    /**
     * ??????????????????????????????
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean loginSucceedLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * ????????????????????????????????????????????????
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean loginPwdWeakTypeAction(LoginPanel panel, ResponseBean rsp);

    /**
     * ???????????????????????????
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean firstLoginLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * ???????????????????????????????????????
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean pwdExpiredLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * ???????????????????????????
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean pwdWillExpiredLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * ????????????????????????????????????
     *
     * @param panel panel
     * @param rsp   rsp
     * @return boolean
     */
    protected abstract boolean onlineUserMaxLaterAction(LoginPanel panel, ResponseBean rsp);

    /**
     * ????????????????????????
     *
     * @param rsp rsp
     */
    protected abstract void showLoginErrorInfo(ResponseBean rsp);

    /**
     * ????????????????????????????????????
     *
     * @return ResponseBean
     */
    protected abstract ResponseBean doGetToolVersionRequest();

    /**
     * ?????????????????????????????????
     *
     * @param rsp rsp
     * @return String
     */
    protected abstract String getServerVersion(ResponseBean rsp);

    /**
     * ??????????????????????????????
     *
     * @return String
     */
    protected abstract String getToolVersionInfo();

    /**
     * ?????????????????????
     *
     * @param version version
     */
    protected abstract void setGlobalContext(String version);

    /**
     * ??????????????????????????????
     *
     * @return info
     */
    protected abstract String showServerOldTips();

    /**
     * ?????????????????????????????????
     *
     * @return info
     */
    protected abstract String showPluginOldTips();

    /**
     * ??????????????????????????????
     *
     * @param rsp rsp
     * @return String
     */
    protected abstract String setPwdErrorInfo(ResponseBean rsp);

    /**
     * ??????????????????
     *
     * @param pwd        pwd
     * @param confirmPwd confirmPwd
     * @return ResponseBean
     */
    protected abstract ResponseBean doSetAdminPwdRequest(char[] pwd, char[] confirmPwd);

    /**
     * ??????????????????????????????????????????
     *
     * @return boolean
     */
    protected abstract boolean isFirstLoginOrPwdExpired();

    /**
     * ??????????????????
     */
    protected abstract void doLoginLaterAction();

    /**
     * ???????????????????????????
     *
     * @return boolean
     */
    protected abstract boolean isFirstLoginAccordingStatus();

    /**
     * ??????????????????Dialog
     *
     * @param changePwdPanel changePwdPanel
     */
    protected abstract void popChangePwdDialog(ChangePasswordPanel changePwdPanel);
}

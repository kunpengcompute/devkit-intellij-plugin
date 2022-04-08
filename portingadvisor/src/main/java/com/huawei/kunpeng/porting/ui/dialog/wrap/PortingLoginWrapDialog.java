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

package com.huawei.kunpeng.porting.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.LoginWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LoginPanel;
import com.huawei.kunpeng.porting.action.setting.webcert.PortingWebServerCertificateAction;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.common.CacheDataOpt;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.common.constant.enums.ConfigProperty;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.http.manager.user.PortingUserManageHandler;
import com.huawei.kunpeng.porting.ui.dialog.disclaimer.UserDisclaimerDialog;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;

/**
 * porting登录dialog
 *
 * @since 2021-04-10
 */
public class PortingLoginWrapDialog extends LoginWrapDialog {
    public PortingLoginWrapDialog(IDEBasePanel panel) {
        super.createLoginWrapDialog(panel, false);
    }

    @Override
    protected String title() {
        return PortingUserManageConstant.LOGIN_TITLE;
    }

    @Override
    protected String dialogName() {
        return Dialogs.LOGIN.dialogName();
    }

    @Override
    protected String helpUrl() {
        return I18NServer.toLocale("plugins_porting_login_help_url");
    }

    @Override
    protected void refreshLogin() {
        LoginUtils.refreshLogin();
    }

    @Override
    protected String getPluginToolWindowID() {
        return PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID;
    }

    @Override
    protected boolean isLoginSucceed(ResponseBean rsp) {
        return LoginUtils.LOGIN_OK.equals(rsp.getStatus());
    }

    @Override
    protected boolean isPwdWeakType(ResponseBean rsp) {
        return LoginUtils.LOGIN_WEAK_PWD_WARN.equals(rsp.getRealStatus()) && "true".equals(
            CommonUtil.getRepDataInfo(rsp, WEAK_PWD, WEAK_PWD));
    }

    @Override
    protected boolean isFirstLogin(ResponseBean rsp) {
        return LoginUtils.USER_FIRST_LOGIN.equals(rsp.getStatus());
    }

    @Override
    protected boolean isPwdExpired(ResponseBean rsp) {
        return LoginUtils.LOGIN_EXPIRED.equals(rsp.getStatus());
    }

    @Override
    protected boolean isPwdWillExpired(ResponseBean rsp) {
        return LoginUtils.LOGIN_WILL_EXPIRED.equals(rsp.getStatus());
    }

    @Override
    protected boolean isOnlineUserMax(ResponseBean rsp) {
        return false;
    }

    @Override
    protected boolean loginSucceedLaterAction(LoginPanel panel, ResponseBean rsp) {
        PortingUserInfoContext.putUserInfo(rsp);
        String tips = I18NServer.respToLocale(rsp);

        // 更新config 配置
        processContextUserInfo(panel.getUserNameField().getText());
        processSavePassword(panel, ConfigProperty.AUTO_LOGIN_CONFIG.vaLue());

        if (panel.getSavePwdCheckBox().isSelected()
            && ValidateUtils.equals(PortingUserInfoContext.getInstance().getRole(), "Admin")) {
            tips += I18NServer.toLocale("plugins_porting_label_adminAutoLoginTip");
        }

        checkToolVersion();
        PortingWebServerCertificateAction wbServerCertificateAction = new PortingWebServerCertificateAction();
        wbServerCertificateAction.getCertInfo();
        IDENotificationUtil.notificationCommon(new NotificationBean("", tips, NotificationType.INFORMATION));
        return true;
    }

    @Override
    protected boolean loginPwdWeakTypeAction(LoginPanel panel, ResponseBean rsp) {
        IDENotificationUtil.notificationCommon(new NotificationBean("",
            CommonI18NServer.toLocale("common_tips_pwdWeak_warn"), NotificationType.WARNING));
        return false;
    }

    @Override
    protected boolean firstLoginLaterAction(LoginPanel panel, ResponseBean rsp) {
        loginChangeCodeStatus = rsp.getStatus();
        PortingUserInfoContext.getInstance().setUserInfo(rsp);
        return true;
    }

    @Override
    protected boolean pwdExpiredLaterAction(LoginPanel panel, ResponseBean rsp) {
        return firstLoginLaterAction(panel, rsp);
    }

    @Override
    protected boolean pwdWillExpiredLaterAction(LoginPanel panel, ResponseBean rsp) {
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", I18NServer.respToLocale(rsp), NotificationType.WARNING));
        PortingUserInfoContext.getInstance().setUserInfo(rsp);
        ApplicationManager.getApplication().invokeLater(() -> {
            refreshLogin();
        });
        return true;
    }

    @Override
    protected boolean onlineUserMaxLaterAction(LoginPanel panel, ResponseBean rsp) {
        return false;
    }

    @Override
    protected void showLoginErrorInfo(ResponseBean rsp) {
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", I18NServer.respToLocale(rsp), NotificationType.ERROR));
        // 登录失败则将左侧树恢复未登录态
        ApplicationManager.getApplication().invokeLater(() -> {
            refreshLogin();
        });
    }

    @Override
    protected ResponseBean doLoginRequest(LoginPanel panel) {
        return PortingUserManageHandler.doLoginRequest(panel.getUserNameField().getPassword(),
            panel.getPwdField().getPassword());
    }

    @Override
    protected String getToolName() {
        return PortingIDEConstant.TOOL_NAME_PORTING;
    }

    @Override
    protected boolean isFirstLoginAccordingStatus() {
        return LoginUtils.USER_FIRST_LOGIN.equals(loginChangeCodeStatus);
    }

    @Override
    protected boolean isFirstLoginOrPwdExpired() {
        return LoginUtils.USER_FIRST_LOGIN.equals(loginChangeCodeStatus)
            || LoginUtils.LOGIN_EXPIRED.equals(loginChangeCodeStatus);
    }

    @Override
    protected void doLoginLaterAction() {
        // update global IDEPluginStatus
        PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_LOGIN);
        CacheDataOpt.clearUserFiles();
        mainPanel.clearPwd();

        // 登录成功后，对每个打开的project刷新左侧树面板
        LeftTreeUtil.refreshReports();

        // 登录成功之后如果打开的是settings面板，则将settings面板关闭
        closeIntellijSettingsDialog();

        // 如果用户首次登录弹出确认框。
        if (!PortingUserInfoContext.getInstance().isSignDisclaimer()) {
            // 第一次源码迁移进行免责声明
            UserDisclaimerDialog dialog = new UserDisclaimerDialog(PortingUserManageConstant.USER_DISCLAIMER_TITLE,
                null, null, true);
            dialog.displayPanel();
        }
    }

    @Override
    protected void popChangePwdDialog(ChangePasswordPanel changePwdPanel) {
        // the status should set before call changePwdDialog.displayPanel()
        loginChangeCodeStatus = UserManageConstant.LOGIN_OK;
        IDEBaseDialog changePwdDialog = new PortingChangePasswordDialog(null, changePwdPanel);
        refreshLogin();
        changePwdDialog.displayPanel();
    }

    @Override
    protected String getServerVersion(ResponseBean rsp) {
        return rsp.getVersion();
    }

    @Override
    protected ResponseBean doGetToolVersionRequest() {
        return PortingUserManageHandler.doGetToolVersionRequest();
    }

    @Override
    protected String getToolVersionInfo() {
        return I18NServer.toLocale("plugins_porting_version_not_get");
    }

    @Override
    protected void setGlobalContext(String version) {
        PortingIDEContext.setValueForGlobalContext(null, BaseCacheVal.SERVER_VERSION.vaLue(), version);
    }

    @Override
    protected String showServerOldTips() {
        return I18NServer.toLocale("plugins_porting_version_server_old");
    }

    @Override
    protected String showPluginOldTips() {
        return I18NServer.toLocale("plugins_porting_version_plugin_old");
    }

    @Override
    protected ResponseBean doSetAdminPwdRequest(char[] pwd, char[] confirmPwd) {
        return PortingUserManageHandler.doSetAdminPwdRequest(pwd, confirmPwd);
    }

    @Override
    protected String setPwdErrorInfo(ResponseBean rsp) {
        return I18NServer.respToLocale(rsp);
    }
}

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

package com.huawei.kunpeng.porting.ui.panel.loginsettings;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.enums.ConfigProperty;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.wm.ToolWindow;

import java.nio.file.Paths;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * WebServerCertificatePanel
 *
 * @since 2020-10-07
 */
public class LoginSettingsPanel extends IDEBasePanel {
    private static final int YES = 0;

    private static final int NO = 1;

    private int oldPwdIndex = 1;

    private int oldAutoLogIndex = 1;

    private JPanel mainPanel;

    private JComboBox pwdCombox;

    private JComboBox autoLoginCombox;

    private JLabel pwdLable;

    private JLabel autoLoginLable;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow 依托的toolWindow窗口，可为空
     * @param panelName 面板名称
     * @param displayName 面板显示title
     * @param isLockable isLockable
     */
    public LoginSettingsPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? Panels.WEB_SERVER_CERTIFICATE.panelName() : panelName;

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化面板
        initPanel(mainPanel);

        // 初始化content实例
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow toolWindow
     * @param displayName 面板显示名称
     * @param isLockable isLockable
     */
    public LoginSettingsPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public LoginSettingsPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        pwdLable.setText(CommonI18NServer.toLocale("common_login_savePassword"));
        pwdCombox.addItem(CommonI18NServer.toLocale("common_yes"));
        pwdCombox.addItem(CommonI18NServer.toLocale("common_no"));

        autoLoginLable.setText(CommonI18NServer.toLocale("common_login_autoLogIn"));
        autoLoginCombox.addItem(CommonI18NServer.toLocale("common_yes"));
        autoLoginCombox.addItem(CommonI18NServer.toLocale("common_no"));

        // 默认选项
        pwdCombox.setSelectedIndex(NO);
        // 由于无法获取密码，所以记住密码无法设置成yes，所以需要置灰
        pwdCombox.setEnabled(false);
        autoLoginCombox.setSelectedIndex(NO);
        autoLoginCombox.setEnabled(false);

        if (ValidateUtils.equals(PortingUserInfoContext.getInstance().getRole(), "Admin")) {
            return;
        }

        // 绑定多选框之间的关联
        correlationComboxLogic();

        // 初始化复选框状态
        String userName = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "userName", String.class);
        if (userName != null && ValidateUtils.equals(PortingUserInfoContext.getInstance().getUserName(), userName)) {
            if (JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "savePassword", Boolean.class)) {
                pwdCombox.setSelectedIndex(YES);
                pwdCombox.setEnabled(true);

                // 记住密码可以修改，则自动登录亦可以修改。
                autoLoginCombox.setEnabled(true);
                oldPwdIndex = YES;
            }
            if (JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "autoLogin", Boolean.class)) {
                autoLoginCombox.setSelectedIndex(YES);
                oldAutoLogIndex = YES;
            }
        } else {
            oldPwdIndex = NO;
            oldAutoLogIndex = NO;
        }
    }

    /**
     *  关联 记住密码 和 自动登录逻辑关系。
     *
     */
    private void correlationComboxLogic() {
        // 假如不记住密码，则自动登录勾选框也要设置成 no
        pwdCombox.addItemListener(event -> {
            if (pwdCombox.getSelectedIndex() == NO) {
                autoLoginCombox.setSelectedIndex(NO);
                autoLoginCombox.setEnabled(false);
            } else {
                autoLoginCombox.setEnabled(true);
            }
        });

        // 假如自动登录,则记住密码勾选框也要设置成 yes
        autoLoginCombox.addItemListener(event -> {
            if (autoLoginCombox.getSelectedIndex() == YES) {
                pwdCombox.setSelectedIndex(YES);
                pwdCombox.setEnabled(true);
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
     * 恢复设置
     */
    public void reset() {
        int setIndex = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "savePassword", Boolean.class)
            ? YES
            : NO;
        pwdCombox.setSelectedIndex(setIndex);
        // 如果reset为记住密码，则可以编辑，否则不能
        if (setIndex == YES) {
            pwdCombox.setEnabled(true);
        } else {
            pwdCombox.setEnabled(false);
        }
        setIndex = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "autoLogin", Boolean.class)
            ? YES
            : NO;
        autoLoginCombox.setSelectedIndex(setIndex);
    }

    /**
     * 是否修改
     *
     * @return boolean 返回是否修改
     */
    public boolean isModified() {
        oldPwdIndex = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "savePassword", Boolean.class)
            ? YES
            : NO;
        boolean isPwdModified = (oldPwdIndex != pwdCombox.getSelectedIndex());
        oldAutoLogIndex = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "autoLogin", Boolean.class)
            ? YES
            : NO;
        boolean autoLoginModified = (oldAutoLogIndex != autoLoginCombox.getSelectedIndex());
        return isPwdModified || autoLoginModified;
    }

    /**
     * 应用
     */
    public void apply() {
        if (!isModified()) {
            return;
        }
        String userName = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "userName", String.class);
        boolean isSavePassword = pwdCombox.getSelectedIndex() == NO ? false : true;
        boolean isAutoLogin = autoLoginCombox.getSelectedIndex() == NO ? false : true;

        if (!isSavePassword) {
            final String curUserCryptRootPath = CommonUtil.getCurUserCryptRootPath();
            if (!FileUtil.validateFilePath(curUserCryptRootPath)) {
                Logger.error("Path contains invalid symbol, this may allow an attacker to access.");
                return;
            }
            FileUtil.deleteDir(null,
                Paths.get(curUserCryptRootPath, IDEConstant.CRYPT_DIR).toString());
        }

        ConfigUtils.updateUserConfig(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue(), userName, isSavePassword,
                isAutoLogin);
        ConfigUtils.updateCurUserAutoLoginModelInConfig(ConfigProperty.AUTO_LOGIN_CONFIG.vaLue(), userName,
            isSavePassword,
            isAutoLogin);
        NotificationBean notificationBean = new NotificationBean("",
                I18NServer.toLocale("login_settings_notification"), NotificationType.INFORMATION);
        IDENotificationUtil.notificationCommon(notificationBean);
    }
}

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

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.LoginManageConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.wm.ToolWindow;

import java.nio.file.Paths;
import java.util.Map;

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
     * ??????????????????????????????????????????
     *
     * @param toolWindow ?????????toolWindow??????????????????
     * @param panelName ????????????
     * @param displayName ????????????title
     * @param isLockable isLockable
     */
    public LoginSettingsPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? Panels.WEB_SERVER_CERTIFICATE.panelName() : panelName;

        // ??????????????????????????????
        registerComponentAction();

        // ???????????????
        initPanel(mainPanel);

        // ?????????content??????
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * ???toolWindow???displayName???????????????
     *
     * @param toolWindow toolWindow
     * @param displayName ??????????????????
     * @param isLockable isLockable
     */
    public LoginSettingsPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * ???toolWindow???????????????,????????????????????????
     *
     * @param toolWindow toolWindow
     */
    public LoginSettingsPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        pwdLable.setText(LoginManageConstant.LOGIN_SAVE_PASSWORD);
        pwdCombox.addItem(LoginManageConstant.LOGIN_YES);
        pwdCombox.addItem(LoginManageConstant.LOGIN_NO);
        autoLoginLable.setText(LoginManageConstant.AUTO_LOG_IN);
        autoLoginCombox.addItem(LoginManageConstant.LOGIN_YES);
        autoLoginCombox.addItem(LoginManageConstant.LOGIN_NO);

        // ????????????
        pwdCombox.setSelectedIndex(NO);
        // ????????????????????????????????????????????????????????????yes?????????????????????
        pwdCombox.setEnabled(false);
        autoLoginCombox.setSelectedIndex(NO);
        autoLoginCombox.setEnabled(false);

        if (ValidateUtils.equals(UserInfoContext.getInstance().getRole(), "Admin")) {
            return;
        }

        // ??????????????????????????????
        correlationComboxLogic();

        // ????????????????????????
        String userName = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "userName", String.class);
        if (userName != null && ValidateUtils.equals(UserInfoContext.getInstance().getUserName(), userName)) {
            if (JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "savePassword", Boolean.class)) {
                pwdCombox.setSelectedIndex(YES);
                pwdCombox.setEnabled(true);

                // ????????????????????????????????????????????????????????????
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
     *  ?????? ???????????? ??? ???????????????????????????
     *
     */
    private void correlationComboxLogic() {
        // ??????????????????????????????????????????????????????????????? no
        pwdCombox.addItemListener(event -> {
            if (pwdCombox.getSelectedIndex() == NO) {
                autoLoginCombox.setSelectedIndex(NO);
                autoLoginCombox.setEnabled(false);
            } else {
                autoLoginCombox.setEnabled(true);
            }
        });

        // ??????????????????,??????????????????????????????????????? yes
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
     * ????????????
     */
    public void reset() {
        int setIndex = JsonUtil.getValueIgnoreCaseFromMap(ConfigUtils.getUserConfig(), "savePassword", Boolean.class)
                ? YES
                : NO;
        pwdCombox.setSelectedIndex(setIndex);
        // ??????reset????????????????????????????????????????????????
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
     * ????????????
     *
     * @return boolean ??????????????????
     */
    public boolean isModified() {
        Map userConfig = ConfigUtils.getUserConfig();
        if (ValidateUtils.isEmptyMap(userConfig)) {
            return false;
        }
        oldPwdIndex = JsonUtil.getValueIgnoreCaseFromMap(userConfig, "savePassword", Boolean.class)
                ? YES
                : NO;
        boolean isPwdModified = (oldPwdIndex != pwdCombox.getSelectedIndex());
        oldAutoLogIndex = JsonUtil.getValueIgnoreCaseFromMap(userConfig, "autoLogin", Boolean.class)
                ? YES
                : NO;
        boolean isAutoLoginModified = (oldAutoLogIndex != autoLoginCombox.getSelectedIndex());
        return isPwdModified || isAutoLoginModified;
    }

    /**
     * ??????
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
    }
}

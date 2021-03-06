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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel;

import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.setting.user.AdminUserSettingsConfigurable;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.ValidationInfo;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * ??????????????????
 *
 * @since 2020-10-13
 */
public class AddAndModifyUserPanel extends UserDialogAbstractPanel {
    private static final long serialVersionUID = 5266605925139711671L;

    private JPanel mainPanel;

    private JTextField userNameField;

    private JLabel userNameLabel;

    private JLabel adminPasswdLabel;

    private JPasswordField adminPasswordField;

    private JLabel newPasswdLabel;

    private JPasswordField newPasswordField;

    private JLabel confirmPasswdLabel;

    private JPasswordField confirmPasswordField;

    private JLabel oldPwdView;

    private JLabel newPwdView;

    private JLabel confirmPwdView;

    private JLabel userNameTipLabel;

    /**
     * ?????????????????????
     */
    private Boolean isCreateFlag = true;

    /**
     * ?????????
     */
    private String userName;

    /**
     * ??????ID
     */
    private String userId;

    /**
     * ???????????????
     */
    private String userRole;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * ??????????????????????????????????????????
     */
    public AddAndModifyUserPanel(Boolean isCreateFlag, String title) {
        this(isCreateFlag, "", "", "");
        // ?????????content??????
        createContent(mainPanel, title, false);
    }

    /**
     * ????????????????????????
     *
     * @param isCreateFlag isCreateFlag
     * @param userName     userName
     * @param userId       userId
     * @param userRole     userRole
     */
    public AddAndModifyUserPanel(Boolean isCreateFlag, Object userName, Object userId, Object userRole) {
        this.isCreateFlag = isCreateFlag;
        this.userName = userName.toString();
        this.userId = userId.toString();
        this.userRole = userRole.toString();

        // ???????????????
        initPanel(mainPanel);

        // ??????????????????????????????
        registerComponentAction();

        // ?????????content??????
        createContent(mainPanel, TuningUserManageConstant.OPERATE_RESET, false);
        // ??????????????????
        passwordFieldAction.registerMouseListener(oldPwdView, adminPasswordField);
        passwordFieldAction.registerMouseListenerOne(newPwdView, newPasswordField);
        passwordFieldAction.registerMouseListenerTwo(confirmPwdView, confirmPasswordField);
    }

    /**
     * ?????????????????????
     *
     * @return ????????????
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        // ????????????????????????????????????
        if (isCreateFlag) {
            if (!CheckedUtils.checkUserName(userNameField.getText())) {
                vi = new ValidationInfo(TuningUserManageConstant.TERM_CHECK_USERNAME, userNameField);
            }
        }
        return vi;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> result = new ArrayList<>();
        if (vi != null) {
            result.add(vi);
        }

        // ???????????????????????????
        if (!CheckedUtils.checkPwdForReg(new String(adminPasswordField.getPassword()))) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, adminPasswordField));
        }

        // ?????????????????????
        if (!CheckedUtils.checkPwdForReg(new String(newPasswordField.getPassword()))) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, newPasswordField));
        }

        // ????????????????????????
        if (!CheckedUtils.checkPwdForReg(new String(confirmPasswordField.getPassword()))) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, confirmPasswordField));
        }
        // ???????????????????????????
        if (!matchNewOldPdw()) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_NO_SAME, newPasswordField));
        }

        return result;
    }

    @Override
    public void clearPwd() {
        if (adminPasswordField != null) {
            adminPasswordField.setText("");
            adminPasswordField = null;
        }

        if (newPasswordField != null) {
            newPasswordField.setText("");
            newPasswordField = null;
        }

        if (confirmPasswordField != null) {
            confirmPasswordField.setText("");
            confirmPasswordField = null;
        }
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (isCreateFlag) {
            setCreateUserComponents();
        } else {
            setModifyComponents();
        }

        oldPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        newPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        confirmPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
    }

    /**
     * ??????????????????????????????
     */
    private void setModifyComponents() {
        userNameLabel.setText(TuningUserManageConstant.USER_LABEL_NAME);
        userNameField.setText(userName);
        userNameField.setEditable(false);
        userNameField.setForeground(CSSConstant.TEXT_BOX_TIP_INFO_COLOR);
        if (ValidateUtils.equals(userRole, TuningUserManageConstant.USER_ROLE_ADMIN)) {
            adminPasswdLabel.setText(I18NServer.toLocale("plugins_common_term_user_label_oldPwd"));
            newPasswdLabel.setText(I18NServer.toLocale("plugins_common_term_user_label_newPwd"));
        } else {
            adminPasswdLabel.setText(TuningUserManageConstant.USER_LABEL_ADMIN_PWD);
            newPasswdLabel.setText(TuningUserManageConstant.USER_LABEL_PASSWORD);
        }
        confirmPasswdLabel.setText(TuningUserManageConstant.USER_LABEL_CONFIRM_PWD);
    }

    /**
     * ????????????????????????
     */
    private void setCreateUserComponents() {
        userNameLabel.setText(TuningUserManageConstant.USER_LABEL_NAME);
        userNameField.addFocusListener(
                new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                    }
                });
        adminPasswdLabel.setText(TuningUserManageConstant.USER_LABEL_ADMIN_PWD);
        newPasswdLabel.setText(TuningUserManageConstant.USER_LABEL_PASSWORD);
        confirmPasswdLabel.setText(TuningUserManageConstant.USER_LABEL_CONFIRM_PWD);
    }

    /**
     * ??????????????????????????????
     *
     * @param data data
     */
    @Override
    public void actionOperate(Object data) {
        if (data instanceof ResponseBean) {
            ResponseBean params = (ResponseBean) data;
            Map<String, String> map = new HashMap<>();
            ResponseBean result = null;
            String title = TuningUserManageConstant.TITLE_CREATE_USER;
            // ??????????????????
            if (isCreateFlag) {
                putCreateParams(map);
                if (action instanceof UserManagerAction) {
                    Logger.info("admin user create user begin!");
                    result =
                            ((UserManagerAction) action)
                                    .sendRequestToWEB(map, UserManagerAction.USER_ULR_PART, HttpMethod.POST.vaLue());
                    Logger.info("admin user create user end!");
                }
            } else {
                // ?????????????????????????????????
                String url = UserManagerAction.USER_ULR_PART + userId + "/";
                if (ValidateUtils.equals(userRole, TuningUserManageConstant.USER_ROLE_ADMIN)) {
                    matchAdminOldPdw();
                    title = TuningUserManageConstant.TITLE_CHANGE_USER;
                    url = UserManagerAction.USER_ULR_PART + userId + "/password/";
                    putModifyParams(map);
                } else {
                    title = TuningUserManageConstant.OPERATE_RESET;
                    putUpdateParams(map);
                }
                if (action instanceof UserManagerAction) {
                    Logger.info("admin user modify user begin!");
                    result = ((UserManagerAction) action).sendRequestToWEB(map, url, HttpMethod.PUT.vaLue());
                    Logger.info("admin user modify user end!");
                }
            }
            // ??????????????????
            if (result != null) {
                doHandlerResult(params, result, title);
            }
        }
    }

    private void doHandlerResult(ResponseBean params, ResponseBean result, String title) {
        if (!isCreateFlag) {
            if ("UserManage.Success".equals(result.getCode())) {
                if (Objects.equals(userId, UserInfoContext.getInstance().getLoginId())) {
                    String tips = TuningI18NServer.toLocale("plugins_hyper_tuner_change_password_sucess");
                    IDENotificationUtil.notificationCommon(
                            new NotificationBean(title, tips, NotificationType.INFORMATION));
                    // ????????????????????????????????????
                    params.setData("0");
                    return;
                } else {
                    String tips = TuningI18NServer.toLocale("plugins_hyper_tuner_change_password_sucess_user");
                    IDENotificationUtil.notificationCommon(
                            new NotificationBean(title, tips, NotificationType.INFORMATION));
                }
            }
        }
        if (handlerResult(result, title) && result != null && result.getCode().equals(TuningIDEConstant.SUCCESS_CODE)) {
            params.setStatus("0");
        }
        // ??????????????????
        AdminUserSettingsConfigurable.getAdminUserSettingsComponent().updateUserTale();
    }

    /**
     * ????????????
     *
     * @param map map
     */
    private void putCreateParams(Map<String, String> map) {
        map.put(CREATE_PRAMS_ADMIN, new String(adminPasswordField.getPassword()));
        map.put(CREATE_PRAMS_CONFIRM, new String(confirmPasswordField.getPassword()));
        map.put(CREATE_PRAMS_PD, new String(newPasswordField.getPassword()));
        map.put(ROLE, USER);
        map.put(USER_NAME, userNameField.getText());
    }

    private void putUpdateParams(Map<String, String> map) {
        map.put(CREATE_PRAMS_ADMIN, new String(adminPasswordField.getPassword()));
        map.put(CREATE_PRAMS_CONFIRM, new String(confirmPasswordField.getPassword()));
        map.put(CREATE_PRAMS_PD, new String(newPasswordField.getPassword()));
        map.put(ROLE, USER);
        map.put(USER_NAME, userNameField.getText());
    }

    /**
     * ??????????????????
     * old_password
     * new_password
     * confirm_password
     *
     * @param map map
     */
    private void putModifyParams(Map<String, String> map) {
        map.put(CREATE_PRAMS_OLD, new String(adminPasswordField.getPassword()));
        map.put(CREATE_PRAMS_NEW, new String(newPasswordField.getPassword()));
        map.put(CREATE_PRAMS_CONFIRM, new String(confirmPasswordField.getPassword()));
    }

    /**
     * map ????????????????????????
     * admin_password
     * password
     * confirm_password:
     *
     * @param map map
     */
    private void putResetParams(Map<String, String> map) {
        map.put(CREATE_PRAMS_PD, new String(newPasswordField.getPassword()));
        map.put(CREATE_PRAMS_ADMIN, new String(adminPasswordField.getPassword()));
        map.put(CREATE_PRAMS_CONFIRM, new String(confirmPasswordField.getPassword()));
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    private boolean matchNewOldPdw() {
        String confirmPwd = new String(confirmPasswordField.getPassword());
        String newPwd = new String(newPasswordField.getPassword());
        return Objects.equals(newPwd, confirmPwd);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    private boolean matchAdminOldPdw() {
        String adminPwd = new String(adminPasswordField.getPassword());
        String newPwd = new String(newPasswordField.getPassword());
        if (Objects.equals(adminPwd, newPwd)) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            I18NServer.toLocale("plugins_common_term_operate_reset"),
                            I18NServer.toLocale("plugins_common_term_title_change_equal"),
                            NotificationType.ERROR));
        }
        return Objects.equals(adminPwd, newPwd);
    }
}

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

package com.huawei.kunpeng.porting.ui.panel.userpanel;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_CHECK_USERNAME;
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_CHECK_USERNAME_NULL;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.OPERATE_RESET;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_CREATE_TIPS;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_NO_SAME;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_VALIDATION_NULL_ADMIN_PASSWORD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_VALIDATION_NULL_PASSWORD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_VALIDATION_PASSWORD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TITLE_CHANGE_USER;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TITLE_CREATE_USER;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_ADMIN_PWD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_CONFIRM_PWD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_NAME;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_PASSWORD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_WORKSPACE;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.WORKSPACE_HEAD_PATH;

import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.CSSConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.porting.action.setting.user.AdminUserSettingsConfigurable;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

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
    private JPanel mainPanel;

    private JPasswordField userNameField;

    private JLabel userNameLabel;

    private JLabel workSpaceLabel;

    private JTextField workSpaceField;

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

    private JPanel oldLinePanel;

    private JPanel newLinePanel;

    private JPanel confirmLinePanel;

    private JPanel userLinePanel;

    private JPanel workLinePanel;
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

    /**
     * ??????????????????
     */
    private String workSpacePath;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    private boolean isCopy;

    /**
     * ??????????????????????????????????????????
     */
    public AddAndModifyUserPanel(Boolean isCreateFlag, String title, String workSpacePath) {
        this(isCreateFlag, "", "", "", workSpacePath);
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
     * @param workSpacePath workSpacePath
     */
    public AddAndModifyUserPanel(Boolean isCreateFlag, Object userName, Object userId, Object userRole,
                                    String workSpacePath) {
        this.isCreateFlag = isCreateFlag;
        this.userName = userName.toString();
        this.userId = userId.toString();
        this.userRole = userRole.toString();
        this.workSpacePath = workSpacePath;

        // ???????????????
        initPanel(mainPanel);

        // ??????????????????????????????
        registerComponentAction();
        registerAction();
        // ?????????content??????
        createContent(mainPanel, OPERATE_RESET, false);
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
            if (StringUtil.stringIsEmpty(userNameField.getText().trim())) {
                vi = new ValidationInfo(TERM_CHECK_USERNAME_NULL, userNameField);
            } else {
                if (!CheckedUtils.checkUserName(userNameField.getText())) {
                    vi = new ValidationInfo(TERM_CHECK_USERNAME, userNameField);
                }
            }
        }
        return vi;
    }

    /**
     * ??????????????????
     */
    protected void registerAction() {
        userNameField.setEchoChar('\0');
        passwordFieldAction.pwdDocument(adminPasswordField);
        passwordFieldAction.pwdDocument(newPasswordField);
        passwordFieldAction.pwdDocument(confirmPasswordField);
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

        // ???????????????????????????(?????????????????????)
        if (StringUtil.stringIsEmpty(new String(adminPasswordField.getPassword()).trim())) {
            result.add(new ValidationInfo(TERM_VALIDATION_NULL_ADMIN_PASSWORD, adminPasswordField));
        }

        // ?????????????????????
        if (StringUtil.stringIsEmpty(new String(newPasswordField.getPassword()).trim())) {
            result.add(new ValidationInfo(TERM_VALIDATION_NULL_PASSWORD, newPasswordField));
        } else {
            if (!CheckedUtils.checkPwdForReg(new String(newPasswordField.getPassword()))) {
                result.add(new ValidationInfo(TERM_VALIDATION_PASSWORD, newPasswordField));
            }
        }

        // ????????????????????????
        if (StringUtil.stringIsEmpty(new String(confirmPasswordField.getPassword()).trim())) {
            result.add(new ValidationInfo(TERM_VALIDATION_NULL_PASSWORD, confirmPasswordField));
        }

        // ???????????????????????????(????????????????????????)
        if (!StringUtil.stringIsEmpty(new String(newPasswordField.getPassword()).trim()) &&
            !StringUtil.stringIsEmpty(new String(confirmPasswordField.getPassword()).trim())) {
            if (!matchNewOldPdw()) {
                result.add(new ValidationInfo(TERM_NO_SAME, confirmPasswordField));
            }
        } else {
            Logger.info("ValidateAll end!");
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
        if (ValidateUtils.isNotEmptyString(this.workSpacePath)) {
            // ?????????????????????
            this.workSpacePath = this.workSpacePath + WORKSPACE_HEAD_PATH + this.userName
                    + (ValidateUtils.isNotEmptyString(this.userName) ? UserManagerAction.FOLDER_SEPARATOR : "");
            workSpaceLabel.setText(USER_LABEL_WORKSPACE);
            workSpaceField.setText(this.workSpacePath);
            workSpaceField.setForeground(CSSConstant.TEXT_BOX_TIP_INFO_COLOR);
            workSpaceField.setEditable(false);
        }
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
        userNameLabel.setText(USER_LABEL_NAME);
        userNameField.setText(this.userName);
        userNameField.setEditable(false);
        userNameField.setForeground(CSSConstant.TEXT_BOX_TIP_INFO_COLOR);
        if (ValidateUtils.equals(this.userRole, UserManageConstant.USER_ROLE_ADMIN)) {
            adminPasswdLabel.setText(UserManageConstant.USER_LABEL_OLD_PASSWORD);
            newPasswdLabel.setText(UserManageConstant.USER_LABEL_NEW_PASSWORD);
        } else {
            adminPasswdLabel.setText(USER_LABEL_ADMIN_PWD);
            newPasswdLabel.setText(USER_LABEL_PASSWORD);
        }
        confirmPasswdLabel.setText(USER_LABEL_CONFIRM_PWD);

        userNameTipLabel.setVisible(false);
    }

    /**
     * ????????????????????????
     */
    private void setCreateUserComponents() {
        userNameLabel.setText(USER_LABEL_NAME);
        userNameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (ValidateUtils.isNotEmptyString(userNameField.getText())) {
                    // ??????????????????
                    workSpaceField.setText(
                        workSpacePath + userNameField.getText() + UserManagerAction.FOLDER_SEPARATOR);
                }
            }
        });

        userNameTipLabel.setText(TERM_CREATE_TIPS);
        userNameTipLabel.setForeground(CSSConstant.TEXT_TIP_INFO_COLOR);
        adminPasswdLabel.setText(USER_LABEL_ADMIN_PWD);
        newPasswdLabel.setText(USER_LABEL_PASSWORD);
        confirmPasswdLabel.setText(USER_LABEL_CONFIRM_PWD);
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
            String title = TITLE_CREATE_USER;
            // ??????????????????
            if (isCreateFlag) {
                putCreateParams(map);
                if (action instanceof UserManagerAction) {
                    Logger.info("admin user create user begin!");
                    result = ((UserManagerAction) action).sendRequestToWEB(map, UserManagerAction.USER_ULR_PART,
                        HttpMethod.POST.vaLue());
                    Logger.info("admin user create user end!");
                }
            } else {
                // ?????????????????????????????????
                String url = UserManagerAction.USER_ULR_PART + userId + UserManagerAction.FOLDER_SEPARATOR;
                if (ValidateUtils.equals(this.userRole, UserManageConstant.USER_ROLE_ADMIN)) {
                    url = UserManagerAction.USER_ULR_PART + this.userId + UserManagerAction.FOLDER_SEPARATOR
                        + RESET_PRAMS + UserManagerAction.FOLDER_SEPARATOR;
                    title = TITLE_CHANGE_USER;
                    putModifyParams(map);
                } else {
                    title = OPERATE_RESET;
                    putResetParams(map);
                }
                if (action instanceof UserManagerAction) {
                    Logger.info("admin user modify user begin!");
                    result = ((UserManagerAction) action).sendRequestToWEB(map, url, HttpMethod.POST.vaLue());
                    Logger.info("admin user modify user end!");
                }
            }

            // ??????????????????
            doHandlerResult(params, result, title);
        }
    }

    private void doHandlerResult(ResponseBean params, ResponseBean result, String title) {
        if (handlerResult(result, title) && !isCreateFlag) {
            if (Objects.equals(this.userId, PortingUserInfoContext.getInstance().getLoginId())) {
                // ????????????????????????????????????
                params.setData("0");
            }
        }
        if (result != null) {
            params.setStatus(result.getStatus());
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
        map.put(WORK_SPACE, this.workSpacePath + userNameField.getText() + UserManagerAction.FOLDER_SEPARATOR);
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
        map.put(CREATE_PRAMS_ADMIN, new String(adminPasswordField.getPassword()));
        map.put(CREATE_PRAMS_PD, new String(newPasswordField.getPassword()));
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
}

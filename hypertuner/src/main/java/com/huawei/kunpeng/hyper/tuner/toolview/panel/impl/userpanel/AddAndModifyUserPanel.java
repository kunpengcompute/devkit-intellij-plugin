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
 * 新增修改用户
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
     * 是否是创建用户
     */
    private Boolean isCreateFlag = true;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户角色。
     */
    private String userRole;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 完整构造方法，不建议直接使用
     */
    public AddAndModifyUserPanel(Boolean isCreateFlag, String title) {
        this(isCreateFlag, "", "", "");
        // 初始化content实例
        createContent(mainPanel, title, false);
    }

    /**
     * 修改用户创建实例
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

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, TuningUserManageConstant.OPERATE_RESET, false);
        // 密码显示事件
        passwordFieldAction.registerMouseListener(oldPwdView, adminPasswordField);
        passwordFieldAction.registerMouseListenerOne(newPwdView, newPasswordField);
        passwordFieldAction.registerMouseListenerTwo(confirmPwdView, confirmPasswordField);
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        // 创建用户时用户名校验处理
        if (isCreateFlag) {
            if (!CheckedUtils.checkUserName(userNameField.getText())) {
                vi = new ValidationInfo(TuningUserManageConstant.TERM_CHECK_USERNAME, userNameField);
            }
        }
        return vi;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> result = new ArrayList<>();
        if (vi != null) {
            result.add(vi);
        }

        // 管理员密码校验处理
        if (!CheckedUtils.checkPwdForReg(new String(adminPasswordField.getPassword()))) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, adminPasswordField));
        }

        // 新密码校验处理
        if (!CheckedUtils.checkPwdForReg(new String(newPasswordField.getPassword()))) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, newPasswordField));
        }

        // 确认密码校验处理
        if (!CheckedUtils.checkPwdForReg(new String(confirmPasswordField.getPassword()))) {
            result.add(new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, confirmPasswordField));
        }
        // 俩个密码不相等处理
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
     * 初始化主面板
     *
     * @param panel 面板
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
     * 设置重置密码界面组件
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
     * 设置创建用户组件
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
     * 自定义函数式事件操作
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
            // 创建用户处理
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
                // 管理员重置用户密码处理
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
            // 处理查询结果
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
                    // 管理员修改密码。退出登录
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
        // 更新用户列表
        AdminUserSettingsConfigurable.getAdminUserSettingsComponent().updateUserTale();
    }

    /**
     * 填放参数
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
     * 修改用户密码
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
     * map 添加的参数如下：
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
     * 比较新密码和确认密码是否相等
     *
     * @return 返回比较结果
     */
    private boolean matchNewOldPdw() {
        String confirmPwd = new String(confirmPasswordField.getPassword());
        String newPwd = new String(newPasswordField.getPassword());
        return Objects.equals(newPwd, confirmPwd);
    }

    /**
     * 比较新密码和确认密码是否相等
     *
     * @return 返回比较结果
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

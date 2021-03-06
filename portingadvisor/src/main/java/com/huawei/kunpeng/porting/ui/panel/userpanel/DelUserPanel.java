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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.TERM_VALIDATION_NULL_ADMIN_PASSWORD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.OPERATE_DELETE_TITLE;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_ADMIN_PWD_CHECK;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_OPERATE_DELETE_TIP1;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_ADMIN_PWD;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.action.setting.user.AdminUserSettingsConfigurable;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * ????????????
 *
 * @since 2020-10-13
 */
public class DelUserPanel extends UserDialogAbstractPanel {
    private JPanel mainPanel;

    private JPanel portPanel;

    private JLabel adminPwdLabel;

    private JPanel warnPanel;

    private JLabel iconLabel;

    private JLabel pwdView;

    private JPasswordField adminPasswordField;

    /**
     * ??????????????????ID
     */
    private String deleteUserId = "";

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * ??????????????????
     *
     * @param deleteInfo ???????????????????????????
     */
    public DelUserPanel(Object deleteInfo) {
        // ???????????????
        initPanel(mainPanel);

        // ??????????????????????????????
        registerComponentAction();

        // ?????????content??????
        createContent(mainPanel, OPERATE_DELETE_TITLE, false);

        if (deleteInfo != null) {
            this.deleteUserId = deleteInfo.toString();
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
        iconLabel.setText(TERM_OPERATE_DELETE_TIP1);
        adminPwdLabel.setText(USER_LABEL_ADMIN_PWD);
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        // ??????????????????
        passwordFieldAction.registerMouseListener(pwdView, adminPasswordField);
        passwordFieldAction.pwdDocument(adminPasswordField);
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
            if (StringUtil.stringIsEmpty(new String(adminPasswordField.getPassword()).trim())) {
                // ????????????????????????, ??????????????????
                params.setStatus(PARAM_ERROR);
                NotificationBean notificationBean = new NotificationBean(OPERATE_DELETE_TITLE, TERM_ADMIN_PWD_CHECK,
                    NotificationType.WARNING);
                notificationBean.setProject(CommonUtil.getDefaultProject());
                IDENotificationUtil.notificationCommon(notificationBean);
                Logger.info("admin password is error! delete user fail");
                return;
            }

            Map<String, String> map = putParams();

            Logger.info("admin user delete user begin!");
            if (action instanceof UserManagerAction) {
                ResponseBean result = ((UserManagerAction) action).sendRequestToWEB(map,
                    UserManagerAction.USER_ULR_PART + deleteUserId + UserManagerAction.FOLDER_SEPARATOR,
                    HttpMethod.DELETE.vaLue());
                Logger.info("admin user delete user end!");

                // ??????????????????
                handlerResult(result, OPERATE_DELETE_TITLE);
                if (result != null) {
                    params.setStatus(result.getStatus());
                }
            }

            // ??????????????????
            AdminUserSettingsConfigurable.getAdminUserSettingsComponent().updateUserTale();
        }
    }

    /**
     * ?????????????????????
     *
     * @return ????????????
     */
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (StringUtil.stringIsEmpty(new String(adminPasswordField.getPassword()).trim())) {
            vi = new ValidationInfo(TERM_VALIDATION_NULL_ADMIN_PASSWORD, adminPasswordField);
        }
        return vi;
    }

    @Override
    public void clearPwd() {
        if (adminPasswordField != null) {
            adminPasswordField.setText("");
            adminPasswordField = null;
        }
    }

    /**
     * ????????????
     *
     * @return ?????????
     */
    private Map<String, String> putParams() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(CREATE_PRAMS_ADMIN, new String(adminPasswordField.getPassword()));
        return map;
    }
}

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

import static com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.CSSConstant.RED_STAR_ICON;

import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.setting.user.AdminUserSettingsConfigurable;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.ValidationInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * ?????????????????????
 *
 * @since 2020-10-7
 */
public class DeleteUserPanel extends UserDialogAbstractPanel {
    /**
     * ????????????
     */
    private static final String DISCLAIMER_INFO_PNG = "/assets/img/settings/disclaimer_dialog.png";

    private static final long serialVersionUID = -3921191435020655511L;

    /**
     * ???????????????
     */
    private JLabel adminPwdLabel = new JLabel();

    /**
     * ??????
     */
    private JLabel warnLabel = new JLabel();

    /**
     * ??????ID??????
     */
    private JTextField userIDField = new JTextField();

    private JLabel pwdView = new JLabel();

    /**
     * ???????????????
     */
    private JPasswordField adminPasswordField = new JPasswordField();

    /**
     * ?????????
     */
    private JPanel leftPanel = new JPanel();

    /**
     * ????????????
     */
    private JPanel centerPanel = new JPanel();

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
    public DeleteUserPanel(Object deleteInfo) {
        mainPanel = new JPanel();
        // ???????????????
        initPanel(mainPanel);

        // ??????????????????????????????
        registerComponentAction();

        // ?????????content??????
        createContent(mainPanel, TuningUserManageConstant.OPERATE_DELETE_TITLE, false);

        if (deleteInfo != null) {
            deleteUserId = deleteInfo.toString();
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
        centerPanel.setPreferredSize(new Dimension(546, 102));
        leftPanel.setPreferredSize(new Dimension(25, 0));
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(null);
        centerPanel.add(userIDField);

        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            JLabel textLabel1 = new JLabel();
            JEditorPane editorPane1 = new JEditorPane();
            editorPane1.setText(
                    TuningUserManageConstant.TERM_OPERATE_DELETE_TIP_EN_PART1
                            + TuningUserManageConstant.TERM_OPERATE_DELETE_TIP1_EN_PART2);
            centerPanel.add(textLabel1);
            centerPanel.add(editorPane1);
            textLabel1.setBounds(0, 0, 30, 28);
            editorPane1.setBounds(30, 0, 500, 56);
            textLabel1.setIcon(new ImageIcon(DeleteUserPanel.class.getResource(DISCLAIMER_INFO_PNG)));
            textLabel1.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
            textLabel1.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????
        } else {
            warnLabel.setText(TuningUserManageConstant.TERM_OPERATE_DELETE_TIP1);
            warnLabel.setBounds(0, 0, 410, 28);
            centerPanel.add(warnLabel);
            warnLabel.setIcon(new ImageIcon(DeleteUserPanel.class.getResource(DISCLAIMER_INFO_PNG)));
            warnLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
            warnLabel.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????
        }

        adminPwdLabel.setIcon(RED_STAR_ICON);
        adminPwdLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
        adminPwdLabel.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????
        adminPwdLabel.setText(TuningUserManageConstant.USER_LABEL_ADMIN_PWD);
        adminPwdLabel.setBounds(0, 56, 200, 32);
        adminPasswordField.setBounds(160, 56, 358, 32);
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        pwdView.setBounds(520, 56, 28, 32);
        centerPanel.add(adminPwdLabel);
        centerPanel.add(adminPasswordField);
        centerPanel.add(pwdView);
        // ??????????????????
        passwordFieldAction.registerMouseListener(pwdView, adminPasswordField);
    }

    /**
     * ??????????????????????????????
     *
     * @param obj obj
     */
    @Override
    public void actionOperate(Object obj) {
        if (obj instanceof ResponseBean) {
            ResponseBean params = (ResponseBean) obj;
            if (!CheckedUtils.checkPwdForReg(new String(adminPasswordField.getPassword()))) {
                // ????????????????????????, ??????????????????
                params.setStatus(PARAM_ERROR);
                NotificationBean notificationBean =
                        new NotificationBean(
                                TuningUserManageConstant.OPERATE_DELETE_TITLE,
                                TuningUserManageConstant.TERM_ADMIN_PWD_CHECK,
                                NotificationType.INFORMATION);
                notificationBean.setProject(CommonUtil.getDefaultProject());
                IDENotificationUtil.notificationCommon(notificationBean);
                Logger.info("admin password is error! delete user fail");
                return;
            }

            Map<String, String> map = putParams();

            Logger.info("admin user delete user begin!");
            if (action instanceof UserManagerAction) {
                ResponseBean result =
                        ((UserManagerAction) action)
                                .sendRequestToWEB(
                                        map,
                                        UserManagerAction.USER_ULR_PART
                                                + deleteUserId
                                                + UserManagerAction.FOLDER_SEPARATOR,
                                        HttpMethod.DELETE.vaLue());
                Logger.info("admin user delete user end!");

                // ??????????????????
                handlerResult(result, TuningUserManageConstant.OPERATE_DELETE_TITLE);
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
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (!CheckedUtils.checkPwd(new String(adminPasswordField.getPassword()))) {
            vi = new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, adminPasswordField);
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

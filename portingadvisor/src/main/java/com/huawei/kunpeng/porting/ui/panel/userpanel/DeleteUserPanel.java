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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.RED_STAR_ICON;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.OPERATE_DELETE_TITLE;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_ADMIN_PWD_CHECK;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_OPERATE_DELETE_TIP1;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_OPERATE_DELETE_TIP1_EN_PART2;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_OPERATE_DELETE_TIP_EN_PART1;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TERM_VALIDATION_PASSWORD;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_ADMIN_PWD;

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
import com.huawei.kunpeng.porting.action.setting.user.AdminUserSettingsConfigurable;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;

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
 * 删除用户界面。
 *
 * @since 2020-10-7
 */
public class DeleteUserPanel extends UserDialogAbstractPanel {
    /**
     * 提示图片
     */
    private static final String DISCLAIMER_INFO_PNG = "/assets/img/settings/disclaimer_dialog.png";

    /**
     * 管理员密码
     */
    private JLabel adminPwdLabel = new JLabel();

    /**
     * 警告
     */
    private JLabel warnLabel = new JLabel();

    /**
     * 用户ID文本
     */
    private JTextField userIDField = new JTextField();

    /**
     * 管理员密码
     */
    private JPasswordField adminPasswordField = new JPasswordField();

    private JLabel pwdView = new JLabel();

    /**
     * 左布局
     */
    private JPanel leftPanel = new JPanel();

    /**
     * 内容面板
     */
    private JPanel centerPanel = new JPanel();

    /**
     * 被删除的用户ID
     */
    private String deleteUserId = "";

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 删除用户信息
     *
     * @param deleteInfo 需要删除的用户信息
     */
    public DeleteUserPanel(Object deleteInfo) {
        mainPanel = new JPanel();
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();
        registerAction();
        // 初始化content实例
        createContent(mainPanel, OPERATE_DELETE_TITLE, false);

        if (deleteInfo != null) {
            this.deleteUserId = deleteInfo.toString();
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
        leftPanel.setPreferredSize(new Dimension(25, 0));
        centerPanel.setPreferredSize(new Dimension(546, 102));
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(null);
        centerPanel.add(userIDField);

        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            JLabel textLabel1 = new JLabel();
            JEditorPane editorPane1 = new JEditorPane();
            editorPane1.setText(TERM_OPERATE_DELETE_TIP_EN_PART1 + TERM_OPERATE_DELETE_TIP1_EN_PART2);
            centerPanel.add(textLabel1);
            centerPanel.add(editorPane1);
            textLabel1.setBounds(0, 0, 30, 28);
            editorPane1.setBounds(30, 0, 500, 56);
            textLabel1.setIcon(new ImageIcon(DeleteUserPanel.class.getResource(DISCLAIMER_INFO_PNG)));
            textLabel1.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
            textLabel1.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        } else {
            warnLabel.setText(TERM_OPERATE_DELETE_TIP1);
            warnLabel.setBounds(0, 0, 410, 28);
            centerPanel.add(warnLabel);
            warnLabel.setIcon(new ImageIcon(DeleteUserPanel.class.getResource(DISCLAIMER_INFO_PNG)));
            warnLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
            warnLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        }

        adminPwdLabel.setIcon(RED_STAR_ICON);
        adminPwdLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        adminPwdLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        adminPwdLabel.setText(USER_LABEL_ADMIN_PWD);
        adminPwdLabel.setBounds(0, 56, 200, 32);
        adminPasswordField.setBounds(160, 56, 358, 32);
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        pwdView.setBounds(520, 56, 28, 32);
        centerPanel.add(adminPwdLabel);
        centerPanel.add(adminPasswordField);
        centerPanel.add(pwdView);
        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, adminPasswordField);
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
            if (!CheckedUtils.checkPwdForReg(new String(adminPasswordField.getPassword()))) {
                // 消息对话框无返回, 仅做通知作用
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

                // 处理返回结果
                handlerResult(result, OPERATE_DELETE_TITLE);
                if (result != null) {
                    params.setStatus(result.getStatus());
                }
            }

            // 更新用户列表
            AdminUserSettingsConfigurable.getAdminUserSettingsComponent().updateUserTale();
        }
    }

    /**
     * 注册组件事件
     */
    protected void registerAction() {
        passwordFieldAction.pwdDocument(adminPasswordField);
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (!CheckedUtils.checkPwdForReg(new String(adminPasswordField.getPassword()))) {
            vi = new ValidationInfo(TERM_VALIDATION_PASSWORD, adminPasswordField);
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
     * 返回参数
     *
     * @return 参数。
     */
    private Map<String, String> putParams() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(CREATE_PRAMS_ADMIN, new String(adminPasswordField.getPassword()));
        return map;
    }
}

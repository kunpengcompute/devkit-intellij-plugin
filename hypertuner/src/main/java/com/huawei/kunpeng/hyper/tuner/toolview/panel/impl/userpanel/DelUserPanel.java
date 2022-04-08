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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_WARN;

import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.setting.user.AdminUserSettingsConfigurable;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

/**
 * 删除用户
 *
 * @since 2020-10-13
 */
public class DelUserPanel extends UserDialogAbstractPanel {
    private JPanel mainPanel;

    private JPanel portPanel;

    private JLabel adminPwdLabel;

    private JPanel warnPanel;

    private JLabel pwdView;

    private JPasswordField adminPasswordField;

    private JLabel deleteTip;

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
    public DelUserPanel(Object deleteInfo) {
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, TuningUserManageConstant.OPERATE_DELETE_TITLE, false);

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
        deleteTip.setIcon(ICON_INFO_WARN);
        deleteTip.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        deleteTip.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        deleteTip.setText(TuningUserManageConstant.TERM_OPERATE_DELETE_TIP1);
        adminPwdLabel.setText(TuningUserManageConstant.USER_LABEL_ADMIN_PWD);
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, adminPasswordField);
        passwordFieldAction.pwdDocument(adminPasswordField);
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

                // 处理返回结果
                handlerResult(result, TuningUserManageConstant.OPERATE_DELETE_TITLE);
                if (result != null && result.getCode().equals(TuningIDEConstant.SUCCESS_CODE)) {
                    params.setStatus("0");
                }
            }

            // 更新用户列表
            AdminUserSettingsConfigurable.getAdminUserSettingsComponent().updateUserTale();
        }
    }

    @Override
    public void clearPwd() {
        if (adminPasswordField != null) {
            adminPasswordField.setText("");
            adminPasswordField = null;
        }
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (!CheckedUtils.checkPwdForReg(new String(adminPasswordField.getPassword()))) {
            vi = new ValidationInfo(TuningUserManageConstant.TERM_VALIDATION_PASSWORD, adminPasswordField);
        }
        return vi;
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

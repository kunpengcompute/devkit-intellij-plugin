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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.syspanel;

import com.huawei.kunpeng.hyper.tuner.action.panel.LogAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.SysSettingManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.CommonSettingsPanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The class SystemSettingPanel
 *
 * @since v2.2.T4
 */
public class SystemSettingPanel extends CommonSettingsPanel {
    /**
     * 配置类
     */
    private LogAction logAction = new LogAction();

    private JSONObject userConfig;

    private Integer certificateTime;

    private Integer passwordTime;

    private IntegerField certificateTimeField;

    private IntegerField passwordTimeField;

    private JLabel certSetLabel;

    private JLabel passwordTimeLable;

    /**
     * 构造函数
     */
    public SystemSettingPanel() {
        // 初始化数据
        mainPanel = new JPanel();
        initPanel(mainPanel);
        registerComponentAction();
        isEdit();
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 界面是否修改
     *
     * @return 结果
     */
    public boolean isModified() {
        return isUserChange() || isLogLevelChange();
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JComponent getPreferredFocusedComponent() {
        return mainPanel;
    }

    /**
     * 用户状态是否改变。
     *
     * @return 用户状态是否改变。
     */
    private boolean isUserChange() {
        String guiUserLimit = userLimitField.getText();
        String guiUserTime = userTimeField.getText();
        String guiPasswordTime = passwordTimeField.getText();
        String guiCertificateTime = certificateTimeField.getText();
        boolean isUserChange =
                !Objects.equals(userLimit.toString(), guiUserLimit)
                        || !Objects.equals(userTime.toString(), guiUserTime)
                        || !Objects.equals(passwordTime.toString(), guiPasswordTime)
                        || !Objects.equals(certificateTime.toString(), guiCertificateTime);
        return isUserChange;
    }

    /**
     * 日志级别是否改变。
     *
     * @return 日志级别是否改变。
     */
    private boolean isLogLevelChange() {
        return !Objects.equals(currentLevel, level);
    }

    /**
     * 事件处理。
     *
     * @throws ConfigurationException 配置异常。
     */
    public void apply() throws ConfigurationException {
        userLimitField.validateContent();
        userTimeField.validateContent();
        certificateTimeField.validateContent();
        passwordTimeField.validateContent();
        String limitField = userLimitField.getText();
        String time = userTimeField.getText();
        String cert = certificateTimeField.getText();
        String password = passwordTimeField.getText();
        // 修改最大用户在线数
        if (!limitField.equals(userLimit.toString())) {
            if (changeLimit(limitField)) {
                userLimit = Integer.valueOf(limitField);
            }
        }
        // 修改密码有效期
        if (!password.equals(passwordTime.toString())) {
            if (changePassword(password)) {
                passwordTime = Integer.valueOf(password);
            }
        }
        // 修改web帧数阈值
        if (!cert.equals(certificateTime.toString())) {
            if (changeCert(cert)) {
                certificateTime = Integer.valueOf(cert);
            }
        }
        // 修改会话超时时间
        if (!time.equals(userTime.toString())) {
            if (changeTime(time)) {
                userTime = Integer.valueOf(time);
            }
        }
        currentLevel = logAction.loglevel();
        if (!level.equals(currentLevel)) {
            logAction.changloglevel(level, (data) -> {
                NotificationBean notificationBean = new NotificationBean();
                if (data != null) {
                    new NotificationBean(SysSettingManageConstant.LOG_LEVEL,
                            MESSAGE_SUCESS, NotificationType.INFORMATION);
                } else {
                    new NotificationBean(
                            SysSettingManageConstant.LOG_LEVEL, MESSAGE_FAILD, NotificationType.ERROR);
                }
                notificationBean.setProject(CommonUtil.getDefaultProject());
                IDENotificationUtil.notificationCommon(notificationBean);
            });
            currentLevel = level;
        }
    }

    /**
     * 重置界面方法
     */
    public void reset() {
        userConfig = logAction.userConfig();
        // 设置初始值
        userLimit = Integer.valueOf(userConfig.getString("ONLINE_USERS"));
        userLimitField.setText(userLimit.toString());
        userTime = Integer.valueOf(userConfig.getString("USER_TIMEOUT"));
        userTimeField.setText(userTime.toString());
        certificateTime = Integer.valueOf(userConfig.getString("CERT_ADVANCED_DAYS"));
        certificateTimeField.setText(certificateTime.toString());
        passwordTime = Integer.valueOf(userConfig.getString("PASSWORD_EXPIRATION_TIME"));
        passwordTimeField.setText(passwordTime.toString());
        comboBox.setSelectedItem(currentLevel);
    }

    /**
     * 初始化面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        if (action == null) {
            action = new UserManagerAction();
        }
        userConfig = logAction.userConfig();

        // 最大在线用户数限制。
        userLimitField =
                new IntegerField(SysSettingManageConstant.MAX_USER_NUM, USER_LIMIT_MIN_NUM, USER_LIMIT_MAX_NUM);
        userLimitField.setCanBeEmpty(false);
        userLimitField.setText(userConfig.getString("ONLINE_USERS"));
        userLimitField.setToolTipText(SysSettingManageConstant.TIP_NUM_MODIFY);
        CardLayout layout = new CardLayout(10, 10);
        JPanel userLimitPanel = new JPanel(layout);
        userLimitPanel.add(userLimitField);

        // 用户超时登录现在
        userTimeField =
                new IntegerField(SysSettingManageConstant.TITLE_TIMEOUT, USER_TIMEOUT_MIN_NUM, USER_TIMEOUT_MAX_NUM);
        userTimeField.setCanBeEmpty(false);
        userTimeField.setText(userConfig.getString("USER_TIMEOUT"));
        userTimeField.setToolTipText(SysSettingManageConstant.TIP_TIMEOUT);
        JPanel userTimePanel = new JPanel(layout);
        userTimePanel.add(userTimeField);

        // web服务证书
        certificateTimeField = new IntegerField(SysSettingManageConstant.TITLE_CERTIFICATE, MIN, MAX);
        certificateTimeField.setCanBeEmpty(false);
        certificateTimeField.setText(userConfig.getString("CERT_ADVANCED_DAYS"));
        certificateTimeField.setToolTipText(SysSettingManageConstant.CERTIFICATE_CONFIG);
        JPanel certificateTimePanel = new JPanel(layout);
        certificateTimePanel.add(certificateTimeField);

        // 密码有效期
        passwordTimeField = new IntegerField(SysSettingManageConstant.TITLE_PASSWORD, USER_PWD_MIN, USER_PWD_MAX);
        passwordTimeField.setCanBeEmpty(false);
        passwordTimeField.setText(userConfig.getString("PASSWORD_EXPIRATION_TIME"));
        passwordTimeField.setToolTipText(SysSettingManageConstant.PASSWORD_CONFIG);
        JPanel passwordTimePanel = new JPanel(layout);
        passwordTimePanel.add(passwordTimeField);

        // 往表单添加元素
        FormBuilder mainFormBuilder =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(new JLabel(SysSettingManageConstant.MAX_USER_NUM), userLimitPanel)
                        .addLabeledComponent(new JLabel(SysSettingManageConstant.TITLE_TIMEOUT), userTimePanel)
                        .addLabeledComponent(
                                new JLabel(SysSettingManageConstant.TITLE_CERTIFICATE), certificateTimePanel)
                        .addLabeledComponent(new JLabel(SysSettingManageConstant.TITLE_PASSWORD), passwordTimePanel);

        // 用户管理运行日志级别
        getLogLevel(layout, mainFormBuilder);
        mainPanel = mainFormBuilder.addComponentFillVertically(panel, 0).getPanel();
    }

    private void isEdit() {
        if (Objects.equals(TuningUserManageConstant.USER_ROLE_USER, UserInfoContext.getInstance().getRole())) {
            userLimitField.setEditable(false);
            userTimeField.setEditable(false);
            certificateTimeField.setEditable(false);
            passwordTimeField.setEditable(false);
            comboBox.setEnabled(false);
        }
    }

    /**
     * 获取运行日志级别
     *
     * @param layout          layout
     * @param mainFormBuilder mainFormBuilder
     */
    private void getLogLevel(CardLayout layout, FormBuilder mainFormBuilder) {
        currentLevel = logAction.loglevel();
        level = currentLevel;
        levelTip = new JLabel(TuningUserManageConstant.TERM_LOGLEVEL);
        String[] listData = new String[] {"ERROR", "WARNING", "INFO", "DEBUG"};
        ComboBoxModel<String> model = new DefaultComboBoxModel(listData);
        comboBox = new ComboBox();
        comboBox.setModel(model);
        JPanel comboBoxNewPanel = new JPanel(layout);
        comboBoxNewPanel.add(comboBox);
        for (int i = 0; i < listData.length; i++) {
            if (listData[i].equals(level)) {
                // 设置默认选中的条目
                comboBox.setSelectedIndex(i);
                break;
            }
        }
        mainFormBuilder.addLabeledComponent(levelTip, comboBoxNewPanel);
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        registerComponentAction();
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new UserManagerAction();
        }
        comboBox.addItemListener(
                event -> {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        if (comboBox.getSelectedItem() instanceof String) {
                            level = (String) comboBox.getSelectedItem();
                        }
                    }
                });
    }

    /**
     * 修改最大在线用户
     *
     * @param limit 最大数
     * @return 结果
     */
    private boolean changeLimit(String limit) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject lobject = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("ONLINE_USERS", limit);
        lobject.put("user_config", object);
        logAction.changUserConfig(
                lobject,
                (data) -> {
                    ResponseBean res = (ResponseBean) data;
                    switch (res.getCode()) {
                        case "UserManage.Success":
                            notifyInfo(
                                    SysSettingManageConstant.MAX_USER_NUM,
                                    MESSAGE_SUCESS,
                                    NotificationType.INFORMATION);
                            succeeded.set(true);
                            break;
                        default:
                            notifyInfo(SysSettingManageConstant.MAX_USER_NUM, MESSAGE_FAILD, NotificationType.ERROR);
                            userLimitField.setText(userLimit.toString());
                            break;
                    }
                });
        return succeeded.get();
    }

    /**
     * 修改会话超时时间
     *
     * @param time shijian
     * @return 结果
     */
    private boolean changeTime(String time) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject tobject = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("USER_TIMEOUT", time);
        tobject.put("user_config", object);
        logAction.changUserConfig(
                tobject,
                (data) -> {
                    if (data instanceof ResponseBean) {
                        ResponseBean res = (ResponseBean) data;
                        switch (res.getCode()) {
                            case "UserManage.Success":
                                notifyInfo(
                                        SysSettingManageConstant.TITLE_TIMEOUT,
                                        MESSAGE_SUCESS,
                                        NotificationType.INFORMATION);
                                succeeded.set(true);
                                break;
                            default:
                                notifyInfo(
                                        SysSettingManageConstant.TITLE_TIMEOUT, MESSAGE_FAILD, NotificationType.ERROR);
                                userTimeField.setText(userTime.toString());
                                break;
                        }
                    }
                });
        return succeeded.get();
    }

    /**
     * 修改证书到期阈值
     *
     * @param certTime 证书到期时间
     * @return 结果
     */
    private boolean changeCert(String certTime) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        JSONObject cobject = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("CERT_ADVANCED_DAYS", certTime);
        cobject.put("user_config", object);
        logAction.changUserConfig(
                cobject,
                (data) -> {
                    if (data instanceof ResponseBean) {
                        ResponseBean res = (ResponseBean) data;
                        switch (res.getCode()) {
                            case "UserManage.Success":
                                notifyInfo(
                                        SysSettingManageConstant.TITLE_CERTIFICATE,
                                        MESSAGE_SUCESS,
                                        NotificationType.INFORMATION);
                                succeeded.set(true);
                                break;
                            default:
                                notifyInfo(
                                        SysSettingManageConstant.TITLE_CERTIFICATE,
                                        MESSAGE_FAILD,
                                        NotificationType.ERROR);
                                userTimeField.setText(userTime.toString());
                                break;
                        }
                    }
                });
        return succeeded.get();
    }

    /**
     * 修改密码有效期
     *
     * @param passwordTime 密码有效期
     * @return 结果
     */
    private boolean changePassword(String passwordTime) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        // 定义外层object
        JSONObject pobject = new JSONObject();
        // 定义内层object
        JSONObject object = new JSONObject();
        object.put("PASSWORD_EXPIRATION_TIME", passwordTime);
        pobject.put("user_config", object);
        logAction.changUserConfig(
                pobject,
                (data) -> {
                    if (data instanceof ResponseBean) {
                        ResponseBean res = (ResponseBean) data;
                        switch (res.getCode()) {
                            case "UserManage.Success":
                                notifyInfo(
                                        SysSettingManageConstant.TITLE_PASSWORD,
                                        MESSAGE_SUCESS,
                                        NotificationType.INFORMATION);
                                succeeded.set(true);
                                break;
                            default:
                                notifyInfo(
                                        SysSettingManageConstant.TITLE_PASSWORD, MESSAGE_FAILD, NotificationType.ERROR);
                                userTimeField.setText(userTime.toString());
                                break;
                        }
                    }
                });
        return succeeded.get();
    }

    private void notifyInfo(String name, String value, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(name, value, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }
}

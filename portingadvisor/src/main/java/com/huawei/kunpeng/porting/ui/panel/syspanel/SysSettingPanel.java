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

package com.huawei.kunpeng.porting.ui.panel.syspanel;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.syssetting.SystemConfigAction;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.action.setting.webcert.PortingWebServerCertificateAction;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;

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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The class SysSettingPanel
 *
 * @since v2.2.T4
 */
public class SysSettingPanel extends IDEBasePanel {
    /**
     * 最小用户数限制。
     */
    private static final int USER_LIMIT_MIN_NUM = 1;

    /**
     * 最大用户数限制。
     */
    private static final int USER_LIMIT_MAX_NUM = 20;

    /**
     * 最小用户数限制。
     */
    private static final int USER_TIMEOUT_MIN_NUM = 10;

    /**
     * 最大用户数限制。
     */
    private static final int USER_TIMEOUT_MAX_NUM = 240;

    /**
     * 閾值最小值
     */
    private static final int MIN = 7;

    /**
     * 閾值最大值
     */
    private static final int MAX = 180;

    /**
     * 主面板
     */
    private JPanel mainPanel;

    /**
     * 配置类
     */
    private SystemConfigAction systemConfigAction = new SystemConfigAction();

    private Integer userLimit;

    private Integer userTime;

    private IntegerField userLimitField;

    private IntegerField userTimeField;

    private PortingWebServerCertificateAction portingWebServerCertificateAction =
        new PortingWebServerCertificateAction();
    private IntegerField certSetField;
    private JPanel certSetPanel;
    private JLabel certSetLabel;

    private JLabel levelTip;
    private JComboBox comboBox;
    private String level;

    // 用于保存后台当前的日志级别。减少和后端交互次数。
    private String currentLevel;

    /**
     * 构造函数
     */
    public SysSettingPanel() {
        // 初始化数据
        mainPanel = new JPanel();
        initPanel(mainPanel);
        registerComponentAction();
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
     * mainPanel
     *
     * @return mainPanel
     */
    public JComponent getPreferredFocusedComponent() {
        return mainPanel;
    }

    /**
     * 界面是否修改
     *
     * @return 结果
     */
    public boolean isModified() {
        return isUserLimitChange() || isCertSetField() || isLogLevelChange();
    }

    /**
     * 用户状态是否改变。
     *
     * @return 用户状态是否改变。
     */
    private boolean isUserLimitChange() {
        String guiUserLimit = userLimitField.getText();
        String guiUserTime = userTimeField.getText();
        return !Objects.equals(userLimit.toString(), guiUserLimit)
            || !Objects.equals(userTime.toString(), guiUserTime);
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
     * 判断是否填写证书信息设置
     *
     * @return boolean
     */
    private boolean isCertSetField() {
        return !Objects.equals(portingWebServerCertificateAction.getInitCertSet(), this.certSetField.getText());
    }

    /**
     * 事件处理。
     *
     * @throws ConfigurationException 配置异常。
     */
    public void apply() throws ConfigurationException {
        this.userLimitField.validateContent();
        this.userTimeField.validateContent();
        this.certSetField.validateContent();

        String limit = userLimitField.getText();
        String time = userTimeField.getText();

        // 修改最大用户在线数
        if (!limit.equals(userLimit.toString())) {
            if (changeLimit(limit)) {
                userLimit = Integer.valueOf(limit);
            }
        }
        // 修改会话超时时间
        if (!time.equals(userTime.toString())) {
            if (changeTime(time)) {
                userTime = Integer.valueOf(time);
            }
        }

        if (!isCertSetField()) {
            Logger.info("CertSetField not provided.");
        } else {
            portingWebServerCertificateAction.setCertTimeoutConfig(certSetField.getText());
        }

        currentLevel = systemConfigAction.loglevel();
        if (!level.equals(currentLevel)) {
            systemConfigAction.changloglevel(level, (data) -> {
                NotificationBean notificationBean = new NotificationBean(I18NServer.toLocale(
                    "plugins_porting_log_level"), data.toString(), NotificationType.INFORMATION);
                notificationBean.setProject(CommonUtil.getDefaultProject());
                IDENotificationUtil.notificationCommon(notificationBean);
            });
            currentLevel = level;
        } else {
            Logger.info("no Request"); // 暂行处理
        }
    }

    /**
     * 重置界面方法
     */
    public void reset() {
        userLimit = systemConfigAction.userLimit();
        userTime = systemConfigAction.userTimeOut();
        userTimeField.setText(userTime.toString());
        userLimitField.setText(userLimit.toString());

        this.certSetField.setText(portingWebServerCertificateAction.getInitCertSet());

        comboBox.setSelectedItem(currentLevel);
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (action == null) {
            action = new UserManagerAction();
        }
        userLimit = systemConfigAction.userLimit();
        userTime = systemConfigAction.userTimeOut();

        // 最大在线用户数限制。
        userLimitField = new IntegerField(I18NServer.toLocale("plugins_porting_max_user_num"), USER_LIMIT_MIN_NUM,
            USER_LIMIT_MAX_NUM);
        userLimitField.setCanBeEmpty(false);
        if (userLimit != null) {
            userLimitField.setText(userLimit.toString());
        }

        userLimitField.setToolTipText(I18NServer.toLocale("plugins_porting_tips_num_modify"));
        CardLayout layout = new CardLayout(10, 10);
        JPanel userLimitPanel = new JPanel(layout);
        userLimitPanel.add(userLimitField);

        // 用户超时登录现在
        userTimeField = new IntegerField(I18NServer.toLocale("plugins_porting_title_timeout"), USER_TIMEOUT_MIN_NUM,
            USER_TIMEOUT_MAX_NUM);
        userTimeField.setCanBeEmpty(false);
        if (userTime != null) {
            userTimeField.setText(userTime.toString());
        }
        userTimeField.setToolTipText(I18NServer.toLocale("plugins_porting_tips_timeout"));
        JPanel userTimePanel = new JPanel(layout);
        userTimePanel.add(userTimeField);

        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JLabel(I18NServer.toLocale("plugins_porting_max_user_num")), userLimitPanel)
            .addLabeledComponent(new JLabel(I18NServer.toLocale("plugins_porting_title_timeout")), userTimePanel);

        addCertSet(layout, mainFormBuilder);

        addLogLevel(layout, mainFormBuilder);

        mainPanel = mainFormBuilder.addComponentFillVertically(panel, 0).getPanel();
    }

    private void addLogLevel(CardLayout layout, FormBuilder mainFormBuilder) {
        currentLevel = systemConfigAction.loglevel();
        level = currentLevel;
        levelTip = new JLabel(PortingUserManageConstant.TERM_LOGLEVEL);
        String[] listData = new String[] {"ERROR", "WARNING", "INFO", "DEBUG"};
        ComboBoxModel<String> model = new DefaultComboBoxModel(listData);
        comboBox = new ComboBox();
        comboBox.setModel(model);
        JPanel comboBoxPanel = new JPanel(layout);
        comboBoxPanel.add(comboBox);
        for (int i = 0; i < listData.length; i++) {
            if (listData[i].equals(level)) {
                // 设置默认选中的条目
                comboBox.setSelectedIndex(i);
                break;
            }
        }
        mainFormBuilder.addLabeledComponent(this.levelTip, comboBoxPanel);
    }

    private void addCertSet(CardLayout layout, FormBuilder mainFormBuilder) {
        // 只有管理员登录才配置证书阈值
        this.certSetPanel = new JPanel(layout);
        this.certSetLabel = new JLabel(I18NServer.toLocale("plugins_porting_certificate_config"));
        Integer certTimeout = portingWebServerCertificateAction.getCertTimeoutConfig();
        this.certSetField = new IntegerField(I18NServer.toLocale("plugins_porting_certificate_config"), MIN, MAX);
        this.certSetField.setText(certTimeout.toString());
        this.certSetField.setToolTipText(I18NServer.toLocale("plugins_porting_certificate_tips_num_modify"));
        this.certSetPanel.add(certSetField);
        mainFormBuilder.addLabeledComponent(this.certSetLabel, certSetPanel);
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
            action = new SystemConfigAction();
        }

        comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                if (comboBox.getSelectedItem() instanceof String) {
                    level = (String) comboBox.getSelectedItem();
                }
            }
        });
    }

    private void notifyInfo(String name, String value, NotificationType type) {
        NotificationBean notificationBean = new NotificationBean(name, value, type);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    private boolean changeLimit(String limit) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        systemConfigAction.changUserLimit(Integer.valueOf(limit), (data) -> {
            if (data instanceof ResponseBean) {
                ResponseBean res = (ResponseBean) data;
                switch (res.getStatus()) {
                    case "0":
                        notifyInfo(I18NServer.toLocale("plugins_porting_max_user_num"), CommonUtil.getRspTipInfo(res),
                            NotificationType.INFORMATION);
                        succeeded.set(true);
                        break;
                    default:
                        notifyInfo(I18NServer.toLocale("plugins_porting_max_user_num"),
                            CommonUtil.getRspTipInfo(res), NotificationType.ERROR);
                        userLimitField.setText(userLimit.toString());
                        break;
                }
            }
        });
        return succeeded.get();
    }

    private boolean changeTime(String time) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        systemConfigAction.changUserTimeOut(Integer.valueOf(time), (data) -> {
            if (data instanceof ResponseBean) {
                ResponseBean res = (ResponseBean) data;
                switch (res.getStatus()) {
                    case "0":
                        notifyInfo(I18NServer.toLocale("plugins_porting_title_timeout"), CommonUtil.getRspTipInfo(res),
                            NotificationType.INFORMATION);
                        succeeded.set(true);
                        break;
                    default:
                        notifyInfo(I18NServer.toLocale("plugins_porting_title_timeout"), CommonUtil.getRspTipInfo(res),
                            NotificationType.ERROR);
                        userTimeField.setText(userTime.toString());
                        break;
                }
            }
        });
        return succeeded.get();
    }
}

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
import com.intellij.ui.components.fields.IntegerField;

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
 * The class SystemSettingPanel
 *
 * @since v2.2.T4
 */
public class SystemSettingPanel extends IDEBasePanel {
    /**
     * ?????????????????????????????????
     */
    private static final String MAX_USER_NUM_TIPS = I18NServer.toLocale("plugins_porting_max_user_num");

    private JPanel mainPanel;

    private JLabel userTimeLabel;

    private IntegerField userTimeField;

    private JLabel certSetLabel;

    private IntegerField certSetField;

    private JLabel logLevelLabel;

    private JComboBox comboBox;

    private JLabel userLimitLabel;

    private IntegerField userLimitField;

    private JLabel crlLabel;

    private JComboBox crlConfigComboBox;

    /**
     * ?????????
     */
    private SystemConfigAction systemConfigAction = new SystemConfigAction();

    private Integer userLimit;

    private Integer userTime;

    private PortingWebServerCertificateAction portingWebServerCertificateAction =
        new PortingWebServerCertificateAction();

    private String level;

    // ????????????????????????????????????????????????????????????????????????
    private String currentLevel;

    private int curIsCRLConfig;


    /**
     * ????????????
     */
    public SystemSettingPanel() {
        // ???????????????
        initPanel(mainPanel);
        registerComponentAction();
    }


    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (action == null) {
            action = new UserManagerAction();
        }
        initLabelAndBasicData();
        addCertSet();
        addLogLevel();
        fillCrlConfig();
    }

    private void initLabelAndBasicData() {
        initLabels();
        initData();
    }

    private void addCertSet() {
        Integer certTimeout = portingWebServerCertificateAction.getCertTimeoutConfig();
        this.certSetField.setText(certTimeout.toString());
        this.certSetField.setValueName(I18NServer.toLocale("plugins_porting_certificate_config"));
    }

    private void addLogLevel() {
        currentLevel = systemConfigAction.loglevel();
        level = currentLevel;

        String[] listData = new String[] {"ERROR", "WARNING", "INFO", "DEBUG"};
        ComboBoxModel<String> model = new DefaultComboBoxModel(listData);
        comboBox.setModel(model);
        for (int i = 0; i < listData.length; i++) {
            if (listData[i].equals(level)) {
                // ???????????????????????????
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void fillCrlConfig() {
        curIsCRLConfig = systemConfigAction.getCurCRLConfig() ? 1 : 0;
        String[] isCRLConfigOpts = new String[] {I18NServer.toLocale("plugins_common_porting_no"),
            I18NServer.toLocale("plugins_common_porting_yes")};

        ComboBoxModel<String> model = new DefaultComboBoxModel<>(isCRLConfigOpts);
        crlConfigComboBox.setModel(model);
        crlConfigComboBox.setSelectedIndex(curIsCRLConfig);
    }

    /**
     * ???????????????
     */
    private void initLabels() {
        userLimitLabel.setText(MAX_USER_NUM_TIPS);
        userTimeLabel.setText(I18NServer.toLocale("plugins_porting_title_timeout"));
        certSetLabel.setText(I18NServer.toLocale("plugins_porting_certificate_config"));
        logLevelLabel.setText(PortingUserManageConstant.TERM_LOGLEVEL);
        crlLabel.setText(I18NServer.toLocale("plugins_porting_setting_sys_config_crl_check"));
    }


    private void initData() {
        userLimitField.setCanBeEmpty(false);
        userTimeField.setCanBeEmpty(false);

        userTime = systemConfigAction.userTimeOut();
        userLimit = systemConfigAction.userLimit();
        if (userLimit == null || userTime == null) {
            return;
        }
        userLimitField.setText(userLimit.toString());
        userTimeField.setText(userTime.toString());

        //
        userLimitField.setValueName(I18NServer.toLocale("plugins_porting_max_user_num"));
        userTimeField.setValueName(I18NServer.toLocale("plugins_porting_title_timeout"));
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
     * ??????????????????
     *
     * @return ??????
     */
    public boolean isModified() {
        return isUserLimitChange() || isCertSetField() || isLogLevelChange() || isCRLConfigModify();
    }

    /**
     * ???????????????????????????
     *
     * @return ???????????????????????????
     */
    private boolean isUserLimitChange() {
        String guiUserLimit = userLimitField.getText();
        String guiUserTime = userTimeField.getText();
        return !Objects.equals(userTime.toString(), guiUserTime)
            || !Objects.equals(userLimit.toString(), guiUserLimit);
    }

    /**
     * ???????????????????????????
     *
     * @return ???????????????????????????
     */
    private boolean isLogLevelChange() {
        return !Objects.equals(currentLevel, level);
    }

    /**
     * ????????????????????????????????????
     *
     * @return boolean
     */
    private boolean isCertSetField() {
        return !Objects.equals(portingWebServerCertificateAction.getInitCertSet(), this.certSetField.getText());
    }


    private boolean isCRLConfigModify() {
        return curIsCRLConfig != crlConfigComboBox.getSelectedIndex();
    }

    /**
     * ???????????????
     *
     * @throws ConfigurationException ???????????????
     */
    public void apply() throws ConfigurationException {
        // ??????
        validateInputCont();
        // ??????
        modifySysConfig();
    }

    /**
     * ??????????????????
     */
    public void reset() {
        userLimit = systemConfigAction.userLimit();
        userTime = systemConfigAction.userTimeOut();
        userTimeField.setText(userTime.toString());
        userLimitField.setText(userLimit.toString());

        this.certSetField.setText(portingWebServerCertificateAction.getInitCertSet());
        comboBox.setSelectedItem(currentLevel);
        crlConfigComboBox.setSelectedIndex(curIsCRLConfig);
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        registerComponentAction();
    }

    /**
     * ??????????????????
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

    /**
     * ?????????????????????
     */
    private void modifySysConfig() {
        modifyUserLimit();
        modifyTokenValidPeriod();
        modifyCertWarningThreshold();
        modifyLogLevel();
        modifyCRLConfig();
    }

    /**
     * ???????????????????????????
     */
    private void modifyUserLimit() {
        if (userLimitField.getText().equals(userLimit.toString())) {
            return;
        }
        if (changeLimit(userLimitField.getText())) {
            userLimit = Integer.valueOf(userLimitField.getText());
        }
    }

    /**
     * ????????????????????????
     */
    private void modifyTokenValidPeriod() {
        // ????????????????????????
        if (userTimeField.getText().equals(userTime.toString())) {
            return;
        }
        if (changeTime(userTimeField.getText())) {
            userTime = Integer.valueOf(userTimeField.getText());
        }
    }

    /**
     * ????????????????????????
     */
    private void modifyCertWarningThreshold() {
        if (!isCertSetField()) {
            Logger.info("CertSetField not provided.");
        } else {
            portingWebServerCertificateAction.setCertTimeoutConfig(certSetField.getText());
        }
    }

    /**
     * ??????????????????
     */
    private void modifyLogLevel() {
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
            Logger.info("no Request");
        }
    }

    /**
     * ??????????????????????????????
     */
    private void modifyCRLConfig() {
        if (crlConfigComboBox.getSelectedIndex() == curIsCRLConfig) {
            return;
        }
        systemConfigAction.updateCRLConfig(crlConfigComboBox.getSelectedIndex(), data -> {
            // ????????????????????????????????????????????????
            if (!(data instanceof ResponseBean)) {
                return;
            }
            // ???????????????????????????
            NotificationType type = NotificationType.ERROR;
            if ("0".equals(((ResponseBean) data).getStatus())) {
                curIsCRLConfig = crlConfigComboBox.getSelectedIndex();
                type = NotificationType.INFORMATION;
            } else {
                crlConfigComboBox.setSelectedIndex(curIsCRLConfig);
            }
            IDENotificationUtil.notifyInfo("", CommonUtil.getRspTipInfo((ResponseBean) data), type);
        });
    }

    private boolean changeTime(String time) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        systemConfigAction.changUserTimeOut(Integer.valueOf(time), (data) -> {
            if (data instanceof ResponseBean) {
                ResponseBean res = (ResponseBean) data;
                callbackFun(succeeded, userTimeField, userTime, res);
            }
        });
        return succeeded.get();
    }

    private boolean changeLimit(String limit) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        systemConfigAction.changUserLimit(Integer.valueOf(limit), (data) -> {
            if (data instanceof ResponseBean) {
                ResponseBean res = (ResponseBean) data;
                callbackFun(succeeded, userLimitField, userLimit, res);
            }
        });
        return succeeded.get();
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param succeed succeed
     * @param field   field
     * @param data    data
     * @param res     res
     */
    private void callbackFun(AtomicBoolean succeed, IntegerField field, Integer data, ResponseBean res) {
        NotificationType type = NotificationType.INFORMATION;
        if ("0".equals(res.getStatus())) {
            succeed.set(true);
        } else {
            type = NotificationType.ERROR;
            field.setText(data.toString());
        }
        IDENotificationUtil.notifyInfo("", CommonUtil.getRspTipInfo(res), type);
    }

    /**
     * ??????????????????
     *
     * @throws ConfigurationException
     */
    private void validateInputCont() throws ConfigurationException {
        validateTemplate(this.userLimitField, I18NServer.toLocale("plugins_porting_tips_num_modify"));
        validateTemplate(this.userTimeField, I18NServer.toLocale("plugins_porting_tips_timeout"));
        validateTemplate(this.certSetField, I18NServer.toLocale("plugins_porting_certificate_tips_num_modify"));
    }

    private void validateTemplate(IntegerField field, String tips) throws ConfigurationException {
        try {
            field.validateContent();
        } catch (ConfigurationException e) {
            throw new ConfigurationException(tips);
        }
    }


}

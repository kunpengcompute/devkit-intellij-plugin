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

package com.huawei.kunpeng.intellij.ui.panel;

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;

import com.huawei.kunpeng.intellij.common.constant.CSSConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.ServerConfigAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * ?????????????????????
 *
 * @since 2020-09-25
 */
public abstract class ServerConfigPanel extends IDEBasePanel {
    private static final int EXTRA_WIDTH = 18;

    /**
     * ?????????
     */
    public JPanel mainPanel;

    /**
     * descriptionLabel2
     */
    protected JLabel descriptionLabel2;
    private JPanel initPanel;
    private JPanel descriptionPanel;
    private JLabel descriptionLabel1;
    private JPanel ipPanel;
    private JLabel ipLabel;
    private JTextField ipField;
    private JPanel portPanel;
    private JTextField portField;
    private JLabel portLabel;

    private ButtonGroup certRadioButtonGroup;

    private JLabel certLabel;

    private JLabel certTipLabel;

    private JRadioButton noneCertRadioButton;

    private JRadioButton useCertRadioButton;

    private JButton certFileChooseButton;

    private JPanel certPanel;

    private JPanel certFilePanel;

    private JTextField certFileField;
    private JLabel spaceLabel;
    private JPanel iconLabe;

    private JFileChooser jfc = new JFileChooser(new File("."));

    /**
     * ??????????????????????????????????????????
     *
     * @param toolWindow  ?????????toolWindow??????????????????
     * @param panelName   ????????????
     * @param displayName ????????????title
     * @param isLockable  isLockable
     */
    public ServerConfigPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.SERVER_CONFIG.panelName() : panelName;

        // ???????????????
        initPanel(mainPanel);
        // ??????????????????????????????
        registerComponentAction();
        // ?????????content??????
        createContent(mainPanel, StringUtil.stringIsEmpty(displayName) ? CommonI18NServer.toLocale(
                "common_config_title") : displayName, isLockable);
    }

    /**
     * ???toolWindow???displayName???????????????
     *
     * @param toolWindow  toolWindow
     * @param displayName ??????????????????
     * @param isLockable  isLockable
     */
    public ServerConfigPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * ???toolWindow???????????????,????????????????????????
     *
     * @param toolWindow toolWindow
     */
    public ServerConfigPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);

        setLabel();
        initRadioButtonShow();
        certButtonChange();
    }

    private void initRadioButtonShow() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        String certPath = null;
        if (config.get(ConfigProperty.CERT_PATH.vaLue()) instanceof String) {
            certPath = (String) config.get(ConfigProperty.CERT_PATH.vaLue());
        }
        if (Objects.isNull(certPath)) {
            certFileChooseButton.setVisible(true);
            useCertRadioButton.setSelected(true);
            certFileField.setVisible(true);
        }
        if ("".equals(certPath)) {
            noneCertRadioButton.setSelected(true);
            useCertRadioButton.setSelected(false);
            certFileChooseButton.setVisible(false);
            certFileField.setVisible(false);
        }
        if (!StringUtil.stringIsEmpty(certPath)) {
            certFileField.setVisible(true);
            noneCertRadioButton.setSelected(false);
            useCertRadioButton.setSelected(true);
            certFileChooseButton.setVisible(true);
            certFileField.setVisible(true);
            certFileField.setText(certPath);
        }
    }

    private void certButtonChange() {
        noneCertRadioButton.addItemListener(event -> {
            if (noneCertRadioButton.isSelected()) {
                certFileChooseButton.setVisible(false);
                certFileField.setText(null);
                certFileField.setVisible(false);
            }
        });
        useCertRadioButton.addItemListener(event -> {
            if (useCertRadioButton.isSelected()) {
                Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
                String certPath = "";
                if (config.get(ConfigProperty.CERT_PATH.vaLue()) instanceof String) {
                    certPath = (String) config.get(ConfigProperty.CERT_PATH.vaLue());
                }
                certFileField.setText(certPath);
                certFileChooseButton.setVisible(true);
                certFileField.setVisible(true);
            }
        });
        certFileChooseButton.addActionListener(event -> {
            int status = jfc.showOpenDialog(this);
            if (status == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = jfc.getSelectedFile();
                    String filePath = file.getCanonicalPath();
                    certFileField.setText(filePath);
                } catch (IOException e) {
                    Logger.error("certFileChooseButton IOException");
                }
            }
        });
    }

    /**
     * ??????????????????
     */
    private void setLabel() {
        readConfig();
        initPanel.setPreferredSize(new Dimension(780, 115));
        descriptionPanel.setPreferredSize(new Dimension(780, 30));
        descriptionLabel1.setIcon(ICON_INFO_ICON);
        descriptionLabel1.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
        descriptionLabel1.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????
        descriptionLabel1.setText(getToolConfigDescription());
        ipLabel.setText(CommonI18NServer.toLocale("common_config_address"));
        portLabel.setText(CommonI18NServer.toLocale("common_config_port"));
        // ???descriptionLabel2????????????????????????
        descriptionLabel2.setText(WeakPwdConstant.CLICK_DEPLOY);
        descriptionLabel2.setForeground(CSSConstant.RESET_DELETE_LABEL_COLOR);
        descriptionLabel2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        String hintText = getDefaultServerPort();
        portField.setText(hintText);
        portField.setForeground(Color.GRAY);
        addPortFieldListener(hintText);

        certFileField.setEditable(false);
        certRadioButtonGroup = new ButtonGroup();
        certRadioButtonGroup.add(noneCertRadioButton);
        certRadioButtonGroup.add(useCertRadioButton);
        certTipLabel.setIcon(BaseIntellijIcons.Settings.CERT_TIPS);
        certTipLabel.setToolTipText(CommonI18NServer.toLocale("common_setting_cert_tip"));
        certLabel.setText(CommonI18NServer.toLocale("common_setting_cert") + " ");
        noneCertRadioButton.setText(CommonI18NServer.toLocale("common_setting_radio_no_cert"));
        useCertRadioButton.setText(CommonI18NServer.toLocale("common_setting_radio_use_cert"));
        certFileChooseButton.setText(CommonI18NServer.toLocale("common_setting_radio_cert_button"));
        int labelWidth = certLabel.getFontMetrics(certLabel.getFont()).stringWidth(certLabel.getText())
                + certTipLabel.getIcon().getIconWidth() + EXTRA_WIDTH;
        ipLabel.setPreferredSize(new Dimension(labelWidth, -1));
        portLabel.setPreferredSize(new Dimension(labelWidth, -1));
        spaceLabel.setPreferredSize(new Dimension(labelWidth, -1));
    }

    private void addPortFieldListener(String hintText) {
        portField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                String temp = portField.getText();
                if (temp.equals(hintText)) {
                    portField.setText("");
                    portField.setForeground(portLabel.getForeground());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (portField == null) {
                    return;
                }
                String temp = portField.getText();
                if (("").equals(temp)) {
                    portField.setForeground(Color.GRAY);
                    portField.setText(hintText);
                }
            }
        });
    }

    /**
     * ?????????????????????
     *
     * @return string ???????????????
     */
    protected abstract String getToolConfigDescription();

    /**
     * ?????????????????????
     *
     * @return string ??????
     */
    protected abstract String getDefaultServerPort();

    /**
     * ????????????
     */
    private void readConfig() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        List portConfig = JsonUtil.getValueIgnoreCaseFromMap(config, ConfigProperty.PORT_CONFIG.vaLue(), List.class);
        if (!ValidateUtils.isEmptyCollection(portConfig)) {
            if (portConfig.get(0) instanceof Map) {
                Map configDef = (Map) portConfig.get(0);
                String ip = JsonUtil.getValueIgnoreCaseFromMap(configDef, "ip", String.class);
                String port = JsonUtil.getValueIgnoreCaseFromMap(configDef, "port", String.class);
                ipField.setText(ip);
                portField.setText(port);
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param component JLabel??????
     * @return textLength ??????????????????
     */
    private int getTextLength(JLabel component) {
        return component.getFontMetrics(component.getFont()).stringWidth(component.getText());
    }

    /**
     * ??????????????????
     */
    @Override
    protected void registerComponentAction() {
        customizeRegisterAction();
    }

    /**
     * ?????????????????????
     */
    protected abstract void customizeRegisterAction();

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof ServerConfigAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @return Map
     */
    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ip", ipField.getText());
        params.put("port", portField.getText());
        params.put("noCertFlag", noneCertRadioButton.isSelected() ? "true" : "false");
        params.put("useCertFlag", useCertRadioButton.isSelected() ? "true" : "false");
        params.put("certFile", certFileField.getText());
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("param", params);

        return result;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();

        // IP????????????
        if (!CheckedUtils.checkIp(ipField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_message_ipError"), ipField));
        }

        // ??????????????????
        if (!CheckedUtils.checkPort(portField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_message_portError"), portField));
        }

        // ????????????????????????
        if (useCertRadioButton.isSelected() && ValidateUtils.isEmptyString(certFileField.getText())) {
            result.add(new ValidationInfo(
                    CommonI18NServer.toLocale("common_setting_field_not_choose_cert"), certFileField));
        }

        return result;
    }

    @Override
    public void clearPwd() {
        if (ipField != null) {
            ipField.setText("");
            ipField = null;
        }

        if (portField != null) {
            portField.setText("");
            portField = null;
        }
    }
}

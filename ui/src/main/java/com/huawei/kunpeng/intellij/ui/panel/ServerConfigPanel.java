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

import com.huawei.kunpeng.intellij.common.constant.CSSConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.InstallConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.*;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.ServerConfigAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;

/**
 * 服务器配置面板
 *
 * @since 2020-09-25
 */
public abstract class ServerConfigPanel extends IDEBasePanel {
    private static final int EXTRA_WIDTH = 18;

    /**
     * 主面板
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
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public ServerConfigPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.SERVER_CONFIG.panelName() : panelName;

        // 初始化面板
        initPanel(mainPanel);
        // 初始化面板内组件事件
        registerComponentAction();
        // 初始化content实例
        createContent(mainPanel, StringUtil.stringIsEmpty(displayName) ? CommonI18NServer.toLocale(
                "common_config_title") : displayName, isLockable);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示名称
     * @param isLockable  isLockable
     */
    public ServerConfigPanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public ServerConfigPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
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
     * 设置初始面板
     */
    private void setLabel() {
        readConfig();
        initPanel.setPreferredSize(new Dimension(780, 115));
        descriptionPanel.setPreferredSize(new Dimension(780, 30));
        descriptionLabel1.setIcon(ICON_INFO_ICON);
        descriptionLabel1.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        descriptionLabel1.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
        descriptionLabel1.setText(getToolConfigDescription());
        ipLabel.setText(CommonI18NServer.toLocale("common_config_address"));
        portLabel.setText(CommonI18NServer.toLocale("common_config_port"));
        // 在descriptionLabel2组件中追加超链接
        descriptionLabel2.setText(InstallConstant.CLICK_DEPLOY);
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
            public void focusGained(FocusEvent event) {
                String temp = portField.getText();
                if (temp.equals(hintText)) {
                    portField.setText("");
                    portField.setForeground(portLabel.getForeground());
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
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
     * 自定义工具描述
     *
     * @return string 国际化描述
     */
    protected abstract String getToolConfigDescription();

    /**
     * 默认服务器端口
     *
     * @return string 端口
     */
    protected abstract String getDefaultServerPort();

    /**
     * 读取配置
     */
    private void readConfig() {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        List portConfig = JsonUtil.getValueIgnoreCaseFromMap(config, ConfigProperty.PORT_CONFIG.vaLue(), List.class);
        if (!ValidateUtils.isEmptyCollection(portConfig)) {
            if (portConfig.get(0) instanceof Map) {
                Map configDef = (Map) portConfig.get(0);
                String ip = JsonUtil.getValueIgnoreCaseFromMap(configDef, "ip", String.class);
                String port = JsonUtil.getValueIgnoreCaseFromMap(configDef, "port", String.class);
                ipField.setText("");
                portField.setText(port);
            }
        }
    }

    /**
     * 获取当前文本
     *
     * @param component JLabel组件
     * @return textLength 当前文本长度
     */
    private int getTextLength(JLabel component) {
        return component.getFontMetrics(component.getFont()).stringWidth(component.getText());
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        customizeRegisterAction();
    }

    /**
     * 自定义事件注册
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
     * 获取服务器配置信息参数
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
        Random random = new Random();
        random.setSeed(10000L);
        int randomPort = random.nextInt(10240) + 45295;
        // 随机寻找一个未被占用的端口
        while (IDENetUtils.isLocalePortUsing(randomPort)) {
            randomPort = random.nextInt(10240) + 45295;
        }
        params.put("localPort", randomPort + "");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("param", params);

        return result;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = new ArrayList<>();

        // IP校验处理
        if (!CheckedUtils.checkIp(ipField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_message_ipError"), ipField));
        }

        // 端口校验处理
        if (!CheckedUtils.checkConfigPort(portField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("common_message_portError"), portField));
        }

        // 校验是否选中证书
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

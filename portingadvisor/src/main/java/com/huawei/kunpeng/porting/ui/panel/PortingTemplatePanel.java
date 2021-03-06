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

package com.huawei.kunpeng.porting.ui.panel;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.template.PortingTemplateAction;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * ?????????????????????
 *
 * @since 2020-10-12
 */
public class PortingTemplatePanel extends IDEBasePanel {
    /**
     * ??????
     */
    public static final String UPGRADE = "upgrade";

    /**
     * ??????
     */
    public static final String RESTORE = "recovery";

    /**
     * *?????????????????????
     */
    private static final String RED_STAR_PATH = "/assets/img/settings/redstar.png";

    /**
     * ???????????????????????????????????????
     */
    private static final String PACKAGES_HREF = I18NServer.toLocale("plugins_porting_package_herf");

    /**
     * ??????????????????
     */
    public boolean isUploaded = false;

    private PortingTemplateAction portingTemplateAction;

    private JPanel mainPanel;

    private JPanel upgradePanel;

    private JPanel restorationPanel;

    private JRadioButton upgradeRadioButton;

    private TextFieldWithBrowseButton packUploadField;

    private JButton uploadBtn;

    private JRadioButton restorationRadioButton;

    private JLabel upgradeDesc;

    private JLabel upgradePackLabel;

    private JLabel restorationDesc;

    private JPasswordField upgradePasswdField;

    private JPasswordField restorationPasswdField;

    private JLabel upgradePasswdLabel;

    private JLabel restorationPasswdLabel;

    private JEditorPane editorPane;

    private JPanel htmlPanel;

    private JLabel eyeUpgrade;

    private JLabel eyeRset;

    private JPanel upLinePanel;

    private JPanel JPanel;

    private JPanel resetLinePanel;

    private boolean isHighlight;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * ????????????
     */
    public PortingTemplatePanel() {
        initPanel(mainPanel);
        // ??????????????????????????????
        registerComponentAction();
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);

        // upgrade
        upgradeRadioButton.setText(I18NServer.toLocale("plugins_porting_common_button_upgrade"));
        upgradePackLabel.setText(I18NServer.toLocale("plugins_porting_label_software_porting_template_package"));
        upgradePackLabel.setToolTipText(I18NServer.toLocale("plugins_porting_label_software_porting_template_package"));
        upgradePasswdLabel.setText(I18NServer.toLocale("plugins_common_label_adminPassword"));
        uploadBtn.setText(I18NServer.toLocale("plugins_porting_common_button_upload"));

        // ???????????????
        packUploadField.setButtonIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        registerBrowseDialog(packUploadField,
            I18NServer.toLocale("plugins_porting_label_software_porting_template_upload"));
        eyeUpgrade.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        // restore
        restorationRadioButton.setText(I18NServer.toLocale("plugins_porting_common_button_restoration"));
        restorationDesc.setText(I18NServer.toLocale("plugins_porting_tip_software_porting_template_restoreDesc"));
        restorationPasswdLabel.setText(I18NServer.toLocale("plugins_common_label_adminPassword"));
        eyeRset.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));

        addRedStars();

        ItemListener operationTypeChangedListener = event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                enableOptions(event.getSource());
                addUpdatePanel();
            }
        };
        restorationRadioButton.addItemListener(operationTypeChangedListener);
        upgradeRadioButton.addItemListener(operationTypeChangedListener);
        upgradeRadioButton.setSelected(true);
        addUpdatePanel();

        addUploadBtnListener(); // ??????upload??????
        addDocumentListener(); // ?????????????????????
        // ??????????????????
        passwordFieldAction.registerMouseListenerTwo(eyeRset, restorationPasswdField);
        passwordFieldAction.registerMouseListener(eyeUpgrade, upgradePasswdField);
    }

    private void addUploadBtnListener() {
        uploadBtn.addActionListener(event -> {
            String localFilePath = packUploadField.getText(); // ?????????????????????????????????
            if ("".equals(localFilePath)) {
                Logger.info("Please upload porting package firstly."); // ?????????????????????????????????????????????
                IDENotificationUtil.notificationCommon(
                    new NotificationBean("",
                        I18NServer.toLocale("plugins_porting_tip_software_uploade_not_package"),
                        NotificationType.ERROR));
            } else {
                uploadFile(localFilePath);
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void addDocumentListener() {
        upgradePasswdField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                highlightState(
                    !ValidateUtils.isEmptyString(new String(upgradePasswdField.getPassword())) || isHighlight,
                    upgradePasswdField);
                isHighlight = false;
            }
        });

        restorationPasswdField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                highlightState(
                    !ValidateUtils.isEmptyString(new String(restorationPasswdField.getPassword())) || isHighlight,
                    restorationPasswdField);
                isHighlight = false;
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void addUpdatePanel() {
        StringBuilder tempSB = new StringBuilder();
        tempSB.append("<html> <body><div style=\"color:rgb({0},{1},{2}); font-family:{3};font-size:{4}\">");
        tempSB.append(I18NServer.toLocale("plugins_porting_tip_software_porting_template_upgradeDesc01"));
        tempSB.append("<a style=\"color:rgb(47, 101, 202);\"; href=").append(PACKAGES_HREF).append(">  ");
        tempSB.append(I18NServer.toLocale("plugins_porting_tip_software_porting_template_upgradeDesc02"));
        tempSB.append("   </a>");
        tempSB.append("</div> </body></html>");

        Color color = upgradePackLabel.getForeground();
        Font font = upgradePackLabel.getFont();
        String messages = MessageFormat.format(tempSB.toString(), color.getRed(), color.getGreen(), color.getBlue(),
            font.getFontName(), font.getSize());
        editorPane = new JEditorPane("text/html", messages);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        HyperlinkListener listener = new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperLink) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(hyperLink.getEventType())) {
                    return;
                }
                CommonUtil.openURI(PACKAGES_HREF);
            }
        };
        editorPane.addHyperlinkListener(listener);
        htmlPanel.removeAll(); // ?????????????????????
        htmlPanel.add(editorPane);
        editorPane.updateUI();
        IdeFocusManager.getGlobalInstance()
            .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(htmlPanel, true));
        htmlPanel.updateUI();
        mainPanel.updateUI();
    }

    /**
     * ?????????????????????????????????
     */
    private void addRedStars() {
        Icon icon = new ImageIcon(PortingTemplatePanel.class.getResource(RED_STAR_PATH));
        upgradePackLabel.setIcon(icon);
        upgradePackLabel.setDisabledIcon(icon);
        upgradePackLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
        upgradePackLabel.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????

        upgradePasswdLabel.setIcon(icon);
        upgradePasswdLabel.setDisabledIcon(icon);
        upgradePasswdLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
        upgradePasswdLabel.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????

        restorationPasswdLabel.setIcon(icon);
        restorationPasswdLabel.setDisabledIcon(icon);
        restorationPasswdLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // ?????????????????????????????????
        restorationPasswdLabel.setVerticalTextPosition(SwingConstants.CENTER); // ?????????????????????????????????

        eyeRset.setDisabledIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        eyeUpgrade.setDisabledIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
    }

    /**
     * ??????????????????
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            portingTemplateAction = new PortingTemplateAction();
        }
        passwordFieldAction.pwdDocument(upgradePasswdField);
        passwordFieldAction.pwdDocument(restorationPasswdField);
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof PortingTemplateAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * ??????mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * ??????apply????????????????????????
     *
     * @throws ConfigurationException ConfigurationException
     */
    public void apply() throws ConfigurationException {
        if (upgradeRadioButton.isSelected()) {
            String usrPassword = new String(upgradePasswdField.getPassword());
            if (!isUploaded) {
                throw new ConfigurationException(
                    I18NServer.toLocale("plugins_porting_software_porting_template_upload_tip"));
            } else if (ValidateUtils.isEmptyString(usrPassword)) {
                highlightState(false, upgradePasswdField);
                throw new ConfigurationException(I18NServer.toLocale("plugins_common_term_no_password"));
            } else {
                portingTemplateAction.portingTemplateManagement(UPGRADE, usrPassword);
            }
        } else if (restorationRadioButton.isSelected()) {
            String usrPassword = new String(restorationPasswdField.getPassword());
            if (ValidateUtils.isEmptyString(usrPassword)) {
                highlightState(false, restorationPasswdField);
                throw new ConfigurationException(I18NServer.toLocale("plugins_common_term_no_password"));
            } else {
                portingTemplateAction.portingTemplateManagement(RESTORE, usrPassword);
            }
        } else {
            Logger.info("Bad Request"); // ????????????
        }

        isHighlight = true;
    }

    /**
     * ??????????????????
     */
    public void reset() {
        upgradeRadioButton.setSelected(true);
        upgradePasswdField.setText("");
        restorationPasswdField.setText("");
        packUploadField.setText("");
        isUploaded = false;
        isHighlight = true;
        highlightState(true, upgradePasswdField);
        highlightState(true, restorationPasswdField);
    }

    /**
     * ??????Apply????????????????????????
     *
     * @return boolean
     */
    public boolean isModified() {
        String upgradePassword = new String(upgradePasswdField.getPassword());
        String recoveryPassword = new String(restorationPasswdField.getPassword());
        return !upgradeRadioButton.isSelected() || !ValidateUtils.isEmptyString(upgradePassword)
                || !ValidateUtils.isEmptyString(recoveryPassword)
                || !ValidateUtils.isEmptyString(packUploadField.getText());
    }

    /**
     * ?????????????????????????????????
     *
     * @param passwordField ?????????????????????
     * @return boolean
     */
    public boolean isPasswordFilled(JPasswordField passwordField) {
        String usrPassword = new String(passwordField.getPassword());
        return !"".equals(usrPassword);
    }

    private <T> void enableOptions(T source) {
        UIUtil.setEnabled(restorationPanel, restorationRadioButton.equals(source), true);
        UIUtil.setEnabled(upgradePanel, upgradeRadioButton.equals(source), true);
        if (restorationRadioButton.equals(source)) {
            upgradeRadioButton.setSelected(false);
        } else if (upgradeRadioButton.equals(source)) {
            restorationRadioButton.setSelected(false);
        } else {
            Logger.info("Abnormal scenario."); // ????????????
        }
    }

    private void registerBrowseDialog(@NotNull TextFieldWithBrowseButton component,
        @NlsContexts.DialogTitle @NotNull String dialogTitle) {
        component.addBrowseFolderListener(dialogTitle, null, null,
            FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    /**
     * ??????????????????????????????
     *
     * @param filePath ??????????????????
     */
    public void uploadFile(String filePath) {
        if (!FileUtil.isValidateFile(filePath, 500)) {
            return;
        }
        File file = new File(filePath);
        portingTemplateAction.uploadButtonClicked(this, file, file.length());
    }

    @Override
    public void clearPwd() {
        if (upgradePasswdField != null) {
            upgradePasswdField.setText("");
        }

        if (restorationPasswdField != null) {
            restorationPasswdField.setText("");
        }
    }

    /**
     * ???????????????
     *
     * @param isValid    isValid
     * @param jTextField jTextField
     */
    private void highlightState(boolean isValid, JTextField jTextField) {
        jTextField.putClientProperty("JComponent.outline", isValid ? null : "error");
    }
}

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

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.UninstallWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.ToolWindow;

import org.jetbrains.annotations.NotNull;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * ????????????
 *
 * @since 2020-10-12
 */
public class UninstallPanel extends IDEBasePanel {
    private static final int FILE_NOT_EXIST = -1;

    private static final String SSH_DEFAULT_PORT = "22";

    /**
     * ???????????????????????????
     */
    public boolean isUploaded = false;

    private JPanel mainPanel;

    private JPanel dscPanel;

    private JLabel dscLabel;

    private JPanel ipPanel;

    private JLabel ipLabel;

    private JPanel portPanel;

    private JLabel portLabel;

    private JPanel userPanel;

    private JLabel userLabel;

    private JLabel pwdLabel;

    private JPasswordField userField;

    private JTextField portField;

    private JTextField ipField;

    private JPasswordField pwdField;

    private JPanel pwdPanel;

    private JPanel modePanel;

    private JLabel modeLabel;

    private JRadioButton pwdRadioButton;

    private JRadioButton privateKeyRadioButton;

    private JPanel privateKeyPanel;

    private JLabel privateKeyLabel;

    private TextFieldWithBrowseButton privateKeyUploadField;

    private JPanel descriptionPanel;

    private JLabel pwdView;

    private JPanel connect;

    private JPanel loadPanel;

    private JLabel loadGif;

    private JPanel checkPanel;

    private JButton checkConnectionButton;

    private JPanel pwdLinePanel;

    private JPasswordField privatePwdField;

    private JLabel privatePwdLabel;

    private JLabel privatePwdView;

    private JLabel info;

    private UninstallWrapDialog dialog;

    private String displayName;

    public UninstallWrapDialog getDialog() {
        return dialog;
    }

    public void setDialog(UninstallWrapDialog dialog) {
        this.dialog = dialog;
    }

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * ??????????????????????????????????????????
     *
     * @param toolWindow  ?????????toolWindow??????????????????
     * @param displayName ????????????title
     */
    public UninstallPanel(ToolWindow toolWindow, String displayName, IDEPanelBaseAction action) {
        setToolWindow(toolWindow);
        this.panelName = Panels.UNINSTALL.panelName();
        // ???????????????
        initPanel(mainPanel);
        // ??????????????????????????????
        if (this.action == null) {
            this.action = action;
        }
        registerComponentAction();
        // ?????????content??????
        this.displayName = displayName;
        createContent(mainPanel, this.displayName, false);
    }

    /**
     * ???????????????
     *
     * @return ???????????????
     */
    public JLabel getLoadGif() {
        return loadGif;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????????????????
     */
    public JButton getCheckConnectionButton() {
        return checkConnectionButton;
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        loadGif.setIcon(null);
        setLabel();
    }

    /**
     * ??????????????????
     */
    private void setLabel() {
        dscLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_uninstall_description"));
        dscLabel.setIcon(BaseIntellijIcons.load(IDEConstant.ICON_INFO));
        ipLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_install_address"));
        portLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_install_sshPort"));
        userLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_install_osUser"));
        modeLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_install_connectMode"));
        pwdRadioButton.setText(CommonI18NServer.toLocale("plugins_ui_common_install_pwdVerify"));
        privateKeyRadioButton.setText(CommonI18NServer.toLocale("plugins_ui_common_install_keyVerify"));
        pwdLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_install_osPassword"));
        privateKeyLabel.setText(CommonI18NServer.toLocale("plugins_ui_common_install_osKey"));
        privatePwdLabel.setText("   " + CommonI18NServer.toLocale("common_server_private_key_label"));
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        privatePwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        ButtonGroup group = new ButtonGroup();
        group.add(pwdRadioButton);
        group.add(privateKeyRadioButton);
        // ???????????????focus??????btn??????
        changeBtnStatus(ipField);
        changeBtnStatus(portField);
        changeBtnStatus(userField);
        changeBtnStatus(pwdField);
        ItemListener operationTypeChangedListener = event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                enableOptions(event.getSource());
            }
        };
        pwdRadioButton.addItemListener(operationTypeChangedListener);
        privateKeyRadioButton.addItemListener(operationTypeChangedListener);
        pwdRadioButton.setSelected(true);
        registerBrowseDialog(privateKeyUploadField, CommonI18NServer.toLocale("plugins_ui_common_install_dialogTitle"));
        portField.setText(SSH_DEFAULT_PORT);
        // ??????????????????
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
        passwordFieldAction.registerMouseListener(privatePwdView, privatePwdField);
    }

    private <T> void enableOptions(T source) {
        if (pwdRadioButton.equals(source)) {
            pwdPanel.setVisible(true);
            privateKeyPanel.setVisible(false);
            pwdRadioButton.setSelected(true);
            privateKeyRadioButton.setSelected(false);
            privateKeyUploadField.setText("");
        } else if (privateKeyRadioButton.equals(source)) {
            pwdPanel.setVisible(false);
            privateKeyPanel.setVisible(true);
            privateKeyRadioButton.setSelected(true);
            pwdRadioButton.setSelected(false);
            pwdField.setText("");
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
     * ??????????????????
     */
    @Override
    protected void registerComponentAction() {
        passwordFieldAction.pwdDocument(pwdField);
        userField.setEchoChar('\0');
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        this.action = action;
        registerComponentAction();
    }

    /**
     * ??????????????????????????????
     *
     * @return Map
     */
    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ip", ipField.getText());
        params.put("port", portField.getText());
        params.put("user", userField.getText());
        params.put("password", new String(pwdField.getPassword()));
        params.put("privateKey", privateKeyUploadField.getText());
        params.put("passPhrase", privatePwdField.getText() == null ? "" : privatePwdField.getText());
        params.put("displayName", displayName);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("param", params);
        return result;
    }

    /**
     * ???????????????????????????ip
     *
     * @return ip
     */
    public String getIp() {
        return ipField.getText();
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    public List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> valList = new ArrayList<>();

        // IP????????????
        if (!CheckedUtils.checkIp(ipField.getText())) {
            valList.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_ipError"), ipField));
        }

        // ??????????????????
        if (!CheckedUtils.checkPort(portField.getText())) {
            valList.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_portError"), portField));
        }

        // ?????????????????????
        if (!CheckedUtils.checkUser(userField.getText())) {
            valList.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), userField));
        }

        // ??????????????????
        if (pwdRadioButton.isSelected() && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            valList.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }

        if (privateKeyRadioButton.isSelected() && !FileUtil.checkKey(privateKeyUploadField.getText())) {
            valList.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_keyError"),
                    privateKeyUploadField));
        }

        return valList;
    }

    @Override
    public void clearPwd() {
        if (pwdField != null) {
            pwdField.setText("");
            pwdField = null;
        }

        if (ipField != null) {
            ipField.setText("");
            ipField = null;
        }

        if (portField != null) {
            portField.setText("");
            portField = null;
        }
    }

    private void changeBtnStatus(JComponent jComponent) {
        jComponent.addFocusListener(new FocusListener() {
            String oldText;

            String newText;

            @Override
            public void focusGained(FocusEvent event) {
                Object text = event.getSource();
                if (text instanceof JTextField) {
                    oldText = ((JTextField) text).getText();
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                Object text = event.getSource();
                if (text instanceof JTextField) {
                    newText = ((JTextField) text).getText();
                }
                if (ValidateUtils.isNotEmptyString(newText) && !Objects.equals(oldText, newText)) {
                    // ?????????????????????
                    dialog.actionOperate(false);
                }
            }
        });
    }
}

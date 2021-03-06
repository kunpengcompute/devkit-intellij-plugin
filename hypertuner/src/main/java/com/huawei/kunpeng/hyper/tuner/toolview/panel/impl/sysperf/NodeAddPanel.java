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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.NodeManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.NodeTipsDialog;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * ?????????????????????
 *
 * @since 2012-10-12
 */
public class NodeAddPanel extends IDEBasePanel implements FocusListener {
    /**
     * ?????????????????????
     */
    private static final String SSH_DEFAULT_PORT = "22";

    /**
     * ??????
     */
    private static final String USER_ROLE_ADMIN = "Admin";

    /**
     * ??????
     */
    private static final String ROOT_USER = "root";

    private NodeTipsDialog dialog;
    private JPanel mainPanel;
    private JTextField weakPasswdTextField;
    private JLabel weakPasswdLabel;
    private JLabel certTipLabel;
    private JTextField nodeNameField;
    private JLabel nodeNameLabel;
    private JPanel nodeNamePanel;
    private JPanel installPathPanel;
    private JLabel installPathLable;
    private JTextField installPathField;
    private JPanel SSHPanel;
    private JLabel SSHLabel;
    private JPanel ipPanel;
    private JLabel ipLabel;
    private JTextField ipField;
    private JPanel portPanel;
    private JLabel portLabel;
    private JTextField portField;
    private JPanel userPanel;
    private JLabel userLabel;
    private JTextField userField;
    private JPanel modePanel;
    private JLabel modeLabel;
    private JRadioButton pwdRadioButton;
    private JRadioButton privateKeyRadioButton;
    private JPanel privateKeyPanel;
    private JLabel privateKeyLabel;
    private JTextField privateKeyUploadField;
    private JPanel pwdPanel;
    private JLabel pwdLabel;
    private JPasswordField pwdField;
    private JLabel pwdView;
    private JLabel portNumber;
    private JPanel rootPwdPanel;
    private JLabel rootPwdLabel;
    private JPasswordField rootPwdField;
    private JLabel rootPwdView;
    private JPanel warnPanel;
    private JLabel rootTipLable;
    private JLabel iconLabel;
    private JLabel privateKeyPwdLable;
    private JPanel privateKeyPwdPanel;
    private JLabel privateKeyPwdView;
    private JPasswordField privateKeyPwdField;
    private JLabel LineLable;
    private JLabel rootPwdKeyIcon;
    private JLabel privateKeyPwdIcon;
    private JLabel pwdIcon;

    // ???????????????
    private boolean isAdminUser;
    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    // ????????????
    private String verifyType = "password";
    private String hintText;

    /**
     * ??????????????????????????????????????????
     *
     * @param panelName ????????????
     */
    public NodeAddPanel(String panelName, String paneltitle) {
        String title = NodeManagerContent.NODE_MANAGER_ADD;
        this.panelName = StringUtil.stringIsEmpty(panelName) ? title : panelName;
        title = StringUtil.stringIsEmpty(paneltitle) ? title : panelName;
        // ???????????????
        initPanel(mainPanel);
        // ???????????????????????????????????????root????????????
        setUserNameFieldListener();
        // ??????????????????????????????
        registerComponentAction();
        // ?????????content??????
        createContent(mainPanel, title, false);
    }

    public NodeAddPanel(JTextField jTextField, String hintText) {
        this.nodeNameField = jTextField;
        this.hintText = hintText;
        jTextField.setText(hintText);
        jTextField.setForeground(Color.GRAY);
    }

    public NodeTipsDialog getDialog() {
        return dialog;
    }

    public void setDialog(NodeTipsDialog dialog) {
        this.dialog = dialog;
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
        panel.setPreferredSize(new Dimension(780, 82));
        warnPanel.setPreferredSize(new Dimension(780, 20));
        isAdminUser = Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole());
    }

    @Override
    public void focusGained(FocusEvent e) {
        // ????????????????????????????????????
        String temp = nodeNameField.getText();
        if (temp.equals(hintText)) {
            nodeNameField.setText("");
            nodeNameField.setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        // ?????????????????????????????????????????????????????????
        String temp = nodeNameField.getText();
        if ("".equals(temp)) {
            nodeNameField.setForeground(Color.GRAY);
            nodeNameField.setText(hintText);
        }
    }

    /**
     * ??????????????????
     */
    private void setLabel() {
        nodeNameLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_nodename"));
        installPathLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_installpath"));
        installPathField.setText("/opt");
        SSHLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_sshmanage"));
        LineLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_about_separator1"));
        ipLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_nodeip"));
        nodeNameField.addFocusListener(
                new NodeAddPanel(nodeNameField, TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_nodeip_tip")));
        portLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_port"));
        userLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_username"));
        userField.setText(ROOT_USER);
        modeLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_verifytype"));
        pwdRadioButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_pwdVerify"));
        privateKeyRadioButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_keyVerify"));
        pwdLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_osPassword"));
        privateKeyLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_oskey"));
        privateKeyPwdLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_keyPwd"));
        rootTipLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_rootTip"));
        rootPwdLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_osRootPassword"));
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        rootPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        privateKeyPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        ButtonGroup group = new ButtonGroup();
        group.add(pwdRadioButton);
        group.add(privateKeyRadioButton);
        ItemListener operationTypeChangedListener =
                nodeAddEvent -> {
                    if (nodeAddEvent.getStateChange() == ItemEvent.SELECTED) {
                        enableOptions(nodeAddEvent.getSource());
                    }
                };
        pwdRadioButton.addItemListener(operationTypeChangedListener);
        privateKeyRadioButton.addItemListener(operationTypeChangedListener);
        pwdRadioButton.setSelected(true);

        // ???????????????
        portField.setText(SSH_DEFAULT_PORT);
        // ??????????????????
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
        passwordFieldAction.registerMouseListener(rootPwdView, rootPwdField);
        passwordFieldAction.registerMouseListener(privateKeyPwdView, privateKeyPwdField);
    }

    private <T> void enableOptions(T source) {
        if (pwdRadioButton.equals(source)) {
            pwdPanel.setVisible(true);
            pwdIcon.setVisible(true);
            privateKeyPanel.setVisible(false);
            privateKeyPwdPanel.setVisible(false);
            privateKeyPwdIcon.setVisible(false);
            pwdRadioButton.setSelected(true);
            privateKeyRadioButton.setSelected(false);
            privateKeyUploadField.setText("");
            privateKeyPwdField.setText("");
        } else if (privateKeyRadioButton.equals(source)) {
            pwdPanel.setVisible(false);
            pwdIcon.setVisible(false);
            privateKeyPanel.setVisible(true);
            privateKeyPwdPanel.setVisible(true);
            privateKeyPwdIcon.setVisible(true);
            privateKeyRadioButton.setSelected(true);
            pwdRadioButton.setSelected(false);
            pwdField.setText("");
            verifyType = "private_key";
        } else {
            Logger.info("Abnormal scenario."); // ????????????
        }
    }

    /**
     * setAction
     *
     * @param action ????????????
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof NodeManagerAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new NodeManagerAction();
        }
    }

    /**
     * ??????????????????????????????
     *
     * @return Map
     */
    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeName", nodeNameField.getText());
        params.put("installPath", installPathField.getText());
        params.put("ip", ipField.getText());
        params.put("port", portField.getText());
        params.put("user", userField.getText());
        params.put("varifyType", verifyType);
        params.put("password", new String(pwdField.getPassword()));
        params.put("privateKey", privateKeyUploadField.getText());
        params.put("privateKeyPwd", new String(privateKeyPwdField.getPassword()));
        params.put("rootPwd", new String(rootPwdField.getPassword()));

        return params;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> result = new ArrayList<>();

        if (vi != null) {
            result.add(vi);
        }
        // IP????????????
        if (!CheckedUtils.checkIp(ipField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_ipError"), ipField));
        }

        // ??????????????????
        if (!CheckedUtils.checkPort(portField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_portError"), portField));
        }

        // ?????????????????????
        if (!CheckedUtils.checkUser(userField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), userField));
        }

        // ??????????????????
        if (pwdRadioButton.isSelected() && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }

        if (!isAdminUser && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), rootPwdField));
        }

        return result;
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

    // ??????????????????????????????root?????????????????????
    private void setUserNameFieldListener() {
        warnPanel.setVisible(false);
        rootPwdPanel.setVisible(false);
        rootPwdKeyIcon.setVisible(false);
        userField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void removeUpdate(DocumentEvent event) {
                                addDocumentListener(event);
                            }

                            @Override
                            public void insertUpdate(DocumentEvent event) {
                                addDocumentListener(event);
                            }

                            @Override
                            public void changedUpdate(DocumentEvent event) {
                                addDocumentListener(event);
                            }
                        });
    }

    // ???????????????,????????????
    private void addDocumentListener(DocumentEvent documentEvent) {
        try {
            String text =
                    documentEvent.getDocument()
                            .getText(
                                    documentEvent.getDocument().getStartPosition().getOffset(),
                                    documentEvent.getDocument().getLength());
            if (text.equals(ROOT_USER)) {
                warnPanel.setVisible(false);
                rootPwdPanel.setVisible(false);
                rootPwdKeyIcon.setVisible(false);
            } else {
                warnPanel.setVisible(true);
                rootPwdPanel.setVisible(true);
                rootPwdKeyIcon.setVisible(true);
            }
        } catch (BadLocationException ex) {
            Logger.error("addDocumentListener IOException");
        }
    }
}

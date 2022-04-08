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
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
 * 添加弱口令面板
 *
 * @since 2012-10-12
 */
public class NodeDeletePanel extends IDEBasePanel {
    /**
     * 常量
     */
    private static final String USER_ROLE_ADMIN = "Admin";

    private NodeTipsDialog dialog;
    private JPanel mainPanel;
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
    private JPanel rootPwdPanel;
    private JLabel rootPwdLabel;
    private JPasswordField rootPwdField;
    private JLabel rootPwdView;
    private JPanel warnPanel;
    private JLabel rootTipLable;
    private JLabel iconLabel;
    private JPanel privateKeyPwdPanel;
    private JLabel privateKeyPwdLable;
    private JLabel privateKeyPwdView;
    private JPasswordField privateKeyPwdField;
    private JPanel ipPanel;
    private JLabel ipLable;
    private JTextField ipField;
    private JPanel portPanel;
    private JLabel portLabel;
    private JTextField portField;
    private JLabel portNumber;
    private JPanel idPanel;
    private JLabel idLabel;
    private JTextField idField;
    private JLabel privateKeyIcon;
    private JLabel rootPwdIcon;
    private JLabel portIcon;
    private JLabel pwdIcon;

    // 是否管理员
    private boolean isAdminUser;
    private String title = NodeManagerContent.NODE_MANAGER_ADD;
    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    // 认证方式
    private String verifyType = "password";
    /**
     * nodeId
     */
    private String nodeId;
    /**
     * nodeIP
     */
    private String nodeIp;
    /**
     * nodePort
     */
    private String nodePort;

    /**
     * 修改用户创建实例
     *
     * @param nodeId   nodeId
     * @param nodeIp   nodeIp
     * @param nodePort nodePort
     */
    public NodeDeletePanel(String nodeId, String nodeIp, String nodePort) {
        this.panelName = StringUtil.stringIsEmpty(panelName) ? title : panelName;
        this.nodeIp = nodeIp;
        this.nodePort = nodePort;
        this.nodeId = nodeId;
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();
        // 初始化content实例
        createContent(mainPanel, title, false);
    }

    public NodeTipsDialog getDialog() {
        return dialog;
    }

    public void setDialog(NodeTipsDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        this.mainPanel = panel;
        mainPanel.setPreferredSize(new Dimension(780, 82));
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        isAdminUser = Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole());
        // 设置用户名输入监听是否隐藏root密码输入
        setUserNameFieldListener();
        ipField.setText(nodeIp);
        portField.setText(nodePort);
        idField.setText(nodeId);
        idPanel.setVisible(false);
        portPanel.setVisible(false);
        portIcon.setVisible(false);
        ipPanel.setVisible(false);
        setLabel();
    }

    /**
     * 设置初始面板
     */
    private void setLabel() {
        userLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_username"));
        userField.setText("root");
        modeLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_verifytype"));
        pwdRadioButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_pwdVerify"));
        privateKeyRadioButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_keyVerify"));
        pwdLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_osPassword"));
        privateKeyLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_oskey"));
        privateKeyPwdLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_keyPwd"));
        rootTipLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_rootTip"));
        rootPwdLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_osRootPassword"));
        ipLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_table_nodeip"));
        portLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_table_port"));
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        rootPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        privateKeyPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        ButtonGroup group = new ButtonGroup();
        group.add(pwdRadioButton);
        group.add(privateKeyRadioButton);
        ItemListener operationTypeChangedListener =
                event -> {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        enableOptions(event.getSource());
                    }
                };
        pwdRadioButton.addItemListener(operationTypeChangedListener);
        privateKeyRadioButton.addItemListener(operationTypeChangedListener);
        pwdRadioButton.setSelected(true);

        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
        passwordFieldAction.registerMouseListener(rootPwdView, rootPwdField);
        passwordFieldAction.registerMouseListener(privateKeyPwdView, privateKeyPwdField);
    }

    private <T> void enableOptions(T source) {
        if (pwdRadioButton.equals(source)) {
            pwdPanel.setVisible(true);
            pwdIcon.setVisible(true);
            privateKeyPanel.setVisible(false);
            privateKeyIcon.setVisible(false);
            privateKeyPwdPanel.setVisible(false);
            pwdRadioButton.setSelected(true);
            privateKeyRadioButton.setSelected(false);
            privateKeyUploadField.setText("");
            privateKeyPwdField.setText("");
        } else if (privateKeyRadioButton.equals(source)) {
            pwdPanel.setVisible(false);
            pwdIcon.setVisible(false);
            privateKeyPanel.setVisible(true);
            privateKeyIcon.setVisible(true);
            privateKeyPwdPanel.setVisible(true);
            privateKeyRadioButton.setSelected(true);
            pwdRadioButton.setSelected(false);
            pwdField.setText("");
            verifyType = "private_key";
        } else {
            Logger.info("Abnormal scenario."); // 暂行处理
        }
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new NodeManagerAction();
        }
    }

    /**
     * setAction
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof NodeManagerAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 获取安装配置信息参数
     *
     * @return Map
     */
    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", userField.getText());
        params.put("varifyType", verifyType);
        params.put("password", new String(pwdField.getPassword()));
        params.put("privateKey", privateKeyUploadField.getText());
        params.put("privateKeyPwd", new String(privateKeyPwdField.getPassword()));
        params.put("rootPwd", new String(rootPwdField.getPassword()));
        params.put("ip", new String(ipField.getText()));
        params.put("port", new String(portField.getText()));
        params.put("id", new String(idField.getText()));
        return params;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> result = new ArrayList<>();

        if (vi != null) {
            result.add(vi);
        }
        // 用户名校验处理
        if (!CheckedUtils.checkUser(userField.getText())) {
            result.add(new ValidationInfo(TuningI18NServer.toLocale("plugins_common_required_tip"), userField));
        }

        // 密码校验处理
        if (pwdRadioButton.isSelected() && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(TuningI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }

        if (!isAdminUser && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(TuningI18NServer.toLocale("plugins_common_required_tip"), rootPwdField));
        }

        return result;
    }

    @Override
    public void clearPwd() {
        if (pwdField != null) {
            pwdField.setText("");
            pwdField = null;
        }
    }

    private void setUserNameFieldListener() {
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

    private void addDocumentListener(DocumentEvent event) {
        try {
            String text =
                    event.getDocument()
                            .getText(
                                    event.getDocument().getStartPosition().getOffset(),
                                    event.getDocument().getLength());
            if (("root").equals(text)) {
                warnPanel.setVisible(false);
                rootPwdPanel.setVisible(false);
                rootPwdIcon.setVisible(false);
            } else {
                warnPanel.setVisible(true);
                rootPwdPanel.setVisible(true);
                rootPwdIcon.setVisible(true);
            }
        } catch (BadLocationException ex) {
            Logger.error("addDocumentListener BadLocationException");
        }
    }
}

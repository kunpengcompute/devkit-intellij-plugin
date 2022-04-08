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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * 更新非本地节点 Agent服务证书 面板
 *
 * @since 2021-6-17
 */
public class AgentUpdatePanel extends IDEBasePanel {
    // 主面板
    private JPanel mainPanel;

    // 节点IP地址
    private JPanel ipPanel;
    private JLabel ipLabel1;

    /**
     * 用户名Panel
     */
    private JPanel userNamePanel;

    private JLabel userNameL1;
    private JTextField userField;

    /**
     * 认证方式 Panel
     */
    private JPanel authenticationModePanel;

    private JLabel authenticationModeL1;
    private JRadioButton pwdRadioButton; // 口令认证
    private JRadioButton privateKeyRadioButton; // 密钥认证

    /**
     * 密钥认证：
     * 私钥文件 Panel
     */
    private JPanel privateKeyFilePanel1;

    private JLabel privateKeyFileL1;
    private JPasswordField privateKeyFile;
    private JLabel privateKeyFileView;

    /**
     * 密钥认证：
     * 密码短语 Panel
     */
    private JPanel privateKeyPwdPanel;

    private JLabel privateKeyPwdL1;
    private JPasswordField privateKeyPwdField;
    private JLabel privateKeyPwdView;
    /**
     * 口令认证：
     * 口令Panel
     */
    private JPanel pwdPanel;

    private JLabel pwdLabel;
    private JPasswordField pwdField;
    private JLabel pwdView;
    /**
     * 提示Panel
     */
    private JPanel warnPanel;

    private JLabel rootTipLabel;
    private JLabel iconLabel;
    /**
     * root口令 面板
     */
    private JPanel rootPwdPanel;

    private JLabel rootPwdLabel;
    private JPasswordField rootPwdField;
    private JLabel rootPwdView;
    private JLabel ipLabel2;

    /**
     * 节点IP地址
     */
    private final String nodeIPAddress;
    /**
     * 节点名称 初始化传值
     */
    private final String nodeName;

    /**
     * 认证方式
     */
    private String verifyType = "password";
    /**
     * 密码操作Action
     */
    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * @param nodeIPAddress ip地址
     * @param nodeName      node_name
     */
    public AgentUpdatePanel(String nodeIPAddress, String nodeName) {
        this.panelName = StringUtil.stringIsEmpty(panelName) ? AgentCertContent.AGENT_CERT_CHANGE_TITLE : panelName;
        this.nodeIPAddress = nodeIPAddress;
        this.nodeName = nodeName;

        initPanel(mainPanel);
        registerComponentAction();
        createContent(mainPanel, AgentCertContent.AGENT_CERT_CHANGE_TITLE, false); // 初始化content实例
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        mainPanel.setPreferredSize(new Dimension(780, 82));
        // 设置用户名输入监听是否隐藏root密码输入
        setUserNameFieldListener();
        setLabel();
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 设置初始面板
     */
    private void setLabel() {
        ipPanel.setPreferredSize(new Dimension(300, 200));
        ipLabel1.setText(AgentCertContent.AGENT_CHANGE_NODE_IP);
        ipLabel2.setText(nodeIPAddress);
        userNameL1.setText(AgentCertContent.AGENT_CHANGE_USERNAME);
        userField.setText("root");

        // 认证方式
        authenticationModeL1.setText(AgentCertContent.AGENT_CHANGE_AUTH_MODE);
        pwdRadioButton.setText(AgentCertContent.AGENT_CHANGE_AUTH_MODE_PWD);
        privateKeyRadioButton.setText(AgentCertContent.AGENT_CHANGE_AUTH_MODE_KEY); // 密钥认证按钮
        privateKeyFileL1.setText(AgentCertContent.AGENT_CHANGE_AUTH_MODE_KEY_FILE); // 私钥文件
        privateKeyPwdL1.setText(AgentCertContent.AGENT_CHANGE_AUTH_MODE_KEY_PASSPHRASE); // 密码短语
        pwdLabel.setText(AgentCertContent.AGENT_CHANGE_AUTH_MODE_PWD_PASSWORD); // 口令
        setVerifyType();

        // 设置密码显示隐藏按钮 ICON
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        rootPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        privateKeyPwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        privateKeyFileView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));

        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
        passwordFieldAction.registerMouseListener(rootPwdView, rootPwdField);
        passwordFieldAction.registerMouseListener(privateKeyPwdView, privateKeyPwdField);
        passwordFieldAction.registerMouseListener(privateKeyFileView, privateKeyFile);
    }

    /**
     * 设置 认证方式 及相应监听事件
     * 切换密码/私钥认证
     */
    private void setVerifyType() {
        ButtonGroup group = new ButtonGroup();
        group.add(pwdRadioButton);
        group.add(privateKeyRadioButton);
        pwdRadioButton.setSelected(true); // 默认为 密钥认证
        privateKeyPwdPanel.setVisible(false);
        privateKeyFilePanel1.setVisible(false);
        ItemListener operationTypeChangedListener =
                event -> {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        enableOptions(event.getSource());
                    }
                };
        pwdRadioButton.addItemListener(operationTypeChangedListener);
        privateKeyRadioButton.addItemListener(operationTypeChangedListener);
        pwdRadioButton.setSelected(true);
    }

    /**
     * 切换密码认证和私钥认证
     *
     * @param source 被选中的按钮
     * @param <T>    T
     */
    private <T> void enableOptions(T source) {
        if (pwdRadioButton.equals(source)) {
            // 密码认证方式
            pwdRadioButton.setSelected(true);
            pwdPanel.setVisible(true);
            privateKeyRadioButton.setSelected(false);
            privateKeyFilePanel1.setVisible(false);
            privateKeyPwdPanel.setVisible(false);
            privateKeyFile.setText("");
            privateKeyPwdField.setText("");
        } else if (privateKeyRadioButton.equals(source)) {
            // 密钥文件认证方式
            pwdRadioButton.setSelected(false);
            pwdPanel.setVisible(false);
            pwdField.setText("");
            privateKeyRadioButton.setSelected(true);
            privateKeyFilePanel1.setVisible(true);
            privateKeyPwdPanel.setVisible(true);
            verifyType = "private_key";
        } else {
            Logger.info("doesn't match"); // 暂行处理
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
        params.put("nodeName", nodeName);
        params.put("user", userField.getText());
        params.put("varifyType", verifyType);
        params.put("password", new String(pwdField.getPassword()));
        params.put("privateKey", new String(privateKeyFile.getPassword()));
        params.put("privateKeyPwd", new String(privateKeyPwdField.getPassword()));
        params.put("rootPwd", new String(rootPwdField.getPassword()));
        params.put("ip", new String(ipLabel2.getText()));
        return params;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    @Override
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> resultList = new ArrayList<>();

        if (vi != null) {
            resultList.add(vi);
        }
        // 用户名校验处理
        if (!CheckedUtils.checkUser(userField.getText())) {
            resultList.add(new ValidationInfo(TuningI18NServer.toLocale("plugins_common_required_tip"), userField));
        }

        // 密码校验处理
        if (pwdRadioButton.isSelected() && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            resultList.add(new ValidationInfo(TuningI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }

        // 密钥路径校验
        if (privateKeyRadioButton.isSelected() && !CheckedUtils.checkPwd(new String(privateKeyFile.getPassword()))) {
            resultList.add(new ValidationInfo(TuningI18NServer.toLocale("plugins_common_required_tip"),
                    privateKeyFile));
        }
        return resultList;
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
            String text = event.getDocument()
                    .getText(event.getDocument().getStartPosition().getOffset(), event.getDocument().getLength());
            if (("root").equals(text)) {
                warnPanel.setVisible(false);
                rootPwdPanel.setVisible(false);
            } else {
                warnPanel.setVisible(true);
                rootPwdPanel.setVisible(true);
            }
        } catch (BadLocationException ex) {
            Logger.error("addDocumentListener BadLocationException.");
        }
    }
}

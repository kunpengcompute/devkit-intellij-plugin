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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.action.javaperf.GuardianManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
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

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * 添加目标环境面板
 *
 * @since 2021-07-12
 */
public class GuardianAddPanel extends IDEBasePanel implements FocusListener {
    /**
     * 密码正则表达式
     */
    private static final String SSH_DEFAULT_PORT = "22";

    private JPanel mainPanel;

    private JPanel tipPanel;
    private JLabel tipLable;

    private JPanel ipPanel;
    private JLabel ipLabel;
    private JTextField ipField;

    private JPanel portPanel;
    private JLabel portLabel;
    private JTextField portField;

    private JPanel userPanel;
    private JLabel userLabel;
    private JTextField userField;

    private JPanel pwdPanel;
    private JLabel pwdLabel;
    private JPasswordField pwdField;
    private JLabel pwdView;

    private JPanel rootTipsPanel;
    private JLabel rootTipsLabel;

    private String title = GuardianMangerConstant.GUARDIAN_ADD_TITLE;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param panelName 面板名称
     */
    public GuardianAddPanel(String panelName) {
        this.panelName = StringUtil.stringIsEmpty(panelName) ? title : panelName;
        // 初始化面板
        initPanel(mainPanel);
        // 设置用户名输入监听是否隐藏root密码输入
        setUserNameFieldListener();
        // 初始化面板内组件事件
        registerComponentAction();
        // 初始化content实例
        createContent(mainPanel, title, false);
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
        panel.setPreferredSize(new Dimension(570, 82));
        tipPanel.setPreferredSize(new Dimension(570, 20));
    }

    /**
     * 设置初始面板
     */
    private void setLabel() {
        tipLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_tip"));

        ipLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_ip"));

        portLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_port"));

        portField.setText(SSH_DEFAULT_PORT);

        userLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_user"));

        pwdLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_add_password"));

        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));

        rootTipsLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardian_rootTips"));

        rootTipsPanel.setVisible(false);

        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
    }

    /**
     * setAction
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof GuardianManagerAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new GuardianManagerAction();
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
        params.put("host", ipField.getText());
        params.put("port", portField.getText());
        params.put("username", userField.getText());
        params.put("password", new String(pwdField.getPassword()));
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
        List<ValidationInfo> result = new ArrayList<>();
        if (vi != null) {
            result.add(vi);
        }
        // IP校验处理
        if (!CheckedUtils.checkIp(ipField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_ipError"), ipField));
        }
        // 密码校验处理
        if (!CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }
        // 端口校验处理
        if (!CheckedUtils.checkPort(portField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_portError"), portField));
        }
        // 用户名校验处理
        if (!CheckedUtils.checkUser(userField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), userField));
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

        if (userField != null) {
            userField.setText("");
            userField = null;
        }
    }

    // 监听用户名，默认隐藏root密钥输入及提示
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

    // 监听用户名,处理逻辑
    private void addDocumentListener(DocumentEvent event) {
        try {
            String text = event.getDocument().getText(
                event.getDocument().getStartPosition().getOffset(),
                event.getDocument().getLength());
            if (("root").equals(text)) {
                rootTipsPanel.setVisible(true);
            } else {
                rootTipsPanel.setVisible(false);
            }
        } catch (BadLocationException ex) {
            Logger.error("addDocumentListener BadLocationException");
        }
    }

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {}
}

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
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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

/**
 * 节点管理常量定义
 *
 * @since 2020-10-15
 */
public class GuardianDeletePanel extends IDEBasePanel implements FocusListener {
    private JPanel mainPanel;

    private JPanel typePanel;
    private JLabel typeLabel;
    private JLabel certTipLabel;
    private JRadioButton allRadioButton;
    private JRadioButton partRadioButton;

    private JPanel userPanel;
    private JLabel userLabel;
    private JTextField userField;

    private JPanel pwdPanel;
    private JLabel pwdLabel;
    private JPasswordField pwdField;
    private JLabel pwdView;

    private String title;
    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    public GuardianDeletePanel(String title) {
        this.title = title;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, title, false);
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        typeLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_delete_type"));
        allRadioButton.setText(
                TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_delete_complete"));
        partRadioButton.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_delete_part"));
        certTipLabel.setIcon(BaseIntellijIcons.Settings.CERT_TIPS);
        certTipLabel.setToolTipText(GuardianMangerConstant.GUARDIAN_PARTIAL_DELETE_TIP);

        ButtonGroup group = new ButtonGroup();
        group.add(allRadioButton);
        group.add(partRadioButton);
        ItemListener operationTypeChangedListener =
                event -> {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        enableOptions(event.getSource());
                    }
                };
        allRadioButton.addItemListener(operationTypeChangedListener);
        partRadioButton.addItemListener(operationTypeChangedListener);
        userLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_userName"));
        pwdLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_password"));
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
        mainPanel.setPreferredSize(new Dimension(500, 40));
    }

    private <T> void enableOptions(T source) {
        if (allRadioButton.equals(source)) {
            allRadioButton.setSelected(true);
            partRadioButton.setSelected(false);
            pwdPanel.setVisible(true);
        } else if (partRadioButton.equals(source)) {
            allRadioButton.setSelected(false);
            partRadioButton.setSelected(true);
            pwdPanel.setVisible(false);
        } else {
            Logger.info("Abnormal scenario."); // 暂行处理
        }
    }

    /**
     * 获取面板信息参数
     *
     * @return Map
     */
    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("all", allRadioButton.isSelected() ? "true" : "false");
        params.put("part", partRadioButton.isSelected() ? "true" : "false");
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
        // 用户名校验处理
        if (!CheckedUtils.checkUser(userField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), userField));
        }
        // 密码校验处理
        if (!checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }
        return result;
    }

    @Override
    public void clearPwd() {
        if (userField != null) {
            userField.setText("");
            userField = null;
        }

        if (pwdField != null) {
            pwdField.setText("");
            pwdField = null;
        }
    }

    /**
     * 校验密码非空
     *
     * @param password password
     * @return ture or false
     */
    protected boolean checkPwd(String password) {
        if (partRadioButton.isSelected()) {
            return true;
        }
        if (ValidateUtils.isNotEmptyString(password)) {
            return true;
        }
        return false;
    }

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

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}

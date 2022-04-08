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

/**
 * 新增修改用户
 *
 * @since 2020-10-13
 */
public class GuardianRestartPanel extends IDEBasePanel implements FocusListener {
    private JPanel mainPanel;

    private JPanel hostPanel;
    private JLabel hostLable;
    private JLabel hostConLable;

    private JPanel userPanel;
    private JLabel userLabel;
    private JTextField userField;

    private JPanel portPanel;
    private JLabel portLabel;
    private JLabel portConLabel;

    private JPanel pwdPanel;
    private JLabel pwdLable;
    private JPasswordField pwdField;
    private JLabel pwdView;

    private JPanel pwdTipPanel;
    private JLabel pwdTipLabel;

    private String id;
    private String ip;
    private String port;
    private String name;

    private String title = GuardianMangerConstant.GUARDIAN_RESTART_TITLE;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 修改用户创建实例
     *
     * @param id   id
     * @param ip   ip
     * @param port port
     */
    public GuardianRestartPanel(Object id, Object ip, Object port, Object name, String panelName) {
        this.panelName = StringUtil.stringIsEmpty(panelName) ? title : panelName;
        this.id = id.toString();
        this.ip = ip.toString();
        this.port = port.toString();
        this.name = name.toString();
        // 初始化面板
        initPanel(mainPanel);

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
        panel.setPreferredSize(new Dimension(545, 20));
    }

    /**
     * 设置初始面板
     */
    private void setLabel() {
        hostLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_serverIP"));
        hostConLable.setText(ip);
        portLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_port"));
        portConLabel.setText(port);
        userLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_userName"));
        pwdLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_password"));
        pwdTipLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_guardianManage_restart_tip"));
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new GuardianManagerAction();
        }
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

    /**
     * 获取安装配置信息参数
     *
     * @return Map
     */
    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("name", name);
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
        if (!CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }
        return result;
    }

    @Override
    public void clearPwd() {
        if (pwdField != null) {
            pwdField.setText("");
            pwdField = null;
        }

        if (userField != null) {
            userField.setText("");
            userField = null;
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}

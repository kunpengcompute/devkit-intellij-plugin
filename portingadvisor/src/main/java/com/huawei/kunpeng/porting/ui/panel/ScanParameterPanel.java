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

import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.CSSConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.scanparam.ScanParamsSetAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.fields.IntegerField;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;


/**
 * 扫描参数面板
 *
 * @since 2020-10-07
 */
public class ScanParameterPanel extends IDEBasePanel {
    /**
     * 如何设置扫描参数超链接
     */
    private static final String HELP_CONFIG_PARAMS = I18NServer.toLocale("plugins_porting_scanning_help_url");

    /**
     * 最小值
     */
    private static final int MIN_VALUE = 1;

    /**
     * 最大值
     */
    private static final int MAX_VALUE = 99999;

    /**
     * ENABLE对应1，DISABLE对应0
     */
    private static final int ENABLE = 1;

    private static final int DISABLE = 0;

    /**
     * 提示信息字体大小
     */
    private static final int TEXT_SIZE = 12;

    /**
     * 记录当前后端存储的参数值
     */
    public boolean currentScanParam;

    /**
     * 绑定的事件处理器
     */
    private ScanParamsSetAction scanParamsSetAction;

    /**
     * 显示工作量评估
     */
    private boolean isShowWorkLoad = true;

    private JPanel mainPanel;

    private JPasswordField passwordField;

    private JLabel paramsDesc;

    private JLabel userPasswordLabel;

    private JLabel displayWorkLoadLabel;

    private JLabel assemblyWorkLoadLabel;

    private JLabel cWorkLoadLabel;

    private IntegerField cTextField;

    private IntegerField assemblyTextField;

    private JLabel cWorkLoad;

    private JLabel assemblyWorkLoad;

    private JPanel pwdLinePanel;

    private JLabel configHelpLabel;

    private JLabel pwdView;

    private JComboBox comboBoxScan;

    private JComboBox comboBoxShow;

    private JPanel scanPanel;

    private JEditorPane scanEditorPanel;

    private JLabel scanTipLabel;

    private String cLines;

    private String asmLines;

    private boolean isHighlight = true;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 构造函数
     */
    public ScanParameterPanel() {
        // 初始化面板内组件事件
        registerComponentAction();
        initPanel(mainPanel);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        addEditorPanelTip(I18NServer.toLocale(
            "plugins_common_porting_settings_configureScanParams_Tip"), paramsDesc, scanPanel);
        paramsDesc.setText(I18NServer.toLocale("plugins_common_porting_settings_configureScanParams_keepGoingTip"));
        userPasswordLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_userPwdLabel"));
        cWorkLoadLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_c_workload"));
        assemblyWorkLoadLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_assembly_workload"));
        displayWorkLoadLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_display_workload"));
        cTextField.setText(cLines);
        assemblyTextField.setText(asmLines);
        pwdView.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        cTextField.setMinValue(MIN_VALUE);
        cTextField.setMaxValue(MAX_VALUE);
        assemblyTextField.setMinValue(MIN_VALUE);
        assemblyTextField.setMaxValue(MAX_VALUE);
        addDocumentListener();
        configHelpLabel.setText(I18NServer.toLocale("plugins_common_porting_settings_config_params_help"));
        configHelpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        configHelpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CommonUtil.openURI(HELP_CONFIG_PARAMS);
            }
        });

        // 初始化是否扫描下拉选项值
        addScanLevel();
        // 初始化是否显示下拉选项值
        addShowLevel();
        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, passwordField);
    }

    private void highlightState(boolean isValid) {
        passwordField.putClientProperty("JComponent.outline", isValid ? null : "error");
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            scanParamsSetAction = new ScanParamsSetAction();
        }
        ResponseBean responseBean = scanParamsSetAction.queryCurScanParam();
        if (responseBean == null) {
            Logger.error("responseBean is null.");
            return;
        }
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            Map<String, Object> map = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            cLines = map.get("c_line").toString();
            asmLines = map.get("asm_line").toString();
            if (map.get("keep_going") instanceof Boolean) {
                currentScanParam = (Boolean) map.get("keep_going");
            }
            if (map.get("p_month_flag") instanceof Boolean) {
                isShowWorkLoad = (Boolean) map.get("p_month_flag");
            }
        }
        passwordFieldAction.pwdDocument(passwordField);
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof ScanParamsSetAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 获取mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 点击apply按钮提交修改请求
     *
     * @throws ConfigurationException 配置异常
     */
    public void apply() throws ConfigurationException {
        // 修改扫描参数配置数据校验配置信息
        try {
            assemblyTextField.validateContent();
            cTextField.validateContent();
        } catch (ConfigurationException e) {
            throw new ConfigurationException(I18NServer.toLocale("plugins_porting_term_config_valid"));
        }

        // 密码校验
        if (!isPasswdFilled()) {
            highlightState(false);
            throw new ConfigurationException(I18NServer.toLocale("plugins_common_term_no_password"));
        }

        Integer curCLines = Integer.valueOf(cTextField.getText());
        Integer curAsmLines = Integer.valueOf(assemblyTextField.getText());

        Map<String, Object> body = new HashMap<>();
        int scanKeyWords = CommonI18NServer.toLocale("common_yes").equals(comboBoxScan.getSelectedItem())
            ? ENABLE : DISABLE;
        int showWorkLoad = CommonI18NServer.toLocale("common_yes").equals(comboBoxShow.getSelectedItem())
            ? ENABLE : DISABLE;
        String usrPassword = new String(passwordField.getPassword());
        String usrName = PortingUserInfoContext.getInstance().getUserName();
        body.put("username", usrName);
        body.put("password", usrPassword);
        body.put("keep_going", scanKeyWords);
        body.put("asm_line", curAsmLines);
        body.put("c_line", curCLines);
        body.put("p_month_flag", showWorkLoad);
        String usrID = PortingUserInfoContext.getInstance().getLoginId();
        if (scanParamsSetAction.setScanParam(this, usrID, body)) {
            // 更新标记。
            if (CommonI18NServer.toLocale("common_yes").equals(comboBoxScan.getSelectedItem())) {
                currentScanParam = true;
            } else {
                currentScanParam = false;
            }

            cLines = cTextField.getText();
            asmLines = assemblyTextField.getText();
            if (CommonI18NServer.toLocale("common_yes").equals(comboBoxShow.getSelectedItem())) {
                isShowWorkLoad = true;
            } else {
                isShowWorkLoad = false;
            }
        }
        isHighlight = true;
    }

    private void addDocumentListener() {
        passwordField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                highlightState(isPasswdFilled() || isHighlight);
                isHighlight = false;
            }
        });
    }

    /**
     * 重置面板内容
     */
    public void reset() {
        if (currentScanParam) {
            comboBoxScan.setSelectedItem(CommonI18NServer.toLocale("common_yes"));
        } else {
            comboBoxScan.setSelectedItem(CommonI18NServer.toLocale("common_no"));
        }
        cTextField.setText(cLines);
        assemblyTextField.setText(asmLines);
        if (isShowWorkLoad) {
            comboBoxShow.setSelectedItem(CommonI18NServer.toLocale("common_yes"));
        } else {
            comboBoxShow.setSelectedItem(CommonI18NServer.toLocale("common_no"));
        }
        isHighlight = true;
        passwordField.setText("");
    }

    /**
     * 判断是否修改参数值且输入了用户密码
     * 若是，设置界面enable Apply按钮
     * 否则，Apply按钮不可点击
     *
     * @return boolean
     */
    public boolean isModified() {
        return !Comparing.equal(getSelectedScanParamVal(), currentScanParam) || isPasswdFilled()
            || isWorkLoadChange() || !getDisplayResultModify();
    }

    @Override
    public void clearPwd() {
        if (passwordField != null) {
            passwordField.setText("");
        }
    }

    /**
     * 判断参数值是否修改
     *
     * @return boolean
     */
    private boolean getSelectedScanParamVal() {
        return CommonI18NServer.toLocale("common_yes").equals(comboBoxScan.getSelectedItem());
    }

    /**
     * 判断工作量展示参数是否修改
     *
     * @return boolean
     */
    private boolean getDisplayResultModify() {
        Boolean comboBoxShowLevel = CommonI18NServer.toLocale("common_yes").equals(comboBoxShow.getSelectedItem());
        return isShowWorkLoad == comboBoxShowLevel;
    }

    /**
     * 判断是否填写密码
     *
     * @return boolean
     */
    private boolean isPasswdFilled() {
        return ValidateUtils.isNotEmptyString(new String(passwordField.getPassword()));
    }

    private boolean isWorkLoadChange() {
        return !cTextField.getText().equals(cLines) || !assemblyTextField.getText().equals(asmLines);
    }

    private void addScanLevel() {
        String[] listData = new String[] {CommonI18NServer.toLocale("common_yes"),
            CommonI18NServer.toLocale("common_no")};
        ComboBoxModel<String> model = new DefaultComboBoxModel(listData);
        comboBoxScan.setModel(model);
        if (currentScanParam) {
            comboBoxScan.setSelectedItem(CommonI18NServer.toLocale("common_yes"));
        } else {
            comboBoxScan.setSelectedItem(CommonI18NServer.toLocale("common_no"));
        }
    }

    private void addShowLevel() {
        String[] listData = new String[] {CommonI18NServer.toLocale("common_yes"),
            CommonI18NServer.toLocale("common_no")};
        ComboBoxModel<String> model = new DefaultComboBoxModel(listData);
        comboBoxShow.setModel(model);
        if (isShowWorkLoad) {
            comboBoxShow.setSelectedItem(CommonI18NServer.toLocale("common_yes"));
        } else {
            comboBoxShow.setSelectedItem(CommonI18NServer.toLocale("common_no"));
        }
    }

    private void addEditorPanelTip(String text, JComponent jComponent, JPanel jPanel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html> <body><div style=\"color:rgb({0},{1},{2});font-family:{3}\">");
        stringBuilder.append(text);
        stringBuilder.append("</div> </body></html>");
        String messages = MessageFormat.format(stringBuilder.toString(), CSSConstant.TEXT_TIP_INFO_COLOR.getRed(),
            CSSConstant.TEXT_TIP_INFO_COLOR.getGreen(),
            CSSConstant.TEXT_TIP_INFO_COLOR.getBlue(), "huawei sans", TEXT_SIZE);

        JEditorPane thisEditor = new JEditorPane("text/html", messages);
        thisEditor.setEditable(false);
        thisEditor.setOpaque(false);

        jPanel.removeAll(); // 需要重新覆盖。
        jPanel.add(thisEditor);
        thisEditor.updateUI();
        IdeFocusManager.getGlobalInstance()
            .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(jPanel, true));
        jPanel.updateUI();
    }
}

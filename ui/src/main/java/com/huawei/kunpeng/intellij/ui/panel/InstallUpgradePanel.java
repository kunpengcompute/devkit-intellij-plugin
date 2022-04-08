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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_ICON;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.PasswordFieldAction;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.InstallUpgradeWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * 安装升级配置面板
 *
 * @since 2020-10-15
 */
public class InstallUpgradePanel extends IDEBasePanel {
    private static final int FILE_NOT_EXIST = -1;

    private static final String SSH_DEFAULT_PORT = "22";

    /**
     * 兼容性列表URL
     */
    private static final String SOFTWARE_SUPPORT_PATH = CommonI18NServer.toLocale("plugins_ui_common_software_support");

    private JPanel mainPanel;

    private JPanel initPanel;

    private JLabel descriptionLabel1;

    private JLabel descriptionLabel2;

    private JLabel descriptionLabel3;

    private JPanel ipPanel;

    private JLabel ipLabel;

    private JTextField ipField;

    private JPanel portPanel;

    private JLabel portLabel;

    private JTextField portField;

    private JPanel userPanel;

    private JLabel userLabel;

    private JPasswordField userField;

    private JPanel pwdPanel;

    private JLabel pwdLabel;

    private JPasswordField pwdField;

    private JPanel modePanel;

    private JLabel modeLabel;

    private JRadioButton pwdRadioButton;

    private JRadioButton privateKeyRadioButton;

    private JPanel privateKeyPanel;

    private JLabel privateKeyLabel;

    private TextFieldWithBrowseButton privateKeyUploadField;

    private JLabel pwdView;

    private JButton checkConnectionButton;

    private JLabel loadGif;

    private JPanel connect;

    private JPanel loadPanel;

    private JPanel checkPanel;

    private JPanel descPanel;

    private JEditorPane descEditorPanel;

    private JLabel iconLabel;

    private JPanel pwdLinePanel;

    private JPasswordField privatePwdField;

    private JLabel privatePwdLabel;

    private JLabel privatePwdView;

    private boolean isUpgrade = false;

    private String displayName;

    private InstallUpgradeWrapDialog dialog;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    public InstallUpgradeWrapDialog getDialog() {
        return dialog;
    }

    /**
     * 设置界面
     *
     * @param dialog 界面
     */
    public void setDialog(InstallUpgradeWrapDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * 获取值
     *
     * @return 值
     */

    public boolean isUpgrade() {
        return isUpgrade;
    }

    /**
     * 设置参数
     *
     * @param upgrade 参数
     */
    public void setUpgrade(boolean upgrade) {
        isUpgrade = upgrade;
    }

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     */
    public InstallUpgradePanel(ToolWindow toolWindow, String panelName, @NotNull String displayName,
        Boolean isUpgrade, IDEPanelBaseAction action) {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? Panels.LOGIN.panelName() : panelName;
        this.isUpgrade = isUpgrade;
        // 初始化面板
        initPanel(mainPanel);
        if (this.action == null) {
            this.action = action;
        }
        // 初始化面板内组件事件
        registerComponentAction();
        // 初始化content实例
        this.displayName = displayName;
        createContent(mainPanel, this.displayName, false);
    }

    /**
     * 带toolWindow和displayName的构造参数
     *
     * @param toolWindow  toolWindow
     * @param displayName 面板显示title
     * @param isUpgrade   是否是升级面板
     */
    public InstallUpgradePanel(ToolWindow toolWindow, String displayName,
        Boolean isUpgrade, IDEPanelBaseAction action) {
        this(toolWindow, null, displayName, isUpgrade, action);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        loadGif.setIcon(null);
        setLabel();
    }

    /**
     * 设置初始面板
     */
    private void setLabel() {
        isUpgradeShow();
        iconLabel.setIcon(ICON_INFO_ICON);
        iconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        iconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
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
        // 添加输入框focus改变btn状态
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
        // 密码显示事件
        passwordFieldAction.registerMouseListener(pwdView, pwdField);
        passwordFieldAction.registerMouseListener(privatePwdView, privatePwdField);
    }

    private void isUpgradeShow() {
        if (isUpgrade) {
            StringBuilder tempSB = new StringBuilder();
            tempSB.append("<html> <body><div style=\"color:rgb({0},{1},{2});font-family:{3};font-size:{4}\">");
            tempSB.append(CommonI18NServer.toLocale("plugins_ui_common_upgrade_description1"));
            tempSB.append("<a style=\"text-decoration:none;color:rgb(47, 101, 202);\"; href=").append(
                SOFTWARE_SUPPORT_PATH).append(">  ");
            tempSB.append(CommonI18NServer.toLocale("plugins_ui_common_upgrade_description2"));
            tempSB.append("   </a>");
            tempSB.append(CommonI18NServer.toLocale("plugins_ui_common_upgrade_description3"));
            tempSB.append("</div> </body></html>");
            addDescPanel(tempSB);
        } else {
            StringBuilder tempSB = new StringBuilder();
            tempSB.append("<html> <body><div style=\"color:rgb({0},{1},{2});font-family:{3};font-size:{4}\">");
            tempSB.append(CommonI18NServer.toLocale("plugins_ui_common_install_description1"));
            tempSB.append("<a style=\"text-decoration:none;color:#447FF5;\"; href=").append(
                SOFTWARE_SUPPORT_PATH).append(">  ");
            tempSB.append(CommonI18NServer.toLocale("plugins_ui_common_install_description2"));
            tempSB.append("   </a>");
            tempSB.append(CommonI18NServer.toLocale("plugins_ui_common_install_description3"));
            tempSB.append("</div> </body></html>");
            addDescPanel(tempSB);
        }
    }

    private void addDescPanel(StringBuilder tempSB) {
        Color color = ipLabel.getForeground();
        Font font = ipLabel.getFont();
        String messages = MessageFormat.format(tempSB.toString(), color.getRed(), color.getGreen(), color.getBlue(),
                font.getFontName(), font.getSize());
        descEditorPanel = new JEditorPane("text/html", messages);
        descEditorPanel.setEditable(false);
        descEditorPanel.setOpaque(false);
        descEditorPanel.setMargin(new Insets(5, 3, 0, 3));
        HyperlinkListener listener = new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperLink) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(hyperLink.getEventType())) {
                    return;
                }
                CommonUtil.openURI(SOFTWARE_SUPPORT_PATH);
            }
        };
        descEditorPanel.addHyperlinkListener(listener);

        descPanel.add(descEditorPanel);
        descEditorPanel.updateUI();
        IdeFocusManager.getGlobalInstance()
                .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(descPanel, true));
        descPanel.updateUI();
        mainPanel.updateUI();
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
            Logger.info("Abnormal scenario.");
        }
    }

    private void registerBrowseDialog(@NotNull TextFieldWithBrowseButton component,
        @NlsContexts.DialogTitle @NotNull String dialogTitle) {
        component.addBrowseFolderListener(dialogTitle, null, null,
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    /**
     * 注册组件事件
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
     * 获取连接校验按钮
     *
     * @return 校验按钮
     */
    public JButton getCheckConnectionButton() {
        return checkConnectionButton;
    }

    /**
     * 获取加载动态
     *
     * @return 加载JLable
     */
    public JLabel getLoadGifLabel() {
        return this.loadGif;
    }

    /**
     * 获取安装配置信息参数
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
        params.put("passPhrase", privatePwdField.getText());
        params.put("displayName", displayName);
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
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_ipError"), ipField));
        }

        // 端口校验处理
        if (!CheckedUtils.checkPort(portField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_portError"), portField));
        }

        // 用户名校验处理
        if (!CheckedUtils.checkUser(userField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), userField));
        }

        // 密码校验处理
        if (pwdRadioButton.isSelected() && !CheckedUtils.checkPwd(new String(pwdField.getPassword()))) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_required_tip"), pwdField));
        }

        // 密钥校验处理
        if (privateKeyRadioButton.isSelected() && !FileUtil.checkKey(privateKeyUploadField.getText())) {
            result.add(new ValidationInfo(CommonI18NServer.toLocale("plugins_common_message_keyError"),
                    privateKeyUploadField));
        }

        return result;
    }

    @Override
    public void clearPwd() {
        if (pwdField != null) {
            pwdField.setText("");
            Arrays.fill(pwdField.getPassword(), '0');
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
                if (dialog == null) {
                    return;
                }
                if (ValidateUtils.isNotEmptyString(newText) && !Objects.equals(oldText, newText) && dialog != null) {
                    // 调用置灰按钮。
                    dialog.actionOperate(false);
                }
            }
        });
    }
}

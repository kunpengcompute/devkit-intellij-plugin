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
import com.huawei.kunpeng.porting.action.setting.whitelist.WhitelistManageAction;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.UIUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
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
 * 白名单管理面板
 *
 * @since 2020-10-12
 */
public class WhitelistManagePanel extends IDEBasePanel {
    /**
     * *号状态标记图片
     */
    private static final String RED_STAR_PATH = "/assets/img/settings/redstar.png";

    /**
     * 白名单包路径
     */
    private static final String PACKAGES_HREF = I18NServer.toLocale("plugins_porting_package_herf");

    private static final int MB_TO_KB = 1024;

    private static final int RECOVERY = 1;

    private static final int UPDATE = 2;

    /**
     * 白名单包是否已上传
     */
    public boolean isUploaded = false;

    private WhitelistManageAction whitelistManageAction;

    private JPanel mainPanel;

    private JPanel upgradePanel;

    private JPanel restorationPanel;

    private JRadioButton upgradeRadioButton;

    private TextFieldWithBrowseButton whitelistPackUploadField;

    private JButton uploadBtn;

    private JRadioButton restorationRadioButton;

    private JLabel whitelistPackLabel;

    private JLabel restorationDesc;

    private JPasswordField upgradePasswdField;

    private JPasswordField restorationPasswdField;

    private JLabel upgradePasswdLabel;

    private JLabel restorationPasswdLabel;

    private JEditorPane editorPane1;

    private JPanel htmlPanel;

    private JLabel eyeUpgrade;

    private JLabel eyeRset;

    private JPanel upLinePanel;

    private JPanel resetLinePanel;

    private JPasswordField passwordField1;

    private boolean isHighlight;

    private PasswordFieldAction passwordFieldAction = new PasswordFieldAction();

    /**
     * 构造函数
     */
    public WhitelistManagePanel() {
        initPanel(mainPanel);
        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        initLabelsAndButtonsText();
        eyeRset.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        eyeUpgrade.setIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        addRedStars();
        ItemListener operationTypeChangedListener = event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                enableOptions(event.getSource());
                addHtmlEditorPanel();
            }
        };
        restorationRadioButton.addItemListener(operationTypeChangedListener);
        upgradeRadioButton.addItemListener(operationTypeChangedListener);
        upgradeRadioButton.setSelected(true);
        addHtmlEditorPanel();
        whitelistPackUploadField.setButtonIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        registerBrowseDialog(whitelistPackUploadField,
            I18NServer.toLocale("plugins_porting_label_whiteList_management_upload"));
        uploadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String localFilePath = whitelistPackUploadField.getText(); // 获取选中的本地文件路径
                if ("".equals(localFilePath)) {
                    Logger.info("Please upload whitelist package firstly."); // 弹窗提示等待统一方式，暂未提供
                    IDENotificationUtil.notificationCommon(new NotificationBean("",
                        I18NServer.toLocale("plugins_porting_whiteList_management_upload_tip2"),
                        NotificationType.ERROR));
                } else {
                    uploadFile(localFilePath);
                }
            }
        });

        addDocumentListener(); // 增加校验监控。
        // 密码显示事件
        passwordFieldAction.registerMouseListenerTwo(eyeRset, restorationPasswdField);
        passwordFieldAction.registerMouseListener(eyeUpgrade, upgradePasswdField);
    }

    private void initLabelsAndButtonsText() {
        upgradeRadioButton.setText(I18NServer.toLocale("plugins_porting_common_button_upgrade"));
        restorationRadioButton.setText(I18NServer.toLocale("plugins_porting_common_button_restoration"));
        restorationDesc.setText(I18NServer.toLocale("plugins_porting_tip_whitelistManage_restorationDesc"));
        uploadBtn.setText(I18NServer.toLocale("plugins_porting_common_button_upload"));
        whitelistPackLabel.setText(I18NServer.toLocale("plugins_porting_label_whitelistManage_package"));
        upgradePasswdLabel.setText(I18NServer.toLocale("plugins_common_label_adminPassword"));
        restorationPasswdLabel.setText(I18NServer.toLocale("plugins_common_label_adminPassword"));
    }

    /**
     * 添加监控事件。
     */
    private void addDocumentListener() {
        passwordFiledAddDocumentListener(upgradePasswdField);
        passwordFiledAddDocumentListener(restorationPasswdField);
    }

    private void passwordFiledAddDocumentListener(JPasswordField backupPasswdField) {
        backupPasswdField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                highlightState(!ValidateUtils.isEmptyString(new String(backupPasswdField.getPassword())) || isHighlight,
                    backupPasswdField);
                isHighlight = false;
            }
        });
    }

    /**
     * 添加html标签
     */
    private void addHtmlEditorPanel() {
        StringBuilder tempSB = new StringBuilder();
        tempSB.append("<html> <body><div style=\"color:rgb({0},{1},{2}); font-family:{3};font-size:{4}\">");
        tempSB.append(I18NServer.toLocale("plugins_porting_tip_whitelistManage_upgradeDesc01"));
        tempSB.append("<a style=\"color:rgb(47, 101, 202);\"; href=").append(PACKAGES_HREF).append(">  ");
        tempSB.append(I18NServer.toLocale("plugins_porting_tip_whitelistManage_upgradeDesc02"));
        tempSB.append("   </a>");
        tempSB.append("</div> </body></html>");

        Color color = whitelistPackLabel.getForeground();
        Font font = whitelistPackLabel.getFont();
        String messages = MessageFormat.format(tempSB.toString(), color.getRed(), color.getGreen(), color.getBlue(),
            font.getFontName(), font.getSize());
        editorPane1 = new JEditorPane("text/html", messages);
        editorPane1.setEditable(false);
        editorPane1.setOpaque(false);
        HyperlinkListener listener = new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperLink) {
                if (!HyperlinkEvent.EventType.ACTIVATED.equals(hyperLink.getEventType())) {
                    return;
                }
                CommonUtil.openURI(PACKAGES_HREF);
            }
        };
        editorPane1.addHyperlinkListener(listener);
        htmlPanel.removeAll(); // 需要重新覆盖。
        htmlPanel.add(editorPane1);
        editorPane1.updateUI();
        IdeFocusManager.getGlobalInstance()
            .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(htmlPanel, true));
        htmlPanel.updateUI();
        mainPanel.updateUI();
    }

    /**
     * 添加红色星号必填项标记
     */
    private void addRedStars() {
        Icon icon = BaseIntellijIcons.load(RED_STAR_PATH);
        setPasswordLabelProperties(icon, whitelistPackLabel);
        setPasswordLabelProperties(icon, upgradePasswdLabel);
        setPasswordLabelProperties(icon, restorationPasswdLabel);
        eyeRset.setDisabledIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
        eyeUpgrade.setDisabledIcon(BaseIntellijIcons.load(IDEConstant.EYE_HIDE));
    }

    private void setPasswordLabelProperties(Icon icon, JLabel label) {
        label.setIcon(icon);
        label.setDisabledIcon(icon);
        label.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        label.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
    }


    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            whitelistManageAction = new WhitelistManageAction();
        }
        passwordFieldAction.pwdDocument(upgradePasswdField);
        passwordFieldAction.pwdDocument(restorationPasswdField);
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof WhitelistManageAction) {
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
     * @throws ConfigurationException ConfigurationException
     */
    public void apply() throws ConfigurationException {
        if (upgradeRadioButton.isSelected()) {
            String usrPassword = new String(upgradePasswdField.getPassword());
            if (!isUploaded) {
                throw new ConfigurationException(I18NServer.toLocale(
                    "plugins_porting_whiteList_management_upload_tip"));
            } else if (ValidateUtils.isEmptyString(usrPassword)) {
                highlightState(false, upgradePasswdField);
                throw new ConfigurationException(I18NServer.toLocale("plugins_common_term_no_password"));
            } else {
                whitelistManageAction.whitelistManagement(UPDATE, usrPassword);
            }
        } else if (restorationRadioButton.isSelected()) {
            String usrPassword = new String(restorationPasswdField.getPassword());
            if (ValidateUtils.isEmptyString(usrPassword)) {
                highlightState(false, restorationPasswdField);
                throw new ConfigurationException(I18NServer.toLocale("plugins_common_term_no_password"));
            } else {
                whitelistManageAction.whitelistManagement(RECOVERY, usrPassword);
            }
        } else {
            Logger.info("Bad Request"); // 暂行处理
        }

        isHighlight = true;
    }

    /**
     * 重置面板内容
     */
    public void reset() {
        upgradeRadioButton.setSelected(true);
        upgradePasswdField.setText("");
        restorationPasswdField.setText("");
        whitelistPackUploadField.setText("");
        isUploaded = false;
        isHighlight = true;
        highlightState(true, upgradePasswdField);
        highlightState(true, restorationPasswdField);
    }

    /**
     * 判断Apply按钮是否可以点击
     *
     * @return boolean
     */
    public boolean isModified() {
        return !upgradeRadioButton.isSelected()
            || !ValidateUtils.isEmptyArray(upgradePasswdField.getPassword())
            || !ValidateUtils.isEmptyArray(restorationPasswdField.getPassword())
            || !ValidateUtils.isEmptyString(whitelistPackUploadField.getText());
    }

    /**
     * 判断管理员密码是否填写
     *
     * @param passwordField 密码输入框组件
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
            // 暂行处理
            Logger.info("Abnormal scenario.");
        }
    }

    private void registerBrowseDialog(@NotNull TextFieldWithBrowseButton component,
        @NlsContexts.DialogTitle @NotNull String dialogTitle) {
        component.addBrowseFolderListener(dialogTitle, null, null,
            FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    /**
     * 文件上传前置判断处理
     *
     * @param filePath 本地文件路径
     */
    public void uploadFile(String filePath) {
        if (!FileUtil.isValidateFile(filePath, 500)) {
            return;
        }
        File file = new File(filePath);
        whitelistManageAction.uploadButtonClicked(this, file, file.length() / MB_TO_KB / MB_TO_KB);
    }

    @Override
    public void clearPwd() {
        clearJPasswordField(upgradePasswdField);
        clearJPasswordField(restorationPasswdField);
        clearJPasswordField(passwordField1);
    }

    private void clearJPasswordField(JPasswordField jPasswordField) {
        if (jPasswordField != null) {
            Arrays.fill(jPasswordField.getPassword(), '0');
            jPasswordField.setText("");
        }
    }

    /**
     * 设置高亮。
     *
     * @param isValid    isValid
     * @param jTextField jTextField
     */
    private void highlightState(boolean isValid, JTextField jTextField) {
        jTextField.putClientProperty("JComponent.outline", isValid ? null : "error");
    }
}

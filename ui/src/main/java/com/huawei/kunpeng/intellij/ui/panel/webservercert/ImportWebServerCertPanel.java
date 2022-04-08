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

package com.huawei.kunpeng.intellij.ui.panel.webservercert;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * ImportWebServerCertPanel
 *
 * @since 2021-09-08
 */
public class ImportWebServerCertPanel extends IDEBasePanel {
    /**
     * mainPanel
     */
    protected JPanel mainPanel;
    /**
     * importPanel
     */
    protected JPanel importPanel;
    /**
     * packUploadLabel
     */
    protected JLabel packUploadLabel;
    /**
     * packUploadField
     */
    protected TextFieldWithBrowseButton packUploadField;
    /**
     * importTip
     */
    protected JTextArea importTip;
    /**
     * portStarLabel
     */
    protected JLabel portStarLabel;
    /**
     * uploadBtn
     */
    protected JButton uploadBtn;

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    protected void initPanel(JPanel panel) {
        super.initPanel(this.mainPanel);
        this.packUploadLabel.setText(I18NServer.toLocale("plugins_porting_certificate_file_label"));
        this.importTip.setText(I18NServer.toLocale("plugins_porting_certificate_import_tip"));
        this.importTip.setBackground(null);
        this.importTip.setEditable(false);
        this.importTip.setFont(this.packUploadLabel.getFont());
        this.importTip.setForeground(this.packUploadLabel.getForeground());
        registerBrowseDialog(packUploadField, "");
    }

    private void registerBrowseDialog(
            @NotNull TextFieldWithBrowseButton component, @NlsContexts.DialogTitle @NotNull String dialogTitle) {
        component.addBrowseFolderListener(
                dialogTitle, null, null, FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * getPackUploadField
     *
     * @return TextFieldWithBrowseButton
     */
    public TextFieldWithBrowseButton getPackUploadField() {
        return packUploadField;
    }

    /**
     * 判断当前面板是否可以进行下一步
     *
     * @return ValidationInfo
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (ValidateUtils.isEmptyString(packUploadField.getText())) {
            vi = new ValidationInfo(I18NServer.toLocale("plugins_porting_settings_import_web_server_certificate"),
                    packUploadField);
        }
        return vi;
    }
}

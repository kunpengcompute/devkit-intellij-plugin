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

package com.huawei.kunpeng.porting.ui.panel.settings.crl;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;

import lombok.Getter;

import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The class: ImportCRLPanel
 *
 * @since 2021-8-11
 */
public class ImportCRLPanel extends IDEBasePanel {
    private static final String CRL_SUFFIX = "crl";

    private JPanel mainPanel;
    private JLabel portStatLabel;
    private JLabel crlUploadLabel;

    @Getter
    private TextFieldWithBrowseButton crlUploadField;

    /**
     * Constructor
     */
    public ImportCRLPanel() {
        initPanel(mainPanel);
        registerComponentAction();
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(this.mainPanel);
        this.crlUploadLabel.setText(I18NServer.toLocale("plugins_porting_certificate_file_label"));
    }

    @Override
    protected void registerComponentAction() {
        action = CertificationRevocationListAction.getInstance();
        registerBrowseDialog(crlUploadField);
    }

    /**
     * 文件过滤器
     *
     *  @param component component
     */
    private void registerBrowseDialog(@NotNull TextFieldWithBrowseButton component) {
        component.addBrowseFolderListener("", null, null, new FileChooserDescriptor(
        true, false, false, false, false, false)
        .withFileFilter(file -> CRL_SUFFIX.equals(file.getExtension())));
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 合理性校验
     *
     * @return ValidationInfo
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        if (ValidateUtils.isEmptyString(crlUploadField.getText())) {
            vi = new ValidationInfo(I18NServer.toLocale("plugins_porting_settings_import_crl"),
                    crlUploadField);
        }
        return vi;
    }

    /**
     * 获取主面板
     *
     * @return JPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }
}

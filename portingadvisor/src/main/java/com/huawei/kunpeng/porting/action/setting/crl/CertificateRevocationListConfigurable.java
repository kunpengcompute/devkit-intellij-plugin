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

package com.huawei.kunpeng.porting.action.setting.crl;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.ui.panel.CertRovListPanel;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * The class: CertificateRevocationListConfigurable 证书吊销列表 Configurable
 *
 * @since 2021-8-10
 */
public class CertificateRevocationListConfigurable implements Configurable {
    private static final String DISPLAY_NAME =
            I18NServer.toLocale("plugins_porting_certificate_revocation_list");
    private CertRovListPanel certRovListPanel;


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.certRovListPanel = new CertRovListPanel();
        return certRovListPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
    }
}

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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Web服务器证书
 *
 * @since 2021-09-08
 */
public class WebServerCertificate implements SearchableConfigurable {
    /**
     * ID
     */
    protected static final String ID = "Web Server Certificate";
    /**
     * 显示名称
     */
    protected static final String DISPLAY_NAME =
            CommonI18NServer.toLocale("plugins_common_certificate_title");

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    @NotNull
    public String getId() {
        return ID;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return null;
    }

    @Override
    public void apply() throws ConfigurationException {
    }

    @Override
    public boolean isModified() {
        return false;
    }
}

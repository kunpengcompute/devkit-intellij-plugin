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

package com.huawei.kunpeng.porting.ui.panel.loginsettings;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * WebServerCertificate
 *
 * @since 2020-10-07
 */
public class LoginSettings implements Configurable {
    private static final String ID = "Login Settings";
    private static final String DISPLAY_NAME =
            I18NServer.toLocale("plugins_porting_loginsettings_title");
    private LoginSettingsPanel loginSettingsPanel;

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return this.DISPLAY_NAME;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    /**
     * 创建面板
     *
     * @return portingSettingsEntrancePanel
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        this.loginSettingsPanel = new LoginSettingsPanel(null, null, false);
        return this.loginSettingsPanel;
    }

    @Override
    public boolean isModified() {
        return this.loginSettingsPanel.isModified();
    }

    /**
     * 点击Apply事件
     *
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {
        if (this.loginSettingsPanel != null) {
            this.loginSettingsPanel.apply();
        }
    }

    /**
     * disposeUIResources方法
     */
    @Override
    public void disposeUIResources() {
        this.loginSettingsPanel = null;
    }


    /**
     * 重置方法
     */
    @Override
    public void reset() {
        this.loginSettingsPanel.reset();
    }
}

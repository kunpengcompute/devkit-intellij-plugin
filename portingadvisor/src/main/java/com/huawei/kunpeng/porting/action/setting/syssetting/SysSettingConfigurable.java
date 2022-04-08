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

package com.huawei.kunpeng.porting.action.setting.syssetting;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.SYSTEM_SETTING_TITLE;

import com.huawei.kunpeng.porting.ui.panel.syspanel.SystemSettingPanel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 系统设置
 *
 * @since 2.2.T4
 */
public class SysSettingConfigurable implements SearchableConfigurable {
    private static final String ID = "com.huawei.kunpeng.porting.settings.SystemSettingPanel";
    /**
     * 全局通用一个配置页面
     */
    private static SystemSettingPanel sysSettingsComponent;

    /**
     * Returns the visible name of the configurable component.
     * Note, that this method must return the display name
     * that is equal to the display name declared in XML
     * to avoid unexpected errors.
     *
     * @return the visible name of the configurable component
     */
    @Override
    @NlsContexts.ConfigurableName
    public String getDisplayName() {
        return SYSTEM_SETTING_TITLE;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return sysSettingsComponent.getPreferredFocusedComponent();
    }

    /**
     * Creates new Swing form that enables user to configure the settings.
     * Usually this method is called on the EDT, so it should not take a long time.
     * <p>
     * Also this place is designed to allocate resources (subscriptions/listeners etc.)
     *
     * @return new Swing form to show, or {@code null} if it cannot be created
     * @see #disposeUIResources
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        if (sysSettingsComponent == null) {
            sysSettingsComponent = new SystemSettingPanel();
        }
        return sysSettingsComponent.getPanel();
    }

    /**
     * Indicates whether the Swing form was modified or not.
     * This method is called very often, so it should not take a long time.
     *
     * @return {@code true} if the settings were modified, {@code false} otherwise
     */
    @Override
    public boolean isModified() {
        return sysSettingsComponent != null && sysSettingsComponent.isModified();
    }

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     *
     * @throws ConfigurationException if values cannot be applied
     */
    @Override
    public void apply() throws ConfigurationException {
        sysSettingsComponent.apply();
    }

    @Override
    public void disposeUIResources() {
        sysSettingsComponent = null;
    }

    @Override
    public void reset() {
        sysSettingsComponent.reset();
    }

    @Override
    @NotNull
    public String getId() {
        return ID;
    }
}

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

package com.huawei.kunpeng.hyper.tuner.setting.user;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.TuningAdminUserSetPanel;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 管理员用户管理设置界面配置器
 *
 * @since 2020-10-06
 */
public class AdminUserSettingsConfigurable implements Configurable {
    /**
     * 全局通用一个配置页面
     */
    private static TuningAdminUserSetPanel tuningAdminUserSetPanel;

    /**
     * 返回该页面用户后续刷页面。
     *
     * @return 返回配置页面
     */
    public static TuningAdminUserSetPanel getAdminUserSettingsComponent() {
        return tuningAdminUserSetPanel;
    }

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
        return TuningUserManageConstant.MANAGE_USER;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return tuningAdminUserSetPanel.getPreferredFocusedComponent();
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
        if (tuningAdminUserSetPanel == null) {
            tuningAdminUserSetPanel = new TuningAdminUserSetPanel();
        } else {
            tuningAdminUserSetPanel.updateUserTale();
        }

        return tuningAdminUserSetPanel.getPanel();
    }

    /**
     * Indicates whether the Swing form was modified or not.
     * This method is called very often, so it should not take a long time.
     *
     * @return {@code true} if the settings were modified, {@code false} otherwise
     */
    @Override
    public boolean isModified() {
        return tuningAdminUserSetPanel != null && tuningAdminUserSetPanel.isModified();
    }

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     *
     * @throws ConfigurationException if values cannot be applied
     */
    @Override
    public void apply() throws ConfigurationException {
        tuningAdminUserSetPanel.apply();
    }

    @Override
    public void disposeUIResources() {
    }

    @Override
    public void reset() {
        tuningAdminUserSetPanel.reset();
    }
}
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

package com.huawei.kunpeng.porting.action.setting.scanparam;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.ui.panel.ScanParameterPanel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 扫描参数设置项
 *
 * @since 2020-10-07
 */
public class ScanParameterConfigurable implements SearchableConfigurable {
    private static final String ID = "Configure Scan Parameter";
    private static final String DISPLAY_NAME =
            I18NServer.toLocale("plugins_common_porting_settings_configureScanParams");
    private ScanParameterPanel scanParameterPanel;

    @Override
    @NotNull
    public String getId() {
        return ID;
    }

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
        if (scanParameterPanel == null) {
            scanParameterPanel = new ScanParameterPanel();
        }
        return scanParameterPanel.getPanel();
    }

    /**
     * 是否修改判断
     *
     * @return boolean
     */
    @Override
    public boolean isModified() {
        return scanParameterPanel != null && scanParameterPanel.isModified();
    }

    /**
     * 点击Apply事件
     *
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {
        if (scanParameterPanel != null) {
            scanParameterPanel.apply();
            scanParameterPanel.clearPwd();
        }
    }

    /**
     * 点击reset按钮事件
     */
    @Override
    public void reset() {
        if (scanParameterPanel != null) {
            scanParameterPanel.reset();
        }
    }

    /**
     * disposeUIResources方法
     */
    @Override
    public void disposeUIResources() {
        if (scanParameterPanel != null) {
            scanParameterPanel.clearPwd();
        }
        scanParameterPanel = null;
    }
}

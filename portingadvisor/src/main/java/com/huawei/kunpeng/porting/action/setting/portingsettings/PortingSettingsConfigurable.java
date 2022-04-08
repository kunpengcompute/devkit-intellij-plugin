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

package com.huawei.kunpeng.porting.action.setting.portingsettings;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.ui.panel.PortingSettingsPanel;

import com.intellij.openapi.options.SearchableConfigurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Porting Settings顶层节点
 *
 * @since 2020-10-07
 */
public class PortingSettingsConfigurable implements SearchableConfigurable {
    private static final String ID = "Porting Settings";
    private static final String DISPLAY_NAME =
            I18NServer.toLocale("plugins_common_porting_settings");

    private PortingSettingsPanel portingSettingsPanel;

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
     * @return portingSettingsPanel
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        if (portingSettingsPanel == null) {
            portingSettingsPanel = new PortingSettingsPanel();
        }
        return portingSettingsPanel.getPanel();
    }

    /**
     * 是否修改判断
     *
     * @return boolean
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * 点击Apply事件，该页面为引导页面
     * 统一入口，无任务变更操作
     */
    @Override
    public void apply() {}
}

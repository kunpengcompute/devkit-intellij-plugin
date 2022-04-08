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

package com.huawei.kunpeng.porting.action.setting.template;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.ui.panel.PortingTemplatePanel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 设置——软件迁移模板管理
 *
 * @since 2021-01-26
 */
public class PortingTemplateConfigurable implements SearchableConfigurable {
    private static final String ID = "Porting Template";
    private static final String DISPLAY_NAME =
        I18NServer.toLocale("plugins_porting_title_software_porting_template");
    private PortingTemplatePanel portingTemplatePanel;

    /**
     * 获取ID
     *
     * @return ID
     */
    @Override
    @NotNull
    @NonNls
    public String getId() {
        return ID;
    }

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Override
    @NlsContexts.ConfigurableName
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * 创建面板
     *
     * @return portingTemplatePanel
     */
    @Override
    @Nullable
    public JComponent createComponent() {
        if (portingTemplatePanel == null) {
            portingTemplatePanel = new PortingTemplatePanel();
        }
        return portingTemplatePanel.getPanel();
    }

    /**
     * 是否修改判断
     *
     * @return boolean
     */
    @Override
    public boolean isModified() {
        return portingTemplatePanel != null && portingTemplatePanel.isModified();
    }

    /**
     * 点击Apply事件
     *
     * @throws ConfigurationException 异常
     */
    @Override
    public void apply() throws ConfigurationException {
        if (portingTemplatePanel != null) {
            portingTemplatePanel.apply();
            portingTemplatePanel.clearPwd();
        }
    }

    /**
     * 点击reset按钮事件
     */
    @Override
    public void reset() {
        if (portingTemplatePanel != null) {
            portingTemplatePanel.reset();
        }
    }

    /**
     * disposeUIResources方法
     */
    @Override
    public void disposeUIResources() {
        if (portingTemplatePanel != null) {
            portingTemplatePanel.clearPwd();
        }
        portingTemplatePanel = null;
    }
}

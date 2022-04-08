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

package com.huawei.kunpeng.hyper.tuner.common;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 功能描述
 *
 * @since 2021年09月09日
 */
public class SettingCommonConfigurable implements Configurable {
    /**
     * displayName
     */
    protected String displayName;

    /**
     * commonConfigPanel
     */
    protected SettingCommonConfigPanel commonConfigPanel;

    public SettingCommonConfigurable(String displayName, SettingCommonConfigPanel commonConfigPanel) {
        SettingCommonConfigPanel settingCommonConfigPanel = commonConfigPanel;
        if (settingCommonConfigPanel == null) {
            settingCommonConfigPanel = new SettingCommonConfigPanel() {
                @Override
                public JPanel getPanel() {
                    return new JPanel();
                }
            };
        }
        this.displayName = displayName;
        this.commonConfigPanel = settingCommonConfigPanel;
    }

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return commonConfigPanel.getPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return commonConfigPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return commonConfigPanel != null && commonConfigPanel.isModified();
    }

    @Override
    public void apply() {
        commonConfigPanel.apply();
    }

    @Override
    public void reset() {
        commonConfigPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        commonConfigPanel = null;
    }
}

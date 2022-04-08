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

package com.huawei.kunpeng.hyper.tuner.setting.javaperf.javaprofilersettings;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.JavaPerfSettingsPanel;
import com.huawei.kunpeng.intellij.ui.panel.CommonSettingsPanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Java性能分析-系统设置
 *
 * @since 2021-07-07
 */
public class JavaSettingConfigurable implements Configurable {
    /**
     * java性能配置页面
     */
    public CommonSettingsPanel commonSettingsPanel;
    String JAVA_PROFILER_SETTINGS = JavaProviderSettingConstant.JAVA_PROFILER_SETTINGS;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return JAVA_PROFILER_SETTINGS;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return commonSettingsPanel.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (commonSettingsPanel == null) {
            commonSettingsPanel = new JavaPerfSettingsPanel();
        }
        return commonSettingsPanel.getPanel();
    }

    @Override
    public void apply() throws ConfigurationException {
        commonSettingsPanel.apply();
    }

    @Override
    public boolean isModified() {
        return commonSettingsPanel != null && commonSettingsPanel.isModified();
    }

    @Override
    public void disposeUIResources() {
        commonSettingsPanel = null;
    }

    @Override
    public void reset() {
        commonSettingsPanel.reset();
    }
}
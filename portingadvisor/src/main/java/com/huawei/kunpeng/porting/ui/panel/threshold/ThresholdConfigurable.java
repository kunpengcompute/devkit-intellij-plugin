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

package com.huawei.kunpeng.porting.ui.panel.threshold;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.ui.panel.ThresholdConfigPanel;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 阈值设置配置类
 *
 * @since v1.0
 */
public class ThresholdConfigurable implements SearchableConfigurable {
    /**
     * 阈值设置配置面板
     */
    private ThresholdConfigPanel thresholdConfigPanel;

    @Override
    @NlsContexts.ConfigurableName
    public String getDisplayName() {
        return I18NServer.toLocale("plugins_common_porting_settings_threshold_config");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return thresholdConfigPanel.getConfPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (thresholdConfigPanel == null) {
            thresholdConfigPanel = new ThresholdConfigPanel();
        }
        return thresholdConfigPanel.getConfPanel();
    }

    @Override
    public boolean isModified() {
        return thresholdConfigPanel != null && !thresholdConfigPanel.checkFieldValue();
    }

    @Override
    public void apply() {
        thresholdConfigPanel.applyAndSaveConf();
    }

    @Override
    public void reset() {
        thresholdConfigPanel.resetConf();
    }

    @Override
    public void disposeUIResources() {
        thresholdConfigPanel = null;
    }

    @Override
    @NotNull
    public String getId() {
        return "com.huawei.kunpeng.porting.ThresholdSettings";
    }
}

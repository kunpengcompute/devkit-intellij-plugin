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

package com.huawei.kunpeng.hyper.tuner.setting.javaperf.reportthresholds;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.ReportThresholdConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.ReportThresholdPanel;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 报告阈值配置
 *
 * @since 2.2.T4
 */
public class ReportThresholdConfigurable implements Configurable {
    private static final String DISPLAY_NAME = ReportThresholdConstant.REPORT_THRESHOLD_TITLE;
    private static ReportThresholdPanel reportThresholdComponent;

    @Override
    public JComponent getPreferredFocusedComponent() {
        if (reportThresholdComponent == null) {
            return createComponent();
        }
        return reportThresholdComponent.getPreferredFocusedComponent();
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
        if (reportThresholdComponent == null) {
            reportThresholdComponent = new ReportThresholdPanel();
        }
        return reportThresholdComponent.getPanel();
    }

    /**
     * Indicates whether the Swing form was modified or not.
     * This method is called very often, so it should not take a long time.
     *
     * @return {@code true} if the settings were modified, {@code false} otherwise
     */
    @Override
    public boolean isModified() {
        return reportThresholdComponent != null && reportThresholdComponent.isModified();
    }

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     *
     * @throws ConfigurationException if values cannot be applied
     */
    @Override
    public void apply() throws ConfigurationException {
        reportThresholdComponent.apply();
    }

    @Override
    public void disposeUIResources() {
        reportThresholdComponent = null;
    }

    @Override
    public void reset() {
        reportThresholdComponent.reset();
    }
}
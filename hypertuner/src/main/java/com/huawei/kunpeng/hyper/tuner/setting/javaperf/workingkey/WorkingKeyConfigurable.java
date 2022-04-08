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

package com.huawei.kunpeng.hyper.tuner.setting.javaperf.workingkey;

import static com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant.WORKING_KEY;

import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.WorkingKeyPanel;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Java性能分析-工作密钥WorkingKeyConfigurable
 *
 * @since 2021-07-09
 */
public class WorkingKeyConfigurable implements Configurable {
    /**
     * 工作密钥页面
     */
    public WorkingKeyPanel workingKeyPanelComponent;

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return WORKING_KEY;
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (workingKeyPanelComponent == null) {
            workingKeyPanelComponent = new WorkingKeyPanel();
        }

        return workingKeyPanelComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return workingKeyPanelComponent != null && workingKeyPanelComponent.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        workingKeyPanelComponent.apply();
    }

    @Override
    public void disposeUIResources() {
        workingKeyPanelComponent = null;
    }
}
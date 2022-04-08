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

package com.huawei.kunpeng.porting.action.setting.weakpwd;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;
import com.huawei.kunpeng.porting.ui.panel.weakpwdpanel.WeakPwdSetPanel;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 添加弱口令面板创建
 *
 * @since 2012-10-12
 */
public class WeakPwdSetConfigurable implements SearchableConfigurable {
    private static final String ID = "Weak Password Dictionary";

    private static final String DISPLAY_NAME = PortingWeakPwdConstant.WEAK_PASSWORD_DIC;

    /**
     * 设置主面板
     */
    public WeakPwdSetPanel weakPwdSetPanel;

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
        if (weakPwdSetPanel == null) {
            weakPwdSetPanel = new WeakPwdSetPanel(null);
        }
        return weakPwdSetPanel.getPanel();
    }

    /**
     * 是否修改判断
     *
     * @return boolean
     */
    @Override
    public boolean isModified() {
        return weakPwdSetPanel != null && weakPwdSetPanel.isModified();
    }

    /**
     * 点击Apply事件
     *
     * @throws ConfigurationException 异常
     */
    @Override
    public void apply() throws ConfigurationException {
        if (weakPwdSetPanel != null) {
            weakPwdSetPanel.apply();
            Logger.info("Del Weak Password");
        }
    }

    @Override
    public void reset() {
        weakPwdSetPanel.reset();
    }

    /**
     * disposeUIResources方法
     */
    @Override
    public void disposeUIResources() {
        weakPwdSetPanel = null;
    }
}

/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.porting.ui.panel.log;

import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.ui.panel.LogManagerPanel;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 日志主界面
 *
 * @since 2020-10-16
 */
public class LogConfigurablePanel implements Configurable {
    private LogManagerPanel mySettingsComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return PortingUserManageConstant.getLogTitle();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new LogManagerPanel();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return mySettingsComponent != null && mySettingsComponent.isModified();
    }

    @Override
    public void apply() {
        mySettingsComponent.apply();
    }

    @Override
    public void reset() {
        mySettingsComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}

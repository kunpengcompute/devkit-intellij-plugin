/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.hyper.tuner.setting.log;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningLogManagerPanel;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 日志主界面
 *
 * @since 2020-10-16
 */
public class LogConfigurable implements Configurable {
    private TuningLogManagerPanel mySettingsComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return TuningUserManageConstant.getLogTitle();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new TuningLogManagerPanel();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return mySettingsComponent != null && mySettingsComponent.isModified();
    }

    @Override
    public void reset() {
        mySettingsComponent.reset();
    }

    @Override
    public void apply() {
        mySettingsComponent.apply();
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
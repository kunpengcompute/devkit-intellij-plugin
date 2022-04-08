/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.porting.ui.panel.log;

import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.porting.action.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

/**
 * 用户管理设置界面配置器
 *
 * @since 2020-10-06
 */
public class LogConfigProvider extends ConfigurableAbstractProvider {
    @Nullable
    @Override
    public Configurable createConfigurable() {
        if (StringUtil.stringIsEmpty(PortingUserInfoContext.getInstance().getRole())) {
            return null; // 用户角色为空不显示。
        }
        return new LogConfigurablePanel();
    }

    @Override
    public boolean isCreate() {
        if (StringUtil.stringIsEmpty(PortingUserInfoContext.getInstance().getRole())) {
            return false; // 用户角色为空不显示。
        }
        return true;
    }
}

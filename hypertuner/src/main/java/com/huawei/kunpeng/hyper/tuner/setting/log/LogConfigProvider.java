/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.kunpeng.hyper.tuner.setting.log;

import com.huawei.kunpeng.hyper.tuner.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

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
        if (StringUtil.stringIsEmpty(UserInfoContext.getInstance().getRole())) {
            // 用户角色为空不显示。
            return null;
        }
        return new LogConfigurable();
    }

    @Override
    public boolean isCreate() {
        // 用户角色为空不显示。
        return !StringUtil.stringIsEmpty(UserInfoContext.getInstance().getRole());
    }
}
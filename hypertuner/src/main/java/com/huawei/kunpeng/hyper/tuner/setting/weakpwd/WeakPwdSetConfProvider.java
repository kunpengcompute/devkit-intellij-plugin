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

package com.huawei.kunpeng.hyper.tuner.setting.weakpwd;

import com.huawei.kunpeng.hyper.tuner.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

/**
 * 添加弱口令面板XML配置类
 *
 * @since 2012-10-12
 */
public class WeakPwdSetConfProvider extends ConfigurableAbstractProvider {
    private static final String ADMIN_USR_ID = "1";

    @Override
    public boolean isCreate() {
        // 用户角色为空或者非管理员时不显示
        return !ValidateUtils.isEmptyString(UserInfoContext.getInstance().getRole())
                && !ValidateUtils.isEmptyString(UserInfoContext.getInstance().getLoginId());
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new WeakPwdSetConfigurable();
    }
}
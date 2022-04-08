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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.hyper.tuner.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Java性能分析系统设置-WorkingKsyConfigprovider
 *
 * @since 2021-07-09
 */
public class WorkingKeyConfigProvider extends ConfigurableAbstractProvider {
    @Override
    public boolean isCreate() {
        return !ValidateUtils.isEmptyString(UserInfoContext.getInstance().getRole())
                && !ValidateUtils.isEmptyString(UserInfoContext.getInstance().getLoginId());
    }

    @Override
    public @Nullable Configurable createConfigurable() {
        return new WorkingKeyConfigurable();
    }

    /**
     * 判断运行时条件是否满足
     *
     * @return isLogin
     */
    @Override
    public boolean canCreateConfigurable() {
        return UserInfoContext.getInstance().getUserName() != null
                && Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole());
    }
}
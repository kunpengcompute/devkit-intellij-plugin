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

package com.huawei.kunpeng.porting.action.setting.user;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 用户管理设置界面配置器
 *
 * @since 2020-10-06
 */
public class UseSettingsConfProvider extends ConfigurableAbstractProvider {
    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new AdminUserSettingsConfigurable();
    }

    @Override
    public boolean isCreate() {
        return ValidateUtils.isNotEmptyString(PortingUserInfoContext.getInstance().getRole())
            && Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole());
    }
}

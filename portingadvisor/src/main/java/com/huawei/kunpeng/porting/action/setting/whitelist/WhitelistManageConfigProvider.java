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

package com.huawei.kunpeng.porting.action.setting.whitelist;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.porting.action.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

/**
 * WhitelistManageConfigProvider
 *
 * @since 2020-10-07
 */
public class WhitelistManageConfigProvider extends ConfigurableAbstractProvider {
    @Nullable
    @Override
    public Configurable createConfigurable() {
        if (ValidateUtils.isEmptyString(PortingUserInfoContext.getInstance().getRole())) {
            return null; // 用户角色为空不显示。
        }
        if (USER_ROLE_ADMIN.equals(PortingUserInfoContext.getInstance().getRole())) {
            return new WhitelistManageConfigurable();
        }
        return null;
    }

    @Override
    public boolean isCreate() {
        if (ValidateUtils.isEmptyString(PortingUserInfoContext.getInstance().getRole())) {
            return false; // 用户角色为空不显示。
        }
        return USER_ROLE_ADMIN.equals(PortingUserInfoContext.getInstance().getRole());
    }
}

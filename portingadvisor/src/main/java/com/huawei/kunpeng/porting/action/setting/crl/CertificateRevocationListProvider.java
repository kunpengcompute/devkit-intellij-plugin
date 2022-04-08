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

package com.huawei.kunpeng.porting.action.setting.crl;

import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.porting.action.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

/**
 * The class: CertificateRevocationListProvider 证书吊销列表 Provider
 *
 * @since 2021-8-10
 */
public class CertificateRevocationListProvider extends ConfigurableAbstractProvider {
    @Override
    public boolean isCreate() {
        if (StringUtil.stringIsEmpty(PortingUserInfoContext.getInstance().getRole())) {
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public Configurable createConfigurable() {
        if (StringUtil.stringIsEmpty(PortingUserInfoContext.getInstance().getRole())) {
            return null;
        }
        return new CertificateRevocationListConfigurable();
    }
}

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

package com.huawei.kunpeng.porting.ui.panel.settings.webservercert;

import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;

import org.jetbrains.annotations.Nullable;

/**
 * WebServerCertificateProvider
 *
 * @since 2020-10-07
 */
public class WebServerCertificateProvider extends ConfigurableProvider {
    /**
     * 判断运行时条件是否满足
     *
     * @return isLogin
     */
    @Override
    public boolean canCreateConfigurable() {
        return PortingUserInfoContext.getInstance().getUserName() != null;
    }

    /**
     * 若运行时条件满足，创建并返回ScanParameterConfigurable对象
     *
     * @return ScanParameterConfigurable
     */
    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new PortingWebServerCertificate();
    }
}

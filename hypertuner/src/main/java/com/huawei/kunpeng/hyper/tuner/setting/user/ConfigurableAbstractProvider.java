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

package com.huawei.kunpeng.hyper.tuner.setting.user;

import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;

import com.intellij.openapi.options.ConfigurableProvider;

/**
 * setting 入口类，执行自动登录
 *
 * @since 2020-09-25
 */
public abstract class ConfigurableAbstractProvider extends ConfigurableProvider {
    /**
     * 执行自动登录之后，返回当前是否展示面板
     *
     * @return boolean isCreate()
     */
    @Override
    public boolean canCreateConfigurable() {
        TuningLoginUtils.autoLogin();
        return isCreate();
    }

    /**
     * 是否创建面板
     *
     * @return boolean 是否创建
     */
    public abstract boolean isCreate();
}
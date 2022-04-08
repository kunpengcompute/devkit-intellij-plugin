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

package com.huawei.kunpeng.hyper.tuner.setting.sysperf.schtask;

import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigurable;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

/**
 * 预约任务 配置类
 *
 * @since 2012-10-12
 */
public class SchTaskConfigProvider extends ConfigurableAbstractProvider {
    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new SettingCommonConfigurable(SchTaskContent.SCH_TASK_DIC, new SchTaskPanel());
    }

    /**
     * 执行自动登录之后，返回当前是否展示面板
     * 判断用户是否登录
     *
     * @return boolean isCreate()
     */
    @Override
    public boolean isCreate() {
        return UserInfoContext.getInstance().getUserName() != null;
    }

}
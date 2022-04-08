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

package com.huawei.kunpeng.hyper.tuner.setting.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigurable;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SysperfSettingsPanel;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;

import org.jetbrains.annotations.Nullable;

/**
 * PortingSettingsConfigProvider
 *
 * @since 2020-10-07
 */
public class SysperfConfigProvider extends ConfigurableProvider {
    /**
     * 为保障代码结构统一，仍用ConfigurableProvider进行运行时条件保证
     * 实际上主入口不依赖运行时条件，因此固定返回true
     *
     * @return boolean
     */
    @Override
    public boolean canCreateConfigurable() {
        if (UserInfoContext.getInstance().getUserName() != null) {
            String installInfo = LeftTreeAction.instance().getInstallInfo();
            return "all".equals(installInfo) || "sys_perf".equals(installInfo);
        }
        return false;
    }

    /**
     * 若运行时条件满足，创建并返回PortingSettingsConfigurable对象
     *
     * @return PortingSettingsConfigurable
     */
    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new SettingCommonConfigurable(TuningI18NServer.toLocale("plugins_common_hyper_tuner_sysperf"),
            new SysperfSettingsPanel());
    }
}
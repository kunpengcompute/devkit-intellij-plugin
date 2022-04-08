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

package com.huawei.kunpeng.hyper.tuner.setting.sysperf.agentcert;

import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigurable;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.hyper.tuner.setting.user.ConfigurableAbstractProvider;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.AgentCertPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nullable;

/**
 * 系统性能分析-预约任务面板XML配置类
 *
 * @since 2012-10-12
 */
public class AgentCertConfProvider extends ConfigurableAbstractProvider {
    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new SettingCommonConfigurable(AgentCertContent.AGENT_CERT_DIC, new AgentCertPanel());
    }

    @Override
    public boolean isCreate() {
        // 判断用户是否登录
        return UserInfoContext.getInstance().getUserName() != null;
    }
}
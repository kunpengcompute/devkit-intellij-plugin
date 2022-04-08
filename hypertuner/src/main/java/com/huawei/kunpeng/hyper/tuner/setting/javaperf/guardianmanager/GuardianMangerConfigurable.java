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

package com.huawei.kunpeng.hyper.tuner.setting.javaperf.guardianmanager;

import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigurable;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianMangerPanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * JavaPerf-目标环境管理配置类
 *
 * @since 2021-07-10
 */
public class GuardianMangerConfigurable extends SettingCommonConfigurable {
    /**
     * 设置主面板
     */
    private static GuardianMangerPanel guardianMangerPanel;

    public GuardianMangerConfigurable(String displayName,
        SettingCommonConfigPanel commonConfigPanel) {
        super(displayName, commonConfigPanel);
    }

    /**
     * 返回该页面用户后续刷页面。
     *
     * @return 返回配置页面
     */
    public static GuardianMangerPanel getGuardianSettingsComponent() {
        return guardianMangerPanel;
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        if (guardianMangerPanel == null) {
            guardianMangerPanel = new GuardianMangerPanel();
        } else {
            guardianMangerPanel.updateGuardianTable();
        }

        return guardianMangerPanel.getPanel();
    }
}
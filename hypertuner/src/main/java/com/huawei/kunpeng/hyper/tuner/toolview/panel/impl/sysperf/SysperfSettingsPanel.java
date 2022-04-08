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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Sysperf设置顶层节点面板
 *
 * @since 2020-10-07
 */
public class SysperfSettingsPanel extends SettingCommonConfigPanel {
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JLabel portingSettingsDesc;

    /**
     * 构造函数
     */
    public SysperfSettingsPanel() {
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (!ValidateUtils.isEmptyString(UserInfoContext.getInstance().getUserName())) {
            portingSettingsDesc.setText(
                UserInfoContext.getInstance().getUserName()
                    + " - "
                    + TuningI18NServer.toLocale("plugins_common_hyper_tuner_settings_tuningSettingsDesc"));
        } else {
            portingSettingsDesc.setText("Please login to Kunpeng porting advisor firstly."); // 查看用户临时方案，后续需删除
        }
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        registerComponentAction();
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}

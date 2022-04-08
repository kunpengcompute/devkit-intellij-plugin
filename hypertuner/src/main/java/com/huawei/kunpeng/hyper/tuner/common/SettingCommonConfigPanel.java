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

package com.huawei.kunpeng.hyper.tuner.common;

import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import javax.swing.JPanel;

/**
 * 功能描述
 *
 * @since 2021年09月09日
 */
public abstract class SettingCommonConfigPanel extends IDEBasePanel {
    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * apply按钮添加事件
     */
    public void apply() {
    }

    /**
     * 是否添加apply
     *
     * @return 结果
     */
    public boolean isModified() {
        return false;
    }

    /**
     * 重置界面
     */
    public void reset() {
    }

    /**
     * 获取对应面板
     *
     * @return mainPanel
     */
    public abstract JPanel getPanel();
}

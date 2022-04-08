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

package com.huawei.kunpeng.hyper.tuner.setting.sysperf.nodemanager;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.NodeManagerPanel;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 添加预约任务面板创建
 *
 * @since 2012-10-12
 */
public class NodeMangerConfigurable implements Configurable {
    private static final String DISPLAY_NAME = NodeManagerContent.NODE_MANAGER_DIC;

    /**
     * 设置主面板
     */
    private static NodeManagerPanel nodeManagerPanel;

    /**
     * 返回该页面用户后续刷页面。
     *
     * @return 返回配置页面
     */
    public static NodeManagerPanel getNodeManageSettingsComponent() {
        return nodeManagerPanel;
    }

    /**
     * 获取显示名称
     *
     * @return 显示名称
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return nodeManagerPanel.getPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (nodeManagerPanel == null) {
            nodeManagerPanel = new NodeManagerPanel();
        } else {
            nodeManagerPanel.updateNodeTable();
        }

        return nodeManagerPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return nodeManagerPanel != null && nodeManagerPanel.isModified();
    }

    @Override
    public void apply() {
        nodeManagerPanel.apply();
    }

    @Override
    public void disposeUIResources() {
    }

    @Override
    public void reset() {
        nodeManagerPanel.reset();
    }
}
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

package com.huawei.kunpeng.devkit.actions;

import com.huawei.kunpeng.devkit.MarketPlaceManager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;

import org.jetbrains.annotations.NotNull;

/**
 * The Class PluginAction
 *
 * @since 2021-07-09
 */
public class PluginAction extends AnAction {
    private final PluginActionOpt action;
    private final PluginId pluginId;

    /**
     * 构造函数
     *
     * @param pluginId 插件id
     * @param action 操作
     */
    public PluginAction(PluginId pluginId, PluginActionOpt action) {
        super(action.getLabel());
        this.pluginId = pluginId;
        this.action = action;
    }

    /**
     * Action Performed
     *
     * @param anActionEvent anActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (pluginId == null) {
            return;
        }
        switch (action) {
            case ENABLE:
                MarketPlaceManager.enablePlugin(pluginId);
                break;
            case INSTALL:
            case UPDATE:
                MarketPlaceManager.installPlugin(new PluginId[]{pluginId});
                break;
            case DISABLE:
                MarketPlaceManager.disablePlugin(pluginId);
                break;
            case UNINSTALL:
                MarketPlaceManager.uninstallPlugin(pluginId);
        }
    }
}
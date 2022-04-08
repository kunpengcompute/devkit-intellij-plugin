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

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 按钮组
 *
 * @since 2021-05-17
 */
public class PluginActionGroup extends ActionGroup {
    private final PluginId pluginId;

    /**
     * PluginActionGroup PluginActionGroup
     *
     * @param pluginId pluginId
     */
    public PluginActionGroup(PluginId pluginId) {
        this.pluginId = pluginId;
    }

    /**
     * getChildren getChildren
     *
     * @param anActionEvent anActionEvent
     * @return AnAction[]
     */
    @NotNull
    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
        if (!MarketPlaceManager.isInstalled(pluginId)) {
            return new AnAction[]{new PluginAction(pluginId, PluginActionOpt.INSTALL)};
        }

        List<AnAction> anActions = new ArrayList<>(3);
        if (MarketPlaceManager.isEnable(pluginId)) {
            anActions.add(new PluginAction(pluginId, PluginActionOpt.DISABLE));
            if (MarketPlaceManager.hasNewerVersion(pluginId)) {
                anActions.add(new PluginAction(pluginId, PluginActionOpt.UPDATE));
            }
        } else {
            anActions.add(new PluginAction(pluginId, PluginActionOpt.ENABLE));
        }

        anActions.add(new PluginAction(pluginId, PluginActionOpt.UNINSTALL));
        AnAction[] anActionArr = new AnAction[anActions.size()];
        anActions.toArray(anActionArr);
        return anActionArr;
    }
}
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

package com.huawei.kunpeng.porting.action.toolwindow;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import org.jetbrains.annotations.NotNull;

/**
 * The class ToolMaintenanceActionGroup: Porting 软件功能：Uninstall & Upgrade
 *
 * @since v1.0
 */
public final class ToolMaintenanceActionGroup extends DefaultActionGroup {
    private static final String MAINTENANCE = I18NServer.toLocale("plugins_porting_lefttree_maintenance");
    private boolean isInitialized = false;

    /**
     * 构造函数
     */
    public ToolMaintenanceActionGroup() {
        super(MAINTENANCE, true);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }

    /**
     * 更新Action对应的Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        if (!isInitialized) {
            for (ToolMaintenanceAction.Action action : ToolMaintenanceAction.Action.values()) {
                add(new ToolMaintenanceAction(action));
            }
            isInitialized = true;
        }
        event.getPresentation().setEnabled(true);
    }
}

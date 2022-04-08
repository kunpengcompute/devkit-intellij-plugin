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
import com.huawei.kunpeng.porting.action.uninstall.IDEUninstallAction;
import com.huawei.kunpeng.porting.action.upgrade.IDEUpgradeAction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

import org.jetbrains.annotations.NotNull;

/**
 * The class ToolMaintenanceAction
 *
 * @since v1.0
 */
public final class ToolMaintenanceAction extends DumbAwareAction {
    private static final String UPGRADE_TEXT = I18NServer.toLocale("plugins_porting_lefttree_upgrade");
    private static final String UNINSTALL_TEXT = I18NServer.toLocale("plugins_porting_lefttree_uninstall");

    @NotNull
    private Action action;

    /**
     * 构造函数
     *
     * @param action action
     */
    public ToolMaintenanceAction(@NotNull Action action) {
        super(action.text, null, null);
        this.action = action;
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        action.anAction.actionPerformed(event);
    }

    /**
     * 更新Action对应的Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(true);
    }


    /**
     * The enum: Action
     *
     * @since v1.0
     */
    public enum Action {
        UNINSTALL(UNINSTALL_TEXT, new IDEUninstallAction()),
        UPGRADE(UPGRADE_TEXT, new IDEUpgradeAction());

        private String text;
        private AnAction anAction;


        /**
         * Constructor
         *
         * @param text     show in hover
         * @param anAction anAction
         */
        Action(String text, AnAction anAction) {
            this.text = text;
            this.anAction = anAction;
        }
    }
}

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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning;

import com.huawei.kunpeng.hyper.tuner.action.uninstall.TuningIDEUninstallAction;
import com.huawei.kunpeng.hyper.tuner.action.upgrade.TuningIDEUpgradeAction;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;

import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.UninstallEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.UpgradeServerEditor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

import org.jetbrains.annotations.NotNull;

/**
 * The class ToolMaintenanceAction
 *
 * @since v1.0
 */
public final class ToolMaintenanceAction extends DumbAwareAction {
    private static final String INSTALL_TEXT = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_install");
    private static final String UPGRADE_TEXT = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_upgrade");
    private static final String UNINSTALL_TEXT = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_uninstall");

    @NotNull
    private final Action action;

    /**
     * 构造函数
     *
     * @param action action
     */
    public ToolMaintenanceAction(@NotNull Action action) {
        super(action.toString(), null, null);
        this.action = action;
    }

    /**
     * The enum: Action
     *
     * @since v1.0
     */
    public enum Action {
        UPGRADE,
        UNINSTALL;

        @Override
        public String toString() {
            switch (this) {
                case UNINSTALL:
                    return UNINSTALL_TEXT;
                case UPGRADE:
                    return UPGRADE_TEXT;
            }
            return super.toString();
        }

        /**
         * 响应
         *
         * @param event AnActionEvent
         */
        public void applyTo(AnActionEvent event) {
            switch (this) {
                case UNINSTALL:
//                    new TuningIDEUninstallAction().actionPerformed(event);
                    UninstallEditor.openPage();
                    break;
                case UPGRADE:
//                    new TuningIDEUpgradeAction().actionPerformed(event);
                    UpgradeServerEditor.openPage();
                    break;
            }
        }
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        action.applyTo(event);
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
}

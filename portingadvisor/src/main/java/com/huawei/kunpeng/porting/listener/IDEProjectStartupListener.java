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

package com.huawei.kunpeng.porting.listener;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import org.jetbrains.annotations.NotNull;

/**
 * 项目启动监听
 *
 * @since 2020.12.11
 */
public class IDEProjectStartupListener implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        closePrefIntellijOpenTerminal(project);
    }

    /**
     * 关闭上次intellij退出时的打开终端
     *
     * @param project 项目
     */
    private static void closePrefIntellijOpenTerminal(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (toolWindow == null) {
            return;
        }
        Content[] contents = toolWindow.getContentManager().getContents();
        for (Content content : contents) {
            if (I18NServer.handleTab(content, "plugins_porting_install_title")
                    || I18NServer.handleTab(content, "plugins_porting_uninstall_title")
                    || I18NServer.handleTab(content, "plugins_porting_upgrade_title")) {
                toolWindow.getContentManager().removeContent(content, true);
            }
        }
    }
}

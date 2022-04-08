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

package com.huawei.kunpeng.devkit.toolview;

import com.huawei.kunpeng.devkit.MarketPlace;
import com.huawei.kunpeng.devkit.common.i18n.DevkitI18NServer;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.annotations.NotNull;

/**
 * The class PortingToolWindowFactory
 *
 * @since v1.0
 */
public class DevkitToolWindowFactory implements ToolWindowFactory {
    private Project project;
    private String panelName;
    private IDEBasePanel mainPanel;

    /**
     * 设置 toolWindow 国际化显示
     *
     * @param toolWindow toolWindow
     */
    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setStripeTitle(DevkitI18NServer.toLocale("plugins_devkit_name"));
    }

    /**
     * 创建 toolWindow 面板
     *
     * @param project    project
     * @param toolWindow toolWindow
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(new MarketPlace(), "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.show();
    }
}
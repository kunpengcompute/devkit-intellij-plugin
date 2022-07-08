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

package com.huawei.kunpeng.intellij.ui.utils;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LeftTreeLoadingPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import java.util.HashMap;

/**
 * 弹框工具类
 *
 * @since 1.0.0
 */
public class UIUtils {
    /**
     * toolWindow面板更换
     *
     * @param destPanel destPanelName
     * @param toolWindow toolWindow
     */
    public static void changeToolWindowToDestPanel(IDEBasePanel destPanel, ToolWindow toolWindow) {
        if (toolWindow == null) {
            return;
        }
        Content[] contents = toolWindow.getContentManager().getContents();
        for (Content cont : contents) {
            cont.dispose();
            toolWindow.getContentManager().removeContent(cont, false);
        }
        if (destPanel == null) {
            Logger.error("changeToolWindowToDestPanel destPanel is null.");
            return;
        }
        toolWindow.getContentManager().addContent(destPanel.getContent());
        toolWindow.getContentManager().setSelectedContent(destPanel.getContent());
    }

    /**
     * 左侧树面板更换为loading面板
     *
     * @param project 打开project
     * @param loadingText loading面板显示内容
     * @param toolWindowId 工具窗口id
     */
    public static void changeToolWindowToLoadingPanel(Project project, String loadingText, String toolWindowId) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
        if (toolWindow == null) {
            return;
        }
        IDEBasePanel destPanel;
        if (loadingText == null) {
            destPanel = new LeftTreeLoadingPanel(toolWindow, project);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put("loadingText", loadingText);
            destPanel = new LeftTreeLoadingPanel(toolWindow, param);
        }
        UIUtils.changeToolWindowToDestPanel(destPanel, toolWindow);
    }
}

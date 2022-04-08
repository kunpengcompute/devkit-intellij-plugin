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
import com.huawei.kunpeng.devkit.toolview.ui.PluginPanel;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LeftTreeLoadingPanel;

import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;


/**
 * The class LeftTreeUtil: 左侧树工具类
 *
 * @since 2020-12-03
 */
public class LeftMainPanelUtil {
    /**
     * 刷新插件入口左侧按钮状态
     *
     * @param pluginId 插件id
     */
    public static void refreshLeftMainPanel(PluginId pluginId) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(CommonUtil.getDefaultProject()).getToolWindow("Devkit");
        ContentManager contentManager = toolWindow.getContentManager();
        JComponent component = contentManager.getContent(0).getComponent();
        MarketPlace marketPlace = new MarketPlace();
        if (component instanceof MarketPlace) {
            marketPlace = (MarketPlace) component;
        }
        Map<PluginId, PluginPanel> idPanelMap = marketPlace.getIdPanelMap();
        PluginPanel pluginPanel = idPanelMap.get(pluginId);
        pluginPanel.refreshRequiredButton();
    }

    /**
     * 左侧树面板更换
     *
     * @param destPanel  destPanelName
     * @param toolWindow toolWindow
     */
    public static void changeLeftTreeToDestPanel(IDEBasePanel destPanel, ToolWindow toolWindow) {
        if (toolWindow == null || destPanel == null) {
            return;
        }
        Content[] contents = toolWindow.getContentManager().getContents();
        for (Content cont : contents) {
            cont.dispose();
            toolWindow.getContentManager().removeContent(cont, false);
        }
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(destPanel, "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().setSelectedContent(content);
    }

    /**
     * 左侧树面板更换为loading面板
     *
     * @param project     打开project
     * @param loadingText loading面板显示内容
     */
    public static void leftTreeLoading(Project project, String loadingText) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Devkit");
        Content[] contents = toolWindow.getContentManager().getContents();
        for (Content cont : contents) {
            cont.dispose();
            toolWindow.getContentManager().removeContent(cont, false);
        }
        IDEBasePanel destPanel;
        if (loadingText == null) {
            destPanel = new LeftTreeLoadingPanel(toolWindow, project);
        } else {
            HashMap<String, String> param = new HashMap<>(16);
            param.put("loadingText", loadingText);
            destPanel = new LeftTreeLoadingPanel(toolWindow, param);
        }
        toolWindow.getContentManager().addContent(destPanel.getContent());
        toolWindow.getContentManager().setSelectedContent(destPanel.getContent());
    }
}
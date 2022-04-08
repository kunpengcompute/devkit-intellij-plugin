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

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeConfigPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeLoginPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

/**
 * The class LeftTreeUtil: 左侧树工具类
 *
 * @since 2020-12-03
 */
public class LeftTreeUtil {
    /**
     * 登录成功后，左侧树面板更换为sysperf
     *
     * @param project    project
     * @param toolWindow toolWindow
     */
    public static void changeLeftTreeToLoginAfter(Project project, ToolWindow toolWindow) {
        if (toolWindow == null) {
            return;
        }
        // 如果打开多个project， 同步每一个project左侧树状态
        Content[] contents = toolWindow.getContentManager().getContents();
        for (Content cont : contents) {
            cont.dispose();
            toolWindow.getContentManager().removeContent(cont, false);
        }
        String installInfo = LeftTreeAction.instance().getInstallInfo();
        // 只有登录成功在展示
        if ("all".equals(installInfo)) {
            LeftTreeSysperfPanel sysPerf = new LeftTreeSysperfPanel(toolWindow);
            JavaPerfToolWindowPanel javaPerf = new JavaPerfToolWindowPanel(toolWindow, project);
            Content javaContent = toolWindow.getContentManager().getFactory()
                    .createContent(javaPerf.getMainPanel(), TuningIDEConstant.JAVA_PROFILER, true);
            Content sysContent = toolWindow.getContentManager().getFactory()
                    .createContent(sysPerf.getMainPanel(), TuningIDEConstant.SYSTEM_PROFILER, true);
            toolWindow.getContentManager().addContent(sysContent);
            toolWindow.getContentManager().addContent(javaContent);
            toolWindow.getContentManager().setSelectedContent(sysContent);
        } else if ("sys_perf".equals(installInfo)) {
            LeftTreeSysperfPanel sysPerf = new LeftTreeSysperfPanel(toolWindow);
            Content sysContent = toolWindow.getContentManager().getFactory()
                    .createContent(sysPerf.getMainPanel(), TuningIDEConstant.SYSTEM_PROFILER, true);
            toolWindow.getContentManager().addContent(sysContent);
            toolWindow.getContentManager().setSelectedContent(sysContent);
        } else if ("java_perf".equals(installInfo)) {
            JavaPerfToolWindowPanel javaPerf = new JavaPerfToolWindowPanel(toolWindow, project);
            Content javaContent = toolWindow.getContentManager().getFactory()
                    .createContent(javaPerf.getMainPanel(), TuningIDEConstant.JAVA_PROFILER, true);
            toolWindow.getContentManager().addContent(javaContent);
            toolWindow.getContentManager().setSelectedContent(javaContent);
        } else {
            Logger.info("No Install Info");
        }
    }

    /**
     * 刷新左侧树面板
     */
    public static void refreshLeftTreePanel() {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeUtil.changeLeftTreeToLoginAfter(project, toolWindow);
        }
    }

    /**
     * 刷新左侧树面板为登录面板
     */
    public static void refresh2LoginPanel() {
        // 如果打开多个project， 同步每一个project左侧树状态
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project proj : openProjects) {
            // 配置服务器完成后刷新左侧树面板为已配置面板
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, proj);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
            // 重新配置后关闭所有打开的webview页面
            LeftTreeAction.instance().closeAllOpenedWebViewPage(proj);
        }
    }

    /**
     * 刷新左侧树面板为配置服务器面板
     */
    public static void refresh2ConfigPanel() {
        // 如果打开多个project， 同步每一个project左侧树状态
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project proj : openProjects) {
            // 配置服务器完成后刷新左侧树面板为配置服务器面板
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            LeftTreeConfigPanel leftTreeConfigPanel = new LeftTreeConfigPanel(toolWindow, proj);
            UIUtils.changeToolWindowToDestPanel(leftTreeConfigPanel, toolWindow);
            // 关闭打开的历史报告页面
            LeftTreeAction.instance().closeAllOpenedWebViewPage(proj);
        }
    }
}

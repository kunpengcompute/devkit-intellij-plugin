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

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.bean.PortingTaskBean;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeConfigPanel;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeLoginPanel;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeReportsPanel;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.List;

/**
 * The class LeftTreeUtil: 左侧树工具类
 *
 * @since 2020-12-03
 */
public class LeftTreeUtil {
    /**
     * 刷新左侧树历史报告
     */
    public static void refreshReports() {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        List<PortingTaskBean.Task> tasks = SourcePortingHandler.obtainAllTasks();
        PortingIDEContext.setReportsNum(tasks == null ? 0 : tasks.size());
        for (Project project : openProjects) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            IDEBasePanel destPanel = new LeftTreeReportsPanel(toolWindow, project, tasks);
            UIUtils.changeToolWindowToDestPanel(destPanel, toolWindow);
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
            ToolWindow toolWindow = ToolWindowManager.getInstance(proj).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            LeftTreeLoginPanel leftTreeLoginPanel = new LeftTreeLoginPanel(toolWindow, proj);
            UIUtils.changeToolWindowToDestPanel(leftTreeLoginPanel, toolWindow);
            // 重新配置后关闭打开的历史报告页面
            DeleteAllReportsAction.closeAllOpenedReports(proj);
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
            ToolWindow toolWindow = ToolWindowManager.getInstance(proj).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            LeftTreeConfigPanel leftTreeConfigPanel = new LeftTreeConfigPanel(toolWindow, proj);
            UIUtils.changeToolWindowToDestPanel(leftTreeConfigPanel, toolWindow);
            // 关闭打开的历史报告页面
            DeleteAllReportsAction.closeAllOpenedReports(proj);
        }
    }

    /**
     * 刷新左侧树面板为配置服务器面板
     */
    public static void refreshLeftPanel() {
        int value = PortingIDEContext.getPortingIDEPluginStatus().value();
        if (value < IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            refresh2ConfigPanel();
        } else if (value == IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            refresh2LoginPanel();
        } else {
            refreshReports();
        }
    }
}

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
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.FeedBackAction;
import com.huawei.kunpeng.intellij.ui.action.HelpAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.bean.PortingTaskBean;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeConfigPanel;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeLoginPanel;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeReportsPanel;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The class PortingToolWindowFactory
 *
 * @since v1.0
 */
public class PortingToolWindowFactory implements ToolWindowFactory {
    private Project project;
    private IDEBasePanel mainPanel;

    /**
     * 设置 toolWindow 国际化显示
     *
     * @param toolWindow toolWindow
     */
    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setStripeTitle(I18NServer.toLocale("plugins_porting_advisor_name"));
    }

    /**
     * 创建 toolWindow 面板
     *
     * @param project    project
     * @param toolWindow toolWindow
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        if (toolWindow instanceof ToolWindowEx) {
            // 设置按钮增添
            setupToolWindow((ToolWindowEx) toolWindow);
            showCorrectPanel(toolWindow);
            if (mainPanel == null) {
                Logger.error("createToolWindowContent mainPanel is null.");
                return;
            }
            toolWindow.getContentManager().addContent(mainPanel.getContent());
            // toolWindow 弹出
            toolWindow.show();
        }
    }

    /**
     * 根据状态左侧树展示不同的面板视图
     *
     * @param toolWindow toolWindow
     */
    private void showCorrectPanel(@NotNull ToolWindow toolWindow) {
        // 自动登录
        LoginUtils.autoLogin();
        int curStatus = PortingIDEContext.getPortingIDEPluginStatus().value();
        if (StringUtil.stringIsEmpty(PortingCommonUtil.readCurIpFromConfig())) {
            mainPanel = new LeftTreeConfigPanel(toolWindow, project);
        } else if (curStatus >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            // 如果此时处于用户登录的状态，应该打开左侧树刷新历史报告
            List<PortingTaskBean.Task> tasks = SourcePortingHandler.obtainAllTasks();
            if (tasks != null) {
                PortingIDEContext.setReportsNum(tasks.size());
            }
            mainPanel = new LeftTreeReportsPanel(toolWindow, project, tasks);
        } else {
            // 此时处于服务器配置完成状态
            mainPanel = new LeftTreeLoginPanel(toolWindow, project);
            PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        }
    }

    /**
     * 左侧树设置功能增加对应的功能
     *
     * @param window window
     */
    private void setupToolWindow(@NotNull ToolWindowEx window) {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ConfigServerActionGroup());
        group.add(new UserLoginAction());
        group.add(new UserActionGroup());
        group.add(new SettingsActionGroup());
        group.add(new ToolMaintenanceActionGroup());
        group.addSeparator();
        if (("zh").equals(I18NServer.getCurrentLocale().getLanguage())) {
            group.add(new FeedBackAction(I18NServer.toLocale("plugins_porting_feedback")));
        }
        group.add(new HelpAction(I18NServer.toLocale("plugins_porting_help")));
        group.add(new DisclaimerAction());
        group.add(new PortingAboutAction());
        group.addSeparator();
        window.setAdditionalGearActions(group);
    }
}

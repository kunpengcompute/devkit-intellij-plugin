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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningConfigSuccessPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningLoginSuccessPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningServerConfigPanel;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.huawei.kunpeng.intellij.ui.action.FeedBackAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 左侧窗口ToolWindowFactory
 *
 * @since 2022-06-28
 */
public class HyperTunerToolWindowFactory implements ToolWindowFactory {
    private Project project;
    private IDEBasePanel mainPanel;

    /**
     * 设置 toolWindow 国际化显示
     *
     * @param toolWindow toolWindow
     */
    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setStripeTitle(TuningI18NServer.toLocale("plugins_hyper_tuner_name"));
    }

    /**
     * 创建 toolWindow 面板
     *
     * @param project project
     * @param toolWin toolWin
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWin) {
        this.project = project;
        if (toolWin instanceof ToolWindowEx) {
            // 设置按钮增添
            setupToolWindow((ToolWindowEx) toolWin);
            showCorrectPanel(toolWin);
            if (mainPanel == null) {
                Logger.error("createToolWindowContent mainPanel is null.");
                return;
            }
            toolWin.getContentManager().addContent(mainPanel.getContent());
            // toolWindow 弹出
            toolWin.show();
            toolWin.setAutoHide(false);
        }
    }

    /**
     * 根据状态左侧树展示不同的面板视图
     *
     * @param toolWindow toolWindow
     */
    private void showCorrectPanel(@NotNull ToolWindow toolWindow) {
        // 启动插件之后先关闭所有WebView页面
        int curStatus = TuningIDEContext.getTuningIDEPluginStatus().value();
        if (StringUtil.stringIsEmpty(CommonUtil.readCurIpFromConfig())) {
            // 未配置服务器
            mainPanel = new TuningServerConfigPanel(toolWindow, project);
        } else if (curStatus >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            // 已登录
            mainPanel = new TuningLoginSuccessPanel(toolWindow, project);
        } else {
            // 已配置服务器未登录
            mainPanel = new TuningConfigSuccessPanel(toolWindow, project);
        }
    }

    /**
     * 左侧树设置功能增加对应的功能
     *
     * @param window window
     */
    private void setupToolWindow(@NotNull ToolWindowEx window) {
        final DefaultActionGroup group = new DefaultActionGroup();

        // 部署服务器
        group.add(new DeployServerAction());
        // 配置服务器
        group.add(new ConfigRemoteServerAction());
        // 申请试用远程实验室
        group.add(new ApplyTrialAction());
        group.addSeparator();
        // 升级服务端/卸载服务端
        for (ToolMaintenanceAction.Action action : ToolMaintenanceAction.Action.values()) {
            group.add(new ToolMaintenanceAction(action));
        }
        group.addSeparator();
        // 建议反馈
        group.add(new FeedBackAction(TuningI18NServer.toLocale("plugins_hyper_tuner_feedback")));
        group.add(new TuningAboutAction());
        group.addSeparator();
        window.setAdditionalGearActions(group);

        // 刷新服务器连接按钮
        List<AnAction> customActions = new ArrayList<>();
        customActions.add(new RefreshConnectionAction());
        window.setTitleActions(customActions);
    }
}

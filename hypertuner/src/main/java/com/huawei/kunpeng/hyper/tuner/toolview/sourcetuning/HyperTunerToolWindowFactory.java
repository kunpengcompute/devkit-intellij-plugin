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
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeConfigPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeLoginPanel;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.FeedBackAction;
import com.huawei.kunpeng.intellij.ui.action.HelpAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;

import org.jetbrains.annotations.NotNull;

/**
 * The class PortingToolWindowFactory
 *
 * @since v1.0
 */
public class HyperTunerToolWindowFactory implements ToolWindowFactory {
    private Project project;
    private IDEBasePanel mainPanel;

    /**
     * ?????? toolWindow ???????????????
     *
     * @param toolWindow toolWindow
     */
    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setStripeTitle(TuningI18NServer.toLocale("plugins_hyper_tuner_name"));
    }

    /**
     * ?????? toolWindow ??????
     *
     * @param project    project
     * @param toolWin toolWin
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWin) {
        this.project = project;
        if (toolWin instanceof ToolWindowEx) {
            // ??????????????????
            setupToolWindow((ToolWindowEx) toolWin);
            // ???????????????????????????????????????webview??????
            LeftTreeAction.instance().closeAllOpenedWebViewPage(project);
            showCorrectPanel(toolWin);
            if (mainPanel == null) {
                Logger.error("createToolWindowContent mainPanel is null.");
                return;
            }
            toolWin.getContentManager().addContent(mainPanel.getContent());
            // toolWindow ??????
            toolWin.show();
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param toolWindow toolWindow
     */
    private void showCorrectPanel(@NotNull ToolWindow toolWindow) {
        // ????????????
        TuningLoginUtils.autoLogin();
        // ??????
        int curStatus = TuningIDEContext.getTuningIDEPluginStatus().value();
        if (StringUtil.stringIsEmpty(CommonUtil.readCurIpFromConfig())) {
            // ??????????????????
            mainPanel = new LeftTreeConfigPanel(toolWindow, project);
        } else if (curStatus >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            // ???????????????????????????????????????????????????????????????
            LeftTreeUtil.refreshLeftTreePanel();
        } else {
            // ???????????????????????????????????????
            mainPanel = new LeftTreeLoginPanel(toolWindow, project);
            TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param window window
     */
    private void setupToolWindow(@NotNull ToolWindowEx window) {
        final DefaultActionGroup group = new DefaultActionGroup();
        // ???????????????
        group.add(new ConfigRemoteServerAction());
        group.add(new UserLoginAction());
        group.add(new UserActionGroup());
        // ??????
        group.add(new SettingsActionGroup());
        // ????????????
        group.add(new ToolMaintenanceActionGroup());
        group.addSeparator();
        if (("zh").equals(I18NServer.getCurrentLocale().getLanguage())) {
            group.add(new FeedBackAction(TuningI18NServer.toLocale("plugins_hyper_tuner_feedback")));
        }
        group.add(new HelpAction(TuningI18NServer.toLocale("plugins_hyper_tuner_help")));
        group.add(new DisclaimerAction()); // ????????????
        // ??????????????????
        group.add(new TuningAboutAction());
        group.addSeparator();
        window.setAdditionalGearActions(group);
    }
}

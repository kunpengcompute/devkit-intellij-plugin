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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.InstallManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.TuningConfigSaveConfirmDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningConfigSuccessPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningServerConfigPanel;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.SaveConfirmPanel;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * Tuning common util extends CommonUtil
 *
 * @since 2022-06-28
 */
public class TuningCommonUtil extends CommonUtil {
    /**
     * 刷新左侧树面板为配置服务器面板
     */
    public static void refreshServerConfigPanel() {
        // 如果打开多个project， 同步每一个project左侧树状态
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project proj : openProjects) {
            // 配置服务器完成后刷新左侧树面板为配置服务器面板
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            TuningServerConfigPanel tuningServerConfigPanel = new TuningServerConfigPanel(toolWindow, proj);
            toolWindow.getContentManager().removeAllContents(true);
            toolWindow.getContentManager().addContent(tuningServerConfigPanel.getContent());
            toolWindow.getContentManager().setSelectedContent(tuningServerConfigPanel.getContent());
            AbstractWebFileProvider.closeAllWebViewPage();
        }
    }

    /**
     * 刷新左侧树面板为配置服务器成功面板
     */
    public static void refreshServerConfigSuccessPanel() {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project proj : openProjects) {
            // 配置服务器完成后刷新左侧树面板为配置服务器面板
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(proj).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            TuningConfigSuccessPanel tuningConfigSuccessPanel = new TuningConfigSuccessPanel(toolWindow, proj);
            toolWindow.getContentManager().removeAllContents(true);
            toolWindow.getContentManager().addContent(tuningConfigSuccessPanel.getContent());
            toolWindow.getContentManager().setSelectedContent(tuningConfigSuccessPanel.getContent());
            AbstractWebFileProvider.closeAllWebViewPage();
        }
    }

    /**
     * 展示切换服务器确认弹框
     */
    public static void showConfigSaveConfirmDialog() {
        IDEBasePanel createConfirmPanel = new SaveConfirmPanel(null);
        TuningConfigSaveConfirmDialog dialog = new TuningConfigSaveConfirmDialog(
                InstallManageConstant.CONFIG_SAVE_CONFIRM_TITLE, createConfirmPanel);
        dialog.displayPanel();
    }
}

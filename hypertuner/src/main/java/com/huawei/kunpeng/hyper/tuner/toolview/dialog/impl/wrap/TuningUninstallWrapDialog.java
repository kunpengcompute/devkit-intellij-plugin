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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap;

import com.huawei.kunpeng.hyper.tuner.action.uninstall.TuningUninstallAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningServerConfigPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.UninstallWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.UninstallPanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * 安装弹框
 *
 * @since 2020-09-25
 */
public class TuningUninstallWrapDialog extends UninstallWrapDialog {
    public TuningUninstallWrapDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected void onOKAction() {
        uninstallAction();
        mainPanel.clearPwd();
        String ip = CommonUtil.readCurIpFromConfig();
        if (ip == null || !ip.equals(needUninstallIp)) {
            return;
        }
        // 如果uninstall服务器ip为当前服务器ip, 则左侧树面板更新为初始配置面板
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // 关闭打开的历史报告页面
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).getToolWindow(TuningIDEConstant.HYPER_TUNER_TOOL_WINDOW_ID);
            TuningServerConfigPanel tuningServerConfigPanel = new TuningServerConfigPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(tuningServerConfigPanel, toolWindow);
        }
        // 清除所有状态
        TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_INIT);
        UserInfoContext.getInstance().clearUserInfo();
        // 清空本地 ip 缓存
//        ConfigUtils.fillIp2JsonFile(TuningIDEConstant.TOOL_NAME_TUNING, "", "","", "");
        // 弹框消失则将两个组件缓存置空
        checkButton = null;
        gifLabel = null;
    }

    private void uninstallAction() {
        if (mainPanel.getAction() instanceof TuningUninstallAction && mainPanel instanceof UninstallPanel) {
            UninstallPanel uninstallPanel = (UninstallPanel) mainPanel;
            TuningUninstallAction action = (TuningUninstallAction) mainPanel.getAction();
            needUninstallIp = uninstallPanel.getIp();
            action.onOKAction(uninstallPanel.getParams());
        }
    }

    /**
     * 点击next前的校验事件
     *
     * @return boolean
     */
    protected boolean nextVerify() {
        Logger.info("UninstallWrapDialog, nextVerify");
        // 检测连接
        if (mainPanel.getAction() instanceof TuningUninstallAction && mainPanel instanceof UninstallPanel) {
            UninstallPanel uninstallPanel = (UninstallPanel) mainPanel;
            TuningUninstallAction tuningUninstallAction = (TuningUninstallAction) mainPanel.getAction();
            tuningUninstallAction.onNextAction(uninstallPanel.getParams(), this);
        }
        // 返回false，不关闭弹框
        return false;
    }
}

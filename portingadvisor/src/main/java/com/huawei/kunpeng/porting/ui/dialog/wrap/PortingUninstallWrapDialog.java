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

package com.huawei.kunpeng.porting.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.UninstallWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.UninstallPanel;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.toolwindow.DeleteAllReportsAction;
import com.huawei.kunpeng.porting.action.uninstall.UninstallAction;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeConfigPanel;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * 卸载弹框
 *
 * @since 2021-04-19
 */
public class PortingUninstallWrapDialog extends UninstallWrapDialog {
    public PortingUninstallWrapDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected void onOKAction() {
        if (mainPanel.getAction() instanceof UninstallAction && mainPanel instanceof UninstallPanel) {
            UninstallPanel uninstallPanel = (UninstallPanel) mainPanel;
            UninstallAction action = (UninstallAction) mainPanel.getAction();
            needUninstallIp = uninstallPanel.getIp();
            action.onOKAction(uninstallPanel.getParams());
        }
        mainPanel.clearPwd();
        String ip = CommonUtil.readCurIpFromConfig();
        if (ip == null || !ip.equals(needUninstallIp)) {
            return;
        }
        // 如果uninstall服务器ip为当前服务器ip, 则左侧树面板更新为初始配置面板
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project project : openProjects) {
            // 关闭打开的历史报告页面
            DeleteAllReportsAction.closeAllOpenedReports(project);
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            LeftTreeConfigPanel leftTreeConfigPanel = new LeftTreeConfigPanel(toolWindow, project);
            UIUtils.changeToolWindowToDestPanel(leftTreeConfigPanel, toolWindow);
        }
        // 清除所有状态
        PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_INIT);
        PortingIDEContext.clearReportsNum();
        PortingUserInfoContext.getInstance().clearUserInfo();
        // 清空本地 ip 缓存
        ConfigUtils.fillIp2JsonFile(PortingIDEConstant.TOOL_NAME_PORTING, "", "", "");
        // 弹框消失则将两个组件缓存置空
        checkButton = null;
        gifLabel = null;
    }

    /**
     * 点击next前的校验事件
     *
     * @return boolean
     */
    @Override
    protected boolean nextVerify() {
        Logger.info("UninstallWrapDialog, nextVerify");
        // 检测连接
        if (mainPanel.getAction() instanceof UninstallAction && mainPanel instanceof UninstallPanel) {
            UninstallPanel uninstallPanel = (UninstallPanel) mainPanel;
            UninstallAction action = (UninstallAction) mainPanel.getAction();
            action.onNextAction(uninstallPanel.getParams(), this);
        }
        // 返回false，不关闭弹框
        return false;
    }
}

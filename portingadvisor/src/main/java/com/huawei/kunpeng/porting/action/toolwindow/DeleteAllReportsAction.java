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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeReportsPanel;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

/**
 * The class DeleteAllReportsAction
 *
 * @since v1.0
 */
public class DeleteAllReportsAction extends AnAction {
    private Project project;

    /**
     * 删除左侧树所有历史报告
     *
     * @param event event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Logger.info("Start delete all reports");
        project = event.getProject();
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(2);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);

        // 弹出确定删除所有reports Dialog
        String title = I18NServer.toLocale("plugins_porting_delete_all_reports_title");
        String message = I18NServer.toLocale("plugins_porting_delete_all_reports_message");
        String exitCode = IDEMessageDialogUtil.showDialog(new MessageDialogBean(message,
            title, buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            deleteAllReports();
            // 关闭打开的历史报告界面
            Project[] openProjects = ProjectUtil.getOpenProjects();
            for (Project proj : openProjects) {
                closeAllOpenedReports(proj);
                // 更新左侧树
                ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                    PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
                IDEBasePanel leftTreeReportsPanel = new LeftTreeReportsPanel(toolWindow, project, null);
                UIUtils.changeToolWindowToDestPanel(leftTreeReportsPanel, toolWindow);
            }
        }
    }

    /**
     * 更新左侧树删除按钮状态: 当历史报告数量大于0时激活删除按钮
     *
     * @param event event
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean isLogin = PortingIDEContext.getPortingIDEPluginStatus().value()
            >= IDEPluginStatus.IDE_STATUS_LOGIN.value();
        if (isLogin && PortingIDEContext.getReportsNum() > 0) {
            event.getPresentation().setEnabled(true);
            return;
        }
        event.getPresentation().setEnabled(false);
    }

    /**
     * 删除当前所有源码扫描报告
     */
    private void deleteAllReports() {
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/all/",
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return;
        }
        Logger.info("Delete all reports, response: {}", response.toString());

        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(response.getStatus())
            ? NotificationType.INFORMATION : NotificationType.ERROR;
        IDENotificationUtil.notificationCommon(
            new NotificationBean(I18NServer.toLocale("plugins_porting_delete_all_reports_title"),
                CommonUtil.getRspTipInfo(response), notificationType));
    }

    /**
     * 关闭所有打开的历史报告
     *
     * @param project 当前project
     */
    public static void closeAllOpenedReports(Project project) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(project);
        List<VirtualFile> openFiles = instance.getOpenFiles();
        // 排除掉远程云环境页面,该页面与用户和环境无关。
        List<VirtualFile> collect =
            openFiles.stream()
                .filter(virtualFile -> virtualFile.getName().endsWith(PortingIDEConstant.PORTING_KPS)
                    && !virtualFile.getName().contains(
                    I18NServer.toLocale("plugins_port_cloud_env_application_process_page_name")))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            instance.closeFiles(collect);
        }
    }
}

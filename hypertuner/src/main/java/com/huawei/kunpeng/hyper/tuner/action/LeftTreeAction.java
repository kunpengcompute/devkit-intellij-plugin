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

package com.huawei.kunpeng.hyper.tuner.action;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.DeleteProjectWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.DeleteTaskWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.ExportTaskWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.DeleteProjectPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.DeleteTaskPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.ExportTaskPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.CreateProjectEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.CreateTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ImportTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ModifyProjectEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ReanalyzeTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ShowNodeEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ShowProjectEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.UpdataTaskEditor;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 左侧树按钮点击，创建webView页面
 *
 * @since 2021-04-21
 */
public class LeftTreeAction extends AnAction {
    private String webViewPage = "";

    private LeftTreeAction() {
    }

    /**
     * 创建LeftTreeAction实例
     *
     * @return LeftTreeAction
     */
    public static LeftTreeAction instance() {
        return new LeftTreeAction();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 检查当前webView是否已经打开
        String operation = event.getPresentation().getText();

        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(getEventProject(event));
        if (operation.equals(SysperfContent.CREATE_PROJECT)) {
            // 创建工程
            webViewPage = SysperfContent.CREATE_PROJECT;
        } else if (operation.equals(SysperfContent.CREATE_TASK)) {
            // 创建任务
            webViewPage = SysperfContent.CREATE_TASK;
        } else if (operation.equals(SysperfContent.NODE_MANAGER_DIC)) {
            // 节点管理
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.NODE_MANAGER_DIC);
            return;
        } else if (operation.equals(SysperfContent.SCH_TASK_DIC)) {
            // 预约任务
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.SCH_TASK_DIC);
            return;
        } else if (operation.equals(SysperfContent.TASK_TEMPLETE_DIC)) {
            // 模板管理
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.TASK_TEMPLETE_DIC);
            return;
        } else if (operation.equals(SysperfContent.IMPORT_TASK)) {
            // 导入
            webViewPage = SysperfContent.IMPORT_TASK;
        } else if (operation.equals(SysperfContent.EXPORT_TASK)) {
            // 导出
            exportTask();
            return;
        } else {
            // 其他
            webViewPage = "";
        }
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(webViewPage))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            openWebViewPage(operation);
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    private void openWebViewPage(String operation) {
        if (operation.equals(SysperfContent.CREATE_PROJECT)) {
            CreateProjectEditor.openPage();
        } else if (operation.equals(SysperfContent.CREATE_TASK)) {
            CreateTaskEditor.openPage();
        } else if (operation.equals(SysperfContent.MODIFY_PROJECT)) {
            ModifyProjectEditor.openPage();
        } else if (operation.equals(SysperfContent.IMPORT_TASK)) {
            ImportTaskEditor.openPage();
        } else if (operation.equals(SysperfContent.EXPORT_TASK)) {
            exportTask();
        } else {
            Logger.info("openWebViewPage is wrong");
        }
    }

    /**
     * 获取后台安装的服务
     *
     * @return 安装详情
     */
    public String getInstallInfo() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "user-management/api/v2.2/users/install-info/", HttpMethod.GET.vaLue(), false);
        ResponseBean resp = TuningHttpsServer.INSTANCE.requestData(message);
        if (resp == null) {
            return "";
        }
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(resp.getData());
        return data.get("data");
    }

    /**
     * 创建任务
     *
     * @since 2021-04-21
     */
    public void createTask() {
        String projectName = LeftTreeSysperfPanel.getSelectProject().getProjectName();
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(projectName + "-" + SysperfContent.CREATE_TASK))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            CreateTaskEditor.openPage();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * 编辑任务
     *
     * @since 2021-04-21
     */
    public void modifyTask() {
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        String pageName = SysperfContent.MODIFY_TASK + '-' +
                LeftTreeSysperfPanel.getSelectProject().getProjectName() + '-' +
                LeftTreeSysperfPanel.getSelectTask().getTaskname();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(pageName))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            UpdataTaskEditor.openPage();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * 重新分析
     *
     * @since 2021-04-21
     */
    public void reanalyzeTask() {
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        String pageName = LeftTreeSysperfPanel.getSelectTask().getTaskname() + '-' +
                LeftTreeSysperfPanel.getSelectTask().getNodeList().get(0).getNodeIP();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(pageName))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            instance.closeFile(collect.get(0));
        }
        ReanalyzeTaskEditor.openPage();
    }

    /**
     * 编辑项目
     *
     * @since 2021-04-21
     */
    public void modifyProject() {
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(SysperfContent.MODIFY_PROJECT))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            ModifyProjectEditor.openPage();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * 删除项目
     */
    public void deleteProject() {
        DeleteProjectPanel panel =
                new DeleteProjectPanel(null, SysperfContent.DELETE_PROJECT, false);
        DeleteProjectWrapDialog dialog = new DeleteProjectWrapDialog(SysperfContent.DELETE_PROJECT, panel);
        panel.setDialog(dialog);
        // 显示弹框
        dialog.displayPanel();
    }

    /**
     * 删除任务
     */
    public void deleteTask() {
        DeleteTaskPanel panel =
                new DeleteTaskPanel(null, SysperfContent.DELETE_TASK, false);
        DeleteTaskWrapDialog dialog = new DeleteTaskWrapDialog(SysperfContent.DELETE_TASK, panel);
        panel.setDialog(dialog);
        // 显示弹框
        dialog.displayPanel();
    }

    /**
     * 展示任务节点详情
     */
    public void showTaskNode() {
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        String pageName = "";
        if (LeftTreeSysperfPanel.isCreateTask) {
            pageName = LeftTreeSysperfPanel.getNewProject().getProjectName() + '-'
                    + LeftTreeSysperfPanel.getNewTask().getTaskname() + '-' +
                    LeftTreeSysperfPanel.getNewNode().getNodeIP();
        } else {
            if (LeftTreeSysperfPanel.getSelectNode() == null) {
                return;
            }
            pageName = LeftTreeSysperfPanel.getSelectProject().getProjectName() + '-'
                    + LeftTreeSysperfPanel.getSelectTask().getTaskname().replace(" ", "-")
                    .replace(":", "-") + '-' +
                    LeftTreeSysperfPanel.getSelectNode().getNodeIP();
        }

        String finalPageName = pageName;
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(finalPageName))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            instance.closeFile(collect.get(0));
        }
        ShowNodeEditor.openPage();
    }

    /**
     * 展示项目详情
     */
    public void showProjectInfo() {
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(LeftTreeSysperfPanel.getSelectProject().getProjectName()))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            ShowProjectEditor.openPage();
        } else {
            instance.openFile(collect.get(0), true);
        }
    }

    /**
     * 导出任务
     */
    public void exportTask() {
        ExportTaskPanel panel =
                new ExportTaskPanel(null, SysperfContent.EXPORT_TASK, false);
        ExportTaskWrapDialog dialog = new ExportTaskWrapDialog(SysperfContent.EXPORT_TASK, panel);
        panel.setDialog(dialog);
        // 显示弹框
        dialog.displayPanel();
    }

    /**
     * 关闭所有打开的webview页面
     *
     * @param project 当前project
     */
    public void closeAllOpenedWebViewPage(Project project) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(project);
        List<VirtualFile> openFiles = instance.getOpenFiles();
        List<VirtualFile> collect =
                openFiles.stream()
                        .filter(virtualFile -> virtualFile.getName().endsWith(TuningIDEConstant.TUNING_KPHT))
                        .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            instance.closeFiles(collect);
        }
    }
}

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
 * ??????????????????????????????webView??????
 *
 * @since 2021-04-21
 */
public class LeftTreeAction extends AnAction {
    private String webViewPage = "";

    private LeftTreeAction() {
    }

    /**
     * ??????LeftTreeAction??????
     *
     * @return LeftTreeAction
     */
    public static LeftTreeAction instance() {
        return new LeftTreeAction();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // ????????????webView??????????????????
        String operation = event.getPresentation().getText();

        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(getEventProject(event));
        if (operation.equals(SysperfContent.CREATE_PROJECT)) {
            // ????????????
            webViewPage = SysperfContent.CREATE_PROJECT;
        } else if (operation.equals(SysperfContent.CREATE_TASK)) {
            // ????????????
            webViewPage = SysperfContent.CREATE_TASK;
        } else if (operation.equals(SysperfContent.NODE_MANAGER_DIC)) {
            // ????????????
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.NODE_MANAGER_DIC);
            return;
        } else if (operation.equals(SysperfContent.SCH_TASK_DIC)) {
            // ????????????
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.SCH_TASK_DIC);
            return;
        } else if (operation.equals(SysperfContent.TASK_TEMPLETE_DIC)) {
            // ????????????
            ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.TASK_TEMPLETE_DIC);
            return;
        } else if (operation.equals(SysperfContent.IMPORT_TASK)) {
            // ??????
            webViewPage = SysperfContent.IMPORT_TASK;
        } else if (operation.equals(SysperfContent.EXPORT_TASK)) {
            // ??????
            exportTask();
            return;
        } else {
            // ??????
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
     * ???????????????????????????
     *
     * @return ????????????
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
     * ????????????
     *
     * @since 2021-04-21
     */
    public void createTask() {
        String projectName = LeftTreeSysperfPanel.getSelectProject().getProjectName();
        // ????????????webView??????????????????
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
     * ????????????
     *
     * @since 2021-04-21
     */
    public void modifyTask() {
        // ????????????webView??????????????????
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
     * ????????????
     *
     * @since 2021-04-21
     */
    public void reanalyzeTask() {
        // ????????????webView??????????????????
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
     * ????????????
     *
     * @since 2021-04-21
     */
    public void modifyProject() {
        // ????????????webView??????????????????
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
     * ????????????
     */
    public void deleteProject() {
        DeleteProjectPanel panel =
                new DeleteProjectPanel(null, SysperfContent.DELETE_PROJECT, false);
        DeleteProjectWrapDialog dialog = new DeleteProjectWrapDialog(SysperfContent.DELETE_PROJECT, panel);
        panel.setDialog(dialog);
        // ????????????
        dialog.displayPanel();
    }

    /**
     * ????????????
     */
    public void deleteTask() {
        DeleteTaskPanel panel =
                new DeleteTaskPanel(null, SysperfContent.DELETE_TASK, false);
        DeleteTaskWrapDialog dialog = new DeleteTaskWrapDialog(SysperfContent.DELETE_TASK, panel);
        panel.setDialog(dialog);
        // ????????????
        dialog.displayPanel();
    }

    /**
     * ????????????????????????
     */
    public void showTaskNode() {
        // ????????????webView??????????????????
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
     * ??????????????????
     */
    public void showProjectInfo() {
        // ????????????webView??????????????????
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
     * ????????????
     */
    public void exportTask() {
        ExportTaskPanel panel =
                new ExportTaskPanel(null, SysperfContent.EXPORT_TASK, false);
        ExportTaskWrapDialog dialog = new ExportTaskWrapDialog(SysperfContent.EXPORT_TASK, panel);
        panel.setDialog(dialog);
        // ????????????
        dialog.displayPanel();
    }

    /**
     * ?????????????????????webview??????
     *
     * @param project ??????project
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

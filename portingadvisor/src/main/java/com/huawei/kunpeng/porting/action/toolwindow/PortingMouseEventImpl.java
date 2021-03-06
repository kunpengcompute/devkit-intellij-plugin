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
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.bean.AnalysisTaskBean;
import com.huawei.kunpeng.porting.bean.PortingTaskBean;
import com.huawei.kunpeng.porting.bean.SoftwareAssessmentTaskBean;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.LeftTreeTitleConstant;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.assesment.SoftwareAssessmentHandler;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;
import com.huawei.kunpeng.porting.http.module.pkgrebuild.PkgRebuildingHandler;
import com.huawei.kunpeng.porting.ui.panel.sourceporting.LeftTreeReportsPanel;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisReportEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationAppraiseEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationAppraiseReportEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.MigrationCenterPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingReportPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingSourceEditor;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.treeStructure.Tree;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * The class PortingMouseEventImpl: ????????????????????????
 *
 * @since v1.0
 */
public class PortingMouseEventImpl implements MouseListener {
    // ??????????????????
    private static final int TWICE = 2;

    // ?????????????????????
    private static final int REPORT_DEPTH = 3;

    // ????????????????????????
    private static final int SERVICE_DEPTH = 2;
    private static final String PLUGINS_PORTING_REBUILDING_MENU_DOWNLOAD_HTML =
        "plugins_porting_rebuilding_menu_download_html";

    private Project project;

    private JPopupMenu treeMenu;

    private JMenuItem delete;

    private JMenuItem open;

    /**
     * ????????????
     *
     * @param project project
     */
    public PortingMouseEventImpl(Project project) {
        this.project = project;
        treeMenu = new JPopupMenu();
        open = new JMenuItem(I18NServer.toLocale("plugins_porting_lefttree_open_reports"));
        delete = new JMenuItem(CommonI18NServer.toLocale("common_tip_operate_del"));
        treeMenu.add(open);
        treeMenu.addSeparator();
        treeMenu.add(delete);
    }

    /**
     * ????????????????????????
     *
     * @param event MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == TWICE) {
            openCurrent(event);
        }
        // ????????????????????????????????????????????????
        if (event.isMetaDown()) {
            if (isReportNode(event, REPORT_DEPTH)) {
                addPortTreeMenu(event);
            }

            if (isReportNode(event, SERVICE_DEPTH)) {
                // ???????????????????????????
                addServiceMenu(event);
            }
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param event ????????????
     */
    private void addServiceMenu(MouseEvent event) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            Tree portTree = (Tree) component;
            TreePath anchorSelectionPath = portTree.getAnchorSelectionPath();
            Object selectedNode = anchorSelectionPath.getLastPathComponent();
            if (!(selectedNode instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedNode;
            Object userObject = treeNode.getUserObject();
            if (userObject == null) {
                return;
            }

            // ???????????????????????????????????????
            JMenuItem addItem = new JMenuItem();
            JMenuItem deleteAll = new JMenuItem();
            addItem.setText(LeftTreeTitleConstant.COMMON_NEW_TASK);
            deleteAll.setText(LeftTreeTitleConstant.CLEAR_REPORTS);

            JPopupMenu serviceMenu = new JPopupMenu();
            serviceMenu.add(addItem);
            if (!Objects.equals(userObject, LeftTreeTitleConstant.ENHANCED_FUNCTION) &&
                !LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING.equals(userObject)) {
                serviceMenu.addSeparator();
                serviceMenu.add(deleteAll);
            }

            deleteAll.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    doDelete(treeNode, userObject.toString());
                }
            });
            addItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    doAdd(userObject.toString());
                }
            });
            serviceMenu.show(portTree, event.getX(), event.getY());
        }
    }

    /**
     * ????????????
     *
     * @param userObject userObject
     */
    private void doAdd(String userObject) {
        // ??????????????????
        if (Objects.equals(userObject, LeftTreeTitleConstant.SOFTWARE_PORTING_ASSESSMENT)) {
            openPage(LeftTreeTitleConstant.SOFTWARE_PORTING_ASSESSMENT, PageType.MIGRATION_APPRAISE);
        }
        // ??????????????????
        if (Objects.equals(userObject, LeftTreeTitleConstant.SOURCE_CODE_PORTING)) {
            openPage(LeftTreeTitleConstant.SOURCE_CODE_PORTING, PageType.SOURCE);
        }
        // ?????????????????????
        if (Objects.equals(userObject, LeftTreeTitleConstant.SOFTWARE_REBUILDING)) {
            openPage(LeftTreeTitleConstant.SOFTWARE_REBUILDING, PageType.ANALYSIS_CENTER);
        }
        // ??????????????????
        if (LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING.equals(userObject)) {
            openPage(LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING, PageType.MIGRATION_CENTER);
        }
        // ????????????
        if (Objects.equals(userObject, LeftTreeTitleConstant.ENHANCED_FUNCTION)) {
            openPage(LeftTreeTitleConstant.ENHANCED_FUNCTION, PageType.ENHANCED_FUNCTION);
        }
    }

    /**
     * ??????????????????
     *
     * @param treeNode   treeNode
     * @param userObject userObject
     */
    private void doDelete(DefaultMutableTreeNode treeNode, String userObject) {
        if (treeNode.getFirstChild() instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) treeNode.getFirstChild();
            if (!firstChild.getUserObject().equals(LeftTreeTitleConstant.NO_REPORTS)
                && treeNode.getChildCount() >= 1) {
                Set<String> allReportTitle = getNodeAllReportTitle(treeNode);
                if (Objects.equals(userObject, LeftTreeTitleConstant.SOFTWARE_PORTING_ASSESSMENT)) {
                    deleteAllAssessmentReport(allReportTitle);
                }
                if (Objects.equals(userObject, LeftTreeTitleConstant.SOURCE_CODE_PORTING)) {
                    deleteAllSourceReports(allReportTitle);
                }
                if (Objects.equals(userObject, LeftTreeTitleConstant.SOFTWARE_REBUILDING)) {
                    deleteALLAnalysisTask();
                }
            }
        }
    }

    /**
     * ??????????????????????????????id
     *
     * @param treeNode ??????
     * @return ??????id??????
     */
    private Set<String> getNodeAllReportTitle(DefaultMutableTreeNode treeNode) {
        Enumeration<TreeNode> children = treeNode.children();
        Set<String> allReport = new HashSet<>();
        while (children.hasMoreElements()) {
            TreeNode element = children.nextElement();
            if (element instanceof DefaultMutableTreeNode) {
                Object taskObject = ((DefaultMutableTreeNode) element).getUserObject();
                if (taskObject instanceof SoftwareAssessmentTaskBean.Task) {
                    // ????????????????????????
                    allReport.add(((SoftwareAssessmentTaskBean.Task) taskObject).getId()
                        + "." + IDEConstant.PORTING_KPS);
                }
                if (taskObject instanceof PortingTaskBean.Task) {
                    // ??????????????????
                    allReport.add(((PortingTaskBean.Task) taskObject).getId()
                        + "." + IDEConstant.PORTING_KPS);
                }
            }
        }
        return allReport;
    }

    /**
     * ????????????????????????????????????
     *
     * @param allReportTitle ?????????????????????title??????
     */
    private void deleteAllAssessmentReport(Set<String> allReportTitle) {
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>();
        button.add(IDEMessageDialogUtil.ButtonName.DELETE);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String select = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_port_migration_appraise_all_delConfirm"),
                I18NServer.toLocale("plugins_port_migration_appraise"),
                button,
                0,
                IDEMessageDialogUtil.getWarn()
            ));
        if (select.equals(IDEMessageDialogUtil.ButtonName.DELETE.getKey())) {
            SoftwareAssessmentHandler.deleteAllAssessmentReport(allReportTitle);
        } else {
            Logger.info("cancel delete migration appraise task");
        }
    }

    /**
     * ??????????????????
     */
    private void deleteALLAnalysisTask() {
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>();
        button.add(IDEMessageDialogUtil.ButtonName.DELETE);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String select = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_porting_webpack_all_delConfirm"),
                I18NServer.toLocale("plugins_porting_software_rebuilding_title"),
                button,
                0,
                IDEMessageDialogUtil.getWarn()
            ));
        if (select.equals(IDEMessageDialogUtil.ButtonName.DELETE.getKey())) {
            PkgRebuildingHandler.deleteAllTask();
        } else {
            Logger.info("cancel delete analysis task");
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param allReportTitle ??????????????????title
     */
    private void deleteAllSourceReports(Set<String> allReportTitle) {
        Logger.info("Start delete all reports");
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(2);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);

        // ????????????????????????reports Dialog
        String title = I18NServer.toLocale("plugins_porting_delete_all_reports_title");
        String message = I18NServer.toLocale("plugins_porting_delete_all_reports_message");
        String exitCode = IDEMessageDialogUtil.showDialog(new MessageDialogBean(message,
            title, buttonNames, 0, IDEMessageDialogUtil.getWarn()));
        // ????????????
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/all/",
                HttpMethod.DELETE.vaLue(), "");
            ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
            Logger.info("Delete all reports, response: {}", response);
            if (response == null) {
                return;
            }
            NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(response.getStatus())
                ? NotificationType.INFORMATION : NotificationType.ERROR;
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_porting_delete_all_reports_title"),
                    CommonUtil.getRspTipInfo(response), notificationType));
            if (notificationType == NotificationType.INFORMATION) {
                // ?????????????????????????????????
                closeAllDeletedReport(allReportTitle);
            }

            // ???????????????
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            IDEBasePanel destPanel = new LeftTreeReportsPanel(toolWindow, project, null);
            UIUtils.changeToolWindowToDestPanel(destPanel, toolWindow);
        }
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param allReportTitle task title set
     */
    public static void closeAllDeletedReport(Set<String> allReportTitle) {
        Project[] openProjects = ProjectUtil.getOpenProjects();
        for (Project proj : openProjects) {
            IDEFileEditorManager instance = IDEFileEditorManager.getInstance(proj);
            List<VirtualFile> openFiles = instance.getOpenFiles();
            List<VirtualFile> collect =
                openFiles.stream()
                    .filter(virtualFile -> allReportTitle.contains(virtualFile.getName()))
                    .collect(Collectors.toList());
            collect.forEach(instance::closeFile);
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param event ????????????
     * @param depth ??????
     * @return true?????????????????????????????????
     */
    private boolean isReportNode(MouseEvent event, int depth) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            TreePath anchorSelectionPath = ((Tree) component).getAnchorSelectionPath();
            if (anchorSelectionPath != null && anchorSelectionPath.getPath().length == depth) {
                return true;
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????
     * ????????????????????????????????????
     *
     * @param event ????????????
     */
    private void addPortTreeMenu(MouseEvent event) {
        final Component component = event.getComponent();

        if (!(component instanceof Tree && event.isMetaDown())) {
            return;
        }
        Tree portTree = (Tree) component;
        TreePath anchorSelectionPath = portTree.getAnchorSelectionPath();
        Object selectedNode = anchorSelectionPath.getLastPathComponent();
        if (!(selectedNode instanceof DefaultMutableTreeNode)) {
            return;
        }
        Object userObject = ((DefaultMutableTreeNode) selectedNode).getUserObject();
        if (userObject instanceof AnalysisTaskBean.Task) {
            // ?????????????????????
            addMenuForAnalysisTask(event, (AnalysisTaskBean.Task) userObject);
            return;
        }
        if (userObject instanceof SoftwareAssessmentTaskBean.Task) {
            // ???????????????????????????????????????vsc???html??????
            addSoftwareDownLoadMenu(event, (SoftwareAssessmentTaskBean.Task) userObject);
        }
        if (userObject instanceof PortingTaskBean.Task) {
            addSourcePortingDownLoadMenu(event, (PortingTaskBean.Task) userObject);
        }
        if (userObject instanceof String) {
            return;
        }

        // ??????????????????????????????????????????????????????,????????????????????????????????????
        removeOpenReportMouseListener();
        open.addMouseListener(new OpenReportMouseListener(event));
        removeDeleteReportMouseListener();
        delete.addMouseListener(new DeleteReportMouseListener(portTree));
        treeMenu.show(portTree, event.getX(), event.getY());
    }

    /**
     * ????????????????????????????????????
     *
     * @param event        ????????????
     * @param analysisTask ??????????????????PortingTask.Task
     */
    private void addSourcePortingDownLoadMenu(MouseEvent event, PortingTaskBean.Task analysisTask) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            JMenuItem downloadHtmlItem = new JMenuItem();
            JMenuItem downloadCSVItem = new JMenuItem();
            downloadHtmlItem.setText(I18NServer.toLocale(PLUGINS_PORTING_REBUILDING_MENU_DOWNLOAD_HTML));
            downloadCSVItem.setText(I18NServer.toLocale("plugins_porting_rebuilding_menu_download_csv"));
            treeMenu.removeAll();
            treeMenu.add(open);
            treeMenu.add(downloadHtmlItem);
            treeMenu.add(downloadCSVItem);
            treeMenu.add(delete);
            downloadHtmlItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    SourcePortingHandler.downLoadReport(1, analysisTask.getId());
                }
            });
            downloadCSVItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    SourcePortingHandler.downLoadReport(0, analysisTask.getId());
                }
            });
        }
    }

    private void removeOpenReportMouseListener() {
        MouseListener[] mouseListeners = open.getMouseListeners();
        for (MouseListener mouseListener : mouseListeners) {
            if (mouseListener instanceof OpenReportMouseListener) {
                open.removeMouseListener(mouseListener);
            }
        }
    }

    private void removeDeleteReportMouseListener() {
        MouseListener[] mouseListeners = delete.getMouseListeners();
        for (MouseListener mouseListener : mouseListeners) {
            if (mouseListener instanceof DeleteReportMouseListener) {
                delete.removeMouseListener(mouseListener);
            }
        }
    }

    private void addSoftwareDownLoadMenu(MouseEvent event, SoftwareAssessmentTaskBean.Task analysisTask) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            JMenuItem downloadHtmlItem = new JMenuItem();
            JMenuItem downloadCSVItem = new JMenuItem();
            downloadHtmlItem.setText(I18NServer.toLocale(PLUGINS_PORTING_REBUILDING_MENU_DOWNLOAD_HTML));
            downloadCSVItem.setText(I18NServer.toLocale("plugins_porting_rebuilding_menu_download_csv"));
            treeMenu.removeAll();
            treeMenu.add(open);
            treeMenu.add(downloadHtmlItem);
            treeMenu.add(downloadCSVItem);
            treeMenu.add(delete);
            downloadHtmlItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    SoftwareAssessmentHandler.downLoadReport(1, analysisTask.getId());
                }
            });
            downloadCSVItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    SoftwareAssessmentHandler.downLoadReport(0, analysisTask.getId());
                }
            });
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param event        event
     * @param analysisTask analysisTask
     */
    private void addMenuForAnalysisTask(MouseEvent event, AnalysisTaskBean.Task analysisTask) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            // ???????????????????????????????????????
            JMenuItem analysisDdownloadItem = new JMenuItem();
            JMenuItem analysisDownloadHtmlItem = new JMenuItem();
            JMenuItem deleteReport = new JMenuItem();
            analysisDdownloadItem.setText(I18NServer.toLocale("plugins_porting_rebuilding_menu_download_package"));
            analysisDownloadHtmlItem.setText(I18NServer.toLocale(PLUGINS_PORTING_REBUILDING_MENU_DOWNLOAD_HTML));
            deleteReport.setText(I18NServer.toLocale("plugins_porting_rebuilding_menu_deleted_report"));

            JPopupMenu serviceMenu = new JPopupMenu();
            if (analysisTask.getStatus() == 0) {
                serviceMenu.add(analysisDdownloadItem);
                serviceMenu.addSeparator();
                // ??????????????????
                addDownloadItem(analysisDdownloadItem, analysisTask);
            }
            serviceMenu.add(analysisDownloadHtmlItem);
            serviceMenu.addSeparator();
            serviceMenu.add(deleteReport);
            deleteReport.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    deleteAnalysisTask(analysisTask);
                }
            });

            analysisDownloadHtmlItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    loadAnalysisHtml(analysisTask);
                }
            });
            Tree portTree = (Tree) component;
            serviceMenu.show(portTree, event.getX(), event.getY());
        }
    }

    private void addDownloadItem(JMenuItem downloadItem, AnalysisTaskBean.Task analysisTask) {
        // ??????????????????
        downloadItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                loadPackage(analysisTask);
            }
        });
    }

    /**
     * ?????????????????????????????????
     *
     * @param analysisTask analysisTask
     */
    private void loadAnalysisHtml(AnalysisTaskBean.Task analysisTask) {
        // ????????????????????????
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/autopack/history/" + analysisTask.getPath() + PortingIDEConstant.PATH_SEPARATOR,
            HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return;
        }
        // ??????HTML
        String htmlContent = PkgRebuildingHandler.loadHtml(response.getData());
        // ???????????????????????????
        String fileName = analysisTask.getPath() + ".html";
        NotificationBean notification = new NotificationBean("",
            I18NServer.toLocale("plugins_porting_report_download_success", fileName),
            NotificationType.INFORMATION);
        notification.setProject(CommonUtil.getDefaultProject());
        UIUtils.saveTXTFileToLocalForDialog(htmlContent, fileName, notification);
    }

    /**
     * ????????????????????????
     *
     * @param userObject userObject
     */
    private void deleteAnalysisTask(AnalysisTaskBean.Task userObject) {
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>();
        button.add(IDEMessageDialogUtil.ButtonName.DELETE);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String select = IDEMessageDialogUtil.showDialog(new MessageDialogBean(
            MessageFormat.format(I18NServer.toLocale("plugins_porting_webpack_delConfirm"), userObject.toString()),
            I18NServer.toLocale("plugins_porting_software_rebuilding_title"),
            button,
            0,
            IDEMessageDialogUtil.getWarn()
        ));
        if (select.equals(IDEMessageDialogUtil.ButtonName.DELETE.getKey())) {
            PkgRebuildingHandler.deletePortingTask(userObject.getPath(), userObject.getName());
        } else {
            Logger.info("cancel delete analysis task");
        }
    }

    /**
     * ???????????????????????????
     *
     * @param selectedObj selectedObj
     */
    private void loadPackage(AnalysisTaskBean.Task selectedObj) {
        PkgRebuildingHandler.downloadPackage(selectedObj.getPath(), selectedObj.getName());
    }

    /**
     * ???????????????????????????????????????
     *
     * @param portTree ??????????????????
     */
    private void deleteAction(Tree portTree) {
        // ?????????
        Object sourceRoot = portTree.getAnchorSelectionPath().getParentPath().getLastPathComponent();
        // ????????????
        TreePath selectPath = portTree.getAnchorSelectionPath();
        if (selectPath != null) {
            Object object = selectPath.getLastPathComponent();
            String taskId = object.toString().replaceAll("[\\/:\\s]", "");
            if (sourceRoot instanceof DefaultMutableTreeNode && object instanceof DefaultMutableTreeNode) {
                deleteSelectTreeNode(portTree, taskId, (DefaultMutableTreeNode) sourceRoot,
                    (DefaultMutableTreeNode) object);
            }
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param portTree ?????????????????????
     * @param taskId   taskId
     * @param root     ??????????????????
     * @param node     ???????????????????????????
     */
    private void deleteSelectTreeNode(Tree portTree, String taskId,
        DefaultMutableTreeNode root, DefaultMutableTreeNode node) {
        // ??????????????????????????????????????????????????????
        treeMenu.setVisible(false);
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>();
        button.add(IDEMessageDialogUtil.ButtonName.DELETE);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String select = IDEMessageDialogUtil.showDialog(new MessageDialogBean(
            I18NServer.toLocale("plugins_porting_message_deleteReport", taskId),
            I18NServer.toLocale("plugins_porting_title_deleteReport"),
            button,
            0,
            IDEMessageDialogUtil.getWarn()
        ));
        // ?????????????????????????????????????????????????????????????????????
        portTree.clearSelection();
        if (select.equals(IDEMessageDialogUtil.ButtonName.DELETE.getKey())) {
            deleteServerReport(taskId, node);
            // ??????????????????????????????
            root.remove(node);
            PortingIDEContext.decreaseReportsNum();
            // ?????????????????????project, ?????????????????????project?????????
            Project[] openProjects = ProjectUtil.getOpenProjects();
            for (Project proj : openProjects) {
                // ???????????????????????????
                closeOpenReport(proj, taskId);
                if (proj == project) {
                    continue;
                }
                List<PortingTaskBean.Task> tasks = SourcePortingHandler.obtainAllTasks();
                ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                    PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
                IDEBasePanel destPanel = new LeftTreeReportsPanel(toolWindow, project, tasks);
                UIUtils.changeToolWindowToDestPanel(destPanel, toolWindow);
            }
        } else {
            Logger.info("Cancel delete report.");
        }
    }

    private void deleteServerReport(String taskId, DefaultMutableTreeNode node) {
        // ????????????????????????
        if (node.getUserObject() instanceof SoftwareAssessmentTaskBean.Task) {
            SoftwareAssessmentHandler.deleteAssessmentReport(taskId);
            return;
        }
        if (node.getUserObject() instanceof PortingTaskBean.Task) {
            SourcePortingHandler.deleteReport(taskId);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param project ?????????intellij project
     * @param taskId  taskId
     */
    private void closeOpenReport(Project project, String taskId) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(project);
        List<VirtualFile> openFiles = instance.getOpenFiles();
        List<VirtualFile> collect =
            openFiles.stream()
                .filter(virtualFile -> virtualFile.getName().contains(taskId))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            instance.closeFile(collect.get(0));
        }
    }

    /**
     * ??????webView
     *
     * @param event ??????
     */
    private void openCurrent(MouseEvent event) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            TreePath anchorSelectionPath = ((Tree) component).getAnchorSelectionPath();
            Object selectedNode = anchorSelectionPath.getLastPathComponent();
            if (!(selectedNode instanceof DefaultMutableTreeNode)) {
                return;
            }
            Object userObject = ((DefaultMutableTreeNode) selectedNode).getUserObject();
            String taskId = anchorSelectionPath.getLastPathComponent().toString()
                .replaceAll("[\\/:\\s]", "");
            handleOpenEvent(anchorSelectionPath, taskId, userObject);
        }
    }

    private <T> void handleOpenEvent(TreePath anchorSelectionPath, String taskId, T userObject) {
        if (userObject instanceof String) {
            if (anchorSelectionPath.getPath().length == REPORT_DEPTH
                && !userObject.equals(LeftTreeTitleConstant.NO_REPORTS)) {
                openPage(taskId, PageType.REPORT);
            }
            if (anchorSelectionPath.getPath().length == SERVICE_DEPTH) {
                if (LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING.equals(userObject)) {
                    openPage(LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING, PageType.MIGRATION_CENTER);
                }
                if (LeftTreeTitleConstant.ENHANCED_FUNCTION.equals(userObject)) {
                    openPage(LeftTreeTitleConstant.ENHANCED_FUNCTION, PageType.ENHANCED_FUNCTION);
                }
            }
        }
        if (userObject instanceof SoftwareAssessmentTaskBean.Task) {
            openPage(taskId, PageType.MIGRATION_APPRAISE_REPORT);
        }

        if (userObject instanceof PortingTaskBean.Task) {
            openPage(taskId, PageType.REPORT);
        }

        // ???????????????
        if (userObject instanceof AnalysisTaskBean.Task) {
            openPage(((AnalysisTaskBean.Task) userObject).getPath(), PageType.ANALYSIS_CENTER_REPORT);
        }
    }

    /**
     * ????????????????????????
     *
     * @param pageName ????????????
     * @param pageType ????????????
     */
    private void openPage(String pageName, PageType pageType) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(project);
        List<VirtualFile> openFiles = instance.getOpenFiles();
        List<VirtualFile> collect = openFiles.stream()
            .filter(virtualFile -> virtualFile.getName().contains(pageName))
            .collect(Collectors.toList());
        // ??????????????????????????????????????????
        if (collect.isEmpty() || PageType.ENHANCED_FUNCTION == pageType) {
            switch (pageType) {
                case REPORT:
                    PortingReportPageEditor.openPage(pageName);
                    break;
                case MIGRATION_APPRAISE_REPORT:
                    MigrationAppraiseReportEditor.openPage(pageName);
                    break;
                case MIGRATION_APPRAISE:
                    MigrationAppraiseEditor.openPage(pageName);
                    break;
                case SOURCE:
                    PortingSourceEditor.openPage();
                    break;
                case ANALYSIS_CENTER:
                    AnalysisEditor.openPage(pageName);
                    break;
                case ANALYSIS_CENTER_REPORT:
                    AnalysisReportEditor.openPage(pageName);
                    break;
                case MIGRATION_CENTER:
                    MigrationCenterPageEditor.openPage();
                    break;
                case ENHANCED_FUNCTION:
                    EnhancedFunctionPageEditor.openPage();
                    break;
                default:
                    break;
            }
        } else {
            // ????????????????????????????????????
            instance.openFile(collect.get(0), true);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    private class OpenReportMouseListener extends MouseAdapter {
        private MouseEvent event;

        OpenReportMouseListener(MouseEvent event) {
            this.event = event;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            openCurrent(event);
        }
    }

    private class DeleteReportMouseListener extends MouseAdapter {
        private Tree portTree;

        DeleteReportMouseListener(Tree portTree) {
            this.portTree = portTree;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            deleteAction(portTree);
            portTree.updateUI();
            portTree.validate();
            portTree.repaint();
        }
    }
}

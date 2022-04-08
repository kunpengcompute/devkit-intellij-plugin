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
 * The class PortingMouseEventImpl: 左侧树事件监听器
 *
 * @since v1.0
 */
public class PortingMouseEventImpl implements MouseListener {
    // 鼠标点击次数
    private static final int TWICE = 2;

    // 报告节点的深度
    private static final int REPORT_DEPTH = 3;

    // 各业务节点的深度
    private static final int SERVICE_DEPTH = 2;
    private static final String PLUGINS_PORTING_REBUILDING_MENU_DOWNLOAD_HTML =
        "plugins_porting_rebuilding_menu_download_html";

    private Project project;

    private JPopupMenu treeMenu;

    private JMenuItem delete;

    private JMenuItem open;

    /**
     * 构造函数
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
     * 处理鼠标点击事件
     *
     * @param event MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == TWICE) {
            openCurrent(event);
        }
        // 判断是否为报告节点且触发右键点击
        if (event.isMetaDown()) {
            if (isReportNode(event, REPORT_DEPTH)) {
                addPortTreeMenu(event);
            }

            if (isReportNode(event, SERVICE_DEPTH)) {
                // 添加业务节点菜单。
                addServiceMenu(event);
            }
        }
    }

    /**
     * 增加左侧菜单栏右击功能绑定
     *
     * @param event 点击事件
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

            // 新建任务和删除历史报告菜单
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
     * 新建操作
     *
     * @param userObject userObject
     */
    private void doAdd(String userObject) {
        // 软件迁移评估
        if (Objects.equals(userObject, LeftTreeTitleConstant.SOFTWARE_PORTING_ASSESSMENT)) {
            openPage(LeftTreeTitleConstant.SOFTWARE_PORTING_ASSESSMENT, PageType.MIGRATION_APPRAISE);
        }
        // 源码迁移扫描
        if (Objects.equals(userObject, LeftTreeTitleConstant.SOURCE_CODE_PORTING)) {
            openPage(LeftTreeTitleConstant.SOURCE_CODE_PORTING, PageType.SOURCE);
        }
        // 软件包重构处理
        if (Objects.equals(userObject, LeftTreeTitleConstant.SOFTWARE_REBUILDING)) {
            openPage(LeftTreeTitleConstant.SOFTWARE_REBUILDING, PageType.ANALYSIS_CENTER);
        }
        // 专项软件迁移
        if (LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING.equals(userObject)) {
            openPage(LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING, PageType.MIGRATION_CENTER);
        }
        // 增强功能
        if (Objects.equals(userObject, LeftTreeTitleConstant.ENHANCED_FUNCTION)) {
            openPage(LeftTreeTitleConstant.ENHANCED_FUNCTION, PageType.ENHANCED_FUNCTION);
        }
    }

    /**
     * 删除历史报告
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
     * 获取节点下所有的报告id
     *
     * @param treeNode 节点
     * @return 报告id集合
     */
    private Set<String> getNodeAllReportTitle(DefaultMutableTreeNode treeNode) {
        Enumeration<TreeNode> children = treeNode.children();
        Set<String> allReport = new HashSet<>();
        while (children.hasMoreElements()) {
            TreeNode element = children.nextElement();
            if (element instanceof DefaultMutableTreeNode) {
                Object taskObject = ((DefaultMutableTreeNode) element).getUserObject();
                if (taskObject instanceof SoftwareAssessmentTaskBean.Task) {
                    // 软件迁移评估报告
                    allReport.add(((SoftwareAssessmentTaskBean.Task) taskObject).getId()
                        + "." + IDEConstant.PORTING_KPS);
                }
                if (taskObject instanceof PortingTaskBean.Task) {
                    // 源码迁移报告
                    allReport.add(((PortingTaskBean.Task) taskObject).getId()
                        + "." + IDEConstant.PORTING_KPS);
                }
            }
        }
        return allReport;
    }

    /**
     * 删除所有软件评估报告记录
     *
     * @param allReportTitle 需要关闭的报告title集合
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
     * 删除所有任务
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
     * 删除所有源码迁移历史报告
     *
     * @param allReportTitle 源码迁移报告title
     */
    private void deleteAllSourceReports(Set<String> allReportTitle) {
        Logger.info("Start delete all reports");
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
                // 关闭打开的历史报告界面
                closeAllDeletedReport(allReportTitle);
            }

            // 更新左侧树
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(
                PortingIDEConstant.PORTING_ADVISOR_TOOL_WINDOW_ID);
            IDEBasePanel destPanel = new LeftTreeReportsPanel(toolWindow, project, null);
            UIUtils.changeToolWindowToDestPanel(destPanel, toolWindow);
        }
    }


    /**
     * 关闭左侧树已经删除掉对应的报告
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
     * 判断选中的左侧树节点是否为报告节点
     *
     * @param event 鼠标事件
     * @param depth 深度
     * @return true即为报告节点，反之则然
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
     * 左侧树报告节点菜单逻辑
     * 点击对应内容执行对应操作
     *
     * @param event 鼠标事件
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
            // 软件包重构处理
            addMenuForAnalysisTask(event, (AnalysisTaskBean.Task) userObject);
            return;
        }
        if (userObject instanceof SoftwareAssessmentTaskBean.Task) {
            // 软件迁移评估左侧树添加下载vsc和html按钮
            addSoftwareDownLoadMenu(event, (SoftwareAssessmentTaskBean.Task) userObject);
        }
        if (userObject instanceof PortingTaskBean.Task) {
            addSourcePortingDownLoadMenu(event, (PortingTaskBean.Task) userObject);
        }
        if (userObject instanceof String) {
            return;
        }

        // 给打开报告和删除报告添加鼠标事件监听,同时删除上一次添加的监听
        removeOpenReportMouseListener();
        open.addMouseListener(new OpenReportMouseListener(event));
        removeDeleteReportMouseListener();
        delete.addMouseListener(new DeleteReportMouseListener(portTree));
        treeMenu.show(portTree, event.getX(), event.getY());
    }

    /**
     * 源码迁移报告添加右键操作
     *
     * @param event        鼠标事件
     * @param analysisTask 源码迁移报告PortingTask.Task
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
     * 软件包重构添加左侧菜单。
     *
     * @param event        event
     * @param analysisTask analysisTask
     */
    private void addMenuForAnalysisTask(MouseEvent event, AnalysisTaskBean.Task analysisTask) {
        final Component component = event.getComponent();
        if (component instanceof Tree) {
            // 新建任务和删除历史报告菜单
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
                // 右键触发菜单
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
        // 右键触发菜单
        downloadItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                loadPackage(analysisTask);
            }
        });
    }

    /**
     * 加载软件包重构报告页面
     *
     * @param analysisTask analysisTask
     */
    private void loadAnalysisHtml(AnalysisTaskBean.Task analysisTask) {
        // 获取报告详情信息
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/autopack/history/" + analysisTask.getPath() + PortingIDEConstant.PATH_SEPARATOR,
            HttpMethod.GET.vaLue(), "");
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return;
        }
        // 获选HTML
        String htmlContent = PkgRebuildingHandler.loadHtml(response.getData());
        // 弹出保存文件选择框
        String fileName = analysisTask.getPath() + ".html";
        NotificationBean notification = new NotificationBean("",
            I18NServer.toLocale("plugins_porting_report_download_success", fileName),
            NotificationType.INFORMATION);
        notification.setProject(CommonUtil.getDefaultProject());
        UIUtils.saveTXTFileToLocalForDialog(htmlContent, fileName, notification);
    }

    /**
     * 删除软件包重构包
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
     * 下载软件包重构包。
     *
     * @param selectedObj selectedObj
     */
    private void loadPackage(AnalysisTaskBean.Task selectedObj) {
        PkgRebuildingHandler.downloadPackage(selectedObj.getPath(), selectedObj.getName());
    }

    /**
     * 右键菜单点击删除执行的操作
     *
     * @param portTree 当前的左侧树
     */
    private void deleteAction(Tree portTree) {
        // 父节点
        Object sourceRoot = portTree.getAnchorSelectionPath().getParentPath().getLastPathComponent();
        // 当前节点
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
     * 左侧树报告节点菜单删除逻辑
     *
     * @param portTree 当前的左侧树树
     * @param taskId   taskId
     * @param root     左侧树根节点
     * @param node     需要删除的目标节点
     */
    private void deleteSelectTreeNode(Tree portTree, String taskId,
        DefaultMutableTreeNode root, DefaultMutableTreeNode node) {
        // 点击删除后先将菜单隐藏再弹出消息弹框
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
        // 清除左侧树节点的选择事件，防止重复触发消息弹框
        portTree.clearSelection();
        if (select.equals(IDEMessageDialogUtil.ButtonName.DELETE.getKey())) {
            deleteServerReport(taskId, node);
            // 左侧树移除该报告节点
            root.remove(node);
            PortingIDEContext.decreaseReportsNum();
            // 若有另外打开的project, 需要刷新另外的project左侧树
            Project[] openProjects = ProjectUtil.getOpenProjects();
            for (Project proj : openProjects) {
                // 若报告已打开则关闭
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
        // 服务器删除该报告
        if (node.getUserObject() instanceof SoftwareAssessmentTaskBean.Task) {
            SoftwareAssessmentHandler.deleteAssessmentReport(taskId);
            return;
        }
        if (node.getUserObject() instanceof PortingTaskBean.Task) {
            SourcePortingHandler.deleteReport(taskId);
        }
    }

    /**
     * 关闭左侧树对应的报告
     *
     * @param project 打开的intellij project
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
     * 打开webView
     *
     * @param event 事件
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

        // 软件包重构
        if (userObject instanceof AnalysisTaskBean.Task) {
            openPage(((AnalysisTaskBean.Task) userObject).getPath(), PageType.ANALYSIS_CENTER_REPORT);
        }
    }

    /**
     * 左侧面板打开页面
     *
     * @param pageName 页面名称
     * @param pageType 页面类型
     */
    private void openPage(String pageName, PageType pageType) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance(project);
        List<VirtualFile> openFiles = instance.getOpenFiles();
        List<VirtualFile> collect = openFiles.stream()
            .filter(virtualFile -> virtualFile.getName().contains(pageName))
            .collect(Collectors.toList());
        // 增强功能打开页面每次重新刷新
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
            // 已打开，定位到该历史报告
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

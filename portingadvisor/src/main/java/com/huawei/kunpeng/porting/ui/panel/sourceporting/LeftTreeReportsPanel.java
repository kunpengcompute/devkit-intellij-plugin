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

package com.huawei.kunpeng.porting.ui.panel.sourceporting;

import static com.huawei.kunpeng.intellij.common.enums.BaseCacheVal.SERVER_VERSION;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.toolwindow.PortTreeCellRender;
import com.huawei.kunpeng.porting.action.toolwindow.PortingMouseEventImpl;
import com.huawei.kunpeng.porting.bean.AnalysisTaskBean;
import com.huawei.kunpeng.porting.bean.PortingTaskBean;
import com.huawei.kunpeng.porting.bean.SoftwareAssessmentTaskBean;
import com.huawei.kunpeng.porting.common.constant.LeftTreeTitleConstant;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.module.assesment.SoftwareAssessmentHandler;
import com.huawei.kunpeng.porting.http.module.pkgrebuild.PkgRebuildingHandler;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * The class LeftTreeReportsPanel: ???????????????????????????
 *
 * @since 2020-12-03
 */
public class LeftTreeReportsPanel extends IDEBasePanel {
    /**
     * ?????????????????????
     */
    private static final int NEED_SHOW_DEPTH = 2;

    private Project project;

    private JPanel mainPanel = new JPanel();

    private JPanel decoratorPanel;

    private Tree rootTree;

    private List<PortingTaskBean.Task> reports;

    private List<AnalysisTaskBean.Task> analysisResults;

    private List<SoftwareAssessmentTaskBean.Task> assessmentTasks;

    /**
     * ???????????????????????????
     *
     * @param toolWindow toolWindow
     * @param panelName  panelName
     * @param project    project
     * @param reports    ??????
     */
    public LeftTreeReportsPanel(ToolWindow toolWindow, String panelName, Project project,
                                List<PortingTaskBean.Task> reports) {
        this.project = project;
        this.reports = reports;
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LEFT_TREE_REPORTS.panelName() : panelName;
        initPanel();
        registerComponentAction();
        createContent(mainPanel, null, false);
    }

    /**
     * ???????????????????????????
     *
     * @param toolWindow ????????????
     * @param project    ???????????????
     * @param reports    ??????????????????
     */
    public LeftTreeReportsPanel(ToolWindow toolWindow, Project project, List<PortingTaskBean.Task> reports) {
        this(toolWindow, null, project, reports);
    }

    /**
     * ?????????????????????????????????
     *
     * @param toolWindow ????????????
     * @param project    ???????????????
     */
    public LeftTreeReportsPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project, null);
    }

    /**
     * ???????????????
     */
    private void initPanel() {
        mainPanel.setLayout(new BorderLayout());
        // ??????????????????
        DefaultMutableTreeNode rootNode = initRootTree();

        addSoftwareAssessmentNode(rootNode);

        // ??????????????????++++++Begin
        addPortingNode(rootNode);
        // ??????????????????++++++END

        // ?????????????????????++++++Begin
        addAnalysisNode(rootNode);
        // ?????????????????????++++++end

        addMigrationCenterNode(rootNode);

        addEnhanceNode(rootNode);

        addActionButton();
        mainPanel.add(decoratorPanel, BorderLayout.CENTER);
        TreeUtil.expand(rootTree, NEED_SHOW_DEPTH);
    }

    private void addSoftwareAssessmentNode(DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode assessmentRootNode = new DefaultMutableTreeNode();
        assessmentRootNode.setUserObject(I18NServer.toLocale("plugins_porting_assessment_label"));
        rootNode.add(assessmentRootNode);
        // ??????porting ??????????????????T3
        String serverVersion = IDEContext.getValueFromGlobalContext(null, SERVER_VERSION.vaLue());
        if (!StringUtil.stringIsEmpty(serverVersion) &&
            PortingIDEConstant.COMPATIBLE_SERVER_VERSION.compareTo(serverVersion) < 0) {
            assessmentTasks = SoftwareAssessmentHandler.selectAllTasks();
            if (ValidateUtils.isNotEmptyCollection(assessmentTasks)) {
                initAssessmentTreeNode(assessmentTasks, assessmentRootNode);
            } else {
                DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode();
                emptyNode.setUserObject(LeftTreeTitleConstant.NO_REPORTS);
                assessmentRootNode.add(emptyNode);
            }
        }
    }

    private void initAssessmentTreeNode(List<SoftwareAssessmentTaskBean.Task> assessmentTasks,
                                        DefaultMutableTreeNode assessmentRootNode) {
        DefaultMutableTreeNode sourceRoot = assessmentRootNode;
        for (SoftwareAssessmentTaskBean.Task task : assessmentTasks) {
            DefaultMutableTreeNode newRecord = new DefaultMutableTreeNode();
            newRecord.setUserObject(task);
            sourceRoot.add(newRecord);
        }
    }

    /**
     * ????????????????????????
     *
     * @param rootNode ?????????
     */
    private void addAnalysisNode(DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode analysisRootNode = new DefaultMutableTreeNode();
        analysisRootNode.setUserObject(I18NServer.toLocale("plugins_porting_software_rebuilding_title"));
        rootNode.add(analysisRootNode);
        analysisResults = PkgRebuildingHandler.selectAllTasks();
        if (ValidateUtils.isNotEmptyCollection(analysisResults)) {
            initAnalysisTreeNode(analysisResults, analysisRootNode);
        } else {
            DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode();
            emptyNode.setUserObject(LeftTreeTitleConstant.NO_REPORTS);
            analysisRootNode.add(emptyNode);
        }
    }

    /**
     * ????????????????????????
     *
     * @param rootNode ?????????
     */
    private void addPortingNode(DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode portingRootNode = new DefaultMutableTreeNode();
        portingRootNode.setUserObject(LeftTreeTitleConstant.SOURCE_CODE_PORTING);
        rootNode.add(portingRootNode);
        if (reports != null && !reports.isEmpty()) {
            initPortingTree(reports, portingRootNode);
        } else {
            DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode();
            emptyNode.setUserObject(LeftTreeTitleConstant.NO_REPORTS);
            portingRootNode.add(emptyNode);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param rootNode ?????????
     */
    private void addMigrationCenterNode(DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode migrationCenterRootNode = new DefaultMutableTreeNode();
        migrationCenterRootNode.setUserObject(LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING);
        rootNode.add(migrationCenterRootNode);
    }

    /**
     * ????????????????????????
     *
     * @param rootNode ??????????????????
     */
    private void addEnhanceNode(DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode enhanceRootNode = new DefaultMutableTreeNode();
        enhanceRootNode.setUserObject(I18NServer.toLocale("plugins_porting_enhanced_function"));
        rootNode.add(enhanceRootNode);
    }

    /**
     * ??????????????????
     *
     * @return ???????????????
     */
    @NotNull
    private DefaultMutableTreeNode initRootTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        DefaultTreeModel rootModel = new DefaultTreeModel(rootNode);
        rootTree = new Tree(rootModel);
        rootTree.setCellRenderer(new PortTreeCellRender());
        rootTree.setRootVisible(false);
        rootTree.addMouseListener(new PortingMouseEventImpl(project));
        rootTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        return rootNode;
    }

    /**
     * ?????????????????????
     */
    private void addActionButton() {
        // add toolbarP
        AnActionButton portingActionButton = AnActionButton.fromAction(ActionManager.getInstance().getAction(
            "ide.kunpeng.action.porting.IDEPortingAction"));

        // ?????????????????????????????????
        AnActionButton analysisActionButton = AnActionButton.fromAction(ActionManager.getInstance().getAction(
            "ide.kunpeng.action.porting.IDEAnalysisAction"));

        // ???????????????????????????????????????
        AnActionButton migrationAppraiseActionButton = AnActionButton.fromAction(ActionManager.getInstance().getAction(
            "ide.kunpeng.action.porting.IDEMigrationAppraiseAction"));

        // ???????????????????????????
        AnActionButton enhancedActionButton = AnActionButton.fromAction(ActionManager.getInstance().getAction(
            "ide.kunpeng.action.porting.IDEEnhancedAction"));

        AnActionButton migrationCenterAction = AnActionButton.fromAction(ActionManager.getInstance().getAction(
            "ide.kunpeng.action.porting.MigrationCenterAction"));

        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(rootTree)
            .initPosition()
            .addExtraAction(migrationAppraiseActionButton)
            .addExtraAction(portingActionButton)
            .addExtraAction(analysisActionButton)
            .addExtraAction(migrationCenterAction)
            .addExtraAction(enhancedActionButton);

        decoratorPanel = toolbarDecorator.createPanel();
    }


    /**
     * ????????????????????????
     *
     * @param tasks           ???????????????????????????????????????
     * @param portingRootNode portingRootNode
     */
    private void initPortingTree(List<PortingTaskBean.Task> tasks, DefaultMutableTreeNode portingRootNode) {
        DefaultMutableTreeNode sourceRoot = portingRootNode;
        for (PortingTaskBean.Task task : tasks) {
            DefaultMutableTreeNode newRecord = new DefaultMutableTreeNode();
            newRecord.setUserObject(task);
            sourceRoot.add(newRecord);
        }
    }

    /**
     * ????????????????????????
     *
     * @param tasks           ???????????????????????????????????????
     * @param portingRootNode portingRootNode
     */
    private void initAnalysisTreeNode(List<AnalysisTaskBean.Task> tasks, DefaultMutableTreeNode portingRootNode) {
        DefaultMutableTreeNode sourceRoot = portingRootNode;
        for (AnalysisTaskBean.Task task : tasks) {
            DefaultMutableTreeNode newRecord = new DefaultMutableTreeNode();
            newRecord.setUserObject(task);
            sourceRoot.add(newRecord);
        }
    }

    /**
     * ???taskId???????????????????????????
     *
     * @param taskId taskId
     * @return ????????????
     */
    private String taskId2ReportId(String taskId) {
        StringBuilder recordTime = new StringBuilder();
        if (taskId != null && taskId.length() == 14) {
            recordTime.append(taskId, 0, 4);
            recordTime.append("/");
            recordTime.append(taskId, 4, 6);
            recordTime.append("/");
            recordTime.append(taskId, 6, 8);
            recordTime.append(" ");
            recordTime.append(taskId, 8, 10);
            recordTime.append(":");
            recordTime.append(taskId, 10, 12);
            recordTime.append(":");
            recordTime.append(taskId, 12, 14);
        }
        return recordTime.toString();
    }

    /**
     * ???????????????
     */
    public void refreshPortingTreePanel() {
        if (rootTree != null) {
            rootTree.updateUI();
            rootTree.validate();
            rootTree.repaint();
        }
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

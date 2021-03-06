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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.CustomAddNodeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskTemplateContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskCommonFormatUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskTemplateFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigTaskParamBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskAnalysisTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.Tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * ?????????????????? ???????????????
 *
 * @since 2021-4-25
 */
public class TaskTemplateDetailPanel extends IDEBasePanel {
    private JPanel mainPanel;
    private JPanel taskNamePanel;
    private JPanel analysisTargetPanel;
    private JPanel analysisTypePanel;
    private JPanel operateTypePanel;
    private JPanel targetTimeLPanel;
    private JPanel cycleAppointPanel;
    private JPanel cyclePanel;
    private JPanel appointmentPanel;
    private JPanel nodePanel;
    private JPanel otherInfoPanel;
    private JScrollPane nodeScrollPane;

    private JLabel taskNameL1;
    private JLabel taskNameL2;
    private JLabel analysisTargetL1;
    private JLabel analysisTargetL2;
    /**
     * ???????????? ???????????????????????????????????????
     */
    private JPanel analysisModePanel;
    private JLabel analysisModeLabel1;
    private JLabel analysisModeLabel2;
    private JLabel analysisTypeL1;
    private JLabel analysisTypeL2;
    private JLabel operateTypeL1;
    private JLabel operateTypeL2;
    private JLabel targetTimeL1;
    private JLabel targetTimeL2;
    private JLabel cycleAppointL1;
    private JLabel appointmentL2;
    private JLabel cycleStartL2;
    private JLabel cycleStopL2;
    private JPanel basicInfoPanel;
    private JPanel schInfoPanel;
    private JScrollPane mainScrollPane;
    private JPanel allInfoPanel;
    private JPanel accessAnalysisTypePanel;
    private JLabel accessAnalysisTypeLabel1;
    private JLabel accessAnalysisTypeLabel2;
    private final TaskTemplateBean templateItem;
    private Tree nodeTree;
    private SchTaskPanelHandle otherInfoHandle;
    private final int panelWidth = 500;
    private final int panelHeight = 25;
    private final int label1Width = 120;
    private final int label1Height = 25;
    private final String currentAnalysisType;

    public TaskTemplateDetailPanel(TaskTemplateBean bean) {
        templateItem = bean;
        currentAnalysisType = templateItem.getAnalysisType();
        // ???????????????
        initPanel(mainPanel);

        // ??????????????????????????????
        registerComponentAction();

        // ?????????content??????
        createContent(mainPanel, TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DETAIL, false);
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        mainPanel.setMaximumSize(new Dimension(panelWidth, 500));
        mainScrollPane.setMaximumSize(new Dimension(panelWidth, 500));
        mainScrollPane.setBorder(null);
        setBasicPanelShow(); // ?????????????????????
        // ????????????????????????
        setAnalysisTypeInfoShow();
        setSchInfoPanel(); // ????????????????????????
        initNodeTree(); // ????????????????????????
    }

    /**
     * ?????? ????????????Panel???????????????
     */
    private void setBasicPanelShow() {
        Dimension labelDim = new Dimension(label1Width, label1Height);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);

        // ????????????
        this.taskNamePanel.setPreferredSize(panelDim);
        this.taskNameL1.setPreferredSize(labelDim);
        this.taskNameL1.setText(TaskManageContent.TASK_NAME);
        this.taskNameL2.setText(templateItem.getTaskName());

        // ????????????
        this.analysisTargetPanel.setPreferredSize(panelDim);
        this.analysisTargetL1.setPreferredSize(labelDim);
        this.analysisTargetL1.setText(TaskManageContent.ANALYSIS_TARGET);
        String analysisTargetFormat = TaskTemplateFormatUtil.analysisTargetFormat(templateItem.getAnalysisTarget());
        this.analysisTargetL2.setText(analysisTargetFormat);

        // ????????????
        if (TaskManageContent.ANALYSIS_TARGET_APP.equals(analysisTargetFormat)) {
            this.analysisModeLabel1.setPreferredSize(labelDim);
            analysisModePanel.setVisible(true);
            analysisModeLabel1.setText(TaskManageContent.ANALYSIS_MODE);
            analysisModeLabel2.setText(templateItem.getAnalysisTarget());
        } else {
            analysisModePanel.setVisible(false);
        }

        // ????????????
        this.analysisTypePanel.setPreferredSize(panelDim);
        this.analysisTypeL1.setPreferredSize(labelDim);
        this.analysisTypeL1.setText(TaskManageContent.ANALYSIS_TYPE);
        String analysisType = templateItem.getAnalysisType();
        String analysisTypeFormat = TaskTemplateFormatUtil.analysisTypeFormat(analysisType);
        this.analysisTypeL2.setText(analysisTypeFormat);

        // ?????????????????? ??????????????????????????????????????????????????????????????????
        if (TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS.equals(analysisTypeFormat)) {
            setMemAccessAnalysisTypePanelShow(analysisType);
        } else {
            accessAnalysisTypePanel.setVisible(false);
        }
    }

    private void setMemAccessAnalysisTypePanelShow(String analysisType) {
        accessAnalysisTypePanel.setVisible(true);
        this.accessAnalysisTypeLabel1.setText(TaskManageContent.ACCESS_ANALYSIS_TYPE);
        String format = TaskCommonFormatUtil.getAccessAnalysisType(analysisType);
        this.accessAnalysisTypeLabel2.setText(format);
    }

    private void setAnalysisTypeInfoShow() {
        GridLayout gridLayout = new GridLayout();
        otherInfoPanel.setLayout(gridLayout);
        otherInfoPanel.setPreferredSize(new Dimension(500, -1));
        otherInfoPanel.setBorder(null);
        String analysisType = templateItem.getAnalysisType();
        SchTaskAnalysisTypeEnum type = SchTaskAnalysisTypeEnum.getType(analysisType);
        otherInfoHandle = type.getTempDetailPanelHandle(otherInfoPanel, templateItem);
        otherInfoHandle.addToTemplateDetail();
        int count = otherInfoPanel.getComponentCount();
        gridLayout.setRows(count); // ????????????
        gridLayout.setColumns(1); // ????????????
        gridLayout.setVgap(8); // ??????????????????
    }

    /**
     * ?????? ???????????? ????????? Panel???????????????
     */
    private void setSchInfoPanel() {
        Boolean cycle = templateItem.getCycle();
        if (cycle == null) {
            // ???????????????- ????????????????????????Panel ????????????
            this.appointmentPanel.setVisible(false);
            this.cyclePanel.setVisible(false);
            this.targetTimeLPanel.setVisible(false);
            this.operateTypePanel.setVisible(false);
            this.cycleAppointPanel.setVisible(false);
            return;
        }
        Dimension labelDim = new Dimension(label1Width, label1Height);
        this.operateTypePanel.setPreferredSize(new Dimension(panelWidth, panelHeight + 7));
        this.operateTypeL1.setText(TaskManageContent.PARAM_OPERATE_TYPE); // ????????????
        this.operateTypeL1.setPreferredSize(labelDim);
        this.targetTimeL1.setText(TaskManageContent.PARAM_TARGET_TIME); // ????????????
        this.targetTimeL1.setPreferredSize(labelDim);
        this.targetTimeL2.setText(templateItem.getTargetTime());
        this.targetTimeL2.setPreferredSize(labelDim);
        this.cycleAppointL1.setText(TaskManageContent.PARAM_SAMPLE_DATE); // ????????????
        this.cycleAppointL1.setPreferredSize(labelDim);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);
        if (cycle) {
            // ????????????-????????????
            this.appointmentPanel.setVisible(false);
            this.appointmentPanel.setPreferredSize(panelDim);
            this.cyclePanel.setVisible(true);
            this.cyclePanel.setPreferredSize(panelDim);
            this.operateTypeL2.setText(TaskManageContent.PARAM_OPERATE_TYPE_CYCLE);
            this.cycleStartL2.setText(templateItem.getCycleStart());
            this.cycleStopL2.setText(templateItem.getCycleStop());
        } else {
            // ????????????-????????????
            this.appointmentPanel.setVisible(true);
            this.appointmentPanel.setPreferredSize(panelDim);
            this.cyclePanel.setVisible(false);
            this.operateTypeL2.setText(TaskManageContent.PARAM_OPERATE_TYPE_ONCE);
            this.appointmentL2.setText(templateItem.getAppointment());
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     * ????????????????????????
     * ??????????????????????????????
     * ???????????????????????????
     */
    private void initNodeTree() {
        int nodeSize = templateItem.getNodeConfig().size();
        String aSwitch = templateItem.getSwitch();
        // ??????????????????????????????????????? ??????????????????????????????????????????
        if (nodeSize <= 1 || getNoMultiNodeSet().contains(currentAnalysisType) || "false".equals(aSwitch)) {
            this.nodeScrollPane.setVisible(false);
            this.analysisTargetL1.setFocusable(true);
            return;
        }
        nodeScrollPane.setPreferredSize(new Dimension(500, 80));
        nodePanel.setLayout(new BorderLayout());

        // ??????????????? ??????????????????
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Node");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);
        nodeTree = new Tree(defaultTreeModel);
        nodeTree.setExpandableItemsEnabled(true);
        nodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // ??????????????????????????????
        nodeTree.setShowsRootHandles(true);

        // ???????????????????????????????????????
        otherInfoHandle.addNodeTreeToTempDetail(rootNode);
        nodeTree.expandPath(nodeTree.getPathForRow(0));
        nodeTree.setRootVisible(false);

        // ????????????????????????
        nodeTree.setEditable(false);
        nodePanel.add(nodeTree);
        nodeScrollPane.setBorder(null);
    }

    private HashSet<String> getNoMultiNodeSet() {
        HashSet<String> noMultiNodeSet = new HashSet<>();
        noMultiNodeSet.add("system");
        noMultiNodeSet.add("mem_access");
        noMultiNodeSet.add("hpc_analysis");
        return noMultiNodeSet;
    }

    // ?????????????????? ????????????????????????????????????
    private void addNodeIntoToTreeNode(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        switch (currentAnalysisType) {
            case "C/C++ Program":
                cppNodeShow(oneNodeNode, configBean);
                break;
            case "microarchitecture":
                microNodeShow(oneNodeNode, configBean);
                break;
            case "resource_schedule":
                resNodeShow(oneNodeNode, configBean);
                break;
            case "system_lock":
                lockNodeShow(oneNodeNode, configBean);
                break;
            case "ioperformance":
                ioNodeShow(oneNodeNode, configBean);
                break;
            default:
                Logger.warn("no match AnalysisType" + currentAnalysisType);
                break;
        }
    }

    // ?????????????????? ????????????????????????????????????
    private void addNodeIntoToTreeNode2(DefaultMutableTreeNode rootNode) {
        java.util.List<NodeConfigBean> nodeConfigBeans = templateItem.getNodeConfig();
        for (NodeConfigBean configBean : nodeConfigBeans) {
            // ??????????????????
            DefaultMutableTreeNode oneNodeNode =
                    new DefaultMutableTreeNode(configBean.getNickName() + "(" + configBean.getNodeIp() + ")");
            if ("miss_event".equals(currentAnalysisType)) {
                missNodeShow(oneNodeNode, configBean);
            } else if ("falsesharing".equals(currentAnalysisType)) {
                falseNodeShow(oneNodeNode, configBean);
            } else {
                Logger.warn("no match AnalysisType" + currentAnalysisType);
                return;
            }

            // ??????????????????????????????
            rootNode.add(oneNodeNode);
        }
    }

    private void cppNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // ?????????CPU???
        String cpuMask = configBean.getTaskParam().getCpuMask();
        if (!StringUtil.stringIsEmpty(cpuMask)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_CPU_MASK, cpuMask));
        }

        // ?????????????????????
        String assemblyLocation = configBean.getTaskParam().getAssemblyLocation();
        if (!StringUtil.stringIsEmpty(assemblyLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, assemblyLocation));
        }

        // C/C++???????????????
        String sourceLocation = configBean.getTaskParam().getSourceLocation();
        if (!StringUtil.stringIsEmpty(sourceLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_SOURCE_LOCATION, sourceLocation));
        }
    }

    private void microNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // C/C++???????????????
        String sourceLocation = configBean.getTaskParam().getSourceLocation();
        if (!StringUtil.stringIsEmpty(sourceLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_SOURCE_LOCATION, sourceLocation));
        }
        // ?????????CPU???
        String cpuMask = configBean.getTaskParam().getCpuMask();
        if (!StringUtil.stringIsEmpty(cpuMask)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_CPU_MASK, cpuMask));
        }
    }

    private void resNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // ?????????????????????
        String assemblyLocation = configBean.getTaskParam().getAssemblyLocation();
        if (!StringUtil.stringIsEmpty(assemblyLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, assemblyLocation));
        }
    }

    private void lockNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // ?????????CPU???
        String cpuMask = configBean.getTaskParam().getCpuMask();
        if (!StringUtil.stringIsEmpty(cpuMask)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_CPU_MASK, cpuMask));
        }
        // ?????????????????????
        String assemblyLocation = configBean.getTaskParam().getAssemblyLocation();
        if (!StringUtil.stringIsEmpty(assemblyLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, assemblyLocation));
        }
    }

    private void ioNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        CustomAddNodeAction addAction = new CustomAddNodeAction(oneNodeNode);
        addAction
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, taskParamBean.getAppDir()) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, taskParamBean.getAppParameters()) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_PID, taskParamBean.getPid()) // PID
                .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, taskParamBean.getProcessName()); // ????????????
    }

    private void missNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        String taskParam2Str = configBean.getTaskParam().getTaskParam();
        JSONObject taskParam2 = JsonUtil.getJsonObjectFromJsonStr(taskParam2Str);
        CustomAddNodeAction addAction = new CustomAddNodeAction(oneNodeNode);
        addAction
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, taskParam2.getString("app")) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, taskParam2.getString("appArgs")) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_PID, taskParam2.getString("pid")) // PID
                .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, taskParam2.getString("process_name")) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam2.getString("cpu")) // ????????????cpu
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam2.getString("srcDir")); // C++?????????
    }

    private void falseNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        CustomAddNodeAction addNodeAction = new CustomAddNodeAction(oneNodeNode);
        addNodeAction
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, taskParamBean.getAppDir()) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, taskParamBean.getAppParameters()) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_PID, taskParamBean.getPid()) // PID
                .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, taskParamBean.getProcessName()) // ????????????
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParamBean.getCpuMask()) // ????????????cpu
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParamBean.getAssemblyLocation()) // ?????????
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParamBean.getSourceLocation()); // C???????????????
    }

    private DefaultMutableTreeNode getTreeNode(String keyStr, String valurStr) {
        return new DefaultMutableTreeNode(keyStr + "    " + valurStr);
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    private void createUIComponents() {
    }
}

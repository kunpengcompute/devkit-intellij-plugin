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
 * 任务模板详情 面板定义类
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
     * 分析模式 （仅分析对象为应用时展示）
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
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DETAIL, false);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        mainPanel.setMaximumSize(new Dimension(panelWidth, 500));
        mainScrollPane.setMaximumSize(new Dimension(panelWidth, 500));
        mainScrollPane.setBorder(null);
        setBasicPanelShow(); // 设置基础信息、
        // 设置其他相关信息
        setAnalysisTypeInfoShow();
        setSchInfoPanel(); // 设置预约相关信息
        initNodeTree(); // 加载节点相关信息
    }

    /**
     * 设置 基础属性Panel显示与隐藏
     */
    private void setBasicPanelShow() {
        Dimension labelDim = new Dimension(label1Width, label1Height);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);

        // 任务名称
        this.taskNamePanel.setPreferredSize(panelDim);
        this.taskNameL1.setPreferredSize(labelDim);
        this.taskNameL1.setText(TaskManageContent.TASK_NAME);
        this.taskNameL2.setText(templateItem.getTaskName());

        // 分析对象
        this.analysisTargetPanel.setPreferredSize(panelDim);
        this.analysisTargetL1.setPreferredSize(labelDim);
        this.analysisTargetL1.setText(TaskManageContent.ANALYSIS_TARGET);
        String analysisTargetFormat = TaskTemplateFormatUtil.analysisTargetFormat(templateItem.getAnalysisTarget());
        this.analysisTargetL2.setText(analysisTargetFormat);

        // 分析模式
        if (TaskManageContent.ANALYSIS_TARGET_APP.equals(analysisTargetFormat)) {
            this.analysisModeLabel1.setPreferredSize(labelDim);
            analysisModePanel.setVisible(true);
            analysisModeLabel1.setText(TaskManageContent.ANALYSIS_MODE);
            analysisModeLabel2.setText(templateItem.getAnalysisTarget());
        } else {
            analysisModePanel.setVisible(false);
        }

        // 分析类型
        this.analysisTypePanel.setPreferredSize(panelDim);
        this.analysisTypeL1.setPreferredSize(labelDim);
        this.analysisTypeL1.setText(TaskManageContent.ANALYSIS_TYPE);
        String analysisType = templateItem.getAnalysisType();
        String analysisTypeFormat = TaskTemplateFormatUtil.analysisTypeFormat(analysisType);
        this.analysisTypeL2.setText(analysisTypeFormat);

        // 访存分析类型 仅分析类型为【访存分析类型】时需要展示该字段
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
        gridLayout.setRows(count); // 设置行数
        gridLayout.setColumns(1); // 设置列数
        gridLayout.setVgap(8); // 设置竖直间隙
    }

    /**
     * 设置 预约任务 相关的 Panel显示与隐藏
     */
    private void setSchInfoPanel() {
        Boolean cycle = templateItem.getCycle();
        if (cycle == null) {
            // 非预约任务- 不展示采集时间等Panel 直接返回
            this.appointmentPanel.setVisible(false);
            this.cyclePanel.setVisible(false);
            this.targetTimeLPanel.setVisible(false);
            this.operateTypePanel.setVisible(false);
            this.cycleAppointPanel.setVisible(false);
            return;
        }
        Dimension labelDim = new Dimension(label1Width, label1Height);
        this.operateTypePanel.setPreferredSize(new Dimension(panelWidth, panelHeight + 7));
        this.operateTypeL1.setText(TaskManageContent.PARAM_OPERATE_TYPE); // 采集方式
        this.operateTypeL1.setPreferredSize(labelDim);
        this.targetTimeL1.setText(TaskManageContent.PARAM_TARGET_TIME); // 采集时间
        this.targetTimeL1.setPreferredSize(labelDim);
        this.targetTimeL2.setText(templateItem.getTargetTime());
        this.targetTimeL2.setPreferredSize(labelDim);
        this.cycleAppointL1.setText(TaskManageContent.PARAM_SAMPLE_DATE); // 采集日期
        this.cycleAppointL1.setPreferredSize(labelDim);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);
        if (cycle) {
            // 预约任务-周期采集
            this.appointmentPanel.setVisible(false);
            this.appointmentPanel.setPreferredSize(panelDim);
            this.cyclePanel.setVisible(true);
            this.cyclePanel.setPreferredSize(panelDim);
            this.operateTypeL2.setText(TaskManageContent.PARAM_OPERATE_TYPE_CYCLE);
            this.cycleStartL2.setText(templateItem.getCycleStart());
            this.cycleStopL2.setText(templateItem.getCycleStop());
        } else {
            // 预约任务-单次采集
            this.appointmentPanel.setVisible(true);
            this.appointmentPanel.setPreferredSize(panelDim);
            this.cyclePanel.setVisible(false);
            this.operateTypeL2.setText(TaskManageContent.PARAM_OPERATE_TYPE_ONCE);
            this.appointmentL2.setText(templateItem.getAppointment());
        }
    }

    /**
     * 加载多节点信息，当以下情况，不展示不加载相关信息
     * 只有一个节点时；
     * 为特定的分析类型时；
     * 未配置多节点参数；
     */
    private void initNodeTree() {
        int nodeSize = templateItem.getNodeConfig().size();
        String aSwitch = templateItem.getSwitch();
        // 单节点任务无需展示节点信息 特定任务类型不展示多节点信息
        if (nodeSize <= 1 || getNoMultiNodeSet().contains(currentAnalysisType) || "false".equals(aSwitch)) {
            this.nodeScrollPane.setVisible(false);
            this.analysisTargetL1.setFocusable(true);
            return;
        }
        nodeScrollPane.setPreferredSize(new Dimension(500, 80));
        nodePanel.setLayout(new BorderLayout());

        // 创建根节点 （不做展示）
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Node");
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(rootNode);
        nodeTree = new Tree(defaultTreeModel);
        nodeTree.setExpandableItemsEnabled(true);
        nodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // 设置树显示根节点句柄
        nodeTree.setShowsRootHandles(true);

        // 将多节点信息添加到树节点中
        otherInfoHandle.addNodeTreeToTempDetail(rootNode);
        nodeTree.expandPath(nodeTree.getPathForRow(0));
        nodeTree.setRootVisible(false);

        // 设置树节点可编辑
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

    // 创建三级节点 配置项，并添加到二级节点
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

    // 创建三级节点 配置项，并添加到二级节点
    private void addNodeIntoToTreeNode2(DefaultMutableTreeNode rootNode) {
        java.util.List<NodeConfigBean> nodeConfigBeans = templateItem.getNodeConfig();
        for (NodeConfigBean configBean : nodeConfigBeans) {
            // 创建二级节点
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

            // 添加二级节点到根节点
            rootNode.add(oneNodeNode);
        }
    }

    private void cppNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // 带采样CPU核
        String cpuMask = configBean.getTaskParam().getCpuMask();
        if (!StringUtil.stringIsEmpty(cpuMask)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_CPU_MASK, cpuMask));
        }

        // 二进制文件路径
        String assemblyLocation = configBean.getTaskParam().getAssemblyLocation();
        if (!StringUtil.stringIsEmpty(assemblyLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, assemblyLocation));
        }

        // C/C++源文件路径
        String sourceLocation = configBean.getTaskParam().getSourceLocation();
        if (!StringUtil.stringIsEmpty(sourceLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_SOURCE_LOCATION, sourceLocation));
        }
    }

    private void microNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // C/C++源文件路径
        String sourceLocation = configBean.getTaskParam().getSourceLocation();
        if (!StringUtil.stringIsEmpty(sourceLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_SOURCE_LOCATION, sourceLocation));
        }
        // 带采样CPU核
        String cpuMask = configBean.getTaskParam().getCpuMask();
        if (!StringUtil.stringIsEmpty(cpuMask)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_CPU_MASK, cpuMask));
        }
    }

    private void resNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // 二进制文件路径
        String assemblyLocation = configBean.getTaskParam().getAssemblyLocation();
        if (!StringUtil.stringIsEmpty(assemblyLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, assemblyLocation));
        }
    }

    private void lockNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        // 带采样CPU核
        String cpuMask = configBean.getTaskParam().getCpuMask();
        if (!StringUtil.stringIsEmpty(cpuMask)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_CPU_MASK, cpuMask));
        }
        // 二进制文件路径
        String assemblyLocation = configBean.getTaskParam().getAssemblyLocation();
        if (!StringUtil.stringIsEmpty(assemblyLocation)) {
            oneNodeNode.add(getTreeNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, assemblyLocation));
        }
    }

    private void ioNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        CustomAddNodeAction addAction = new CustomAddNodeAction(oneNodeNode);
        addAction
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, taskParamBean.getAppDir()) // 应用路径
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, taskParamBean.getAppParameters()) // 应用参数
                .addNotNullToNode(TaskManageContent.PARAM_PID, taskParamBean.getPid()) // PID
                .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, taskParamBean.getProcessName()); // 进程名称
    }

    private void missNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        String taskParam2Str = configBean.getTaskParam().getTaskParam();
        JSONObject taskParam2 = JsonUtil.getJsonObjectFromJsonStr(taskParam2Str);
        CustomAddNodeAction addAction = new CustomAddNodeAction(oneNodeNode);
        addAction
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, taskParam2.getString("app")) // 应用路径
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, taskParam2.getString("appArgs")) // 应用参数
                .addNotNullToNode(TaskManageContent.PARAM_PID, taskParam2.getString("pid")) // PID
                .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, taskParam2.getString("process_name")) // 进程名称
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam2.getString("cpu")) // 带采样核cpu
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam2.getString("srcDir")); // C++源文件
    }

    private void falseNodeShow(DefaultMutableTreeNode oneNodeNode, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        CustomAddNodeAction addNodeAction = new CustomAddNodeAction(oneNodeNode);
        addNodeAction
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, taskParamBean.getAppDir()) // 应用路径
                .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, taskParamBean.getAppParameters()) // 应用参数
                .addNotNullToNode(TaskManageContent.PARAM_PID, taskParamBean.getPid()) // PID
                .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, taskParamBean.getProcessName()) // 进程名称
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParamBean.getCpuMask()) // 带采样核cpu
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParamBean.getAssemblyLocation()) // 二进制
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParamBean.getSourceLocation()); // C源文件路径
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

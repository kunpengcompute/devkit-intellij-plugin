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
import com.huawei.kunpeng.hyper.tuner.action.sysperf.SchTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.SchTaskFormatUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskCommonFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigTaskParamBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.FalseHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MissEventHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskAnalysisTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.SchTaskTableRenderer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.Tree;

import static com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskAnalysisTypeEnum.FALSE_SHARE;
import static com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskAnalysisTypeEnum.MISS_EVENT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * 预约任务 展示详情 面板
 *
 * @since 2020-10-11
 */
public class SchTaskDetailPanel extends IDEBasePanel {
    private final int panelWidth = 500;
    private final int panelHeight = 25;
    private final int label1Width = 120;
    private final int label1Height = 25;
    private final String currentAnalysisType;
    private final SchTaskBean schTaskItem;
    private final JSONObject taskInfoJsonObj;
    private JPanel mainPanel;
    private JPanel basicInfoPanel;
    private JPanel schInfoPanel;
    private JLabel taskName1;
    private JLabel taskName2;
    private JLabel scheduleStatus1;
    private JLabel scheduleStatus2;
    private JLabel analysisTarget1;
    private JLabel analysisTarget2;
    private JLabel analysisType1;
    private JLabel projectName2;
    private JLabel analysisType2;
    private JLabel projectName1;
    private JLabel collectType1;
    private JLabel collectType2;
    private JLabel targetTime1;
    private JLabel targetTime2;
    private JLabel cycleDate1;
    private JLabel cycleDate2;
    private JScrollPane nodeScrollPane;
    private JPanel nodePanel;
    private JPanel taskNamePanel;
    private JPanel scheduleStatusPanel;
    private JPanel analysisTargetPanel;
    private JPanel analysisTypePanel;
    private JPanel projectNamePanel;
    private JPanel targetTimePanel;
    private JPanel collectTypePanel;
    private JPanel otherInfoPanel;
    private JPanel cycleDatePanel;
    private JScrollPane mainScrollPane;
    private JPanel allInfoPanel;
    private JPanel analysisModePanel;
    private JLabel analysisModeLabel1;
    private JLabel analysisModeLabel2;
    private JPanel accessAnalysisTypePanel;
    private JLabel accessAnalysisTypeLabel1;
    private JLabel accessAnalysisTypeLabel2;
    private Tree nodeTree;
    private SchTaskPanelHandle otherInfoHandle;

    public SchTaskDetailPanel(SchTaskBean bean) {
        this.panelName = "SchTaskDetailPanel";
        schTaskItem = bean;
        SchTaskAction schTaskManageAction = new SchTaskAction();
        taskInfoJsonObj = schTaskManageAction.getSchTaskInfoJSONObj(schTaskItem.getTaskId());
        currentAnalysisType = schTaskItem.getAnalysisType();
        initPanel(mainPanel); // 初始化面板
        registerComponentAction(); // 初始化面板内组件事件
        createContent(mainPanel, SchTaskContent.SCH_TASK_DIALOG_DETAIL_TITLE, false); // 初始化content实例
    }

    /**
     * 初始化主面板
     *
     * @param jPanel 面板
     */
    protected void initPanel(JPanel jPanel) {
        super.initPanel(jPanel);
        mainPanel.setMaximumSize(new Dimension(panelWidth, 500));
        mainScrollPane.setMaximumSize(new Dimension(panelWidth, 400));
        mainScrollPane.setBorder(null);
        setBasicInfoShow();
        setAnalysisTypeInfoShow();
        setScheduleInfoShow();
        initNodeTree(schTaskItem);
    }

    private void setAnalysisTypeInfoShow() {
        GridLayout gridLayout = new GridLayout();
        otherInfoPanel.setLayout(gridLayout);
        otherInfoPanel.setPreferredSize(new Dimension(500, -1));
        otherInfoPanel.setBorder(null);
        String analysisType = schTaskItem.getAnalysisType();
        SchTaskAnalysisTypeEnum type = SchTaskAnalysisTypeEnum.getType(analysisType);
        if (type.equals(MISS_EVENT)) {
            otherInfoHandle = new MissEventHandle(otherInfoPanel, taskInfoJsonObj, null);
            otherInfoHandle.setSchTaskItem(schTaskItem);
            otherInfoHandle.generateSchItemShowList();
        } else if (type.equals(FALSE_SHARE)) {
            otherInfoHandle = new FalseHandle(otherInfoPanel, schTaskItem, taskInfoJsonObj, null);
        } else {
            otherInfoHandle = type.getPanelHandle(otherInfoPanel, schTaskItem);
        }
        otherInfoHandle.addToSchDetail();
        int count = otherInfoPanel.getComponentCount();
        gridLayout.setRows(count); // 设置行数
        gridLayout.setColumns(1); // 设置列数
        gridLayout.setVgap(8); // 设置竖直间隙
    }

    /**
     * 设置 基本信息 Panel 显示
     */
    private void setBasicInfoShow() {
        Dimension labelDim = new Dimension(label1Width, label1Height);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);

        // 任务名称
        this.taskNamePanel.setPreferredSize(panelDim);
        this.taskName1.setPreferredSize(labelDim);
        this.taskName1.setText(TaskManageContent.TASK_NAME);
        this.taskName2.setText(schTaskItem.getTaskName());

        // 工程名
        this.projectNamePanel.setPreferredSize(panelDim);
        this.projectName1.setPreferredSize(labelDim);
        this.projectName1.setText(TaskManageContent.PARAM_BELONG_PROJECT);
        this.projectName2.setText(schTaskItem.getTaskInfo().getProjectName());

        // 分析对象
        this.analysisTargetPanel.setPreferredSize(panelDim);
        this.analysisTarget1.setPreferredSize(labelDim);
        this.analysisTarget1.setText(TaskManageContent.ANALYSIS_TARGET);
        String analysisTarget = SchTaskFormatUtil.analysisTargetFormat(schTaskItem.getTaskInfo().getAnalysisTarget());
        this.analysisTarget2.setText(analysisTarget);

        // 分析模式
        if (TaskManageContent.ANALYSIS_TARGET_APP.equals(analysisTarget)) {
            this.analysisModeLabel1.setPreferredSize(labelDim);
            this.analysisModePanel.setVisible(true);
            this.analysisModeLabel1.setText(TaskManageContent.ANALYSIS_MODE);
            this.analysisModeLabel2.setText(schTaskItem.getTaskInfo().getAnalysisTarget());
        } else {
            this.analysisModePanel.setVisible(false);
        }

        // 分析类型
        this.analysisTypePanel.setPreferredSize(panelDim);
        this.analysisType1.setPreferredSize(labelDim);
        this.analysisType1.setText(TaskManageContent.ANALYSIS_TYPE);
        String analysisType = schTaskItem.getAnalysisType();
        String analysisTypeFormat = SchTaskFormatUtil.analysisTypeFormat(analysisType);
        this.analysisType2.setText(analysisTypeFormat);

        // 访存分析类型 仅分析类型为 访存分析类型时需要展示该字段
        if (TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS.equals(analysisTypeFormat)) {
            setMemAccessAnalysisTypePanelShow(analysisType);
        } else {
            accessAnalysisTypePanel.setVisible(false);
        }
        // 预约状态
        this.scheduleStatusPanel.setPreferredSize(panelDim);
        this.scheduleStatus1.setPreferredSize(labelDim);
        this.scheduleStatus1.setText(TaskManageContent.TASK_STATUS);
        String scheduleStatus = SchTaskFormatUtil.stateFormat(schTaskItem.getScheduleStatus());
        this.scheduleStatus2.setText(scheduleStatus);
        SchTaskTableRenderer renderer = new SchTaskTableRenderer();
        ImageIcon icon = renderer.getStatusIcon(schTaskItem.getScheduleStatus());
        this.scheduleStatus2.setIcon(icon);
    }

    private void setMemAccessAnalysisTypePanelShow(String analysisType) {
        accessAnalysisTypePanel.setVisible(true);
        this.accessAnalysisTypeLabel1.setText(TaskManageContent.ACCESS_ANALYSIS_TYPE);
        String format = TaskCommonFormatUtil.getAccessAnalysisType(analysisType);
        this.accessAnalysisTypeLabel2.setText(format);
    }

    /**
     * 设置 预约信息 Panel 显示
     */
    private void setScheduleInfoShow() {
        Dimension labelDim = new Dimension(label1Width, label1Height);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);

        // 采集时间
        this.targetTime1.setText(TaskManageContent.PARAM_TARGET_TIME);
        this.targetTimePanel.setPreferredSize(panelDim);
        this.targetTime1.setPreferredSize(labelDim);
        targetTime2.setText(schTaskItem.getTargetTime() + "");

        // 是否周期采集
        this.collectType1.setText(TaskManageContent.PARAM_OPERATE_TYPE);
        this.collectTypePanel.setPreferredSize(new Dimension(panelWidth, panelHeight + 5));
        this.collectType1.setPreferredSize(labelDim);
        this.cycleDate1.setText(TaskManageContent.PARAM_SAMPLE_DATE);
        this.cycleDate1.setPreferredSize(labelDim);
        boolean cycle = schTaskItem.isCycle();
        if (cycle) {
            collectType2.setText(TaskManageContent.PARAM_OPERATE_TYPE_CYCLE); // 采集方式-周期采集
            String dateStr = schTaskItem.getTaskInfo().getCycleStart() + "-" + schTaskItem.getTaskInfo().getCycleStop();
            cycleDate2.setText(dateStr); // 采集日期（时间段）
        } else {
            collectType2.setText(TaskManageContent.PARAM_OPERATE_TYPE_ONCE); // 采集方式-单次采集
            cycleDate2.setText(schTaskItem.getTaskInfo().getAppointment()); // 采集日期（时间点）
        }
    }

    // 初始化节点树
    private void initNodeTree(SchTaskBean schTaskItem) {
        java.util.List<NodeConfigBean> nodeConfigBeans = schTaskItem.getTaskInfo().getNodeConfig();
        // 单节点任务无需展示节点信息；特定任务类型不展示多节点信息； 未配置多节点信息无需展示
        boolean switchResponse = schTaskItem.getTaskInfo().getSwitchResponse();
        if (nodeConfigBeans.size() < 2 || getNoMultiNodeSet().contains(currentAnalysisType) || !switchResponse) {
            nodeScrollPane.setVisible(false);
            cycleDate1.setFocusable(true);
            return;
        }
        nodeScrollPane.setPreferredSize(new Dimension(500, 80));
        nodePanel.setLayout(new BorderLayout());
        // 创建根节点 （不做展示）
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Node");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        nodeTree = new Tree(treeModel);
        nodeTree.setExpandableItemsEnabled(true);
        nodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        nodeTree.setShowsRootHandles(true); // 设置树显示根节点句柄

        // 将多节点信息添加到树节点中
        otherInfoHandle.addNodeTreeToDetail(rootNode);
        nodeTree.expandPath(nodeTree.getPathForRow(0));
        nodeTree.setRootVisible(false);
        nodeTree.setEditable(false); // 设置树节点不可编辑
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
        CustomAddNodeAction addAction = new CustomAddNodeAction(oneNodeNode);
        switch (currentAnalysisType) {
            case "C/C++ Program":
                cppNodeShow(addAction, configBean);
                break;
            case "microarchitecture":
                microNodeShow(addAction, configBean);
                break;
            case "resource_schedule":
                resNodeShow(addAction, configBean);
                break;
            case "system_lock":
                lockNodeShow(addAction, configBean);
                break;
            case "miss_event":
                missNodeShow(addAction, configBean);
                break;
            case "falsesharing":
                falseNodeShow(addAction, configBean);
                break;
            case "ioperformance":
                ioNodeShow(addAction, configBean);
                break;
            default:
                Logger.warn("no match AnalysisType" + currentAnalysisType);
                break;
        }
    }

    private void cppNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* 带采样CPU核 */
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam.getCpuMask())
                /* 二进制文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParam.getAssemblyLocation())
                /* C/C++源文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam.getSourceLocation());
    }

    private void microNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* C/C++源文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam.getSourceLocation())
                /* 带采样CPU核 */
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam.getCpuMask());
    }

    private void resNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* 二进制文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParam.getAssemblyLocation());
    }

    private void lockNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* 二进制文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParam.getAssemblyLocation())
                /* C/C++源文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam.getSourceLocation());
    }

    private void missNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        String taskParam2Str = configBean.getTaskParam().getTaskParam();
        JSONObject taskParam2 = JsonUtil.getJsonObjectFromJsonStr(taskParam2Str);
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParam2.getString("app"), taskParam2.getString("appArgs"),
                taskParam2.getString("pid"), taskParam2.getString("process_name"))
                /* 带采样核cpu */
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam2.getString("cpu"))
                /* C/C++源文件路径 */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam2.getString("srcDir"));
    }

    private void falseNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParamBean.getAppDir(), taskParamBean.getAppParameters(), taskParamBean.getPid(),
                taskParamBean.getProcessName())
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParamBean.getCpuMask()) // 带采样核cpu
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParamBean.getAssemblyLocation()) // 二进制
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParamBean.getSourceLocation()); // C源文件路径
    }

    private void ioNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        // 应用路径 应用参数 PID 进程名称
        commonNodeShow(addAction, taskParamBean.getAppDir(), taskParamBean.getAppParameters(), taskParamBean.getPid(),
                taskParamBean.getProcessName());
    }

    /**
     * 添加共有的节点参数
     *
     * @param addAction 自定义添加树节点Action
     * @param path      应用路径
     * @param param     应用参数
     * @param pid       PID
     * @param proName   进程名称
     * @return 自定义添加树节点Action
     */
    private CustomAddNodeAction commonNodeShow
    (CustomAddNodeAction addAction, String path, String param, String pid, String proName) {
        String target = schTaskItem.getTaskInfo().getAnalysisTarget();
        if (TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(target)) {
            addAction
                    .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, path) // 应用路径
                    .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, param); // 应用参数
        }
        if (TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS.equals(target)) {
            addAction
                    .addNotNullToNode(TaskManageContent.PARAM_PID, pid) // PID
                    .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, proName); // 进程名称
        }
        return addAction;
    }

    /**
     * 初始化面板内组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new SchTaskAction();
        }
    }

    /**
     * setAction
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof SchTaskAction) {
            this.action = action;
            registerComponentAction();
        }
    }
}

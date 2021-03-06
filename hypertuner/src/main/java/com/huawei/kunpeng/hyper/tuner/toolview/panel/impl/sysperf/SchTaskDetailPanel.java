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
 * ???????????? ???????????? ??????
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
        initPanel(mainPanel); // ???????????????
        registerComponentAction(); // ??????????????????????????????
        createContent(mainPanel, SchTaskContent.SCH_TASK_DIALOG_DETAIL_TITLE, false); // ?????????content??????
    }

    /**
     * ??????????????????
     *
     * @param jPanel ??????
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
        gridLayout.setRows(count); // ????????????
        gridLayout.setColumns(1); // ????????????
        gridLayout.setVgap(8); // ??????????????????
    }

    /**
     * ?????? ???????????? Panel ??????
     */
    private void setBasicInfoShow() {
        Dimension labelDim = new Dimension(label1Width, label1Height);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);

        // ????????????
        this.taskNamePanel.setPreferredSize(panelDim);
        this.taskName1.setPreferredSize(labelDim);
        this.taskName1.setText(TaskManageContent.TASK_NAME);
        this.taskName2.setText(schTaskItem.getTaskName());

        // ?????????
        this.projectNamePanel.setPreferredSize(panelDim);
        this.projectName1.setPreferredSize(labelDim);
        this.projectName1.setText(TaskManageContent.PARAM_BELONG_PROJECT);
        this.projectName2.setText(schTaskItem.getTaskInfo().getProjectName());

        // ????????????
        this.analysisTargetPanel.setPreferredSize(panelDim);
        this.analysisTarget1.setPreferredSize(labelDim);
        this.analysisTarget1.setText(TaskManageContent.ANALYSIS_TARGET);
        String analysisTarget = SchTaskFormatUtil.analysisTargetFormat(schTaskItem.getTaskInfo().getAnalysisTarget());
        this.analysisTarget2.setText(analysisTarget);

        // ????????????
        if (TaskManageContent.ANALYSIS_TARGET_APP.equals(analysisTarget)) {
            this.analysisModeLabel1.setPreferredSize(labelDim);
            this.analysisModePanel.setVisible(true);
            this.analysisModeLabel1.setText(TaskManageContent.ANALYSIS_MODE);
            this.analysisModeLabel2.setText(schTaskItem.getTaskInfo().getAnalysisTarget());
        } else {
            this.analysisModePanel.setVisible(false);
        }

        // ????????????
        this.analysisTypePanel.setPreferredSize(panelDim);
        this.analysisType1.setPreferredSize(labelDim);
        this.analysisType1.setText(TaskManageContent.ANALYSIS_TYPE);
        String analysisType = schTaskItem.getAnalysisType();
        String analysisTypeFormat = SchTaskFormatUtil.analysisTypeFormat(analysisType);
        this.analysisType2.setText(analysisTypeFormat);

        // ?????????????????? ?????????????????? ??????????????????????????????????????????
        if (TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS.equals(analysisTypeFormat)) {
            setMemAccessAnalysisTypePanelShow(analysisType);
        } else {
            accessAnalysisTypePanel.setVisible(false);
        }
        // ????????????
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
     * ?????? ???????????? Panel ??????
     */
    private void setScheduleInfoShow() {
        Dimension labelDim = new Dimension(label1Width, label1Height);
        Dimension panelDim = new Dimension(panelWidth, panelHeight);

        // ????????????
        this.targetTime1.setText(TaskManageContent.PARAM_TARGET_TIME);
        this.targetTimePanel.setPreferredSize(panelDim);
        this.targetTime1.setPreferredSize(labelDim);
        targetTime2.setText(schTaskItem.getTargetTime() + "");

        // ??????????????????
        this.collectType1.setText(TaskManageContent.PARAM_OPERATE_TYPE);
        this.collectTypePanel.setPreferredSize(new Dimension(panelWidth, panelHeight + 5));
        this.collectType1.setPreferredSize(labelDim);
        this.cycleDate1.setText(TaskManageContent.PARAM_SAMPLE_DATE);
        this.cycleDate1.setPreferredSize(labelDim);
        boolean cycle = schTaskItem.isCycle();
        if (cycle) {
            collectType2.setText(TaskManageContent.PARAM_OPERATE_TYPE_CYCLE); // ????????????-????????????
            String dateStr = schTaskItem.getTaskInfo().getCycleStart() + "-" + schTaskItem.getTaskInfo().getCycleStop();
            cycleDate2.setText(dateStr); // ???????????????????????????
        } else {
            collectType2.setText(TaskManageContent.PARAM_OPERATE_TYPE_ONCE); // ????????????-????????????
            cycleDate2.setText(schTaskItem.getTaskInfo().getAppointment()); // ???????????????????????????
        }
    }

    // ??????????????????
    private void initNodeTree(SchTaskBean schTaskItem) {
        java.util.List<NodeConfigBean> nodeConfigBeans = schTaskItem.getTaskInfo().getNodeConfig();
        // ??????????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????
        boolean switchResponse = schTaskItem.getTaskInfo().getSwitchResponse();
        if (nodeConfigBeans.size() < 2 || getNoMultiNodeSet().contains(currentAnalysisType) || !switchResponse) {
            nodeScrollPane.setVisible(false);
            cycleDate1.setFocusable(true);
            return;
        }
        nodeScrollPane.setPreferredSize(new Dimension(500, 80));
        nodePanel.setLayout(new BorderLayout());
        // ??????????????? ??????????????????
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Node");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        nodeTree = new Tree(treeModel);
        nodeTree.setExpandableItemsEnabled(true);
        nodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        nodeTree.setShowsRootHandles(true); // ??????????????????????????????

        // ???????????????????????????????????????
        otherInfoHandle.addNodeTreeToDetail(rootNode);
        nodeTree.expandPath(nodeTree.getPathForRow(0));
        nodeTree.setRootVisible(false);
        nodeTree.setEditable(false); // ???????????????????????????
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
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* ?????????CPU??? */
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam.getCpuMask())
                /* ????????????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParam.getAssemblyLocation())
                /* C/C++??????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam.getSourceLocation());
    }

    private void microNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* C/C++??????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam.getSourceLocation())
                /* ?????????CPU??? */
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam.getCpuMask());
    }

    private void resNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* ????????????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParam.getAssemblyLocation());
    }

    private void lockNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParam = configBean.getTaskParam();
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParam.getAppDir(), taskParam.getAppParameters(), taskParam.getTargetPid(),
                taskParam.getProcessName())
                /* ????????????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParam.getAssemblyLocation())
                /* C/C++??????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam.getSourceLocation());
    }

    private void missNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        String taskParam2Str = configBean.getTaskParam().getTaskParam();
        JSONObject taskParam2 = JsonUtil.getJsonObjectFromJsonStr(taskParam2Str);
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParam2.getString("app"), taskParam2.getString("appArgs"),
                taskParam2.getString("pid"), taskParam2.getString("process_name"))
                /* ????????????cpu */
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParam2.getString("cpu"))
                /* C/C++??????????????? */
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParam2.getString("srcDir"));
    }

    private void falseNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParamBean.getAppDir(), taskParamBean.getAppParameters(), taskParamBean.getPid(),
                taskParamBean.getProcessName())
                .addNotNullToNode(TaskManageContent.PARAM_CPU_MASK, taskParamBean.getCpuMask()) // ????????????cpu
                .addNotNullToNode(TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskParamBean.getAssemblyLocation()) // ?????????
                .addNotNullToNode(TaskManageContent.PARAM_SOURCE_LOCATION, taskParamBean.getSourceLocation()); // C???????????????
    }

    private void ioNodeShow(CustomAddNodeAction addAction, NodeConfigBean configBean) {
        NodeConfigTaskParamBean taskParamBean = configBean.getTaskParam();
        // ???????????? ???????????? PID ????????????
        commonNodeShow(addAction, taskParamBean.getAppDir(), taskParamBean.getAppParameters(), taskParamBean.getPid(),
                taskParamBean.getProcessName());
    }

    /**
     * ???????????????????????????
     *
     * @param addAction ????????????????????????Action
     * @param path      ????????????
     * @param param     ????????????
     * @param pid       PID
     * @param proName   ????????????
     * @return ????????????????????????Action
     */
    private CustomAddNodeAction commonNodeShow
    (CustomAddNodeAction addAction, String path, String param, String pid, String proName) {
        String target = schTaskItem.getTaskInfo().getAnalysisTarget();
        if (TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(target)) {
            addAction
                    .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PATH, path) // ????????????
                    .addNotNullToNode(TaskManageContent.PARAM_APPLICATION_PARAM, param); // ????????????
        }
        if (TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS.equals(target)) {
            addAction
                    .addNotNullToNode(TaskManageContent.PARAM_PID, pid) // PID
                    .addNotNullToNode(TaskManageContent.PARAM_PROCESS_NAME, proName); // ????????????
        }
        return addAction;
    }

    /**
     * ??????????????????????????????
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
     * @param action ????????????
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof SchTaskAction) {
            this.action = action;
            registerComponentAction();
        }
    }
}

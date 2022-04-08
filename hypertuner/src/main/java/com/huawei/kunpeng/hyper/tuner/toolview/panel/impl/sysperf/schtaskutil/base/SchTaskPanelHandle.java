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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskCommonFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.SchTaskUpdateNodeDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskUpdateNodePanel;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 分析任务-面板组件添加类
 * 预约任务详情/预约任务修改/任务模板详情
 *
 * @since 2021-8-26
 */
public class SchTaskPanelHandle {
    /**
     * 新的状态存在的组件id
     */
    protected static final int NEW_NODE_STATUS_COMP_INDEX = 2;
    /**
     * 用户修改后的值存在的组件在 所在panel中的索引
     */
    private static final int NEW_VALUE_COMP_INDEX = 1;
    /**
     * 待添加的面板
     */
    protected JPanel otherInfoPanel;
    /**
     * 预约任务对象
     */
    protected SchTaskBean schTaskItem;
    /**
     * 添加的面板和索引值map
     */
    protected HashMap<String, Integer> compIndexMap;
    /**
     * 预约任务对象 JSON格式
     */
    protected JSONObject taskInfoJsonObj;
    /**
     * 任务模板对象
     */
    protected TaskTemplateBean templateItem;
    /**
     * 修改后的进程名
     */
    protected String newProcessName;
    /**
     * 修改后的pid
     */
    protected String newPid;
    /**
     * 修改后应用路径
     */
    protected String newAppDir;
    /**
     * 修改后的应用参数
     */
    protected String newAppParam;
    /**
     * 多节点按钮是否可用
     */
    protected boolean switchBtnValid;
    /**
     * 当前otherInfoPanel最后一个添加的组件索引
     */
    protected int compIndex;
    /**
     * 字段国际化Map
     */
    protected HashMap<String, String> fileValueI18NMap = TaskCommonFormatUtil.getTaskInfoParamValueI18NMap();
    /**
     * 多选/单选 国际化Map
     */
    protected HashMap<String, String> selectN81IMap = TaskCommonFormatUtil.getSelectMap();
    /**
     * pid 字段 默认为 target-pid，各任务类型应按需覆盖
     */
    protected String pidFieldName = "target-pid";
    /**
     * 进程名 字段 默认为 process-name，各任务类型应按需覆盖
     */
    protected String processFieldName = "process-name";
    /**
     * 应用路径 字段 默认为 app-dir，各任务类型应按需覆盖
     */
    protected String appDirFieldName = "app-dir";
    /**
     * 应用参数字段 默认为 app-parameter ，各任务类型应按需覆盖
     */
    protected String appParamFieldName = "app-parameters";
    /**
     * 面板内带展示的数据列表
     *
     * @see SchTaskOtherInfo
     */
    protected List<SchTaskOtherInfo> detailAppInfoList = new ArrayList<>();
    /**
     * 面板内带展示的数据列表
     *
     * @see SchTaskOtherInfo
     */
    protected List<SchTaskOtherInfo> paramList = new ArrayList<>();
    /**
     * 多节点参数数据列表
     */
    protected List<JPanel> multiNodePanelList = new ArrayList<>();
    /**
     * 多节点参数映射，key为节点id，value为该节点 对应的参数列表
     */
    protected HashMap<Integer, List<SchTaskOtherInfo>> nodeParamMap = new HashMap<>();
    /**
     * 任务模板待展示数据列表
     */
    protected List<SchTaskOtherInfo> taskTempInfoList = new ArrayList<>();
    /**
     * 当前任务的分析类型，再预约任务和任务模板是生成条件不同
     */
    protected String currentAnalysisTarget;
    /**
     * 是否是可以编辑展示对象的值的面板
     */
    protected boolean isEdit = false;
    /**
     * 是否有多节点
     */
    protected boolean isMultiNode;

    /**
     * 使用 SchTaskBean 类型的预约任务信息初始化
     *
     * @param otherInfoPanel 被添加面板
     * @param schTaskItem    待修改预约任务对象
     * @param compIndexMap   组件索引Map
     */
    public SchTaskPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        this.otherInfoPanel = otherInfoPanel;
        this.schTaskItem = schTaskItem;
        this.compIndexMap = compIndexMap;
        setEdit(compIndexMap);
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public SchTaskPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        this(otherInfoPanel, new JSONObject(), null);
        this.templateItem = templateItem;
    }

    /**
     * 使用 JSONObject 类型的预约任务信息初始化
     *
     * @param otherInfoPanel  被添加面板
     * @param taskInfoJsonObj 待修改预约任务对象
     * @param comIndexMap     组件索引Map
     */
    public SchTaskPanelHandle(JPanel otherInfoPanel, JSONObject taskInfoJsonObj, HashMap<String, Integer> comIndexMap) {
        this.otherInfoPanel = otherInfoPanel;
        this.taskInfoJsonObj = taskInfoJsonObj;
        this.compIndexMap = comIndexMap;
        setEdit(comIndexMap);
    }

    /**
     * 设置预约任务对象   SchTaskBean 类型
     *
     * @param schTaskItem 预约任务对象
     */
    public void setSchTaskItem(SchTaskBean schTaskItem) {
        this.schTaskItem = schTaskItem;
    }

    public void setMultiNode(boolean multiNode) {
        isMultiNode = multiNode;
    }

    private void setEdit(HashMap<String, Integer> map) {
        if (map != null) {
            this.isEdit = Boolean.TRUE;
        } else {
            this.isEdit = Boolean.FALSE;
        }
    }

    /**
     * 将其他信息添加到 预约任务修改的 OtherInfoPanel 中
     */
    public void addPanel() {
        addHandle();
    }

    /**
     * 将其他信息添加到 预约任务详情 OtherInfoPanel 中
     */
    public void addToSchDetail() {
        String appDir; // 应用路径
        String appParam; // 应用参数
        String processName;  // 进程名
        String pid;  // PID
        if ("miss_event".equals(schTaskItem.getAnalysisType())) {
            appDir = taskInfoJsonObj.getJSONObject("task_param").getString("app");
            appParam = taskInfoJsonObj.getJSONObject("task_param").getString("appArgs");
            processName = taskInfoJsonObj.getJSONObject("task_param").getString("process_name");
            pid = taskInfoJsonObj.getJSONObject("task_param").getString("pid");
        } else {
            appDir = schTaskItem.getTaskInfo().getAppDir();
            appParam = schTaskItem.getTaskInfo().getAppParameters();
            processName = schTaskItem.getTaskInfo().getProcessName();
            pid = schTaskItem.getTaskInfo().getTargetPid();
            pid = pid == null ? schTaskItem.getTaskInfo().getPid() : pid;
        }
        if (TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(currentAnalysisTarget)) {
            // 应用路径
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "appDir",
                    TaskManageContent.PARAM_APPLICATION_PATH, appDir, false));
            // 应用参数
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "appParam",
                    TaskManageContent.PARAM_APPLICATION_PARAM, appParam, false));
        } else if (TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS.equals(currentAnalysisTarget)) {
            // 进程名
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "processName",
                    TaskManageContent.PARAM_PROCESS_NAME, processName, false));
            // PID
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "pid",
                    TaskManageContent.PARAM_PID, pid, false));
        } else {
            addHandle();
            return;
        }
        for (SchTaskOtherInfo item : detailAppInfoList) {
            Optional<JPanel> jLabelPanelOptional = EditPanelTypeEnum.J_LABEL
                    .getItemPanel(item.getFieldNameI18N(), item.getFieldValue(), null, null);
            jLabelPanelOptional.ifPresent(jPanel -> otherInfoPanel.add(jPanel));
        }
        addHandle();
    }

    /**
     * 将其他信息添加到 任务模板 OtherInfoPanel 中
     */
    public void addToTemplateDetail() {
        String appDir; // 应用路径
        String appParam; // 应用参数
        String processName;  // 进程名
        String pid;  // PID
        if ("miss_event".equals(templateItem.getAnalysisType())) {
            appDir = templateItem.getTaskParam().getApp();
            appParam = templateItem.getTaskParam().getAppArgs();
            processName = templateItem.getTaskParam().getProcessName();
            pid = templateItem.getTaskParam().getPid();
        } else {
            appDir = templateItem.getAppDir();
            appParam = templateItem.getAppParameters();
            processName = templateItem.getProcessName();
            pid = templateItem.getTargetPid();
            pid = pid == null ? templateItem.getPid() : pid;
        }
        if (TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(currentAnalysisTarget)) {
            // 应用参数
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "appParam",
                    TaskManageContent.PARAM_APPLICATION_PARAM, appParam, false));
            // 应用路径
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "appDir",
                    TaskManageContent.PARAM_APPLICATION_PATH, appDir, false));

        } else if (TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS.equals(currentAnalysisTarget)) {
            // PID
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "pid",
                    TaskManageContent.PARAM_PID, pid, false));
            // 进程名
            detailAppInfoList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "processName",
                    TaskManageContent.PARAM_PROCESS_NAME, processName, false));
        } else {
            addHandle();
            return;
        }
        for (SchTaskOtherInfo item : detailAppInfoList) {
            Optional<JPanel> jLabelPanelOptional = EditPanelTypeEnum.J_LABEL
                    .getItemPanel(item.getFieldNameI18N(), item.getFieldValue(), null, null);
            jLabelPanelOptional.ifPresent(jPanel -> otherInfoPanel.add(jPanel));
        }
        addHandle();
    }


    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
    }

    /**
     * 获取单个分析任务 多节点配置相关面板列表
     */
    public void generateMultiNodeInfoShowList() {
        List<NodeConfigBean> nodeConfigBeanList = schTaskItem.getTaskInfo().getNodeConfig(); // 节点列表
        for (NodeConfigBean configBean : nodeConfigBeanList) {
            MultiNodeParam nodeParam = new MultiNodeParam(configBean.getNodeId(), configBean.getNodeIp(),
                    configBean.getNickName(), configBean.getTaskParam().isStatus());
            // 获取多节点配置面板的展示的待修改参数 （各个任务类型根据需要重写）
            List<SchTaskOtherInfo> nodeParamList = genNodeParamList(configBean);
            nodeParam.setNodeParamList(nodeParamList);
            nodeParamMap.put(nodeParam.getNodeId(), nodeParamList);
            JPanel nodeConfigItemPanel = getNodeItemPanel(nodeParam);
            multiNodePanelList.add(nodeConfigItemPanel);
        }
    }

    /**
     * 获取单个分析任务 多节点配置相关面板列表
     */
    public void generateMultiNodeInfoShowListOfTemp() {
        List<NodeConfigBean> nodeConfigBeanList = templateItem.getNodeConfig(); // 节点列表
        for (NodeConfigBean configBean : nodeConfigBeanList) {
            // 获取多节点配置面板的展示的待修改参数 （各个任务类型根据需要重写）
            List<SchTaskOtherInfo> nodeParamList = genNodeParamList(configBean);
            nodeParamMap.put(configBean.getNodeId(), nodeParamList);
        }
    }

    /**
     * 根据多节点配置列表，将参数 添加到预约任务详情中节点树的内容
     *
     * @param rootNode 待添加的根节点
     */
    public void addNodeTreeToDetail(DefaultMutableTreeNode rootNode) {
        java.util.List<NodeConfigBean> nodeConfigBeans = schTaskItem.getTaskInfo().getNodeConfig();
        addAllNodeInfoToPanel(rootNode, nodeConfigBeans);
    }

    /**
     * 根据多节点配置列表，将参数 添加到预约任务详情中节点树的内容
     *
     * @param rootNode 待添加的根节点
     */
    public void addNodeTreeToTempDetail(DefaultMutableTreeNode rootNode) {
        java.util.List<NodeConfigBean> nodeConfigBeans = templateItem.getNodeConfig();
        addAllNodeInfoToPanel(rootNode, nodeConfigBeans);
    }

    private void addAllNodeInfoToPanel(DefaultMutableTreeNode rootNode, List<NodeConfigBean> nodeConfigBeans) {
        for (NodeConfigBean configBean : nodeConfigBeans) {
            int nodeId = configBean.getNodeId();
            String nickName = configBean.getNickName();
            DefaultMutableTreeNode oneNodeNode = new DefaultMutableTreeNode(nickName + "(" + nodeId + ")");
            List<SchTaskOtherInfo> thisNodeParamList = nodeParamMap.get(nodeId);
            addNodeTreeToDetailEveryType(oneNodeNode, thisNodeParamList);
            rootNode.add(oneNodeNode);
        }
    }

    /**
     * 各类型自定义节点数展示内容，默认展示 nodeParamMap 对应节点的list中的全部数据
     *
     * @param oneNodeNode 树节点
     * @param infos       每个节点对应的数据列表
     */
    protected void addNodeTreeToDetailEveryType(DefaultMutableTreeNode oneNodeNode, List<SchTaskOtherInfo> infos) {
        for (SchTaskOtherInfo item : infos) {
            // 过滤掉配置多节点信息面板展示的 节点id 和节点名称 信息 （公用一个list）
            if (EditPanelTypeEnum.J_LABEL.value().equals(item.getFieldType())) {
                continue;
            }
            String nodeValueStr = item.getFieldValue();
            nodeValueStr = StringUtils.isEmpty(nodeValueStr) ? "--" : nodeValueStr;
            String userObject = item.getFieldNameI18N() + "    " + nodeValueStr;
            oneNodeNode.add(new DefaultMutableTreeNode(userObject));
        }
    }

    /**
     * 不同的任务类型 （根据需要重写）
     * 生成各自多节点配置面板展示的参数
     *
     * @param configBean 待展示的数据
     * @return 生成的数据，直接用于多节点面板展示
     */
    protected List<SchTaskOtherInfo> genNodeParamList(NodeConfigBean configBean) {
        // 默认生公共的节点参数
        return genCommonNodeParamList(configBean);
    }

    /**
     * 公共的多节点配置参数
     * 包含 进程名/pid  或者  应用地址/应用路径
     *
     * @param configBean 待展示的数据
     * @return 生成的数据，直接用于多节点面板展示
     */
    @NotNull
    protected List<SchTaskOtherInfo> genCommonNodeParamList(NodeConfigBean configBean) {
        List<SchTaskOtherInfo> nodeParamList = new ArrayList<>();
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "nodeId",
                TaskManageContent.PARAM_NODE_ID, configBean.getNodeId() + "", false));
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "nodeName",
                TaskManageContent.PARAM_NODE_NAME, configBean.getNickName(), false));
        switch (currentAnalysisTarget) {
            case TaskManageContent.TARGET_APP_LAUNCH_APPLICATION:
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, appDirFieldName,
                        TaskManageContent.PARAM_APPLICATION_PATH, configBean.getTaskParam().getAppDir(),
                        "^/opt/[\\S]+"));
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, appParamFieldName,
                        TaskManageContent.PARAM_APPLICATION_PARAM, configBean.getTaskParam().getAppParameters(),
                        false));
                break;
            case TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS:
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, processFieldName,
                        TaskManageContent.PARAM_PROCESS_NAME, configBean.getTaskParam().getProcessName(), false));
                String pid = configBean.getTaskParam().getTargetPid();
                pid = pid == null ? configBean.getTaskParam().getPid() : pid;
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, pidFieldName,
                        TaskManageContent.PARAM_PID, pid, false));
                break;
            default:
                break;
        }
        return nodeParamList;
    }

    /**
     * 获取修改后的 预约任务信息，将新的值放入jsonObject中
     *
     * @param jsonObject 新的值
     */
    public void getNewVal(JSONObject jsonObject) {
        // 先从面板获取用户修改后的值
        getNewValueHandle();
        // 设置到 JSON 中（也对特殊进行处理）
        JSONObject json = getPutJsonObjEveryType(jsonObject);
        setBasicInfoToJson(json);
        // 设置多节点任务参数（也对特殊进行处理）
        setMultiNodeParaToJson(jsonObject);
    }

    /**
     * 针对特殊类型接口数据结构不一致，获取不同的JSON对象。默认为传入参数。
     *
     * @param jsonObject 新的值
     * @return 新的值存储对象（各类型）
     */
    protected JSONObject getPutJsonObjEveryType(JSONObject jsonObject) {
        return jsonObject;
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
    }

    /**
     * 将信息列表中的参数添加到面板
     * 若为 update 类型，则根据数据类型添加可编辑的面板
     * 否则添加为 不可编辑的面板
     */
    protected void addHandle() {
        for (SchTaskOtherInfo item : paramList) {
            String fieldType = item.getFieldType(); // 字段类型
            String fieldNameI18N = item.getFieldNameI18N(); // 国际化的值
            String fieldValue = item.getFieldValue(); // 字段值
            String fieldName = item.getFieldName(); // 字段名称
            List<String> showList = item.getShowList();
            List<String> selected = item.getSelected();
            if (isEdit) {
                Optional<JPanel> jPanelOptional = EditPanelTypeEnum.getType(fieldType)
                        .getItemPanel(fieldNameI18N, fieldValue, showList, selected);
                if (jPanelOptional.isPresent()) {
                    otherInfoPanel.add(jPanelOptional.get());
                    compIndexMap.put(fieldName, compIndex++);
                }
            } else {
                Optional<JPanel> jLabelPanelOptional = EditPanelTypeEnum.J_LABEL
                        .getItemPanel(fieldNameI18N, fieldValue, showList, selected);
                jLabelPanelOptional.ifPresent(jPanel -> otherInfoPanel.add(jPanel));
            }
        }
        // 添加多节点
        if (isMultiNode) {
            if (isEdit) {
                // 添加可编辑的多节点
                addNodeConfigPanel();
            }
        }
    }

    /**
     * String 转int
     *
     * @param numberStr String
     * @return int
     */
    protected int parseString2Int(String numberStr) {
        int numberInt = 0;
        try {
            numberInt = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            Logger.info("number string  format exception：");
        }
        return numberInt;
    }

    /**
     * 将 配置节点参数面板 添加到 otherInfoPanel ，并添加监听
     */
    protected void addNodeConfigPanel() {
        // 单节点任务不展示相关Panel
        if (multiNodePanelList.size() < 2) {
            return;
        }
        boolean switchResponse = schTaskItem.getTaskInfo().getSwitchResponse();
        JPanel switchResponsePanel =
                OtherInfoPanelUtil.getRadioBtnPanel(TaskManageContent.PARAM_CONFIG_NODE_PARAM, switchResponse);
        // 添加多节点参数配置开关
        otherInfoPanel.add(switchResponsePanel);
        compIndexMap.put("switchResponse", compIndex++);
        // 将待配置节点列表添加到otherInfoPanel中 设置是否显示
        for (JPanel nodeConfigItemPanel : multiNodePanelList) {
            Component nodeBtnComp = nodeConfigItemPanel.getComponent(3);
            if (nodeBtnComp instanceof JButton) {
                JButton nodeBtn = (JButton) nodeBtnComp;
                switchBtnValid = switchResponse;
                nodeBtn.setEnabled(switchResponse);
            }
            otherInfoPanel.add(nodeConfigItemPanel);
        }
        addNodeListener(switchResponsePanel, multiNodePanelList);
    }

    /**
     * 添加监听
     *
     * @param switchResponsePanel 多节点配置面板 （被监听）
     * @param nodeConfigPanelList 节点面板列表
     */
    protected void addNodeListener(JPanel switchResponsePanel, List<JPanel> nodeConfigPanelList) {
        // 添加监听
        Component component2 = switchResponsePanel.getComponent(1);
        componentMouseEvent(nodeConfigPanelList, component2, true);
        Component component3 = switchResponsePanel.getComponent(2);
        componentMouseEvent(nodeConfigPanelList, component3, false);
    }

    private void componentMouseEvent(List<JPanel> nodeConfigPanelList, Component component2, boolean b) {
        if (component2 instanceof JRadioButton) {
            JRadioButton jRadioButton = (JRadioButton) component2;
            jRadioButton.addMouseListener(
                    new MouseAdapter() {
                        /**
                         * 鼠标点击事件 #447ff5
                         *
                         *  @param event 事件
                         */
                        @Override
                        public void mouseClicked(MouseEvent event) {
                            mouseClickHandle(nodeConfigPanelList, b);
                        }
                    });
        }
    }

    private void mouseClickHandle(List<JPanel> nodeConfigPanelList, boolean b) {
        for (JPanel nodeConfigItemPanel : nodeConfigPanelList) {
            Component nodeBtnComp = nodeConfigItemPanel.getComponent(3);
            if (nodeBtnComp instanceof JButton) {
                JButton nodeBtn = (JButton) nodeBtnComp;
                nodeBtn.setEnabled(b);
                switchBtnValid = b;
            }
        }
    }

    /**
     * 获取单个节点panel
     * 展示节点ip，昵称，是否已配置，和配置按钮，添加监听时事件，弹窗修改
     *
     * @param nodeParam nodeParam
     * @return JPanel
     */
    protected JPanel getNodeItemPanel(MultiNodeParam nodeParam) {
        JPanel panel = OtherInfoPanelUtil.getBasicPanel(nodeParam.getNodeNameStr(), 8);
        // 0设置节点名称 label 大小
        panel.getComponent(0).setPreferredSize(new Dimension(80, 25));

        // 1节点IP Label
        String nodeIp = nodeParam.getNodeIPStr() == null ? nodeParam.getNodeNameStr() : nodeParam.getNodeIPStr();
        JLabel nodeIpLabel = new JLabel(nodeIp);
        nodeIpLabel.setPreferredSize(new Dimension(80, 25));
        panel.add(nodeIpLabel);

        // 2节点状态label （是否已配置）
        String nodeStat = TaskManageContent.PARAM_CONFIG_NODE_PARAM_STATUS_FALSE;
        if (nodeParam.isNodeStatus()) {
            nodeStat = TaskManageContent.PARAM_CONFIG_NODE_PARAM_STATUS_TRUE;
        }
        JLabel nodeStatusLabel = new JLabel(nodeStat);
        nodeStatusLabel.setPreferredSize(new Dimension(95, 25));
        panel.add(nodeStatusLabel);

        // 3配置按钮
        JButton configBtn = new JButton(TaskManageContent.PARAM_CONFIG_NODE_PARAM_CONFIG);
        panel.add(configBtn);

        // 待修改的参数
        nodeParamMap.put(nodeParam.getNodeId(), nodeParam.getNodeParamList());
        // 按钮添加监听，打开新的panel
        configBtn.addMouseListener(new MouseAdapter() {
            /**
             * 鼠标点击事件 #447ff5
             *
             *  @param event 事件
             */
            @Override
            public void mouseClicked(MouseEvent event) {
                if (switchBtnValid) {
                    List<SchTaskOtherInfo> nodeParamList = nodeParamMap.get(nodeParam.getNodeId());
                    SchTaskUpdateNodePanel updateNodePanel = new SchTaskUpdateNodePanel(
                            "SchTaskUpdateNodePanel", "displayName", nodeParamList);
                    SchTaskUpdateNodeDialog dialog =
                            new SchTaskUpdateNodeDialog(TaskManageContent.PARAM_CONFIG_NODE_PARAM, updateNodePanel);
                    dialog.displayPanel();
                    nodeParamMap.put(nodeParam.getNodeId(), nodeParamList);
                    if (updateNodePanel.isSaveNewVal()) {
                        nodeStatusLabel.setText(TaskManageContent.PARAM_CONFIG_NODE_PARAM_STATUS_TRUE);
                    }
                }
            }
        });
        return panel;
    }

    /**
     * 获取新的值 bool 类型转 enable/disable
     *
     * @param bool bool类型
     * @return enable/disable
     */
    protected String bool2AbleString(boolean bool) {
        if (bool) {
            return "enable";
        } else {
            return "disable";
        }
    }


    /**
     * 获取新的值 bool 类型转 enable/disable
     *
     * @param ableStr String 类型
     * @return enable/disable
     */
    protected Boolean ableStr2Bool(String ableStr) {
        if ("enable".equals(ableStr)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 展示时获取国际化
     *
     * @param value 待展示的字段
     * @return 字段国际化之后的值
     */
    protected String getI18N(String value) {
        String result = fileValueI18NMap.get(value);
        if (result != null) {
            return result;
        }
        return value;
    }

    /**
     * 调用接口时，将国际化的值转化为接口需要的值（逆国际化），若map中没有对应的测返回原本的值
     *
     * @param value 待展示的字段
     * @return 逆国际化之后的值
     */
    protected String getN81I(String value) {
        String result = selectN81IMap.get(value);
        if (result != null) {
            return result;
        }
        return value;
    }

    /**
     * 从 JSpinner 组件获取数字
     *
     * @param component 包含 JSpinner 的面板组件
     * @return 获取的数字
     */
    protected Integer getValueFromJSpinner(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component component2 = panel.getComponent(NEW_VALUE_COMP_INDEX);
            if (component2 instanceof JSpinner) {
                JSpinner jSpinner = (JSpinner) component2;
                Object newValueObj = jSpinner.getValue();
                if (newValueObj instanceof Integer) {
                    return (Integer) newValueObj;
                }
            }
        }
        return 0;
    }

    /**
     * 从单选框面板获取修改后的值
     *
     * @param component 包含单选框 的Panel
     * @return 修改后的 bool类型 值
     */
    protected boolean getValueFromJRadioBtn(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component component2 = panel.getComponent(NEW_VALUE_COMP_INDEX);
            if (component2 instanceof JRadioButton) {
                JRadioButton jRadioButton = (JRadioButton) component2;
                return jRadioButton.isSelected();
            }
        }
        return false;
    }

    /**
     * 从 下拉单选框 面板获取修改后的值
     *
     * @param component 包含单选框 的Panel
     * @return 修改后的值
     */
    protected String getValueFromJComboBox(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component component2 = panel.getComponent(NEW_VALUE_COMP_INDEX);
            if (component2 instanceof JComboBox) {
                JComboBox jComboBox = (JComboBox) component2;
                Object newValueObj = jComboBox.getSelectedItem();
                if (newValueObj instanceof String) {
                    String newValueStr = (String) newValueObj;
                    String newValueStrI18N = selectN81IMap.get(newValueStr);
                    if (newValueStrI18N != null) {
                        return newValueStrI18N;
                    } else {
                        // 部分特殊映射 应单独处理
                        Logger.info("JComboBox select value format failed, please check the hashmap");
                        return newValueStr;
                    }
                }
            }
        }
        return "";
    }

    /**
     * 从 JLabel组件 中获取值
     *
     * @param component 包含目标组件的Panel
     * @param compIndex 目标组件在Panel的索引
     * @return 获取的值
     */
    protected String getValueFromJLabel(Component component, int compIndex) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component indexComp = panel.getComponent(compIndex);
            if (indexComp instanceof JLabel) {
                return ((JLabel) indexComp).getText();
            }
        }
        return "";
    }

    /**
     * 从 otherInfoPanel 中编辑框中获取 用户输入/选择的新数据，存入 paramList 中
     */
    protected void getNewValueHandle() {
        for (SchTaskOtherInfo infoItem : paramList) {
            String fieldName = infoItem.getFieldName();
            int compIdx = compIndexMap.get(fieldName);
            String fieldType = infoItem.getFieldType();
            Component component = otherInfoPanel.getComponent(compIdx);
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component component2 = panel.getComponent(1);
                Optional<String> newValOption = EditPanelTypeEnum.getType(fieldType).getNewValFormComp(component2);
                newValOption.ifPresent(infoItem::setFieldValue);
            }
        }
    }

    /**
     * 设置 应用 相关信息 和 普通信息
     *
     * @param jsonObject jsonObject
     */
    protected void setBasicInfoToJson(JSONObject jsonObject) {
        jsonObject.put(pidFieldName, newPid);
        jsonObject.put(processFieldName, newProcessName);
        jsonObject.put(appDirFieldName, newAppDir);
        jsonObject.put(appParamFieldName, newAppParam);
        for (SchTaskOtherInfo infoItem : paramList) {
            putInfoToJson(jsonObject, infoItem);
        }
        // 对特殊的进行处理
        specialValueHandle(jsonObject);
    }

    /**
     * 将 otherInfo 面板中用户输入的参数 设置到多节点Json中
     *
     * @param jsonObject jsonObject
     */
    protected void setMultiNodeParaToJson(JSONObject jsonObject) {
        boolean switchResponse = false;
        JSONArray nodeConfigJsonArr = jsonObject.getJSONArray("nodeConfig");
        // 多节点任务 且 该任务可以配置多节点 则去获取用户是否选择配置多节点参数
        if (nodeConfigJsonArr.size() > 1 && isMultiNode) {
            // 根据panel 获取用户的选择-是否配置多节点参数
            switchResponse = getValueFromJRadioBtn(otherInfoPanel.getComponent(compIndexMap.get("switchResponse")));
        }
        jsonObject.put("switch", switchResponse);
        for (int i = 0; i < nodeConfigJsonArr.size(); i++) {
            JSONObject nodeConfigItem = nodeConfigJsonArr.getJSONObject(i);
            // 为解决Miss 任务类型的请求参数结构不同，相较于其他任务类型，多嵌套一层 task_param ，但是代表对象状态的 status 位于相同层级
            JSONObject taskParamObj = getTaskParam(nodeConfigItem);
            taskParamObj.put(pidFieldName, newPid);
            taskParamObj.put(processFieldName, newProcessName);
            taskParamObj.put(appDirFieldName, newAppDir);
            taskParamObj.put(appParamFieldName, newAppParam);
            // 其他多节点信息
            for (SchTaskOtherInfo infoItem : paramList) {
                putInfoToJson(taskParamObj, infoItem);
            }
            specialValueHandle(taskParamObj);
            if (switchResponse) {
                // 将弹出的多节点配置面板中修改后参数，添加 到 JSON 中
                Integer switchResponseIndex = compIndexMap.get("switchResponse");
                Component nodeItem = otherInfoPanel.getComponent(switchResponseIndex + 1 + i);
                if (nodeItem instanceof JPanel) {
                    JPanel nodeItemPanel = (JPanel) nodeItem;
                    String status = getValueFromJLabel(nodeItemPanel, NEW_NODE_STATUS_COMP_INDEX);
                    if (TaskManageContent.PARAM_CONFIG_NODE_PARAM_STATUS_TRUE.equals(status)) {
                        List<SchTaskOtherInfo> nodeParamList = nodeParamMap.get(nodeConfigItem.getInteger("nodeId"));
                        setSingleNodeJsonArrItem(taskParamObj, nodeParamList);
                        // 打开多节点配置时，将节点状态设置为 true
                        taskParamObj.replace("status", true);
                        // miss类型特殊处理
                        setMissNodeStatus(nodeConfigItem, true);
                        // 多节点特殊值处理
                        specialNodeValueHandle(taskParamObj);
                    }
                }
            } else {
                // 关闭多节点配置时，将节点状态设置为 false
                taskParamObj.replace("status", false);
                // miss类型特殊处理
                setMissNodeStatus(nodeConfigItem, false);
            }
        }
    }

    private void setMissNodeStatus(JSONObject nodeConfigItem, boolean nodeStatus) {
        JSONObject missNodeStatus = nodeConfigItem.getJSONObject("task_param");
        if (missNodeStatus != null) {
            missNodeStatus.replace("status", nodeStatus);
        }
    }

    /**
     * 各类型继承，获取不同的对象
     *
     * @param nodeConfigItem nodeConfigItem
     * @return task_param 对象
     */
    protected JSONObject getTaskParam(JSONObject nodeConfigItem) {
        return nodeConfigItem.getJSONObject("task_param");
    }

    /**
     * 将 多节点参数修改面板 中该用户输入的参数 设置到 JSON 对象中
     *
     * @param taskParamObj  目标Json对象
     * @param nodeParamList 面板返回的值
     */
    protected void setSingleNodeJsonArrItem(JSONObject taskParamObj, List<SchTaskOtherInfo> nodeParamList) {
        for (SchTaskOtherInfo item : nodeParamList) {
            taskParamObj.replace(item.getFieldName(), item.getFieldValue());
        }
    }

    /**
     * 将单个信息添加到到json对象中
     *
     * @param jsonObject jsonObject
     * @param infoItem   infoItem
     */
    protected void putInfoToJson(JSONObject jsonObject, SchTaskOtherInfo infoItem) {
        String fieldName = infoItem.getFieldName();
        String fieldValue = infoItem.getFieldValue();
        String fieldType = infoItem.getFieldType();
        switch (EditPanelTypeEnum.getType(fieldType)) {
            case J_SPINNER:
                jsonObject.put(fieldName, Integer.parseInt(fieldValue));
                break;
            case RADIO_BTN:
                Boolean booleValue;
                if (fieldValue != null
                        && ("true".equals(fieldValue) || fieldValue.equals(TaskManageContent.PARAM_TRUE))) {
                    booleValue = Boolean.TRUE;
                } else {
                    booleValue = Boolean.FALSE;
                }
                jsonObject.put(fieldName, booleValue);
                break;
            case MULTI_CHECK_BOX:
            case J_COMBO_BOX:
                fieldValue = getN81I(fieldValue);
                jsonObject.put(fieldName, fieldValue);
                break;
            default:
                jsonObject.put(fieldName, fieldValue);
                break;
        }
    }

    /**
     * 特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
    }

    /**
     * 多节点特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialNodeValueHandle(JSONObject jsonObj) {
    }

    /**
     * 获取输入框的值，对输入的值进行校验。若超过最大最小值。则报错提示。
     *
     * @param compIndex 组件在other Panel中的位置
     * @param min       最小值
     * @param max       最大值 int类型
     * @return ValidationInfo 参数错误提示 filter(Image bitmap) {
     * <p>
     * }
     */
    protected Optional<ValidationInfo> getIntegerValid(Integer compIndex, int min, int max) {
        if (compIndex == null || compIndex > otherInfoPanel.getComponentCount()) {
            return Optional.empty();
        }
        Component integerPanelComp = otherInfoPanel.getComponent(compIndex);
        int intValue = getValueFromJSpinner(integerPanelComp);
        if (integerPanelComp instanceof JPanel) {
            JPanel integerPanel = (JPanel) integerPanelComp;
            Component integerInputComp = integerPanel.getComponent(NEW_VALUE_COMP_INDEX);
            if (intValue > max || intValue < min) {
                if (integerInputComp instanceof JComponent) {
                    String noticeShow = MessageFormat.format(SchTaskContent.INPUT_PARAM_ERROR, min, max);
                    JComponent integerInput = (JComponent) integerInputComp;
                    return Optional.of(new ValidationInfo(noticeShow, integerInput));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 获取 自定义 输入框的值，对输入的值进行校验。
     * 若为 高精度类型 则跳过校验；
     * 若为 自定义类型 且 超过最大最小值。则报错提示。
     *
     * @param compIndex 组件在other Panel中的位置
     * @param min       最小值
     * @param max       最大值 int类型
     * @return ValidationInfo 参数错误提示 filter(Image bitmap) {
     * <p>
     * }
     */
    protected Optional<ValidationInfo> getIntegerCustomValid(Integer compIndex, int min, int max) {
        if (compIndex == null || compIndex > otherInfoPanel.getComponentCount()) {
            return Optional.empty();
        }
        Component integerPanelComp = otherInfoPanel.getComponent(compIndex);
        if (integerPanelComp instanceof JPanel) {
            JPanel integerPanel = (JPanel) integerPanelComp;
            // 高精度/自定义 选择组件
            Component jComboBoxComp = integerPanel.getComponent(NEW_VALUE_COMP_INDEX);
            if (!(jComboBoxComp instanceof JComboBox)) {
                return Optional.empty();
            }
            JComboBox jComboBox = (JComboBox) jComboBoxComp;
            Object jComboBoxVal = jComboBox.getSelectedItem();
            if (TaskManageContent.PARAM_INTERVAL_CUSTOM.equals(jComboBoxVal)) {
                Component integerInputComp = integerPanel.getComponent(NEW_VALUE_COMP_INDEX + 1);
                // 用户输入值的组件
                if (!(integerInputComp instanceof JSpinner)) {
                    return Optional.empty();
                }
                JSpinner jSpinner = (JSpinner) integerInputComp;
                Object jSpinnerValue = jSpinner.getValue();
                if (!(jSpinnerValue instanceof Integer)) {
                    return Optional.empty();
                }
                int intValue = (Integer) jSpinnerValue;
                if (intValue > max || intValue < min) {
                    // 范围错误
                    String noticeShow = MessageFormat.format(SchTaskContent.INPUT_PARAM_ERROR, min, max);
                    JComponent integerInput = (JComponent) integerInputComp;
                    return Optional.of(new ValidationInfo(noticeShow, integerInput));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 获取输入框的值，对输入的值进行校验。若超过最大最小值。则报错提示。
     *
     * @param compIndex 组件在other Panel中的位置
     * @param min       最小值
     * @param max       最大值 double类型
     * @return ValidationInfo 参数错误提示
     */
    protected Optional<ValidationInfo> getIntegerValid(Integer compIndex, int min, double max) {
        if (compIndex == null || compIndex > otherInfoPanel.getComponentCount()) {
            return Optional.empty();
        }
        Component integerPanelComp = otherInfoPanel.getComponent(compIndex);
        int intValue = getValueFromJSpinner(integerPanelComp);
        if (integerPanelComp instanceof JPanel) {
            JPanel integerPanel = (JPanel) integerPanelComp;
            Component integerInputComp = integerPanel.getComponent(NEW_VALUE_COMP_INDEX);
            if (intValue > max || intValue < min) {
                if (integerInputComp instanceof JComponent) {
                    JComponent integerInput = (JComponent) integerInputComp;
                    String noticeShow = MessageFormat.format(SchTaskContent.INPUT_PARAM_ERROR, min, max);
                    return Optional.of(new ValidationInfo(noticeShow, integerInput));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 获取 文本输入框 的值，根据对应的正则表达式对输入的值进行校验。
     * 主要校验对象有 C/C源文件路径；二进制文件路径；带采样cou核
     *
     * @param fieldName 字段名称
     * @param regex     正则1 （不可以为空）
     * @param regex2    正则2（带采样cpu核心需要，可以为空）
     * @param errMsg    错误提示信息
     * @return 参数错误提示（Optional类型）
     */
    protected Optional<ValidationInfo> getTextRegexValid(String fieldName, String regex, String regex2, String errMsg) {
        Integer componentIdx = compIndexMap.get(fieldName);
        if (componentIdx == null || componentIdx > otherInfoPanel.getComponentCount() || StringUtils.isEmpty(regex)) {
            return Optional.empty();
        }
        Component panelComp = otherInfoPanel.getComponent(componentIdx);
        if (!(panelComp instanceof JPanel)) {
            return Optional.empty();
        }
        JPanel jPanel = (JPanel) panelComp;
        Component textComp = jPanel.getComponent(NEW_VALUE_COMP_INDEX);
        if (!(textComp instanceof JTextField)) {
            return Optional.empty();
        }
        JTextField jTextField = (JTextField) textComp;
        String inputValStr = jTextField.getText();
        // 仅为修改场景，无需做必填校验，为空则跳过
        if (StringUtils.isEmpty(inputValStr)) {
            return Optional.empty();
        }
        // 若符合正则则跳过
        if (StringUtils.isEmpty(regex2)) {
            return inputValStr.matches(regex)
                    ? Optional.empty()
                    : Optional.of(new ValidationInfo(errMsg, jTextField));
        } else {
            return inputValStr.matches(regex) && !inputValStr.matches(regex2)
                    ? Optional.empty()
                    : Optional.of(new ValidationInfo(errMsg, jTextField));
        }
    }

    public void setNewProcessName(String newProcessName) {
        this.newProcessName = newProcessName;
    }

    public void setNewPid(String newPid) {
        this.newPid = newPid;
    }

    public void setNewAppDir(String newAppDir) {
        this.newAppDir = newAppDir;
    }

    public void setNewAppParam(String newAppParam) {
        this.newAppParam = newAppParam;
    }

    /**
     * 每个任务类型设置分析目标字段名称
     *
     * @param pid         pid
     * @param processName 进程名
     * @param appDir      应用地址路径
     * @param appParam    应用参数
     */
    public void setAnalysisTargetInfo(String pid, String processName, String appDir, String appParam) {
        this.pidFieldName = pid;
        this.processFieldName = processName;
        this.appDirFieldName = appDir;
        this.appParamFieldName = appParam;
    }

    /**
     * 对参数列表中的参数进行用户输入校验
     *
     * @param result 校验结果
     */
    public void doCommonValidateOfParamList(List<ValidationInfo> result) {
        for (SchTaskOtherInfo infoItem : paramList) {
            int cmpIdx = compIndexMap.get(infoItem.getFieldName());
            if (infoItem.getFieldType().equals(EditPanelTypeEnum.TEXT_FIELD.value())) {
                Component component = otherInfoPanel.getComponent(cmpIdx);
                compInputValVerify(result, infoItem, component);
            }
        }
    }

    private void compInputValVerify(List<ValidationInfo> result, SchTaskOtherInfo infoItem, Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component component2 = panel.getComponent(1);
            if (component2 instanceof JTextField) {
                String itemVerifyRegex = infoItem.getVerifyRegex();
                JTextField textField = (JTextField) component2;
                // 校验正则不为空 且输入值不为空
                if (!StringUtils.isEmpty(itemVerifyRegex) && !StringUtils.isEmpty(textField.getText())) {
                    if (!textField.getText().matches(itemVerifyRegex)) {
                        result.add(new ValidationInfo(SchTaskContent.INPUT_PARAM_INCORRECT_FORMAT, textField));
                    }
                }
            }
        }
    }
}

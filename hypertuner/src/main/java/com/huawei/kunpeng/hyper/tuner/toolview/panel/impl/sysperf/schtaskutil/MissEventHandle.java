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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskCommonFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.EditPanelTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.MultiNodeParam;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskOtherInfo;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 分析类型 Miss 事件
 *
 * @since 2021-8-26
 */
public class MissEventHandle extends SchTaskPanelHandle {
    /**
     * Miss 事件 构造函数
     *
     * @param otherInfoPanel  目标面板
     * @param taskInfoJsonObj 带展示的预约任务 （JSON格式）
     * @param compIndexMap    组件索引Map
     */
    public MissEventHandle(JPanel otherInfoPanel, JSONObject taskInfoJsonObj, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, taskInfoJsonObj, compIndexMap);
        String target = taskInfoJsonObj.getJSONObject("task_param").getString("target");
        seMissAnalysiTarget(target);
        setAnalysisTargetInfo("pid", "process_name", "app", "appArgs");
    }

    void seMissAnalysiTarget(String target) {
        if ("app".equals(target)) {
            currentAnalysisTarget = TaskManageContent.TARGET_APP_LAUNCH_APPLICATION;
        } else if ("pid".equals(target)) {
            currentAnalysisTarget = TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS;
        } else {
            // "sys".equals(target) 时 分析对象为系统
            currentAnalysisTarget = TaskManageContent.TARGET_APP_PROFILE_SYSTEM;
        }
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public MissEventHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        String target = templateItem.getTaskParam().getTarget();
        seMissAnalysiTarget(target);
        generateTempItemShowList();
    }

    /**
     * 将其他信息添加到OtherInfoPanel中
     */
    public void generateSchItemShowList() {
        // 0采样时长 (秒) duration
        JSONObject taskParamJsonObj = taskInfoJsonObj.getJSONObject("task_param");
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, taskParamJsonObj.getInteger("duration") + "", false));
        // 1采样间隔 (指令数)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "period",
                TaskManageContent.PARAM_PERIOD, taskParamJsonObj.getString("period"), false));
        // 2延迟采样时长 (毫秒)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "startDelay",
                TaskManageContent.PARAM_SAMPLING_DELAY, taskParamJsonObj.getInteger("startDelay") + "", false));
        // 3指标类型
        String[] metricsListData = new String[]{
                TaskManageContent.PARAM_MEM_MISS_METRICS_LLC,
                TaskManageContent.PARAM_MEM_MISS_METRICS_TLB,
                TaskManageContent.PARAM_MEM_MISS_METRICS_REMOTE_ACCESS,
                TaskManageContent.PARAM_MEM_MISS_METRICS_LONG_LATENCY_LOAD};
        List<String> metricsSelect = new ArrayList<>();
        metricsSelect.add(getI18N(taskParamJsonObj.getString("metrics")));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "metrics",
                TaskManageContent.PARAM_ANALYSIS_METRICS,
                Arrays.asList(metricsListData), metricsSelect));
        // 4待采样CPU核
        if ("sys".equals(taskParamJsonObj.getString("target"))) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpu",
                    TaskManageContent.PARAM_CPU_MASK, taskParamJsonObj.getString("cpu"), false));
        }
        // 5采样范围
        String[] spaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL};
        List<String> spaceSelected = new ArrayList<>();
        spaceSelected.add(getI18N(taskParamJsonObj.getString("space")));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "space",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(spaceListData), spaceSelected));
        // 6C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "srcDir",
                TaskManageContent.PARAM_SOURCE_LOCATION, taskParamJsonObj.getString("srcDir"), false));
        // 7内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(taskParamJsonObj.getString("kcore")), false));
        // 8采集文件大小 (Mb)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "perfDataLimit",
                TaskManageContent.PARAM_FILE_SIZE, taskParamJsonObj.getInteger("perfDataLimit") + "", false));
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        // 0采样时长 (秒) duration
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, templateItem.getTaskParam().getDuration().toString(), false));
        // 1采样间隔 (指令数)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "period",
                TaskManageContent.PARAM_PERIOD, templateItem.getTaskParam().getPeriod(), false));
        // 2延迟采样时长 (毫秒)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "startDelay",
                TaskManageContent.PARAM_SAMPLING_DELAY, templateItem.getTaskParam().getStartDelay() + "", false));
        // 3指标类型
        String[] metricsListData = new String[]{
                TaskManageContent.PARAM_MEM_MISS_METRICS_LLC,
                TaskManageContent.PARAM_MEM_MISS_METRICS_TLB,
                TaskManageContent.PARAM_MEM_MISS_METRICS_REMOTE_ACCESS,
                TaskManageContent.PARAM_MEM_MISS_METRICS_LONG_LATENCY_LOAD};
        List<String> metricsSelect = new ArrayList<>();
        metricsSelect.add(getI18N(templateItem.getTaskParam().getMetrics()));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "metrics",
                TaskManageContent.PARAM_ANALYSIS_METRICS,
                Arrays.asList(metricsListData), metricsSelect));
        // 4待采样CPU核
        if ("sys".equals(templateItem.getTaskParam().getTarget())) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpu",
                    TaskManageContent.PARAM_CPU_MASK, templateItem.getTaskParam().getCpu(), false));
        }
        // 5采样范围
        String[] spaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL};
        List<String> spaceSelected = new ArrayList<>();
        spaceSelected.add(getI18N(templateItem.getTaskParam().getSpace()));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "space",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(spaceListData), spaceSelected));
        // 6C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "srcDir",
                TaskManageContent.PARAM_SOURCE_LOCATION, templateItem.getTaskParam().getSrcDir(), false));
        // 7内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(templateItem.getTaskParam().getKcore()), false));
        // 8采集文件大小 (Mb)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "perfDataLimit",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getTaskParam().getPerfDataLimit(), false));
        generateMultiNodeInfoShowListOfTemp();
    }

    /**
     * Miss 任务类型
     * 生成各自多节点配置面板展示的参数
     *
     * @param configBean 待展示的数据
     * @return 生成的数据，直接用于多节点面板展示
     */
    protected List<SchTaskOtherInfo> genNodeParamList(NodeConfigBean configBean) {
        // 默认生公共的节点参数
        List<SchTaskOtherInfo> nodeParamList = new ArrayList<>();
        String taskParamStr = configBean.getTaskParam().getTaskParam();
        JSONObject taskParam2Obj = JSONObject.parseObject(taskParamStr);
        getMissTemplateDetailNodeList(nodeParamList, taskParam2Obj);
        return nodeParamList;
    }

    private void getMissTemplateDetailNodeList(List<SchTaskOtherInfo> nodeParamList, JSONObject taskParam2Obj) {
        switch (currentAnalysisTarget) {
            case TaskManageContent.TARGET_APP_LAUNCH_APPLICATION: // 应用Launch
                // 应用路径
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, appDirFieldName,
                        TaskManageContent.PARAM_APPLICATION_PATH, taskParam2Obj.getString("app"), false));
                // 应用参数
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, appParamFieldName,
                        TaskManageContent.PARAM_APPLICATION_PARAM, taskParam2Obj.getString("appArgs"), false));
                // c/c++源文件路径
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "srcDir",
                        TaskManageContent.PARAM_SOURCE_LOCATION, taskParam2Obj.getString("srcDir"), false));
                break;
            case TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS: // 应用Attach
                // 进程名
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, processFieldName,
                        TaskManageContent.PARAM_PROCESS_NAME, taskParam2Obj.getString("process_name"), false));
                // PID
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, pidFieldName,
                        TaskManageContent.PARAM_PID, taskParam2Obj.getString("pid"), false));
                break;
            default: // 系统
                // 带采样cpu核
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpu",
                        TaskManageContent.PARAM_CPU_MASK, taskParam2Obj.getString("cpu"), false));
                // c/c++源文件路径
                nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "srcDir",
                        TaskManageContent.PARAM_SOURCE_LOCATION, taskParam2Obj.getString("srcDir"), false));
                break;
        }
    }

    /**
     * 获取单个分析任务 多节点配置相关面板列表
     */
    public void generateMultiNodeInfoShowList() {
        // 配置节点参数
        JSONArray nodeConfigJsonArr = taskInfoJsonObj.getJSONArray("nodeConfig");
        for (int i = 0; i < nodeConfigJsonArr.size(); i++) {
            JSONObject nodeConfig = nodeConfigJsonArr.getJSONObject(i);
            JSONObject taskParamObj = nodeConfig.getJSONObject("task_param");
            int nodeId = nodeConfig.getInteger("nodeId");
            String nickName = nodeConfig.getString("nickName");
            boolean nodeStatus = taskParamObj.getBoolean("status");
            JSONObject taskParam2Obj = taskParamObj.getJSONObject("task_param");
            MultiNodeParam nodeParam = new MultiNodeParam(nodeId, nickName, nickName, nodeStatus);

            // 获取多节点配置面板的展示的待修改参数
            List<SchTaskOtherInfo> nodeParamList = new ArrayList<>();
            nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "nodeId",
                    TaskManageContent.PARAM_NODE_ID, nodeId + "", false));
            nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "nodeName",
                    TaskManageContent.PARAM_NODE_NAME, nickName, false));
            getMissTemplateDetailNodeList(nodeParamList, taskParam2Obj);
            nodeParam.setNodeParamList(nodeParamList);
            nodeParamMap.put(nodeParam.getNodeId(), nodeParamList);
            JPanel nodeConfigItemPanel = getNodeItemPanel(nodeParam);
            multiNodePanelList.add(nodeConfigItemPanel);
        }
    }

    /**
     * Miss事件 任务特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
        // 进行特殊处理
        for (SchTaskOtherInfo infoItem : paramList) {
            if ("space".equals(infoItem.getFieldName())) {
                String spaceN81I = TaskCommonFormatUtil.getSelectMapOf3().get(infoItem.getFieldValue());
                jsonObj.put(infoItem.getFieldName(), spaceN81I);
            }
        }
    }

    /**
     * 各类型继承，获取不同的对象
     *
     * @param nodeConfigItem nodeConfigItem
     * @return task_param 对象
     */
    protected JSONObject getTaskParam(JSONObject nodeConfigItem) {
        return nodeConfigItem.getJSONObject("task_param").getJSONObject("task_param");
    }

    /**
     * 针对特殊类型接口数据结构不一致，获取不同的JSON对象。默认为传入参数。
     *
     * @param jsonObject 新的值
     * @return 新的值存储对象（依各类型）
     */
    protected JSONObject getPutJsonObjEveryType(JSONObject jsonObject) {
        return jsonObject.getJSONObject("task_param");
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
        Integer durationIndex = compIndexMap.get("duration");
        getIntegerValid(durationIndex, 1, 300).ifPresent(result::add);
        // period
        Integer periodIndex = compIndexMap.get("period");
        double max = Math.pow(2, 32) - 1;
        getIntegerValid(periodIndex, 1024, max).ifPresent(result::add);
        // startDelay
        Integer startDelayIndex = compIndexMap.get("startDelay");
        getIntegerValid(startDelayIndex, 0, 900000).ifPresent(result::add);
        // perfDataLimit
        Integer perfDataLimitIndex = compIndexMap.get("perfDataLimit");
        getIntegerValid(perfDataLimitIndex, 1, 10000).ifPresent(result::add);
        // C/C++源文件路径
        getTextRegexValid("srcDir", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // 待采样CPU核 只有系统类型存在
        getTextRegexValid("cpu",
                TaskManageContent.PARAM_CPU_MASK_REGEX, TaskManageContent.PARAM_CPU_MASK_REGEX2,
                SchTaskContent.INPUT_PARAM_ERROR_CPU).ifPresent(result::add);
    }
}

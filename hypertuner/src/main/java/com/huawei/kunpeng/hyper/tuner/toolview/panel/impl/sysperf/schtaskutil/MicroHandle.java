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
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskInfoBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.EditPanelTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskOtherInfo;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 微架构 分析类型
 *
 * @since 2021-8-26
 */
public class MicroHandle extends SchTaskPanelHandle {
    /**
     * 使用 SchTaskBean 类型的预约任务信息初始化
     *
     * @param otherInfoPanel 被添加面板
     * @param schTaskItem    待修改预约任务对象
     * @param compIndexMap   组件索引Map
     */
    public MicroHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("targetPid", "process_name", "appDir", "appParameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public MicroHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("targetPid", "process_name", "appDir", "appParameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        // 0采样模式
        List<String> samplingModeListData = new ArrayList<>();
        samplingModeListData.add(TaskManageContent.PARAM_SAMPLING_MODE_SUMMARY);
        samplingModeListData.add(TaskManageContent.PARAM_SAMPLING_MODE_DETAIL);
        String samplingModeI18N = "summary".equals(taskInfo.getSamplingMode()) // detail /summary
                ? TaskManageContent.PARAM_SAMPLING_MODE_SUMMARY
                : TaskManageContent.PARAM_SAMPLING_MODE_DETAIL;
        List<String> samplingModeSelect = new ArrayList<>();
        samplingModeSelect.add(samplingModeI18N);
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingMode",
                TaskManageContent.PARAM_SAMPLING_MODE, samplingModeListData, samplingModeSelect));
        // 1采样时长 duration
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, taskInfo.getDuration() + "", false));
        // 2采样间隔  interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, taskInfo.getInterval(), false));
        // 3分析指标
        String[] listData = new String[]{
                TaskManageContent.PARAM_ANALYSIS_INDEX_BAD,
                TaskManageContent.PARAM_ANALYSIS_INDEX_FRONT,
                TaskManageContent.PARAM_ANALYSIS_INDEX_RESOURCE,
                TaskManageContent.PARAM_ANALYSIS_INDEX_CORE,
                TaskManageContent.PARAM_ANALYSIS_INDEX_MEMORY};
        List<String> analysisIndexSelected = new ArrayList<>();
        analysisIndexSelected.add(getI18N(schTaskItem.getTaskInfo().getAnalysisIndex()));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "analysisIndex",
                TaskManageContent.PARAM_ANALYSIS_INDEX, Arrays.asList(listData), analysisIndexSelected));
        // 内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(schTaskItem.getTaskInfo().getKcore() + ""), false));
        // 5采样范围
        List<String> samplingSpaceSelected = new ArrayList<>();
        samplingSpaceSelected.add(getI18N(taskInfo.getSamplingSpace()));
        String[] samplingSpaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL};
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingSpace",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(samplingSpaceListData), samplingSpaceSelected));
        // 6延迟采样时间
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "samplingDelay",
                TaskManageContent.PARAM_SAMPLING_DELAY, taskInfo.getSamplingDelay(), false));
        // C/C++ 源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, taskInfo.getSourceLocation(), false));
        // 采集文件大小
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "perfDataLimit",
                TaskManageContent.PARAM_FILE_SIZE, taskInfo.getPerfDataLimit() + "", false));
        // 待采样CPU核
        if ("Profile System".equals(taskInfo.getAnalysisTarget())) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpuMask",
                    TaskManageContent.PARAM_CPU_MASK, taskInfo.getCpuMask(), false));
        }
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        // 0采样模式
        List<String> samplingModeListData = new ArrayList<>();
        samplingModeListData.add(TaskManageContent.PARAM_SAMPLING_MODE_SUMMARY);
        samplingModeListData.add(TaskManageContent.PARAM_SAMPLING_MODE_DETAIL);
        String samplingModeI18N = "summary".equals(templateItem.getSamplingMode()) // detail /summary
                ? TaskManageContent.PARAM_SAMPLING_MODE_SUMMARY
                : TaskManageContent.PARAM_SAMPLING_MODE_DETAIL;
        List<String> samplingModeSelect = new ArrayList<>();
        samplingModeSelect.add(samplingModeI18N);
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingMode",
                TaskManageContent.PARAM_SAMPLING_MODE, samplingModeListData, samplingModeSelect));
        // 1采样时长 duration
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, templateItem.getDuration() + "", false));
        // 2采样间隔  interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, templateItem.getInterval(), false));
        // 3分析指标
        String[] listData = new String[]{
                TaskManageContent.PARAM_ANALYSIS_INDEX_BAD,
                TaskManageContent.PARAM_ANALYSIS_INDEX_FRONT,
                TaskManageContent.PARAM_ANALYSIS_INDEX_RESOURCE,
                TaskManageContent.PARAM_ANALYSIS_INDEX_CORE,
                TaskManageContent.PARAM_ANALYSIS_INDEX_MEMORY};
        List<String> analysisIndexSelected = new ArrayList<>();
        analysisIndexSelected.add(getI18N(templateItem.getAnalysisIndex()));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "analysisIndex",
                TaskManageContent.PARAM_ANALYSIS_INDEX, Arrays.asList(listData), analysisIndexSelected));
        // 内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(templateItem.getKcore() + ""), false));
        // 5采样范围
        List<String> samplingSpaceSelected = new ArrayList<>();
        samplingSpaceSelected.add(getI18N(templateItem.getSamplingSpace()));
        String[] samplingSpaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL};
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingSpace",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(samplingSpaceListData), samplingSpaceSelected));
        // 6延迟采样时间
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "samplingDelay",
                TaskManageContent.PARAM_SAMPLING_DELAY, templateItem.getSamplingDelay(), false));
        // C/C++ 源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, templateItem.getSourceLocation(), false));
        // 采集文件大小
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "perfDataLimit",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getPerfDataLimit() + "", false));
        // 待采样CPU核
        if ("Profile System".equals(templateItem.getAnalysisTarget())) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpuMask",
                    TaskManageContent.PARAM_CPU_MASK, templateItem.getCpuMask(), false));
        }
        generateMultiNodeInfoShowListOfTemp();
    }

    /**
     * 生成多节点配置面板展示的参数
     *
     * @param configBean 待展示的数据
     * @return 生成的数据，直接用于多节点面板展示
     */
    protected List<SchTaskOtherInfo> genNodeParamList(NodeConfigBean configBean) {
        List<SchTaskOtherInfo> nodeParamList = genCommonNodeParamList(configBean);
        if ("Profile System".equals(currentAnalysisTarget)) {
            nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpuMask",
                    TaskManageContent.PARAM_CPU_MASK, configBean.getTaskParam().getCpuMask(), false));
        }
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, configBean.getTaskParam().getSourceLocation(), false));
        return nodeParamList;
    }

    /**
     * 各类型继承，获取不同的对象
     *
     * @param nodeConfigItem nodeConfigItem
     * @return task_param 对象
     */
    protected JSONObject getTaskParam(JSONObject nodeConfigItem) {
        JSONObject taskParamObj = nodeConfigItem.getJSONObject("taskParam");
        return taskParamObj;
    }

    /**
     * 特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
        // 进行特殊处理
        for (SchTaskOtherInfo infoItem : paramList) {
            String fieldName = infoItem.getFieldName();
            String fieldValue = infoItem.getFieldValue();
            if ("samplingSpace".equals(fieldName)) {
                // 采样范围
                String samplingSpaceN81I = TaskCommonFormatUtil.getSelectMapOf3().get(fieldValue);
                jsonObj.put(fieldName, samplingSpaceN81I);
            }
        }
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
        // 采样时长
        String samplingModeI18N = getValueFromJComboBox(otherInfoPanel.getComponent(compIndexMap.get("samplingMode")));
        int max = "summary".equals(samplingModeI18N)
                ? 999 : 30;
        Integer durationIndex = compIndexMap.get("duration");
        getIntegerValid(durationIndex, 1, max).ifPresent(result::add);
        // interval
        Integer intervalIndex = compIndexMap.get("interval");
        getIntegerValid(intervalIndex, 1, 999).ifPresent(result::add);

        // samplingDelay
        Integer samplingDelayIndex = compIndexMap.get("samplingDelay");
        getIntegerValid(samplingDelayIndex, 0, 999).ifPresent(result::add);
        // perfDataLimit
        Integer perfDataLimitIndex = compIndexMap.get("perfDataLimit");
        getIntegerValid(perfDataLimitIndex, 1, 1024).ifPresent(result::add);
        // C/C++源文件路径
        getTextRegexValid("sourceLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // 待采样CPU核 只有系统类型存在
        getTextRegexValid("cpuMask",
                TaskManageContent.PARAM_CPU_MASK_REGEX, TaskManageContent.PARAM_CPU_MASK_REGEX2,
                SchTaskContent.INPUT_PARAM_ERROR_CPU).ifPresent(result::add);
    }
}

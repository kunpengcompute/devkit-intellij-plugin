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
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 分析类型 伪共享分析
 *
 * @since 2021-8-26
 */
public class FalseHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public FalseHandle
    (JPanel otherInfoPanel, SchTaskBean schTaskItem, JSONObject jsonObject, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        taskInfoJsonObj = jsonObject;
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("pid", "process_name", "appDir", "appParameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public FalseHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("pid", "process_name", "appDir", "appParameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // 0采样时长（秒） duration
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, taskInfo.getDuration().toString(), false));
        // 1采样间隔 (指令数)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "period",
                TaskManageContent.PARAM_PERIOD, taskInfo.getPeriod() + "", false));
        // 2延迟采样时长 (毫秒)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "samplingDelay",
                TaskManageContent.PARAM_SAMPLING_DELAY, taskInfo.getSamplingDelay(), false));
        // 3待采样CPU核
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpuMask",
                TaskManageContent.PARAM_CPU_MASK, taskInfo.getCpuMask(), false));
        // 4采样范围 all-user
        String[] samplingSpaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL
        };
        List<String> samplingSpaceSelectList = new ArrayList<>();
        samplingSpaceSelectList.add(getI18N(taskInfo.getSamplingSpace()));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingSpace",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(samplingSpaceListData), samplingSpaceSelectList));
        // 5二进制/符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, taskInfo.getAssemblyLocation(),
                TaskManageContent.PARAM_PATH_REGEX));
        // 6C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, taskInfo.getSourceLocation(),
                TaskManageContent.PARAM_PATH_REGEX));
        // 7内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(schTaskItem.getTaskInfo().getKcore().toString()), false));
        // 8采集文件大小
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "filesize",
                TaskManageContent.PARAM_FILE_SIZE, taskInfo.getFilesize().toString(), false));
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        // 0采样时长（秒） duration
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, templateItem.getDuration() + "", false));
        // 采样间隔 (指令数)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "period",
                TaskManageContent.PARAM_PERIOD, templateItem.getPeriod(), false));
        // 延迟采样时长 (毫秒)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "samplingDelay",
                TaskManageContent.PARAM_SAMPLING_DELAY, templateItem.getSamplingDelay(), false));
        // 待采样CPU核
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpuMask",
                TaskManageContent.PARAM_CPU_MASK, templateItem.getCpuMask(), false));
        // 采样范围 all-user
        String[] samplingSpaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL
        };
        List<String> samplingSpaceSelectList = new ArrayList<>();
        samplingSpaceSelectList.add(getI18N(templateItem.getSamplingSpace()));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingSpace",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(samplingSpaceListData), samplingSpaceSelectList));
        // 5二进制/符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, templateItem.getAssemblyLocation(), false));
        // 6C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, templateItem.getSourceLocation(), false));
        // 7内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(templateItem.getKcore().toString()), false));
        // 8采集文件大小
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "filesize",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getFileSize(), false));
        generateMultiNodeInfoShowListOfTemp();
    }

    /**
     * 配置各任务类型的 多节点修改面板的参数
     *
     * @param configBean 待展示的数据
     * @return 格式化的数据
     */
    protected List<SchTaskOtherInfo> genNodeParamList(NodeConfigBean configBean) {
        List<SchTaskOtherInfo> nodeParamList = genCommonNodeParamList(configBean);
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpuMask",
                TaskManageContent.PARAM_CPU_MASK, configBean.getTaskParam().getCpuMask(), false));
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, configBean.getTaskParam().getAssemblyLocation(),
                TaskManageContent.PARAM_PATH_REGEX));
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, configBean.getTaskParam().getSourceLocation(),
                TaskManageContent.PARAM_PATH_REGEX));
        if (TaskManageContent.TARGET_APP_PROFILE_SYSTEM.equals(currentAnalysisTarget)) {
            nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "filesize",
                    TaskManageContent.PARAM_FILE_SIZE, configBean.getTaskParam().getFilesize() + "", false));
        }
        return nodeParamList;
    }

    /**
     * 特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
        // 进行特殊处理
        for (SchTaskOtherInfo infoItem : paramList) {
            if ("samplingSpace".equals(infoItem.getFieldName())) {
                String samplingSpace = infoItem.getFieldValue();
                String samplingSpaceN81I = TaskCommonFormatUtil.getSelectMapOf1().get(samplingSpace);
                jsonObj.put(infoItem.getFieldName(), samplingSpaceN81I);
            }
        }
    }

    /**
     * 多节点特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialNodeValueHandle(JSONObject jsonObj) {
        for (SchTaskOtherInfo infoItem : paramList) {
            if ("filesize".equals(infoItem.getFieldName())) {
                String filesize = infoItem.getFieldValue();
                try {
                    int filesizeInt = Integer.parseInt(filesize);
                    jsonObj.put(infoItem.getFieldName(), filesizeInt);
                } catch (NumberFormatException e) {
                    jsonObj.put(infoItem.getFieldName(), 10);
                    Logger.warn("false sharing analysis filesize parse error");
                }
            }
        }
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
        Integer durationIndex = compIndexMap.get("duration");
        getIntegerValid(durationIndex, 1, 10).ifPresent(result::add);
        // period
        Integer periodIndex = compIndexMap.get("period");
        double max = Math.pow(2, 32) - 1;
        getIntegerValid(periodIndex, 1024, max).ifPresent(result::add);
        // samplingDelay
        Integer startDelayIndex = compIndexMap.get("samplingDelay");
        getIntegerValid(startDelayIndex, 0, 900000).ifPresent(result::add);
        Integer perfDataLimitIndex = compIndexMap.get("filesize");
        getIntegerValid(perfDataLimitIndex, 1, 1024).ifPresent(result::add);

        // 二进制/符号文件路径
        getTextRegexValid("assemblyLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // C/C++源文件路径
        getTextRegexValid("sourceLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // 待采样CPU核
        getTextRegexValid("cpuMask",
                TaskManageContent.PARAM_CPU_MASK_REGEX, TaskManageContent.PARAM_CPU_MASK_REGEX2,
                SchTaskContent.INPUT_PARAM_ERROR_CPU).ifPresent(result::add);
    }
}

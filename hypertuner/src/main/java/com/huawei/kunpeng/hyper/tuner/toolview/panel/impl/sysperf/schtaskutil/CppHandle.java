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
 * 热点函数（c++性能分析）分析类型
 *
 * @since 2021-8-26
 */
public class CppHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public CppHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("target-pid", "process_name", "app-dir", "app-parameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public CppHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("target-pid", "process_name", "app-dir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // 0采样时长（秒）  Launch 不存在
        if (!TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(currentAnalysisTarget)) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                    TaskManageContent.PARAM_DURATION, schTaskItem.getTaskInfo().getDuration() + "", false));
        }
        // 1采样范围
        String samplingSpaceObjSet = schTaskItem.getTaskInfo().getSamplingSpace();
        String samplingSpace = JSONObject.parseObject(samplingSpaceObjSet).getString("id");
        String[] samplingSpaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL
        };
        List<String> samplingSpaceSelectList = new ArrayList<>();
        samplingSpaceSelectList.add(getI18N(samplingSpace));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingSpace",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(samplingSpaceListData), samplingSpaceSelectList));
        // 2采样间隔（毫秒） interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER_CUSTOM, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, schTaskItem.getTaskInfo().getInterval(), false));
        // 3二进制/符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, schTaskItem.getTaskInfo().getAssemblyLocation(), true));
        // 4C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, schTaskItem.getTaskInfo().getSourceLocation(), true));
        // 5采集文件大小 (Mb)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "size",
                TaskManageContent.PARAM_FILE_SIZE, schTaskItem.getTaskInfo().getSize() + "", false));
        // 6内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(schTaskItem.getTaskInfo().getKcore().toString()), false));
        // 待采样CPU核 只有系统类型存在
        if ("Profile System".equals(currentAnalysisTarget)) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpu-mask",
                    TaskManageContent.PARAM_CPU_MASK, schTaskItem.getTaskInfo().getCpuMask(), true));
        }
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        // 0采样时长（秒）  Launch 不存在
        if (!TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(currentAnalysisTarget)) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                    TaskManageContent.PARAM_DURATION, templateItem.getDuration() + "", false));
        }
        // 1采样范围
        String samplingSpaceObjSet = templateItem.getSamplingSpace();
        String[] samplingSpaceListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL
        };
        List<String> samplingSpaceSelectList = new ArrayList<>();
        String samplingSpace = JSONObject.parseObject(samplingSpaceObjSet).getString("id");
        samplingSpaceSelectList.add(getI18N(samplingSpace));
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "samplingSpace",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(samplingSpaceListData), samplingSpaceSelectList));
        // 2采样间隔（毫秒） interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, templateItem.getInterval(), false));
        // 3二进制/符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, templateItem.getAssemblyLocation(), true));
        // 4C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, templateItem.getSourceLocation(), true));
        // 5采集文件大小 (Mb)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "size",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getFileSize() + "", false));
        // 6内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(templateItem.getKcore().toString()), false));
        // 待采样CPU核 只有系统类型存在
        if ("Profile System".equals(currentAnalysisTarget)) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpu-mask",
                    TaskManageContent.PARAM_CPU_MASK, templateItem.getCpuMask(), true));
        }
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
        if (TaskManageContent.TARGET_APP_PROFILE_SYSTEM.equals(currentAnalysisTarget)) {
            nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "cpu-mask",
                    TaskManageContent.PARAM_CPU_MASK, configBean.getTaskParam().getCpuMask(), false));
        }
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, configBean.getTaskParam().getAssemblyLocation(), false));
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, configBean.getTaskParam().getSourceLocation(), false));
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
                String samplingSpaceN81I = TaskCommonFormatUtil.getSelectMapOf3().get(samplingSpace);
                JSONObject samplingSpaceJson2 = new JSONObject();
                samplingSpaceJson2.put("label", samplingSpace);
                samplingSpaceJson2.put("id", samplingSpaceN81I);
                jsonObj.put(infoItem.getFieldName(), samplingSpaceJson2);
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
        if (durationIndex != null) {
            getIntegerValid(durationIndex, 1, 300).ifPresent(result::add);
        }
        // interval
        Integer intervalIndex = compIndexMap.get("interval");
        getIntegerCustomValid(intervalIndex, 1, 1000).ifPresent(result::add);
        // size
        Integer sizeIndex = compIndexMap.get("size");
        getIntegerValid(sizeIndex, 1, 100).ifPresent(result::add);
        // 二进制/符号文件路径
        getTextRegexValid("assemblyLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // C/C++源文件路径
        getTextRegexValid("sourceLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // 待采样CPU核 只有系统类型存在
        getTextRegexValid("cpu-mask",
                TaskManageContent.PARAM_CPU_MASK_REGEX, TaskManageContent.PARAM_CPU_MASK_REGEX2,
                SchTaskContent.INPUT_PARAM_ERROR_CPU).ifPresent(result::add);
    }
}

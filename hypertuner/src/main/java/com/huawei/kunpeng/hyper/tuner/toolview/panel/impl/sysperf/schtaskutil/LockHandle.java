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
 * 分析类型 锁与等待分析
 *
 * @since 2021-8-26
 */
public class LockHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public LockHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
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
    public LockHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("target-pid", "process_name", "app-dir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务 信息列表
     */
    public void generateSchItemShowList() {
        // 采样时长（秒）
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        if (taskInfo.getDuration() != null) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                    TaskManageContent.PARAM_DURATION, taskInfo.getDuration() + "", false));
        }
        // 采样间隔（毫秒） interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER_CUSTOM, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, taskInfo.getInterval() + "", false));
        // 采样范围
        String[] collectRangeListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL
        };
        String collectRangeN81I = getI18N(taskInfo.getCollectRange());
        List<String> collectRangeSelect = new ArrayList<>();
        collectRangeSelect.add(collectRangeN81I);
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "collect_range",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(collectRangeListData), collectRangeSelect));
        // 符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, schTaskItem.getTaskInfo().getAssemblyLocation(), false));
        // 标准函数
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.LOCK_FUNCTION, "functionname",
                TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION, schTaskItem.getTaskInfo().getFunctionname(), false));
        // C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, schTaskItem.getTaskInfo().getSourceLocation(), false));
        // 采集文件大小 (Mb)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "collect_file_size",
                TaskManageContent.PARAM_FILE_SIZE, schTaskItem.getTaskInfo().getCollectFileSize() + "", false));
        // 内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(schTaskItem.getTaskInfo().getKcore() + ""),
                false));
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, templateItem.getInterval(), false));
        generateMultiNodeInfoShowListOfTemp();
        // 采样时长（秒）
        if (templateItem.getDuration() != null) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                    TaskManageContent.PARAM_DURATION, templateItem.getDuration() + "", false));
        }
        // 采样间隔（毫秒） interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, templateItem.getInterval() + "", false));
        // 采样范围
        String[] collectRangeListData = new String[]{
                TaskManageContent.PARAM_SAMPLING_RANGE_USER,
                TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL,
                TaskManageContent.PARAM_SAMPLING_RANGE_ALL
        };
        String collectRangeN81I = getI18N(templateItem.getCollectRange());
        List<String> collectRangeSelect = new ArrayList<>();
        collectRangeSelect.add(collectRangeN81I);
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "collect_range",
                TaskManageContent.PARAM_SAMPLING_RANGE, Arrays.asList(collectRangeListData), collectRangeSelect));
        // 符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, templateItem.getAssemblyLocation(), false));
        // 标准函数
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.LOCK_FUNCTION, "functionname",
                TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION, templateItem.getFunctionname(), false));
        // C/C++源文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "sourceLocation",
                TaskManageContent.PARAM_SOURCE_LOCATION, templateItem.getSourceLocation(), false));
        // 采集文件大小 (Mb)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "collect_file_size",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getFileSize() + "", false));
        // 内核函数关联汇编代码
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "kcore",
                TaskManageContent.PARAM_ASSOCIATE_LOCATION,
                TaskCommonFormatUtil.booleFormat(templateItem.getKcore() + ""),
                false));
        generateMultiNodeInfoShowListOfTemp();
        setMultiNode(true);
    }

    /**
     * 配置各任务类型的 多节点修改面板的参数
     *
     * @param configBean 待展示的数据
     * @return 格式化的数据
     */
    protected List<SchTaskOtherInfo> genNodeParamList(NodeConfigBean configBean) {
        List<SchTaskOtherInfo> nodeParamList = genCommonNodeParamList(configBean);
        // 二进制/符号问价路径
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, configBean.getTaskParam().getAssemblyLocation(), false));
        // c/c++源文件路径
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
            if ("collect_range".equals(infoItem.getFieldName())) {
                String fieldValueN81I = TaskCommonFormatUtil.getSelectMapOf2().get(infoItem.getFieldValue());
                jsonObj.put(infoItem.getFieldName(), fieldValueN81I);
            }
        }
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
        // duration
        Integer durationIndex = compIndexMap.get("duration");
        if (durationIndex != null) {
            getIntegerValid(durationIndex, 1, 300).ifPresent(result::add);
        }
        // interval
        Integer intervalIndex = compIndexMap.get("interval");
        getIntegerCustomValid(intervalIndex, 1, 1000).ifPresent(result::add);
        // collectFileSize
        Integer collectFileSizeIndex = compIndexMap.get("collectFileSize");
        getIntegerValid(collectFileSizeIndex, 1, 4096).ifPresent(result::add);
        // 二进制/符号文件路径
        getTextRegexValid("assemblyLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
        // C/C++源文件路径
        getTextRegexValid("sourceLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
    }
}

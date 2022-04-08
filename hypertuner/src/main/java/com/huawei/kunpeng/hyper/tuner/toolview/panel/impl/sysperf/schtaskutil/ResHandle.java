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

import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 分析类型:资源调度
 *
 * @since 2021-8-26
 */
public class ResHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public ResHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("target-pid", "process-name", "app-dir", "app-parameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public ResHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("target-pid", "process-name", "app-dir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        TaskInfoBean schTaskItemTaskInfo = schTaskItem.getTaskInfo();
        // 采样时长（s）
        if (!"Launch Application".equals(schTaskItemTaskInfo.getAnalysisTarget())) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                    TaskManageContent.PARAM_DURATION, schTaskItemTaskInfo.getDuration().toString(), false));
        }
        // 二进制/符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, schTaskItemTaskInfo.getAssemblyLocation(), false));
        // 采集调用栈
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "dis-callstack", TaskManageContent.PARAM_STACK,
                TaskCommonFormatUtil.booleFormat(schTaskItemTaskInfo.getDisCallstack() + ""), false));
        // 采集文件大小 (MB)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "size",
                TaskManageContent.PARAM_FILE_SIZE, schTaskItemTaskInfo.getSize() + "", false));
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        // 采样时长（s）
        if (!"Launch Application".equals(templateItem.getAnalysisTarget())) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                    TaskManageContent.PARAM_DURATION, templateItem.getDuration().toString(), false));
        }
        // 二进制/符号文件路径
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, templateItem.getAssemblyLocation(), false));
        // 采集调用栈
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "dis-callstack", TaskManageContent.PARAM_STACK,
                TaskCommonFormatUtil.booleFormat(templateItem.getDisCallstack() + ""), false));
        // 采集文件大小 (MB)
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "size",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getSize() + "", false));
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
        // 二进制符号文件路径
        nodeParamList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "assemblyLocation",
                TaskManageContent.PARAM_ASSEMBLY_LOCATION, configBean.getTaskParam().getAssemblyLocation(), false));
        return nodeParamList;
    }

    /**
     * 特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
        // 进行特殊处理
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
        Integer sizeIndex = compIndexMap.get("size");
        getIntegerValid(sizeIndex, 1, 512).ifPresent(result::add);
        // 二进制/符号文件路径
        getTextRegexValid("assemblyLocation", TaskManageContent.PARAM_PATH_REGEX, "",
                SchTaskContent.INPUT_PARAM_ERROR_LOCATION).ifPresent(result::add);
    }
}

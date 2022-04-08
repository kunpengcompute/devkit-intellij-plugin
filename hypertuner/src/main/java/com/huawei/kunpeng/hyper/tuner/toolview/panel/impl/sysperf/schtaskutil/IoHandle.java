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
 * 分析类型 IO分析
 *
 * @since 2021-8-26
 */
public class IoHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public IoHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("targetPid", "process_name", "appDir", "app-parameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public IoHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("targetPid", "process_name", "appDir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // 采样时长（秒） duration
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, taskInfo.getDuration() + "", false));
        // 统计周期（秒）
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "statistical",
                TaskManageContent.PARAM_STATISTICAL, taskInfo.getStatistical() + "", false));
        // 采集文件大小（MB）
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "size",
                TaskManageContent.PARAM_FILE_SIZE, taskInfo.getSize().toString(), false));
        // 采集调用栈
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "stack",
                TaskManageContent.PARAM_STACK, TaskCommonFormatUtil.booleFormat(taskInfo.getStack() + ""), false));
        // io 分析类型 分析对象 为系统时不支持多节点配置
        if (!TaskManageContent.TARGET_APP_PROFILE_SYSTEM.equals(currentAnalysisTarget)) {
            generateMultiNodeInfoShowList();
            setMultiNode(true);
        }
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, templateItem.getDuration().toString(), false));
        generateMultiNodeInfoShowListOfTemp();
        // 采样时长（秒） duration
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, templateItem.getDuration() + "", false));
        // 统计周期（秒）
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "statistical",
                TaskManageContent.PARAM_STATISTICAL, templateItem.getStatistical() + "", false));
        // 采集文件大小（MB）
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "size",
                TaskManageContent.PARAM_FILE_SIZE, templateItem.getFileSize(), false));
        // 采集调用栈
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "stack",
                TaskManageContent.PARAM_STACK, TaskCommonFormatUtil.booleFormat(templateItem.getStack() + ""), false));
        // io 分析类型 分析对象 为系统时不支持多节点配置
        if (!TaskManageContent.TARGET_APP_PROFILE_SYSTEM.equals(currentAnalysisTarget)) {
            generateMultiNodeInfoShowListOfTemp();
            setMultiNode(true);
        }
    }

    /**
     * 配置各任务类型的 多节点修改面板的参数
     *
     * @param configBean 待展示的数据
     * @return 格式化的数据
     */
    protected List<SchTaskOtherInfo> genNodeParamList(NodeConfigBean configBean) {
        return genCommonNodeParamList(configBean);
    }

    /**
     * 特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
    }

    /**
     * 各类型继承，获取不同的对象
     *
     * @param nodeConfigItem nodeConfigItem
     * @return task_param 对象
     */
    protected JSONObject getTaskParam(JSONObject nodeConfigItem) {
        return nodeConfigItem.getJSONObject("taskParam");
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
        Integer durationIndex = compIndexMap.get("duration");
        getIntegerValid(durationIndex, 2, 300).ifPresent(result::add);
        // statistical
        Integer statisticalIndex = compIndexMap.get("statistical");
        getIntegerValid(statisticalIndex, 1, 5).ifPresent(result::add);
        // size
        Integer sizeIndex = compIndexMap.get("size");
        getIntegerValid(sizeIndex, 10, 500).ifPresent(result::add);
    }
}

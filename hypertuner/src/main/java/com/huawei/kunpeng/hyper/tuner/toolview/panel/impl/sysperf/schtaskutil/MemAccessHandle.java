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
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskInfoBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.EditPanelTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskOtherInfo;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 内存访问 分析类型
 *
 * @since 2021-8-26
 */
public class MemAccessHandle extends SchTaskPanelHandle {
    /**
     * 使用 SchTaskBean 类型的预约任务信息初始化
     *
     * @param otherInfoPanel 被添加面板
     * @param schTaskItem    待修改预约任务对象
     * @param compIndexMap   组件索引Map
     */
    public MemAccessHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("pid", "process_name", "app-dir", "app-parameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public MemAccessHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("pid", "process_name", "app-dir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // 采样时长 duration
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, taskInfo.getDuration() + "", false));
        // 采样间隔  interval 100/1000 无需国际化
        List<String> intervalShowList = new ArrayList<>();
        List<String> intervalSelect = new ArrayList<>();
        intervalSelect.add(taskInfo.getInterval());
        intervalShowList.add("100");
        intervalShowList.add("1000");
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, intervalShowList, intervalSelect));
        // 采样类型  缓存/ddr
        List<String> selected = new ArrayList<>();
        Object typeObj = JSONArray.parse(taskInfo.getTaskParam().getType());
        if (typeObj instanceof JSONArray) {
            JSONArray typeJsonArr = (JSONArray) typeObj;
            for (int i = 0; i < typeJsonArr.size(); i++) {
                selected.add(getI18N(typeJsonArr.getString(i)));
            }
        }
        List<String> showList = new ArrayList<>();
        showList.add(TaskManageContent.PARAM_SAMPLE_TYPE_CACHE);
        showList.add(TaskManageContent.PARAM_SAMPLE_TYPE_DDR);
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.MULTI_CHECK_BOX, "task_param",
                TaskManageContent.PARAM_SAMPLE_TYPE, showList, selected));
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, templateItem.getDuration().toString(), false));
        // 采样间隔  interval 100/1000 无需国际化
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_LABEL, "interval",
                TaskManageContent.PARAM_INTERVAL_MS, templateItem.getInterval(), false));
        // 采样类型  缓存/ddr
        List<String> selected = new ArrayList<>();
        Object typeObj = JSONArray.parse(templateItem.getTaskParam().getType());
        if (typeObj instanceof JSONArray) {
            JSONArray typeJsonArr = (JSONArray) typeObj;
            for (int i = 0; i < typeJsonArr.size(); i++) {
                selected.add(getI18N(typeJsonArr.getString(i)));
            }
        }
        List<String> showList = new ArrayList<>();
        showList.add(TaskManageContent.PARAM_SAMPLE_TYPE_CACHE);
        showList.add(TaskManageContent.PARAM_SAMPLE_TYPE_DDR);
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.MULTI_CHECK_BOX, "task_param",
                TaskManageContent.PARAM_SAMPLE_TYPE, showList, selected));
        generateMultiNodeInfoShowListOfTemp();
    }

    /**
     * 特殊值处理
     *
     * @param jsonObj jsonObject
     */
    public void specialValueHandle(JSONObject jsonObj) {
        // 进行特殊处理
        for (SchTaskOtherInfo infoItem : paramList) {
            if ("task_param".equals(infoItem.getFieldName())) {
                JSONArray typeJsonArr = JSONArray.parseArray(infoItem.getFieldValue());
                for (int i = 0; i < typeJsonArr.size(); i++) {
                    typeJsonArr.set(i, selectN81IMap.get(typeJsonArr.get(i).toString())); // 将国际化转为接口传值
                }
                JSONObject typeJsonObj = new JSONObject();
                typeJsonObj.put("type", typeJsonArr);
                jsonObj.put(infoItem.getFieldName(), typeJsonObj); // 采样类型
            }
        }
    }

    @Override
    public void inputValid(List<ValidationInfo> result) {
        Integer durationIndex = compIndexMap.get("duration");
        getIntegerValid(durationIndex, 1, 300).ifPresent(result::add);
    }
}

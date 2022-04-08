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
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskInfoBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.EditPanelTypeEnum;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskOtherInfo;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base.SchTaskPanelHandle;

import com.intellij.openapi.ui.ValidationInfo;

import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 全景分析 分析类型
 *
 * @since 2021-8-26
 */
public class OverAllHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public OverAllHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public OverAllHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // duration 采样时长 0（秒）
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, taskInfo.getDuration() + "", false));
        // 采样间隔 1（毫秒） interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_S, taskInfo.getInterval() + "", false));
        // 采集Top活跃进程
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "topCheck",
                TaskManageContent.PARAM_TOP_CHECK,
                TaskCommonFormatUtil.booleFormat(taskInfo.getTopCheck() + ""), false));
        // 大数据采集路径 大数据类型任务
        String configDir = schTaskItem.getTaskInfo().getConfigDir();
        if (configDir != null) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "configDir",
                    TaskManageContent.PARAM_CONFIG_DIR, taskInfo.getTopCheck().toString(), false));
        }
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        // duration 采样时长 0（秒）
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, templateItem.getDuration() + "", false));
        // 采样间隔 1（毫秒） interval
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_S, templateItem.getInterval() + "", false));
        // 采集Top活跃进程
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "topCheck",
                TaskManageContent.PARAM_TOP_CHECK,
                TaskCommonFormatUtil.booleFormat(templateItem.getTopCheck() + ""), false));
        // 大数据采集路径 大数据类型任务
        String configDir = templateItem.getConfigDir();
        if (configDir != null) {
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "configDir",
                    TaskManageContent.PARAM_CONFIG_DIR, templateItem.getTopCheck().toString(), false));
        }
    }

    /**
     * 对输入的值进行校验
     *
     * @param result 校验数组
     */
    public void inputValid(List<ValidationInfo> result) {
        Integer durationIndex = compIndexMap.get("duration");
        getIntegerValid(durationIndex, 2, 300).ifPresent(result::add);
        // interval
        Integer intervalIndex = compIndexMap.get("interval");
        Integer durationValue = getValueFromJSpinner(otherInfoPanel.getComponent(durationIndex));
        int max = Math.min(durationValue / 2, 10);
        getIntegerValid(intervalIndex, 1, max).ifPresent(result::add);
    }
}

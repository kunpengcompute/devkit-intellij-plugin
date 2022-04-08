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

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

/**
 * 分析类型 HPC分析
 *
 * @since 2021-8-26
 */
public class HpcHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public HpcHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
        super(otherInfoPanel, schTaskItem, compIndexMap);
        currentAnalysisTarget = schTaskItem.getTaskInfo().getAnalysisTarget();
        setAnalysisTargetInfo("targetPid", "process_name", "app-dir", "app-parameters");
        generateSchItemShowList();
    }

    /**
     * 构造函数 - 任务模板
     *
     * @param otherInfoPanel 目标面板
     * @param templateItem   待展示的任务模板
     */
    public HpcHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("targetPid", "process_name", "app-dir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // 采样时长（秒）
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, taskInfo.getDuration().toString(), false));
        // 采样类型
        List<String> select = new ArrayList<>();
        select.add(getI18N(taskInfo.getPreset()));
        String[] samplingModeListData = new String[]{
                TaskManageContent.PARAM_SAMPLE_TYPE_HPC_SUMMARY,
                TaskManageContent.PARAM_SAMPLE_TYPE_HPC_TOP_DOWN,
                TaskManageContent.PARAM_SAMPLE_TYPE_HPC_INSTRUCT
        };
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "preset", TaskManageContent.PARAM_SAMPLE_TYPE,
                Arrays.asList(samplingModeListData), select));
        if (TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(currentAnalysisTarget)) {
            // OpenMP参数 openMpParam
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "open_mp_param",
                    TaskManageContent.PARAM_HPC_OPENMP_PARAM, taskInfo.getOpenMpParam(), false));
            // MPI
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "mpi_status",
                    TaskManageContent.PARAM_HPC_MPI,
                    TaskCommonFormatUtil.booleFormat(schTaskItem.getTaskInfo().getMpiStatus().toString()), false));
            // 命令所在目录 mpiEnvDir
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "mpi_env_dir",
                    TaskManageContent.PARAM_HPC_MPI_DIR, taskInfo.getMpiEnvDir(), false));
            // rank rank
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "rank",
                    TaskManageContent.PARAM_HPC_MPI_RANK, taskInfo.getRank() + "", false));
        }
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION, templateItem.getDuration().toString(), false));
        // 采样类型
        List<String> select = new ArrayList<>();
        select.add(getI18N(templateItem.getPreset()));
        String[] samplingModeListData = new String[]{
                TaskManageContent.PARAM_SAMPLE_TYPE_HPC_SUMMARY,
                TaskManageContent.PARAM_SAMPLE_TYPE_HPC_TOP_DOWN,
                TaskManageContent.PARAM_SAMPLE_TYPE_HPC_INSTRUCT
        };
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_COMBO_BOX, "preset", TaskManageContent.PARAM_SAMPLE_TYPE,
                Arrays.asList(samplingModeListData), select));
        if (TaskManageContent.TARGET_APP_LAUNCH_APPLICATION.equals(currentAnalysisTarget)) {
            // OpenMP参数 openMpParam
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "open_mp_param",
                    TaskManageContent.PARAM_HPC_OPENMP_PARAM, templateItem.getOpenMpParam(), false));
            // MPI
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "mpi_status",
                    TaskManageContent.PARAM_HPC_MPI,
                    TaskCommonFormatUtil.booleFormat(templateItem.getMpiStatus().toString()), false));
            // 命令所在目录 mpiEnvDir
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.TEXT_FIELD, "mpi_env_dir",
                    TaskManageContent.PARAM_HPC_MPI_DIR, templateItem.getMpiEnvDir(), false));
            // rank rank
            paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "rank",
                    TaskManageContent.PARAM_HPC_MPI_RANK, templateItem.getRank() + "", false));
        }
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
            if ("preset".equals(infoItem.getFieldName())) {
                HashMap<String, String> i18NMap = new HashMap<>();
                // HPC 采样类型 与微架构分析 采样模式 sampleMode 冲突，请注意
                i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_HPC_SUMMARY, "default");
                i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_HPC_TOP_DOWN, "top-down");
                i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_HPC_INSTRUCT, "instruction-mix");
                String presetValue = infoItem.getFieldValue();
                jsonObj.put(infoItem.getFieldName(), i18NMap.get(presetValue));
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
        getIntegerValid(durationIndex, 1, 300).ifPresent(result::add);
        // rank rank
        Integer rankIndex = compIndexMap.get("rank");
        if (rankIndex != null) {
            getIntegerValid(rankIndex, 1, 128).ifPresent(result::add);
        }
    }
}

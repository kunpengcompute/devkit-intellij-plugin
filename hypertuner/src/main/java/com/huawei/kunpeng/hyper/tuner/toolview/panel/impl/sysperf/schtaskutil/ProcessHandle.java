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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ValidationInfo;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 分析类型
 *
 * @since 2021-8-26
 */
public class ProcessHandle extends SchTaskPanelHandle {
    /**
     * 构造函数
     *
     * @param otherInfoPanel 目标面板
     * @param schTaskItem    待修改的预约任务 （SchTaskBean 格式）
     * @param compIndexMap   组件索引Map
     */
    public ProcessHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem, HashMap<String, Integer> compIndexMap) {
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
    public ProcessHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
        super(otherInfoPanel, templateItem);
        currentAnalysisTarget = templateItem.getAnalysisTarget();
        setAnalysisTargetInfo("pid", "process_name", "app-dir", "app-parameters");
        generateTempItemShowList();
    }

    /**
     * 获取单个分析任务中待展示的数据列表，供预约任务详情，预约任务修改，
     */
    public void generateSchItemShowList() {
        // 采样时长（秒）
        TaskInfoBean taskInfo = schTaskItem.getTaskInfo();
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, taskInfo.getDuration() + "", false));
        // 采样间隔
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_S, taskInfo.getInterval() + "", false));
        // 采样类型 cpu/内存/存储io/上下文切换
        Object typeObj = JSONArray.parse(taskInfo.getTaskParam().getType());
        List<String> selectList = new ArrayList<>();
        if (typeObj instanceof JSONArray) {
            JSONArray typeJsonArr = (JSONArray) typeObj;
            for (int i = 0; i < typeJsonArr.size(); i++) {
                selectList.add(getI18N(typeJsonArr.getString(i)));
            }
        }
        String[] typeShowList = new String[]{
                TaskManageContent.PARAM_SAMPLE_TYPE_CPU,
                TaskManageContent.PARAM_SAMPLE_TYPE_DISK,
                TaskManageContent.PARAM_SAMPLE_TYPE_CONTEXT,
                TaskManageContent.PARAM_SAMPLE_TYPE_MEM};
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.MULTI_CHECK_BOX, "task_param",
                TaskManageContent.PARAM_SAMPLE_TYPE, Arrays.asList(typeShowList), selectList));
        // 采集线程信息
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "thread",
                TaskManageContent.PARAM_THREAD, TaskCommonFormatUtil.booleFormat(taskInfo.getThread()), false));
        if ("Profile System".equals(taskInfo.getAnalysisTarget())) {
            return;
        }
        // 跟踪系统调用
        String straceAnalysis = taskInfo.getStraceAnalysis();
        Boolean straceAnalysisBool = Boolean.FALSE;
        if ("enable".equals(straceAnalysis) || "true".equals(straceAnalysis)) {
            straceAnalysisBool = Boolean.TRUE;
        }
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "strace-analysis",
                TaskManageContent.PARAM_STRACE_ANALYSIS, TaskCommonFormatUtil.booleFormat(straceAnalysisBool + ""),
                false));
        generateMultiNodeInfoShowList();
        setMultiNode(true);
    }

    /**
     * 获取单个分析任务中待展示的数据列表，任务模板详情页面展示
     */
    public void generateTempItemShowList() {
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "duration",
                TaskManageContent.PARAM_DURATION_SAMPLING, templateItem.getDuration() + "", false));
        // 采样间隔
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.J_SPINNER, "interval",
                TaskManageContent.PARAM_INTERVAL_S, templateItem.getInterval() + "", false));
        // 采样类型 cpu/内存/存储io/上下文切换
        Object typeObj = JSONArray.parse(templateItem.getTaskParam().getType());
        List<String> selectList = new ArrayList<>();
        if (typeObj instanceof JSONArray) {
            JSONArray typeJsonArr = (JSONArray) typeObj;
            for (int i = 0; i < typeJsonArr.size(); i++) {
                selectList.add(getI18N(typeJsonArr.getString(i)));
            }
        }
        String[] typeShowList = new String[]{
                TaskManageContent.PARAM_SAMPLE_TYPE_CPU,
                TaskManageContent.PARAM_SAMPLE_TYPE_DISK,
                TaskManageContent.PARAM_SAMPLE_TYPE_CONTEXT,
                TaskManageContent.PARAM_SAMPLE_TYPE_MEM};
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.MULTI_CHECK_BOX, "task_param",
                TaskManageContent.PARAM_SAMPLE_TYPE, Arrays.asList(typeShowList), selectList));
        // 采集线程信息
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "thread",
                TaskManageContent.PARAM_THREAD, TaskCommonFormatUtil.booleFormat(templateItem.getThread()), false));
        if ("Profile System".equals(templateItem.getAnalysisTarget())) {
            return;
        }
        // 跟踪系统调用
        String straceAnalysis = templateItem.getStraceAnalysis();
        Boolean straceAnalysisBool = Boolean.FALSE;
        if ("enable".equals(straceAnalysis) || "true".equals(straceAnalysis)) {
            straceAnalysisBool = Boolean.TRUE;
        }
        paramList.add(new SchTaskOtherInfo(EditPanelTypeEnum.RADIO_BTN, "strace-analysis",
                TaskManageContent.PARAM_STRACE_ANALYSIS, TaskCommonFormatUtil.booleFormat(straceAnalysisBool + ""),
                false));
        generateMultiNodeInfoShowListOfTemp();
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
        // 进行特殊处理
        for (SchTaskOtherInfo infoItem : paramList) {
            if ("strace-analysis".equals(infoItem.getFieldName())) {
                jsonObj.put(infoItem.getFieldName(), bool2AbleString(Boolean.parseBoolean(infoItem.getFieldValue())));
            }
            if ("thread".equals(infoItem.getFieldName())) {
                jsonObj.put(infoItem.getFieldName(), bool2AbleString(Boolean.parseBoolean(infoItem.getFieldValue())));
            }
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

    /**
     * 进程线程 节点数展示内容，默认展示 nodeParamMap 对应节点的list中的全部数据
     *
     * @param oneNodeNode       树节点
     * @param infos 每个节点对应的数据列表(与多节点配置面板相同)
     */
    protected void addNodeTreeToDetailEveryType(DefaultMutableTreeNode oneNodeNode, List<SchTaskOtherInfo> infos) {
        for (SchTaskOtherInfo item : infos) {
            if (EditPanelTypeEnum.J_LABEL.value().equals(item.getFieldType())) {
                continue;
            }
            String nodeValueStr = item.getFieldValue();
            nodeValueStr = StringUtils.isEmpty(nodeValueStr) ? "--" : nodeValueStr;
            String userObject = item.getFieldNameI18N() + "    " + nodeValueStr;
            oneNodeNode.add(new DefaultMutableTreeNode(userObject));
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
        Integer max = Math.min(durationValue / 2, 10);
        getIntegerValid(intervalIndex, 1, max).ifPresent(result::add);
    }
}

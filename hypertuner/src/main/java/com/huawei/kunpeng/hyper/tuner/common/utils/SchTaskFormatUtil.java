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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeConfigTaskParamBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskInfoBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 预约任务格式化工具类
 *
 * @since 2021-4-25
 */
public class SchTaskFormatUtil extends TaskCommonFormatUtil {
    /**
     * 将 Json 对象 转化为 Java 对象
     *
     * @param scheduleTaskItem 带格式化的 预约任务对象
     * @return 格式化得到的java对象
     */
    public static SchTaskBean taskJson2JavaObj(JSONObject scheduleTaskItem) {
        String taskInfoStr = scheduleTaskItem.getString("taskInfo");
        JSONObject taskInfoJsonObj = JSONObject.parseObject(taskInfoStr);
        scheduleTaskItem.put("taskInfo", taskInfoJsonObj);
        SchTaskBean schTaskBean = scheduleTaskItem.toJavaObject(SchTaskBean.class);
        JSONObject taskInfo = scheduleTaskItem.getJSONObject("taskInfo");
        TaskInfoBean taskInfoBean = taskInfo.toJavaObject(TaskInfoBean.class);

        // 将switch 转化为 switchResponse
        taskInfoBean.setSwitchResponse(taskInfo.getBoolean("switch"));
        JSONArray nodeConfigArr = taskInfo.getJSONArray("nodeConfig");
        List<NodeConfigBean> nodeConfigBeans = new ArrayList<>();
        for (int j = 0; j < nodeConfigArr.size(); j++) {
            JSONObject nodeConfigItem = nodeConfigArr.getJSONObject(j);
            NodeConfigBean nodeConfigBean = nodeConfigItem.toJavaObject(NodeConfigBean.class);
            JSONObject taskParam = nodeConfigItem.getJSONObject("taskParam");
            if (taskParam == null) {
                taskParam = nodeConfigItem.getJSONObject("task_param");
            }
            NodeConfigTaskParamBean nodeConfigTaskParamBean = taskParam.toJavaObject(NodeConfigTaskParamBean.class);
            nodeConfigBean.setTaskParam(nodeConfigTaskParamBean);
            nodeConfigBeans.add(nodeConfigBean);
        }
        taskInfoBean.setNodeConfig(nodeConfigBeans);
        schTaskBean.setTaskInfo(taskInfoBean);
        String beanAnaType = schTaskBean.getAnalysisType();
        String beanAnaTarget = schTaskBean.getTaskInfo().getAnalysisTarget();
        if (beanAnaTarget == null || beanAnaTarget.isEmpty()) {
            if (beanAnaTarget == null || beanAnaTarget.isEmpty()) {
                String taskParamBeanTarget = schTaskBean.getTaskInfo().getTaskParam().getTarget();
                String analysisTarget = getAnalysisTarget(beanAnaType, taskParamBeanTarget);
                schTaskBean.getTaskInfo().setAnalysisTarget(analysisTarget);
            }
        }
        return schTaskBean;
    }

    /**
     * 任务状态：
     * 预约
     * 下发中
     * 完成
     * 失败
     *
     * @param status 带格式化的状态
     * @return 格式化之后的状态
     */
    public static String stateFormat(String status) {
        if (status == null || status.isEmpty()) {
            return TaskManageContent.FORMAT_ERROR;
        }
        String statusFormat = "";
        switch (status) {
            case "reserve":
                statusFormat = SchTaskContent.STATUS_RESERVE;
                break;
            case "running":
                statusFormat = SchTaskContent.STATUS_RUNNING;
                break;
            case "success":
                statusFormat = SchTaskContent.STATUS_SUCCESS;
                break;
            case "fail":
                statusFormat = SchTaskContent.STATUS_FAIL;
                break;
            default:
                statusFormat = status;
        }
        return statusFormat;
    }
}

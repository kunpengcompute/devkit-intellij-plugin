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

import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 格式化任务模板对象
 *
 * @since 2021-4-26
 */
public class TaskTemplateFormatUtil extends TaskCommonFormatUtil {
    /**
     * 格式化返回值 对象
     *
     * @param bean 带格式化的 预约任务对象
     */
    public static void tempFormat(TaskTemplateBean bean) {
        String beanTar = bean.getAnalysisTarget();
        if (beanTar == null || beanTar.isEmpty()) {
            String analysisType = bean.getAnalysisType();
            bean.setAnalysisTarget(analysisType);
        }
        String analysisTypeFormat = analysisTypeFormat(bean.getAnalysisType());
        bean.setAnalysisType(analysisTypeFormat);
        String analysisTargetFormat = TaskTemplateFormatUtil.analysisTargetFormat(bean.getAnalysisTarget());
        bean.setAnalysisTarget(analysisTargetFormat);
    }

    /**
     * JSON 对象转JavaBean
     * 对analysisTarget字段未空的访存分析类型任务模板进行过滤
     *
     * @param jsonObject json对象
     * @return TaskTemplateBean
     */
    public static TaskTemplateBean js2JavaObj(JSONObject jsonObject) {
        TaskTemplateBean bean;
        bean = JSON.toJavaObject(jsonObject, TaskTemplateBean.class);
        String beanAnaTarget = bean.getAnalysisTarget();
        String beanAnaType = bean.getAnalysisType();

        // 对访存分析特殊情况进行处理
        if (beanAnaTarget == null || beanAnaTarget.isEmpty()) {
            if (beanAnaType.equals(MEM_ACCESS) || beanAnaType.equals(MISS_EVENT) || beanAnaType.equals(FALSE_SHARING)) {
                String taskParamBeanTarget = bean.getTaskParam().getTarget();
                String analysisTarget = getAnalysisTarget(beanAnaType, taskParamBeanTarget);
                bean.setAnalysisTarget(analysisTarget);
            } else {
                bean.setAnalysisTarget(beanAnaType);
            }
        }

        // 采集文件大小 处理
        int fileSize = fileSizeHandle(beanAnaType, jsonObject);
        bean.setFileSize(fileSize + "");
        bean.setSize(fileSize + "");
        return bean;
    }

    /**
     * 采集文件大小 处理
     *
     * @param beanAnaType beanAnaType
     * @param jsonObject  jsonObject
     * @return 采集文件大小
     */
    private static Integer fileSizeHandle(String beanAnaType, JSONObject jsonObject) {
        Integer fileSize;
        if (jsonObject.getInteger("filesize") != null
                && jsonObject.getInteger("filesize") > 0) {
            fileSize = jsonObject.getInteger("filesize");
        } else if (jsonObject.getInteger("perfDataLimit") != null
                && jsonObject.getInteger("perfDataLimit") > 0) {
            // microarchitecture
            fileSize = jsonObject.getInteger("perfDataLimit");
        } else if (jsonObject.getInteger("size") != null
                && jsonObject.getInteger("size") > 0) {
            // ioperformance
            fileSize = jsonObject.getInteger("size");
        } else if (jsonObject.getInteger("collect_file_size") != null
                && jsonObject.getInteger("collect_file_size") > 0) {
            // system_lock
            fileSize = jsonObject.getInteger("collect_file_size");
        } else {
            fileSize = 0;
        }
        return fileSize;
    }

    /**
     * 格式化返回值 对象
     *
     * @param beans 带格式化的 预约任务对象
     */
    public static void tempFormat(List<TaskTemplateBean> beans) {
        for (TaskTemplateBean bean : beans) {
            tempFormat(bean);
        }
    }
}
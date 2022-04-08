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

package com.huawei.kunpeng.hyper.tuner.http;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.project.SysperfProject;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.project.SysperfProjects;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.TaskData;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.Tasklist;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * sysperf获取数据类
 *
 * @since 2021-01-06
 */
public class SysperfProjectServer {
    /**
     * 请求成功返回
     */
    public static final String SUCCESS_CODE = "SysPerf.Success";

    /**
     * 工程管理URL
     */
    public static final String GETPROJECT_URL = "sys-perf/api/v2.2/projects/?auto-flag=on";

    /**
     * 获取所有sysperf項目
     *
     * @return List
     */
    public static List<SysperfProject> getAllSysperfProject() {
        Logger.info("Start obtain all tasks.");
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                GETPROJECT_URL, HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return Collections.emptyList();
        }

        // 处理获取到的后端数据
        return JSONObject.parseObject(response.getData(), SysperfProjects.class).getProjects();
    }

    /**
     * 获取所有任务
     *
     * @param projectname 项目名
     * @return 任务集合
     */
    public static List<Tasklist> getAllSysperfTasks(String projectname) {
        Logger.info("Start obtain all tasks.");
        List<Tasklist> tasklist = new ArrayList<>();
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/tasks/task-summary/?analysis-type=all&project-name="
                        + projectname + "&auto-flag=on&page=1&per-page=1000",
                HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        if (Objects.isNull(response)) {
            return tasklist;
        }
        // 处理获取到的后端数据
        String result = response.getData().replaceAll("switch", "switcha");
        result = result.substring(1, result.length() - 1);
        if (!("").equals(result)) {
            tasklist = JSONObject.parseObject(result, TaskData.class).getTasklist();
        }
        for (Tasklist taskListItem : tasklist) {
            String taskName = taskListItem.getTaskname();
            taskName = scheduleTaskTaskNameHandle(taskName);
            taskListItem.setTaskname(taskName);
        }
        return tasklist;
    }

    /**
     * 根据任务名称判断是否为预约任务，并格式化预约任务的任务名称
     *
     * @param taskName 任务名称
     * @return 格式化之后的任务名称
     */
    private static String scheduleTaskTaskNameHandle(String taskName) {
        if (taskName.length() < 19) {
            return taskName;
        }
        // 判断是否为预约后执行的任务
        if (scheduleTaskJudge(taskName)) {
            String taskNameSubStr1 = taskName.substring(0, taskName.length() - 9);
            String taskNameSubStr2 = taskName.substring(taskName.length() - 9)
                    .replaceFirst("-", " ").replaceAll("-", ":");
            return taskNameSubStr1 + taskNameSubStr2;
        } else {
            return taskName;
        }
    }

    /**
     * 根据任务名称判断是否为预约后执行的任务
     *
     * @param taskName 任务名称
     * @return bool
     */
    public static boolean scheduleTaskJudge(String taskName) {
        if (taskName.length() < 19) {
            return false;
        }
        String taskNameSubStr2 = taskName.substring(taskName.length() - 8).replaceAll("-", ":");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            format.parse(taskNameSubStr2);
            // 截取的字符串转化为时间，成功则为预约任务
            return true;
        } catch (ParseException e) {
            // 失败则为 非预约任务
            return false;
        }
    }

    /**
     * 导出任务
     *
     * @param taskId 任务名
     * @return 结果
     */
    public static ResponseBean startTask(int taskId) {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/tasks/" + taskId + "/status/",
                        HttpMethod.PUT.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("status", "running");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 导出任务
     *
     * @param taskId 任务名
     * @return 结果
     */
    public static ResponseBean stopTask(int taskId) {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/tasks/" + taskId + "/status/",
                        HttpMethod.PUT.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("status", "cancelled");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 导出任务
     *
     * @param taskId 任务名
     * @return 结果
     */
    public static ResponseBean exportTaskRar(String taskId) {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/import_export_tasks/?language=zh&id=" + taskId,
                        HttpMethod.GET.vaLue(), "");
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 创建任务
     *
     * @param paramMap   导入任务名
     * @param section    上传文件个数
     * @param uploadType 上传文件方式
     * @param filePath   指定文件路径
     * @param fileSize   文件大小
     * @return ResponseBean
     */
    public static ResponseBean uploadTaskFile(Map<String, String> paramMap, int section, String uploadType,
        String filePath, int fileSize) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/import_export_tasks/import_task/",
                HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        String projectName = paramMap.get("projectName");
        if (projectName != null && !projectName.isEmpty()) {
            obj.put("project_name", projectName);
        }
        String taskName = paramMap.get("taskName");
        if (taskName != null && !taskName.isEmpty()) {
            obj.put("task_name", taskName);
        }
        if ("server".equals(uploadType)) {
            obj.put("file_path", filePath);
            obj.put("upload_type", uploadType);
        } else {
            obj.put("section_qty", section);
            obj.put("task_filesize", fileSize);
            obj.put("upload_type", uploadType);
        }
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (!SUCCESS_CODE.equals(responseBean.getCode())) {
            IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                    responseBean.getMessage(), NotificationType.ERROR));
        }
        return responseBean;
    }

    /**
     * 检测文件上传状态
     *
     * @param index    任务id
     * @param fileName 文件名
     * @return 结果
     */
    public static ResponseBean uploadSuccess(int index, String fileName) {
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/import_export_tasks/upload_success/",
                        HttpMethod.POST.vaLue(), "");

        JSONObject obj = new JSONObject();
        obj.put("id", index);
        obj.put("file_name", fileName);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (SUCCESS_CODE.equals(responseBean.getCode())) {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ResponseBean rsp = importTaskStu(index);
                    if (!("uploading").equals(rsp.getCode()) && !("uploading").equals(rsp.getCode())) {
                        timer.cancel();
                    }
                }
            }, 0, 500);
        } else {
            IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                    responseBean.getMessage(), NotificationType.ERROR));
        }
        return responseBean;
    }

    /**
     * 查询分片数接口
     *
     * @param id 导入任务id
     * @return 结果
     */
    public static ResponseBean getChunkNumber(int id) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/import_export_tasks/get_chunk_number/?id=" + id,
                HttpMethod.GET.vaLue(), "");
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 查看任务状态
     *
     * @param index 任务id
     * @return 结果
     */
    public static ResponseBean importTaskStu(int index) {
        String language = I18NServer.getCurrentLanguage().equals(Language.EN.code()) ? "en" : "zh";
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "sys-perf/api/v2.2/import_export_tasks/?language=" + language + "&id=" + index,
                        HttpMethod.GET.vaLue(), "");
        return TuningHttpsServer.INSTANCE.requestData(message);
    }
}
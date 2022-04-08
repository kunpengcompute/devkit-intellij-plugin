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
import com.huawei.kunpeng.hyper.tuner.model.javaperf.DataList;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.GcLog;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.Guardian;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.HasTaskUser;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.Members;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.MemoryDumpReprots;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.Owner;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.SamplingTaskInfo;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.ThreadDumpReports;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * sysperf获取数据类
 *
 * @since 2021-01-06
 */
public class JavaProjectServer {
    /**
     * 请求成功返回
     */
    public static final String SUCCESS_CODE = "SysPerf.Success";

    /**
     * 获取所有sysperf項目
     *
     * @return List
     */
    public static List<Members> getAllGuardian() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/api/guardians",
                HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        List<Members> list = new ArrayList<>();
        if (response == null) {
            return list;
        }

        // 处理获取到的后端数据
        List<Members> members = JSONObject.parseObject(response.getData(), Guardian.class).getMembers();
        if (members != null) {
            list.addAll(members);
        }
        return list;
    }

    /**
     * 获取所有sysperf任务的用户
     *
     * @return List
     */
    public static List<Owner> getAllUser() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/user/actions/list", HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        List<Owner> list = new ArrayList<>();
        if (response == null) {
            return list;
        }
        // 处理获取到的后端数据
        String userListStr = "{\"Owners\":" + response.getData() + "}";
        List<Owner> owners = JSONObject.parseObject(userListStr, HasTaskUser.class).getOwners();
        if (owners != null) {
            list.addAll(owners);
        }
        return list;
    }

    /**
     * 获取具体用户所有Record(管理员)
     *
     * @param id id
     * @return List
     */
    public static List<SamplingTaskInfo> getUserRecord(String id) {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/api/records/user",
                HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("userId", id);
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        List<SamplingTaskInfo> list = new ArrayList<>();
        if (response == null) {
            return list;
        }
        // 处理获取到的后端数据
        List<SamplingTaskInfo> members = JSONObject.parseObject(response.getData(), Members.class).getMembers();
        if (members != null) {
            list.addAll(members);
        }
        return list;
    }

    /**
     * 获取用户所有Record(普通用户)
     *
     * @return List
     */
    public static List<SamplingTaskInfo> getUserRecord() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/api/records",
                HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        List<SamplingTaskInfo> list = new ArrayList<>();
        if (response == null) {
            return list;
        }
        // 处理获取到的后端数据
        List<SamplingTaskInfo> members = JSONObject.parseObject(response.getData(), Members.class).getMembers();
        if (members != null) {
            list.addAll(members);
        }
        return list;
    }

    /**
     * 停止采样分析
     *
     * @param guardianId guardianId
     * @param recordId   recordId
     * @return 用户所有Record
     */
    public static ResponseBean stopSamplingTask(String guardianId, String recordId) {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/guardians/" + guardianId + "/cmds/stop-record",
                HttpMethod.POST.vaLue(), "");

        JSONObject obj = new JSONObject();
        obj.put("recordId", recordId);
        obj.put("timeout", 10000);
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        return TuningHttpsServer.INSTANCE.requestData(request);
    }

    /**
     * 获取用户所有内存转储(普通用户)
     *
     * @return List
     */
    public static List<MemoryDumpReprots> getUserMemoryDumpReports() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/heap/actions/list", HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        return dealMemoryDumpResponse(response);
    }

    /**
     * 获取用户所有内存转储(管理员)
     *
     * @param userId userId
     * @return List List
     */
    public static List<MemoryDumpReprots> getUserMemoryDumpReports(String userId) {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/heap/actions/query", HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("userId", userId);
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        return dealMemoryDumpResponse(response);
    }

    private static List<MemoryDumpReprots> dealMemoryDumpResponse(ResponseBean response) {
        List<MemoryDumpReprots> list = new ArrayList<>();
        if (response == null) {
            return list;
        }
        // 处理获取到的后端数据
        String dataJson = "{\"memoryData\":" + response.getData() + "}";
        List<MemoryDumpReprots> memoryData = JSONObject.parseObject(dataJson, DataList.class).getMemoryData();
        if (memoryData != null) {
            list.addAll(memoryData);
        }
        return memoryData;
    }

    /**
     * 获取用户所有gc日志(普通用户)
     *
     * @return List
     */
    public static List<GcLog> getUserDcLogsReports() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "/java-perf/api/gcLog/list",
                HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        return dealDcLogsResponse(response);
    }

    /**
     * 获取用户所有gc日志(管理员)
     *
     * @param userId userId
     * @return List List
     */
    public static List<GcLog> getUserDcLogsReports(String userId) {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "/java-perf/api/gcLog/query",
                HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("userId", userId);
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);

        return dealDcLogsResponse(response);
    }

    private static List<GcLog> dealDcLogsResponse(ResponseBean response) {
        List<GcLog> list = new ArrayList<>();
        if (response == null) {
            return list;
        }
        // 处理获取到的后端数据
        String dataJson = "{\"gcLogData\":" + response.getData() + "}";
        List<GcLog> gcLogData = JSONObject.parseObject(dataJson, DataList.class).getGcLogData();
        if (gcLogData != null) {
            list.addAll(gcLogData);
        }
        return list;
    }

    /**
     * 获取用户所有线程转储(普通用户)
     *
     * @return List
     */
    public static List<ThreadDumpReports> getUserThreadDumpReports() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/threadDump/list", HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        return dealThreadDumpResponse(response);
    }

    /**
     * 获取用户所有线程转储(管理员)
     *
     * @param userId userId
     * @return List List
     */
    public static List<ThreadDumpReports> getUserThreadDumpReports(String userId) {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/threadDump/query", HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("userId", userId);
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        return dealThreadDumpResponse(response);
    }

    private static List<ThreadDumpReports> dealThreadDumpResponse(ResponseBean response) {
        List<ThreadDumpReports> list = new ArrayList<>();
        if (response == null) {
            return list;
        }
        // 处理获取到的后端数据
        String dataJson = "{\"threadData\":" + response.getData() + "}";
        List<ThreadDumpReports> threadData = JSONObject.parseObject(dataJson, DataList.class).getThreadData();
        if (threadData != null) {
            list.addAll(threadData);
        }
        return list;
    }

    /**
     * 获取目标环境进程列表
     *
     * @param guardianId guardianId
     * @return 目标环境进程列表
     */
    public static String qryGuardianDetail(String guardianId) {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/api/guardians/"
                + guardianId, HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return "";
        }
        return response.getResponseJsonStr();
    }

    /**
     * 获取在线分析进程信息
     *
     * @return 在线分析进程信息
     */
    public static String queryGuardins() {
        RequestDataBean request = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "java-perf/guardians",
                HttpMethod.GET.vaLue(), "");
        ResponseBean response = TuningHttpsServer.INSTANCE.requestData(request);
        if (response == null) {
            return "";
        }
        return response.getResponseJsonStr();
    }
}

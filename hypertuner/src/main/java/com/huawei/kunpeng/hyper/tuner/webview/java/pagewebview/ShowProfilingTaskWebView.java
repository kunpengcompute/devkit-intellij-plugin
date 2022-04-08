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

package com.huawei.kunpeng.hyper.tuner.webview.java.pagewebview;

import com.huawei.kunpeng.hyper.tuner.action.javaperf.ReportThresholdAction;
import com.huawei.kunpeng.hyper.tuner.http.JavaProjectServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 展示在线分析
 *
 * @since 2021-01-05
 */
public class ShowProfilingTaskWebView extends JavaWebView {
    /**
     * 默认构造函数
     */
    public ShowProfilingTaskWebView(MessageBean message, boolean isExport) {
        Map<String, Object> queryParamMap = new HashMap<>();
        String router = "";
        if (isExport) {
            router = "/profiling/" + message.getCbid();
            String messageStr = message.getData();
            String jvmName = message.getCbid();
            String jvmId = message.getCmd();
            queryParamMap.put("downloadDatas", messageStr);
            queryParamMap.put("currentSelectJvm", jvmName);
            queryParamMap.put("jvmId", jvmId);
            queryParamMap.put("downloadProfile", true);
        } else {
            ReportThresholdAction reportThresholdAction = new ReportThresholdAction();
            JavaPerfToolWindowPanel.profilingMessage = message;
            Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
            router = data.get("router");
            Map<String, String> msgs = JsonUtil.getJsonObjFromJsonStr
                    (JSON.toJSONString(data.get("message")));
            for (String key : msgs.keySet()) {
                queryParamMap.put(key, msgs.get(key));
            }
            putQueryParams(queryParamMap, reportThresholdAction);
            JavaPerfToolWindowPanel.refreshProfilingNode(String.valueOf(data.get("viewTitle")), true);
        }

        // 系统信息
        if (IDEContext.getValueFromGlobalContext(null,
                BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            queryParamMap.put("OS", "windows");
        } else {
            queryParamMap.put("OS", "linux");
        }
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("queryParams", queryParamMap);
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                router, pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "ShowProfilingTaskWebView");
    }

    private void putQueryParams(Map<String, Object> queryParams, ReportThresholdAction reportThresholdAction) {
        JSONObject gcReportConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/gcLog/");
        queryParams.put("profilingStata", "running");
        Integer maxGcLogCount = Integer.valueOf(gcReportConfig.getString("maxGcLogCount"));
        queryParams.put("maxGcLogCount", maxGcLogCount);
        Integer gclogReportNum = JavaProjectServer.getUserDcLogsReports().size();
        queryParams.put("gclogReportNum", gclogReportNum);
        JSONObject heapReportConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings/heap/");
        Integer maxHeapCount = Integer.valueOf(heapReportConfig.getString("maxHeapCount"));
        queryParams.put("maxHeapCount", maxHeapCount);
        Integer heapReportNum = JavaProjectServer.getUserMemoryDumpReports().size();
        queryParams.put("heapReportNum", heapReportNum);
        JSONObject threadHistoryConfig = reportThresholdAction.getReportConfig("java-perf/api/tools/settings" +
                "/threadDump/");
        Integer maxThreadDumpCount = Integer.valueOf(threadHistoryConfig.getString("maxThreadDumpCount"));
        queryParams.put("maxThreadDumpCount", maxThreadDumpCount);
        Integer threadReportNum = JavaProjectServer.getUserThreadDumpReports().size();
        queryParams.put("threadReportNum", threadReportNum);
    }

    private String getLongStrList(String messageStr) {
        String str = messageStr;
        List<String> list = new ArrayList<>();
        truncatedStr(str, list);
        StringBuilder newStr = new StringBuilder();
        for (String s : list) {
            newStr.append(s.replaceAll("\\\\\"", "#slash#")
                    .replaceAll("\"", "#single#").replaceAll(":", "#colon#")
                    .replaceAll("\\n", "&n@"));
        }
        return newStr.toString();
    }

    private void truncatedStr(String str, List<String> list) {
        String splitStr = str;
        if (splitStr == null) {
            Logger.error("profile json is null.");
            return;
        }
        while (true) {
            if (splitStr.length() > 10000) {
                list.add(splitStr.substring(0, 10000));
                splitStr = splitStr.substring(10000, splitStr.length());
            } else {
                list.add(splitStr);
                break;
            }
        }
    }
}

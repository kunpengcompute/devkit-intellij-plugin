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

package com.huawei.kunpeng.porting.webview.pagewebview;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageDataBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import java.util.HashMap;
import java.util.Map;

/**
 * porting增强功能报告详情页面
 *
 * @since 2021/1/5
 */
public class EnhancedReportWebView extends PortingWebView {
    /**
     * workspace key
     */
    public static final String WORKSPACE_KEY = "workspace";

    /**
     * command key
     */
    public static final String COMMAND_KEY = "command";

    /**
     * selected key
     */
    public static final String SELECTED_KEY = "selected";

    /**
     * 默认构造函数
     */
    public EnhancedReportWebView(String taskId, String taskType) {
        // 组装与webview交互的message
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intellijFlag", true);
        queryParams.put("taskId", taskId);
        queryParams.put("taskType", taskType);
        if (IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            queryParams.put("OS", "windows");
        } else {
            queryParams.put("OS", "linux");
        }
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        createQueryParams(taskType, queryParams, responseBean);
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("queryParams", queryParams);
        NavigatorPageBean<NavigatorPageDataBean> navigatorPage =
            MessageRouterHandler.generateNavigatorPage("/navigate", "/enchanceReport",
                pageParams, sessionBean);
        super.createWebView(navigatorPage, null, taskId);
    }

    private void createQueryParams(String taskType, Map<String, Object> queryParams, ResponseBean responseBean) {
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (taskType.equals(TaskType.BYTE_ALIGN.value())) {
            queryParams.put(WORKSPACE_KEY,
                IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, WORKSPACE_KEY));
            queryParams.put("filePath", data.get("scan_result"));
            queryParams.put(COMMAND_KEY,
                IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, COMMAND_KEY));
            queryParams.put(SELECTED_KEY,
                IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, SELECTED_KEY));
            queryParams.put("resp", responseBean.getResponseJsonStr().replace("\"", "'"));
            return;
        }
        if (taskType.equals(TaskType.MIGRATION_PRE_CHECK.value())
                || taskType.equals(TaskType.CACHE_LINE_ALIGNMENT.value())) {
            queryParams.put(WORKSPACE_KEY,
                IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, WORKSPACE_KEY));
            Map<String, Object> results = JsonUtil.getJsonObjFromJsonStr(data.get("scan_result").toString());
            queryParams.put("filePath", results.keySet());
            queryParams.put("resp", responseBean.getResponseJsonStr().replace("\"", "'"));
            return;
        }
        if (taskType.equals(TaskType.WEAK_CHECK.value())) {
            if (RespondStatus.REPORT_NOT_NEW.value().equals(responseBean.getRealStatus())) {
                // 重新设置报告状态以正常报告打开
                responseBean.setResponseJsonStr(
                    responseBean.getResponseJsonStr().replace("\"status\":1", "\"status\":0"));
            }
            queryParams.put("resp", responseBean.getResponseJsonStr().replace("\"", "'")
                .replace("\\\\n", ""));
            return;
        }
        queryParams.put("resp", responseBean.getResponseJsonStr().replace("\"", "'"));
    }
}

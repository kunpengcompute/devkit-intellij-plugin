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

package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.OpenNewPageEditor;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 打开新的页面
 * 锁与等待分析类型
 *
 * @since 2021-06-05
 */
public class OpenNewPageWebView extends WebView {
    /**
     * 默认构造函数
     */
    public OpenNewPageWebView() {
        MessageBean messageBean = OpenNewPageEditor.message;
        String messageDataStr = messageBean.getData();

        Object messageDataObj = JSON.parse(messageDataStr);
        if (!(messageDataObj instanceof JSONObject)) {
            return;
        }
        JSONObject messageDataJSONObj = (JSONObject) messageDataObj;
        JSONObject message2JsonObj = messageDataJSONObj.getJSONObject("message");

        // 获取 header
        Object headerObj = JSON.parse(message2JsonObj.getString("headers"));
        if (!(headerObj instanceof JSONArray)) {
            return;
        }
        JSONArray headerArr = (JSONArray) headerObj;

        // 获取 functionDetails
        Object functionDetails = JSON.parse(message2JsonObj.getString("functionDetails"));
        if (!(functionDetails instanceof JSONObject)) {
            return;
        }
        JSONObject functionDetailsJSONObj = (JSONObject) functionDetails;
        message2JsonObj.remove("headers");
        message2JsonObj.put("headers", headerArr);
        message2JsonObj.remove("functionDetails");
        message2JsonObj.put("functionDetails", functionDetailsJSONObj);

        // 组装与webview交互的message
        Map<String, Object> queryParams = getQueryParamsMap();

        String str = JSON.toJSONString(message2JsonObj).replaceAll("\\\\\"",
                "#slash#")
                .replaceAll("\"", "#single#").replaceAll(":", "#colon#")
                .replaceAll("\\n", "&n@").replaceAll("\\\\", "#LINE#");
        String message2JSONStr = JSONObject.toJSONString(str);

        message2JSONStr = message2JSONStr.substring(1, message2JSONStr.length() - 1);
        queryParams.put("message", message2JSONStr);

        // 系统信息
        if (IDEContext.getValueFromGlobalContext(null,
                BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            queryParams.put("OS", "windows");
        } else {
            queryParams.put("OS", "linux");
        }
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("queryParams", queryParams);
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                "/addfunction", pageParams, sessionBean);

        super.createWebView(navigatorPage, null, "OpenNewPageWebView"
                + LeftTreeSysperfPanel.getSelectProject().getProjectName());
    }

    @NotNull
    private Map<String, Object> getQueryParamsMap() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("response", "###");
        queryParams.put("intelliJFlag", true);
        queryParams.put("isLoginRouter", true);
        queryParams.put("projectId", LeftTreeSysperfPanel.getSelectProject().getProjectId());
        queryParams.put("projectName", LeftTreeSysperfPanel.getSelectProject().getProjectName());
        queryParams.put("operation", "createTask");
        String panelId = LeftTreeSysperfPanel.getSelectProject().getProjectName() + "--"
                + new Date().getTime();
        queryParams.put("panelId", panelId);
        queryParams.put("viewTitle", "Locks and Waits");
        return queryParams;
    }

}

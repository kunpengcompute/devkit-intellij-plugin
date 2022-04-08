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

import com.huawei.kunpeng.hyper.tuner.model.javaperf.TaskSelfInfo;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.SystemOS;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 展示采样分析
 *
 * @since 2021-01-05
 */
public class ShowSamplingTaskWebView extends JavaWebView {
    /**
     * 默认构造函数
     */
    public ShowSamplingTaskWebView(MessageBean message, boolean isCreate) {
        Map<String, Object> queryParams = new HashMap<>();
        String router;
        if (isCreate) {
            Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
            router = data.get("router");
            String messageStr = String.valueOf(data.get("message"));
            String sendMessage = String.valueOf(JsonUtil.getJsonObjFromJsonStr(messageStr
            ).get("sendMessage"));
            sendMessage = sendMessage.replaceAll("\"", "%").replaceAll(":", "#");
            queryParams.put("sendMessage", sendMessage);
        } else {
            router = "sampling/" + JavaPerfToolWindowPanel.selectSamplingTask.getName();
            // 组装与webview交互的message
            queryParams.put("response", "###");
            queryParams.put("intelliJFlag", true);
            queryParams.put("isLoginRouter", true);
            TaskSelfInfo selfInfo = new TaskSelfInfo();
            selfInfo.setSelfInfo(JavaPerfToolWindowPanel.selectSamplingTask);
            String sendMessage = JSON.toJSONString(selfInfo)
                    .replaceAll(":", "#");
            sendMessage = sendMessage.replace("\"", "%");
            queryParams.put("sendMessage", sendMessage);
        }
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
                router, pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "ShowSamplingTaskWebView"
                + UUID.randomUUID());
    }
}

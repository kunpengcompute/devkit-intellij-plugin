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

import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageDataBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * porting增强功能任务创建页面
 *
 * @since 2021/1/5
 */
public class EnhancedFunctionWebView extends PortingWebView {
    /**
     * 默认构造函数
     */
    public EnhancedFunctionWebView(HashMap<String, String> param) {
        // 组装与webview交互的message
        Map<String, Object> pageParams = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intellijFlag", true);
        pageParams.put("queryParams", queryParams);
        if (!param.isEmpty()) {
            queryParams.putAll(param);
        }
        NavigatorPageBean<NavigatorPageDataBean> navigatorPage = MessageRouterHandler.generateNavigatorPage(
                "/navigate", "/PortingPre-check", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "PortingPre-check");
    }
}

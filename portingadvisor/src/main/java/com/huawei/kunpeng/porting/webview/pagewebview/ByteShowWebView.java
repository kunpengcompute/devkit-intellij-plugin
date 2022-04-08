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
 * 字节对齐建议查看页面
 *
 * @since 2021/1/9
 */
public class ByteShowWebView extends PortingWebView {
    /**
     * 默认构造函数
     */
    public ByteShowWebView(String reportId, String diffPath) {
        // 组装与webview交互的message
        Map<String, Object> pageParams = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intellijFlag", true);
        queryParams.put("reportId", reportId);
        queryParams.put("diffPath", diffPath);
        pageParams.put("queryParams", queryParams);
        NavigatorPageBean<NavigatorPageDataBean> navigatorPage = MessageRouterHandler.generateNavigatorPage(
                "/navigate", "/bytereport", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "bytereport");
    }
}

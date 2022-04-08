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

package com.huawei.kunpeng.intellij.js2java.handler;

import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageDataBean;
import com.huawei.kunpeng.intellij.common.bean.WebSessionBean;

import java.util.Date;
import java.util.Map;

/**
 * js与java信息交互处理器
 *
 * @since 1.0.0
 */
public class MessageRouterHandler {
    /**
     * 生成NavigatorPage信息，打开webview新页面时的交互信息
     *
     * @param cmd js执行的函数
     * @param page 需要跳转的页面
     * @param pageParams 页面参数
     * @param webSession webSession
     * @return NavigatorPageBean
     */
    public static NavigatorPageBean<NavigatorPageDataBean> generateNavigatorPage(
            String cmd, String page, Map<String, Object> pageParams, WebSessionBean webSession) {
        NavigatorPageDataBean data = new NavigatorPageDataBean(pageParams, page, webSession);
        return new NavigatorPageBean<>(cmd, data, String.valueOf(new Date().getTime()));
    }
}

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
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageDataBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.listener.IDEThemeListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 源码扫描Webview页面
 *
 * @since 2021-01-05
 */
public class PortingSourceWebView extends PortingWebView {
    /**
     * 默认构造函数
     */
    public PortingSourceWebView() {
        // 组装与webview交互的message
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("response", "###");
        queryParams.put("intelliJFlag", true);
        queryParams.put("isLoginRouter", true);
        // 判断当前用户是否屏蔽gcc检查
        if (PortingCommonUtil.isSignEnvPrompt()) {
            queryParams.put("gccCheckShow", checkGCC());
        } else {
            queryParams.put("gccCheckShow", null);
        }
        // 系统信息
        if (IDEContext.getValueFromGlobalContext(null, BaseCacheVal.SYSTEM_OS.vaLue()) == SystemOS.WINDOWS) {
            queryParams.put("OS", "windows");
        } else {
            queryParams.put("OS", "linux");
        }
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("queryParams", queryParams);
        // 主题信息
        if (IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.LIGHT_THEME.vaLue())) {
            queryParams.put("currentTheme", IDEThemeListener.LIGHT_THEME);
        } else {
            queryParams.put("currentTheme", IDEThemeListener.DARCULA_THEME);
        }
        NavigatorPageBean<NavigatorPageDataBean> navigatorPage = MessageRouterHandler.generateNavigatorPage(
                "/navigate", "/home", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "home");
    }

    /**
     * gcc版本检查
     *
     * @return true为版本过低或缺失，反之则然
     */
    private boolean checkGCC() {
        RequestDataBean request = new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/tasks/checkasmenv/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        if (responseBean != null && RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            return false;
        } else {
            return true;
        }
    }
}

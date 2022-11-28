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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 性能分析工具IDE插件登录WebView入口
 *
 * @since 2021-01-05
 */
public class IDELoginWebView extends WebView {
    /**
     * 默认构造函数
     */
    public IDELoginWebView(String localPort) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        List portConfig = JsonUtil.getValueIgnoreCaseFromMap(config, ConfigProperty.PORT_CONFIG.vaLue(), List.class);
        String ip = null;
        String port = null;
        String localPortCache = null;
        if (!ValidateUtils.isEmptyCollection(portConfig)) {
            if (portConfig.get(0) instanceof Map) {
                Map configDef = (Map) portConfig.get(0);
                ip = JsonUtil.getValueIgnoreCaseFromMap(configDef, "ip", String.class);
                port = JsonUtil.getValueIgnoreCaseFromMap(configDef, "port", String.class);
                localPortCache = JsonUtil.getValueIgnoreCaseFromMap(configDef, "localPort", String.class);
            }
        }
        if (localPort == null) {
            localPort = localPortCache;
        }
        // 组装与webview交互的message
        Map<String, Object> pageParams = new HashMap<>();
        pageParams.put("ip", ip);
        pageParams.put("port", port);
        pageParams.put("localPort", localPort);
        boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        String currentTheme = "dark";
        if (isLightThemeInContext) {
            currentTheme = "light";
        }
        pageParams.put("currentTheme", currentTheme);
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                "/", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "IDELoginWebView");
    }

    /**
     * 要覆盖的index.html位置
     * @return login页面所用index.html位置
     */
    @Override
    public String getIndexHtmlKey() {
        return TuningIDEConstant.TUNING_LOGIN_WEB_VIEW_INDEX_HTML;
    }
    @Override
    public String getWebViewIndex() {
        return TuningIDEContext.getLoginWebViewIndex();
    }
}

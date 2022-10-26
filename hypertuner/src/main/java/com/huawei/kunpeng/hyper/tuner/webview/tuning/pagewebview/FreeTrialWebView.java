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
 * 免费试用webview
 *
 * @since 2022-10-26
 */
public class FreeTrialWebView extends WebView{

    public FreeTrialWebView(String localPort) {
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
}

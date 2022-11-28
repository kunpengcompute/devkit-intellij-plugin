package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置服务器WebView页面
 *
 * @since 2022-10-25
 */
public class ConfigureServerWebView extends WebView {
    /**
     * 默认构造函数
     */
    public ConfigureServerWebView() {
        // 组装与webview交互的message
        System.out.println("init configure server webview");
        Map<String, Object> pageParams = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intellijFlag", true);
        pageParams.put("queryParams",queryParams);
        boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        String currentTheme = isLightThemeInContext ? "light" : "dark";
        pageParams.put("currentTheme", currentTheme);
        NavigatorPageBean navigatorPage =
                MessageRouterHandler.generateNavigatorPage("/navigate", "/config",
                        pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "Configure Server");
    }
}

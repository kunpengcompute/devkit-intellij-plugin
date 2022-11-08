package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.Map;

public class UninstallWebView extends WebView {
    /**
     * 默认构造函数
     */
    public UninstallWebView() {
        Map<String, Object> pageParams = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intellijFlag", true);
        pageParams.put("queryParams",queryParams);
        boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        String currentTheme = isLightThemeInContext ? "light" : "dark";
        pageParams.put("currentTheme", currentTheme);
        NavigatorPageBean navigatorPage =
                MessageRouterHandler.generateNavigatorPage("/navigate", "/uninstall",
                        pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "Uninstall Hyper Tuner");
    }
}

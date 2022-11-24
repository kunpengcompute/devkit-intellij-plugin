package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置指引WebView页面
 *
 * @since 2022-11-23
 */
public class ConfigGuideWebView extends WebView {
    /**
     * 默认构造函数
     */
    public ConfigGuideWebView() {
        System.out.println("init config guide webview");
        Map<String, Object> pageParams = new HashMap<>();
        boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        String currentTheme = isLightThemeInContext ? "light" : "dark";
        pageParams.put("currentTheme", currentTheme);
        NavigatorPageBean navigatorPage =
                MessageRouterHandler.generateNavigatorPage("/navigate", "/guide",
                        pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "Configure Guide");
    }
}

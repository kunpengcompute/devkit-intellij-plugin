package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.github.markusbernhardt.proxy.util.Logger;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.Map;

public class ErrorInstructionWebView extends WebView {

    /**
     * 错误指示webview页面
     * @param queryParams 错误指示页面需要的参数
     *
     * @since 2022-11-26
     */
    public ErrorInstructionWebView(Map<String, String> queryParams) {
        System.out.println("init error instruction webview");
        Map<String, Object> pageParams = new HashMap<>();
        boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        String currentTheme = isLightThemeInContext ? "light" : "dark";
        queryParams.put("intellijFlag", "true");
        pageParams.put("currentTheme", currentTheme);
        pageParams.put("queryParams", queryParams);

        NavigatorPageBean navigatorPage =
                MessageRouterHandler.generateNavigatorPage("/navigate", "/errorInstruction",
                        pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "Error Instruction");
    }
}

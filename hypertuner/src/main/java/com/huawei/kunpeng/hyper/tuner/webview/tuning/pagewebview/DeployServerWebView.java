package com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview;

import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.handler.MessageRouterHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 部署服务器webview
 *
 * @since 2022-10-26
 */
public class DeployServerWebView extends WebView{

    public DeployServerWebView() {
        // 组装与webview交互的message
        Map<String, Object> queryParams = new HashMap<>();
        Map<String, Object> pageParams = new HashMap<>();
        queryParams.put("intellijFlag", true);
        pageParams.put("queryParams",queryParams);
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                "/install", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "DeployServerWebView");
    }
}

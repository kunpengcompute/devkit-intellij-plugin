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

    public FreeTrialWebView() {
        // 组装与webview交互的message
        Map<String, Object> pageParams = new HashMap<>();
        boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        String currentTheme = "dark";
        if (isLightThemeInContext) {
            currentTheme = "light";
        }
        pageParams.put("currentTheme", currentTheme);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("intelliJFlag", true);
        pageParams.put("queryParams", queryParams);
        NavigatorPageBean navigatorPage = MessageRouterHandler.generateNavigatorPage("/navigate",
                "/freeTrialProcessEnvironment", pageParams, sessionBean);
        super.createWebView(navigatorPage, null, "FreeTrialWebView");
    }
}

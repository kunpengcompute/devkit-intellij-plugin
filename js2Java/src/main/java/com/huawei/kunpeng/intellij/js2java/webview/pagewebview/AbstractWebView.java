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

package com.huawei.kunpeng.intellij.js2java.webview.pagewebview;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.exception.IDEException;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.util.JcefDevToolsUtil;
import com.huawei.kunpeng.intellij.js2java.webview.HandlerAction;

import com.esotericsoftware.minlog.Log;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefClient;

import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefFocusHandlerAdapter;
import org.cef.handler.CefMessageRouterHandler;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 自定义webview
 *
 * @since 1.0.0
 */
public abstract class AbstractWebView {
    /**
     * title 对应打开的webview page页面
     */
    private static Map<String, AbstractWebView> openPageMap = new HashMap<>();

    /**
     * 内容承载面板
     */
    protected JPanel jPanel;

    /**
     * cefBrowser浏览器实例
     */
    protected CefBrowser cefBrowser;

    /**
     * intellij cefApp对象
     */
    protected JBCefApp jbCefApp;

    /**
     * intellij cefClient客户端实例
     */
    protected JBCefClient jbCefClient;

    /**
     * 页面title标签的值
     */
    private String title;

    /**
     * 根据title获取打开的webview page
     *
     * @param title title
     * @return webview page
     */
    public static AbstractWebView getOpenPageByTitle(String title) {
        return openPageMap.get(title);
    }

    /**
     * java发送消息到js页面
     *
     * @param message 发送的消息Json串，message = { cmd: 'navigate', data: {page:'/home',pageParam:'',token:''}};
     */
    public void sendMessageToJs(String message) {
        cefBrowser.executeJavaScript("window.JavaMessageBridge" + "(" + message + ")", null, 0);
    }

    /**
     * 页面发送消息到java
     */
    public void jsToJavaActive() {
        CefMessageRouter.CefMessageRouterConfig cefMessageRouterConfig = new CefMessageRouter.CefMessageRouterConfig(
                "sendMessageToJava", "javaCancel");
        CefMessageRouter cmr = CefMessageRouter.create(cefMessageRouterConfig);
        if (cmr == null) {
            return;
        }
        addHandlerAction(cmr, (message) -> {
            if (message == null) {
                return;
            }
            // 接收并解析消息
            MessageBean messageBean = JsonUtil.jsonToDataModel(message, MessageBean.class);
            if (messageBean == null) {
                return;
            }
            messageBean.setMessageJsonStr(message);
            routingCmd(messageBean);
        });
        jbCefClient.getCefClient().addMessageRouter(cmr);
    }

    private void addHandlerAction(CefMessageRouter router, HandlerAction handlerAction) {
        if (handlerAction == null) {
            return;
        }
        router.addHandler(new CefMessageRouterHandler() {
            @Override
            public boolean onQuery(CefBrowser browser, CefFrame cefFrame, long requestNo, String queryName,
                                   boolean persistent, CefQueryCallback cefQueryCallback) {
                handlerAction.handlerAction(queryName);
                return true;
            }

            @Override
            public void onQueryCanceled(CefBrowser cefBrowser, CefFrame cefFrame, long longVar) {
            }

            @Override
            public void setNativeRef(String str, long longVar) {
            }

            @Override
            public long getNativeRef(String str) {
                return 0;
            }
        }, true);
    }

    /**
     * 覆盖重写index页面，传入indexHtmlNew，则以indexHtmlNew为准
     *
     * @param jsonStr      Json串, top.navigatorPage信息
     * @param indexHtmlNew 新的index页面
     * @return String
     */
    protected String overrideIndexPage(String jsonStr, String indexHtmlNew) {
        String indexHtml = "";
        if (StringUtil.stringIsEmpty(indexHtmlNew)) {
            // 获取index.html内容
            indexHtml = IDEContext.getValueFromGlobalContext(null, getIndexHtmlKey());

            // 替换重载内容
            indexHtml = indexHtml.replaceFirst("top\\.navigatorPage", "top\\.navigatorPage = " + jsonStr);
        } else {
            indexHtml = indexHtmlNew;
        }

        // 将重载写内容写入index.html文件
        Boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                BaseCacheVal.LIGHT_THEME.vaLue());
        if (isLightThemeInContext != null && isLightThemeInContext) {
            indexHtml = indexHtml.replaceFirst("<body class=\"vscode-dark\">", "<body class=\"vscode-light\">");
            indexHtml = indexHtml.replaceFirst("<body class=\"vscode-dark intellij-dark\">",
                    "<body class=\"vscode-light intellij-light\">");
        }

        FileUtil.writeFile(indexHtml, getWebViewIndex(), null);
        return indexHtml;
    }

    /**
     * getWebViewIndex
     *
     * @return getWebViewIndex
     */

    public String getWebViewIndex() {
        return CommonUtil.getWebViewIndex();
    }

    /**
     * getIndexHtmlKey
     *
     * @return getIndexHtmlKey
     */

    public String getIndexHtmlKey() {
        return IDEConstant.PORTING_WEB_VIEW_INDEX_HTML;
    }

    /**
     * 设置字体
     *
     * @param indexHtml 源indexHTML
     * @return 修改之后的newIndexHtml
     */
    protected String setFontFamily(String indexHtml) {
        String newIndexHtml = indexHtml;
        if (I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            newIndexHtml = newIndexHtml.replaceFirst("</head>",
                    "<style> " + "@font-face {" + "font-family: huawei sans;" + "src:url(HuaweiSans-Regular.ttf);" + "}"
                            + "</style></head>");
        }
        if (I18NServer.getCurrentLanguage().equals(Language.ZH.code())) {
            newIndexHtml = newIndexHtml.replaceFirst("</head>",
                    "<style> "
                            + "body {font-family: \"HuaweiFont\", \"Helvetica\", \"Arial\", \"PingFangSC-Regular\", " +
                            "\"Hiragino Sans GB\", \"Microsoft YaHei\", \"微软雅黑\", \"Microsoft JhengHei\" !important;"
                            + "}</style></head>");
        }
        return newIndexHtml;
    }

    /**
     * 为webview注册主题切换监听
     */
    private void addThemeChangeListener() {
        final PropertyChangeListener propertyChangeListener = evt -> {
            boolean isLightThemeInContext = IDEContext.getValueFromGlobalContext(CommonUtil.getProjectName(),
                    BaseCacheVal.LIGHT_THEME.vaLue());
            String message = "dark";
            if (!isLightThemeInContext) {
                message = "light";
            }
            // 给页面发送消息
            cefBrowser.executeJavaScript("switchTheme('" + message + "')", null, 0);
        };
        // 给webview的component注册监听
        cefBrowser.getUIComponent().addPropertyChangeListener("background", propertyChangeListener);
    }

    /**
     * 获取webview结构的响应信息
     *
     * @param response 响应信息
     * @return String
     */
    protected String getResponseForWebView(ResponseBean response) {
        response.setResponseJsonStr(response.getResponseJsonStr().replaceAll("\\\\\\\\", "/")
                .replaceAll("//", "/"));
        return response.getResponseJsonStr();
    }

    /**
     * 获取页面内容
     *
     * @return JComponent
     */
    public JComponent getContent() {
        return jPanel;
    }

    /**
     * 关闭该页面jbCefClient实例
     */
    public void dispose() {
        if (title != null) {
            // 关闭窗口删除缓存webview页面
            openPageMap.remove(title);
        }
        if (jbCefClient != null) {
            jbCefClient.dispose();
        }
    }

    /**
     * 提示版本不匹配
     *
     * @param project project
     */
    private void alertVersionNotMatch(Project project) {
        if (OS.isWindows()) {
            IDENotificationUtil.sendMessage(CommonI18NServer.toLocale("version.is.not.window.support"),
                    NotificationType.INFORMATION, project);
        } else if (OS.isLinux()) {
            IDENotificationUtil.sendMessage(CommonI18NServer.toLocale("version.is.not.linux.support"),
                    NotificationType.INFORMATION, project);
        } else if (OS.isMacintosh()) {
            IDENotificationUtil.sendMessage(CommonI18NServer.toLocale("version.is.not.mac.support"),
                    NotificationType.INFORMATION, project);
        } else {
            IDENotificationUtil.sendMessage(CommonI18NServer.toLocale("version.is.not.support"),
                    NotificationType.INFORMATION, project);
        }
    }

    /**
     * create web page for each scene
     *
     * @param navigatorPage navigatorPage params
     * @param response      response the result of other interface
     */
    protected void createWebView(NavigatorPageBean navigatorPage, ResponseBean response) {
        createWebView(navigatorPage, response, null);
    }

    /**
     * create web page for each scene
     *
     * @param navigatorPage navigatorPage params
     * @param response      response the result of other interface
     * @param title         webview page title
     */
    protected void createWebView(NavigatorPageBean navigatorPage, ResponseBean response, String title) {
        jPanel = new JPanel(new BorderLayout());
        jPanel.setOpaque(true);
        if (!JBCefApp.isSupported()) {
            alertVersionNotMatch(CommonUtil.getDefaultProject());
            return;
        }
        jbCefApp = JBCefApp.getInstance();
        jbCefClient = jbCefApp.createClient();
        // 注册js交互事件
        jsToJavaActive();
        // 加载页面内容
        loadPageContent(navigatorPage, response, title);
        // 装载页面
        cefBrowser = jbCefClient.getCefClient().createBrowser("file:///" + getWebViewIndex(), false, false);
        // 添加焦点处理
        jbCefClient.getCefClient().addFocusHandler(new CefFocusHandlerAdapter() {
            @Override
            public boolean onSetFocus(CefBrowser browser, FocusSource source) {
                if (source == FocusSource.FOCUS_SOURCE_NAVIGATION) {
                    if (SystemInfoRt.isWindows) {
                        browser.setFocus(false);
                    }
                    return true;
                }
                if (SystemInfoRt.isLinux) {
                    browser.getUIComponent().requestFocus();
                } else {
                    browser.getUIComponent().requestFocusInWindow();
                }
                return false;
            }
        });
        JcefDevToolsUtil.registerJcefDevTools(jbCefClient.getCefClient(), cefBrowser, CommonUtil.getProjectName());
        jPanel.add(cefBrowser.getUIComponent(), BorderLayout.CENTER);
        addThemeChangeListener();
        // 缓存webview页面
        if (title != null) {
            this.title = title;
            addOpenPageWebView(title);
        }
    }

    private void addOpenPageWebView(String title) {
        openPageMap.put(title, this);
    }

    /**
     * 加载页面内容
     *
     * @param navigatorPage navigatorPage params
     * @param response      response the result of other interface
     * @param title         wwebview page title
     */
    private void loadPageContent(NavigatorPageBean navigatorPage, ResponseBean response, String title) {
        try {
            // 自定义重写index.html
            String indexHtml = IDEContext.getValueFromGlobalContext(null, getIndexHtmlKey());
            List<String> list = getLongStrList(navigatorPage);
            int i = 0;
            for (String str : list) {
                str = Matcher.quoteReplacement(str);
                if (list.size() == 1) {
                    System.out.println("list size is 1!!!");
                    indexHtml = indexHtml.replaceFirst("self\\.navigatorPage",
                            "self\\.navigatorPage = " + str);
                    break;
                }
                if (i == 0) {
                    indexHtml = indexHtml.replaceFirst("self\\.navigatorPage",
                            "self\\.navigatorPage = " + str + "IntellIJStr");
                    i++;
                } else {
                    indexHtml = indexHtml.replace("IntellIJStr", str + "IntellIJStr");
                }
            }
            indexHtml = indexHtml.replaceFirst("IntellIJStr", "");
            if (title != null) {
                if (indexHtml.lastIndexOf("porting-advisor") > 0) {
                    indexHtml = indexHtml.replaceFirst("porting-advisor", title);
                } else if (indexHtml.lastIndexOf("Kunpeng Hyper Tuner") > 0) {
                    indexHtml = indexHtml.replaceFirst("Kunpeng Hyper Tuner", title);
                } else if (indexHtml.lastIndexOf("Kunpeng Tuning Kit") > 0) {
                    indexHtml = indexHtml.replaceFirst("Kunpeng Tuning Kit", title);
                } else {
                    Log.info("index.html doesn't have title! ");
                }
            }
            if (!Objects.isNull(response)) {
                indexHtml = indexHtml.split("###")[0] +
                        response.getResponseJsonStr().replaceAll("\"", "\\\\\"") + indexHtml.split("###")[1];
            }
            indexHtml = setFontFamily(indexHtml);
            // 重载index.html页面
            overrideIndexPage(null, indexHtml);
        } catch (IDEException ideException) {
            Logger.error("WebView loadPageContent error,{}", ideException.getErrorMessage());
        }
    }

    /**
     * indexHtmlReplace
     *
     * @param indexHtml     indexHtml
     * @param navigatorPage navigatorPage
     * @return indexHtmlReplace
     */

    public String indexHtmlReplace(String indexHtml, NavigatorPageBean navigatorPage) {
        AtomicBoolean status = new AtomicBoolean(true);
        return StreamSupport.stream(Splitter.on("\n").split(indexHtml).spliterator(), false)
                .map(lineStr -> {
                    if (status.get() && lineStr.contains("self\\.navigatorPage")) {
                        String newLineStr = lineStr.replaceFirst("self\\.navigatorPage = ",
                                new Gson().toJson(navigatorPage));
                        status.set(false);
                        return newLineStr;
                    } else {
                        return lineStr;
                    }
                }).collect(Collectors.joining("\n"));
    }

    /**
     * getLongStrList
     *
     * @param navigatorPage navigatorPage
     * @return List
     */
    public List<String> getLongStrList(NavigatorPageBean navigatorPage) {
        String str = JsonUtil.getJsonStrFromJsonObj(navigatorPage);
        List<String> list = new ArrayList<>();
        while (true) {
            if (str.length() > 10000) {
                list.add(str.substring(0, 10000));
                str = str.substring(10000);
            } else {
                list.add(str);
                break;
            }
        }
        return list;
    }

    /**
     * 路由执行具体的函数
     *
     * @param messageBean messageBean
     */
    public abstract void routingCmd(MessageBean messageBean);
}

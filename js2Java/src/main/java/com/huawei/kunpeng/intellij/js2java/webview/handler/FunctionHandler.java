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

package com.huawei.kunpeng.intellij.js2java.webview.handler;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.bean.NavigatorPageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * js与java交互的函数处理器
 *
 * @since 1.0.0
 */
public class FunctionHandler {
    /**
     * 执行cmd函数
     *
     * @param cmd 函数名
     * @param message 数据
     * @param module 模块
     */
    public void executeFunction(String cmd, MessageBean message, String module) {
        // 存储类中的方法名
        Set<String> methodSet = new HashSet<>();
        if (ValidateUtils.isEmptyCollection(methodSet)) {
            for (Method method : this.getClass().getMethods()) {
                methodSet.add(method.getName());
            }
        }
        try {
            if (methodSet.contains(cmd)) {
                this.getClass().getMethod(cmd, MessageBean.class, String.class).invoke(this, message, module);
            } else {
                Logger.error("executeFunction error, not function:{}", cmd);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Logger.error("executeFunction error.");
        }
    }

    /**
     * 回调信息至js
     *
     * @param cmd 函数名
     * @param cbid 回调函数id
     * @param data 数据
     */
    public static void invokeCallback(String cmd, String cbid, String data) {
        AbstractWebView page;
        if (cbid.contains("#")) {
            String title = cbid.split("#", -1)[1];
            page = AbstractWebView.getOpenPageByTitle(title);
        } else {
            // 获取当前激活状态的文件窗口
            Project project = CommonUtil.getDefaultProject();
            VirtualFile file = IDEFileEditorManager.getInstance(project).getSelectFile();
            WebFileEditor webViewPage = AbstractWebFileProvider.getWebViewPage(project, file);
            // 避免任务过程关闭WebView造成空指针
            if (webViewPage == null) {
                return;
            }
            page = webViewPage.getWebView();
        }
        if (page == null) {
            Logger.error("get pageInfo error.");
            return;
        }
        // 回调
        page.sendMessageToJs(JsonUtil.getJsonStrFromJsonObj(
                NavigatorPageBean.builder()
                        .cmd(cmd)
                        .cbid(cbid)
                        .data(data)
                        .build()));
    }

    /**
     * webview打开页面超链接
     *
     * @param message 数据
     * @param module 模块
     */
    public void openHyperlinks(MessageBean message, String module) {
        Logger.info("openHyperlinks start.");

        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        URI uri = null;
        try {
            uri = new URI(data.get("hyperlinks").replaceAll("/n", ""));
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException e) {
            Logger.error("openHyperlinks URISyntaxException.");
        } catch (IOException e) {
            Logger.error("openHyperlinks IOException.");
        }

        Logger.info("openHyperlinks end.");
    }
}

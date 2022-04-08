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

package com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.webview.TuningWebFileEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.OpenNewPageWebView;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pagewebview.WebView;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 锁与等待类型
 * 打开新页面
 *
 * @since 2021-06-06
 */
public class OpenNewPageEditor extends TuningWebFileEditor {
    /**
     * 函数详情 页面 所需 参数
     */
    public static MessageBean message;
    private final OpenNewPageWebView openNewPageWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描Webview虚拟文件
     */
    public OpenNewPageEditor(VirtualFile file) {
        currentFile = file;
        openNewPageWebView = new OpenNewPageWebView();
    }

    /**
     * 获取webview 实例
     *
     * @return openNewPageWebView
     */
    @Override
    public WebView getWebView() {
        return openNewPageWebView;
    }

    /**
     * 获取组件 实例
     *
     * @return openNewPageWebView
     */
    @Override
    public JComponent getComponent() {
        return openNewPageWebView.getContent();
    }

    /**
     * 关闭该页面
     */
    @Override
    public void dispose() {
        super.dispose();
        openNewPageWebView.dispose();
    }

    /**
     * 查看函数信息入口
     *
     * @param message2 页面所悟需要的参数
     */
    public static void openPage(MessageBean message2) {
        String messageDataStr = message2.getData();
        Object messageDataObj = JSON.parse(messageDataStr);
        if (!(messageDataObj instanceof JSONObject)) {
            return;
        }
        JSONObject messageDataJSONObj = (JSONObject) messageDataObj;
        JSONObject message2JsonObj = messageDataJSONObj.getJSONObject("message");
        String pageName = message2JsonObj.getString("functionName");

        String fileName = new StringBuilder(TuningIDEConstant.TUNING_KPHT)
                .append(SysperfContent.PATH_SEPARATOR)
                .append(PageType.FUNCTION_INFO.value())
                .append(SysperfContent.PATH_SEPARATOR)
                .append(pageName)
                .append(".")
                .append(TuningIDEConstant.TUNING_KPHT)
                .toString();
        message = message2;
        closeWebView(fileName);
        openWebView(fileName);
    }
}

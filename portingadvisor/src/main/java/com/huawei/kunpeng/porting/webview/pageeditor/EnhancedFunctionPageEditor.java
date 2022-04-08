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

package com.huawei.kunpeng.porting.webview.pageeditor;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.webview.pagewebview.EnhancedFunctionWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;
import java.util.HashMap;


/**
 * 承载porting增强功能任务创建页面的编辑区
 *
 * @since 2021/1/5
 */
public class EnhancedFunctionPageEditor extends WebFileEditor {
    private static final String FILE_NAME = new StringBuilder(IDEConstant.PORTING_KPS)
        .append(IDEConstant.PATH_SEPARATOR)
        .append(PageType.ENHANCED_FUNCTION.value())
        .append(IDEConstant.PATH_SEPARATOR)
        .append(I18NServer.toLocale("plugins_porting_enhanced_function"))
        .append(".")
        .append(IDEConstant.PORTING_KPS)
        .toString();

    private static HashMap<String, String> param = new HashMap<>();

    private final EnhancedFunctionWebView enhancedFunctionWebView;

    /**
     * EnhancedFunctionPageEditor
     *
     * @param file file
     */
    public EnhancedFunctionPageEditor(VirtualFile file) {
        currentFile = file;
        enhancedFunctionWebView = new EnhancedFunctionWebView(param);
    }

    @Override
    public JComponent getComponent() {
        return enhancedFunctionWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return enhancedFunctionWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        enhancedFunctionWebView.dispose();
    }

    /**
     * 打开任务创建页面
     */
    public static void openPage() {
        closePage();
        EnhancedFunctionPageEditor.param = new HashMap<>();
        openWebView(FILE_NAME);
    }

    /**
     * 关闭报告详情页
     */
    public static void closePage() {
        closeWebView(FILE_NAME);
    }

    /**
     * 判断页面是否打开
     *
     * @return 页面是否打开
     */
    public static boolean isPageOpen() {
        return isWebViewOpen(FILE_NAME);
    }

    /**
     * 带参数打开webview
     *
     * @param param 参数
     */
    public static void openPageContainsParam(HashMap<String, String> param) {
        closePage();
        EnhancedFunctionPageEditor.param = param;
        openWebView(FILE_NAME);
    }
}

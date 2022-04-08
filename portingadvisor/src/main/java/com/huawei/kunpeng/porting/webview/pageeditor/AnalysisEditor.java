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

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.webview.pagewebview.AnalysisWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

/**
 * 软件包构建_详情页面
 *
 * @since 2020-12-29
 */
public class AnalysisEditor extends WebFileEditor {
    /**
     * 是否可以新建任务
     */
    private static AtomicBoolean isCanOpen = new AtomicBoolean(true);

    private final AnalysisWebView analysisWebView;

    /**
     * 返回是否可以新建任务
     *
     * @return 返回是否可以新建任务
     */
    public static boolean getIsCanOpen() {
        return isCanOpen.get();
    }

    /**
     * 返回是否可以新建任务
     *
     * @param isCanOpen isCanOpen
     */
    public static void setIsCanOpen(boolean isCanOpen) {
        AnalysisEditor.isCanOpen.set(isCanOpen);
    }

    /**
     * 清除软件包重构进度条信息。
     */
    public static void clearStatus() {
        IDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING, "anyCtaskId", null);
        AnalysisEditor.isCanOpen.set(true);
    }

    /**
     * 构造函数
     *
     * @param file file
     */
    public AnalysisEditor(VirtualFile file) {
        currentFile = file;
        analysisWebView = new AnalysisWebView();
    }

    @Override
    public JComponent getComponent() {
        return analysisWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return analysisWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        analysisWebView.dispose();
    }

    /**
     * 打开报告详情页
     *
     * @param pageName 报告任务Id
     */
    public static void openPage(String pageName) {
        String fileName = new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.ANALYSIS_CENTER.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(pageName)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();

        closeWebView(fileName);
        openWebView(fileName);
    }

    /**
     * 关闭报告详情页
     *
     * @param pageName 报告任务Id
     */
    public static void closePage(String pageName) {
        String fileName = new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.ANALYSIS_CENTER.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(pageName)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();

        closeWebView(fileName);
    }
}

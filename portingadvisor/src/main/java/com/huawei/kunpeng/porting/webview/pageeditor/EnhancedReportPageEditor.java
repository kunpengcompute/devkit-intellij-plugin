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
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.webview.pagewebview.EnhancedReportWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

/**
 * 承载porting增强功能报告页面的编辑区
 *
 * @since 2021/1/5
 */
public class EnhancedReportPageEditor extends WebFileEditor {
    private static final Set<String> PAGES = new HashSet<>();

    private final EnhancedReportWebView enhancedReportWebView;

    /**
     * EnhancedReportPageEditor
     *
     * @param file     file
     * @param taskId   taskId
     * @param taskType taskType
     */
    public EnhancedReportPageEditor(VirtualFile file, String taskId, String taskType) {
        currentFile = file;
        enhancedReportWebView = new EnhancedReportWebView(taskId, taskType);
    }

    @Override
    public JComponent getComponent() {
        return enhancedReportWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return enhancedReportWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        enhancedReportWebView.dispose();
    }

    /**
     * 打开报告详情页
     *
     * @param taskId   报告任务Id
     * @param taskType 报告任务类型
     */
    public static void openPage(String taskId, String taskType) {
        String fileName = getFileName(taskId, taskType);
        PAGES.add(fileName);
        // 关闭打开的对应文件
        closeWebView(fileName);

        // 打开新文件
        openWebView(fileName);
    }

    /**
     * 关闭报告详情页
     *
     * @param taskId   报告任务Id
     * @param taskType 报告任务类型
     */
    public static void closePage(String taskId, String taskType) {
        String fileName = getFileName(taskId, taskType);
        closeWebView(fileName);
        PAGES.remove(fileName);
    }

    /**
     * 关闭所有报告
     */
    public static void closeAllPage() {
        for (String pageName : PAGES) {
            closeWebView(pageName);
        }
        if (!PAGES.isEmpty()) {
            PAGES.clear();
        }
    }

    /**
     * 获取文件名
     *
     * @param taskId   报告任务Id
     * @param taskType 报告任务类型
     * @return 文件名
     */
    private static String getFileName(String taskId, String taskType) {
        return new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.ENHANCED_REPORT.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(taskType)
            .append(IDEConstant.PATH_SEPARATOR)
            .append(taskId)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();
    }
}

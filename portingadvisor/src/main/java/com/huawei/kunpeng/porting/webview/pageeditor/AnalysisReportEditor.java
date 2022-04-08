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
import com.huawei.kunpeng.porting.webview.pagewebview.AnalysisReportWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 软件包构建_报告页面
 *
 * @since 2021-2-8
 */
public class AnalysisReportEditor extends WebFileEditor {
    private final AnalysisReportWebView analysisReportWebView;

    /**
     * AnalysisReportEditor
     *
     * @param file   file
     * @param taskId taskId
     */
    public AnalysisReportEditor(VirtualFile file, String taskId) {
        currentFile = file;
        analysisReportWebView = new AnalysisReportWebView(taskId);
    }

    @Override
    public JComponent getComponent() {
        return analysisReportWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return analysisReportWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        analysisReportWebView.dispose();
    }

    /**
     * 打开报告详情页
     *
     * @param taskId 报告任务Id
     */
    public static void openPage(String taskId) {
        String fileName = new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.ANALYSIS_CENTER_REPORT.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(taskId)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();

        // 关闭打开的对应文件
        closeWebView(fileName);

        // 打开新文件
        openWebView(fileName);
    }
}

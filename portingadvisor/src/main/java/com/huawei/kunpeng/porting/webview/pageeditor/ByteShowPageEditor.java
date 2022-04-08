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
import com.huawei.kunpeng.porting.webview.pagewebview.ByteShowWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

/**
 * 字节对齐建议查看页面编辑区
 *
 * @since 2021/1/9
 */
public class ByteShowPageEditor extends WebFileEditor {
    private static final Set<String> PAGES = new HashSet<>();

    private final ByteShowWebView byteShowWebView;

    /**
     * ByteShowPageEditor
     *
     * @param file     file
     * @param reportId reportId
     * @param diffPath diffPath
     */
    public ByteShowPageEditor(VirtualFile file, String reportId, String diffPath) {
        currentFile = file;
        byteShowWebView = new ByteShowWebView(reportId, diffPath);
    }

    @Override
    public JComponent getComponent() {
        return byteShowWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return byteShowWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        byteShowWebView.dispose();
    }

    /**
     * 打开页面
     *
     * @param reportId 报告任务Id
     * @param diffPath 文件路径
     */
    public static void openPage(String reportId, String diffPath) {
        String fileName = getFileName(reportId, diffPath);
        PAGES.add(fileName);

        // 关闭打开的对应文件
        closeWebView(fileName);

        // 打开新文件
        openWebView(fileName);
    }

    /**
     * 关闭全部页面
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
     * @param reportId 报告任务Id
     * @param diffPath 文件路径
     * @return 文件名
     */
    private static String getFileName(String reportId, String diffPath) {
        return new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.BYTE_SHOW.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(reportId)
            .append(diffPath)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();
    }
}

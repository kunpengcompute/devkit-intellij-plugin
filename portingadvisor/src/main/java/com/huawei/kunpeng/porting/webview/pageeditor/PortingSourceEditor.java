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
import com.huawei.kunpeng.porting.common.constant.LeftTreeTitleConstant;
import com.huawei.kunpeng.porting.common.constant.enums.PageType;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingSourceWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 扫描菜单事件
 *
 * @since 2021-01-06
 */
public class PortingSourceEditor extends WebFileEditor {
    private final PortingSourceWebView portingSourceWebView;

    /**
     * 默认构造函数
     *
     * @param file 源码扫描Webview虚拟文件
     */
    public PortingSourceEditor(VirtualFile file) {
        currentFile = file;
        portingSourceWebView = new PortingSourceWebView();
    }

    @Override
    public PortingWebView getWebView() {
        return portingSourceWebView;
    }

    @Override
    public JComponent getComponent() {
        return portingSourceWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        portingSourceWebView.dispose();
    }

    /**
     * 打开源码分析扫描入口
     */
    public static void openPage() {
        String fileName = new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.SOURCE.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(LeftTreeTitleConstant.SOURCE_CODE_PORTING)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();

        closeWebView(fileName);
        openWebView(fileName);
    }
}

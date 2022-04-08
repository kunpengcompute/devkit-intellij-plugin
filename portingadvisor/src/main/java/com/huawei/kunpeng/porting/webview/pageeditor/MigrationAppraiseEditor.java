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
import com.huawei.kunpeng.porting.webview.pagewebview.MigrationAppraiseWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 软件迁移评估页面编辑区
 *
 * @since v1.0
 */
public class MigrationAppraiseEditor extends WebFileEditor {
    private final MigrationAppraiseWebView migrationAppraiseWebView;

    /**
     * MigrationAppraiseEditor
     *
     * @param file 文件
     */
    public MigrationAppraiseEditor(VirtualFile file) {
        currentFile = file;
        migrationAppraiseWebView = new MigrationAppraiseWebView();
    }

    @Override
    public JComponent getComponent() {
        return migrationAppraiseWebView.getContent();
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    @Override
    public PortingWebView getWebView() {
        return migrationAppraiseWebView;
    }

    @Override
    public void dispose() {
        super.dispose();
        migrationAppraiseWebView.dispose();
    }

    /**
     * 打开软件迁移评估首页
     *
     * @param name 软件迁移评估首页名
     */
    public static void openPage(String name) {
        String fileName = new StringBuilder(IDEConstant.PORTING_KPS).append(IDEConstant.PATH_SEPARATOR)
            .append(PageType.MIGRATION_APPRAISE.value())
            .append(IDEConstant.PATH_SEPARATOR)
            .append(name)
            .append(".")
            .append(IDEConstant.PORTING_KPS)
            .toString();

        openWebView(fileName);
    }
}

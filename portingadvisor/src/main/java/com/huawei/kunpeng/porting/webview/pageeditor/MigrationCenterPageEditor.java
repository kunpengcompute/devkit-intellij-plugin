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
import com.huawei.kunpeng.porting.webview.pagewebview.MigrationCenterWebView;
import com.huawei.kunpeng.porting.webview.pagewebview.PortingWebView;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.JComponent;

/**
 * 承载专项软件迁移主页面的编辑区
 *
 * @since 2020-12-30
 */
public class MigrationCenterPageEditor extends WebFileEditor {
    private static final String FILE_NAME = new StringBuilder(IDEConstant.PORTING_KPS)
        .append(IDEConstant.PATH_SEPARATOR)
        .append(PageType.MIGRATION_CENTER.value())
        .append(IDEConstant.PATH_SEPARATOR)
        .append(LeftTreeTitleConstant.DEDICATED_SOFTWARE_PORTING)
        .append(".")
        .append(IDEConstant.PORTING_KPS)
        .toString();

    private final MigrationCenterWebView migrationCenterWebView;

    /**
     * MigrationCenterPageEditor
     *
     * @param file file
     */
    public MigrationCenterPageEditor(VirtualFile file) {
        this.currentFile = file;
        this.migrationCenterWebView = new MigrationCenterWebView();
    }

    @Override
    public PortingWebView getWebView() {
        return migrationCenterWebView;
    }

    @Override
    public JComponent getComponent() {
        return migrationCenterWebView.getContent();
    }

    @Override
    public void dispose() {
        super.dispose();
        migrationCenterWebView.dispose();
    }

    /**
     * 打开专项软件迁移主页面
     */
    public static void openPage() {
        // 打开新文件
        openWebView(FILE_NAME);
    }
}

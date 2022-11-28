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

package com.huawei.kunpeng.hyper.tuner.webview;

import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.Optional;

/**
 * 自定义webview文件编辑器
 *
 * @since 2022-06-28
 */
public abstract class TuningWebFileEditor extends WebFileEditor {
    /**
     * 打开webview页面
     *
     * @param pageName 页面名称
     */
    public static void openWebView(String pageName) {
        // 关闭打开的对应文件
        Optional<File> fileOptional = FileUtil.getFile(CommonUtil.getPluginWebViewFilePath(pageName), true);
        if (!fileOptional.isPresent()) {
            return;
        }
        File file = fileOptional.get();
        VirtualFile scanPage = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        if (scanPage != null) {
            if (CommonUtil.getDefaultProject() == null) {
                return;
            }
            IDEFileEditorManager.getInstance().openFile(scanPage, true);
            // 更新目前的选中文件
            IDEFileEditorManager.getInstance().setSelectFile(scanPage);
        }
    }
}

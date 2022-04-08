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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.PageType;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

/**
 * 自定义webview文件Provider
 *
 * @since 2020-10-23
 */
public class WebFileProvider extends AbstractWebFileProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if (file.getCanonicalPath().contains(TuningIDEConstant.TUNING_NAME)) {
            return file.getFileType().getDefaultExtension().equals(TuningWebFileType.EXTENSION);
        }
        return false;
    }

    @Override
    public WebFileEditor getWebFileEditor(@NotNull VirtualFile file, String[] paths, String[] pathsAfter) {
        WebFileEditor webFileEditor = new DefaultEditor(file);
        if (IDEContext.checkLogin(TuningIDEConstant.TOOL_NAME_TUNING)) {
            webFileEditor = PageType.getStatusByValue(pathsAfter[0]).getWebFileEditor(file).orElse(webFileEditor);
        }
        return webFileEditor;
    }

    @Override
    public boolean validateFilePathAndType(VirtualFile file) {
        if (file.getCanonicalPath().contains(TuningIDEConstant.TUNING_NAME)) {
            return file.getFileType().getDefaultExtension().equals(TuningWebFileType.EXTENSION);
        }
        return false;
    }
}


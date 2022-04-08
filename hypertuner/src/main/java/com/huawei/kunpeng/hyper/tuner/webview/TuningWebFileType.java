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

import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * 自定义webview文件类型
 *
 * @since 2020-10-23
 */
public class TuningWebFileType implements FileType {
    /**
     * 文件扩展
     */
    public static final String EXTENSION = "kpht";

    @NotNull
    @Override
    public String getName() {
        return EXTENSION;
    }

    @NotNull
    @Override
    @NlsContexts.Label
    public String getDescription() {
        return "Webview";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return BaseIntellijIcons.Settings.LOGO;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return CharsetToolkit.UTF8;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}

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

package com.huawei.kunpeng.devkit;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * KPPluginFileType
 *
 * @since 2021-08-25
 */
public class KPPluginFileType implements FileType {
    /**
     * 插件显示名称
     */
    private static final String PLUGIN_NAME = "Kunpeng plugins";

    @NotNull
    @Override
    public String getName() {
        return "KunpengPluginStore";
    }

    @NotNull
    @Override
    public String getDescription() {
        return PLUGIN_NAME;
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "kpplugin";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return KPIconProvider.HW_LOGO_X16;
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile virtualFile, byte @NotNull [] bytes) {
        return "UTF-8";
    }
}
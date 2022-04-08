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

package com.huawei.kunpeng.intellij.common.filetype;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * 用于源码迁移自定义文件类型
 *
 * @since 2021.04.01
 */
public class PortingTextFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    private static final PortingTextFileType INSTANCE = new PortingTextFileType();

    private PortingTextFileType() {
        super(PortingTextLanguage.LANGUAGE);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Text;
    }

    @NotNull
    @Override
    public String getName() {
        return "PortingText";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "PortingTextFileType";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isMyFileType(@NotNull VirtualFile file) {
        if (file.isDirectory()) {
            return false;
        }
        CharSequence fileName = file.getNameSequence();
        FileType originalFileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
        return isTypeShouldBeReplacedByPortingTextFileType(originalFileType);
    }

    private static boolean isTypeShouldBeReplacedByPortingTextFileType(@Nullable FileType registeredType) {
        return registeredType == UnknownFileType.INSTANCE
                || registeredType == INSTANCE
                || registeredType == PlainTextFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "";
    }
}

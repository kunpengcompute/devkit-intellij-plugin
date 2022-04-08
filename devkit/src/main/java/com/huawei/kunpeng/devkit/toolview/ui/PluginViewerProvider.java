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

package com.huawei.kunpeng.devkit.toolview.ui;

import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

/**
 * PluginViewerProvider
 *
 * @since 2021-05-18
 */
public class PluginViewerProvider implements FileEditorProvider, DumbAware {
    /**
     * 编辑器ID
     */
    public static final String EDITOR_ID = "kunpeng-plugin-marketplace";


    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return virtualFile instanceof PluginVirtualFile;
    }

    @NotNull
    @Override
    public PluginViewer createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (virtualFile instanceof PluginVirtualFile) {
            return new PluginViewer((PluginVirtualFile) virtualFile);
        }
        PluginViewer pluginViewer = null;
        return pluginViewer;
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return EDITOR_ID;
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
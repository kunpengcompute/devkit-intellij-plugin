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

package com.huawei.kunpeng.porting.listener;

import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

/**
 * IDE 文件编辑区监听器
 * document层面监听，已在plugin.xml文件内注册
 *
 * @since 2020-11-18
 */
public class IDEFileDocumentManagerListener implements FileDocumentManagerListener {
    /**
     * ctrl+s保存触发此方法
     *
     * @param document 编辑区所有修改过的document
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        VirtualFile vFile = FileDocumentManager.getInstance().getFile(document);
        EditorSourceFileHandle.getEditorSourceFileHandle().saveSourceFile(vFile, false);
    }
}

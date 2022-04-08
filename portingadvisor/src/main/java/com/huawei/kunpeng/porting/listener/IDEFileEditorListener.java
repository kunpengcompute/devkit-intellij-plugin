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

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.porting.action.report.PortingInspectionAction;
import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;

import com.intellij.codeInspection.ex.InspectionProfileModifiableModelKt;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.spellchecker.inspections.SpellCheckingInspection;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;


/**
 * IDE 文件编辑区监听器
 * Project层面监听，已在plugin.xml文件内注册
 *
 * @since 2020-10-29
 */
public class IDEFileEditorListener implements FileEditorManagerListener, FileEditorManagerListener.Before {
    /**
     * 主编辑区打开文件时的操作
     *
     * @param source 原生文件编辑区管理器
     * @param file   打开的文件
     */
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        // 若打开的是源码文件
        if (IDEFileEditorManager.getInstance().isSourceFile(file)) {
            // 设置当前打开源码文件
            IDEFileEditorManager.getInstance().setCurrentSourceFile(file);
            // 为源码文件注册文档监听器
            Document document = FileDocumentManager.getInstance().getDocument(file);
            document.addDocumentListener(IDEDocumentListener.getInstance());

            // 设置PortingInspectionAction生效
            InspectionProfileModifiableModelKt.modifyAndCommitProjectProfile(source.getProject(), it ->
                it.enableTool(PortingInspectionAction.SHORT_NAME, source.getProject()));
            // 设置SpellCheckingInspection失效
            PsiFile psiFile = PsiDocumentManager.getInstance(source.getProject()).getPsiFile(document);
            InspectionProfileModifiableModelKt.modifyAndCommitProjectProfile(source.getProject(), it ->
                it.disableTool(SpellCheckingInspection.SPELL_CHECKING_INSPECTION_TOOL_NAME, psiFile));
        }
    }

    /**
     * 关闭文件时的操作
     *
     * @param source 原生文件编辑区管理器
     * @param file   关闭的文件
     */
    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        // 如果关闭的是源码文件
        Document document = FileDocumentManager.getInstance().getDocument(file);
        boolean isSourceFile = IDEFileEditorManager.getInstance().isSourceFile(file);
        boolean isLocalFile = true;
        if (isSourceFile && document != null) {
            // 移除源码文档中的文档监听器
            document.removeDocumentListener(IDEDocumentListener.getInstance());

            // 保存到服务器并删除临时文件
            EditorSourceFileHandle.getEditorSourceFileHandle().saveSourceFile(file, true);

            // 关闭源码文件 删除textmate或者C++中在打开源码文件时添加的pattern
            removeFileTypePattern(file);

            // 在源码文件列表中删去该文件
            String filePath = new File(file.getPath()).getPath();
            IDEFileEditorManager.getInstance().removeSourceFile(file);
            EditorSourceFileHandle.getPortInfo().remove(filePath);
            isLocalFile = EditorSourceFileHandle.getSourceFile().getOrDefault(filePath, true);
            EditorSourceFileHandle.getSourceFile().remove(filePath);
        }
        if (IDEFileEditorManager.getInstance().getOpenSourceFiles().size() == 0 && document != null) {
            EditorSourceFileHandle.portingJson = null;

            // 设置PortingInspectionAction失效
            InspectionProfileModifiableModelKt.modifyAndCommitProjectProfile(source.getProject(), it ->
                it.enableTool(SpellCheckingInspection.SPELL_CHECKING_INSPECTION_TOOL_NAME, source.getProject()));

            // 设置SpellCheckingInspection生效
            PsiFile psiFile = PsiDocumentManager.getInstance(source.getProject()).getPsiFile(document);
            InspectionProfileModifiableModelKt.modifyAndCommitProjectProfile(source.getProject(), it ->
                it.disableTool(PortingInspectionAction.SHORT_NAME, psiFile));
        }
        if (isSourceFile && !isLocalFile) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    file.delete(this);
                } catch (IOException e) {
                    Logger.error("delete virtual file error.");
                }
            });
        }
    }

    private void removeFileTypePattern(VirtualFile file) {
        FileType[] registeredFileTypes = FileTypeManagerImpl.getInstance().getRegisteredFileTypes();
        for (FileType fileType : registeredFileTypes) {
            if (("PortingText").equals(fileType.getName())) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    FileTypeManagerImpl.getInstanceEx().removeAssociation(fileType,
                        FileTypeManagerImpl.parseFromString(file.getName()));
                });
            }
        }
    }

    /**
     * 选中文件变化时更新selectFile
     *
     * @param event 文件编辑器事件
     */
    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        // 当最后一个文件关闭后file会为null
        VirtualFile file = event.getNewFile();
        if (file == null) {
            return;
        }
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        if (instance != null) {
            // 更新当前选中文件信息
            instance.setSelectFile(file);
            if (instance.isSourceFile(file)) {
                // 若当前选中文件为源码文件，则更新当前选中源码文件信息
                instance.setCurrentSourceFile(file);
            } else {
                // 非源码文件则将选中源码文件信息置空
                instance.setCurrentSourceFile(null);
            }
        }
        String filePath = new File(file.getPath()).getPath();
        EditorSourceFileHandle.portingJson = EditorSourceFileHandle.getPortInfo().get(filePath);
        if (isFileExist(event)) {
                EditorSourceFileHandle.getEditorSourceFileHandle().saveSourceFile(event.getOldFile(), false);
        }
    }

    private boolean isFileExist(@NotNull FileEditorManagerEvent event) {
        if (event.getOldFile() != null) {
            // File.getpath和VirtualFile.getpath有点区别
            String filePath = new File(event.getOldFile().getPath()).getPath();
            return EditorSourceFileHandle.getSourceFile().get(filePath) != null;
        }
        return false;
    }

    @Override
    public void beforeFileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        return;
    }

    @Override
    public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        return;
    }
}

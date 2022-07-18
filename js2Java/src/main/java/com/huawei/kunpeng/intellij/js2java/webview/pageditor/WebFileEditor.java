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

package com.huawei.kunpeng.intellij.js2java.webview.pageditor;

import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 自定义webview文件编辑器
 *
 * @since 1.0.0
 */
public abstract class WebFileEditor implements FileEditor {
    /**
     * 该文件编辑器当前打开的文件
     */
    protected VirtualFile currentFile;

    private JComponent component;

    private final UserDataHolderBase myUserDataHolder = new UserDataHolderBase();

    @NotNull
    @Override
    public JComponent getComponent() {
        return component;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return (currentFile == null) ? "" : currentFile.getName();
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        if (currentFile != null) {
            AbstractWebFileProvider.closeWebViewPage(null, currentFile);
        }
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return this.myUserDataHolder.getUserData(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        this.myUserDataHolder.putUserData(key, value);
    }

    /**
     * 获取webView对象
     *
     * @return webView
     */
    public abstract AbstractWebView getWebView();

    /**
     * 临时关闭webview页面
     *
     * @param pageName 页面名称
     */
    public static void closeWebView(String pageName) {
        // 关闭打开的对应文件
        for (VirtualFile virtualFile : IDEFileEditorManager.getInstance().getOpenFiles()) {
            if (virtualFile.getCanonicalPath() != null && virtualFile.getCanonicalPath().endsWith(pageName)) {
                IDEFileEditorManager.getInstance().closeFile(virtualFile);
            }
        }
    }

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
        File ioFile = fileOptional.get();
        VirtualFile scanPage = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ioFile);
        if (scanPage != null) {
            if (CommonUtil.getDefaultProject() == null) {
                return;
            }
            IDEFileEditorManager.getInstance().openFile(scanPage, true);
        }
    }

    /**
     * webview页面是否打开
     *
     * @param pageName 页面名称
     * @return 页面是否打开
     */
    public static boolean isWebViewOpen(String pageName) {
        for (VirtualFile virtualFile : IDEFileEditorManager.getInstance().getOpenFiles()) {
            if (virtualFile.getCanonicalPath() != null && virtualFile.getCanonicalPath().endsWith(pageName)) {
                return IDEFileEditorManager.getInstance().isFileOpen(virtualFile);
            }
        }
        return false;
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return currentFile;
    }
}

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

package com.huawei.kunpeng.intellij.js2java.provider;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.intellij.js2java.webview.pagewebview.AbstractWebView;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 自定义webview文件Provider
 *
 * @since 1.0.0
 */
public abstract class AbstractWebFileProvider implements FileEditorProvider, DumbAware {
    // 有效的project对应的webview缓存
    private static Map<Project, Map<VirtualFile, WebFileEditor>> projectWebViewMap
            = new HashMap<>();

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return validateFilePathAndType(file);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        String[] paths = file.getCanonicalPath().split(IDEConstant.PATH_SEPARATOR +
                file.getFileType().getDefaultExtension()
                + IDEConstant.PATH_SEPARATOR);
        if (paths.length != 2) {
            return new DefaultEditor(file);
        }
        String[] pathsAfter = paths[1].split(IDEConstant.PATH_SEPARATOR);
        WebFileEditor webFileEditor = getWebFileEditor(file, paths, pathsAfter);
        // 缓存已打开的页面
        addProjectWebView(file, webFileEditor);
        return webFileEditor;
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }

    /**
     * 添加有效的project对应的webview缓存
     *
     * @param file        页面对应的文件全路径
     * @param webViewPage 打开的页面
     */
    private static synchronized void addProjectWebView(VirtualFile file, WebFileEditor webViewPage) {
        if (file == null || webViewPage == null) {
            return;
        }
        Project projectDef = CommonUtil.getDefaultProject();
        // 不存在则新建
        projectWebViewMap.computeIfAbsent(projectDef, key -> new HashMap<>());
        // 存在则返回
        if (projectWebViewMap.get(projectDef).get(file) != null) {
            return;
        }
        projectWebViewMap.get(projectDef).put(file, webViewPage);
    }

    /**
     * 获取已经打开的页面
     *
     * @param project 项目，不传则取当前打开项目
     * @param file    页面对应的文件
     * @return WebFileEditor
     */
    public static WebFileEditor getWebViewPage(final Project project, VirtualFile file) {
        Project projectDef = project;
        if (projectDef == null) {
            projectDef = CommonUtil.getDefaultProject();
        }
        WebFileEditor result = null;
        if (projectWebViewMap.get(projectDef) == null) {
            return result;
        }
        return projectWebViewMap.get(projectDef).get(file);
    }

    /**
     * 关闭对应项目下的文件对应的webview页面
     *
     * @param project 项目，不传则取当前打开项目
     * @param file    页面对应的文件
     */
    public static void closeWebViewPage(final Project project, VirtualFile file) {
        Project projectDef = project;
        if (projectDef == null) {
            projectDef = CommonUtil.getDefaultProject();
        }
        if (file == null || ValidateUtils.isEmptyMap(projectWebViewMap) || ValidateUtils.isEmptyMap(
                projectWebViewMap.get(projectDef))) {
            return;
        }
        IDEFileEditorManager.getInstance(projectDef).closeFile(file);
        projectWebViewMap.get(projectDef).keySet().removeIf(file::equals);
    }

    /**
     * 关闭所有project下打开的webview页面
     *
     * @param project 项目，不传则取当前打开项目
     */
    public static void closeWebViewPageForProject(final Project project) {
        Project projectDef = project;
        if (projectDef == null) {
            projectDef = CommonUtil.getDefaultProject();
        }
        List<VirtualFile> fs = new ArrayList<>(projectWebViewMap.get(projectDef).keySet());
        projectWebViewMap.get(projectDef).clear();
        for (VirtualFile virtualFile : fs) {
            IDEFileEditorManager.getInstance(projectDef).closeFile(virtualFile);
        }
    }

    /**
     * 关闭所有打开的webview页面
     */
    public static void closeAllWebViewPage() {
        for (Map.Entry<Project, Map<VirtualFile, WebFileEditor>> map : projectWebViewMap.entrySet()) {
            closeWebViewPageForProject(map.getKey());
        }
        projectWebViewMap.clear();
    }

    /**
     * 默认
     */
    public static class DefaultEditor extends WebFileEditor {
        private AbstractWebView result;

        /**
         * 构造函数
         *
         * @param file file
         */
        public DefaultEditor(VirtualFile file) {
            this.currentFile = file;
        }

        @NotNull
        @Override
        public JComponent getComponent() {
            return new JPanel();
        }

        /**
         * 获取webView对象
         *
         * @return webView
         */
        public AbstractWebView getWebView() {
            return result;
        }
    }

    /**
     * 具体WebFileEditor实现类
     *
     * @param file       file
     * @param paths      全路径
     * @param pathsAfter 后缀
     * @return WebFileEditor WebFileEditor
     */
    protected abstract WebFileEditor getWebFileEditor(VirtualFile file, String[] paths, String[] pathsAfter);

    /**
     * 文件路径是否包含插件主目录、文件类型校验
     *
     * @param file 文件
     * @return boolean result
     */
    public abstract boolean validateFilePathAndType(VirtualFile file);
}

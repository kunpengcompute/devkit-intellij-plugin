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

package com.huawei.kunpeng.intellij.js2java.fileditor;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IDE 文件编辑区管理类
 *
 * @since 1.0.0
 */
public class IDEFileEditorManager {
    /**
     * project关联的IDEFileEditorManager实例
     */
    public static Map<Project, IDEFileEditorManager>
            projectRelateIDEFileEditorManagerInstance = new ConcurrentHashMap<>();

    // Intellij提供的原生主编辑区管理实例
    private FileEditorManager projectFileEditorManager;

    // 主编辑区选中的文件
    private VirtualFile selectFile;

    // 主编辑区选中的源码文件（当选中的文件非源码文件时置空）
    private VirtualFile currentSourceFile;

    // 主编辑区已经打开的源码文件
    private List<VirtualFile> openSourceFiles = new LinkedList<>();

    private IDEFileEditorManager(Project project) {
        if (project != null) {
            projectFileEditorManager = FileEditorManager.getInstance(project);
            // 该接口返回的selectFiles恒为非空
            VirtualFile[] selectFiles = projectFileEditorManager.getSelectedFiles();
            if (ValidateUtils.isNotEmptyArray(selectFiles)) {
                selectFile = selectFiles[0];
            } else {
                selectFile = null;
            }
        } else {
            Logger.error("The project is null.");
        }
    }

    /**
     * 获取IDE文件编辑区管理实例
     *
     * @param project 给定的Project
     * @return 返回参数project对应的IDEFileEditorManager实例
     */
    public static IDEFileEditorManager getInstance(Project project) {
        return getIdeFileEditorManager(project);
    }

    /**
     * 获取IDE文件编辑区管理实例
     * 默认使用IDEContext中的缓存project
     *
     * @return 返回当前project对应的IDEFileEditorManager实例
     */
    public static IDEFileEditorManager getInstance() {
        Project project = CommonUtil.getDefaultProject();
        return getIdeFileEditorManager(project);
    }

    @NotNull
    private static IDEFileEditorManager getIdeFileEditorManager(Project project) {
        IDEFileEditorManager ideFileEditorManagerInstance = projectRelateIDEFileEditorManagerInstance.get(project);
        if (ideFileEditorManagerInstance == null) {
            synchronized (IDEFileEditorManager.class) {
                ideFileEditorManagerInstance = projectRelateIDEFileEditorManagerInstance.get(project);
                if (ideFileEditorManagerInstance == null) {
                    ideFileEditorManagerInstance = new IDEFileEditorManager(project);
                    projectRelateIDEFileEditorManagerInstance.putIfAbsent(project, ideFileEditorManagerInstance);
                }
            }
        }
        return ideFileEditorManagerInstance;
    }

    /**
     * 获取project的原生的文件编辑管理类
     *
     * @return 返回原生的FileEditorManager实例
     */
    public FileEditorManager getProjectFileEditorManager() {
        return projectFileEditorManager;
    }

    /**
     * 获取文件编辑区所有打开的文件列表
     *
     * @return 返回编辑区打开的文件列表
     */
    public List<VirtualFile> getOpenFiles() {
        return new ArrayList<>(Arrays.asList(projectFileEditorManager.getOpenFiles()));
    }

    /**
     * 获取目前最新选中的文件
     *
     * @return 返回编辑区目前选中的文件
     */
    public VirtualFile getSelectFile() {
        return selectFile;
    }

    /**
     * 更新目前的选中文件
     * 在FileEditorManagerListener事件触发调用
     *
     * @param file 目前被选中的文件
     */
    public void setSelectFile(VirtualFile file) {
        selectFile = file;
    }

    /**
     * 在文件编辑区打开某一文件
     *
     * @param file 要打开的文件
     * @param focusEditor if need to focus
     * @return 文件编辑器数组
     */
    public FileEditor[] openFile(VirtualFile file, boolean focusEditor) {
        return projectFileEditorManager.openFile(file, focusEditor);
    }

    /**
     * 在文件编辑区将打开若干文件
     *
     * @param files 要打开的文件列表
     * @param focusEditor {@code true} if need to focus
     */
    public void openFiles(List<VirtualFile> files, boolean focusEditor) {
        for (VirtualFile file : files) {
            projectFileEditorManager.openFile(file, focusEditor);
        }
    }

    /**
     * 关闭文件编辑区的某一文件
     *
     * @param file 要关闭的文件
     */
    public void closeFile(VirtualFile file) {
        // 参数校验，避免关闭未打开的文件
        if (isFileOpen(file)) {
            projectFileEditorManager.closeFile(file);
        }
    }

    /**
     * 关闭文件编辑区的若干文件
     *
     * @param files 要关闭的文件列表
     */
    public void closeFiles(List<VirtualFile> files) {
        for (VirtualFile file : files) {
            if (isFileOpen(file)) {
                projectFileEditorManager.closeFile(file);
            }
        }
    }

    /**
     * 关闭所有文件编辑区打开的文件
     */
    public void closeAllFile() {
        List<VirtualFile> openFiles = getOpenFiles();
        for (VirtualFile file : openFiles) {
            projectFileEditorManager.closeFile(file);
        }
    }

    /**
     * 判断文件是否已被打开
     *
     * @param file 需要判断的文件
     * @return true代表文件已被打开，反之亦然
     */
    public boolean isFileOpen(VirtualFile file) {
        return projectFileEditorManager.isFileOpen(file);
    }

    /**
     * 添加源码文件
     * 在源码文件打开的动作中执行该方法对打开源码文件列表进行更新
     *
     * @param file 需要添加的源码文件
     */
    public void addSourceFileIfAbsent(VirtualFile file) {
        if (!isSourceFile(file)) {
            openSourceFiles.add(file);
        }
    }

    /**
     * 获取已经打开的源码文件列表
     *
     * @return openSourceFiles 编辑区中打开的文件列表
     */
    public List<VirtualFile> getOpenSourceFiles() {
        return openSourceFiles;
    }

    /**
     * 获取当前选中的源码文件
     *
     * @return openSourceFiles 编辑区中打开的文件列表
     */
    public VirtualFile getCurrentSourceFile() {
        return currentSourceFile;
    }

    /**
     * 移除源码文件
     *
     * @param file 需要移除的源码文件
     */
    public void removeSourceFile(VirtualFile file) {
        openSourceFiles.remove(file);
    }

    /**
     * 判断文件是否为打开的源码文件
     *
     * @param file 需要判断的文件
     * @return true 为源码文件
     */
    public boolean isSourceFile(VirtualFile file) {
        return openSourceFiles.contains(file);
    }

    /**
     * 更新当前选中的源码文件
     * if null 即选中的文件非源码文件
     *
     * @param file 当前选中的源码文件
     */
    public void setCurrentSourceFile(VirtualFile file) {
        currentSourceFile = file;
    }
}

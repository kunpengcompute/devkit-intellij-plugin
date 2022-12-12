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

package com.huawei.kunpeng.hyper.tuner.listener;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;

import com.huawei.kunpeng.intellij.js2java.provider.AbstractWebFileProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;

import org.jetbrains.annotations.NotNull;

/**
 * IDE project监听器
 * 可以在项目打开和关闭时执行自定义方法
 *
 * @since 2020-10-29
 */
public class IDEProjectListener implements ProjectManagerListener {
    @Override
    public void projectClosing(@NotNull Project project) {
        Logger.info("======project {} closing======", project.getName());
        AbstractWebFileProvider.closeAllWebViewPage();
    }

    /**
     * 项目打开时，将project注册进全局上下文
     *
     * @param project IDE启动的项目
     */
    @Override
    public void projectOpened(@NotNull Project project) {
        Logger.info("=====project {} Opened=====", project.getName());
        TuningIDEContext.setValueForGlobalContext(
                TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.CURRENT_PROJECT.vaLue(), project);
        Logger.info("=====project {} Opened successful=====", project.getName());
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        Logger.info("=====project {} closed=====", project.getName());
        IDEFileEditorManager.projectRelateIDEFileEditorManagerInstance.remove(project);
    }
}

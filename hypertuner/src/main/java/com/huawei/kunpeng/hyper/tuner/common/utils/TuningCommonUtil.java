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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.intellij.openapi.wm.impl.ProjectFrameHelper;

import java.awt.Window;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * porting common util extends CommonUtil
 *
 * @since 2.3.T10
 */
public class TuningCommonUtil extends CommonUtil {
    /**
     * 判断当前窗口是否有正在进行的任务
     *
     * @param titleStr 任务标题
     * @return 是否有正在信息的任务
     */
    public static boolean isTaskRunning(String titleStr) {
        Window window = CommonUtil.getDefaultWindow();
        AtomicBoolean isRunning = new AtomicBoolean(false);
        ProjectFrameHelper frame = ProjectFrameHelper
                .getFrameHelper(window);
        if (frame == null) {
            return isRunning.get();
        }
        StatusBarEx statusBar = frame.getStatusBar();
        if (statusBar != null) {
            statusBar.getBackgroundProcesses().forEach(pair -> {
                if (pair.getFirst().getTitle().equals(titleStr)) {
                    isRunning.set(true);
                    return;
                }
            });
        }
        return isRunning.get();
    }

    /**
     * 获取插件安装目录
     *
     * @return string 安装目录
     */
    public static String getPluginInstalledPath() {
        String pluginPath = PathManager.getPluginsPath();
        return pluginPath + IDEConstant.PATH_SEPARATOR + TuningIDEConstant.PLUGIN_NAME;
    }

    /**
     * 获取安装目录下具体位置文件
     *
     * @param filePath 文件位置
     * @return string 文件
     */
    public static String getPluginInstalledPathFile(String filePath) {
        return getPluginInstalledPath() + IDEConstant.PATH_SEPARATOR + filePath;
    }
}

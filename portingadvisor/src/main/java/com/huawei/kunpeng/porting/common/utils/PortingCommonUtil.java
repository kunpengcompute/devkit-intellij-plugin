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

package com.huawei.kunpeng.porting.common.utils;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.ConfigProperty;

import com.alibaba.fastjson.JSON;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.intellij.openapi.wm.impl.ProjectFrameHelper;

import java.awt.Window;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * porting common util extends CommonUtil
 *
 * @since 2.3.T10
 */
public class PortingCommonUtil extends CommonUtil {
    /**
     * 获取当前用户是否确定不再进行环境检查
     *
     * @return boolean checkResult
     */
    public static boolean isSignEnvPrompt() {
        return PortingUserInfoContext.sourceCodeCheckGcc.contains(PortingUserInfoContext.getInstance().getUserName());
    }

    /**
     * 更新当前用户确定不再进行环境检查
     *
     * @param key ip+"#"+userName
     */
    public static void write2ConfigEnvPrompt(String key) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        List portConfig = JsonUtil.getValueIgnoreCaseFromMap(config, ConfigProperty.PORT_ENV_DO_NOT_PROMPT.vaLue(),
                List.class);
        portConfig.add(key);
        FileUtil.ConfigParser.saveJsonConfigToFile(JSON.toJSONString(config), IDEConstant.CONFIG_PATH);
    }

    /**
     * 判断当前窗口是否有正在进行的任务
     *
     * @param title 任务标题
     * @return 是否有正在信息的任务
     */
    public static boolean isTaskRunning(String title) {
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
                if (pair.getFirst().getTitle().equals(title)) {
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
        return pluginPath + IDEConstant.PATH_SEPARATOR + PortingIDEConstant.PLUGIN_NAME;
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

    /**
     * 从配置文件中读取 ip
     *
     * @return string ip
     */
    public static String readCurIpFromConfig() {
        return ConfigUtils.readValueFromConfig(IDEConstant.CONFIG_PATH, ConfigProperty.PORT_CONFIG.vaLue(), "ip");
    }
}

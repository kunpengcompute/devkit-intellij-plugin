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

package com.huawei.kunpeng.porting.common;

import com.huawei.kunpeng.intellij.common.BaseCacheDataOpt;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.ConfigProperty;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;

import com.intellij.util.ui.UIUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * IDE 请求相关缓存数据操作
 *
 * @since 2020-09-25
 */
public class CacheDataOpt extends BaseCacheDataOpt {
    /**
     * 初始化系统全局缓存
     */
    public static void initGlobalCache() {
        // 设置项目名
        PortingCommonUtil.setProjectName(PortingIDEConstant.TOOL_NAME_PORTING);

        // 设置插件安装主目录
        PortingCommonUtil.setPluginName(PortingIDEConstant.PLUGIN_NAME);

        PortingCommonUtil.setToolName(PortingIDEConstant.TOOL_NAME_PORTING);

        // 设置webviewIndex入口路径
        PortingCommonUtil.setWebViewIndex(PortingIDEContext.getPortingWebViewIndex());

        Logger.info("start loading GlobalCache");

        // 清除用户相关文件
        clearUserFiles();

        // 加载当前系统显示语言
        I18NServer.updateCurrentLocale();

        // 缓存当前Intellij主题
        boolean isUnderIntelliJLaF = UIUtil.isUnderIntelliJLaF();
        PortingIDEContext.setValueForGlobalContext(
            PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.LIGHT_THEME.vaLue(), isUnderIntelliJLaF);

        // 获取系统信息及动态库的环境path
        loadingSystemOS();

        // 加载config配置信息
        Logger.info("start loading config.properties");
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(PortingIDEConstant.CONFIG_PATH);
        if (config.get(ConfigProperty.PORT_CONFIG.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.PORT_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                Map configDef = (Map) configList.get(0);
                PortingIDEContext.setValueForGlobalContext(
                    PortingIDEConstant.TOOL_NAME_PORTING,
                    BaseCacheVal.IP.vaLue(), configDef.get(BaseCacheVal.IP.vaLue()));
                PortingIDEContext.setValueForGlobalContext(
                    PortingIDEConstant.TOOL_NAME_PORTING,
                    BaseCacheVal.PORT.vaLue(), configDef.get(BaseCacheVal.PORT.vaLue()));
                PortingIDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_DEP, BaseCacheVal.IP.vaLue(),
                    configDef.get(BaseCacheVal.IP.vaLue()));
                PortingIDEContext.setValueForGlobalContext(
                    PortingIDEConstant.TOOL_NAME_DEP,
                    BaseCacheVal.PORT.vaLue(), configDef.get(BaseCacheVal.PORT.vaLue()));
            }
        }

        // 解压webview到安装目录
        Logger.info("start loading webview");
        Optional<File> fileOptional = FileUtil.getFile(
            CommonUtil.getPluginInstalledPath() + PortingIDEConstant.PORTING_PLUGIN_NAME, true);
        fileOptional.ifPresent(file -> FileUtil.readAndWriterFileFromJar(file, PortingIDEConstant.PORTING_PLUGIN_NAME,
            true));

        FileUtil.unzipFile(CommonUtil.getPluginInstalledPath() + PortingIDEConstant.PORTING_PLUGIN_NAME,
            CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WEB_VIEW_PATH));

        // 加载index页面到缓存，并替换base路径
        Logger.info("start loading index");
        String indexHtml = FileUtil.readFileContent(PortingIDEContext.getPortingWebViewIndex());
        indexHtml = indexHtml.replaceFirst("base href=\"\\./\"", "base href=\"\"");
        PortingIDEContext.setValueForGlobalContext(null, PortingIDEConstant.PORTING_WEB_VIEW_INDEX_HTML, indexHtml);
        Logger.info("loading GlobalCache successful");
    }

    /**
     * 重置用户相关插件缓存信息
     */
    public static void clearUserFiles() {
        Logger.info("start clear UserFiles");
        // 清空历史报告页面文件
        FileUtil.deleteDir(null, CommonUtil.getPluginWebViewFilePath(PortingIDEConstant.PORTING_KPS));
        FileUtil.deleteDir(null, CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WORKSPACE_TEMP));
        Logger.info("clear UserFiles successful");
    }
}

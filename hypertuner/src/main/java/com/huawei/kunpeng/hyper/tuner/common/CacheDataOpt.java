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

package com.huawei.kunpeng.hyper.tuner.common;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningCommonUtil;
import com.huawei.kunpeng.intellij.common.BaseCacheDataOpt;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;

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
        Logger.info("=====start loading GlobalCache=====");

        // 设置项目名
        TuningCommonUtil.setProjectName(TuningIDEConstant.TOOL_NAME_TUNING);

        // 设置插件安装主目录
        TuningCommonUtil.setPluginName(TuningIDEConstant.PLUGIN_NAME);

        TuningCommonUtil.setToolName(TuningIDEConstant.TOOL_NAME_TUNING);

        // 加载当前系统显示语言
        TuningI18NServer.updateTuningCurrentLocale();
        // 设置webviewIndex入口路径
        CommonUtil.setWebViewIndex(TuningIDEContext.getSysWebViewIndex());
        // 缓存当前Intellij主题
        boolean isUnderIntelliJLaF = UIUtil.isUnderIntelliJLaF();

        TuningIDEContext.setValueForGlobalContext(
                TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.LIGHT_THEME.vaLue(), isUnderIntelliJLaF);

        // 获取系统信息及动态库的环境path
        loadingSystemOS();

        // 加载config配置信息
        Logger.info("=====start loading config.properties=====");
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(TuningIDEConstant.CONFIG_PATH);
        if (config.get(ConfigProperty.PORT_CONFIG.vaLue()) instanceof List) {
            List configList = (List) config.get(ConfigProperty.PORT_CONFIG.vaLue());
            if (configList.get(0) instanceof Map) {
                Map configDef = (Map) configList.get(0);
                IDEContext.setValueForGlobalContext(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        BaseCacheVal.IP.vaLue(),
                        configDef.get(BaseCacheVal.IP.vaLue()));
                IDEContext.setValueForGlobalContext(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        BaseCacheVal.PORT.vaLue(),
                        configDef.get(BaseCacheVal.PORT.vaLue()));
            }
        }
        Logger.info("=====start loading webview=====");
        Optional<File> optionalFile = FileUtil.getFile(
                CommonUtil.getPluginInstalledPath() + TuningIDEConstant.TUNING_PLUGIN_NAME, true);
        optionalFile.ifPresent(file -> FileUtil.readAndWriterFileFromJar(file, TuningIDEConstant.TUNING_PLUGIN_NAME,
                true));

        FileUtil.unzipFile(CommonUtil.getPluginInstalledPath() + TuningIDEConstant.TUNING_PLUGIN_NAME,
                CommonUtil.getPluginInstalledPathFile(TuningIDEConstant.TUNING_WEB_VIEW_PATH));

        // 加载index页面到缓存，并替换base路径
        Logger.info("=====start loading index=====");
        String indexHtml = FileUtil.readFileContent(TuningIDEContext.getSysWebViewIndex());
        indexHtml = indexHtml.replaceFirst("base href=\"\\./\"", "base href=\"\"");
        IDEContext.setValueForGlobalContext(null, TuningIDEConstant.TUNING_WEB_VIEW_INDEX_HTML, indexHtml);
        Logger.info("=====loading GlobalCache successful=====");

        // 加载index页面到缓存，并替换base路径
        Logger.info("=====start loading index=====");
        String javaIndexHtml = FileUtil.readFileContent(TuningIDEContext.getJavaWebViewIndex());
        javaIndexHtml = javaIndexHtml.replaceFirst("base href=\"\\./\"", "base href=\"\"");
        IDEContext.setValueForGlobalContext(null, TuningIDEConstant.JAVA_WEB_VIEW_INDEX_HTML, javaIndexHtml);
        Logger.info("=====loading GlobalCache successful=====");
    }

    /**
     * 重置用户相关插件缓存信息
     */
    public static void clearUserFiles() {
        Logger.info("=====start clear UserFiles=====");
        // 清空历史报告页面文件
        FileUtil.deleteDir(null, CommonUtil.getPluginWebViewFilePath(TuningIDEConstant.PORTING_KPS));
        Logger.info("=====clear UserFiles successful=====");
    }
}

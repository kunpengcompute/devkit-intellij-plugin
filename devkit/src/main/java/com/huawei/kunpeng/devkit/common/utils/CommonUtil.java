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

package com.huawei.kunpeng.devkit.common.utils;

import com.huawei.kunpeng.devkit.model.KPPluginNode;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerConfigurable;
import com.intellij.ide.plugins.PluginManagerMain;
import com.intellij.ide.plugins.PluginNode;
import com.intellij.ide.plugins.marketplace.MarketplaceRequests;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.containers.ContainerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CommonUtil
 *
 * @since 2021-08-25
 */
public class CommonUtil {
    /**
     * 获取插件安装路径下的插件包（jar）中的文件
     *
     * @param filePath 相对插件包中的文件路径
     * @return InputStream InputStream
     */
    public static InputStream getPluginInstalledFile(String filePath) {
        return CommonUtil.class.getResourceAsStream(filePath);
    }

    /**
     * 重启IDE
     */
    public static void restartIDE() {
        if (PluginManagerConfigurable.showRestartDialog() == Messages.YES) {
            Application application = ApplicationManager.getApplication();
            application.exit(true, false, true);
        }
    }

    /**
     * 获取插件列表
     *
     * @return List
     * @throws IOException
     */
    public static List<PluginNode> getPluginList() throws IOException {
        List<PluginNode> plugins = new ArrayList<>(4);
        try {
            List<PluginNode> pluginList = MarketplaceRequests.getInstance().searchPlugins("search=Kunpeng", 99);
            for (PluginNode pluginNode : pluginList) {
                PluginNode pluginDetail = MarketplaceRequests.getInstance().loadPluginDetails(pluginNode);
                if ("Huawei Technologies Co., Ltd.".equals(pluginDetail.getVendor())) {
                    pluginDetail.setDownloads(pluginNode.getDownloads());
                    pluginDetail.setRating(pluginNode.getRating());
                    pluginDetail.setExternalPluginId(pluginNode.getExternalPluginId());
                    plugins.add(pluginDetail);
                    KPPlugins.put(new KPPluginNode(pluginDetail));
                }
            }
        } catch (IOException e) {
            Logger.info("getPluginList fail");
            throw new IOException();
        }
        return plugins;
    }

    /**
     * downloadPlugins downloadPlugins
     *
     * @param pluginNodes       pluginNodes
     * @param allPlugins        allPlugins
     * @param onInstallRunnable onInstallRunnable
     * @param pluginEnabler     pluginEnabler
     * @param clazz             class
     */
    public static <T> void downloadPlugins(
            List<PluginNode> pluginNodes, List<? extends IdeaPluginDescriptor> allPlugins,
            Runnable onInstallRunnable, T pluginEnabler, Class<?> clazz) {
        try {
            Method installMethod = null;
            if (isNewApiVersion()) {
                installMethod = PluginManagerMain.class.getMethod("downloadPluginsAndCleanup",
                        List.class, Collection.class, Runnable.class, clazz, Runnable.class);
            } else {
                installMethod = PluginManagerMain.class.getMethod("downloadPlugins", List.class, List.class,
                        Runnable.class, clazz, Runnable.class);
            }

            Type type = installMethod.getGenericParameterTypes()[1];
            if (type instanceof ParameterizedType) {
                if (!((ParameterizedType) type).getActualTypeArguments()[0]
                        .getTypeName().contains(PluginId.class.getName())) {
                    methodInvoke(pluginNodes, allPlugins, onInstallRunnable, pluginEnabler, installMethod);
                } else {
                    List<PluginId> pluginIds = allPlugins.stream()
                            .map(PluginDescriptor::getPluginId).collect(Collectors.toList());
                    installMethod.invoke(null, pluginNodes,
                            pluginIds, onInstallRunnable, pluginEnabler, null);
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var8) {
            Logger.error("call ide install plugin method error");
        }
    }

    private static <T> void methodInvoke(List<PluginNode> pluginNodes, List<? extends IdeaPluginDescriptor> allPlugins,
                                            Runnable onInstallRunnable, T pluginEnabler, Method installMethod)
            throws IllegalAccessException, InvocationTargetException {
        if (isNewApiVersion()) {
            Collection<PluginNode> pluginNodeList =
                    ContainerUtil.filterIsInstance(allPlugins, PluginNode.class);
            installMethod.invoke(null, pluginNodes, pluginNodeList,
                    onInstallRunnable, pluginEnabler, null);
        } else {
            installMethod.invoke(null, pluginNodes, allPlugins,
                    onInstallRunnable, pluginEnabler, null);
        }
    }

    /**
     * 读取文本文件内容
     *
     * @param filePath 文件名称
     * @return string fileContent
     */
    public static String readHtmlFile(String filePath) {
        InputStream htmlFile = CommonUtil.getPluginInstalledFile(filePath);
        if (htmlFile == null) {
            return "";
        }
        StringBuilder sbf = new StringBuilder();
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(htmlFile))
        ) {
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr).append(System.lineSeparator());
            }
        } catch (IOException e) {
            Logger.error("readHtmlFile IOException.");
        } finally {
            if (htmlFile != null) {
                try {
                    htmlFile.close();
                } catch (IOException e) {
                    Logger.error("IO close error");
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 判断intellij大版本是否高于211（2021.1），2021.2版本和2021.1之前的版本部分api变更
     *
     * @return true新版本使用新的api
     */
    public static boolean isNewApiVersion() {
        // 211是2021.1.*的基线版本号
        return ApplicationInfo.getInstance().getBuild().getBaselineVersion() > 211;
    }
}

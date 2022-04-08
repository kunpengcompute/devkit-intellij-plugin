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

import com.huawei.kunpeng.devkit.MarketPlaceManager;
import com.huawei.kunpeng.devkit.model.KPPluginNode;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 插件相关帮助方法
 *
 * @since 2021-05-21
 */
public class PluginUtil {
    private PluginUtil() {
    }

    /**
     * isPluginChanged isPluginChanged
     *
     * @param showingPlugin showingPlugin
     * @param latest        latest
     * @return Optional
     */
    public static Optional<IdeaPluginDescriptor> isPluginChanged(
            IdeaPluginDescriptor showingPlugin, IdeaPluginDescriptor latest) {
        if (latest == null) {
            return Optional.empty();
        }
        PluginFieldValue[] pluginFieldValues = {IdeaPluginDescriptor::getVersion, IdeaPluginDescriptor::getName,
                IdeaPluginDescriptor::getDescription, IdeaPluginDescriptor::getVendor,
                plugin -> String.format(Locale.ROOT, "%f", KPPlugins.getRatingFloat(plugin.getPluginId())),
                plugin -> KPPlugins.getTotalDownloads(plugin.getPluginId())};
        return Stream.of(pluginFieldValues).filter(pluginFieldValue ->
                pluginFieldChanged(showingPlugin, latest, pluginFieldValue))
                .findFirst().map(changed -> latest);
    }

    /**
     * 获取插件更新的版本tooltip
     *
     * @param pluginId 插件ID
     * @return 旧版本->新版本
     */
    public static Optional<String> updateToolTip(PluginId pluginId) {
        if (!MarketPlaceManager.isInstalled(pluginId) && !MarketPlaceManager.hasNewerVersion(pluginId)) {
            return Optional.empty();
        }
        String oldVersion =
                MarketPlaceManager.installedPlugins().stream().filter(plugin -> pluginId.equals(plugin.getPluginId()))
                        .findFirst().map(IdeaPluginDescriptor::getVersion).orElse("");
        String newVersion = insteadPluginVersion(pluginId).orElse(
                Optional.ofNullable(MarketPlaceManager.latestPlugin(pluginId)).map(IdeaPluginDescriptor::getVersion)
                        .orElse(""));
        if (StringUtil.isEmpty(oldVersion) || StringUtil.isEmpty(newVersion)) {
            return Optional.empty();
        }
        return Optional.of(String.format(Locale.ROOT, "%s -> %s", oldVersion, newVersion));
    }

    private static Optional<String> insteadPluginVersion(PluginId pluginId) {
        return insteadPlugin(pluginId).map(plugin -> plugin.getName() + "(" + plugin.getVersion() + ")");
    }

    /**
     * 获取插件的替代插件
     *
     * @param pluginId 插件标识
     * @return 替代插件
     */
    public static Optional<IdeaPluginDescriptor> insteadPlugin(PluginId pluginId) {
        String insteadPluginString = KPPlugins.get(pluginId).map(KPPluginNode::getInsteadPluginId).orElse("");
        if (StringUtil.isEmpty(insteadPluginString)) {
            return Optional.empty();
        }
        PluginId insteadPluginId = PluginId.getId(insteadPluginString);
        return Optional.ofNullable(MarketPlaceManager.latestPlugin(insteadPluginId));
    }

    private static boolean pluginFieldChanged(
            IdeaPluginDescriptor plugin, IdeaPluginDescriptor latest, PluginFieldValue pluginFieldValue) {
        return StringUtil.compare(pluginFieldValue.value(plugin), pluginFieldValue.value(latest), true) != 0;
    }

    /**
     * 插件信息
     */
    public interface PluginFieldValue {
        String value(IdeaPluginDescriptor pluginDescriptor);
    }
}


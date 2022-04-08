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

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.extensions.PluginId;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KPPlugins KPPlugins
 *
 * @since 2021-08-25
 */
public class KPPlugins {
    private static final String MARKET_PLACE_HOST = "https://plugins.jetbrains.com";
    private static final Map<PluginId, KPPluginNode> HW_PLUGIN_NODE_MAP = new ConcurrentHashMap<>(0);
    private static final Map<PluginId, File> INSTALLED_PLUGIN_PATH_MAP = new ConcurrentHashMap<>(0);

    /**
     * KPPlugins KPPlugins
     */
    public KPPlugins() {
    }

    /**
     * put put
     *
     * @param kpPluginNode kpPluginNode
     */
    public static void put(KPPluginNode kpPluginNode) {
        HW_PLUGIN_NODE_MAP.put(kpPluginNode.getPluginNode().getPluginId(), kpPluginNode);
    }

    /**
     * isOffline isOffline
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean isOffline(PluginId pluginId) {
        return HW_PLUGIN_NODE_MAP.containsKey(pluginId) && (HW_PLUGIN_NODE_MAP.get(pluginId)).isOffline();
    }

    /**
     * getTotalDownloads getTotalDownloads
     *
     * @param pluginId pluginId
     * @return String
     */
    public static String getTotalDownloads(PluginId pluginId) {
        return !HW_PLUGIN_NODE_MAP.containsKey(pluginId) ? "0" : (HW_PLUGIN_NODE_MAP.get(pluginId)).getTotalDownloads();
    }

    /**
     * getRatingFloat getRatingFloat
     *
     * @param pluginId pluginId
     * @return float
     */
    public static float getRatingFloat(PluginId pluginId) {
        return !HW_PLUGIN_NODE_MAP.containsKey(pluginId) ? 0.0F : (HW_PLUGIN_NODE_MAP.get(pluginId)).getRatingFloat();
    }

    /**
     * isBeta isBeta
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean isBeta(PluginId pluginId) {
        return HW_PLUGIN_NODE_MAP.containsKey(pluginId) && (HW_PLUGIN_NODE_MAP.get(pluginId)).isBeta();
    }

    /**
     * get get
     *
     * @param pluginId pluginId
     * @return Optional
     */
    public static Optional<KPPluginNode> get(PluginId pluginId) {
        return Optional.ofNullable(HW_PLUGIN_NODE_MAP.get(pluginId));
    }

    /**
     * openPluginURL openPluginURL
     *
     * @param id id
     */
    public static void openPluginURL(String id) {
        BrowserUtil.browse(String.format(Locale.ROOT, "%s/plugin/%s/", MARKET_PLACE_HOST, id));
    }

    /**
     * openVendor openVendor
     *
     * @param vendor vendor
     */
    public static void openVendor(String vendor) {
        BrowserUtil.browse(String.format(Locale.ROOT, "%s/search?search=%s", MARKET_PLACE_HOST, vendor));
    }
}
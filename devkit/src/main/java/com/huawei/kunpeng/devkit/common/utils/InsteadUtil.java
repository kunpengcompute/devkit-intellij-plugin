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
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * InsteadUtil
 *
 * @since 2021-08-25
 */
public class InsteadUtil {
    /**
     * InsteadUtil InsteadUtil
     */
    public InsteadUtil() {
    }

    /**
     * insteadPluginNotInstall insteadPluginNotInstall
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean insteadPluginNotInstall(PluginId pluginId) {
        return MarketPlaceManager.isInstalled(pluginId) && insteadPlugin(pluginId).isPresent();
    }

    /**
     * insteadPlugin insteadPlugin
     *
     * @param pluginId pluginId
     * @return Optional
     */
    public static Optional<IdeaPluginDescriptor> insteadPlugin(PluginId pluginId) {
        String insteadString = KPPlugins.get(pluginId).map(KPPluginNode::getInsteadPluginId).orElse("");
        if (StringUtil.isEmpty(insteadString)) {
            return Optional.empty();
        } else {
            PluginId insteadPluginId = PluginId.getId(insteadString);
            return !MarketPlaceManager.isInstalled(insteadPluginId) &&
                    !MarketPlaceManager.needRestart(insteadPluginId) ?
                    Optional.ofNullable(MarketPlaceManager.latestPlugin(insteadPluginId)) : Optional.empty();
        }
    }

    /**
     * insteadPluginInstallDialog insteadPluginInstallDialog
     *
     * @param pluginIds pluginIds
     * @return Optional
     */
    public static Optional<InsteadPlugin> insteadPluginInstallDialog(PluginId[] pluginIds) {
        List<String> insteadPluginList = new ArrayList(0);
        List<PluginId> replacedPlugin = new ArrayList(0);
        PluginId[] insteadPluginIds = Stream.of(pluginIds)
                .map(pluginId -> insteadPlugin(pluginId)
                        .map(insteadPlugin -> {
                            replacedPlugin.add(pluginId);
                            IdeaPluginDescriptor plugin = MarketPlaceManager.latestPlugin(pluginId);
                            insteadPluginList
                                    .add(String.format(Locale.ROOT, "replace %s(%s) with %s(%s)",
                                            plugin.getName(), plugin.getVersion(), insteadPlugin.getName(),
                                            insteadPlugin.getVersion())
                                    );
                            return insteadPlugin.getPluginId();
                        }).orElse(pluginId)).distinct().toArray(PluginId[]::new);
        if (insteadPluginList.size() > 0) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.addOkAction().setText("Install instead plugins.");
            dialogBuilder.addCancelAction().setText("Cancel");
            dialogBuilder.setTitle("Huawei Plugins");
            dialogBuilder.setCenterPanel(createInsteadPluginListPanel(insteadPluginList));
            if (dialogBuilder.show() != 0) {
                return Optional.empty();
            }
        }

        return Optional.of(new InsteadPlugin(replacedPlugin, insteadPluginIds));
    }

    private static JPanel createInsteadPluginListPanel(List<String> insteadPluginList) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descPanel.add(new JLabel("The plugin to be installed has replacement plugin"));
        panel.add(descPanel);
        JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JBList<String> jbList = new JBList(insteadPluginList);
        jbList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value));
        jbList.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        listPanel.add(jbList);
        panel.add(listPanel);
        return panel;
    }

    /**
     * InsteadPlugin
     */
    public static class InsteadPlugin {
        private final List<PluginId> replacedList;
        private final PluginId[] pluginIds;

        /**
         * InsteadPlugin InsteadPlugin
         *
         * @param replacedList replacedList
         * @param pluginIds    pluginIds
         */
        public InsteadPlugin(List<PluginId> replacedList, PluginId[] pluginIds) {
            this.replacedList = replacedList;
            this.pluginIds = pluginIds;
        }

        /**
         * disableReplacedPlugins disableReplacedPlugins
         *
         * @return Runnable
         */
        public Runnable disableReplacedPlugins() {
            return () -> this.replacedList.forEach(MarketPlaceManager::disablePlugin);
        }

        /**
         * getPluginIds getPluginIds
         *
         * @return PluginId[]
         */
        public PluginId[] getPluginIds() {
            return this.pluginIds;
        }
    }
}
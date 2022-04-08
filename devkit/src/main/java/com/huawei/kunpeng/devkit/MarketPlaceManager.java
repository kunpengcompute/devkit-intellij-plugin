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

package com.huawei.kunpeng.devkit;

import com.huawei.kunpeng.devkit.common.i18n.DevkitI18NServer;
import com.huawei.kunpeng.devkit.common.utils.CommonUtil;
import com.huawei.kunpeng.devkit.common.utils.InsteadUtil;
import com.huawei.kunpeng.devkit.common.utils.KPPlugins;
import com.huawei.kunpeng.devkit.listen.ListenerManager;
import com.huawei.kunpeng.devkit.refl.PluginInstallerRef;
import com.huawei.kunpeng.devkit.toolview.LeftMainPanelUtil;
import com.huawei.kunpeng.devkit.toolview.ui.PluginListViewer;
import com.huawei.kunpeng.devkit.toolview.ui.PluginVirtualFile;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.InstalledPluginsState;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginManagerMain;
import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.net.IOExceptionDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Window;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;

/**
 * 插件市场管理
 *
 * @since 2021-05-18
 */
public class MarketPlaceManager {
    private static final Logger logger = LoggerFactory.getLogger(MarketPlaceManager.class);
    private static final InstalledPluginsState INSTALLED_PLUGINS_STATE = InstalledPluginsState.getInstance();
    private static final List<IdeaPluginDescriptor> UN_BUNDLED_PLUGINS = new ArrayList(0);
    private static final Set<PluginId> UNINSTALLED_IDS = new ConcurrentSkipListSet();
    private static final Set<PluginId> STATUS_CHANGE_IDS = new ConcurrentSkipListSet();
    private static final Set<PluginId> HAS_NEW_VERSION_PLUGINS = new ConcurrentSkipListSet();
    private static final Map<String, PluginNode> LATEST_MAP = new ConcurrentHashMap<>(0);
    private static final List<PluginId> INSTALING_LIST = new ArrayList<>(0);
    private static boolean isInstallStatusChange = false;

    static {
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        if (plugins != null && plugins.length > 0) {
            for (IdeaPluginDescriptor ideaPluginDescriptor : plugins) {
                if (!ideaPluginDescriptor.isBundled()) {
                    UN_BUNDLED_PLUGINS.add(ideaPluginDescriptor);
                }
            }
        }
    }

    private MarketPlaceManager() {
    }

    /**
     * 已安装的插件
     *
     * @return List
     */
    public static List<IdeaPluginDescriptor> installedPlugins() {
        return UN_BUNDLED_PLUGINS;
    }

    /**
     * 判断是否有新版本
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean hasNewerVersion(PluginId pluginId) {
        PluginNode pluginNode = KPPlugins.get(pluginId).get().getPluginNode();
        IdeaPluginDescriptor exists = UN_BUNDLED_PLUGINS.stream()
            .filter(installedPlugin -> pluginId.equals(installedPlugin.getPluginId())).findFirst().orElse(null);
        if (exists == null) {
            return false;
        }
        return !pluginNode.getVersion().equals(exists.getVersion());
    }

    /**
     * isInstalled
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean isInstalled(PluginId pluginId) {
        return PluginManager.isPluginInstalled(pluginId);
    }

    /**
     * isEnable
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean isEnable(PluginId pluginId) {
        if (!(UN_BUNDLED_PLUGINS instanceof IdeaPluginDescriptor)) {
            return false;
        }
        IdeaPluginDescriptor plugin = UN_BUNDLED_PLUGINS.stream()
            .filter(ideaPluginDescriptor ->
                pluginId.toString().equals(ideaPluginDescriptor.getPluginId().toString()))
            .findFirst().orElseGet(() -> Stream.of(PluginManagerCore.getPlugins())
                .filter(pluginDescriptor -> pluginId.equals(pluginDescriptor.getPluginId())).findFirst()
                .orElse(null));
        if (plugin == null) {
            return false;
        } else {
            return plugin.isEnabled() == !STATUS_CHANGE_IDS.contains(pluginId);
        }
    }

    /**
     * needRestart
     *
     * @param pluginId pluginId
     * @return boolean
     */
    public static boolean needRestart(PluginId pluginId) {
        return INSTALLED_PLUGINS_STATE.wasInstalled(pluginId)
            || INSTALLED_PLUGINS_STATE.wasUpdated(pluginId) || UNINSTALLED_IDS.contains(pluginId);
    }

    /**
     * getPlugins
     *
     * @return List
     * @throws IOException IOException
     */
    public static List<PluginNode> getPlugins() throws IOException {
        List<PluginNode> pluginList = CommonUtil.getPluginList();
        pluginList.forEach(pluginNode ->
            LATEST_MAP.put(pluginNode.getPluginId().getIdString(), pluginNode));
        return pluginList;
    }

    /**
     * loadPlugins
     *
     * @param pluginListViewer pluginListViewer
     * @throws IOException IOException
     */
    public static void loadPlugins(PluginListViewer pluginListViewer) throws IOException {
        MarketPlaceManager.PluginUpdateCheckViewer pluginUpdateCheckViewer
            = new MarketPlaceManager.PluginUpdateCheckViewer(() -> {});

        List<PluginNode> pluginList = CommonUtil.getPluginList();
        pluginUpdateCheckViewer.show(pluginList);
        pluginListViewer.show(pluginList);
    }

    /**
     * enablePlugin
     *
     * @param pluginId pluginId
     */
    public static void enablePlugin(PluginId pluginId) {
        PluginManagerCore.enablePlugin(pluginId);
        toggleAddStatusChangePlugin(pluginId);
        LeftMainPanelUtil.refreshLeftMainPanel(pluginId);
    }

    /**
     * disablePlugin
     *
     * @param pluginId pluginId
     */
    public static void disablePlugin(PluginId pluginId) {
        PluginManagerCore.disablePlugin(pluginId);
        toggleAddStatusChangePlugin(pluginId);
    }

    private static void toggleAddStatusChangePlugin(PluginId pluginId) {
        if (STATUS_CHANGE_IDS.contains(pluginId)) {
            STATUS_CHANGE_IDS.remove(pluginId);
        } else {
            STATUS_CHANGE_IDS.add(pluginId);
        }

        ListenerManager.trigger(ListenerManager.PluginEnableStatusChange.class,
            isInstallStatusChange || STATUS_CHANGE_IDS.size() != 0);
        ListenerManager.trigger(ListenerManager.PluginActionDone.class, pluginId);
    }

    /**
     * uninstallPlugin
     *
     * @param pluginId pluginId
     */
    public static void uninstallPlugin(PluginId pluginId) {
        try {
            IdeaPluginDescriptor ideaPluginDescriptor = UN_BUNDLED_PLUGINS
                .stream().filter(plugin -> pluginId.getIdString().equals(plugin.getPluginId()
                    .getIdString())).findFirst().orElse(null);
            if (ideaPluginDescriptor == null) {
                return;
            }
            PluginInstallerRef.prepareToUninstall(ideaPluginDescriptor);
            UNINSTALLED_IDS.add(pluginId);
            ListenerManager.trigger(ListenerManager.PluginActionDone.class, pluginId);
            ListenerManager.trigger(ListenerManager.PluginEnableStatusChange.class,
                isInstallStatusChange || STATUS_CHANGE_IDS.size() != 0);
            LeftMainPanelUtil.refreshLeftMainPanel(pluginId);
        } catch (IOException | InvocationTargetException var2) {
            logger.warn("uninstall error {}", pluginId);
        }
    }

    /**
     * installPlugin
     *
     * @param pluginIds pluginIds
     */
    public static void installPlugin(PluginId[] pluginIds) {
        installPlugin(null, pluginIds);
    }

    /**
     * installPlugin
     *
     * @param enableDepends enableDepends
     * @param pluginId      pluginId
     */
    public static void installPlugin(Runnable enableDepends, PluginId[] pluginId) {
        PluginId installId = pluginId[0];
        if (!INSTALING_LIST.contains(installId)) {
            INSTALING_LIST.add(installId);
            if (pluginId != null) {
                InsteadUtil.insteadPluginInstallDialog(pluginId).ifPresent(insteadPlugin ->
                    installPluginAndExecute(() -> {
                        if (enableDepends != null) {
                            enableDepends.run();
                        }
                        insteadPlugin.disableReplacedPlugins().run();
                    }, insteadPlugin.getPluginIds()));
            }
        }
    }

    /**
     * installPluginAndExecute
     *
     * @param runnable  runnable
     * @param pluginIds pluginIds
     */
    private static void installPluginAndExecute(Runnable runnable, PluginId[] pluginIds) {
        List<IdeaPluginDescriptor> ideaPluginDescriptors = Stream.of(pluginIds)
            .map(pluginId -> LATEST_MAP.get(pluginId.getIdString()))
            .filter(plugin -> plugin != null && (!isInstalled(plugin.getPluginId())
                || hasNewerVersion(plugin.getPluginId()))).collect(Collectors.toList());
        if (ideaPluginDescriptors.size() != 0) {
            try {
                List<PluginNode> pluginNodeList = new ArrayList<>(1);
                ideaPluginDescriptors.forEach(pluginDescriptor -> {
                    if (pluginDescriptor instanceof PluginNode) {
                        pluginNodeList.add((PluginNode) pluginDescriptor);
                    }
                });
                Class<?> clazz = getPluginEnablerClass();
                PluginManagerMain.PluginEnabler.HEADLESS instance = new PluginManagerMain.PluginEnabler.HEADLESS();
                Runnable onInstallRunnable = getOnInstallRunnable(runnable, ideaPluginDescriptors,
                        pluginNodeList, instance, clazz);
                List<IdeaPluginDescriptor> plugins
                    = Stream.of(PluginManager.getPlugins()).collect(Collectors.toList());
                CommonUtil.downloadPlugins(pluginNodeList, plugins, onInstallRunnable, instance, clazz);
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> IOExceptionDialog
                    .showErrorDialog(DevkitI18NServer.toLocale("action_download_and_install_plugin"),
                        DevkitI18NServer.toLocale("error_plugin_download_failed"))
                );
            }
        } else {
            logger.debug("plugin not found in marketplace");
        }
    }

    private static Class<?> getPluginEnablerClass() throws ClassNotFoundException {
        Class<?> clazz = null;
        if (CommonUtil.isNewApiVersion()) {
            clazz = Class.forName("com.intellij.ide.plugins.PluginEnabler");
        } else {
            clazz = Class.forName("com.intellij.ide.plugins.PluginManagerMain$PluginEnabler");
        }
        return clazz;
    }

    private static Runnable getOnInstallRunnable(Runnable runnable, List<IdeaPluginDescriptor> ideaPluginDescriptors,
                                                    List<PluginNode> pluginNodeList,
                                                    PluginManagerMain.PluginEnabler.HEADLESS instance,
                                                    Class<?> finalClazz) {
        return () -> {
            try {
                Method suggestToEnableInstalledDependantPluginsMethod = PluginManagerMain.class.getMethod(
                        "suggestToEnableInstalledDependantPlugins", finalClazz, List.class);
                suggestToEnableInstalledDependantPluginsMethod.invoke(null, instance, pluginNodeList);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("invoke suggestToEnableInstalledDependantPlugins method failed.");
            }
            Set<String> depends = new HashSet<>();
            ideaPluginDescriptors.forEach(pluginDescriptor -> {
                INSTALLED_PLUGINS_STATE.getInstalledPlugins().stream()
                        .filter(plugin ->
                                plugin.getPluginId().equals(pluginDescriptor.getPluginId())).findFirst()
                        .ifPresent(plugin -> plugin.getDependencies()
                                .forEach(pluginId -> depends.add(pluginId.getPluginId().getIdString()))
                        );
                ListenerManager.trigger(ListenerManager.PluginActionDone.class, pluginDescriptor.getPluginId());
                ListenerManager.trigger(ListenerManager.PluginEnableStatusChange.class,
                        isInstallStatusChange || STATUS_CHANGE_IDS.size() != 0);
                LeftMainPanelUtil.refreshLeftMainPanel(pluginDescriptor.getPluginId());
                INSTALING_LIST.remove(pluginDescriptor.getPluginId());
            });
            runnable.run();
            PluginManagerMain.notifyPluginsUpdated(null);
        };
    }

    /**
     * showPluginViewer
     *
     * @param pluginId pluginId
     */
    public static void showPluginViewer(String pluginId) {
        if (LATEST_MAP.containsKey(pluginId)) {
            showPluginViewer(LATEST_MAP.get(pluginId));
        }
    }

    /**
     * showPluginViewer
     *
     * @param ideaPluginDescriptor ideaPluginDescriptor
     */
    public static void showPluginViewer(PluginNode ideaPluginDescriptor) {
        PluginVirtualFile pluginInfoVirtualFile = new PluginVirtualFile(ideaPluginDescriptor);
        Stream.of(ProjectManager.getInstance().getOpenProjects()).filter(project -> {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            return window != null && window.isActive();
        }).findFirst().ifPresent(project -> {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, pluginInfoVirtualFile);
            fileEditorManager.openEditor(fileDescriptor, true);
            fileEditorManager.setSelectedEditor(pluginInfoVirtualFile, "kunpeng-plugin-marketplace");
        });
    }

    /**
     * latestPlugin
     *
     * @param pluginId pluginId
     * @return IdeaPluginDescriptor
     */
    public static IdeaPluginDescriptor latestPlugin(PluginId pluginId) {
        return LATEST_MAP.get(pluginId.getIdString());
    }

    /**
     * PluginUpdateCheckViewer
     */
    public static class PluginUpdateCheckViewer implements PluginListViewer {
        private final Runnable installedUpdate;

        /**
         * PluginUpdateCheckViewer
         *
         * @param installedUpdate installedUpdate
         */
        public PluginUpdateCheckViewer(Runnable installedUpdate) {
            this.installedUpdate = installedUpdate;
        }

        /**
         * show
         *
         * @param plugins plugins
         */
        @Override
        public void show(List<PluginNode> plugins) {
            if (plugins != null && plugins.size() != 0) {
                this.installedUpdate.run();
            }
        }
    }
}
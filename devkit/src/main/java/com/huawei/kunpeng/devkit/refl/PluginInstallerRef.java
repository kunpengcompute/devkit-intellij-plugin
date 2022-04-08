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

package com.huawei.kunpeng.devkit.refl;

import com.huawei.kunpeng.devkit.MarketPlaceManager;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginInstaller;
import com.intellij.ide.startup.StartupActionScriptManager;
import com.intellij.openapi.extensions.PluginId;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * PluginInstallerRef
 *
 * @since 2021-05-18
 */
public class PluginInstallerRef {
    private static final Object LOCK = new Object();

    /**
     * PluginInstallerRef
     */
    public PluginInstallerRef() {
    }

    /**
     * prepareToUninstall
     *
     * @param ideaPluginDescriptor ideaPluginDescriptor
     * @throws InvocationTargetException InvocationTargetException
     * @throws IOException               IOException
     */
    public static void prepareToUninstall(IdeaPluginDescriptor ideaPluginDescriptor)
            throws InvocationTargetException, IOException {
        try {
            PluginInstaller.class.getMethod("prepareToUninstall", IdeaPluginDescriptor.class);
            synchronized (LOCK) {
                if (MarketPlaceManager.isInstalled(ideaPluginDescriptor.getPluginId())
                        && ideaPluginDescriptor.getPluginPath() != null) {
                    StartupActionScriptManager.addActionCommand(new StartupActionScriptManager
                            .DeleteCommand(ideaPluginDescriptor.getPluginPath().toFile()));
                }
            }
        } catch (NoSuchMethodException var5) {
            try {
                Method prepareToUninstallMethod = PluginInstaller.class.getMethod("prepareToUninstall",
                        PluginId.class);
                prepareToUninstallMethod.invoke(null, ideaPluginDescriptor.getPluginId());
            } catch (IllegalAccessException | NoSuchMethodException var3) {
                throw new IOException("ide uninstall method changed");
            }
        }
    }
}
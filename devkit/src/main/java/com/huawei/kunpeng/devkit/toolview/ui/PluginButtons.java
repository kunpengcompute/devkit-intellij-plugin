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

package com.huawei.kunpeng.devkit.toolview.ui;

import com.huawei.kunpeng.devkit.MarketPlaceManager;
import com.huawei.kunpeng.devkit.actions.MouseClickAction;
import com.huawei.kunpeng.devkit.common.utils.CommonUtil;
import com.huawei.kunpeng.devkit.common.utils.PluginUtil;
import com.huawei.kunpeng.devkit.listen.ListenerManager;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.ui.Messages;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 插件详情界面，插件可执行操作按钮控件
 *
 * @since 2021-05-18
 */
public class PluginButtons extends JPanel implements Disposable, ListenerManager.PluginActionDone {
    private static final int INSTALL_INDEX = 0;

    private static final int UPDATE_INDEX = 1;

    private static final int ENABLE_INDEX = 2;

    private static final int DISABLE_INDEX = 3;

    private static final int UNINSTALL_INDEX = 4;

    private static final int ACTION_BUTTON_SIZE = 5;

    private final PluginId pluginId;

    private final InstallButton restartButton;

    private final InstallButton installButton;

    private final JButton updateButton;

    private final ColorButton enableButton;

    private final ColorButton disableButton;

    private final ColorButton uninstallButton;

    /**
     * PluginButtons
     *
     * @param pluginId pluginId
     */
    public PluginButtons(PluginId pluginId) {
        this.pluginId = pluginId;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        restartButton = new InstallButton(true);
        restartButton.setText("Restart IDE");
        restartButton.addMouseListener(new MouseClickAction(CommonUtil::restartIDE));
        add(restartButton);
        installButton = new InstallButton(true);
        installButton.addMouseListener(new MouseClickAction(
                () -> MarketPlaceManager.installPlugin(new PluginId[]{pluginId})));
        add(installButton);
        updateButton = new JButton("Update");
        updateButton.addMouseListener(new MouseClickAction(
                () -> MarketPlaceManager.installPlugin(new PluginId[]{pluginId})));
        add(updateButton);
        enableButton = new ColorButton();
        enableButton.setText("Enable");
        enableButton.addMouseListener(new MouseClickAction(() -> MarketPlaceManager.enablePlugin(pluginId)));
        add(enableButton);
        disableButton = new ColorButton();
        disableButton.addMouseListener(new MouseClickAction(() -> MarketPlaceManager.disablePlugin(pluginId)));
        disableButton.setText("Disable");
        add(disableButton);
        uninstallButton = new ColorButton();
        uninstallButton.setText("Uninstall");
        uninstallButton.addMouseListener(new MouseClickAction(() -> {
            int yes = Messages
                    .showYesNoCancelDialog("Do you really want to uninstall this plugin?", "Devkit Plugins", null);
            if ((pluginId != null) && (yes == 0)) {
                MarketPlaceManager.uninstallPlugin(pluginId);
            }
        }));
        add(uninstallButton);
        add(Box.createHorizontalGlue());
        refreshButtons();
        ListenerManager.registerListener(this);
    }

    @Override
    public void action(PluginId changePluginId) {
        if (pluginId.getIdString().equals(changePluginId.getIdString())) {
            refreshButtons();
        }
    }

    @Override
    public void dispose() {
        ListenerManager.removeListener(this);
    }

    private void refreshButtons() {
        boolean needRestart = MarketPlaceManager.needRestart(pluginId);
        this.restartButton.setVisible(needRestart);
        boolean[] visible = new boolean[ACTION_BUTTON_SIZE];
        if (!needRestart) {
            visible[UNINSTALL_INDEX] = MarketPlaceManager.isInstalled(pluginId);
            visible[INSTALL_INDEX] = !visible[UNINSTALL_INDEX];
            if (visible[UNINSTALL_INDEX]) {
                visible[DISABLE_INDEX] = MarketPlaceManager.isEnable(pluginId);
                visible[ENABLE_INDEX] = !visible[DISABLE_INDEX];
                visible[UPDATE_INDEX] = MarketPlaceManager.hasNewerVersion(pluginId);
            }
        }
        installButton.setVisible(visible[INSTALL_INDEX]);
        updateButton.setVisible(visible[UPDATE_INDEX]);
        if (visible[UPDATE_INDEX]) {
            PluginUtil.updateToolTip(pluginId).ifPresent(updateButton::setToolTipText);
        }
        enableButton.setVisible(visible[ENABLE_INDEX]);
        disableButton.setVisible(visible[DISABLE_INDEX]);
        uninstallButton.setVisible(visible[UNINSTALL_INDEX]);
    }
}
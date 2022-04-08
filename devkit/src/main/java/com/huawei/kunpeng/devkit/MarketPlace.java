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

import com.huawei.kunpeng.devkit.actions.MouseClickAction;
import com.huawei.kunpeng.devkit.common.i18n.DevkitI18NServer;
import com.huawei.kunpeng.devkit.toolview.ui.GreenButton;
import com.huawei.kunpeng.devkit.toolview.ui.PluginPanel;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.components.BorderLayoutPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 插件入口左侧panel类
 *
 * @since 2021-8-14
 */
public class MarketPlace extends IDEBasePanel {
    /**
     * 加载panel
     */
    public JBLoadingPanel loadingPanel = new JBLoadingPanel(new BorderLayout(), CommonUtil.getDefaultProject());

    /**
     * 存储插件入口插件信息
     */
    public Map<PluginId, PluginPanel> idPanelMap = new HashMap<>();

    /**
     * 保存当前的加载组件便于随时启动或停止
     */
    private final JPanel centerPanel = new BorderLayoutPanel();

    public MarketPlace() {
        BorderLayout layout = new BorderLayout();
        layout.setHgap(0);
        layout.setVgap(0);
        this.setLayout(layout);
        this.addComponentListener(
                new ComponentAdapter() {
                    /**
                     * componentResized
                     *
                     * @param e ComponentEvent
                     */
                    @Override
                    public void componentResized(ComponentEvent e) {
                        if (centerPanel != null) {
                            setCenterPanelInfo();
                        }
                    }
                });
        setCenterPanel();
    }

    private void setCenterPanelInfo() {
        Dimension dimension;
        if (MarketPlace.this.getWidth() <= 300) {
            if (centerPanel.getWidth() > 290) {
                dimension = new Dimension(290, centerPanel.getHeight());
                centerPanel.setMinimumSize(dimension);
                centerPanel.setMaximumSize(new Dimension(290, 32767));
            }
        } else {
            dimension = new Dimension(MarketPlace.this.getWidth() - 10, centerPanel.getHeight());
            centerPanel.setMinimumSize(dimension);
            centerPanel.setMaximumSize(new Dimension(centerPanel.getWidth() - 10, 32767));
        }
    }

    /**
     * setCenterPanel setCenterPanel
     */
    public void setCenterPanel() {
        centerPanel.removeAll();
        this.removeAll();
        UIUtil.invokeLaterIfNeeded(() -> loadingPanel.startLoading());
        this.add(loadingPanel);
        this.updateUI();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        Future<?> getPlugin =
                ApplicationManager.getApplication()
                        .executeOnPooledThread(
                                () -> {
                                    List<PluginNode> pluginList;
                                    try {
                                        pluginList = MarketPlaceManager.getPlugins();
                                    } catch (IOException e) {
                                        setErrorPanel();
                                        return;
                                    } finally {
                                        this.remove(loadingPanel);
                                    }
                                    pluginList.forEach(
                                            pluginNode -> {
                                                PluginPanel pluginPanel = new PluginPanel(pluginNode);
                                                idPanelMap.put(pluginNode.getPluginId(), pluginPanel);
                                                centerPanel.add(pluginPanel);
                                            });
                                    centerPanel.add(Box.createVerticalGlue());
                                    this.add(centerPanel);
                                    this.updateUI();
                                });

        // 以下代码可用 ScheduledExecutorService 代替
        // 在多线程环境下更安全,但尚不知具体业务功能,暂不改动.
        Timer timer = new Timer();
        TimerTask task =
                new TimerTask() {
                    @Override
                    public void run() {
                        if (!getPlugin.isDone()) {
                            getPlugin.cancel(true);
                            setErrorPanel();
                        }
                    }
                };
        timer.schedule(task, 10000);
    }

    private void setErrorPanel() {
        this.removeAll();
        centerPanel.setLayout(new BorderLayout());
        JPanel textPanel = new JPanel(new BorderLayout());
        JLabel textLabel = new JLabel(DevkitI18NServer.toLocale("network_error"));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        textPanel.add(textLabel, BorderLayout.CENTER);
        textPanel.setPreferredSize(new Dimension(-1, 80));
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(textPanel, BorderLayout.NORTH);
        JButton jButton = new GreenButton(DevkitI18NServer.toLocale("retry"),
                new JBColor(new Color(75, 110, 175), new Color(75, 110, 175)));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(120, 20));
        jButton.addMouseListener(new MouseClickAction(this::setCenterPanel));
        buttonPanel.add(jButton);
        errorPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(errorPanel, BorderLayout.CENTER);
        this.add(centerPanel);
        this.updateUI();
    }

    @Override
    protected void registerComponentAction() {}

    @Override
    protected void setAction(IDEPanelBaseAction action) {}

    public Map<PluginId, PluginPanel> getIdPanelMap() {
        return idPanelMap;
    }
}
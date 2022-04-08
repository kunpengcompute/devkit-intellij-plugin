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

import com.huawei.kunpeng.devkit.KPIconProvider;
import com.huawei.kunpeng.devkit.actions.MouseClickAction;
import com.huawei.kunpeng.devkit.common.utils.KPPlugins;
import com.huawei.kunpeng.devkit.common.utils.KPUIUtils;
import com.huawei.kunpeng.devkit.common.utils.PluginUtil;
import com.huawei.kunpeng.devkit.listen.ListenerManager;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.Gray;
import com.intellij.util.ui.UIUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 插件详情界面
 *
 * @since 2021-05-18
 */
public class PluginViewer implements FileEditor {
    private static final int ICON_PANEL_SIZE = 128;

    private static final int ICON_SIZE = 128;

    private static final int ICON_XY = 10;

    private static final int FILL_SPACING_SIZE = 24;

    private final JPanel pluginDetails;
    private final PluginVirtualFile pluginFile;
    private PluginNode pluginDescriptor;
    private PluginButtons pluginButtons;
    private JLabel iconLabel;
    private JLabel pluginName;
    private JLabel pluginVersion;
    private PluginInfoTab pluginInfoTab;

    /**
     * PluginViewer
     *
     * @param file file
     */
    public PluginViewer(PluginVirtualFile file) {
        this.pluginDetails = new JPanel();
        pluginDetails.setLayout(new BorderLayout());
        this.pluginDescriptor = file.getDescriptor();
        createHeader();
        createTabsAndDetails();
        listenPluginChange();
        this.pluginFile = file;
    }

    private void listenPluginChange() {
        ListenerManager.registerListener((ListenerManager.PluginChange) changePlugin -> {
            if (!changePlugin.getPluginId().getIdString().equals(this.pluginDescriptor.getPluginId().getIdString())) {
                // 改变状态或版本的不是该插件
                return;
            }
            PluginUtil.isPluginChanged(pluginDescriptor, changePlugin).ifPresent(latestPlugin -> {
                this.pluginDescriptor = changePlugin;
                this.viewPluginInfo();
            });
        });
    }

    @Override
    @Nullable
    public VirtualFile getFile() {
        return this.pluginFile;
    }

    /**
     * 信息改变需要更新版本号、插件图标、插件描述、插件日志
     */
    private void viewPluginInfo() {
        iconLabel.setIcon(KPIconProvider.getPluginIcon(pluginDescriptor, true));
        pluginName.setText(pluginDescriptor.getName());
        pluginVersion.setText(pluginDescriptor.getVersion());
        this.pluginInfoTab.descChange(pluginDescriptor);
        pluginButtons.action(pluginDescriptor.getPluginId());
    }

    private void createTabsAndDetails() {
        pluginInfoTab = new PluginInfoTab(pluginDescriptor);
        pluginDetails.add(PluginInfoTab.createTabs(pluginInfoTab), BorderLayout.CENTER);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ICON_PANEL_SIZE));
        headerPanel.setMinimumSize(new Dimension(0, ICON_PANEL_SIZE));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(null);
        Dimension iconDimension = new Dimension(ICON_PANEL_SIZE, ICON_PANEL_SIZE);
        iconPanel.setMaximumSize(iconDimension);
        iconPanel.setMinimumSize(iconDimension);
        iconPanel.setPreferredSize(iconDimension);
        iconLabel = new JLabel(KPIconProvider.getPluginIcon(pluginDescriptor, true));
        iconLabel.setBounds(ICON_XY, ICON_XY, ICON_SIZE, ICON_SIZE);
        iconPanel.add(iconLabel);
        headerPanel.add(iconPanel);
        headerPanel.add(Box.createHorizontalStrut(FILL_SPACING_SIZE));
        headerPanel.add(createInfoPanel());
        pluginDetails.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createInfoPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(createPluginNamePanel());
        jPanel.add(createAuthorAndRatePanel());
        pluginButtons = new PluginButtons(pluginDescriptor.getPluginId());
        jPanel.add(pluginButtons);
        return jPanel;
    }

    private JPanel createPluginNamePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
        pluginName = new JLabel(pluginDescriptor.getName());
        pluginName.addMouseListener(
                new MouseClickAction(() -> KPPlugins.openPluginURL(pluginDescriptor.getExternalPluginId())));
        Font labelFont = UIUtil.getLabelFont();
        if (labelFont != null) {
            pluginName.setFont(labelFont.deriveFont(KPUIUtils.DETAIL_PLUGIN_NAME_SIZE));
        }
        jPanel.add(pluginName);
        jPanel.add(Box.createHorizontalStrut(KPUIUtils.BOX_SIZE_10));
        String vendor = pluginDescriptor.getVendor();
        JLabel vendorLabel = new JLabel(vendor);
        vendorLabel.setFont(new Font("Arial-ItalicMT", Font.ITALIC, 12));
        jPanel.add(vendorLabel);
        jPanel.add(Box.createHorizontalGlue());
        return jPanel;
    }

    /**
     * createAuthorAndRatePanel
     *
     * @return JPanel
     */
    public JPanel createAuthorAndRatePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
        JLabel author = new JLabel("Kunpeng");
        author.addMouseListener(new MouseClickAction(() -> KPPlugins.openVendor("Kunpeng")));
        jPanel.add(author);
        Dimension dimension = new Dimension(KPUIUtils.BOX_SIZE_28, KPUIUtils.TEXT_HEIGHT);
        jPanel.add(new SplitLineStruct(dimension));
        String download = KPPlugins.getTotalDownloads(pluginDescriptor.getPluginId());
        jPanel.add(new JLabel(download, KPIconProvider.DOWNLOAD_ICON, SwingConstants.LEFT));
        jPanel.add(new SplitLineStruct(dimension));
        jPanel.add(KPUIUtils.createRateComponent(KPPlugins.getRatingFloat(pluginDescriptor.getPluginId())));
        jPanel.add(new SplitLineStruct(dimension));
        pluginVersion = new JLabel(pluginDescriptor.getVersion());
        jPanel.add(pluginVersion);
        jPanel.add(Box.createHorizontalGlue());
        jPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, KPUIUtils.BOX_SIZE_10, 0));
        return jPanel;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return pluginDetails;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return pluginButtons;
    }

    @NotNull
    @Override
    public String getName() {
        return "KunpengMarketPlace";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {
    }

    @Override
    public void deselectNotify() {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        if (pluginButtons != null) {
            pluginButtons.dispose();
        }
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {
    }

    /**
     * 评分 下载数间隔
     */
    public static class SplitLineStruct extends Box.Filler {
        private static final int LINE_SPLIT = 2;

        private final int width;

        private final int height;

        public SplitLineStruct(Dimension size) {
            super(size, size, size);
            this.width = (int) size.getWidth();
            this.height = (int) size.getHeight();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Gray._94);
            int length = width / LINE_SPLIT;
            graphics.drawLine(length, 0, length, height);
        }
    }
}
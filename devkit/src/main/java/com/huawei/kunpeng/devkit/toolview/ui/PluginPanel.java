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

import com.huawei.kunpeng.devkit.JsoupHelper;
import com.huawei.kunpeng.devkit.MarketPlaceManager;
import com.huawei.kunpeng.devkit.actions.MouseClickAction;
import com.huawei.kunpeng.devkit.common.i18n.DevkitI18NServer;
import com.huawei.kunpeng.devkit.common.utils.CommonUtil;
import com.huawei.kunpeng.devkit.common.utils.PluginUtil;

import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * PluginPanel
 *
 * @since 2021-05-18
 */
public class PluginPanel extends JPanel {
    private static final int MAX_HEIGHT = 70;
    private static final int ICON_WIDTH = 70;
    private static final int ICON_SIZE = 40;
    private static final int ICON_X = 15;
    private static final int ICON_Y = 15;
    private static final int BOX_SIZE = 5;
    private static final float NAME_SIZE = 14.0F;
    private static final int MIN_WIDTH = 260;
    private final JPanel iconPanel;
    private final JPanel infoPanel;
    private final JPanel footerPanel;
    private final RequiredButton requiredButton;
    private final boolean isShowZeroRating = true;
    private final JLabel vendorLabel;
    private final JLabel iconLabel;
    private final String iconName;
    private PluginNode plugin;
    private JPanel headerPanel;
    private JPanel descPanel;
    private JLabel descLabel;
    private JLabel downloadLabel;
    private JLabel ratingLabel;
    private boolean isRating;
    private JLabel nameLabel;
    private JLabel versionLabel;
    private int nameInitialWidth;
    private final ComponentAdapter resize = new ComponentAdapter() {
        /**
         * componentResized
         *
         * @param event ComponentEvent
         */
        @Override
        public void componentResized(ComponentEvent event) {
            super.componentResized(event);
            if (PluginPanel.this.nameLabel != null
                    && PluginPanel.this.downloadLabel != null
                    && PluginPanel.this.ratingLabel != null) {
                if (PluginPanel.this.nameInitialWidth == 0) {
                    PluginPanel.this.nameInitialWidth = PluginPanel.this.nameLabel.getWidth();
                }

                int width = PluginPanel.this.getWidth() - 70;
                int versionWidth = PluginPanel.this.versionLabel.getWidth();
                int maxWidth = width - versionWidth - 5;
                if (width < 260) {
                    PluginPanel.this.downloadLabel.setVisible(false);
                    PluginPanel.this.ratingLabel.setVisible(false);
                } else {
                    boolean ratingVisible = PluginPanel.this.isShowZeroRating || !PluginPanel.this.isRating;
                    PluginPanel.this.downloadLabel.setVisible(true);
                    PluginPanel.this.ratingLabel.setVisible(ratingVisible);
                    maxWidth -= PluginPanel.this.downloadLabel.getWidth()
                            + (ratingVisible ? PluginPanel.this.ratingLabel.getWidth() : 0);
                }

                Dimension dimension;
                if (PluginPanel.this.nameInitialWidth > maxWidth) {
                    dimension = new Dimension(maxWidth, 20);
                } else {
                    dimension = new Dimension(PluginPanel.this.nameInitialWidth, 20);
                }

                PluginPanel.this.nameLabel.setMinimumSize(dimension);
                PluginPanel.this.nameLabel.setPreferredSize(dimension);
                PluginPanel.this.nameLabel.setMaximumSize(dimension);
            }
        }
    };

    /**
     * PluginPanel
     *
     * @param pluginNode pluginNode
     */
    public PluginPanel(PluginNode pluginNode) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(JBUI.size(32767, 70));
        this.plugin = pluginNode;
        this.iconPanel = new JPanel();
        this.iconPanel.setOpaque(false);
        Dimension iconSize = JBUI.size(70, 70);
        this.iconPanel.setLayout(null);
        this.iconPanel.setMinimumSize(iconSize);
        this.iconPanel.setPreferredSize(iconSize);
        this.iconPanel.setMaximumSize(iconSize);
        this.iconLabel = new JLabel();
        iconName = pluginNode.getName().split("Kunpeng")[1].toLowerCase(Locale.ROOT).replace(" ", "");
        Icon icon = IconLoader.findIcon("/assets/img/icon/" + iconName + ".svg");
        if (icon != null) {
            iconLabel.setIcon(icon);
        }
        this.iconLabel.setBounds(15, 15, 40, 40);
        this.iconPanel.add(this.iconLabel);

        this.add(this.iconPanel);
        this.infoPanel = new JPanel();
        this.infoPanel.setPreferredSize(new Dimension(220, 70));
        this.infoPanel.setOpaque(false);
        this.infoPanel.setLayout(new BoxLayout(this.infoPanel, BoxLayout.Y_AXIS));
        this.createHeadPanel();
        this.createDescPanel();
        this.footerPanel = new JPanel();
        this.footerPanel.setOpaque(false);
        this.footerPanel.setLayout(new BoxLayout(this.footerPanel, BoxLayout.X_AXIS));
        this.vendorLabel = new JLabel("Kunpengfamily");
        this.footerPanel.add(this.vendorLabel);
        this.footerPanel.add(Box.createHorizontalGlue());
        this.requiredButton =
                new RequiredButton(RequiredButton.getTypeByPluginId(plugin.getPluginId()), plugin.getPluginId());
        this.footerPanel.add(this.requiredButton);
        this.infoPanel.add(this.footerPanel);
        this.add(this.infoPanel);
        this.initEvents();
    }

    private void createDescPanel() {
        this.descPanel = new JPanel();
        this.descPanel.setOpaque(false);
        this.descPanel.setLayout(null);
        this.descLabel = new JLabel(DevkitI18NServer.toLocale(iconName + "_tip"));
        this.descPanel.add(this.descLabel);
        this.descPanel.setMaximumSize(new Dimension(32767, 20));
        this.descPanel.addComponentListener(new ComponentAdapter() {
            /**
             * componentResized
             *
             * @param event event
             */
            @Override
            public void componentResized(ComponentEvent event) {
                super.componentResized(event);
                PluginPanel.this.descLabel.setBounds(0, 0, PluginPanel.this.descPanel.getWidth(), 20);
            }
        });
        this.infoPanel.add(this.descPanel);
    }

    private void createHeadPanel() {
        this.headerPanel = new JPanel();
        this.headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        this.headerPanel.setOpaque(false);
        this.headerPanel.setLayout(new BoxLayout(this.headerPanel, BoxLayout.X_AXIS));
        this.nameLabel = new JLabel(this.plugin.getName());
        Font labelFont = UIUtil.getLabelFont();
        if (labelFont != null) {
            this.nameLabel.setFont(labelFont.deriveFont(Font.BOLD, 14.0F));
        }
        this.headerPanel.add(this.nameLabel);
        this.headerPanel.add(Box.createHorizontalStrut(5));
        Border borderTop = BorderFactory.createEmptyBorder(5, 0, 0, 0);
        this.versionLabel = new JLabel(this.plugin.getVersion());
        this.versionLabel.setBorder(borderTop);
        this.headerPanel.add(this.versionLabel);
        this.headerPanel.add(Box.createHorizontalGlue());
        this.downloadLabel = new JLabel(plugin.getDownloads(),
                IconLoader.findIcon("/assets/img/icon/download.svg"), SwingConstants.LEFT);
        this.headerPanel.add(this.downloadLabel);
        this.headerPanel.add(Box.createHorizontalStrut(5));
        this.createRatingLabel();
        this.headerPanel.setMaximumSize(new Dimension(32767, 20));
        this.infoPanel.add(this.headerPanel);
    }

    private void createRatingLabel() {
        this.ratingLabel = new JLabel();
        ratingLabel.setText(plugin.getRating());
        ratingLabel.setIcon(IconLoader.findIcon("/assets/img/star.png"));
        this.ratingLabel.setVisible(this.isShowZeroRating || !this.isRating);
        this.headerPanel.add(this.ratingLabel);
    }

    private void settingViewInfo() {
        this.nameLabel.setText(plugin.getName());
        this.versionLabel.setText(plugin.getVersion());
        this.descLabel.setText(StringUtil.trim(JsoupHelper.htmlToText(this.plugin.getDescription())));
        this.vendorLabel.setText(this.plugin.getVendor());
        this.refreshRequiredButton();
    }

    private void initEvents() {
        this.addMouseListener(
                new MouseClickAction(
                        () -> MarketPlaceManager.showPluginViewer(plugin.getPluginId().getIdString())) {
                    /**
                     * mouseClicked
                     *
                     * @param event event
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        super.mouseClicked(event);
                        if (event.getButton() == 3) {
                            PluginActionPopup.create(plugin.getPluginId(), event.getComponent())
                                    .show(new RelativePoint(event));
                        }
                    }

                    /**
                     * mouseEntered
                     *
                     * @param event event
                     */
                    @Override
                    public void mouseEntered(MouseEvent event) {
                        super.mouseEntered(event);
                        PluginPanel.this.setBackground(UIUtil.getFocusedBoundsColor());
                        PluginPanel.this.updateUI();
                    }

                    /**
                     * mouseExited
                     *
                     * @param event event
                     */
                    @Override
                    public void mouseExited(MouseEvent event) {
                        super.mouseExited(event);
                        PluginPanel.this.setBackground(UIUtil.getLabelBackground());
                        PluginPanel.this.updateUI();
                    }
                });
        this.addComponentListener(this.resize);
    }

    /**
     * showButton
     *
     * @param pluginId pluginId
     */
    public void showButton(PluginId pluginId) {
        if (pluginId.toString().equals(this.plugin.getPluginId().getIdString())) {
            this.refreshRequiredButton();
        }
    }

    /**
     * refreshRequiredButton
     */
    public void refreshRequiredButton() {
        this.requiredButton.setRequiredType(RequiredButton.getTypeByPluginId(plugin.getPluginId()));
    }

    /**
     * refreshUI
     */
    public void refreshUI() {
        PluginUtil.isPluginChanged(plugin, MarketPlaceManager.latestPlugin(plugin.getPluginId()))
                .ifPresent(latest -> {
                    if (latest instanceof PluginNode) {
                        plugin = (PluginNode) latest;
                    }
                    this.settingViewInfo();
                });
    }

}
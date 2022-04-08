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

import com.huawei.kunpeng.devkit.actions.MouseClickAction;
import com.huawei.kunpeng.devkit.common.utils.CommonUtil;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.border.Border;

/**
 * PluginInfoTab
 *
 * @since 2021-05-18
 */
public class PluginInfoTab extends JPanel {
    private static final int MARGIN_SIZE = 10;

    private static final int SCROLL_WIDTH = 24;

    private static final int TAB_MARGIN_TOP = 15;

    private static final int TAB_MARGIN_LEFT_BOTTOM = 5;

    private static final int TAB_GAP_SIZE = 20;

    private static final int TAB_HEIGHT = 40;

    private static final int SIDE = 2;

    private static final int PLUGIN_DESC_MIN_WIDTH = 750;

    private static final Color TAB_BORDER_COLOR = new JBColor(0xb0b0b0, 0x4e4e4e);

    private static final Border SELECTED_BORDER =
        BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtil.getLabelForeground());

    private static final Border UNSELECTED_BORDER =
        BorderFactory.createEmptyBorder(0, 0, 1, 0);
    private static final String ASSETS_DESC = "/assets/desc/";

    private final String selectedTab;
    private final PluginDesc pluginDesc;
    private final String[] tabNames = new String[] {"Details", "Change notes"};
    private List<JLabel> tabs;
    private int selectedIndex;

    private String[] textArr;

    /**
     * PluginInfoTab
     *
     * @param pluginNode pluginNode
     */
    public PluginInfoTab(IdeaPluginDescriptor pluginNode) {
        selectedTab = tabNames[0];
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(TAB_MARGIN_TOP, TAB_MARGIN_LEFT_BOTTOM, TAB_MARGIN_LEFT_BOTTOM, 0));
        String filePath = ASSETS_DESC + pluginNode.getPluginId().getIdString().toLowerCase(Locale.ROOT)
            .replace(" ", "") + ".html";
        String descContent = CommonUtil.readHtmlFile(filePath);
        textArr = new String[] {descContent, pluginNode.getChangeNotes()};
        pluginDesc = new PluginDesc(tabNames[0], textArr[0]);
        IntStream.range(0, tabNames.length).forEach(index -> {
            if (!StringUtil.isEmpty(textArr[index])) {
                String tab = tabNames[index];
                add(Box.createHorizontalStrut(TAB_GAP_SIZE));
                JLabel jLabel = new JLabel(tab);
                jLabel.addMouseListener(new MouseClickAction(() -> toggleTab(index)));
                jLabel.setBorder(selectedTab.equals(tab) ? SELECTED_BORDER : UNSELECTED_BORDER);
                add(jLabel);
            }
        });
    }

    /**
     * createTabs
     *
     * @param pluginInfoTab pluginInfoTab
     * @return JPanel
     */
    public static JPanel createTabs(PluginInfoTab pluginInfoTab) {
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new GridLayout());
        tabPanel.add(pluginInfoTab);
        tabPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TAB_BORDER_COLOR));
        tabPanel.setMinimumSize(new Dimension(0, TAB_HEIGHT));
        tabPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, TAB_HEIGHT));
        JPanel tabAndDesc = new JPanel(new BorderLayout());
        tabAndDesc.add(tabPanel, BorderLayout.NORTH);
        tabAndDesc.add(pluginInfoTab.getPluginDesc(), BorderLayout.CENTER);

        return tabAndDesc;
    }

    /**
     * descChange
     *
     * @param pluginNode pluginNode
     */
    public void descChange(IdeaPluginDescriptor pluginNode) {
        textArr = new String[] {pluginNode.getDescription(), pluginNode.getChangeNotes()};
        this.showTab(selectedIndex, true);
    }

    /**
     * add
     *
     * @param comp comp
     * @return Component
     */
    @Override
    public Component add(Component comp) {
        Component child = super.add(comp);
        if (child instanceof JLabel) {
            if (tabs == null) {
                tabs = new LinkedList<>();
            }
            tabs.add((JLabel) child);
        }
        return child;
    }

    /**
     * getPluginDesc
     *
     * @return PluginDesc
     */
    public PluginDesc getPluginDesc() {
        return pluginDesc;
    }

    private void showTab(int tabIndex, boolean force) {
        selectedIndex = tabIndex;
        String tab = tabNames[tabIndex];
        tabs.forEach(label -> label.setBorder(label.getText().equals(tab) ? SELECTED_BORDER : UNSELECTED_BORDER));
        pluginDesc.selectTab(tab, textArr[tabIndex], force);
    }

    private void toggleTab(int tabIndex) {
        if (selectedIndex == tabIndex) {
            return;
        }
        showTab(tabIndex, false);
    }

    /**
     * 插件描述
     *
     * @since 2021-04-12
     */
    public static class PluginDesc extends JScrollPane {
        private final Map<String, PluginDescTextPanel> descPanels = new HashMap<>();

        private int marginMinWidth = 0;

        private int startMargin = MARGIN_SIZE;

        /**
         * PluginDesc
         *
         * @param tab  tab
         * @param text text
         */
        public PluginDesc(String tab, String text) {
            selectTab(tab, text, true);
        }

        /**
         * computeMargin
         *
         * @param width width
         * @return int
         */
        public int computeMargin(int width) {
            if (marginMinWidth == 0) {
                this.marginMinWidth = width;
            }
            return defaultMargin();
        }

        /**
         * defaultMargin
         *
         * @return int
         */
        public int defaultMargin() {
            if (this.getWidth() == 0) {
                return MARGIN_SIZE;
            }
            int containerWidth = this.getWidth() - SCROLL_WIDTH;
            startMargin = MARGIN_SIZE;
            int width = marginMinWidth == 0 ? PLUGIN_DESC_MIN_WIDTH : marginMinWidth;
            if ((containerWidth - width) > MARGIN_SIZE * SIDE) {
                startMargin = (containerWidth - width) / SIDE;
            }
            return startMargin;
        }

        /**
         * getMarginMinWidth
         *
         * @return int
         */
        public int getMarginMinWidth() {
            return marginMinWidth;
        }

        /**
         * selectTab
         *
         * @param tab   tab
         * @param text  text
         * @param force force
         */
        public void selectTab(String tab, String text, boolean force) {
            PluginDescTextPanel showingPanel;
            if (descPanels.containsKey(tab)) {
                showingPanel = descPanels.get(tab);
                if (force) {
                    showingPanel.setText(text);
                }
            } else {
                showingPanel = new PluginDescTextPanel(text, startMargin, this);
                descPanels.put(tab, showingPanel);
            }
            showingPanel.show(startMargin);
        }
    }

    /**
     * 插件详情、版本日志
     *
     * @since 2021-04-12
     */
    public static class PluginDescTextPanel extends JTextPane {
        private final PluginDesc pluginDesc;
        private int confirmedWidth;
        private int scrollPanelHeight;

        /**
         * PluginDescTextPanel
         *
         * @param text        text
         * @param startMargin startMargin
         * @param pluginDesc  pluginDesc
         */
        public PluginDescTextPanel(String text, int startMargin, PluginDesc pluginDesc) {
            this.pluginDesc = pluginDesc;
            setContentType("text/html");
            setText(text);
            addHyperlinkListener(new BrowserHyperlinkListener());
            setEditable(false);
            resetMargin(startMargin);
            this.confirmedWidth = pluginDesc.getMarginMinWidth();
            addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
                @Override
                public void ancestorResized(HierarchyEvent event) {
                    super.ancestorResized(event);
                    if (getWidth() == 0 || !(event.getChanged() instanceof JViewport)) {
                        return;
                    }
                    JViewport viewport = (JViewport) event.getChanged();
                    if (confirmedWidth == 0) { // 未确定宽度
                        if (getWidth() > pluginDesc.getWidth() || (viewport.getHeight() < scrollPanelHeight)) {
                            // 不需要尝试设置750宽度活竖向高度改变,当前宽度为最小宽度
                            confirmWidth();
                        } else if (scrollPanelHeight == 0) {
                            // 记录初始最小宽度
                            scrollPanelHeight = viewport.getHeight();
                            attemptMinWidth();
                        } else {
                            // 无法确定最小宽度,按750默认宽度处理
                            resetMargin(pluginDesc.defaultMargin());
                        }
                    } else {
                        // 没有出现横向滚动条情况下750的宽度足够展示,按750计算resize之后的margin
                        resetMargin(pluginDesc.computeMargin(confirmedWidth));
                    }
                }
            });
        }

        private void attemptMinWidth() {
            int containerWidth = pluginDesc.getWidth();
            if (containerWidth < PLUGIN_DESC_MIN_WIDTH) {
                confirmWidth();
            } else {
                resetMargin(pluginDesc.defaultMargin());
            }
        }

        private void confirmWidth() {
            Insets margin = getMargin();
            confirmedWidth = getWidth() - margin.left - margin.right;
            resetMargin(pluginDesc.computeMargin(confirmedWidth));
        }

        /**
         * show
         *
         * @param startMargin startMargin
         */
        public void show(int startMargin) {
            resetMargin(startMargin);
            pluginDesc.setViewportView(this);
        }

        private void resetMargin(int marginLeft) {
            Insets margin = getMargin();
            if (marginLeft != margin.left) {
                setMargin(JBUI.insets(MARGIN_SIZE, marginLeft, 0, marginLeft));
                updateUI();
            }
        }
    }
}

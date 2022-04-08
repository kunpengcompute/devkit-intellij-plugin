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

import com.huawei.kunpeng.devkit.common.utils.CommonUtil;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.impl.ApplicationInfoImpl;
import com.intellij.openapi.util.IconLoader;

import java.awt.Component;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.Icon;

/**
 * KPIconProvider
 *
 * @since 2021-08-25
 */
public class KPIconProvider {
    /**
     * 评分相关图标数
     */
    public static final int STAR_ICONS_COUNT = 11;

    /**
     * 评分相关图标
     */
    public static final Icon[] STAR_ICONS = new Icon[STAR_ICONS_COUNT];

    /**
     * kpplugin文件图标
     */
    public static final Icon HW_LOGO_X16 =
            IconLoader.findIcon(KPIconProvider.class.getResource("/assets/img/icon/kunPeng.svg"));

    /**
     * 华为认证插件图标大小
     */
    public static final int HUAWEI_CERTIFICATE_ICON_SIZE = 24;

    /**
     * 华为默认插件图标
     */
    public static final Icon HW_LOGO_X40 =
            IconLoader.findIcon(KPIconProvider.class.getResource("/assets/img/star.png"));

    /**
     * 华为默认插件大图标
     */
    public static final Icon HW_LOGO_X80 =
            IconLoader.findIcon(KPIconProvider.class.getResource("/assets/img/star.png"));

    /**
     * 下载图标
     */
    public static final Icon DOWNLOAD_ICON =
            IconLoader.findIcon(KPIconProvider.class.getResource("/assets/img/icon/download.svg"));

    private static final double SMALL_ICON_SIZE = 40d;

    private static final double BIG_ICON_SIZE = 80d;

    private static final int SUPPORT_SVG_LOGO_BASE_LINE = 181;

    static {
        IntStream.range(0, STAR_ICONS_COUNT).forEach(index ->
                STAR_ICONS[index] = IconLoader.findIcon(KPIconProvider.class.getResource("/assets/img/star.png")));
    }

    /**
     * getPluginIcon
     *
     * @param ideaPluginDescriptor ideaPluginDescriptor
     * @param isBig                isBig
     * @return Icon                icon
     */
    public static Icon getPluginIcon(IdeaPluginDescriptor ideaPluginDescriptor, boolean isBig) {
        if (ApplicationInfoImpl.getShadowInstance().getBuild().getBaselineVersion() < SUPPORT_SVG_LOGO_BASE_LINE) {
            return isBig ? HW_LOGO_X80 : HW_LOGO_X40;
        }
        String iconName = ideaPluginDescriptor.getName()
                .split("Kunpeng")[1].toLowerCase(Locale.ROOT).replace(" ", "");
        return IconLoader.findIcon("/assets/img/header/" + iconName + ".svg");
    }

    /**
     * 延迟加载图标
     */
    public static class LazyIcon implements Icon {
        private final Set<Component> componentSet;
        private Icon icon;

        public LazyIcon(Icon icon) {
            this.icon = icon;
            componentSet = new HashSet<>(0);
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
            componentSet.forEach(component -> {
                if (component.isShowing()) {
                    component.repaint();
                }
            });
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int xInt, int yInt) {
            if (icon != null) {
                icon.paintIcon(component, graphics, xInt, yInt);
            }
            componentSet.add(component);
        }

        @Override
        public int getIconWidth() {
            return icon.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return icon.getIconWidth();
        }
    }
}
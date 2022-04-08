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

import com.huawei.kunpeng.devkit.KPIconProvider;

import com.intellij.ui.JBColor;

import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.IntStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * KPUIUtils
 *
 * @since 2021-08-25
 */
public class KPUIUtils {
    /**
     * 默认文本行高度
     */
    public static final int TEXT_HEIGHT = 20;

    /**
     * 插件编辑窗插件名字体大小
     */
    public static final float DETAIL_PLUGIN_NAME_SIZE = 20f;

    /**
     * 插件间距4
     */
    public static final int BOX_SIZE_4 = 4;

    /**
     * 插件间距10
     */
    public static final int BOX_SIZE_10 = 10;

    /**
     * 插件间距28
     */
    public static final int BOX_SIZE_28 = 28;

    /**
     * 评分显示款段
     */
    public static final int STAR_PANEL_WIDTH = 100;

    /**
     * 评分进位数
     */
    public static final int RATE = 10;

    /**
     * 最大评分数
     */
    public static final int RATE_START = 5;

    /**
     * font size 14
     */
    public static final float FONT_SIZE_14 = 14.0f;

    /**
     * border width 3
     */
    public static final int BORDER_3 = 3;

    /**
     * 插件列表中插件组件最小宽度
     */
    public static final int PLUGIN_MIN_WIDTH = 300;

    /**
     * 插件详情\更新日志显示最小宽度
     */
    public static final int PLUGIN_DESC_MIN_WIDTH = 750;

    /**
     * 绿色背景
     */
    public static final Color GREEN_COLOR = new JBColor(0x5D9B47, 0x2B7B50);

    /**
     * 白色前景
     */
    public static final Color WHITE_FOREGROUND_COLOR = new JBColor(JBColor.WHITE, new Color(0xBBBBBB));

    private KPUIUtils() {
    }

    /**
     * createRateComponent createRateComponent
     *
     * @param rating rating
     * @return JComponent
     */
    public static JComponent createRateComponent(float rating) {
        JPanel ratePanel = new JPanel();
        ratePanel.setOpaque(false);
        ratePanel.setLayout(new BoxLayout(ratePanel, BoxLayout.X_AXIS));
        int rate = Math.round(rating * RATE);
        IntStream.range(0, RATE_START).forEach(index -> {
            int num = rate - (index * RATE);
            num = (num > RATE) ? RATE : (Math.max(num, 0));
            if (index != 0) {
                ratePanel.add(Box.createHorizontalStrut(BOX_SIZE_4));
            }
            ratePanel.add(new JLabel(KPIconProvider.STAR_ICONS[num]));
        });
        Dimension dimension = new Dimension(STAR_PANEL_WIDTH, TEXT_HEIGHT);
        ratePanel.setMaximumSize(dimension);
        ratePanel.setMinimumSize(dimension);
        ratePanel.setPreferredSize(dimension);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setMinimumFractionDigits(1);
        ratePanel.setToolTipText(String.format(Locale.ENGLISH, "Score: %s", numberFormat.format(rating)));
        return ratePanel;
    }
}

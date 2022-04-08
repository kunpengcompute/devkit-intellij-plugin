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

import com.intellij.ui.JBColor;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * ColorButton
 *
 * @since 2021-05-21
 */
public class ColorButton extends JButton {
    /**
     * 颜色常量：白色前景
     */
    protected static final Color WHITE_FOREGROUND = new JBColor(JBColor.WHITE, new Color(0xBBBBBB));

    /**
     * ColorButton
     */
    public ColorButton() {
        setOpaque(false);
    }

    /**
     * setWidth
     *
     * @param button button
     * @param noScaleWidth noScaleWidth
     */
    public static void setWidth(@NotNull JButton button, int noScaleWidth) {
        Border border = button.getBorder();
        int noScaleWidthResult = 0;
        if (border != null) {
            Insets insets = border.getBorderInsets(button);
            noScaleWidthResult = noScaleWidth + insets.left + insets.right;
        }
        button.setPreferredSize(new Dimension(noScaleWidthResult, button.getPreferredSize().height));
    }

    /**
     * setWidth72
     *
     * @param button button
     */
    public static void setWidth72(@NotNull JButton button) {
        setWidth(button, 72);
    }

    /**
     * 设置文本颜色
     *
     * @param color Color颜色
     */
    protected final void setTextColor(@NotNull Color color) {
        putClientProperty("JButton.textColor", color);
    }

    /**
     * setFocusedTextColor
     *
     * @param color color
     */
    protected final void setFocusedTextColor(@NotNull Color color) {
        putClientProperty("JButton.focusedTextColor", color);
    }

    /**
     * setBgColor
     *
     * @param color color
     */
    protected final void setBgColor(@NotNull Color color) {
        putClientProperty("JButton.backgroundColor", color);
    }

    /**
     * setFocusedBgColor
     *
     * @param color color
     */
    protected final void setFocusedBgColor(@NotNull Color color) {
        putClientProperty("JButton.focusedBackgroundColor", color);
    }

    /**
     * setBorderColor
     *
     * @param color color
     */
    protected final void setBorderColor(@NotNull Color color) {
        putClientProperty("JButton.borderColor", color);
    }

    /**
     * setFocusedBorderColor
     *
     * @param color color
     */
    protected final void setFocusedBorderColor(@NotNull Color color) {
        putClientProperty("JButton.focusedBorderColor", color);
    }
}


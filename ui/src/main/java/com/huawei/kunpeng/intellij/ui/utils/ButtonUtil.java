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

package com.huawei.kunpeng.intellij.ui.utils;

import com.intellij.ui.JBColor;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import javax.swing.JButton;

/**
 * 按钮样式设置国内工具类
 *
 * @since 2.3.T20
 */
public class ButtonUtil {
    /**
     *  设置按钮文本颜色
     *
     * @param button 按钮
     * @param color 颜色
     */
    public static void setTextColor(@NotNull JButton button, @NotNull Color color) {
        button.putClientProperty("JButton.textColor", color);
    }

    /**
     *  设置按钮聚焦文本颜色
     *
     * @param button 按钮
     * @param color 颜色
     */
    public static void setFocusedTextColor(@NotNull JButton button, @NotNull Color color) {
        button.putClientProperty("JButton.focusedTextColor", color);
    }

    /**
     *  设置按钮背景色
     *
     * @param button 按钮
     * @param color 颜色
     */
    public static void setBgColor(@NotNull JButton button, @NotNull Color color) {
        button.putClientProperty("JButton.backgroundColor", color);
    }

    /**
     *  设置按钮聚焦背景色
     *
     * @param button 按钮
     * @param color 颜色
     */
    public static void setFocusedBgColor(@NotNull JButton button, @NotNull Color color) {
        button.putClientProperty("JButton.focusedBackgroundColor", color);
    }

    /**
     *  设置按钮边界颜色
     *
     * @param button 按钮
     * @param color 颜色
     */
    public static void setBorderColor(@NotNull JButton button, @NotNull Color color) {
        button.putClientProperty("JButton.borderColor", color);
    }

    /**
     *  设置按钮聚焦边界颜色
     *
     * @param button 按钮
     * @param color 颜色
     */
    public static void setFocusedBorderColor(@NotNull JButton button, @NotNull Color color) {
        button.putClientProperty("JButton.focusedBorderColor", color);
    }


    /**
     * 设置通用按钮样式。
     *
     * @param button 按钮
     */
    public static void setCommonButtonStyle(@NotNull JButton button) {
        ButtonUtil.setBgColor(button,
                new JBColor(new Color(75, 137, 202), new Color(54, 88, 129)));
        ButtonUtil.setBorderColor(button,
                new JBColor(new Color(151, 195, 243), new Color(71, 106, 137)));
        ButtonUtil.setTextColor(button, new JBColor(Color.white, new Color(187, 187, 187)));
    }
}

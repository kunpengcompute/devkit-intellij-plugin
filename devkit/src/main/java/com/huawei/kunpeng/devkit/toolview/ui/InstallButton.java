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

import java.awt.Color;

/**
 * InstallButton
 *
 * @since 2021-05-18
 */
public class InstallButton extends ColorButton {
    private static final Color GREEN_COLOR = new JBColor(0x5D9B47, 0x2B7B50);

    private static final Color FILL_FOREGROUND_COLOR = WHITE_FOREGROUND;

    private static final Color FILL_BACKGROUND_COLOR = GREEN_COLOR;

    private static final Color FOREGROUND_COLOR = GREEN_COLOR;

    private static final Color BACKGROUND_COLOR = WHITE_FOREGROUND;

    private static final Color FOCUSED_BACKGROUND = new Color(0xE1F6DA);

    private static final Color BORDER_COLOR = GREEN_COLOR;

    /**
     * InstallButton
     *
     * @param fill fill
     */
    public InstallButton(boolean fill) {
        if (fill) {
            setTextColor(FILL_FOREGROUND_COLOR);
            setBgColor(FILL_BACKGROUND_COLOR);
        } else {
            setTextColor(FOREGROUND_COLOR);
            setFocusedTextColor(FOREGROUND_COLOR);
            setBgColor(BACKGROUND_COLOR);
        }

        setFocusedBgColor(FOCUSED_BACKGROUND);
        setBorderColor(BORDER_COLOR);
        setFocusedBorderColor(BORDER_COLOR);
        setTextAndSize();
    }

    /**
     * 设置文本和字体
     */
    protected void setTextAndSize() {
        setText("Install");
        setWidth72(this);
    }
}
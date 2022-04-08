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
 * GreenButton
 *
 * @since 2021-05-18
 */
public class GreenButton extends ColorButton {
    private static final Color GREEN_COLOR = new JBColor(6134599, 2849616);

    /**
     * GreenButton
     *
     * @param text text
     */
    public GreenButton(String text) {
        this(GREEN_COLOR, text, null);
    }

    /**
     * GreenButton
     *
     * @param text  text
     * @param color color
     */
    public GreenButton(String text, Color color) {
        this(color, text, color);
    }

    public GreenButton(Color bgColor, String text, Color borderColor) {
        this.setBgColor(bgColor);
        this.setTextColor(WHITE_FOREGROUND);
        this.setText(text);
        if (borderColor != null) {
            this.setBorderColor(borderColor);
        }
    }

}
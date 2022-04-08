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

package com.huawei.kunpeng.porting.ui.panel;

import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * 设置页面head view
 *
 * @since 2020-09-25
 */
public class HeadPanel extends IDEBasePanel {
    private ImageIcon icon;

    private Image img;

    /**
     * 构造函数
     */
    public HeadPanel() {
        initPanel(null);
    }

    @Override
    protected void initPanel(JPanel panel) {
        icon = new ImageIcon(HeadPanel.class.getResource("/assets/img/banner/banner.png"));
        img = icon.getImage();
        setPreferredSize(new Dimension(0, 260));
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * paintComponent
     *
     * @param graphics 画笔
     */
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        // 设置背景图片可以跟随窗口自行调整大小，可以自己设置成固定大小
        graphics.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}

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

package com.huawei.kunpeng.intellij.ui.dialog;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;

/**
 * IDE自定义内容弹框基类
 *
 * @since 1.0.0
 */
public abstract class ContentDialog extends JFrame implements IDEBaseDialog {
    /**
     * 弹框标题
     */
    protected String dialogTitle;

    /**
     * 弹框名称
     */
    protected String dialogName;

    /**
     * 主面板
     */
    protected IDEBasePanel mainPanel;

    /**
     * 弹框位置大小
     */
    protected Rectangle rectangle;

    /**
     * 弹框大小是否可变
     */
    protected boolean isResizable;

    /**
     * 构造函数
     */
    public ContentDialog() {
    }

    /**
     * 创建主体内容面板
     *
     * @return IDEBasePanel
     */
    @Nullable
    protected abstract IDEBasePanel createCenterPanel();

    /**
     * 初始化弹框面板容器
     */
    @Nullable
    protected void initDialog() {
        // 设置基本布局，表格布局
        setLayout(new BorderLayout());

        // 主面板不存在则创建
        if (mainPanel == null) {
            mainPanel = createCenterPanel();
        }
        add(mainPanel, BorderLayout.CENTER);

        // 设置弹框title
        setTitle(dialogTitle);

        // 设置面板大小是否可变
        setResizable(isResizable);

        // 位置信息小于1时居中显示
        if (rectangle == null) {
            rectangle = new Rectangle(0, 0, IDEConstant.DIALOG_DEFAULT_WIDTH, IDEConstant.DIALOG_DEFAULT_HEIGHT);
        }

        if (rectangle.getX() <= 0 || rectangle.getY() <= 0) {
            setSize(rectangle.width, rectangle.height);
            setLocationRelativeTo(null);
        } else {
            setBounds(rectangle);
        }

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * 弹框是否有效存在
     *
     * @return boolean
     */
    @Override
    public boolean isValid() {
        return super.isValid();
    }

    /**
     * 销毁弹框
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * 显示弹框
     */
    public void displayPanel() {
        setVisible(true);
    }

    /**
     * 获取弹框名称
     *
     * @return boolean
     */
    public String getDialogName() {
        return dialogName;
    }

    /**
     * 刷新弹框
     */
    @Override
    public void updateDialog() {
    }
}

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

import com.huawei.kunpeng.intellij.ui.panel.BaseAboutPanel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The class AboutDialog: 弹出关于工具信息BaseDialog
 *
 * @since v1.0
 */
public abstract class AboutDialog extends DialogWrapper {
    private static final String PATH = "/assets/img/lefttree/logo.png";

    public AboutDialog(@Nullable Project project) {
        super(project);
        initDialog();
    }

    /**
     * 初始化弹框内容
     */
    @Nullable
    public void initDialog() {
        init();
        setTitle(getProductTitle());
        setResizable(false);
    }

    /**
     * 创建环境检查Dialog主Panel
     *
     * @return JComponent
     */
    @Override
    @Nullable
    protected JPanel createCenterPanel() {
        // 创建左对齐panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel iconLabel = new JLabel(new ImageIcon(AboutDialog.class.getResource(PATH)));
        titlePanel.add(iconLabel);
        JLabel separator = new JLabel();
        separator.setPreferredSize(new Dimension(2, 2));
        titlePanel.add(separator);

        // 添加Title
        JLabel titleLabel = new JLabel();
        titleLabel.setText(getProductInfo());
        titleLabel.setFont(new Font("huawei sans", Font.BOLD, 36));
        titleLabel.setVerticalTextPosition(SwingConstants.CENTER);
        titlePanel.add(titleLabel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(625, 225));
        centerPanel.add(titlePanel, BorderLayout.NORTH);

        // 添加主面板
        centerPanel.add(new BaseAboutPanel(getProductVersion(), getProductReleaseTime()).getComponent());
        return centerPanel;
    }

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return Action[]
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[] {};
    }

    /**
     * 获取工具信息
     *
     * @return string 工具信息
     */
    protected abstract String getProductInfo();

    /**
     * 获取工具名
     *
     * @return string 工具名
     */
    protected abstract String getProductTitle();

    /**
     * 工具版本号
     *
     * @return string 版本号
     */
    protected abstract String getProductVersion();

    /**
     * 工具发布时间
     *
     * @return string 发布时间
     */
    protected abstract String getProductReleaseTime();
}

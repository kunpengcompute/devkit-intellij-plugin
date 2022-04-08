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

import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * 添加弱口令弹框
 *
 * @since 2012-10-12
 */
public class AddWeakPwdDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param dialogName 弹框名称
     * @param panel 需要展示的面板之一
     */
    public AddWeakPwdDialog(
            String title,
            String dialogName,
            IDEBasePanel panel,
            Rectangle rectangle,
            Dimension dimension,
            boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? WeakPwdConstant.ADD_WEAK_PASSWORD : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.ADD_WEAK_PWD.dialogName() : dialogName;
        this.mainPanel = panel;

        // 无位置信息时居中显示
        this.rectangle = rectangle;

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public AddWeakPwdDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
        this(title, null, panel, rectangle, null, false);
    }

    /**
     * 不带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param dialogName 弹框名称
     * @param panel 需要展示的面板之一
     */
    public AddWeakPwdDialog(
            String title, String dialogName, IDEBasePanel panel, Dimension dimension, boolean resizable) {
        this(title, dialogName, panel, null, dimension, resizable);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public AddWeakPwdDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, null, null, false);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public AddWeakPwdDialog(String title, IDEBasePanel panel, Dimension dimension) {
        this(title, null, panel, null, dimension, false);
    }

    /**
     * 添加弱口令弹框内容
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认新增弱口令
     */
    @Override
    protected void onOKAction() {
    }

    /**
     * 确认删除弱口令
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     *  异常信息集中处理
     *
     * @return 异常集合
     */
    protected ValidationInfo doValidate() {
        return this.mainPanel.doValidate();
    }
}

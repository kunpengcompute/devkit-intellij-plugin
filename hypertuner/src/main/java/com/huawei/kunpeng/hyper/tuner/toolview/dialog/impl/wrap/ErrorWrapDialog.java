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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import java.awt.Rectangle;

/**
 * Porting 异常场景显示弹窗
 *
 * @since 2020-10-09
 */
public class ErrorWrapDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param rectangle  位置大小信息
     * @param resizable  大小是否可变
     */
    public ErrorWrapDialog(
            String title, String dialogName, IDEBasePanel panel, Rectangle rectangle, boolean resizable) {
        this.title =
                ValidateUtils.isEmptyString(title)
                        ? TuningI18NServer.toLocale("plugins_common_message_responseError")
                        : title;
        this.dialogName = ValidateUtils.isEmptyString(dialogName) ? Dialogs.ERROR_GUIDE.dialogName() : dialogName;
        mainPanel = panel;

        // 无位置信息时居中显示
        this.rectangle = rectangle;

        // 设置弹框大小是否可变
        this.resizable = resizable;

        // 设置弹框中保存取消按钮的名称
        setOKAndCancelName(
                CommonI18NServer.toLocale("common_term_operate_ok"),
                CommonI18NServer.toLocale("common_term_operate_close"));

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title     弹窗标题
     * @param panel     需要展示的面板之一
     * @param rectangle 位置大小信息
     */
    public ErrorWrapDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
        this(title, null, panel, rectangle, false);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public ErrorWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 不带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public ErrorWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this(title, dialogName, panel, new Rectangle(0, 0, 900, 380), resizable);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        super.initDialog();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {}

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {}

    /**
     * 创建主内容面板
     *
     * @return IDEBasePanel
     */
    @Override
    protected @Nullable IDEBasePanel createCenterPanel() {
        return mainPanel;
    }
}

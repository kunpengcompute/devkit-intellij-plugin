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
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Intellij 类型弹框
 *
 * @since 2020-09-25
 */
public class LogoutWrapDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public LogoutWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title =
                StringUtil.stringIsEmpty(title) ? TuningI18NServer.toLocale("plugins_hyper_tuner_login_logOut") : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.LOGIN.dialogName() : dialogName;
        mainPanel = panel;

        // 设置弹框大小是否可变
        this.resizable = resizable;

        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName(
                CommonI18NServer.toLocale("common_term_operate_ok"),
                CommonI18NServer.toLocale("common_term_operate_cancel"));

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public LogoutWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        TuningLoginUtils.logout();
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {}

    /**
     * 创建面板内容
     *
     * @return JComponent
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}

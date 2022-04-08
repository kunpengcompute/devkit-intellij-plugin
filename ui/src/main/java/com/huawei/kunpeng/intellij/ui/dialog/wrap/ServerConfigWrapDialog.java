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

package com.huawei.kunpeng.intellij.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.enums.Dialogs;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import java.awt.Rectangle;
import java.util.List;

/**
 * 基础配置服务器弹框
 *
 * @since 2021-04-16
 */
public abstract class ServerConfigWrapDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param dialogName 弹框名称
     * @param panel 需要展示的面板之一
     * @param rectangle 位置大小信息
     * @param resizable 大小是否可变
     */
    public ServerConfigWrapDialog(String title, String dialogName, IDEBasePanel panel, Rectangle rectangle,
        boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? UserManageConstant.CONFIG_TITLE : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? Dialogs.SERVER_CONFIG.dialogName() : dialogName;
        this.mainPanel = panel;

        // 无位置信息时居中显示
        this.rectangle = rectangle;
        // 设置弹框大小是否可变
        this.resizable = resizable;
        // 设置弹框中保存取消按钮的名称
        setOKAndCancelName(CommonI18NServer.toLocale("common_button_save"),
                CommonI18NServer.toLocale("common_button_cancel"));
        setHelp(CommonI18NServer.toLocale("common_config_help"), getHelpUrl());
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 自定义帮助链接
     *
     * @return string 帮助链接
     */
    protected abstract String getHelpUrl();

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     * @param rectangle 位置大小信息
     */
    public ServerConfigWrapDialog(String title, IDEBasePanel panel, Rectangle rectangle) {
        this(title, null, panel, rectangle, false);
    }

    /**
     * 不带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param dialogName 弹框名称
     * @param panel 需要展示的面板之一
     * @param resizable 大小是否可变
     */
    public ServerConfigWrapDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this(title, dialogName, panel, null, resizable);
    }

    /**
     * 不带位置信息的完整构造函数，代理生成时会使用
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public ServerConfigWrapDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, null, false);
    }

    /**
     * 初始化弹框
     */
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
    }

    /**
     * 点击确定事件
     */
    @Override
    protected void onOKAction() {
        Logger.info("ServerConfigWrapDialog, onOKAction");
        if (mainPanel == null) {
            return;
        }
        customizeOkAction();
        mainPanel.clearPwd();
    }

    /**
     * 自定义OkAction
     */
    protected abstract void customizeOkAction();

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
        Logger.info("ServerConfigWrapDialog, onCancelAction");
        if (mainPanel == null) {
            return;
        }
        customizeCancelAction();
        mainPanel.clearPwd();
    }

    /**
     * 自定义取消事件
     */
    protected abstract void customizeCancelAction();

    /**
     * 创建主内容面板
     *
     * @return IDEBasePanel
     */
    @Override
    protected @Nullable
    IDEBasePanel createCenterPanel() {
        return mainPanel;
    }

    /**
     *  异常信息集中处理
     *
     * @return 异常集合
     */
    protected List<ValidationInfo> doValidateAll() {
        return this.mainPanel.doValidateAll();
    }
}
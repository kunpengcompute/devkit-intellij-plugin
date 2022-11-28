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

package com.huawei.kunpeng.intellij.ui.action;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;

import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * The class AboutAction
 *
 * @since v1.0
 */
public abstract class AboutAction extends AnAction implements DumbAware {
    private static final Icon icon = BaseIntellijIcons.load(IDEConstant.MENU_ICONS_PATH + IDEConstant.MENU_ABOUT_ICON);
    /**
     * 左侧树关于菜单项动作
     */
    public AboutAction() {
        super(CommonI18NServer.toLocale("common_about"), "", icon);
    }

    /**
     * 弹出工具版本信息
     *
     * @param event event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        showAboutDialog(event);
    }

    /**
     * 弹出工具版本信息
     *
     * @param event event
     */
    protected abstract void showAboutDialog(AnActionEvent event);
}

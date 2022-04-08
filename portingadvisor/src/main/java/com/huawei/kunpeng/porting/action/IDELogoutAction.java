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

package com.huawei.kunpeng.porting.action;

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.LogoutPanel;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.ui.dialog.wrap.LogoutWrapDialog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * 扫描菜单事件
 *
 * @since 2020-09-25
 */
public class IDELogoutAction extends AnAction {
    /**
     * 点击事件
     *
     * @param e 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        IDEBasePanel panel = new LogoutPanel(null, I18NServer.toLocale("plugins_porting_login_logOutTips"));
        IDEBaseDialog dialog = new LogoutWrapDialog(I18NServer.toLocale("plugins_porting_login_logOut"), panel);
        dialog.displayPanel();
    }

    /**
     * change status
     *
     * @param anActionEvent 事件
     */
    @Override
    public void update(AnActionEvent anActionEvent) {
        int value = 0;
        anActionEvent.getPresentation().setEnabled(false);
        IDEPluginStatus valueFromGlobalContext = PortingIDEContext.getPortingIDEPluginStatus();
        value = (valueFromGlobalContext).value();

        /* 根据IDEPlugin status来更新菜单是否有效 */
        if (value >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            anActionEvent.getPresentation().setEnabled(true);
        }
    }
}

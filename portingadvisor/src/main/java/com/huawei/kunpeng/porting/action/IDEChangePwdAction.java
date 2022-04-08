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

import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingChangePasswordDialog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * '
 * The class IDEChangePwdAction
 *
 * @since 2021-11-1
 */
public class IDEChangePwdAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 弹出修改用户名密码Dialog
        IDEBasePanel panel = new ChangePasswordPanel(null);
        IDEBaseDialog changePwdDialog = new PortingChangePasswordDialog(null, panel);
        changePwdDialog.displayPanel();
    }
}

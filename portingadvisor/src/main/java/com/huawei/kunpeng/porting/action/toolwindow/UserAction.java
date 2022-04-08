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

package com.huawei.kunpeng.porting.action.toolwindow;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.action.IDEChangePwdAction;
import com.huawei.kunpeng.porting.action.IDELogoutAction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;

/**
 * The class UserAction
 *
 * @since v1.0
 */
public class UserAction extends AnAction implements DumbAware {
    private static final String CHANGE_PASSWORD_TEXT = I18NServer.toLocale("plugins_porting_lefttree_change_password");

    private static final String LOGOUT_TEXT = I18NServer.toLocale("plugins_porting_lefttree_logout");


    @NotNull
    private final UserAction.Action action;

    /**
     * 构造函数
     *
     * @param action action
     */
    public UserAction(@NotNull UserAction.Action action) {
        super(action.text, null, null);
        this.action = action;
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        action.anAction.actionPerformed(event);
    }

    /**
     * The enum: Action of user
     *
     * @since v1.0
     */
    public enum Action {
        CHANGE_PASSWORD(CHANGE_PASSWORD_TEXT, new IDEChangePwdAction()),
        LOGOUT(LOGOUT_TEXT, new IDELogoutAction());

        private String text;
        private AnAction anAction;

        Action(String text, AnAction anAction) {
            this.text = text;
            this.anAction = anAction;
        }
    }
}

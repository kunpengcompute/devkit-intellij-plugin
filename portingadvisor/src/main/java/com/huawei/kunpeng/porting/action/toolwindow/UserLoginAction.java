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

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.action.IDELoginAction;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.utils.IntellijAllIcons;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;

/**
 * The class UserLoginAction: 左侧导航树用户登录模块
 *
 * @since v1.0
 */
public class UserLoginAction extends AnAction implements DumbAware {
    private static final String LOGIN = I18NServer.toLocale("plugins_porting_lefttree_login");

    /**
     * 构造函数
     */
    public UserLoginAction() {
        super(LOGIN, "", null);
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new IDELoginAction().actionPerformed(event);
    }

    /**
     * 更新Action对应的Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setIcon(IntellijAllIcons.Settings.LOGIN);
        // 向服务端请求

        int value = 0;
        value = PortingIDEContext.getPortingIDEPluginStatus().value();

        // 根据IDEPlugin status来更新login是否置灰
        if (value >= IDEPluginStatus.IDE_STATUS_LOGIN.value()) {
            event.getPresentation().setEnabledAndVisible(false);
        } else if (value >= IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            event.getPresentation().setEnabledAndVisible(true);
        } else {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(false);
        }
    }
}

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
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.utils.IntellijAllIcons;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import org.jetbrains.annotations.NotNull;

/**
 * The class UserActionGroup
 *
 * @since v1.0
 */
public class UserActionGroup extends DefaultActionGroup {
    private boolean isInitialized = false;

    /**
     * 构造函数
     */
    public UserActionGroup() {
        super("User", true);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }

    /**
     * 更改导航栏用户操作 Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setIcon(IntellijAllIcons.Settings.LOGIN);
        int value = 0;
        value = PortingIDEContext.getPortingIDEPluginStatus().value();
        if (value <= IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            event.getPresentation().setVisible(false);
        } else {
            event.getPresentation().setText(PortingUserInfoContext.getInstance().getUserName(), false);
            event.getPresentation().setEnabledAndVisible(true);
            if (!isInitialized) {
                for (UserAction.Action action : UserAction.Action.values()) {
                    add(new UserAction(action));
                }
                isInitialized = true;
            }
        }
    }
}

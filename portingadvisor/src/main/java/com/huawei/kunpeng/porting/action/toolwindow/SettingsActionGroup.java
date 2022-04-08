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

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * The class SettingsActionGroup
 *
 * @since v2.2.T4
 */
public class SettingsActionGroup extends DefaultActionGroup {
    private static final String SETTINGS = I18NServer.toLocale("plugins_porting_lefttree_settings");

    /**
     * 构造函数
     */
    public SettingsActionGroup() {
        super(SETTINGS, true);
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
        event.getPresentation().setIcon(AllIcons.General.Settings);
        this.removeAll();
        int value = PortingIDEContext.getPortingIDEPluginStatus().value();
        if (value <= IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            event.getPresentation().setEnabled(false);
        } else {
            event.getPresentation().setEnabledAndVisible(true);
            createMupo(event);
        }
    }

    private void createMupo(AnActionEvent event) {
        boolean isAdminUser = Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole());
        for (SettingActionEnum action : SettingActionEnum.values()) {
            action.getAnAction().update(event);
            if (isAdminUser) {
                if (action.isAdminUserNeed()) {
                    add(action.getAnAction());
                }
            } else {
                if (action.isComUserNeed()) {
                    add(action.getAnAction());
                }
            }
            if (action.isAddSeparator()) {
                addSeparator();
            }
        }
    }
}

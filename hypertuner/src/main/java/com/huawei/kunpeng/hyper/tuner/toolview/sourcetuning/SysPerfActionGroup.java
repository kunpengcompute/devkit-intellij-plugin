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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings.sysperf.SysPerfActionEnum;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 显示工具窗口 (ToolWindows)  菜单栏(Options Menu) 中的
 * 设置栏下属子菜单 鲲鹏性能分析栏 下属子菜单组
 *
 * @since v2.2.T4
 */
public class SysPerfActionGroup extends DefaultActionGroup {
    private static final String SYS_PERF = TuningI18NServer.toLocale("plugins_common_hyper_tuner_sysperf");

    private boolean isInitialized = false;

    /**
     * 构造函数
     */
    public SysPerfActionGroup() {
        super(SYS_PERF, true);
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
        this.removeAll();
        int value = TuningIDEContext.getTuningIDEPluginStatus().value();
        if (value <= IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            event.getPresentation().setEnabled(false);
        } else {
            event.getPresentation().setEnabledAndVisible(true);
            createMupo(event);
        }
    }

    private void createMupo(AnActionEvent event) {
        boolean isAdminUser = Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole());
        for (SysPerfActionEnum action : SysPerfActionEnum.values()) {
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

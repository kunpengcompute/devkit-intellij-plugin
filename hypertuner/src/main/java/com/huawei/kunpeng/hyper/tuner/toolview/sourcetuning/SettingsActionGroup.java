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

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings.SettingsGroupFactory;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings.SettingActionEnum;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The class SettingsActionGroup
 *
 * @since v2.2.T4
 */
public class SettingsActionGroup extends DefaultActionGroup {
    private static final String SETTINGS = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_settings");

    /**
     * 构造函数
     */
    public SettingsActionGroup() {
        super(SETTINGS, true);
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
        int value = TuningIDEContext.getTuningIDEPluginStatus().value();
        if (value <= IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            event.getPresentation().setEnabled(false);
        } else {
            event.getPresentation().setEnabledAndVisible(true);
            createActionGroup(event);
        }
    }

    private void createActionGroup(AnActionEvent event) {
        String role = UserInfoContext.getInstance().getRole();
        for (List<SettingActionEnum> groups : SettingActionEnum.groups()) {
            for (SettingActionEnum action : groups) {
                if (action.shouldAddBy(role)) {
                    add(action.getAnAction());
                    action.getAnAction().update(event);
                }
            }
            addSeparator();
        }
        String installInfo = LeftTreeAction.instance().getInstallInfo();
        // 根据后台安装服务加载设置菜单
        List<AnAction> actions = SettingsGroupFactory.createActionsByInstallation(installInfo);
        for (int i = 0; i < actions.size(); i++) {
            add(actions.get(i));
            if (i < actions.size() - 1) {
                addSeparator();
            }
        }
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}

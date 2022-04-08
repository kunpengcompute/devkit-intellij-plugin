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

package com.huawei.kunpeng.hyper.tuner.action;

import com.huawei.kunpeng.hyper.tuner.action.serverconfig.TuningServerConfigAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * 扫描菜单事件
 *
 * @since 2020-09-25
 */
public class IDELoginAction extends AnAction {
    /**
     * 响应成功状态
     */
    private static final String SUCCESS = "UserManage.Success";

    /**
     * 点击事件
     *
     * @param e 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TuningServerConfigAction.instance.notificationForHyperlinkAction();
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
        IDEPluginStatus valueFromGlobalContext = TuningIDEContext.getTuningIDEPluginStatus();
        value = valueFromGlobalContext.value();

        /* 根据IDEPlugin status来更新菜单是否有效 */
        if (value >= IDEPluginStatus.IDE_STATUS_SERVER_CONFIG.value()) {
            anActionEvent.getPresentation().setEnabled(true);
        }
    }
}

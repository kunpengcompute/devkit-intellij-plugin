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

package com.huawei.kunpeng.porting.ui.panel;

import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.ServerConfigPanel;
import com.huawei.kunpeng.porting.action.serverconfig.PortingServerConfigAction;
import com.huawei.kunpeng.porting.ui.dialog.disclaimer.InstallDisclaimerDialog;

import com.intellij.openapi.wm.ToolWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 服务器配置面板
 *
 * @since 2020-04-13
 */
public class PortingServerConfigPanel extends ServerConfigPanel {
    public PortingServerConfigPanel(ToolWindow toolWindow) {
        super(toolWindow);
    }

    @Override
    protected String getToolConfigDescription() {
        return I18NServer.toLocale("plugins_porting_config_description");
    }

    @Override
    protected String getDefaultServerPort() {
        return I18NServer.toLocale("plugins_porting_default_port");
    }

    @Override
    protected void customizeRegisterAction() {
        if (action == null) {
            action = PortingServerConfigAction.instance;
        }
        // 为descriptionLabel2组件添加鼠标事件，鼠标移入、移除、点击、跳转
        descriptionLabel2.addMouseListener(new MouseAdapter() {
            /**
             * 发生单击事件时被触发
             *
             * @param mouseEvent mouseEvent
             */
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                IDEBaseDialog dialog = new InstallDisclaimerDialog(WeakPwdConstant.BEFORE_INSTALL, null);
                dialog.displayPanel();
            }
        });
    }
}

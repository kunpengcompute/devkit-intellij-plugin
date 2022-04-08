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

package com.huawei.kunpeng.porting.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.ServerConfigWrapDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.panel.ServerConfigPanel;
import com.huawei.kunpeng.porting.action.serverconfig.PortingServerConfigAction;

/**
 * 配置服务器弹框
 *
 * @since 2020-10-14
 */
public class PortingServerConfigWrapDialog extends ServerConfigWrapDialog {
    public PortingServerConfigWrapDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected String getHelpUrl() {
        return I18NServer.toLocale("plugins_porting_login_help_url");
    }

    @Override
    protected void customizeOkAction() {
        if (mainPanel.getAction() instanceof PortingServerConfigAction && mainPanel instanceof ServerConfigPanel) {
            ServerConfigPanel serverConfigPanel = (ServerConfigPanel) mainPanel;
            PortingServerConfigAction action = (PortingServerConfigAction) mainPanel.getAction();
            action.onOKAction(serverConfigPanel.getParams(), null);
        }
    }

    @Override
    protected void customizeCancelAction() {
        if (mainPanel.getAction() instanceof PortingServerConfigAction) {
            PortingServerConfigAction action = (PortingServerConfigAction) mainPanel.getAction();
            action.onCancelAction(null, null);
        }
    }
}

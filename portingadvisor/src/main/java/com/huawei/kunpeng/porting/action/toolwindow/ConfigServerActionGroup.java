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

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.utils.IntellijAllIcons;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import org.jetbrains.annotations.NotNull;

/**
 * 按钮样式设置国内工具类
 *
 * @since 2.3.T20
 */
public class ConfigServerActionGroup extends DefaultActionGroup {
    private static final String NAME = I18NServer.toLocale("plugins_port_remote_server_name");

    private boolean isInitialized = true;

    /**
     * 构造函数
     */
    public ConfigServerActionGroup() {
        super(NAME, true);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }

    /**
     * 更改导航栏远程服务器 Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setIcon(IntellijAllIcons.Settings.SERVER);
        event.getPresentation().setEnabledAndVisible(true);
        event.getPresentation().setText(NAME);
        int pluginStatus = IDEContext.getIDEPluginStatus(PortingIDEConstant.TOOL_NAME_PORTING).value();
        // 如果状态正在配置服务器中
        if (pluginStatus == IDEPluginStatus.IDE_STATUS_SERVER_DEPLOY.value()) {
            event.getPresentation().setEnabled(false);
            this.removeAll();
            isInitialized = true;
            return;
        }

        // 更新左侧树配置服务器Label
        if (pluginStatus > IDEPluginStatus.IDE_STATUS_SERVER_DEPLOY.value()) {
            String ip = PortingCommonUtil.readCurIpFromConfig();
            if (!StringUtil.stringIsEmpty(ip)) {
                event.getPresentation().setText(ip, false);
            }
        }

        if (isInitialized) {
            isInitialized = false;
            add(new ConfigRemoteServerAction());
            add(new CloudEnvApplyAction());
        }
    }
}

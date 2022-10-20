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

import com.huawei.kunpeng.hyper.tuner.action.serverconfig.TuningIDEServerConfigAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;

/**
 * The class ConfigRemoteServerAction: 配置远端服务器
 *
 * @since 2022-06-28
 */
public class ConfigRemoteServerAction extends AnAction implements DumbAware {
    private static final String CONFIG_SERVER = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_config_server");
    private static final String CONFIG_SERVER_ICON_PATH="/assets/img/lefttree/server.svg";

    /**
     * 左侧树服务器配置菜单动作
     */
    public ConfigRemoteServerAction() {
        super(CONFIG_SERVER, "", BaseIntellijIcons.load(CONFIG_SERVER_ICON_PATH));
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new TuningIDEServerConfigAction().actionPerformed(event);
    }

    /**
     * 更新Action对应的Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
        event.getPresentation().setText(CONFIG_SERVER);
        // 更新左侧树配置服务器Label
        String ip = CommonUtil.readCurIpFromConfig();
        if (!StringUtil.stringIsEmpty(ip)) {
            event.getPresentation().setText(ip);
        }
        int value = TuningIDEContext.getTuningIDEPluginStatus().value();
        setConfigRemoteServerStatus(event, value);
    }

    private void setConfigRemoteServerStatus(@NotNull AnActionEvent event, int value) {
        if (value == IDEPluginStatus.IDE_STATUS_SERVER_DEPLOY.value()) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(false);
        } else {
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
        }
    }
}

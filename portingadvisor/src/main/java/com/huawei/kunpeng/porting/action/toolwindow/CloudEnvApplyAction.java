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

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.webview.pageeditor.CloudEnvApplicationProcessEditor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;


/**
 * 按钮样式设置国内工具类
 *
 * @since 2.3.T20
 */
public class CloudEnvApplyAction extends AnAction implements DumbAware {
    private static final String NAME = I18NServer.toLocale("plugins_port_remote_lab_name");

    /**
     * 左侧树服务器配置菜单动作
     */
    public CloudEnvApplyAction() {
        super(NAME, "", null);
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        CloudEnvApplicationProcessEditor.openPage(
            I18NServer.toLocale("plugins_port_cloud_env_application_process_page_name"));
    }

    /**
     * 更新Action对应的Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
        event.getPresentation().setText(NAME);
    }
}
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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.hyper.tuner.action.serverconfig.TuningServerConfigAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningWeakPwdConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.InstallDisclaimerDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.ServerConfigPanel;

import com.intellij.openapi.wm.ToolWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 服务器配置面板
 *
 * @since 2020-09-25
 */
public class TuningServerConfigPanel extends ServerConfigPanel {
    public TuningServerConfigPanel(ToolWindow toolWindow) {
        super(toolWindow);
    }

    @Override
    protected String getToolConfigDescription() {
        return TuningI18NServer.toLocale("plugins_hyper_tuner_config_description");
    }

    @Override
    protected String getDefaultServerPort() {
        return TuningI18NServer.toLocale("plugins_hyper_tuner_default_port");
    }

    @Override
    protected void customizeRegisterAction() {
        if (action == null) {
            action = TuningServerConfigAction.instance;
        }
        // 为descriptionLabel2组件添加鼠标事件，鼠标移入、移除、点击、跳转
        descriptionLabel2.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 发生单击事件时被触发
                     *
                     * @param mouseEvent mouseEvent
                     */
                    public void mouseClicked(MouseEvent mouseEvent) {
                        IDEBaseDialog dialog = new InstallDisclaimerDialog(TuningWeakPwdConstant.BEFORE_INSTALL, null);
                        if (dialog != null) {
                            dialog.displayPanel();
                        }
                    }
                });
    }
}

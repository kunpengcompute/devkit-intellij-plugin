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

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.FreeTrialEditor;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * The class of DeployServerAction: 部署服务器
 *
 * @since 2022-10-20
 */

public class ApplyTrialAction extends AnAction implements DumbAware {
    private static final String APPLY_TRIAL = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_apply_trial");

    private static final Icon icon = BaseIntellijIcons.load(IDEConstant.MENU_ICONS_PATH + IDEConstant.MENU_APPLY_LAB_ICON);
    /**
     * 左侧树服务器配置菜单动作
     */
    public ApplyTrialAction() {
        super(APPLY_TRIAL, null, icon);
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        FreeTrialEditor.openPage();
    }

    /**
     * 更新Action对应的Presentation
     *
     * @param event AnActionEvent
     */
    @Override
    public void update(@NotNull AnActionEvent event) {

    }
}

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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.DisclaimerDialog;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

/**
 * The class DisclaimerAction:Tool Windows中设置按钮中的 免责声明
 *
 * @since 2021-6-25
 */
public class DisclaimerAction extends AnAction implements DumbAware {
    private static final String DISCLAIMER_IN = CommonI18NServer.toLocale("common_disclaimer");

    /**
     * 左侧树菜单免责声明动作
     */
    public DisclaimerAction() {
        super(DISCLAIMER_IN, null, null);
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String title = TuningUserManageConstant.USER_DISCLAIMER_TITLE;
        DisclaimerDialog dialog = new DisclaimerDialog(title, null, null, false);
        dialog.displayPanel();
    }
}

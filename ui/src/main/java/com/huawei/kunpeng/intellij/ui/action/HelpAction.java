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

package com.huawei.kunpeng.intellij.ui.action;

import com.huawei.kunpeng.intellij.common.util.CommonUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

import org.jetbrains.annotations.NotNull;

/**
 * The class HelpAction
 *
 * @since v1.0
 */
public class HelpAction extends AnAction implements DumbAware {
    private static final String HELP = "FAQ";

    private String helpUrl;

    /**
     * 构造函数
     */
    public HelpAction(String helpUrl) {
        super(HELP, "", null);
        this.helpUrl = helpUrl;
    }

    /**
     * 浏览器打开帮助也没
     *
     * @param event event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        CommonUtil.openURI(helpUrl);
    }
}

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

package com.huawei.kunpeng.porting.action;

import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 左侧树增强功能
 *
 * @since 2021/1/5
 */
public class IDEEnhancedAction extends AnAction {
    /**
     * 点击事件
     *
     * @param event 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        EnhancedFunctionPageEditor.openPage();
    }

    /**
     * change status
     *
     * @param anActionEvent 事件
     */
    @Override
    public void update(AnActionEvent anActionEvent) {
    }
}

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

package com.huawei.kunpeng.porting.listener;

import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.util.ui.UIUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 主题监听器
 *
 * @since 2020-11-21
 */
public class IDEThemeListener implements LafManagerListener {
    /**
     * 黑色主题代码，与VSCode同步
     */
    public static final int DARCULA_THEME = 1;

    /**
     * 白色主题代码，与VSCode同步
     */
    public static final int LIGHT_THEME = 2;

    /**
     * 发生了主题切换
     *
     * @param source LafManager
     */
    @Override
    public void lookAndFeelChanged(@NotNull LafManager source) {
        // 发送主题信息至页面
        boolean isUnderIntelliJLaF = UIUtil.isUnderIntelliJLaF();
        Boolean isLightThemeInContext = PortingIDEContext.getValueFromGlobalContext(
            PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.LIGHT_THEME.vaLue());

        if (isLightThemeInContext == null || isUnderIntelliJLaF == isLightThemeInContext) {
            return;
        }
        PortingIDEContext.setValueForGlobalContext(
            PortingIDEConstant.TOOL_NAME_PORTING, BaseCacheVal.LIGHT_THEME.vaLue(), isUnderIntelliJLaF);
        LeftTreeUtil.refreshLeftPanel();
    }
}

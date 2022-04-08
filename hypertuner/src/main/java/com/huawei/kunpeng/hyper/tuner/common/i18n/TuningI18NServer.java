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

package com.huawei.kunpeng.hyper.tuner.common.i18n;

import com.huawei.kunpeng.hyper.tuner.common.constant.enums.I18NTitle;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.wm.ToolWindowManager;

import java.util.Locale;

/**
 * 国际化服务
 *
 * @since 2020-09-25
 */
public class TuningI18NServer extends I18NServer {
    /**
     * 更新当前系统语言
     *
     * @return Locale
     */
    public static Locale updateTuningCurrentLocale() {
        Locale locale = getLocale();
        // 更新缓存
        IDEContext.setValueForGlobalContext(null, BaseCacheVal.CURRENT_LOCALE.vaLue(), locale);

        // 更新工具语言国际化标签
        updateTitleDisplay(I18NTitle.values());

        return locale;
    }

    /**
     * 更新工具栏的国际化标签
     *
     * @param values I18NTitle
     */
    public static void updateTitleDisplay(I18NTitle[] values) {
        for (I18NTitle title : values) {
            switch (title.titleType()) {
                case ACTION:
                    ActionManager.getInstance()
                            .getAction(title.titleId())
                            .getTemplatePresentation()
                            .setText(toLocale(title.i18nKey()));
                    break;
                case TOOL_WINDOW:
                    ToolWindowManager instance = ToolWindowManager.getInstance(CommonUtil.getDefaultProject());
                    if (instance != null) {
                        instance.getToolWindow(title.titleId()).setTitle(toLocale(title.i18nKey()));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

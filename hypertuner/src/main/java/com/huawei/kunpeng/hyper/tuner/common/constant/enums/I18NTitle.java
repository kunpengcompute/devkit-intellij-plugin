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

package com.huawei.kunpeng.hyper.tuner.common.constant.enums;

import com.huawei.kunpeng.intellij.common.enums.ToolTitleType;

/**
 * IDE自定义的所有工具栏国际化映射
 *
 * @since 2020-10-16
 */
public enum I18NTitle {
    LOGIN(ToolTitleType.ACTION, "plugins_hyper_tuner_title_log_in",
            "ide.kunpeng.action.tuning.login");

    // 工具栏类型（ToolTitleType）
    private final ToolTitleType titleType;

    // 国际化词条key
    private final String i18nKey;

    // 类型id（action id， toolWindow id）
    private final String titleId;

    private I18NTitle(ToolTitleType titleType, String i18nKey, String titleId) {
        this.titleType = titleType;
        this.i18nKey = i18nKey;
        this.titleId = titleId;
    }

    /**
     * 获取工具栏类型
     *
     * @return String
     */
    public ToolTitleType titleType() {
        return titleType;
    }

    /**
     * 获取工具栏id
     *
     * @return String
     */
    public String titleId() {
        return titleId;
    }

    /**
     * 获取工具栏Title的国际化Key
     *
     * @return String
     */
    public String i18nKey() {
        return i18nKey;
    }
}

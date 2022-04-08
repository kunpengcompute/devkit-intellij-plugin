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

package com.huawei.kunpeng.porting.common.utils;

import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * 图片加载类
 *
 * @since 2020-11-13
 */
public class IntellijAllIcons extends BaseIntellijIcons {
    /**
     * ReportOperation相关的图片
     */
    public static final class ReportOperation {
        /**
         * 需要按照建议修改的行标记图片
         */
        @NotNull
        public static final Icon REPORT_PROBLEM = load("/assets/img/report-line-mark.svg");

        /**
         * 提示图片
         */
        public static final Icon ICON_INFO = load( "/assets/img/settings/disclaimer_dialog.png");
    }
}

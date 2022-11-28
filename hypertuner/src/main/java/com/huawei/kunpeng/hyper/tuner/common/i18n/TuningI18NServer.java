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

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

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
        return locale;
    }
}

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

package com.huawei.kunpeng.intellij.common.i18n;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.log.Logger;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 国际化（仅适用于公共模块）
 *
 * @since 2021-04-02
 */
public class CommonI18NServer {
    /**
     * resource bundle message's name
     */
    private static final String MESSAGES = "commonMessage";

    /**
     * 获取当前系统Locale
     *
     * @return Locale
     */
    public static Locale getCurrentLocale() {
        return IDEContext.getValueFromGlobalContext(null, BaseCacheVal.CURRENT_LOCALE.vaLue());
    }

    /**
     * 无参国际化
     *
     * @param code code
     * @return string
     */
    public static String toLocale(String code) {
        Locale currentLocale = getCurrentLocale();
        return ResourceBundle.getBundle(MESSAGES, currentLocale).getString(code);
    }

    /**
     * 带参国际化
     *
     * @param code code
     * @param value value
     * @return string
     */
    public static String toLocale(String code, String value) {
        String result = toLocale(code);
        if (value == null || value.isEmpty()) {
            Logger.error("code: {}, args is null or empty.", code);
            return result;
        }
        return MessageFormat.format(result, value);
    }

    /**
     * 带参国际化
     *
     * @param code code
     * @param args arms
     * @return string
     */
    public static String toLocale(String code, List<?> args) {
        String result = toLocale(code);
        if (args == null || args.isEmpty()) {
            Logger.error("code: {}, args is null or empty.", code);
            return result;
        }
        return MessageFormat.format(result, args.toArray());
    }
}

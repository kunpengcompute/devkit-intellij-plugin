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

package com.huawei.kunpeng.devkit.common.i18n;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 国际化服务
 *
 * @since 2020-09-25
 */
public class DevkitI18NServer extends I18NServer {
    /**
     * 无参国际化
     *
     * @param code code
     * @return string
     */
    public static String toLocale(String code) {
        Locale locale = getLocale();
        String result = "";
        try {
            result = ResourceBundle.getBundle(MESSAGES, locale).getString(code);
            return result;
        } catch (MissingResourceException missingResourceException) {
            return result;
        }
    }
}
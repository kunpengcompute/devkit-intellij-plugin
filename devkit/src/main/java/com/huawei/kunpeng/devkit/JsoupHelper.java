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

package com.huawei.kunpeng.devkit;

import com.intellij.openapi.util.text.StringUtil;

/**
 * JsoupHelper
 *
 * @since 2021-08-25
 */
public class JsoupHelper {
    private static final String HTML_REG = "<[^>]+>";
    private static final String[][] HTML_SPECIAL_CHARS =
            new String[][]{{"&quot;", "\""}, {"&amp;", "&"}, {"&lt;", "<"}, {"&gt;", ">"}, {"&nbsp;", " "}};

    /**
     * htmlToText
     *
     * @param html html
     * @return String
     */
    public static String htmlToText(String html) {
        return StringUtil.isEmpty(html) ? "" : replaceSpecialChars(html.replaceAll("<[^>]+>", ""));
    }

    private static String replaceSpecialChars(String text) {
        String nText = text;
        for (String[] special : HTML_SPECIAL_CHARS) {
            nText = nText.replaceAll(special[0], special[1]);
        }
        return nText;
    }
}
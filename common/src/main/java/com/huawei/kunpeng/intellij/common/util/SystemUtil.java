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

package com.huawei.kunpeng.intellij.common.util;

import java.util.Locale;

/**
 * The Class SystemUtil.
 *
 * @since 2.2.T3
 */
public class SystemUtil {
    private static boolean IS_WINDOWS;

    static {
        String os = System.getProperty("os.name");
        if (os != null) {
            IS_WINDOWS = os.toLowerCase(Locale.ENGLISH).contains("windows");
        }
    }

    /**
     * is Windows
     *
     * @return boolean
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }
}

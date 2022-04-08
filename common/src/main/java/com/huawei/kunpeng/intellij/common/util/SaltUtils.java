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

import com.huawei.kunpeng.intellij.common.crypto.RandomStringGenerator;

import java.util.Optional;

/**
 * The Class SaltUtils.
 *
 * @since 2.2.T3
 */
public class SaltUtils {
    private static final int DEFAULT_SALT_LENGTH = 32 * 8;
    private static final String DEFAULT_SALT_KEY = "E381954C3C4243C4A0643399539C7AF6";

    /**
     * getDefaultSalt
     *
     * @return salt
     */
    public static String getDefaultSalt() {
        return createDefaultSalt();
    }

    /**
     * 生成salt value 并持久化到本地
     *
     * @return salt value
     */
    private static String createDefaultSalt() {
        // 从文件中获取salt
        Optional<String> optional = FileUtil.getPropertiesVal(DEFAULT_SALT_KEY);

        // 文件中不存在则随机生成salt, 并持久化
        if (!optional.isPresent() || optional.get().isEmpty()) {
            String value = RandomStringGenerator.generate(DEFAULT_SALT_LENGTH);
            // 持久化到本地
            FileUtil.saveWorkingKeyCipherText(DEFAULT_SALT_KEY, value);
            return value;
        }
        return optional.get();
    }
}

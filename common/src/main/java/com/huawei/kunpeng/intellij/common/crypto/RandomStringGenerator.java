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

package com.huawei.kunpeng.intellij.common.crypto;

import com.huawei.kunpeng.intellij.common.log.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * the class RandomStringGenerator
 *
 * @since 1.0.0
 */
public class RandomStringGenerator {
    private static final String ALLOWED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int EXPECTED_BUFFER_DATA = 1024;

    private static final String ALGORITHM_SHA1PRNG = "SHA1PRNG";

    /**
     * Generate random key
     *
     * @param keyLength random key length
     * @return random key
     */
    public static String generate(int keyLength) {
        StringBuilder build = new StringBuilder(EXPECTED_BUFFER_DATA);
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance(ALGORITHM_SHA1PRNG);
            int length = ALLOWED_CHARS.length();
            for (int i = 0; i < keyLength; i++) {
                build.append(ALLOWED_CHARS.charAt(secureRandom.nextInt(length)));
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.error("failed to get secure random. stack trace : ", e);
        }
        return build.toString();
    }
}

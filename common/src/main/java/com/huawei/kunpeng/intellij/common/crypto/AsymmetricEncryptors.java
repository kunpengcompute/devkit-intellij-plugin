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
import com.huawei.kunpeng.intellij.common.util.SaltUtils;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * AsymmetricEncryptors
 *
 * @since 1.0.0
 */
public class AsymmetricEncryptors {
    /**
     * 获取单例
     *
     * @return 单例对象
     */
    public static AsymmetricCrypto getInstance() {
        if (instance == null) {
            instance = new DefaultAsymmetricCrypto();
        }
        return instance;
    }

    private static AsymmetricCrypto instance;

    /**
     * Default implement of interface AsymmetricCrypto
     */
    static class DefaultAsymmetricCrypto implements AsymmetricCrypto {
        static final String DEFAULT_ALGORITHM = "PBKDF2WithHmacSHA256";

        private static final int KEY_LENGTH = 256;

        private static final int ITERATIONS = 10000;

        /**
         * encrypt the plain data
         *
         * @param data plain data
         * @param key  key
         * @return the encrypted data
         */
        @Override
        public String encrypt(String data, String key) {
            String result = null;
            try {
                PBEKeySpec spec = new PBEKeySpec(data.toCharArray(),
                        SaltUtils.getDefaultSalt().getBytes(StandardCharsets.UTF_8),
                    ITERATIONS,
                    KEY_LENGTH);
                SecretKeyFactory skf = SecretKeyFactory.getInstance(DEFAULT_ALGORITHM);

                result = Base64.encodeBase64String(skf.generateSecret(spec).getEncoded());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                Logger.error("failed to encrypt data by {}. stack trace : ", DEFAULT_ALGORITHM, e);
            }
            return result;
        }
    }
}

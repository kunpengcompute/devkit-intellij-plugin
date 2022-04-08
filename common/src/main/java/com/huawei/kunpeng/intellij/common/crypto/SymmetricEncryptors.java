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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * SymmetricEncryptors
 *
 * @since 1.0.0
 */
public class SymmetricEncryptors {
    private static final String DEFAULT_ALGORITHM = "AES/GCM/NoPadding";

    private static Crypto instance;

    /**
     * Method to obtain Crypto instance
     *
     * @return Crypto instance
     */
    public static Crypto getInstance() {
        if (instance == null) {
            instance = new DefaultSymmetricCryptoImpl();
        }
        return instance;
    }

    /**
     * Default implements of interface Crypto
     */
    static class DefaultSymmetricCryptoImpl implements Crypto {
        private static final String AES = "AES";
        private static final int IV_LENGTH = 12;
        private static final int GROUP_LENGTH = 128;
        private static final int SECRET_LENGTH = 16;
        private static final int INT_BYTE_LENGTH = 4;

        /**
         * encrypt data
         *
         * @param clearText clearText
         * @param originKey encrypt key string for encrypt
         * @return the encrypted text
         */
        @Override
        public String encrypt(String clearText, String originKey) {
            try {
                String secretKey = initAESSecretKey(originKey);
                SecretKey secKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), AES);
                // gen iv
                byte[] iv = new byte[IV_LENGTH];
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(iv);
                final Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GROUP_LENGTH, iv);
                cipher.init(Cipher.ENCRYPT_MODE, secKey, gcmParameterSpec);

                byte[] cipherText = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
                ByteBuffer byteBuffer = ByteBuffer.allocate(INT_BYTE_LENGTH + iv.length + cipherText.length);
                byteBuffer.putInt(iv.length);
                byteBuffer.put(iv);
                byteBuffer.put(cipherText);
                byte[] cipherMessage = byteBuffer.array();
                String result = Base64.getEncoder().encodeToString(cipherMessage);
                return result;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                Logger.error("Use AES-GCM algorithm encrypt data error", e);
            }
            return "";
        }

        /**
         * decrypt data
         *
         * @param cipherText cipher text
         * @param originKey  key string for decrypt
         * @return the clean text
         */
        @Override
        public String decrypt(String cipherText, String originKey) {
            byte[] plainText = new byte[0];
            try {
                String secretKey = initAESSecretKey(originKey);
                byte[] decode = Base64.getDecoder().decode(cipherText);
                ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
                int ivLength = byteBuffer.getInt();
                byte[] iv = new byte[ivLength];
                byteBuffer.get(iv);
                byte[] cipherContent = new byte[byteBuffer.remaining()];
                byteBuffer.get(cipherContent);
                final Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), AES),
                        new GCMParameterSpec(GROUP_LENGTH, iv));
                plainText = cipher.doFinal(cipherContent);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                Logger.error("Use AES-GCM algorithm decrypt data error", e);
            }
            return new String(plainText, StandardCharsets.UTF_8);
        }

        private String initAESSecretKey(String originKey) throws InvalidAlgorithmParameterException {
            if (originKey.length() < SECRET_LENGTH) {
                throw new InvalidAlgorithmParameterException("The key length does not meet the requirement.");
            }
            return originKey.substring(0, SECRET_LENGTH);
        }
    }
}

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

package com.huawei.kunpeng.intellij.common.crypto.km;

import static com.huawei.kunpeng.intellij.common.util.FileUtil.saveWorkingKeyCipherText;

import com.huawei.kunpeng.intellij.common.crypto.RandomStringGenerator;
import com.huawei.kunpeng.intellij.common.crypto.SymmetricEncryptors;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;

/**
 * working key management
 *
 * @since 1.0.0
 */
public class WorkingKeyManager {
    /**
     * 秘钥长度（字节）
     */
    private static final int KEY_LENGTH = 16;

    private static String encryptWorkingKey(String clearText, String cipherKey) {
        return SymmetricEncryptors.getInstance().encrypt(clearText, cipherKey);
    }

    private static String decryptWorkingKey(String cipherText, String cipherKey) {
        return SymmetricEncryptors.getInstance().decrypt(cipherText, cipherKey);
    }

    /**
     * get the working key by key string from property file, if can not find working key in file,
     * then create a new working key,and save to the property file after encrypt.
     *
     * @param keyStr    the key for the value of working key
     * @param cipherKey root key
     * @return working key
     */
    public String getOrCreateWorkingKey(String keyStr, String cipherKey) {
        String cipherText = FileUtil.getPropertiesVal(keyStr).orElse(null);
        String clearWorkingKey = "";
        if (cipherText == null || cipherText.isEmpty()) {
            clearWorkingKey = createWorkingKey();
            if (!saveWorkingKeyCipherText(keyStr, encryptWorkingKey(clearWorkingKey, cipherKey))) {
                return "";
            }
        } else {
            clearWorkingKey = decryptWorkingKey(cipherText, cipherKey);
        }
        return clearWorkingKey;
    }

    private String createWorkingKey() {
        String workingKey;
        workingKey = RandomStringGenerator.generate(KEY_LENGTH);
        Logger.info("success to create working key.");
        return workingKey;
    }
}

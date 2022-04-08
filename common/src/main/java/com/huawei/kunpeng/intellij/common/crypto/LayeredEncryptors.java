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

import com.huawei.kunpeng.intellij.common.crypto.km.RootKeyManager;
import com.huawei.kunpeng.intellij.common.crypto.km.WorkingKeyManager;
import com.huawei.kunpeng.intellij.common.log.BaseLogger;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

/**
 * the layered encryptors
 *
 * @since 1.0.0
 */
public class LayeredEncryptors {
    private static RootKeyManager rootKeyManager = new RootKeyManager();

    private static WorkingKeyManager workingKeyManager = new WorkingKeyManager();

    /**
     * encrypted data, use Two-layer encryption
     *
     * @param clearText clear text
     * @param keyStr    use keyStr to find the cipher text of working key
     * @return encrypted data string
     */
    public static String encrypt(String clearText, String keyStr) {
        String rootKey = rootKeyManager.getRootKey();
        String workingKey = workingKeyManager.getOrCreateWorkingKey(keyStr, rootKey);
        return SymmetricEncryptors.getInstance().encrypt(clearText, workingKey);
    }

    /**
     * decrypted data, use Two-layer decryption
     *
     * @param cipherText cipher text of data
     * @param keyStr     use keyStr to find the cipher text of working key
     * @return decrypted data string
     */
    public static String decrypt(String cipherText, String keyStr) {
        String rootKey = rootKeyManager.getRootKey();
        if (StringUtil.stringIsEmpty(rootKey)) {
            return "";
        }
        String workingKey = workingKeyManager.getOrCreateWorkingKey(keyStr, rootKey);
        if (StringUtil.stringIsEmpty(rootKey) || StringUtil.stringIsEmpty(workingKey)) {
            return "";
        }
        return SymmetricEncryptors.getInstance().decrypt(cipherText, workingKey);
    }
}

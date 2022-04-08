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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.crypto.LayeredEncryptors;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.notification.NotificationType;

import java.nio.file.Paths;
import java.util.Optional;

/**
 * The class CryptoUtil: 加解密工具类
 *
 * @since 2021/4
 */
public class CryptoUtil {
    /**
     * 口令加密持久化对应的key
     */
    private static final String ENCRYPT_PASSWD_KEY = "6SoY4Tz0bhaB4Weagv9eXSAVYffCK1yq";

    /**
     * Working Key 持久化对应的key
     */
    private static final String WORKING_KEY_STR = "ecf4c5260d98426dba2cfadfce38b75d";

    /**
     * 对输入的明文密码进行加密
     *
     * @param passwd 明文密码
     * @return 密文密码
     */
    public static void encrypt(String passwd) {
        final String encryptedPasswd = LayeredEncryptors.encrypt(passwd, WORKING_KEY_STR);
        FileUtil.saveWorkingKeyCipherText(ENCRYPT_PASSWD_KEY, encryptedPasswd);
    }

    /**
     * 从持久化文件中读取密文解密
     *
     * @return 明文密码
     */
    public static String decrypt() {
        // 从property文件获取密码密文
        Optional<String> encryptPasswd = FileUtil.getPropertiesVal(ENCRYPT_PASSWD_KEY);
        if (!encryptPasswd.isPresent() || encryptPasswd.get().isEmpty()) {
            return showCryptNotification();
        }
        // 解码
        String passwd = LayeredEncryptors.decrypt(encryptPasswd.get(), WORKING_KEY_STR);
        if (StringUtil.stringIsEmpty(passwd)) {
            showCryptNotification();
        }
        return passwd;
    }

    private static String showCryptNotification() {
        try {
            Logger.error("property file has no ciphertext.");
            // 加解密文件已失效， 直接删除
            FileUtil.deleteDir(null, Paths.get(CommonUtil.getCurUserCryptRootPath(), IDEConstant.CRYPT_DIR).toString());
            return "";
        } finally {
            IDENotificationUtil.notificationCommon(new NotificationBean("", I18NServer.toLocale(
                    "plugins_common_login_decrypt_error"), NotificationType.ERROR));
        }
    }
}

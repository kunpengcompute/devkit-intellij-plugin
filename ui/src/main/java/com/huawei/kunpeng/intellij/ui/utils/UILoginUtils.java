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

package com.huawei.kunpeng.intellij.ui.utils;

import com.huawei.kunpeng.intellij.common.util.CryptoUtil;

/**
 * 用户口令加解密
 *
 * @since 2021-04-10
 */
public class UILoginUtils {
    /**
     * 使用DPAPI解密，获取用户密码
     *
     * @return 密码
     */
    public static String decrypt() {
        return CryptoUtil.decrypt();
    }

    /**
     * 使用DPAPI加密用户密码
     *
     * @param passwd 密码
     */
    public static void encrypt(String passwd) {
        CryptoUtil.encrypt(passwd);
    }
}

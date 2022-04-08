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

import java.text.Normalizer;

/**
 * 校验检查公共Common类
 *
 * @since 2021-09-09
 */
public class CheckedUtils {
    /**
     * 密码正则表达式
     */
    public static final String CHECK_REG_EXP = "^(?![\\d]+$)(?![a-z]+$)(?![A-Z]+$)" +
            "(?![-_=+~`!@#$%^&*()\\|\\[{}\\];:'\",<.>/\\?]+$)" +
            "[\\da-zA-Z-_=+~`!@#$%^&*()\\|\\[{}\\];:'\",<.>/\\?]{8,32}$";

    /**
     * 用户名正则表达式
     */
    public static final String USER_NAME_REG_EXP = "^[a-zA-Z][a-zA-Z0-9_-]{5,31}$";

    /**
     * 校验端口合法性
     *
     * @param port port
     * @return ture or false
     */
    public static boolean checkPort(String port) {
        String regex = "([1-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])";
        String tempStr = Normalizer.normalize(port, Normalizer.Form.NFKC);
        if (ValidateUtils.isNotEmptyString(tempStr)) {
            return tempStr.matches(regex);
        }
        return false;
    }

    /**
     * 校验IP格式
     *
     * @param ip IP
     * @return ture or false
     */
    public static boolean checkIp(String ip) {
        String tempStr = Normalizer.normalize(ip, Normalizer.Form.NFKC);
        if ("0.0.0.0".equals(tempStr) || "255.255.255.255".equals(tempStr)) {
            return false;
        }
        String regex =
                "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d"
                        + "|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
        if (ValidateUtils.isNotEmptyString(tempStr)) {
            return tempStr.matches(regex);
        }
        return false;
    }


    /**
     * 校验用户名非空
     *
     * @param user user
     * @return ture or false
     */
    public static boolean checkUser(String user) {
        return ValidateUtils.isNotEmptyString(user);
    }

    /**
     * RegExp(/^[a-zA-Z][a-zA-Z0-9_-]{5,31}$/)
     *
     * @param userName userName
     * @return 是否正确
     */
    public static boolean checkUserName(String userName) {
        String tempStr = Normalizer.normalize(userName, Normalizer.Form.NFKC);
        if (ValidateUtils.isNotEmptyString(tempStr)) {
            return tempStr.matches(USER_NAME_REG_EXP);
        }
        return false;
    }

    /**
     * 校验密码非空
     *
     * @param password password
     * @return ture or false
     */
    public static boolean checkPwd(String password) {
        return ValidateUtils.isNotEmptyString(password);
    }

    /**
     * 正则校验密码
     *
     * @param pwd password
     * @return ture or false
     */
    public static boolean checkPwdForReg(String pwd) {
        String tempStr = Normalizer.normalize(pwd, Normalizer.Form.NFKC);
        if (ValidateUtils.isNotEmptyString(tempStr)) {
            return tempStr.matches(CHECK_REG_EXP);
        }
        return false;
    }
}

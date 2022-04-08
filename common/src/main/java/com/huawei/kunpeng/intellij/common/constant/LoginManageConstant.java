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

package com.huawei.kunpeng.intellij.common.constant;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;

/**
 * 登录声明相关常量定义
 *
 * @since 2020-10-15
 */
public class LoginManageConstant {
    /**
     * 国际化: 记住密码
     */
    public static final String LOGIN_SAVE_PASSWORD = CommonI18NServer.toLocale("common_login_savePassword");

    /**
     * 国际化: 是
     */
    public static final String LOGIN_YES = CommonI18NServer.toLocale("common_yes");

    /**
     * 国际化: 否
     */
    public static final String LOGIN_NO = CommonI18NServer.toLocale(
            "common_no");

    /**
     * 国际化: 自动登录
     */
    public static final String AUTO_LOG_IN = CommonI18NServer.toLocale("common_login_autoLogIn");


}

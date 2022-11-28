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
 * 用户管理相关常量定义
 *
 * @since 2021-04-08
 */
public class UserManageConstant {
    /**
     * 国际化 免责声明
     */
    public static final String USER_DISCLAIMER_TITLE = CommonI18NServer.toLocale("common_user_disclaimer_title");

    /**
     * 国际化 关闭。
     */
    public static final String TERM_OPERATE_CLOSE = CommonI18NServer.toLocale("common_term_operate_close");

    /**
     * 配置面板title
     */
    public static final String CONFIG_TITLE = CommonI18NServer.toLocale("common_config_title");

    /**
     * 国际化：继续登录
     */
    public static final String CERT_ERROR_TITLE = CommonI18NServer.toLocale("common_setting_cert_error_title");

    /**
     * 国际化：证书验证失败
     */
    public static final String CERT_ERROR_CONTENT = CommonI18NServer.toLocale("common_setting_cert_error_content");
}

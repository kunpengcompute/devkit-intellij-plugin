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

package com.huawei.kunpeng.hyper.tuner.common.constant;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.LoginManageConstant;

/**
 * 登录声明相关常量定义
 *
 * @since 2020-10-15
 */
public class TuningLoginManageConstant extends LoginManageConstant {
    /**
     * 国际化: 登录
     */
    public static final String LEFTTREE_LOGIN = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_login");

    /**
     * 国际化: =登录性能分析工具
     */
    public static final String LOGIN_TITLE = TuningI18NServer.toLocale("plugins_hyper_tuner_login_title");


    /**
     * 国际化: 修改初始密码
     */
    public static final String LOGIN_INITIAL_PASSWORD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_login_initialPassword");

    /**
     * 国际化: 确认密码
     */
    public static final String LOGIN_CONFIRM_PASSWORD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_login_confirmPassword");

    /**
     * 国际化: 已连接
     */
    public static final String LEFTTREE_SERVER_CONNECTED = TuningI18NServer.toLocale(
            "plugins_porting_lefttree_server_connected");

    /**
     * 国际化: 立即登录
     */
    public static final String LEFTTREE_LOGIN_NOW = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_login_now");
}

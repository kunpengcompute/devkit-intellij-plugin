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

/**
 * 系统管理声明相关常量定义
 *
 * @since 2020-10-15
 */
public class SysSettingManageConstant {
    /**
     * 国际化: 修改成功
     */
    public static final String UPDATE_SUCESS = TuningI18NServer.toLocale("plugins_hyper_tuner_common_update_sucess");

    /**
     * 国际化: 修改失败
     */
    public static final String UPDATE_FAILD = TuningI18NServer.toLocale("plugins_hyper_tuner_common_update_faild");

    /**
     * 国际化: 用户管理运行日志级别
     */
    public static final String LOG_LEVEL = TuningI18NServer.toLocale("plugins_hyper_tuner_log_level");

    /**
     * 国际化: 最大同时在线普通用户数
     */
    public static final String MAX_USER_NUM = TuningI18NServer.toLocale("plugins_hyper_tuner_max_user_num");

    /**
     * 国际化: 输入整数
     */
    public static final String TIP_NUM_MODIFY = TuningI18NServer.toLocale("plugins_hyper_tuner_tips_num_modify");

    /**
     * 国际化: 会话超时时间
     */
    public static final String TITLE_TIMEOUT = TuningI18NServer.toLocale("plugins_hyper_tuner_title_timeout");

    /**
     * 国际化: 输入整数
     */
    public static final String TIP_TIMEOUT = TuningI18NServer.toLocale("plugins_hyper_tuner_tips_timeout");

    /**
     * 国际化: Web服务证书过期告警阈值
     */
    public static final String TITLE_CERTIFICATE = TuningI18NServer.toLocale("plugins_hyper_tuner_title_certificate");

    /**
     * 国际化: 输入整数
     */
    public static final String CERTIFICATE_CONFIG = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_config");

    /**
     * 国际化: 输入整数
     */
    public static final String TITLE_PASSWORD = TuningI18NServer.toLocale("plugins_hyper_tuner_title_password");

    /**
     * 国际化: 输入整数
     */
    public static final String PASSWORD_CONFIG = TuningI18NServer.toLocale("plugins_hyper_tuner_password_config");

    /**
     * 国际化: 输入整数
     */
    public static final String RUN_NODE_HELP = TuningI18NServer.toLocale("plugins_hyper_tuner_run_node_help");

    /**
     * 国际化: 输入整数
     */
    public static final String LOG_NODE_HELP = TuningI18NServer.toLocale("plugins_hyper_tuner_log_node_help");
}

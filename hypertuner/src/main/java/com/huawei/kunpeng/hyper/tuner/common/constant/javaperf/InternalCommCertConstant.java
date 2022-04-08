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

package com.huawei.kunpeng.hyper.tuner.common.constant.javaperf;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * Java性能分析 模块
 * 内部通信证书 相关常量定义
 *
 * @since 2021-07-07
 */
public class InternalCommCertConstant {
    /**
     * 内部通信证书 面板展示名称
     */
    public static final String DISPLAY_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert");
    /**
     * 表头：证书名称
     */
    public static final String TABLE_COL_NAME_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_name");
    /**
     * 表头：状态
     */
    public static final String TABLE_COL_NAME_STATUS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_status");
    /**
     * 国际化：状态-valid 有效
     */
    public static final String TABLE_COL_NAME_STATUS_VALID = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_status_valid");
    /**
     * 国际化：状态-Expiring 即将到期
     */
    public static final String TABLE_COL_NAME_STATUS_EXPIRING = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_status_expiring");
    /**
     * 国际化：状态-Expired-已过期
     */
    public static final String TABLE_COL_NAME_STATUS_EXPIRED = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_status_expired");
    /**
     * 国际化：状态-NONE  Permanently valid -永久有效
     */
    public static final String TABLE_COL_NAME_STATUS_NONE = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_status_none");

    /**
     * 表头：证书到期时间
     */
    public static final String TABLE_COL_NAME_EXPIRE_TIME = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_expireTime");
    /**
     * 表头：证书类型
     */
    public static final String TABLE_COL_NAME_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_internalCommCert_type");
}

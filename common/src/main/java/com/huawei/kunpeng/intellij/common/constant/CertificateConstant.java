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

import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * The class: CertificateConstant
 *
 * @since 2021-8-25
 */
public class CertificateConstant {
    /**
     * 国际化： 证书名称
     */
    public static final String CERT_NAME = I18NServer.toLocale("plugins_porting_setting_crl_cert_name");

    /**
     * 国际化：颁发者
     */
    public static final String ISSUER = I18NServer.toLocale("plugins_porting_setting_crl_issuer");

    /**
     * 国际化：生效日期
     */
    public static final String EFFECTIVE_DATE = I18NServer.toLocale("plugins_porting_setting_crl_effective_date");

    /**
     * 国际化：下一次更新时间
     */
    public static final String NEXT_UPDATE_TIME = I18NServer.toLocale("plugins_porting_setting_crl_next_update_time");

    /**
     * 国际化：状态
     */
    public static final String CERT_STATUS = I18NServer.toLocale("plugins_porting_setting_crl_cert_status");
    /**
     * 国际化：操作
     */
    public static final String OPERATE = I18NServer.toLocale("plugins_porting_setting_crl_operate");

    /**
     * 国际化：导入
     */
    public static final String TITLE_IMPORT_CRL = I18NServer.toLocale("plugins_porting_setting_crl_title_import_crl");

    /**
     * 国际化：删除
     */
    public static final String TITLE_DELETE_CRL = I18NServer.toLocale("plugins_porting_setting_crl_title_delete_crl");

    /**
     * 国际化：序列号
     */
    public static final String SERIAL_NUMBER = I18NServer.toLocale("plugins_porting_crl_serial_number");

    /**
     * 国际化：吊销日期
     */
    public static final String REVOCATION_DATE = I18NServer.toLocale("plugins_porting_crl_revocation_date");
}

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
 * 部署声明相关公共常量定义
 *
 * @since 2021-04-09
 */
public class InstallConstant {
    /**
     * 国际化: 部署前必读
     */
    public static final String BEFORE_INSTALL = CommonI18NServer.toLocale("common_message_beforeInstall");

    /**
     * 国际化: 点击此处部署
     */
    public static final String CLICK_DEPLOY = CommonI18NServer.toLocale("common_tip_install");

    /**
     * 国际化: 读部署
     */
    public static final String READ_DEPLOY = CommonI18NServer.toLocale("common_message_beforeInstallOption");

    /**
     * 国际化: 确认保存配置标题
     */
    public static final String CONFIG_SAVE_CONFIRM_TITLE = CommonI18NServer
            .toLocale("common_config_saveConfirm_title");

    /**
     * 国际化: 确认指纹标题
     */
    public static final String FINGER_CONFIRM_TITLE = CommonI18NServer.toLocale("common_using_account_title_name");
}

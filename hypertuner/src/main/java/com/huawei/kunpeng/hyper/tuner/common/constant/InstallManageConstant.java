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

import com.huawei.kunpeng.intellij.common.constant.InstallConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 安装部署/卸载 相关常量定义
 *
 * @since 2020-10-6
 */
public class InstallManageConstant  extends InstallConstant {
    /**
     * 安装面板title
     */
    public static final String INSTALL_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_install_title");

    /**
     * 升级面板title
     */
    public static final String UPGRADE_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_upgrade_title");

    /**
     * 国际化: 部署声明内容
     */
    public static final String DEPLOY_CONTENT =I18NServer.toLocale(
            "plugins_hyper_tuner_message_beforeInstallDsc");

    /**
     * 国际化: 部署必读标题
     */
    public static final String FIRST_CONFIG_TITLE =I18NServer.toLocale(
            "plugins_hyper_tuner_message_beforeInstallTitle");

    /**
     * 国际化: 确认保存配置标题
     */
    public static final String CONFIG_SAVE_CONFIRM_TITLE =I18NServer.toLocale(
            "plugins_hyper_tuner_config_saveConfirm_title");


    /**
     * 国际化：卸载标题
     */
    public static final String UNINSTALL_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_uninstall_title");

    /**
     * 国际化：卸载成功
     */
    public static final String UNINSTALL_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_uninstall_success");

    /**
     * 国际化：导入证书
     */
    public static final String IMPORT_CA_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_import_caCret");
    /**
     * 国际化：导入证书成功
     */
    public static final String IMPORT_CA_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_import_caCret_success");
    /**
     * 国际化：导入证书失败
     */
    public static final String IMPORT_CA_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_import_caCret_fail");

}

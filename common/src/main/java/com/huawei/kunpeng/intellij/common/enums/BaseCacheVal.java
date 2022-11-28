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

package com.huawei.kunpeng.intellij.common.enums;

/**
 * 基础缓存KEY集合
 *
 * @since 1.0.0
 */
public enum  BaseCacheVal {
    LIGHT_THEME("LightTheme"),
    IP("ip"),
    PORT("port"),
    LOCAL_PORT("localPort"), // 本地nginx 代理端口
    TOKEN("token"),
    BASE_URL("baseUrl"),
    SYSTEM_OS("systemOS"),
    CURRENT_LOCALE("locale"),
    CURRENT_CHARSET("currentCharset"),
    CURRENT_PROJECT("currentProject"),
    TERMINAL_PROJECT("terminal_project"),
    JCEF_DLL_EVN_PATH("JcefDllEvnPath"),
    PORTING_ZH_PATH_UNICODE_STR("porting_zh_path_unicode_str"),
    PORTING_WEB_VIEW_INDEX("PortingWebViewIndex"),
    TUNING_WEB_VIEW_INDEX("TuningWebViewIndex"),
    FREE_TRIAL_WEB_VIEW_INDEX("FreeTrialWebViewIndex"),
    PORTING_SESSION("portingSession"),
    MIGRATION_TIP("migrationTip"),
    SERVER_VERSION("serverVersion"),
    CURRENT_LANGUAGE("Language"),
    COMMON_LANGUAGE("commonLanguage");

    private final String value;

    BaseCacheVal(String value) {
        this.value = value;
    }

    /**
     * get value of type
     *
     * @return value of type
     */
    public String vaLue() {
        return value;
    }
}

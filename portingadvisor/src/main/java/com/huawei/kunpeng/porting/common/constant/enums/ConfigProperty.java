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

package com.huawei.kunpeng.porting.common.constant.enums;

/**
 * config.json文件属性映射
 *
 * @since 2020-09-25
 */
public enum ConfigProperty {
    DEP_CONFIG("depConfig"),
    DEP_VERSION("depVersion"),
    HOST_VERIFIER("hostVerifier"),
    PORT_CONFIG("portConfig"),
    PORT_DISCLAIMER("portDisclaimer"),
    PORT_ENV_DO_NOT_PROMPT("portEnvDoNotPrompt"),
    PORT_VERSION("portVersion"),
    AUTO_LOGIN_CONFIG("autoLoginConfig"),
    PKG_URL("pkg_url"),
    CERT_PATH("certPath");

    private final String value;

    ConfigProperty(String value) {
        this.value = value;
    }

    /**
     * 获取字符串类型值
     *
     * @return 返回字符串类型值
     */
    public String vaLue() {
        return value;
    }
}
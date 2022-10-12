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
 * 操作系统类型
 *
 * @since 1.0.0
 */
public enum SystemOS {
    WINDOWS(0,  "window"),
    LINUX(1,  "linux"),
    MAC(3, "mac"),
    OTHER(2,  "linux");

    private final int value;

    private final String code;

    SystemOS(int value, String code) {
        this.value = value;
        this.code = code;
    }

    /**
     * 获取int类型值
     *
     * @return int
     */
    public int vaLue() {
        return value;
    }

    /**
     * 获取String类型值
     *
     * @return String
     */
    public String code() {
        return code;
    }
}
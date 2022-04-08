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
 * 语言类型
 *
 * @since 2020-09-25
 */
public enum Language {
    ZH(0, "zh-cn"),
    EN(1, "en-us");

    private final int value;

    private final String code;

    Language(int value, String code) {
        this.value = value;
        this.code = code;
    }

    /**
     * 获取int类型值
     *
     * @return 返回int
     */
    public int vaLue() {
        return value;
    }

    /**
     * 获取String类型值
     *
     * @return 返回String类型值
     */
    public String code() {
        return code;
    }
}
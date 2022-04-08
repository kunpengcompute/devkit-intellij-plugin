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
 * 任务类型枚举
 *
 * @since 2021/1/7
 */
public enum TaskType {
    SOURCE_SCAN("0"),
    REFACTOR("1"),
    WHITELIST_MANAGER("2"),
    MIGRATION("3"),
    SOLUTION_MANAGER("4"),
    MIGRATION_PRE_CHECK("5"),
    BYTE_ALIGN("6"),
    BINARY_SCAN("7"),
    WEAK_COMPILE("9"),
    WEAK_CHECK("10"),
    BC_CHECK("11"),
    CACHE_LINE_ALIGNMENT("12");

    private final String value;

    TaskType(String value) {
        this.value = value;
    }

    /**
     * 获取String类型值
     *
     * @return String
     */
    public String value() {
        return value;
    }
}

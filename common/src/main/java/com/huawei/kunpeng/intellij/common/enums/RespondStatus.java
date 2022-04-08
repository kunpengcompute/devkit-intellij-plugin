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
 * 接口响应状态枚举
 *
 * @since 1.0.0
 */
public enum RespondStatus {
    PROCESS_STATUS_ERROR("-1"),
    PROCESS_STATUS_NORMAL("0"),
    PROCESS_STATUS_NOT_NORMAL("1"),

    /**
     * 磁盘空间不足
     */
    DISK_NOT_ENOUGH("0x010611");

    private final String valueStatus;

    RespondStatus(String valueStatus) {
        this.valueStatus = valueStatus;
    }

    /**
     * 获取字符串类型值
     *
     * @return String
     */
    public String value() {
        return valueStatus;
    }

    /**
     * 通过延伸信息value获取RespondStatus类的一个枚举实例
     *
     * @param valueStatus 响应状态
     * @return String
     */
    public static RespondStatus getStatusByValue(String valueStatus) {
        for (RespondStatus status : RespondStatus.values()) {
            if (status.value().equals(valueStatus)) {
                return status;
            }
        }
        return RespondStatus.PROCESS_STATUS_NORMAL;
    }
}

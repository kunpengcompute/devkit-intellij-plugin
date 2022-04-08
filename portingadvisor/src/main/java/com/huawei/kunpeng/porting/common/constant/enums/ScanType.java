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
 * 上传文件scan_type
 *
 * @since 2021-01-12
 */
public enum ScanType {
    SOURCE_CODING_SCAN("0"),
    BYTE_CHECK("1"),
    PRE_CHECK("2"),
    WEAK_UPLOAD_ZIP("6"),
    WEAK_UPLOAD_COMPILED_FILE("7");

    private final String value;

    ScanType(String value) {
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

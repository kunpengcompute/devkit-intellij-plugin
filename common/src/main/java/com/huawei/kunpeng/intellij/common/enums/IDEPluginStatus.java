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
 * IDE plugin全局状态枚举
 *
 * @since 1.0.0
 */
public enum IDEPluginStatus {
    IDE_STATUS_INIT(0),
    IDE_STATUS_SERVER_DEPLOY(1),
    IDE_STATUS_SERVER_CONFIG(2),
    IDE_STATUS_LOGIN(3),
    IDE_STATUS_PORTING(4);

    private final int value;

    IDEPluginStatus(int value) {
        this.value = value;
    }

    /**
     * 获取enum对应的int类型值
     *
     * @return 返回enum对应的int类型值
     */
    public int value() {
        return value;
    }
}

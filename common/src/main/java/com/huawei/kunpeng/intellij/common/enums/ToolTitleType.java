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
 * 用到的IDE工具栏类型
 *
 * @since 1.0.0
 */
public enum ToolTitleType {
    ACTION("action"),
    TOOL_WINDOW("toolWindow");

    private final String titleType;

    ToolTitleType(String titleType) {
        this.titleType = titleType;
    }

    /**
     * 获取工具栏类型
     *
     * @return String
     */
    public String titleType() {
        return titleType;
    }
}
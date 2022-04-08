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
 * 页面类型
 *
 * @since 2020-11-16
 */
public enum PageType {
    NULL("null"),
    /**
     * 源码迁移
     */
    SOURCE("source"),
    REPORT("report"),
    /**
     * 软件包构建
     */
    ANALYSIS_CENTER("analysisCenter"),
    ANALYSIS_CENTER_REPORT("analysisCenter_report"),

    /**
     * 软件迁移评估
     */
    MIGRATION_APPRAISE("appraise"),
    MIGRATION_APPRAISE_REPORT("appraise_report"),

    /**
     * 专项软件迁移
     */
    MIGRATION_CENTER("migrationCenter"),

    /**
     * 增强功能
     */
    ENHANCED_FUNCTION("enhancedFunction"),
    ENHANCED_REPORT("enhancedReport"),
    BYTE_SHOW("byteShow"),
    CACHE_LINE_ALIGNMENT("cacheLineAlignment"),

    /**
     * 云环境申请流程
     */
    CLOUD_ENV_APPLICATION_PROCESS("cloudEnvApplicationProcess");

    private final String type;

    PageType(String type) {
        this.type = type;
    }

    /**
     * 获取函数名
     *
     * @return String
     */
    public String value() {
        return type;
    }

    /**
     * 通过延伸信息value获取PageType类的一个枚举实例
     *
     * @param value 值
     * @return String
     */
    public static PageType getStatusByValue(String value) {
        for (PageType pageType : PageType.values()) {
            if (pageType.value().equals(value)) {
                return pageType;
            }
        }

        return PageType.NULL;
    }
}

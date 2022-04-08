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
 * The Enum RestErrorCode.
 *
 * @since 1.0.0
 */
public enum ErrorCode {
    UNKNOWN_ERROR("plugins_common_error_unknownError"),
    FORBIDDEN("plugins_common_error_forbidden"),
    RESOURCE_NOT_FOUND("plugins_common_error_notFound"),
    CONFLICT("plugins_common_error_conflict"),
    RESOURCE_ALREADY_EXIST("plugins_common_error_resourceExist"),
    SESSION_EXPIRED("plugins_common_error_sessionExpired"),
    BAD_REQUEST("plugins_common_error_badRequest");

    private final String id;

    /**
     * Instantiates a new enum error code.
     *
     * @param id resource id , this id need match the key of the i18n file
     */
    ErrorCode(String id) {
        this.id = id;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */

    public String getId() {
        return id;
    }
}

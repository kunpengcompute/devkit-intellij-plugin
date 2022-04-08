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

package com.huawei.kunpeng.intellij.common.exception;

import com.huawei.kunpeng.intellij.common.enums.ErrorCode;
import com.huawei.kunpeng.intellij.common.enums.HttpStatus;

/**
 * The Class InternalServerException.
 *
 * @since 1.0.0
 */
public class InternalServerException extends IDEException {
    /**
     * Instantiates a new internal server exception. 500
     *
     * @param cause the cause
     */
    public InternalServerException(Throwable cause) {
        super(HttpStatus.HTTP_500_INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_ERROR, cause);
    }

    /**
     * Instantiates a new internal server exception.
     *
     * @param errorCode the error code
     */
    public InternalServerException(ErrorCode errorCode) {
        super(HttpStatus.HTTP_500_INTERNAL_SERVER_ERROR, errorCode);
    }
}

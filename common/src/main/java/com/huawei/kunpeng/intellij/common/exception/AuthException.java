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

import com.huawei.kunpeng.intellij.common.enums.HttpStatus;

/**
 * The Class AuthException.
 *
 * @since 1.0.0
 */
public class AuthException extends IDEException {
    /**
     * Instantiates a new auth exception. 401
     */
    public AuthException() {
        super(null, HttpStatus.HTTP_401_UNAUTHORIZED, null, null);
    }
}

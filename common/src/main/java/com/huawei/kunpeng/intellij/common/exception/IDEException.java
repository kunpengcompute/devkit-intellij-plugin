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
import com.huawei.kunpeng.intellij.common.util.I18NServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Class IDEException.
 *
 * @since 1.0.0
 */
public class IDEException extends RuntimeException {
    private static final long serialVersionUID = 477816283856688396L;

    private ErrorCode errorCode;

    private HttpStatus statusCode = HttpStatus.HTTP_500_INTERNAL_SERVER_ERROR;

    private List<?> args;

    /**
     * Instantiates a new ide exception.
     */
    public IDEException() {
        super();
    }

    /**
     * Instantiates a new ide exception.
     *
     * @param statusCode the status code
     * @param errorCode  the error code
     */
    public IDEException(HttpStatus statusCode, ErrorCode errorCode) {
        this(errorCode, statusCode, null, Collections.emptyList());
    }

    /**
     * Instantiates a new ide exception.
     *
     * @param errorCode  the code
     * @param statusCode http status code
     * @param cause      the cause
     */
    public IDEException(ErrorCode errorCode, HttpStatus statusCode, Throwable cause) {
        this(errorCode, statusCode, cause, null);
    }

    /**
     * Instantiates a new ide exception.
     *
     * @param errorCode  the error code
     * @param statusCode the status code
     * @param cause      the cause
     * @param args       the args
     */
    public IDEException(ErrorCode errorCode, HttpStatus statusCode, Throwable cause, List<?> args) {
        super(cause);
        ErrorCode tempErrorCode = errorCode;
        if (errorCode == null) {
            tempErrorCode = (statusCode == null) ? ErrorCode.UNKNOWN_ERROR : null;
        }
        this.errorCode = tempErrorCode;
        this.statusCode = statusCode;
        this.args = args;
    }

    /**
     * Instantiates a new ide exception.
     *
     * @param statusCode the status code
     * @param errorCode  the error code
     * @param arg0       the arg 0
     */
    public IDEException(HttpStatus statusCode, ErrorCode errorCode, Object arg0) {
        this(errorCode, statusCode, null, Arrays.asList(arg0));
    }

    /**
     * Instantiates a new ide exception.
     *
     * @param statusCode the status code
     * @param errorCode  the error code
     * @param cause      the cause
     */
    public IDEException(HttpStatus statusCode, ErrorCode errorCode, Throwable cause) {
        this(errorCode, statusCode, cause, Collections.emptyList());
    }

    /**
     * Instantiates a new ide exception.
     *
     * @param statusCode the status code
     * @param errorCode  the error code
     * @param arg0       the arg 0
     * @param arg1       the arg 1
     */
    public IDEException(HttpStatus statusCode, ErrorCode errorCode, Object arg0, Object arg1) {
        this(errorCode, statusCode, null, Arrays.asList(arg0, arg1));
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        if (errorCode != null) {
            return Translator.toLocale(errorCode.getId(), args);
        }
        if (statusCode != null) {
            return statusCode.name();
        }
        return HttpStatus.HTTP_500_INTERNAL_SERVER_ERROR.name();
    }

    /**
     * Error code to string.
     *
     * @return the error code
     */
    public String getErrorCode() {
        if (statusCode != null) {
            return statusCode.toString();
        }
        if (errorCode != null) {
            return errorCode.name();
        }
        return HttpStatus.HTTP_500_INTERNAL_SERVER_ERROR.toString();
    }

    private static class Translator {
        /**
         * Format message with parameters, args replaces %s characters
         *
         * @param code the code
         * @param args the args
         * @return the string
         */
        public static String toLocale(String code, List<?> args) {
            if (args == null || args.isEmpty()) {
                return I18NServer.toLocale(code);
            }
            return I18NServer.toLocale(code, args);
        }
    }
}

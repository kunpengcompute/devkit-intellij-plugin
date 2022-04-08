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
 * http请求响应状态枚举
 *
 * @since 1.0.0
 */
public enum HttpStatus {
    HTTP_200_OK(200),
    HTTP_400_BAD_REQUEST(400),
    HTTP_401_UNAUTHORIZED(401),
    HTTP_403_FORBIDDEN(403),
    HTTP_404_NOT_FOUND(404),
    HTTP_406_NOT_ACCEPTABLE(406),
    HTTP_409_CONFLICT(409),
    HTTP_412_PRECONDITION_FAILED(412),
    HTTP_417_EXPECTATION_FAILED(417),
    HTTP_423_LOCKED(423),
    HTTP_500_INTERNAL_SERVER_ERROR(500),
    HTTP_502_SERVERERROR(502);

    private final int value;

    HttpStatus(int value) {
        this.value = value;
    }

    /**
     * 获取字符串类型值
     *
     * @return 返回字符串类型值
     */
    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }

    /**
     * 通过延伸信息value获取HttpStatus类的一个枚举实例
     *
     * @param value 状态值
     * @return 返回HttpStatus
     */
    public static HttpStatus getHttpStatusByValue(int value) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.value() == value) {
                return status;
            }
        }
        return HttpStatus.HTTP_200_OK;
    }
}
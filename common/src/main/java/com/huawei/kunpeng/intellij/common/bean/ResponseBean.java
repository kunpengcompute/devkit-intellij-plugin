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

package com.huawei.kunpeng.intellij.common.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 响应数据体
 *
 * @since 1.0.0
 */
@Data
@ToString(of = {"status", "data", "info", "infochinese", "message", "realStatus", "responseJsonStr", "token", "code", "messageArgs"})
@EqualsAndHashCode(callSuper = false)
public class ResponseBean extends DataBean {
    /**
     * 获取响应状态
     */
    private String status;
    /**
     * 设置响应数据
     */
    private String data;

    /**
     * 获取响应info
     */
    private String info;

    /**
     * 设置响infochinese
     */
    private String infochinese;

    /**
     * 返回后端版本
     */
    private String version;

    /**
     * realStatus
     */
    private String realStatus;

    /**
     * 设置原始响应JSON串
     */
    private String responseJsonStr;

    /**
     * token
     */
    private String token;

    /**
     * 响应code
     */
    private String code;

    /**
     * 响应message
     */
    private String message;

    private String messageArgs;

}

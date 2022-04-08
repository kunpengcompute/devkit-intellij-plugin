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

/**
 * ssh配置信息
 *
 * @since 1.0.0
 */
@Data
public class SshConfig {
    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 用户
     */
    private String user;
    /**
     * 密码
     */
    private String password;

    /**
     * 认证方法
     */
    private String identity;

    /**
     * 指纹
     */
    private String fingerprint;

    /**
     * 密码短语
     */
    private String passPhrase;
}

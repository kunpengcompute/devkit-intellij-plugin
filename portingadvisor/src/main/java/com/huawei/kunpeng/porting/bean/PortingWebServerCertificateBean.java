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

package com.huawei.kunpeng.porting.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * WebServerCertificateBean
 *
 * @since 2020-10-07
 */
@Data
@Builder
@ToString(of = {"webServerCertName", "webServerCertExpireTime", "status"})
public class PortingWebServerCertificateBean {
    /**
     * web服务证书名称
     */
    @Builder.Default
    private String webServerCertName = "cert.pem";

    /**
     * web服务证书到期时间
     */
    private String webServerCertExpireTime;

    /**
     * web服务证书状态
     */
    private String status;
}

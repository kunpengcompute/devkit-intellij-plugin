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

package com.huawei.kunpeng.hyper.tuner.model;

/**
 * WebServerCertificateBean
 *
 * @since 2020-10-07
 */
public class TuningWebServerCertificateBean {
    /**
     * web服务证书名称
     */
    private String webServerCertName;

    /**
     * web服务证书到期时间
     */
    private String webServerCertExpireTime;

    /**
     * web服务证书状态
     */
    private String status;

    public TuningWebServerCertificateBean() {
        this(null, null);
    }

    /**
     * 推荐构造函数
     *
     * @param webServerCertExpireTime web服务证书到期时间
     * @param status                  web服务证书状态
     */
    public TuningWebServerCertificateBean(String webServerCertExpireTime, String status) {
        this.webServerCertName = "server.crt";
        this.webServerCertExpireTime = webServerCertExpireTime;
        this.status = status;
    }

    public void setWebServerCertName(String webServerCertName) {
        this.webServerCertName = webServerCertName;
    }

    public void setWebServerCertExpireTime(String webServerCertExpireTime) {
        this.webServerCertExpireTime = webServerCertExpireTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWebServerCertName() {
        return webServerCertName;
    }

    public String getWebServerCertExpireTime() {
        return webServerCertExpireTime;
    }

    public String getStatus() {
        return status;
    }

    /**
     * 重写toString
     *
     * @return String
     */
    @Override
    public String toString() {
        return "WebServerCertificateBean{"
                + "webServerCertName='"
                + webServerCertName
                + '\''
                + ", webServerCertExpireTime="
                + webServerCertExpireTime
                + '\''
                + ", status="
                + status
                + '}';
    }
}

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

package com.huawei.kunpeng.intellij.common.http.method;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.util.HttpCommonUtil;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Put请求类型
 *
 * @since 1.0.0
 */
public class PutRequest implements MethodRequest {
    @Override
    public void setRequestData(HttpURLConnection conn, RequestDataBean request) throws IOException {
        HttpMethodRequest.setRequestDataChar(conn, request);
    }

    /**
     * 设置上传类型的请求参数
     *
     * @param conn    连接
     * @param req 数据
     * @throws IOException io异常
     */
    @Override
    public void setRequestDataForUpload(HttpURLConnection conn, RequestDataBean req) throws IOException {
        HttpCommonUtil.setRequestDataForUpload(conn, req);
    }

    /**
     * 设置下载类型的请求参数
     *
     * @param conn    连接
     * @param request 数据
     * @throws IOException io异常
     */
    @Override
    public void setRequestDataForDownLoad(HttpURLConnection conn, RequestDataBean request) throws IOException {
        HttpCommonUtil.setRequestDataForDownLoad(conn, request);
    }
}
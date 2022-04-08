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

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;

/**
 * http请求方法类型设置
 *
 * @since 1.0.0
 */
public class HttpMethodRequest {
    private static Map<String, MethodRequest> methodRequest = new HashMap<>();

    static {
        methodRequest.put(HttpMethod.GET.vaLue(), new GetRequest());
        methodRequest.put(HttpMethod.POST.vaLue(), new PostRequest());
        methodRequest.put(HttpMethod.DELETE.vaLue(), new DeleteRequest());
        methodRequest.put(HttpMethod.PUT.vaLue(), new PutRequest());
    }

    /**
     * 设置请求公共属性
     *
     * @param conn 连接
     * @param request 数据
     * @throws ProtocolException 异常
     */
    private static void setCommonData(HttpURLConnection conn, RequestDataBean request) throws ProtocolException {
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Charset", IDEConstant.CHARSET_UTF8);
        conn.setRequestMethod(CommonUtil.normalizeForString(request.getMethod()));
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Authorization", CommonUtil.normalizeForString(request.getToken()));
    }

    /**
     * 根据不同请求类型设置请求属性
     *
     * @param method 请求方法
     * @param conn 连接
     * @param request 数据
     * @throws IOException 异常
     */
    public static void setRequestData(String method, HttpURLConnection conn, RequestDataBean request)
        throws IOException {
        if (methodRequest.get(method) == null) {
            Logger.error(method, " http method is nonexistent");
        }
        // 设置公共请求参数
        HttpMethodRequest.setCommonData(conn, request);
        // 上传文件请求
        if (request.isNeedUploadFile()) {
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            methodRequest.get(method).setRequestDataForUpload(conn, request);
        } else if (request.isNeedDownloadFile()) {
            conn.setRequestProperty("Content-Type", "application/json");
            // 下载文件请求
            methodRequest.get(method).setRequestDataForDownLoad(conn, request);
        } else {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Language", I18NServer.getCurrentLanguage());
            // 普通请求
            methodRequest.get(method).setRequestData(conn, request);
        }
    }

    /**
     * 请求编码设置
     *
     * @param conn 连接
     * @param request 数据
     * @throws IOException 异常
     */
    public static void setRequestDataChar(HttpURLConnection conn, RequestDataBean request) throws IOException {
        if (request.getBodyData() == null) {
            return;
        }
        try (OutputStream out = conn.getOutputStream()) {
            if (request.getCharset() != null && IDEConstant.CHARSET_UTF8.equals(request.getCharset())) {
                out.write(request.getBodyData().getBytes(IDEConstant.CHARSET_UTF8));
            } else {
                out.write(StringUtil.getStrCharsetByOSToServer(request.getBodyData())
                        .getBytes(IDEContext.getCurrentCharset() == null ?
                                IDEConstant.CHARSET_UTF8 : IDEContext.getCurrentCharset()));
            }
        }
    }
}

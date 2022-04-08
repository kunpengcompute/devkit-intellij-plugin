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

package com.huawei.kunpeng.intellij.common.http;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.exception.IDEException;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ByteBufferInputStreamAc;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 发送请求
 *
 * @since 2021-7-22
 */
public class HttpAPIServiceTrust {
    /**
     * 根据json参数获取响应体
     *
     * @param url       url
     * @param jsonParam 参数
     * @param method    请求方法
     * @param token     token
     * @param type      类型
     * @return 响应体
     * @throws IOException io异常
     */
    public static String getResponseString(String url, JSONObject jsonParam, String method,
        String token, String type) throws IOException {
        Map<String, String> httpHeaderMap = new HttpHeaderFactory(type, token).createHttpHeaderMap();
        return doRequestReal(url, jsonParam, method, token, httpHeaderMap);
    }

    private static String doRequestReal(String url, JSONObject jsonParam, String method, String token,
        Map<String, String> headerMap) throws IOException {
        try {
            HttpResponse response = null;
            CloseableHttpClient httpClient = HTTPSSLClient.getHttpClinet();
            switch (method) {
                case "PATCH":
                    response = patchRspHandle(httpClient, url, jsonParam, token);
                    break;
                case "POST":
                    response = postRspHandle(httpClient, url, jsonParam, headerMap);
                    break;
                default:
                    Logger.error("method {} not match case ", method);
            }
            if (response != null) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                } else {
                    Logger.error("rest error. url:{}", url);
                    throw new IOException("return error");
                }
            } else {
                Logger.info("response is null");
                return "";
            }
        } catch (IOException e) {
            Logger.error("rest net error. url:{}", url);
            Logger.error("IOException:{}", e);
            throw e;
        }
    }

    private static HttpResponse postRspHandle(CloseableHttpClient client, String url, JSONObject jsonParam,
        Map<String, String> headerMap) throws IOException {
        // 文件上传
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity;
        headerMap.forEach(httpPost::addHeader);
        Object fileObj = jsonParam.get("file");
        Object inputStreamObj = jsonParam.get("inputStream");
        if (jsonParam.size() == 1 && fileObj != null && fileObj instanceof File) {
            File file = (File) fileObj;
            httpPost.setEntity(uploadFile(file));
        } else if (jsonParam.size() == 5 && inputStreamObj != null &&
                inputStreamObj instanceof ByteBufferInputStreamAc) {
            ByteBufferInputStreamAc byteBufferInputStreamAc = (ByteBufferInputStreamAc) inputStreamObj;
            httpPost.setEntity(uploadFile(byteBufferInputStreamAc, jsonParam));
        } else {
            throw new InvalidObjectException("Input param error, file error");
        }
        // 执行提交
        return client.execute(httpPost);
    }

    private static HttpResponse patchRspHandle(CloseableHttpClient client, String url,
        JSONObject jsonObj, String token) throws IOException {
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.setHeader("Content-type", "application/merge-patch+json");
        httpPatch.setHeader("Charset", StandardCharsets.UTF_8.name());
        httpPatch.setHeader("Accept", "application/json");
        httpPatch.setHeader("Accept-Charset", StandardCharsets.UTF_8.name());
        httpPatch.addHeader("Authorization", token);
        httpPatch.addHeader("Accept-Language", I18NServer.getCurrentLanguage());
        if (jsonObj != null) {
            StringEntity entity = new StringEntity(jsonObj.toString(), StandardCharsets.UTF_8.name());
            httpPatch.setEntity(entity);
        }
        return client.execute(httpPatch);
    }

    private static HttpEntity uploadFile(ByteBufferInputStreamAc ByteBufferInputStreamAc, JSONObject jsonObject) {
        StringBody id = new StringBody(jsonObject.get("id").toString(),
                ContentType.create("text/plain", StandardCharsets.UTF_8));
        StringBody fileName = new StringBody(jsonObject.get("fileName").toString(),
                ContentType.create("text/plain", StandardCharsets.UTF_8));
        StringBody chunk = new StringBody(jsonObject.get("chunk").toString(),
                ContentType.create("text/plain", StandardCharsets.UTF_8));
        StringBody fileSize = new StringBody(jsonObject.get("fileSize").toString(),
                ContentType.create("text/plain", StandardCharsets.UTF_8));
        InputStreamBody fileInputStream = new InputStreamBody(ByteBufferInputStreamAc,
                ContentType.DEFAULT_BINARY, jsonObject.get("fileName").toString());
        Map<String, ContentBody> contentBodyMap = new HashMap<>(8);
        contentBodyMap.put("id", id);
        contentBodyMap.put("chunk", chunk);
        contentBodyMap.put("file_name", fileName);
        contentBodyMap.put("file_size", fileSize);
        contentBodyMap.put("file", fileInputStream);
        return buildMultipartEntity(contentBodyMap);
    }

    private static HttpEntity uploadFile(File file) {
        FileBody fileBody = new FileBody(file);
        Map<String, ContentBody> contentBodyMap = new HashMap<>(2);
        contentBodyMap.put("file", fileBody);
        return buildMultipartEntity(contentBodyMap);
    }

    private static HttpEntity buildMultipartEntity(Map<String, ContentBody> contentBodyMap) {
        // HttpEntity builder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 字符编码
        builder.setCharset(Charset.forName(IDEContext.getCurrentCharset()));
        // 模拟浏览器
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setContentType(ContentType.create(
                "multipart/form-data", Charset.forName(IDEContext.getCurrentCharset())));
        contentBodyMap.forEach(builder::addPart);
        // HttpEntity
        return builder.build();
    }

    /**
     * 获取当前请求的url
     *
     * @param context 上下文
     * @param message 请求体封装对象
     * @return url
     */
    public static String getCurrentRequestUrl(Map<String, Object> context, RequestDataBean message) {
        String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue()))
                .map(Object::toString).orElse(null);
        String port = Optional.ofNullable(context.get(BaseCacheVal.PORT.vaLue()))
                .map(Object::toString).orElse(null);
        if (ValidateUtils.isEmptyString(ip) || ValidateUtils.isEmptyString(port)) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean("", I18NServer.toLocale("plugins_common_message_configServer"),
                            NotificationType.WARNING));
            throw new IDEException();
        }
        // 组装完整的url
        return IDEConstant.URL_PREFIX +
                ip +
                ":" +
                port +
                context.get(BaseCacheVal.BASE_URL.vaLue()) +
                message.getUrl();
    }
}
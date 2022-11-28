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

import com.huawei.kunpeng.intellij.common.BaseCacheDataOpt;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.HttpStatus;
import com.huawei.kunpeng.intellij.common.exception.IDEException;
import com.huawei.kunpeng.intellij.common.http.method.HttpMethodRequest;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.alibaba.fastjson.JSONException;
import com.intellij.notification.NotificationType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * https请求服务
 *
 * @since 1.0.0
 */
public abstract class HttpsServer {
    /**
     * 是否认证服务器证书成功
     */
    public static boolean isCertConfirm = true;

    /**
     * http对外服务接口，从全局context中读取ip和port
     *
     * @param message 自定义请求传入数据
     * @return ResponseBean
     */
    public ResponseBean requestData(RequestDataBean message) {
        ResponseBean response = null;
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, message.getModule());
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue())).map(Object::toString).orElse(null);
            String port = Optional.ofNullable(context.get(BaseCacheVal.PORT.vaLue())).map(Object::toString).orElse(null);
            if (ip == null && port == null) {
                IDENotificationUtil.notificationCommon(new NotificationBean
                        ("", I18NServer.toLocale("plugins_common_message_configServer"), NotificationType.WARNING));
                throw new IDEException();
            }
            // 组装完整的url
            String url = IDEConstant.URL_PREFIX +
                    ip +
                    ":" +
                    port +
                    context.get(BaseCacheVal.BASE_URL.vaLue()) +
                    message.getUrl();
            message.setUrl(url);
            // 对需要token的接口设置token
            if (message.isNeedToken()) {
                String token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue()))
                        .map(Object::toString).orElse(null);
                message.setToken(token);
            }
            // 获取后端请求数据
            response = this.requestWebData(message).orElse(null);
        }
        return response;
    }

    /**
     * http对外服务接口，传入ip和port参数
     *
     * @param message 自定义请求传入数据
     * @param ip 服务器ip地址
     * @param port 服务器端口
     * @return ResponseBean
     */
    public ResponseBean requestDataWithIpAndPort(RequestDataBean message, String ip, String port) {
        ResponseBean response = null;
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, message.getModule());
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            if (ip == null && port == null) {
                throw new IDEException();
            }
            // 组装完整的url
            String url = IDEConstant.URL_PREFIX +
                    ip +
                    ":" +
                    port +
                    context.get(BaseCacheVal.BASE_URL.vaLue()) +
                    message.getUrl();
            message.setUrl(url);
            // 对需要token的接口设置token
            if (message.isNeedToken()) {
                String token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue()))
                        .map(Object::toString).orElse(null);
                message.setToken(token);
            }
            // 获取后端请求数据
            response = this.requestWebData(message).orElse(null);
        }
        return response;
    }

    /**
     * 发送请求并获取数据
     *
     * @param request 请求数据体
     * @return ResponseBean
     */
    private Optional<ResponseBean> requestWebData(RequestDataBean request) {
        ResponseBean response = null;
        try {
            // 预留异常标记
            String rep = sendSSLRequest(request).get();
            response = JsonUtil.jsonToDataModel(StringUtil.getStrFromDiffCharset(rep, IDEConstant.CHARSET_UTF8,
                    IDEConstant.CHARSET_UTF8), ResponseBean.class);
            if (response == null) {
                return Optional.empty();
            }
            handleResponseStatus(response);
            // 更新全局缓存token信息
            String token = response.getToken();
            if (!StringUtil.stringIsEmpty(token)) {
                BaseCacheDataOpt.updateGlobalToken(request.getModule(), token);
            }
        } catch (JSONException e) {
            Logger.error("requestWebData error.");
        } catch (NoSuchElementException e) {
            Logger.error("No value present.");
        }
        return Optional.of(response);
    }

    /**
     * 发送https请求
     *
     * @param request 请求
     * @return Optional<String>
     */
    private Optional<String> sendSSLRequest(RequestDataBean request) {
        HttpsURLConnection conn = null;
        try {
            TrustManager[] trustManagers = getTrustManagers();
            // 创建https连接
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustManagers, new java.security.SecureRandom());
            URL console = new URL(CommonUtil.encodeForURL(StringUtil.getStrCharsetByOSToServer(StringUtil
                    .getUrlIncludeParams(request.getUrl(), request.getUrlParams()))));
            URLConnection urlConnection = console.openConnection();
            if (urlConnection instanceof HttpsURLConnection) {
                conn = (HttpsURLConnection) urlConnection;
                conn.setSSLSocketFactory(sc.getSocketFactory());
                conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
                // 设置请求参数
                HttpMethodRequest.setRequestData(request.getMethod(), conn, request);
            }

            // 未收到服务器响应， 请检查网络设置
            if (conn != null && conn.getHeaderFields().isEmpty() && !request.isNeedCancel()) {
                return dealServerUnResponse(request);
            }
            if (conn != null) {
                return receiveResponse(conn);
            }
        } catch (ConnectException | SocketTimeoutException e) {
            Logger.error("invoke HttpUtils.sendSSLRequest ConnectException | SocketTimeoutException!!!");
//            return displayServerAbnormalPanel();
            return Optional.of("");
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException e) {
            Logger.error("invoke HttpUtils.sendSSLRequest IOException|KeyManagementException" + "|NoSuchAlgorithmException!!");
        } finally {
            HttpsURLConnection finalConn = conn;
            FileUtil.closeStreams(null, () -> {
                if (finalConn != null) {
                    finalConn.disconnect();
                }
            });
        }
        return Optional.of("");
    }

    @NotNull
    private TrustManager[] getTrustManagers() {
        TrustManager[] trustManagers = new TrustManager[]{new TrustAnyTrustManager()};
        Map configMap = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        String certPath = "";
        if (configMap.get(ConfigProperty.CERT_PATH.vaLue()) instanceof String) {
            certPath = (String) configMap.get(ConfigProperty.CERT_PATH.vaLue());
        }
        if (!StringUtil.stringIsEmpty(certPath)) {
            Optional<File> fileOptional = FileUtil.getFile(certPath, false);
            if (!fileOptional.isPresent()) {
                return trustManagers;
            }
            File file = fileOptional.get();
            try (FileInputStream caFileInputStream = new FileInputStream(file)) {
                X509Certificate x509certificate = null;
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate certificate = cf.generateCertificate(caFileInputStream);
                if (certificate instanceof X509Certificate) {
                    x509certificate = (X509Certificate) certificate;
                }
                trustManagers = new TrustManager[]{new SafeTrustManager(x509certificate)};
            } catch (IOException | CertificateException e) {
                isCertConfirm = false;
                Logger.error("certificate validate failed, because of {}!!", e.getMessage());
            }
        }
        return trustManagers;
    }

    /**
     * 获取请求响应数据
     *
     * @param conn 连接
     * @return Optional<String>
     * @throws IOException io异常
     */
    protected Optional<String> receiveResponse(HttpURLConnection conn) throws IOException {
        BufferedReader br = null;
        try {
            int rspCode = conn.getResponseCode();
            String message = null;
            switch (HttpStatus.getHttpStatusByValue(rspCode)) {
                case HTTP_200_OK: {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    return generateResponse(handleErr(br).get(), conn);
                }
                case HTTP_400_BAD_REQUEST:
                    dealUnAuthorized400(conn);
                    break;
                case HTTP_401_UNAUTHORIZED:
                    dealUnAuthorized(conn);
                    break;
                case HTTP_404_NOT_FOUND:
                    message = HttpStatus.HTTP_404_NOT_FOUND.name();
                    break;
                case HTTP_409_CONFLICT:
                    dealUnAuthorized409(conn);
                    break;
                case HTTP_406_NOT_ACCEPTABLE: {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    return handleErr(br);
                }
                default:
                    // 错误处理状态码 500系列
//                    return displayServerAbnormalPanel();
                    return Optional.of("");
            }
            if (rspCode != HttpURLConnection.HTTP_OK && rspCode != HttpURLConnection.HTTP_UNAUTHORIZED
                    && rspCode != HttpURLConnection.HTTP_BAD_REQUEST
                    && rspCode != HttpStatus.HTTP_423_LOCKED.value()) {
                Logger.error("Detail message is {} and response message is {}", message, "Request Error.");
            }
        } catch (IOException e) {
            Logger.error("Failed call the api.IOException");
        } finally {
            handleFinally(conn, br);
        }
        return Optional.of("");
    }

    /**
     * generateResponse 根据返回值 生成响应字符串
     *
     * @param responseStr 接口返回值
     * @param conn        HttpURLConnection
     * @return Optional<String>
     */
    protected Optional<String> generateResponse(String responseStr, HttpURLConnection conn) {
        ResponseBean response = JsonUtil.jsonToDataModel(StringUtil.getStrFromDiffCharset(responseStr,
                IDEConstant.CHARSET_UTF8, IDEConstant.CHARSET_UTF8), ResponseBean.class);
        if (response == null) {
            return Optional.empty();
        }
        // 响应中无data格式的数据时，将响应信息整个放入data
        if (StringUtil.stringIsEmpty(response.getData())) {
            response.setData(responseStr);
        }
        // 更新token
        String token = conn.getHeaderField("token");
        if (!StringUtil.stringIsEmpty(token)) {
            response.setToken(token);
        }
        response.setResponseJsonStr(responseStr);
        return Optional.ofNullable(JsonUtil.getJsonStrFromJsonObj(response));
    }

    /**
     * handleErr 处理接口返回的数据
     *
     * @param br 接口返回的字符输入流
     * @return 读出的字符串
     * @throws IOException IOException
     */
    @NotNull
    protected Optional<String> handleErr(BufferedReader br) throws IOException {
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append(IDEConstant.LINE_SEPARATOR);
        }
        // 适配下载场景后端无数据问题。
        if (sb.length() == 0) {
            sb.append(IDEConstant.LINE_SEPARATOR).append(IDEConstant.LINE_SEPARATOR);
        }
        return Optional.ofNullable(sb.substring(0, sb.length() - IDEConstant.LINE_SEPARATOR.length()));
    }

    /**
     * 接口请求最终处理，关闭输入流
     *
     * @param conn HttpURLConnection
     * @param br   接口返回的字符输入流
     * @throws IOException IOException
     */
    protected void handleFinally(HttpURLConnection conn, BufferedReader br) throws IOException {
        FileUtil.closeStreams(br, null);
        // 406,423为非标自定义返回码，InputStream不会有任何内容，报IOException异常
        if (HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_406_NOT_ACCEPTABLE
                && HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_423_LOCKED) {
            FileUtil.closeStreams(conn.getInputStream(), null);
        }
        FileUtil.closeStreams(conn.getErrorStream(), null);
    }

    /**
     * 自定义处理400
     *
     * @param conn conn
     * @throws IOException IOException
     */
    public abstract void dealUnAuthorized400(HttpURLConnection conn) throws IOException;

    /**
     * 自定义处理409
     *
     * @param conn conn
     * @throws IOException IOException
     */
    public abstract void dealUnAuthorized409(HttpURLConnection conn) throws IOException;

    /**
     * 自定义处理401
     *
     * @param conn conn
     */
    public abstract void dealUnAuthorized(HttpURLConnection conn);

    /**
     * 服务未响应处理
     *
     * @param request requestDataBean
     * @return Optional<String>
     */
    public abstract Optional<String> dealServerUnResponse(RequestDataBean request);

    /**
     * 服务请求异常处理
     *
     * @return Optional<String>
     */
    public abstract Optional<String> displayServerAbnormalPanel();

    /**
     * 处理状态码各自服务状态码
     *
     * @param responseBean 响应数据
     */
    public abstract void handleResponseStatus(ResponseBean responseBean);

    /**
     * ssl校验设置
     *
     * @since 2020-09-25
     */
    public static class TrustAnyTrustManager implements X509TrustManager {
        /**
         * checkClientTrusted
         *
         * @param chain    chain
         * @param authType authType
         */
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            if (ValidateUtils.isEmptyString(authType)) {
                Logger.error("authType is null");
            }
            if (ValidateUtils.isEmptyArray(chain)) {
                Logger.error("chain is null");
            }
        }

        /**
         * checkServerTrusted
         *
         * @param chain    chain
         * @param authType authType
         */
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            if (ValidateUtils.isEmptyString(authType)) {
                Logger.error("authType is null");
            }
            if (ValidateUtils.isEmptyArray(chain)) {
                Logger.error("chain is null");
            }
        }

        /**
         * getAcceptedIssuers
         *
         * @return X509Certificate[]
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    /**
     * TrustAnyHostnameVerifier
     *
     * @since 2020-09-25
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        /**
         * verify
         *
         * @param hostname hostname
         * @param session  session
         * @return boolean
         */
        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (ValidateUtils.isNotEmptyString(hostname) || session != null) {
                return true;
            }
            return false;
        }
    }

    /**
     * ssl cert safe verify
     */
    public static class SafeTrustManager implements X509TrustManager {
        /**
         * X.509 certificates
         */
        public X509Certificate x509certificate;

        public SafeTrustManager(X509Certificate x509certificate) {
            this.x509certificate = x509certificate;
        }

        /**
         * checkClientTrusted
         *
         * @param chain    chain
         * @param authType authType
         */
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        /**
         * checkServerTrusted
         *
         * @param x509Certificates x509Certificates
         * @param authType         authType
         * @throws CertificateException certificateException
         */
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
            try {
                for (X509Certificate cert : x509Certificates) {
                    cert.checkValidity();
                    cert.verify(x509certificate.getPublicKey());
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
                isCertConfirm = false;
                Logger.error("certificate verify failed");
            }
        }

        /**
         * getAcceptedIssuers
         *
         * @return X509Certificate[]
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}

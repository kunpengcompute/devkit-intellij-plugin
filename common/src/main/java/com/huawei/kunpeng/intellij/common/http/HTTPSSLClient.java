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

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;

/**
 * ssl方式获取httpClient
 *
 * @since 2021-7-22
 */
public class HTTPSSLClient {
    private static final String HTTP = "http";

    private static final String HTTPS = "https";

    private static SSLConnectionSocketFactory sslConnectionSocketFactory = null;

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null; // 连接池管理类

    private static SSLContext sslContext = null; // 管理Https连接的上下文类

    static {
        try {
            TrustManager[] trustManagers = getTrustManagers();
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registryBuilder
                    = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, PlainConnectionSocketFactory.INSTANCE)
                    .register(HTTPS, sslConnectionSocketFactory)
                    .build();
            poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder);
            poolingHttpClientConnectionManager.setMaxTotal(200);
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Get sslContext NoSuchAlgorithmException.");
        } catch (KeyManagementException e) {
            Logger.error("Get sslContext KeyManagementException.");
        }
    }

    /**
     * 获取连接
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }

    private static TrustManager[] getTrustManagers() {
        TrustManager[] trustManagers = new TrustManager[]{new HttpsServer.TrustAnyTrustManager()};
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        String certPath = "";
        if (config.get(ConfigProperty.CERT_PATH.vaLue()) instanceof String) {
            certPath = (String) config.get(ConfigProperty.CERT_PATH.vaLue());
        }
        if (!StringUtil.stringIsEmpty(certPath)) {
            Optional<File> fileOptional = FileUtil.getFile(certPath, false);
            if (!fileOptional.isPresent()) {
                return trustManagers;
            }
            File file = fileOptional.get();
            try (FileInputStream caFileInputStream = new FileInputStream(file)) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate certificate = cf.generateCertificate(caFileInputStream);
                if (certificate instanceof X509Certificate) {
                    X509Certificate x509certificate = (X509Certificate) certificate;
                    trustManagers = new TrustManager[]{new HttpsServer.SafeTrustManager(x509certificate)};
                }
            } catch (IOException | CertificateException e) {
                Logger.error("certificate validate failed, because of {}!!", e.getMessage());
            }
        }
        return trustManagers;
    }
}

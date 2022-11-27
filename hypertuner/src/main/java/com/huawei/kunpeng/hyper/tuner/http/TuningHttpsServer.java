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

package com.huawei.kunpeng.hyper.tuner.http;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEContext;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpStatus;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.http.HttpsServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * https请求服务
 *
 * @since 2020-09-25
 */
public class TuningHttpsServer extends HttpsServer {
    /**
     * 实例
     */
    public static final HttpsServer INSTANCE = new TuningHttpsServer();
    private static final int STATUS_MIN_LEN = 2;
    private boolean isTimer = false;

    private TuningHttpsServer() {
    }


    /**
     * 获取请求响应数据
     *
     * @param conn 连接
     * @return Optional<String>
     * @throws IOException io异常
     */
    @Override
    public Optional<String> receiveResponse(HttpURLConnection conn) throws IOException {
        BufferedReader br = null;
        try {
            int rspCode = conn.getResponseCode();
            String message = null;
            switch (HttpStatus.getHttpStatusByValue(rspCode)) {
                case HTTP_200_OK: {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    return cmdsResponse(conn, br);
                }
                case HTTP_400_BAD_REQUEST:
                case HTTP_403_FORBIDDEN:
                case HTTP_409_CONFLICT:
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    return generateResponse(handleErr(br).get(), conn);
                case HTTP_401_UNAUTHORIZED:
                    dealUnAuthorized(conn);
                    break;
                case HTTP_404_NOT_FOUND:
                    message = HttpStatus.HTTP_404_NOT_FOUND.name();
                    break;
                case HTTP_412_PRECONDITION_FAILED:
                    message = HttpStatus.HTTP_412_PRECONDITION_FAILED.name();
                    break;
                case HTTP_406_NOT_ACCEPTABLE: {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    return handleErr(br);
                }
                case HTTP_417_EXPECTATION_FAILED: {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    return generateResponse(handleErr(br).get(), conn);
                }
                default:
                    // 错误处理状态码 500系列
//                    return displayServerAbnormalPanel();
                    return Optional.of("");
            }
            if (rspCode != HttpURLConnection.HTTP_OK && rspCode != HttpURLConnection.HTTP_UNAUTHORIZED
                    && rspCode != HttpURLConnection.HTTP_BAD_REQUEST && rspCode != HttpStatus.HTTP_423_LOCKED.value()) {
                Logger.error("Detail message is {} and response message is {}", message, "Request Error.");
            }
        } catch (IOException e) {
            Logger.error("Failed call the api.IOException");
        } finally {
            handleFinally(conn, br);
        }
        return Optional.of("");
    }

    @Nullable
    private Optional<String> cmdsResponse(HttpURLConnection conn, BufferedReader br) throws IOException {
        if (conn.getURL().toString().contains("/cmds/")) {
            ResponseBean response = new ResponseBean();
            Optional<String> optStr = handleErr(br);
            response.setData(optStr.get());
            response.setResponseJsonStr(optStr.get());
            return Optional.ofNullable(JsonUtil.getJsonStrFromJsonObj(response));
        }
        return generateResponse(handleErr(br).get(), conn);
    }

    private Optional<String> handlerUploadFailed(RequestDataBean request) {
        String detail = I18NServer.toLocale("plugins_hyper_tuner_tip_file_upload_failed")
                + request.getFile()[0].getName();
        errorTipInfo(detail);
        return Optional.ofNullable("");
    }

    private void errorTipInfo(String detail) {
        IDENotificationUtil.notificationCommon(new NotificationBean("", detail, NotificationType.ERROR));
    }

    @Override
    public void handleFinally(HttpURLConnection conn, BufferedReader br) throws IOException {
        FileUtil.closeStreams(br, null);
        // 406,423为非标自定义返回码，InputStream不会有任何内容，报IOException异常
        if (isCloseInputStream(conn)) {
            FileUtil.closeStreams(conn.getInputStream(), null);
        }
        FileUtil.closeStreams(conn.getErrorStream(), null);
    }

    private boolean isCloseInputStream(HttpURLConnection conn) throws IOException {
        return HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_423_LOCKED
                && HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_406_NOT_ACCEPTABLE
                && HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_403_FORBIDDEN
                && HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_409_CONFLICT
                && HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_417_EXPECTATION_FAILED
                && HttpStatus.getHttpStatusByValue(conn.getResponseCode()) != HttpStatus.HTTP_400_BAD_REQUEST;
    }

    @Override
    public void dealUnAuthorized400(HttpURLConnection conn) {
    }

    @Override
    public void dealUnAuthorized409(HttpURLConnection conn) {
    }

    /**
     * 处理接口返回401状态码
     *
     * @param conn 连接对象
     */
    @Override
    public void dealUnAuthorized(HttpURLConnection conn) {
        // 左侧树面板还原到登录面板状态,
        if (!TuningIDEContext.getTuningIDEPluginStatus().equals(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG)) {
            UserInfoContext.getInstance().clearUserInfo();
            BufferedReader br = null;
            try {
                String line;
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.indexOf("CrowdedOut") > -1) {
                    IDENotificationUtil.notificationCommon(
                            new NotificationBean(
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_login_needLoginTitle"),
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_login_other"),
                                    NotificationType.INFORMATION));
                } else {
                    // 自动登录
                    IDENotificationUtil.notificationCommon(
                            new NotificationBean(
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_login_needLoginTitle"),
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_login_needLogin"),
                                    NotificationType.INFORMATION));
                }
                // update global IDEPluginStatus
                TuningIDEContext.setTuningIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);

            } catch (IOException e) {
                Logger.error("An exception occurred when processing dealUnAuthorized.");
            } finally {
                FileUtil.closeStreams(br, null);
            }
        }
    }

    @Override
    public Optional<String> dealServerUnResponse(RequestDataBean request) {
        isTimer = request.getUrl().contains("auto-flag") || request.getUrl().contains("java-perf/api/guardians") ||
                request.getUrl().contains("java-perf/api/records") || request.getUrl().contains("java-perf/guardians")||
                request.getUrl().contains("/version");
        if (request.isNeedUploadFile()) {
            return handlerUploadFailed(request);
        }
//        return displayServerAbnormalPanel();
        return Optional.ofNullable("");
    }

    /**
     * 显示服务器异常引导面板
     *
     * @return Optional<String>
     */
    @Override
    public Optional<String> displayServerAbnormalPanel() {
        String detail = TuningI18NServer.toLocale("plugins_common_message_responseError_messagePrefix")
                + TuningI18NServer.toLocale("plugins_common_message_responseError_viewDetail")
                + TuningI18NServer.toLocale("plugins_common_message_responseError_messageSuffix");
        if (!isTimer) {
            errorTipInfo(detail);
        } else {
            UserInfoContext.getInstance().clearUserInfo();
        }
        return Optional.of("");
    }

    /**
     * 处理状态码
     *
     * @param responseBean 响应数据
     */
    @Override
    public void handleResponseStatus(ResponseBean responseBean) {
        final HashSet<String> specialCode = new HashSet<>();
        specialCode.add(RespondStatus.LOGIN_FIRST_SUCCESS.value());
        specialCode.add(RespondStatus.LOGIN_SUCCESS_PWD_EXPIRED.value());
        String status = responseBean.getStatus();
        Map<String, Object> response = JsonUtil.getJsonObjectFromJsonStr(responseBean.getResponseJsonStr());
        if (response != null && response.size() == 0) {
            // webview 锁与等待分析 函数信息页面 下载 svg 图片
            response.put("data", responseBean.getResponseJsonStr());
        }
        if (response != null) {
            response.put("realStatus", status);
            responseBean.setRealStatus(status);
        }
        if (status != null && status.length() >= STATUS_MIN_LEN) {
            int index = status.length() - STATUS_MIN_LEN;
            String specialNumber = Character.toString(status.charAt(index));
            if (response != null) {
                response.put("status", Integer.parseInt(specialNumber));
            }
            if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(specialNumber) &&
                    !specialCode.contains(status)) {
                responseBean.setStatus(RespondStatus.PROCESS_STATUS_NORMAL.value());
            }
        }
        responseBean.setResponseJsonStr(JsonUtil.getJsonStrFromJsonObj(response));
    }
}

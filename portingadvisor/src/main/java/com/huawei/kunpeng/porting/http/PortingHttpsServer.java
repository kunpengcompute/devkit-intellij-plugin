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

package com.huawei.kunpeng.porting.http;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.IDEPluginStatus;
import com.huawei.kunpeng.intellij.common.http.HttpsServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.LoginWrapDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.serverconfig.PortingServerConfigAction;
import com.huawei.kunpeng.porting.action.toolwindow.LeftTreeUtil;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.ui.dialog.wrap.ErrorWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.ErrorPanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * https请求服务
 *
 * @since 2020-09-25
 */
public class PortingHttpsServer extends HttpsServer {
    /**
     * http instance
     */
    public static final HttpsServer INSTANCE = new PortingHttpsServer();

    private static final int STATUS_MIN_LEN = 2;

    private PortingHttpsServer() {
    }

    /**
     * 处理文件上传失败
     *
     * @param request 请求数据
     * @return Optional<String>
     */
    private Optional<String> handlerUploadFailed(RequestDataBean request) {
        StringBuffer detail = new StringBuffer(I18NServer.toLocale("plugins_porting_tip_file_upload_failed"));
        detail.append(request.getFile()[0].getName());
        IDENotificationUtil.notificationForHyperlink(
            new NotificationBean("", detail.toString(), NotificationType.ERROR), new ActionOperate() {
                @Override
                public void actionOperate(Object data) {
                    IDEBasePanel panel = new ErrorPanel(
                        null, Panels.ERROR_GUIDE.panelName(), false, true);
                    IDEBaseDialog dialog = new ErrorWrapDialog(
                        I18NServer.toLocale("common_term_error_guide_title"), panel);
                    dialog.displayPanel();
                }
            });

        return Optional.ofNullable("");
    }

    /**
     * 显示服务器异常引导面板
     *
     * @return Optional<String>
     */
    @Override
    public Optional<String> displayServerAbnormalPanel() {
        StringBuffer detail = new StringBuffer();
        detail.append(I18NServer.toLocale("plugins_common_message_responseError_messagePrefix"))
            .append(I18NServer.toLocale("plugins_common_message_responseError_viewDetail"))
            .append(I18NServer.toLocale("plugins_common_message_responseError_messageSuffix"));
        IDENotificationUtil.notificationForHyperlink(
            new NotificationBean("", detail.toString(), NotificationType.ERROR), data -> {
                IDEBasePanel panel = new ErrorPanel(null, Panels.ERROR_GUIDE.panelName(), false, true);
                IDEBaseDialog dialog = new ErrorWrapDialog(
                    I18NServer.toLocale("common_term_error_guide_title"), panel);
                dialog.displayPanel();
            });
        return Optional.of("");
    }

    /**
     * 处理接口返回400状态码
     *
     * @param conn 连接对象
     */
    @Override
    public void dealUnAuthorized400(HttpURLConnection conn) {
        Logger.error("400 bad request.");
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", I18NServer.toLocale("http_400_bad_request"), NotificationType.ERROR));
    }

    /**
     * 处理接口返回409状态码
     *
     * @param conn 连接对象
     */
    @Override
    public void dealUnAuthorized409(HttpURLConnection conn) {
        Logger.error("409 conflict request.");
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", I18NServer.toLocale("http_409_conflict_request"), NotificationType.ERROR));
    }

    /**
     * 处理接口返回401状态码
     *
     * @param conn 连接对象
     */
    @Override
    public void dealUnAuthorized(HttpURLConnection conn) {
        // 左侧树面板还原到登录面板状态,
        LoginUtils.refreshLogin();
        if (!PortingIDEContext.getPortingIDEPluginStatus().equals(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG)) {
            PortingUserInfoContext.clearStatus();
            BufferedReader br = null;
            try {
                String line = null;
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.indexOf("CrowdedOut") > -1) {
                    IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                            I18NServer.toLocale("plugins_porting_login_needLoginTitle"),
                            I18NServer.toLocale("plugins_porting_login_other"),
                            NotificationType.INFORMATION));
                } else {
                    // 自动登录
                    IDENotificationUtil.notificationCommon(
                        new NotificationBean(
                            I18NServer.toLocale("plugins_porting_login_needLoginTitle"),
                            I18NServer.toLocale("plugins_porting_login_needLogin"),
                            NotificationType.INFORMATION));
                }
                // update global IDEPluginStatus
                PortingIDEContext.setPortingIDEPluginStatus(IDEPluginStatus.IDE_STATUS_SERVER_CONFIG);
                LoginUtils.gotoLogin();
            } catch (IOException e) {
                Logger.error("An exception occurred when processing dealUnAuthorized.");
            } finally {
                FileUtil.closeStreams(br, null);
            }
        }
    }

    @Override
    public Optional<String> dealServerUnResponse(RequestDataBean request) {
        if (request.isNeedUploadFile()) {
            return handlerUploadFailed(request);
        }
        if (!request.getUrl().endsWith(PortingServerConfigAction.SERVER_STATUS_URL)) {
            // 左侧树面板刷新到登录面板
            ApplicationManager.getApplication().invokeLater(LeftTreeUtil::refresh2LoginPanel);
        }
        return displayServerAbnormalPanel();
    }

    /**
     * 处理状态码
     *
     * @param responseBean 响应数据
     */
    @Override
    public void handleResponseStatus(ResponseBean responseBean) {
        final HashSet<String> specialCode = new HashSet<String>() {{
            add(RespondStatus.LOGIN_FIRST_SUCCESS.value());
            add(RespondStatus.LOGIN_SUCCESS_PWD_EXPIRED.value());
        }};
        String status = responseBean.getStatus();
        Map<String, Object> response = JsonUtil.getJsonObjectFromJsonStr(responseBean.getResponseJsonStr());
        if (response != null) {
            response.put("realStatus", status);
            responseBean.setRealStatus(status);
        }
        if (status != null && status.length() >= STATUS_MIN_LEN) {
            int index = status.length() - STATUS_MIN_LEN;
            String specialNum = Character.toString(status.charAt(index));
            if (response != null) {
                response.put("status", Integer.parseInt(specialNum));
            }
            if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(specialNum) &&
                !specialCode.contains(status)) {
                responseBean.setStatus(RespondStatus.PROCESS_STATUS_NORMAL.value());
            }
        }
        responseBean.setResponseJsonStr(JsonUtil.getJsonStrFromJsonObj(response));
    }

    /**
     * IP锁定处理
     */
    @Override
    public void handleIPLocked() {
        IDENotificationUtil.notificationCommon(new NotificationBean("",
            I18NServer.toLocale("plugins_porting_ip_locked"), NotificationType.ERROR));
        if (PortingIDEContext.getPortingIDEPluginStatus().equals(IDEPluginStatus.IDE_STATUS_LOGIN)) {
            LoginUtils.clearStatus();
            LoginWrapDialog.closeIntellijSettingsDialog();
        }
    }
}

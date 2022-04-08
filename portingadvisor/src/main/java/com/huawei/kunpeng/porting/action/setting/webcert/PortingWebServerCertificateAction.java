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

package com.huawei.kunpeng.porting.action.setting.webcert;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.bean.PortingWebServerCertificateBean;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.dialog.webservercert.PortingExportWebServerCertDialog;
import com.huawei.kunpeng.porting.ui.dialog.webservercert.PortingImportWebServerCertDialog;
import com.huawei.kunpeng.porting.ui.panel.settings.webservercert.PortingExportWebServerCertificatePanel;
import com.huawei.kunpeng.porting.ui.panel.settings.webservercert.PortingImportWebServerCertPanel;
import com.huawei.kunpeng.porting.ui.panel.settings.webservercert.PortingWebServerCertificatePanel;

import com.intellij.notification.NotificationType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * web服务证书事件处理器
 *
 * @since 2020-10-13
 */
public class PortingWebServerCertificateAction extends IDEPanelBaseAction {
    /**
     * 默认显示名称
     */
    private static final String DEFAULT_FILE_NAME = "cert";

    private static final String CERTIFICATE_WILL_EXPIRE = "0";

    private static final String CERTIFICATE_VALID = "1";

    private static final String CERTIFICATE_EXPIRED = "-1";

    private static final String PLUGINS_PORTING_CERTIFICATE_GENERATION_FILE =
        "plugins_porting_certificate_generation_file";

    private String initCertSet;

    private PortingWebServerCertificatePanel portingWebServerCertificatePanel;

    /**
     * WeakPwdSetPanel
     *
     * @param panel         面板
     * @param actionOperate actionOperate
     * @return result
     */
    public PortingWebServerCertificateBean getCertStatus(
        PortingWebServerCertificatePanel panel, ActionOperate actionOperate) {
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/", HttpMethod.GET.vaLue(), ""));
        return parseWebServerCertificateData(responseBean);
    }

    /**
     * getCertInfo
     */
    public void getCertInfo() {
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/", HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            String certFlag = jsonMessage.get("cert_flag");
            if (CERTIFICATE_VALID.equals(certFlag)) {
                return;
            }
            String certExpiredTip = "";
            if (CERTIFICATE_EXPIRED.equals(certFlag)) {
                certExpiredTip = I18NServer.toLocale("plugins_porting_certificate_expired");
            }
            if (CERTIFICATE_WILL_EXPIRE.equals(certFlag)) {
                certExpiredTip = I18NServer.toLocale("plugins_porting_certificate_will_expire");
                String certExpired = jsonMessage.get("cert_expired").replace('T', ' ');
                certExpiredTip = MessageFormat.format(certExpiredTip, certExpired);
            }
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", certExpiredTip, NotificationType.INFORMATION));
        }
    }

    /**
     * 解析web服务证书数据
     *
     * @param responseBean 服务端反馈的参数。
     * @return web服务证书
     */
    private PortingWebServerCertificateBean parseWebServerCertificateData(ResponseBean responseBean) {
        // 解析web服务证书数据
        if (responseBean != null && responseBean.getData() != null) {
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            String certExpired = jsonMessage.get("cert_expired").replace('T', ' ');
            Map<String, String> certFlag = new HashMap<>();
            certFlag.put(CERTIFICATE_EXPIRED, I18NServer.toLocale("plugins_porting_certificate_failure"));
            certFlag.put(CERTIFICATE_WILL_EXPIRE, I18NServer.toLocale("plugins_porting_certificate_nearFailure"));
            certFlag.put(CERTIFICATE_VALID, I18NServer.toLocale("plugins_porting_certificate_valid"));
            return PortingWebServerCertificateBean.builder()
                .webServerCertExpireTime(certExpired)
                .status(certFlag.get(jsonMessage.get("cert_flag")))
                .build();
        }
        return PortingWebServerCertificateBean.builder().build();
    }

    /**
     * 获取证书到期告警阈值
     *
     * @return 证书到期告警阈值
     */
    public Integer getCertTimeoutConfig() {
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/cert_time/", HttpMethod.GET.vaLue(), ""));
        Integer result = 0;
        if (responseBean != null && responseBean.getData() != null) {
            Map<String, Integer> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            result = jsonMessage.get("cert_time");
        }
        this.initCertSet = result.toString();
        return result;
    }

    /**
     * 获取证书到期告警阈值
     *
     * @param certTimeout 证书到期告警阈值
     */
    public void setCertTimeoutConfig(String certTimeout) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/cert_time/",
            HttpMethod.POST.vaLue(), "");
        Map<String, Integer> paramMap = new HashMap<>();
        paramMap.put("cert_time", Integer.valueOf(certTimeout));
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(paramMap));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean != null && RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            this.initCertSet = certTimeout;
        }
        popupInfo("", responseBean);
        getCertInfo();
    }

    /**
     * 弹出生成CSR文件弹框
     */
    public void createCsrFile() {
        IDEBasePanel exportWebServerCertPanel = new PortingExportWebServerCertificatePanel(null);
        IDEBaseDialog dialog = new PortingExportWebServerCertDialog(
            I18NServer.toLocale(PLUGINS_PORTING_CERTIFICATE_GENERATION_FILE), exportWebServerCertPanel);
        dialog.displayPanel();
    }

    /**
     * 导入证书
     */
    public void importCertFile() {
        IDEBasePanel importWebServerCertPanel = new PortingImportWebServerCertPanel(null);
        IDEBaseDialog dialog = new PortingImportWebServerCertDialog(
            I18NServer.toLocale("plugins_porting_certificate_import_file"), importWebServerCertPanel);
        dialog.displayPanel();
    }

    /**
     * 弹出生成CSR文件弹框
     *
     * @param paramMap 生成CSR文件的参数
     */
    public void exportCsrFile(Map<String, String> paramMap) {
        // 导出数据
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csr", "csr");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File(DEFAULT_FILE_NAME));
        int returnVal = chooser.showSaveDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            int index = path.lastIndexOf(File.separator);
            String toPath = path.substring(0, index);
            String fileName = path.substring(index + 1);
            if (ValidateUtils.isEmptyString(fileName)) {
                fileName = "cert.csr";
            } else {
                fileName = fileName + ".csr";
            }
            RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/csr/",
                HttpMethod.POST.vaLue(), "");
            message.setBodyData(JsonUtil.getJsonStrFromJsonObj(paramMap));
            ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
            this.download(fileName, responseBean, toPath);
        }
    }

    private void download(String fileName, ResponseBean responseBean, String toPath) {
        if (responseBean == null) {
            return;
        }
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            popupInfo(I18NServer.toLocale(PLUGINS_PORTING_CERTIFICATE_GENERATION_FILE), responseBean);
            return;
        }
        Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        String content = jsonMessage.get("content");
        OutputStream output = null;
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(
                new ByteArrayInputStream(content.getBytes(PortingIDEConstant.CHARSET_UTF8)));
            byte[] buffer = new byte[content.length()];
            File des = new File(toPath, fileName);
            boolean isFile = des.createNewFile();
            if (isFile) {
                output = new FileOutputStream(des);
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
            }
            popupInfo(I18NServer.toLocale(PLUGINS_PORTING_CERTIFICATE_GENERATION_FILE), responseBean);
        } catch (FileNotFoundException e) {
            Logger.error("export csr file fail.");
        } catch (IOException e) {
            if (Logger.isErrorEnabled()) {
                Logger.error("export csr file fail.IOException");
            }
        } finally {
            FileUtil.closeStreams(input, null);
            FileUtil.closeStreams(output, null);
        }
    }

    /**
     * 重启服务
     */
    public void restartService() {
        String title = I18NServer.toLocale("plugins_porting_certificate_restart");
        sendRestartMsg(title);
    }

    private void sendRestartMsg(String title) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/nginx/reload/",
            HttpMethod.POST.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        // 正常启动，提示等待信息
        if (responseBean == null || RespondStatus.NGINX_STARTING.value().equals(responseBean.getRealStatus())) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(title, I18NServer.toLocale("plugins_porting_certificate_restart_tip"),
                    NotificationType.INFORMATION));
        }
        // 启动失败时直接返回异常信息,不再提示等待启动
        if (responseBean != null && RespondStatus.CER_NOT_EXIST.value().equals(responseBean.getStatus())) {
            popupInfo("", responseBean);
            return;
        }

        if (!portingWebServerCertificatePanel.updateTable() && responseBean != null &&
            !RespondStatus.NGINX_STARTING.value().equals(responseBean.getRealStatus())) {
            popupInfo(title, responseBean);
        }
    }

    /**
     * 更新秘钥
     */
    public void requestUpdateKey() {
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(2);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String buttonCode = IDEMessageDialogUtil.showDialog(
                new MessageDialogBean(I18NServer.toLocale("update_working_key_info"),
                        I18NServer.toLocale("update_working_key"),
                        buttonNames, 1, IDEMessageDialogUtil.getWarn()));
        if (buttonCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/admin/workkey/",
                            HttpMethod.POST.vaLue(), "");
            ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
            popupInfo(I18NServer.toLocale("plugins_porting_certificate_update"), responseBean);
        }
    }

    private void popupInfo(String title, ResponseBean responseBean) {
        if (responseBean == null) {
            return;
        }
        NotificationType type = NotificationType.INFORMATION;
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            type = NotificationType.ERROR;
        }

        IDENotificationUtil.notificationCommon(
            new NotificationBean(title, CommonUtil.getRspTipInfo(responseBean), type));
    }

    /**
     * getInitCertSet
     *
     * @return 证书到期告警阈值
     */
    public String getInitCertSet() {
        return initCertSet;
    }

    /**
     * 获取Panel
     *
     * @return portingWebServerCertificatePanel
     */
    public PortingWebServerCertificatePanel getWebServerCertificatePanel() {
        return portingWebServerCertificatePanel;
    }

    /**
     * 设置Panel
     *
     * @param portingWebServerCertificatePanel portingWebServerCertificatePanel
     */
    public void setWebServerCertificatePanel(PortingWebServerCertificatePanel portingWebServerCertificatePanel) {
        this.portingWebServerCertificatePanel = portingWebServerCertificatePanel;
    }
}
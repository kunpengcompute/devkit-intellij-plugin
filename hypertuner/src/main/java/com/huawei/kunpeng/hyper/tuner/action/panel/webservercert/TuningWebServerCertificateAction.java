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

package com.huawei.kunpeng.hyper.tuner.action.panel.webservercert;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.TuningWebServerCertificateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.webservercert.TuningExportWebServerCertDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.webservercert.TuningImportWebServerCertDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert.TuningExportWebServerCertificatePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert.TuningImportWebServerCertPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert.TuningWebServerCertificatePanel;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONArray;
import com.intellij.notification.NotificationType;

import org.apache.commons.collections.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * web服务证书事件处理器
 *
 * @since 2020-10-13
 */
public class TuningWebServerCertificateAction extends IDEPanelBaseAction {
    /**
     * 默认显示名称
     */
    private static final String DEFAULT_FILE_NAME = "server";

    private static final String CERTIFICATE_WILL_EXPIRE = "1";

    private static final String CERTIFICATE_WILL_EXPIRE1 = "expire";

    private static final int BUTTON_LENGTH = 2;

    private static final int ARRAY_INDEX = 0;

    private static final String CERTIFICATE_VALID1 = "inactive";

    private static final String CERTIFICATE_VALID = "0";

    private static final String CERTIFICATE_EXPIRED = "2";

    private static final String CERTIFICATE_EXPIRED1 = "expired";

    private String initCertSet;

    private TuningWebServerCertificatePanel tuningWebServerCertificatePanel;

    /**
     * WeakPwdSetPanel
     *
     * @param panel         面板
     * @param actionOperate actionOperate
     * @return result
     */
    public TuningWebServerCertificateBean getCertStatus(
            TuningWebServerCertificatePanel panel, ActionOperate actionOperate) {
        ResponseBean responseBean =
                TuningHttpsServer.INSTANCE.requestData(
                        new RequestDataBean(
                                TuningIDEConstant.TOOL_NAME_TUNING,
                                "user-management/api/v2.2/certificates/",
                                HttpMethod.GET.vaLue(),
                                ""));
        Logger.info("responseBean", responseBean);
        return parseWebServerCertificateData(responseBean);
    }

    /**
     * getCertInfo
     */
    public void getCertInfo() {
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/certificates/",
                        HttpMethod.GET.vaLue(), ""));
        Logger.info("responseBean", responseBean);
        if (responseBean == null) {
            return;
        }
        if (TuningIDEConstant.SUCCESS_CODE.equals(responseBean.getCode())) {
            JSONArray jsonArray = JSONArray.parseArray(responseBean.getData());
            Map<String, String> jsonMessage =
                    JsonUtil.getJsonObjFromJsonStr(jsonArray.getString(ARRAY_INDEX));
            String certExpiredTip = " ";
            String certFlag = jsonMessage.get("certStatus");
            if (CERTIFICATE_VALID.equals(certFlag) || CERTIFICATE_VALID1.equals(certFlag)) {
                return;
            }
            if (CERTIFICATE_EXPIRED.equals(certFlag) || CERTIFICATE_EXPIRED1.equals(certFlag)) {
                certExpiredTip = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_expired");
                IDENotificationUtil.notificationCommon(
                        new NotificationBean("", certExpiredTip, NotificationType.WARNING));
            }
            if (CERTIFICATE_WILL_EXPIRE.equals(certFlag) || CERTIFICATE_WILL_EXPIRE1.equals(certFlag)) {
                certExpiredTip = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_will_expire");
                String certExpired = jsonMessage.get("expireDate").replace('T', ' ');
                certExpiredTip = MessageFormat.format(certExpiredTip, certExpired);
                IDENotificationUtil.notificationCommon(
                        new NotificationBean("", certExpiredTip, NotificationType.WARNING));
            }
        }
    }

    /**
     * 解析web服务证书数据
     *
     * @param responseBean 服务端反馈的参数。
     * @return web服务证书
     */
    private TuningWebServerCertificateBean parseWebServerCertificateData(ResponseBean responseBean) {
        // 解析web服务证书数据
        if (responseBean != null && responseBean.getData() != null) {
            JSONArray jsonArray = JSONArray.parseArray(responseBean.getData());
            if (CollectionUtils.isEmpty(jsonArray)) {
                return new TuningWebServerCertificateBean("", "");
            }
            Map<String, String> jsonMessage =
                    JsonUtil.getJsonObjFromJsonStr(jsonArray.getString(ARRAY_INDEX));
            String certExpired = jsonMessage.get("expireDate").replace('T', ' ');
            Map<String, String> certFlag = new HashMap<String, String>();
            certFlag.put(CERTIFICATE_EXPIRED, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_failure"));
            certFlag.put(
                    CERTIFICATE_WILL_EXPIRE, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_nearFailure"));
            certFlag.put(CERTIFICATE_VALID, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_valid"));
            certFlag.put(CERTIFICATE_EXPIRED1, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_failure"));
            certFlag.put(
                    CERTIFICATE_WILL_EXPIRE1, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_nearFailure"));
            certFlag.put(CERTIFICATE_VALID1, TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_valid"));
            TuningWebServerCertificateBean tuningWebServerCertificateBean =
                    new TuningWebServerCertificateBean(certExpired, certFlag.get(jsonMessage.get("certStatus")));
            return tuningWebServerCertificateBean;
        }
        return new TuningWebServerCertificateBean("", "");
    }

    /**
     * 获取证书到期告警阈值
     *
     * @return 证书到期告警阈值
     */
    public Integer getCertTimeoutConfig() {
        ResponseBean responseBean =
                TuningHttpsServer.INSTANCE.requestData(
                        new RequestDataBean(
                                TuningIDEConstant.TOOL_NAME_TUNING,
                                "user-management/api/v2.2/certificates/",
                                HttpMethod.GET.vaLue(),
                                ""));
        Logger.info("responseBean", responseBean);
        Integer result = 0;
        if (responseBean != null && responseBean.getData() != null) {
            Map<String, Integer> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            result = jsonMessage.get("cert_time");
        }
        this.initCertSet = result.toString();
        return result;
    }

    /**
     * 弹出生成CSR文件弹框
     */
    public void createCsrFile() {
        IDEBasePanel exportWebServerCertPanel = new TuningExportWebServerCertificatePanel(null);
        IDEBaseDialog dialog =
                new TuningExportWebServerCertDialog(
                        I18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file"),
                        exportWebServerCertPanel);
        dialog.displayPanel();
    }

    /**
     * 导入证书
     */
    public void importCertFile() {
        IDEBasePanel importWebServerCertPanel = new TuningImportWebServerCertPanel(null);
        IDEBaseDialog dialog =
                new TuningImportWebServerCertDialog(
                        I18NServer.toLocale("plugins_hyper_tuner_certificate_import_file"), importWebServerCertPanel);
        dialog.displayPanel();
    }

    /**
     * 弹出生成CSR文件弹框
     *
     * @param paramMap 生成CSR文件的参数
     */
    public void exportCsrFile(Map<String, String> paramMap) {
        // 导出数据
        FileOutputStream fileOutputStream = null;
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csr", "csr");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File(DEFAULT_FILE_NAME));
        int returnVal = chooser.showSaveDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            int index = path.lastIndexOf(TuningIDEConstant.WINDOW_PATH_SEPARATOR);
            String toPath = path.substring(0, index);
            String fileName = path.substring(index + 1);
            if (ValidateUtils.isEmptyString(fileName)) {
                fileName = "server.csr";
            } else {
                fileName = fileName + ".csr";
            }
            RequestDataBean message =
                    new RequestDataBean(
                            TuningIDEConstant.TOOL_NAME_TUNING,
                            "user-management/api/v2.2/certificates/",
                            HttpMethod.POST.vaLue(),
                            "");
            message.setBodyData(JsonUtil.getJsonStrFromJsonObj(paramMap));
            ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
            Logger.info("responseBean", responseBean);
            this.download(fileName, responseBean, toPath);
        }
    }

    private void download(String fileName, ResponseBean responseBean, String toPath) {
        if (responseBean == null) {
            return;
        }
        if (!"UserManage.Success".equals(responseBean.getCode())) {
            popupInfo(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file"), responseBean,
                    TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file_success"));
            return;
        }
        Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        String content = jsonMessage.get("certificate");
        OutputStream output = null;
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new ByteArrayInputStream(content.getBytes(IDEConstant.CHARSET_UTF8)));
            byte[] buffer = new byte[content.length()];
            String filePathName = toPath + "/" + fileName;
            File des = new File(filePathName);
            boolean isFile;
            String generationFile = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file");
            boolean isToContinue = ExportToFileUtil.isExistNotToContinue(filePathName, generationFile);
            if (isToContinue) {
                return;
            } else {
                des.delete();
                isFile = des.createNewFile();
            }
            if (isFile) {
                output = new FileOutputStream(des);
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
            }
            popupInfo(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file"), responseBean,
                    TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_generation_file_success"));
        } catch (FileNotFoundException e) {
            Logger.error("export csr file fail.");
        } catch (IOException e) {
            if (Logger.isErrorEnabled()) {
                Logger.error("export csr file fail.IOException");
            }
        } finally {
            FileUtil.closeStreams(output, null);
            FileUtil.closeStreams(input, null);
        }
    }

    /**
     * 重启服务
     */
    public void restartService() {
        String title = TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_restart");
        IDENotificationUtil.notificationCommon(
                new NotificationBean(
                        title,
                        TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_restart_tip"),
                        NotificationType.INFORMATION));
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/certificates/cert_active/",
                        HttpMethod.POST.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        Logger.info("responseBean", responseBean);
        try {
            // 重起nginx服务時，程序休息5s，再更新证书信息
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Logger.error("sleep fail after restart web service.");
            Thread.currentThread().interrupt();
        }
        if (!tuningWebServerCertificatePanel.updateTable()) {
            popupInfo(title, responseBean, "");
        }
    }

    /**
     * 更新秘钥
     */
    public void requestUpdateKey() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/work-keys/",
                        HttpMethod.PUT.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        String tips = TuningI18NServer.toLocale("plugins_hyper_tuner_workkey_Change_Success");
        Logger.info("responseBean", responseBean);
        popupInfo(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_update"), responseBean, tips);
    }

    private void popupInfo(String title, ResponseBean responseBean, String tips) {
        if (responseBean == null) {
            return;
        }
        NotificationType type = NotificationType.INFORMATION;
        if (!responseBean.getCode().contains("Success")) {
            type = NotificationType.ERROR;
        }
        IDENotificationUtil.notificationCommon(new NotificationBean(title, tips, type));
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
     * getWebServerCertificatePanel
     *
     * @return tuningWebServerCertificatePanel
     */
    public TuningWebServerCertificatePanel getWebServerCertificatePanel() {
        return tuningWebServerCertificatePanel;
    }

    /**
     * setWebServerCertificatePanel
     *
     * @param tuningWebServerCertificatePanel tuningWebServerCertificatePanel
     */
    public void setWebServerCertificatePanel(TuningWebServerCertificatePanel tuningWebServerCertificatePanel) {
        this.tuningWebServerCertificatePanel = tuningWebServerCertificatePanel;
    }
}

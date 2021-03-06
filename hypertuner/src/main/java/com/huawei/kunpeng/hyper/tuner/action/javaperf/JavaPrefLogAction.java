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

package com.huawei.kunpeng.hyper.tuner.action.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.javaperf.JavaPerfLogExcelExportUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.JavaPerfOperateLogBean;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.JavaPerfRunLogBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf.RunJavaPerfLogDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.RunJavaPerfLogPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.javaperf.JavaPerfTableRenderer;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.intellij.notification.NotificationType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * javaPerf ???????????????
 *
 * @since 2012-10-12
 */
public class JavaPrefLogAction extends IDEPanelBaseAction {
    /**
     * ??????????????????
     */
    private static final String LOG_NAME = "java-perf-log.zip";
    private static final String OPERA_LOG_NAME = "log.csv";
    /**
     * ???????????????
     */
    private static final String STATUS_SUCCEEDED = "0";

    /**
     * ????????????
     */
    private static final int FONT_SIZE = 12;

    /**
     * ????????????????????????
     *
     * @param timeStampStr double????????????????????????????????? ?????????1626158578.105330000
     * @return ??????????????????????????? yyyy/MM/dd HH:mm:ss
     */
    public static String getTimeFromTimeStampStr(String timeStampStr) {
        double timeStampDouble = Double.parseDouble(timeStampStr);
        timeStampDouble = Math.round(timeStampDouble * 1000);
        long timeStampLong = new Double(timeStampDouble).longValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date(Long.parseLong(String.valueOf(timeStampLong))));
    }

    /**
     * ?????? ???????????? ??????
     *
     * @return ??????????????????
     */
    public List<JavaPerfOperateLogBean> getOperateLogList() {
        List<JavaPerfOperateLogBean> sysPerfOperateLogBeans = new ArrayList<>();
        RequestDataBean message;
        message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/audits?page=0&size=200", HttpMethod.GET.vaLue(),
                "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return sysPerfOperateLogBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object membersObj = jsonMessage.get("members");
        if (membersObj instanceof JSONArray) {
            JSONArray logArr = (JSONArray) membersObj;
            JavaPerfOperateLogBean sysPerfOperateLogBean;
            for (int mun = 0; mun < logArr.size(); mun++) {
                sysPerfOperateLogBean = logArr.getObject(mun, JavaPerfOperateLogBean.class);
                sysPerfOperateLogBeans.add(sysPerfOperateLogBean);
            }
        }
        return sysPerfOperateLogBeans;
    }

    /**
     * ??????????????????
     *
     * @param pathLog ????????????
     */
    public void export(String pathLog) {
        List<JavaPerfOperateLogBean> list = getOperateLogList();
        String path = pathLog + File.separator + OPERA_LOG_NAME;
        if (list != null && list.size() > 0) {
            for (JavaPerfOperateLogBean bean : list) {
                String timeFormat = getTimeFromTimeStampStr(bean.getCreateTime());
                bean.setCreateTime(timeFormat);
                String statusFormat = JavaPerfTableRenderer.getOperateLogTableStatusText(bean.getSucceed());
                bean.setSucceed(statusFormat);
            }
            String sheetTitle = "log";
            String[] title = new String[]{
                    TuningLogManageConstant.LOG_USERNAME, // ????????????
                    TuningLogManageConstant.LOG_EVENT, // ????????????
                    TuningLogManageConstant.LOG_RESULT, // ????????????
                    TuningLogManageConstant.LOG_IP, // ????????????IP
                    TuningLogManageConstant.LOG_TIME, // ????????????
                    TuningLogManageConstant.LOG_DETAIL // ????????????
            };
            String[] properties =
                    new String[]{"username", "operation", "resource", "clientIp", "succeed", "createTime"};
            JavaPerfLogExcelExportUtil excelExport2 = new JavaPerfLogExcelExportUtil();
            excelExport2.setData(list);
            excelExport2.setHeardKey(properties);
            excelExport2.setFontSize(FONT_SIZE);
            excelExport2.setSheetName(sheetTitle);
            excelExport2.setHeardList(title);
            try {
                excelExport2.exportExport(path);
                sendExportSuccessMessage("operate", pathLog);
            } catch (IOException e) {
                Logger.error("export file error!!");
            }
        }
    }

    /**
     * ??????????????????????????? ?????????????????????????????????????????????
     *
     * @param logType ???????????????????????????/????????????
     * @param pathLog ?????????????????????
     */
    private void sendExportSuccessMessage(String logType, String pathLog) {
        String logTypeI18N = "";
        if ("operate".equalsIgnoreCase(logType)) {
            logTypeI18N = I18NServer.toLocale("plugins_common_title_opt_log");
        } else if ("run".equalsIgnoreCase(logType)) {
            logTypeI18N = I18NServer.toLocale("plugins_common_title_run_log");
        } else {
            logTypeI18N = "";
        }
        NotificationBean notificationBean =
                new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_button_download_log"),
                        logTypeI18N
                                + I18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                + IDEConstant.HTML_HEAD + pathLog + IDEConstant.HTML_FOOT,
                        NotificationType.INFORMATION);

        // ??????
        IDENotificationUtil.notificationForHyperlink(notificationBean, new ActionOperate() {
            @Override
            public void actionOperate(Object data) {
                CommonUtil.showFileDirOnDesktop(pathLog);
            }
        });
    }

    /**
     * ?????? ?????????????????? ????????????
     *
     * @return ????????????
     */
    public List<JavaPerfRunLogBean> getRunLogList() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/logging/files", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<JavaPerfRunLogBean> runLogBeans = new ArrayList<>();
        if (responseBean == null) {
            return runLogBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object membersObj = jsonMessage.get("members");
        if (membersObj instanceof JSONArray) {
            JSONArray logArr = (JSONArray) membersObj;
            JavaPerfRunLogBean runLogItem = new JavaPerfRunLogBean();
            for (int i = 0; i < logArr.size(); i++) {
                runLogItem = logArr.getObject(i, JavaPerfRunLogBean.class);
                runLogBeans.add(runLogItem);
            }
        }
        return runLogBeans;
    }


    /**
     * ??????????????????????????????????????????????????????????????????
     */
    public void showLogListDialog() {
        RunJavaPerfLogDialog runLogDialog = new RunJavaPerfLogDialog("",
                new RunJavaPerfLogPanel("", "", true));
        runLogDialog.displayPanel();
    }

    /**
     * ??????????????????
     *
     * @param path     ?????????????????????
     * @param fileName ?????????????????????
     */
    public void downloadRunLog(String path, String fileName) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/logging/files/download?fileName=" + fileName, HttpMethod.GET.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadFileName(fileName);
        message.setDownloadPtah(path);
        message.setUrlParams(path + File.separator + fileName);
        File[] files = {new File(fileName)};
        message.setFile(files);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_button_download_log"),
                            I18NServer.toLocale("plugins_common_title_run_log")
                                    + I18NServer.toLocale("plugins_hyper_tuner_download_report_fail"),
                            NotificationType.ERROR));
            return;
        }
        sendExportSuccessMessage("run", path );
    }
}
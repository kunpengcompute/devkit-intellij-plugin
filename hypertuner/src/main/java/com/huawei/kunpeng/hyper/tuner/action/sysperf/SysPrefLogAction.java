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

package com.huawei.kunpeng.hyper.tuner.action.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.SysPerfLogExcelExportUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SysPerfOperateLogBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.RunSysPerfLogDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.RunSysPerfLogPanel;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ????????????????????????
 *
 * @since 2012-10-12
 */
public class SysPrefLogAction extends IDEPanelBaseAction {
    /**
     * ??????????????????
     */
    private static final String LOG_NAME = "sys-perf-log.zip";
    /**
     * ???????????????
     */
    private static final String STATUS_SUCCEEDED = "0";

    /**
     * ????????????
     */
    private static final int FONT_SIZE = 12;

    /**
     * ?????? ???????????? ??????
     *
     * @return ??????????????????
     */
    public List<SysPerfOperateLogBean> getSysPerfLogList() {
        List<SysPerfOperateLogBean> sysPerfOperateLogBeans = new ArrayList<>();
        RequestDataBean message;
        message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/sys-perf/api/v2.2/operation-logs/?page=1&per-page=200", HttpMethod.GET.vaLue(),
                "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return sysPerfOperateLogBeans;
        }
        Map<String, JSONArray> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object logs = jsonMessage.get("logs");
        if (logs instanceof JSONArray) {
            JSONArray logArr = (JSONArray) logs;
            SysPerfOperateLogBean sysPerfOperateLogBean;
            for (int mun = 0; mun < logArr.size(); mun++) {
                sysPerfOperateLogBean = logArr.getObject(mun, SysPerfOperateLogBean.class);
                sysPerfOperateLogBeans.add(sysPerfOperateLogBean);
            }
        }
        return sysPerfOperateLogBeans;
    }

    /**
     * ?????? ?????????????????? ????????????
     *
     * @param type ???????????????????????? {  Web_Server????????????("")   ????????????????????????("analyzer")}
     * @return ????????????
     */
    public List<SysPerfOperateLogBean> getRunlogList(String type) {
        String url = "";
        if ("analyzer".equals(type) || "collector".equals(type)) {
            url = "sys-perf/api/v2.2/run-logs/" + type + "/";
        } else {
            url = "sys-perf/api/v2.2/run-logs/";
        }
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<SysPerfOperateLogBean> logBeans = new ArrayList<>();
        if (responseBean == null) {
            return logBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object logsObj = jsonMessage.get("logs");
        int count = 0;
        if (jsonMessage.get("totalCounts") instanceof Integer) {
            count = (Integer) jsonMessage.get("totalCounts");
        }
        if (count > 0) {
            for (int mun = 0; mun < count; mun++) {
                if (logsObj instanceof JSONObject) {
                    JSONObject logJsonObj = (JSONObject) logsObj;
                    getBeansFromJsonObj(mun, logJsonObj, logBeans);
                }
                if (logsObj instanceof JSONArray) {
                    JSONArray logArr = (JSONArray) logsObj;
                    JSONObject logItem = logArr.getJSONObject(mun);
                    getBeansFromJsonObj(mun, logItem, logBeans);
                }
            }
        }
        return logBeans;
    }

    private void getBeansFromJsonObj(int i, JSONObject logJsonObj, List<SysPerfOperateLogBean> logBeans) {
        SysPerfOperateLogBean sysPerfOperateLogBean = new SysPerfOperateLogBean();
        JSONArray fileName = logJsonObj.getJSONArray("file_name");
        sysPerfOperateLogBean.setFilename(fileName.getString(0));
        JSONArray fileSize = logJsonObj.getJSONArray("file_size");
        sysPerfOperateLogBean.setFilesize(fileSize.getString(0));
        logBeans.add(sysPerfOperateLogBean);
    }

    /**
     * ??????????????????
     *
     * @param pathLog ????????????
     */
    public void export(String pathLog) {
        List<SysPerfOperateLogBean> list = getSysPerfLogList();
        String path = pathLog + "/log.csv";
        if (list != null && list.size() > 0) {
            String sheetTitle = "log";
            String[] title = new String[]{
                    TuningLogManageConstant.LOG_USERNAME,
                    TuningLogManageConstant.LOG_EVENT,
                    TuningLogManageConstant.LOG_RESULT,
                    TuningLogManageConstant.LOG_TIME,
                    TuningLogManageConstant.LOG_DETAIL
            };
            String[] properties = new String[]{"username", "module_type", "result", "ipaddr", "time", "information"};
            SysPerfLogExcelExportUtil excelExport2 = new SysPerfLogExcelExportUtil();
            excelExport2.setData(list);
            excelExport2.setHeardKey(properties);
            excelExport2.setFontSize(FONT_SIZE);
            excelExport2.setSheetName(sheetTitle);
            excelExport2.setHeardList(title);
            try {
                excelExport2.exportExport(path);
                sendExportSuccessMessage(pathLog);
            } catch (IOException e) {
                Logger.error("export file error!!");
            }
        }
    }

    /**
     * ??????????????????????????? ?????????????????????????????????????????????
     *
     * @param pathLog ?????????????????????
     */
    private void sendExportSuccessMessage(String pathLog) {
        NotificationBean notificationBean =
                new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_button_download_log"),
                        I18NServer.toLocale("plugins_common_title_opt_log")
                                + " "
                                + I18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                + "<html> <a href=\"#\">" + pathLog
                                + "</a></html>",
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
     * ??????????????????????????????????????????????????????????????????
     *
     * @param type ?????????????????????
     */
    public void showLogListDialog(String type) {
        RunSysPerfLogDialog runLogDialog = new RunSysPerfLogDialog("",
                new RunSysPerfLogPanel("", "", true, type));
        runLogDialog.displayPanel();
    }

    /**
     * ??????????????????
     *
     * @param path     ????????????
     * @param fileName ??????????????????
     */
    public void downloadSelectLog(String path, String fileName) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/run-logs/download/?log-name=" + fileName, HttpMethod.GET.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(true);
        message.setUrlParams(path + File.separator + fileName);
        message.setFile(new File[]{new File(LOG_NAME)});
        TuningHttpsServer.INSTANCE.requestData(message);
        IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_hyper_tuner_button_download_log"),
                        I18NServer.toLocale("plugins_common_title_run_log") + " "
                                + I18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                + message.getUrlParams(),
                        NotificationType.INFORMATION));
    }

}
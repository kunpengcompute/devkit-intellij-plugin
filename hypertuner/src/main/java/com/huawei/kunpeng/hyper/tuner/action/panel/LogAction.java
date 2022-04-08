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

package com.huawei.kunpeng.hyper.tuner.action.panel;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.OperateLogExcelExportUtil;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
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
 * 日志管理
 *
 * @since 2020-12-03
 */
public class LogAction extends IDEPanelBaseAction {
    /**
     * 日志文件名。
     */
    private static final String LOG_NAME = "log.zip";

    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 12;

    /**
     * 查询操作日志
     *
     * @return 日志列表
     */
    public List<OperateLogBean> getOperalogList() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/operation-logs/",
                        HttpMethod.GET.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<OperateLogBean> logBeans = new ArrayList<>();
        if (responseBean == null) {
            return logBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object loglist = jsonMessage.get("logs");
        if (loglist instanceof JSONArray) {
            JSONArray usersJson = (JSONArray) loglist;
            OperateLogBean operatLogBean;
            for (int mun = 0; mun < usersJson.size(); mun++) {
                operatLogBean = usersJson.getObject(mun, OperateLogBean.class);
                logBeans.add(operatLogBean);
            }
        }
        return logBeans;
    }

    /**
     * 查询操作日志
     *
     * @return 日志列表
     */
    public List<OperateLogBean> getRunlogList() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/run-logs/",
                        HttpMethod.GET.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<OperateLogBean> logBeans = new ArrayList<>();
        if (responseBean == null) {
            return logBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object loglist = jsonMessage.get("logs");
        int count = 0;
        if (jsonMessage.get("totalCounts") instanceof Integer) {
            count = (Integer) jsonMessage.get("totalCounts");
        }
        if (count > 0) {
            for (int mun = 0; mun < count; mun++) {
                OperateLogBean operatLogBean = new OperateLogBean();
                JSONObject usersJson = (JSONObject) loglist;

                JSONArray fileName = new JSONArray();
                if (usersJson.get("file_name") instanceof JSONArray) {
                    fileName = (JSONArray) usersJson.get("file_name");
                }
                operatLogBean.setFilename(fileName.getString(mun));
                JSONArray fileSize = new JSONArray();
                if (usersJson.get("file_size") instanceof JSONArray) {
                    fileSize = (JSONArray) usersJson.get("file_size");
                }
                operatLogBean.setFilesize(fileSize.getString(mun));
                logBeans.add(operatLogBean);
            }
        }
        return logBeans;
    }

    /**
     * 操作日志导出
     *
     * @param pathLog 地址路径
     */
    public void export(String pathLog) {
        List<OperateLogBean> list = getOperalogList();
        String path = pathLog + "/log.csv";
        if (list != null && list.size() > 0) {
            String sheetTitle = "log";
            String[] title = new String[]{"操作用户", "操作名称", "操作结果", "操作主机IP", "操作时间", "操作详情"};
            String[] properties = new String[]{"username", "module_type", "result", "ipaddr", "time", "information"};
            OperateLogExcelExportUtil excelExport1 = new OperateLogExcelExportUtil();
            excelExport1.setData(list);
            excelExport1.setHeardKey(properties);
            excelExport1.setFontSize(FONT_SIZE);
            excelExport1.setSheetName(sheetTitle);
            excelExport1.setHeardList(title);
            try {
                excelExport1.exportExport(path);
                sendExportSuccessMessage(pathLog);
            } catch (IOException e) {
                Logger.error("export file error!!");
            }
        }
    }

    /**
     * 推送导出陈宫消息、 并增加跳转到日志下载目录功能。
     *
     * @param pathLog 下载日志路径。
     */
    private void sendExportSuccessMessage(String pathLog) {
        NotificationBean notificationBean =
                new NotificationBean(
                        TuningI18NServer.toLocale("plugins_hyper_tuner_button_download_log"),
                        TuningI18NServer.toLocale("plugins_common_title_opt_log")
                                + " "
                                + TuningI18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                + "<html> <a href=\"#\">"
                                + pathLog
                                + "</a></html>",
                        NotificationType.INFORMATION);
        /**
         * 通知
         */
        IDENotificationUtil.notificationForHyperlink(
                notificationBean,
                new ActionOperate() {
                    @Override
                    public void actionOperate(Object data) {
                        CommonUtil.showFileDirOnDesktop(pathLog);
                    }
                });
    }

    /**
     * 下载运行日志
     *
     * @param path     地址路径
     * @param fileName 日志文件名称
     */
    public void downloadSelectLog(String path, String fileName) {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/run-logs/download/?log-name=" + fileName,
                        HttpMethod.GET.vaLue(),
                        "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(true);
        message.setUrlParams(path + File.separator + fileName);
        message.setFile(new File[]{new File(fileName)});
        TuningHttpsServer.INSTANCE.requestData(message);
        IDENotificationUtil.notificationCommon(
                new NotificationBean(
                        TuningI18NServer.toLocale("plugins_hyper_tuner_button_download_log"),
                        TuningI18NServer.toLocale("plugins_common_title_run_log")
                                + " "
                                + TuningI18NServer.toLocale("plugins_hyper_tuner_download_report_success")
                                + path + fileName,
                        NotificationType.INFORMATION));
    }

    /**
     * 获取用户系统配置
     *
     * @return obj
     */
    public JSONObject userConfig() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/config/userconf/",
                        HttpMethod.GET.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        JSONObject obj = new JSONObject();
        if (responseBean == null) {
            return obj;
        }
        obj = JSONObject.parseObject(responseBean.getData());
        return obj;
    }

    /**
     * 查询运行日志级别
     *
     * @return 日志级别
     */
    public String loglevel() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/run-logs/info/",
                        HttpMethod.GET.vaLue(),
                        "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        String level = null;
        if (responseBean == null) {
            return level;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (jsonMessage.get("logLevel") instanceof String) {
            level = (String) jsonMessage.get("logLevel");
        }
        return level;
    }

    /**
     * 修改运行日志级别
     *
     * @param level         修改运行日志级别
     * @param actionOperate 回调
     */
    public void changloglevel(String level, ActionOperate actionOperate) {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/run-logs/update/",
                        HttpMethod.POST.vaLue(),
                        "");
        JSONObject obj = new JSONObject();
        obj.put("logLevel", level);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.error("responseBean is null");
            return;
        }
        actionOperate.actionOperate(responseBean); // 做回调
    }

    /**
     * 修改用户系统设置
     *
     * @param obj           入参
     * @param actionOperate 回调
     */
    public void changUserConfig(JSONObject obj, ActionOperate actionOperate) {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/config/userconf/",
                        HttpMethod.PUT.vaLue(),
                        "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean resBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (resBean == null) {
            Logger.error("responseBean is null");
            return;
        }
        actionOperate.actionOperate(resBean);
    }
}

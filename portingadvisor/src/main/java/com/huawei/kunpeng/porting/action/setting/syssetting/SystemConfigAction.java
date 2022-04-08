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

package com.huawei.kunpeng.porting.action.setting.syssetting;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.SETTINGS_TASK_CANCELED_FAILED;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.SETTINGS_TASK_CANCELED_SUCCESS;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


/**
 * 系统配置管理
 *
 * @since 2020/12/03
 */
public class SystemConfigAction extends IDEPanelBaseAction {
    /**
     * 创建任务URL。
     */
    private static final String CREATE_LOG_URL = "/portadv/runlog/create_log/";

    /**
     * 查询压缩任务状态URL。
     */
    private static final String ZIP_LOG_URL = "/portadv/runlog/zip_log/";

    /**
     * 下载URL。
     */
    private static final String DOWNLOAD_URL = "/portadv/download/";

    /**
     * 日志文件夹。
     */
    private static final String LOG_NAME_PATH = "/log.zip";

    /**
     * 日志文件名。
     */
    private static final String LOG_NAME = "log.zip";

    /**
     * 日志所在目录。
     */
    private static final String LOG_DIR = "downloadlog";

    /**
     * 任务ID参数。
     */
    private static final String TASK_ID_PARAMS = "task_id";

    /**
     * 用户本地日志下载路径参数key。
     */
    private static final String USER_LOCAL_DIR_PARAMS = "download_path";

    /**
     * 下载文件所在目录参数。
     */
    private static final String SUB_PATH_PARAMS = "sub_path";

    /**
     * 下载文件参数。
     */
    private static final String FILE_PATH_PARAMS = "file_path";

    /**
     * 成功状态。
     */
    private static final String STATUS_SUCCEEDED = "0";

    private static final String HREF_BEFORE = "<html> <a href=\"#\">";

    private static final String HREF_END = "</a></html>";

    /**
     * CSV文件换行
     */
    private static final String NEW_LINE_SEPARATOR = "\n";

    /**
     * 操作日志csv
     */
    private static final String LOG_NAME_CSV = "log.csv";
    private static final String PLUGINS_PORTING_BUTTON_DOWNLOAD_LOG = "plugins_porting_button_download_log";
    private static final String PLUGINS_PORTING_DOWNLOAD_REPORT_SUCCESS = "plugins_porting_download_report_success";
    private static final String LEVEL = "level";
    private static final String MAX_ONLINE_USERS = "max_online_users";

    private static CSVPrinter buildCSVPrinter(List<OperateLogBean> list, String pathLog) throws IOException {
        FileOutputStream fos = new FileOutputStream(pathLog + PortingIDEConstant.PATH_SEPARATOR + LOG_NAME_CSV);
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
        boolean isEn = "File".equals(ActionManager.getInstance().getAction("FileMenu").getTemplateText());
        if (isEn) {
            csvPrinter.printRecord(
                Stream.of("User Name", "Event", "Result", "Time", "Details").collect(Collectors.toList()));
        } else {
            csvPrinter.printRecord(
                Stream.of("操作用户", "操作名称", "操作结果", "操作时间", "操作详情").collect(Collectors.toList()));
        }

        if (!list.isEmpty()) {
            for (OperateLogBean operateLogBean : list) {
                csvPrinter.printRecord(operateLogBean.getUsername(), operateLogBean.getEvent(),
                    operateLogBean.getResult(), operateLogBean.getTime() + "\t", operateLogBean.getDetail());
            }
        }
        return csvPrinter;
    }

    /**
     * 查询操作日志
     *
     * @param isDownLoad true需要记录下载操作
     * @return 日志列表
     */
    public List<OperateLogBean> getOperateLogList(String isDownLoad) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/log/?download=" + isDownLoad, HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        List<OperateLogBean> logBeans = new ArrayList<>();
        if (responseBean == null) {
            return logBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object loglist = jsonMessage.get("loglist");
        if (loglist instanceof JSONArray) {
            JSONArray usersJson = (JSONArray) loglist;
            OperateLogBean operatLogBean;
            for (int i = 0; i < usersJson.size(); i++) {
                operatLogBean = usersJson.getObject(i, OperateLogBean.class);
                logBeans.add(operatLogBean);
            }
        }
        return logBeans;
    }

    /**
     * 导出csv格式日志文件
     *
     * @param pathLog pathLog
     * @throws IOException 抛出异常信息
     */
    public void exportCsvFile(String pathLog) throws IOException {
        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = buildCSVPrinter(getOperateLogList("true"), pathLog);
        } catch (IOException e) {
            Logger.error("exportCsvFile IOException");
        } finally {
            if (csvPrinter == null) {
                Logger.error("");
            } else {
                csvPrinter.close();
            }
        }
        sendExportSuccessMessage(pathLog);
    }

    /**
     * 推送导出陈宫消息、 并增加跳转到日志下载目录功能。
     *
     * @param pathLog 下载日志路径。
     */
    private void sendExportSuccessMessage(String pathLog) {
        NotificationBean notificationBean
            = new NotificationBean(I18NServer.toLocale(PLUGINS_PORTING_BUTTON_DOWNLOAD_LOG),
            I18NServer.toLocale("plugins_common_title_opt_log") + " " + I18NServer.toLocale(
                PLUGINS_PORTING_DOWNLOAD_REPORT_SUCCESS) + HREF_BEFORE + pathLog + HREF_END,
            NotificationType.INFORMATION);
        IDENotificationUtil.notificationForHyperlink(notificationBean,
            data -> CommonUtil.showFileDirOnDesktop(pathLog));
    }

    /**
     * 下载运行日志
     *
     * @param path 地址路径
     */

    public void downloadSelectLog(String path) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/runlog/download_all_logs/", HttpMethod.GET.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(true);
        message.setUrlParams(path + LOG_NAME_PATH);
        message.setFile(new File[] {new File(LOG_NAME)});
        PortingHttpsServer.INSTANCE.requestData(message);
        IDENotificationUtil.notificationCommon(
            new NotificationBean(I18NServer.toLocale(PLUGINS_PORTING_BUTTON_DOWNLOAD_LOG),
                I18NServer.toLocale("plugins_common_title_run_log") + " " + I18NServer.toLocale(
                    PLUGINS_PORTING_DOWNLOAD_REPORT_SUCCESS)
                    + message.getUrlParams(), NotificationType.INFORMATION));
    }

    /**
     * 向后端发起创建任务请求。
     *
     * @param localDir 本地路径
     * @return 返回创建结果消息。
     */
    public String createCompressesTask(String localDir) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            CREATE_LOG_URL, HttpMethod.POST.vaLue(), "");
        if (ValidateUtils.isNotEmptyString(localDir)) {
            Map<String, String> map = new HashMap<>();
            map.put(USER_LOCAL_DIR_PARAMS, localDir);
            message.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));
        }
        message.setCharset(PortingIDEConstant.CHARSET_UTF8);
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        String taskName = "";
        if (responseBean != null) {
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (jsonMessage != null) {
                taskName = jsonMessage.get(TASK_ID_PARAMS);
            }
            // 创建失败提示。
            if (!STATUS_SUCCEEDED.equals(responseBean.getStatus())) {
                handlerResult(responseBean, I18NServer.toLocale(PLUGINS_PORTING_BUTTON_DOWNLOAD_LOG));
            }
        }
        return taskName;
    }

    /**
     * 查询当前用户是否有压缩任务：
     *
     * @return 返回任务名称或者任务状态
     */
    public ResponseBean selectCompressesTask() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            ZIP_LOG_URL, HttpMethod.GET.vaLue(), "");
        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 查询压缩任务状态：
     *
     * @param taskName 任务名称
     * @return 返回任务压缩状态
     */
    public ResponseBean selectCompressesTaskStatus(String taskName) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            ZIP_LOG_URL + "?" + TASK_ID_PARAMS + "=" + taskName, HttpMethod.GET.vaLue(), "");
        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 删除压缩任务。
     *
     * @param taskName 压缩任务
     */
    public void deleteTask(String taskName) {
        if (ValidateUtils.isEmptyString(taskName)) {
            Logger.info("taskName is null when Delete log Task!");
            return;
        }
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            ZIP_LOG_URL + "?" + TASK_ID_PARAMS + "=" + taskName, HttpMethod.DELETE.vaLue(), "");
        ResponseBean result = PortingHttpsServer.INSTANCE.requestData(message);

        if (result == null) {
            Logger.info("ResponseBean is null when Delete log Task!");
            return;
        }
        String resultStr = SETTINGS_TASK_CANCELED_SUCCESS;
        NotificationType messageType = NotificationType.INFORMATION;
        if (!STATUS_SUCCEEDED.equals(result.getStatus())) {
            messageType = NotificationType.ERROR;
            resultStr = SETTINGS_TASK_CANCELED_FAILED;
        }
        // 消息对话框无返回, 仅做通知作用
        NotificationBean notificationBean = new NotificationBean(
            I18NServer.toLocale(PLUGINS_PORTING_BUTTON_DOWNLOAD_LOG), resultStr, messageType);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    /**
     * 按照新接口下载日志
     *
     * @param path 下载日志的本地路径
     */
    public void downloadRunLogByNewWays(String path) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            DOWNLOAD_URL, HttpMethod.POST.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadPtah(path);
        message.setDownloadFileName(LOG_NAME);
        Map<String, String> map = new HashMap<>();
        map.put(SUB_PATH_PARAMS, LOG_DIR);
        map.put(FILE_PATH_PARAMS, LOG_NAME);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));
        PortingHttpsServer.INSTANCE.requestData(message);
        doSuccess(path);
    }

    /**
     * 下载成功处理
     *
     * @param path 下载路径。
     */
    private void doSuccess(String path) {
        Project project = CommonUtil.getDefaultProject();
        NotificationBean notificationBean = new NotificationBean(
            I18NServer.toLocale(PLUGINS_PORTING_BUTTON_DOWNLOAD_LOG),
            I18NServer.toLocale("plugins_common_title_run_log") + " "
                + I18NServer.toLocale(PLUGINS_PORTING_DOWNLOAD_REPORT_SUCCESS)
                + HREF_BEFORE + path + HREF_END,
            NotificationType.INFORMATION);
        notificationBean.setProject(project);
        IDENotificationUtil.notificationForHyperlink(notificationBean, data -> CommonUtil.showFileDirOnDesktop(path));
    }

    /**
     * 处理返回结果并弹出通知
     *
     * @param result 查询结果
     * @param title  通知标题
     */
    private void handlerResult(ResponseBean result, String title) {
        String resultStr = I18NServer.respToLocale(result);
        CommonUtil.getRspTipInfo(result);
        NotificationType messageType = NotificationType.INFORMATION;
        if (!STATUS_SUCCEEDED.equals(result.getStatus())) {
            messageType = NotificationType.ERROR;
        }
        // 消息对话框无返回, 仅做通知作用
        NotificationBean notificationBean = new NotificationBean(title, resultStr, messageType);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    /**
     * 查询运行日志级别
     *
     * @return 日志级别
     */

    public String loglevel() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/users/" + PortingUserInfoContext.getInstance().getLoginId() + "/loglevel/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        String level = null;
        if (responseBean == null) {
            return "";
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (jsonMessage.get(LEVEL) instanceof String) {
            level = (String) jsonMessage.get(LEVEL);
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
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/users/" + PortingUserInfoContext.getInstance().getLoginId() + "/loglevel/", HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put(LEVEL, level);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);

        // 做回调
        actionOperate.actionOperate(CommonUtil.getRspTipInfo(responseBean));
    }

    /**
     * 查询最大用户数
     *
     * @return 查询最大用户数
     */

    public int userLimit() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/loginlimit/",
            HttpMethod.GET.vaLue(), "");

        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        int online = 0;
        if (responseBean != null) {
            Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (jsonMessage.get(MAX_ONLINE_USERS) instanceof Integer) {
                online = (Integer) jsonMessage.get(MAX_ONLINE_USERS);
            }
        }
        return online;
    }

    /**
     * 修改最大用户数
     *
     * @param maxonline     修改最大用户数
     * @param actionOperate 回调
     */

    public void changUserLimit(Integer maxonline, ActionOperate actionOperate) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/loginlimit/",
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put(MAX_ONLINE_USERS, maxonline);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.error("responseBean is null");
            return;
        }
        // 做回调
        actionOperate.actionOperate(responseBean);
    }

    /**
     * 查询系统超时时间
     *
     * @return 查询系统超时时间
     */

    public int userTimeOut() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/admin/timeout/",
            HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        int timeout = 0;
        if (responseBean != null) {
            Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (jsonMessage.get("timeout_configuration") instanceof Integer) {
                timeout = (Integer) jsonMessage.get("timeout_configuration");
            }
        }
        return timeout;
    }

    /**
     * 修改系统超时时间
     *
     * @param configuration 修改系统超时时间
     * @param actionOperate 回调
     */

    public void changUserTimeOut(Integer configuration, ActionOperate actionOperate) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/admin/timeout/",
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("new_timeout_configuration", configuration);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);

        // 做回调
        actionOperate.actionOperate(responseBean);
    }

    /**
     * 获取当前系统配置证书吊销列表配置
     *
     * @return isConfigCRL
     */
    public boolean getCurCRLConfig() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/crl/",
            HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);

        if (responseBean == null) {
            Logger.error("Response is null");
            return false;
        }

        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (!(jsonMessage.get("crl_configuration") instanceof Boolean)) {
            Logger.error("Response data error.");
        }
        return (Boolean) jsonMessage.get("crl_configuration");
    }

    /**
     * 更改当前证书吊销列表配置
     *
     * @param configNum     configNum
     * @param actionOperate actionOperate
     */
    public void updateCRLConfig(int configNum, ActionOperate actionOperate) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/cert/crl/",
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("new_crl_configuration", configNum);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        actionOperate.actionOperate(responseBean);
    }
}

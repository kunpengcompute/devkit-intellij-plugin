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

package com.huawei.kunpeng.porting.process;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * The class MigrationProcess: Handle porting migration process
 *
 * @since v1.0
 */
public class MigrationProcess extends IDEBaseTask {
    /**
     * ????????????????????????id
     */
    private static final String TASK_ID = "id";

    /**
     * ??????????????????
     */
    private static final String RUNNING_STATUS = "runningstatus";

    /**
     * ???????????????
     */
    private static final String PROGRESS_FAKE = "progress_fake";

    /**
     * intellij??????
     */
    private static final String IDEA_NOTIFY = "intelliJNotify";

    /**
     * ????????????
     */
    private static final String TYPE = "type";

    /**
     * url??????
     */
    private static final String URL_REQ = "url";

    /**
     * ?????????????????????url
     */
    private static final String TASK_ID_STR_CHECK = "&task_id=";

    /**
     * ???????????????
     */
    private static final int MIGRATION_STEP_VALID_START = 0;

    /**
     * ????????????
     */
    private static final int BUTTON_NUM = 2;

    private static final String SOFTWARE_MIGRATION_TITLE =
            I18NServer.toLocale("plugins_porting_software_migration_title");

    /**
     * ??????ID
     */
    private String taskId;

    /**
     * url??????
     */
    private String reqOfURL;

    /**
     * ?????????????????????
     */
    private String indicatorMessage;

    /**
     * ?????????????????????
     */
    private String solutionName;

    /**
     * ??????????????????
     */
    private MessageBean messageBean;

    /**
     * ??????????????????
     */
    private boolean migrationCenterFlag = false;

    private final String solutionXmlKey;

    /**
     * ????????????????????????????????????????????????ID
     *
     * @param taskId   ??????ID
     */
    public MigrationProcess(String taskId) {
        this.taskId = taskId;
        solutionXmlKey = "solution_xml";
    }

    /**
     * ????????????ID
     *
     * @param taskId ??????ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * ????????????ID
     *
     * @return String
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * ???????????????
     *
     * @return ?????????
     */
    public MessageBean getMessageBean() {
        return messageBean;
    }

    /**
     * ???????????????
     *
     * @param messageBean ?????????
     */
    public void setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
    }

    /**
     * ????????????????????????????????????
     *
     * @param indicator ???????????????
     */
    @Override
    protected void runTask(ProgressIndicator indicator) {
        // ????????????
        int processCurrentValue = queryMigrationProgress();
        try {
            while (processCurrentValue < 100) {
                if (indicator.isCanceled() ||
                    processCurrentValue == Integer.valueOf(RespondStatus.PROCESS_STATUS_ERROR.value())) {
                    break;
                }
                indicator.checkCanceled();
                indicator.setFraction(new BigDecimal(processCurrentValue).divide(new BigDecimal(100)).doubleValue());
                indicator.setText(indicatorMessage);
                TimeUnit.MILLISECONDS.sleep(750);
                processCurrentValue = queryMigrationProgress();
            }
        } catch (InterruptedException e) {
            Logger.error("Get migration process failed. exception : InterruptedException");
        }
    }

    /**
     * ????????????
     *
     * @param indicator ???????????????
     */
    @Override
    protected void cancel(ProgressIndicator indicator) {
        // ????????????
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(BUTTON_NUM);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        // ??????????????????????????????
        String exitCode = IDEMessageDialogUtil.showDialog(new MessageDialogBean(
            I18NServer.toLocale("plugins_porting_migration_close_task_confirm_tip"),
                SOFTWARE_MIGRATION_TITLE, buttonNames, 0,
            IDEMessageDialogUtil.getWarn()));
        // ????????????
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            ResponseBean commonResp = invokeCommonRequest();
            deletePortingTask(commonResp);
        } else {
            // ????????????
            MigrationProcess process = new MigrationProcess(taskId);
            process.setMessageBean(messageBean);
            process.processForCommon(null, SOFTWARE_MIGRATION_TITLE, process);
        }
    }

    /**
     * ????????????, ???????????????????????????????????????
     *
     * @param indicator ???????????????
     */
    @Override
    protected void success(ProgressIndicator indicator) {
    }

    /**
     * Query migration task process
     *
     * @return process
     */
    public int queryMigrationProgress() {
        if (messageBean == null) {
            return Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value());
        }
        ResponseBean responseBean = invokeCommonRequest();
        if (responseBean == null) {
            return Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value());
        }
        Logger.info("Query migration task process, Response: {}", responseBean.toString());
        Map<String, Object> progressData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        // ??????????????????yum??????????????????
        if (RespondStatus.MIGRATION_CENTER_YUM_FAILED.value().equals(responseBean.getRealStatus())) {
            showYumFailedFaqNotification(responseBean, progressData);
            return Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value());
        }
        if (progressData.get(RUNNING_STATUS) instanceof Integer) {
            int progress = (Integer) progressData.get(RUNNING_STATUS);
            progress = getProgressByRespondStatus(progress, progressData);
            Map<String, Object> messageData = JsonUtil.getJsonObjFromJsonStr(messageBean.getData());
            if (progress >= 100 || progress == Integer.valueOf(RespondStatus.PROCESS_STATUS_ERROR.value())) {
                showMessageTip(progress, messageBean, progressData);
                CommonHandler.invokeCallback(
                    messageBean.getCmd(), messageBean.getCbid(), responseBean.getResponseJsonStr());
                enableWebView(messageBean, Boolean.TRUE);
            } else {
                freshMessageAndWebView(progress, responseBean, messageData, progressData);
            }
            return progress;
        }

        return Integer.valueOf(RespondStatus.PROCESS_STATUS_ERROR.value());
    }

    private void showYumFailedFaqNotification(ResponseBean responseBean, Map<String, Object> progressData) {
        String msg = CommonUtil.getRspTipInfo(responseBean) + I18NServer.toLocale("plugins_porting_faq_tips");
        Object solutionXmlName = progressData.get(solutionXmlKey);
        if (solutionXmlName != null) {
            msg = MessageFormat.format(
                I18NServer.toLocale("plugins_common_migration_error_title"), solutionXmlName) + msg;
        }
        IDENotificationUtil.notificationForHyperlink(new NotificationBean("", msg, NotificationType.ERROR),
            op -> CommonUtil.openURI(I18NServer.toLocale("plugins_porting_migration_yum_error_url")));
    }

    /**
     * ?????????????????????????????????WebView??????
     *
     * @param progress     ???????????????
     * @param responseBean ????????????
     * @param messageData  ???????????????????????????
     * @param progressData ????????????????????????
     */
    private void freshMessageAndWebView(int progress,
                                        ResponseBean responseBean, Map<String, Object> messageData,
                                        Map<String, Object> progressData) {
        if ("center".equalsIgnoreCase(String.valueOf(messageData.get("entry")))) {
            migrationCenterFlag = true;
            if (reqOfURL.indexOf(TASK_ID_STR_CHECK) == -1) {
                messageData.put(URL_REQ, (reqOfURL += TASK_ID_STR_CHECK + progressData.get("task_name")));
                messageBean.setData(JsonUtil.getJsonStrFromJsonObj(messageData));
            }
        }
        // ????????????100???????????????????????????????????????
        String stepInfo = null;
        int stepId = Integer.parseInt(String.valueOf(progressData.get(TASK_ID)));
        if (stepId >= Integer.valueOf(RespondStatus.PROCESS_STATUS_NORMAL.value())) {
            stepInfo = MessageFormat.format(I18NServer.toLocale(
                ERRInfo.getERRInfoByType(String.valueOf(progressData.get(TYPE))).i18nKey()), stepId + 1);
        }
        solutionName = String.valueOf(progressData.get(solutionXmlKey));
        indicatorMessage = MessageFormat.format(I18NServer.toLocale("plugins_common_migration_info"),
            solutionName, new BigDecimal(Double.valueOf(progress)).intValue() + "%", stepInfo);
        if (progress == Integer.valueOf(RespondStatus.PROCESS_STATUS_NORMAL.value())) {
            indicatorMessage = I18NServer.toLocale("plugins_common_migration_info_initialize");
        }
        // ????????????????????????
        CommonHandler.invokeCallback(messageBean.getCmd(), messageBean.getCbid(), responseBean.getResponseJsonStr());
    }

    /**
     * ??????????????????URL
     *
     * @return ??????????????????
     */
    private ResponseBean invokeCommonRequest() {
        Map<String, Object> messageData = JsonUtil.getJsonObjFromJsonStr(messageBean.getData());
        if (messageData.get(URL_REQ) instanceof String) {
            reqOfURL = (String) messageData.get(URL_REQ);
        }
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, reqOfURL,
            HttpMethod.GET.vaLue(), "");
        data.setNeedProcess(true);
        Language.getRegisteredLanguages();
        return PortingHttpsServer.INSTANCE.requestData(data);
    }

    /**
     * ???????????????WebView
     *
     * @param messageBean ?????????
     * @param enableFlag  ????????????
     */
    public void enableWebView(MessageBean messageBean, boolean enableFlag) {
        Map<String, Object> freshData = new HashMap<>();
        freshData.put("enableView", enableFlag);
        CommonHandler.invokeCallback(messageBean.getCmd(),
            messageBean.getCbid(), JsonUtil.getJsonStrFromJsonObj(freshData));
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param progress     ?????????????????????
     * @param progressData ??????data?????????
     * @return currentValue ????????????
     */
    private int getProgressByRespondStatus(int progress, Map<String, Object> progressData) {
        int currentValue = progress;
        if (progress == Integer.valueOf(RespondStatus.PROCESS_STATUS_RUNNING.value())) {
            if (progressData.get(PROGRESS_FAKE) instanceof Integer) {
                currentValue = (Integer) progressData.get(PROGRESS_FAKE);
            }
        } else if (progress == Integer.valueOf(RespondStatus.PROCESS_STATUS_SUCCESS.value())) {
            currentValue = 101;
        } else {
            currentValue = Integer.valueOf(RespondStatus.PROCESS_STATUS_ERROR.value());
        }
        return currentValue;
    }

    /**
     * ?????????????????????????????????
     *
     * @param processCurrentValue ??????????????????
     * @param message             ???????????????
     * @param progressData        ?????????????????????
     */
    private void showMessageTip(int processCurrentValue, MessageBean message, Map<String, Object> progressData) {
        Map<String, Object> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String noticeMessage = "";
        if (processCurrentValue < Integer.valueOf(RespondStatus.PROCESS_STATUS_NORMAL.value())) {
            String messageTip = "";
            String typeName = ERRInfo.getERRInfoByType(String.valueOf(progressData.get(TYPE))).i18nKey();
            int stepId = Integer.parseInt(String.valueOf(progressData.get(TASK_ID)));
            if (stepId >= MIGRATION_STEP_VALID_START) {
                messageTip = MessageFormat.format(I18NServer.toLocale(typeName),
                    stepId + 1) + I18NServer.toLocale("plugins_common_migration_fail_sup");
            }
            if (ValidateUtils.isEmptyString(solutionName)) {
                solutionName = String.valueOf(progressData.get(solutionXmlKey));
            }
            noticeMessage = I18NServer.toLocale("plugins_common_migration_fail",
                Arrays.asList(solutionName)) + MessageFormat.format(I18NServer.toLocale(
                "plugins_common_migration_fail_content"), messageTip, messageData.get("fixPath"));

            IDENotificationUtil.notificationCommon(new NotificationBean("", noticeMessage, NotificationType.ERROR));
        } else {
            String outName = getOutName(messageData, progressData);
            noticeMessage = I18NServer.toLocale("plugins_common_migration_success", solutionName);
            if (ValidateUtils.isNotEmptyString(outName)) {
                noticeMessage += MessageFormat.format(I18NServer.toLocale(
                    "plugins_common_migration_success_file_tips"), outName);
                sendMessageAndDownloadFile(noticeMessage, outName);
            } else {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean("", noticeMessage, NotificationType.INFORMATION));
            }
        }
    }

    /**
     * ??????outName
     *
     * @param messageData  ??????????????????
     * @param progressData ?????????????????????
     * @return outName ?????????
     */
    private String getOutName(Map<String, Object> messageData, Map<String, Object> progressData) {
        if (migrationCenterFlag) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("software", String.valueOf(progressData.get(solutionXmlKey)));
            RequestDataBean data = new RequestDataBean(
                PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/solution/detailinfo/", HttpMethod.GET.vaLue(), "");
            data.setUrlParams(JsonUtil.getJsonStrFromJsonObj(paramsMap));
            ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
            if (responseBean == null) {
                return "";
            }
            if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
                Map<String, Object> softData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
                Map<String, String> outNameData;
                if (softData.get("sw_info") instanceof Map) {
                    outNameData = (Map) softData.get("sw_info");
                    return outNameData.get("outname");
                }
            }
        }
        return String.valueOf(messageData.get("outputName"));
    }

    /**
     * ???????????????????????????
     *
     * @param noticeMessage ????????????
     * @param outName       ????????????
     */
    public void sendMessageAndDownloadFile(String noticeMessage, String outName) {
        StringBuffer notify = new StringBuffer();
        notify.append("<html><span>").append(noticeMessage).append("</span><br />").append("<a href=\"#\">")
            .append(I18NServer.toLocale("plugins_common_migration_success_file_download"))
            .append(PortingIDEConstant.HTML_FOOT);
        IDENotificationUtil.notificationForHyperlink(
            new NotificationBean("", notify.toString(), NotificationType.INFORMATION), data -> {
                // ???????????????????????????
                FileSaverDialog dialog = FileChooserFactory.getInstance()
                    .createSaveFileDialog(new FileSaverDescriptor("Save File", "Select local file"),
                        CommonUtil.getDefaultProject());
                VirtualFileWrapper fileWrapper = dialog.save(
                    LocalFileSystem.getInstance().findFileByPath(CommonUtil.getDefaultProject().getBasePath()),
                    outName.substring(outName.lastIndexOf(PortingIDEConstant.PATH_SEPARATOR) + 1));
                if (fileWrapper == null) {
                    return;
                }
                sendReqAndSaveFile(fileWrapper, outName);
            });
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param fileWrapper fileWrapper
     * @param outName     outName
     * @throws IOException IO??????
     */
    private void sendReqAndSaveFile(VirtualFileWrapper fileWrapper, String outName) {
        try {
            RequestDataBean dataBean = new RequestDataBean(
                PortingIDEConstant.TOOL_NAME_PORTING, "/portadv/solution/result/", HttpMethod.POST.vaLue(), "");
            Map<String, String> params = new HashMap<>();
            params.put("file", outName);
            dataBean.setBodyData(JsonUtil.getJsonStrFromJsonObj(params));
            dataBean.setNeedDownloadZip(false);
            dataBean.setNeedDownloadFile(true);
            File file = fileWrapper.getFile();
            int lastSplit = file.getCanonicalPath().lastIndexOf(PortingIDEConstant.WINDOW_PATH_SEPARATOR);
            dataBean.setDownloadFileName(file.getCanonicalPath().substring(lastSplit + 1));
            // ?????????????????????
            dataBean.setDownloadPtah(file.getCanonicalPath().substring(0, lastSplit));
            dataBean.setFile(new File[] {new File(outName)});
            ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(dataBean);
            if (responseBean == null) {
                return;
            }
            NotificationType notificationType =
                (responseBean.getStatus() == null) ? NotificationType.INFORMATION : NotificationType.ERROR;
            String message =
                I18NServer.toLocale("plugins_common_migration_download_success", file.getCanonicalPath());
            if (responseBean.getStatus() != null) {
                message = CommonUtil.getRspTipInfo(responseBean);
            }
            // ??????????????????
            IDENotificationUtil.notificationCommon(new NotificationBean("", message, notificationType));
        } catch (IOException e) {
            Logger.error("IOException occurred when executing method of sendReqAndSaveFile.");
        }
    }

    /**
     * ??????PortingTask
     *
     * @param commonResp ?????????????????????????????????
     */
    public void deletePortingTask(ResponseBean commonResp) {
        String url = "/portadv/solution/" + taskId + "/";
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.DELETE.vaLue(), "");
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return;
        }
        Logger.info("delete migration Task, Response: {}", responseBean.toString());

        NotificationType notificationType = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? NotificationType.INFORMATION
            : NotificationType.ERROR;

        String content = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())
            ? I18NServer.toLocale("plugins_porting_settings_task_canceled_success")
            : I18NServer.toLocale("plugins_porting_settings_task_canceled_failed");
        IDENotificationUtil.notificationCommon(new NotificationBean(
                SOFTWARE_MIGRATION_TITLE, content, notificationType));

        // ??????????????????
        Map<String, Object> freshData = JsonUtil.getJsonObjectFromJsonStr(commonResp.getResponseJsonStr());
        freshData.put(IDEA_NOTIFY, Boolean.TRUE);
        commonResp.setResponseJsonStr(JsonUtil.getJsonStrFromJsonObj(freshData));
        CommonHandler.invokeCallback(messageBean.getCmd(), messageBean.getCbid(), commonResp.getResponseJsonStr());
        enableWebView(messageBean, Boolean.TRUE);
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    private enum ERRInfo {
        STEPS("steps", "plugins_common_migration_fail_steps"),
        PRE_CHECK("precheck", "plugins_common_migration_fail_precheck"),
        BASH("bash", "plugins_common_migration_fail_bash"),
        OS_CHECK("oscheck", "plugins_common_migration_fail_oscheck");

        private final String typeName;

        private final String i18nKey;

        ERRInfo(String typeName, String i18nKey) {
            this.typeName = typeName;
            this.i18nKey = i18nKey;
        }

        /**
         * ??????????????????
         *
         * @return typeName
         */
        public String typeName() {
            return typeName;
        }

        /**
         * ????????????????????????key
         *
         * @return i18nKey
         */
        public String i18nKey() {
            return i18nKey;
        }

        /**
         * ??????????????????functionName??????ERRInfo????????????????????????
         *
         * @param typeName ?????????
         * @return ERRInfo ????????????
         */
        public static ERRInfo getERRInfoByType(String typeName) {
            for (ERRInfo errInfo : ERRInfo.values()) {
                if (errInfo.typeName().equals(typeName)) {
                    return errInfo;
                }
            }

            return ERRInfo.STEPS;
        }
    }
}

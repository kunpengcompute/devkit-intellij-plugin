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
     * 任务过程中的步骤id
     */
    private static final String TASK_ID = "id";

    /**
     * 任务状态属性
     */
    private static final String RUNNING_STATUS = "runningstatus";

    /**
     * 进度值属性
     */
    private static final String PROGRESS_FAKE = "progress_fake";

    /**
     * intellij标识
     */
    private static final String IDEA_NOTIFY = "intelliJNotify";

    /**
     * 类型属性
     */
    private static final String TYPE = "type";

    /**
     * url属性
     */
    private static final String URL_REQ = "url";

    /**
     * 拼接迁移中心的url
     */
    private static final String TASK_ID_STR_CHECK = "&task_id=";

    /**
     * 步骤初始值
     */
    private static final int MIGRATION_STEP_VALID_START = 0;

    /**
     * 按钮数量
     */
    private static final int BUTTON_NUM = 2;

    private static final String SOFTWARE_MIGRATION_TITLE =
            I18NServer.toLocale("plugins_porting_software_migration_title");

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * url的值
     */
    private String reqOfURL;

    /**
     * 进度条显示文本
     */
    private String indicatorMessage;

    /**
     * 迁移的软件名称
     */
    private String solutionName;

    /**
     * 发送的消息体
     */
    private MessageBean messageBean;

    /**
     * 迁移中心标识
     */
    private boolean migrationCenterFlag = false;

    private final String solutionXmlKey;

    /**
     * 软件迁移构造方法，封装任务类型、ID
     *
     * @param taskId   任务ID
     */
    public MigrationProcess(String taskId) {
        this.taskId = taskId;
        solutionXmlKey = "solution_xml";
    }

    /**
     * 设置任务ID
     *
     * @param taskId 任务ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取任务ID
     *
     * @return String
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 获取消息体
     *
     * @return 消息体
     */
    public MessageBean getMessageBean() {
        return messageBean;
    }

    /**
     * 设置消息体
     *
     * @param messageBean 消息体
     */
    public void setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
    }

    /**
     * 分析任务中的循环状态查询
     *
     * @param indicator 自定义参数
     */
    @Override
    protected void runTask(ProgressIndicator indicator) {
        // 当前进度
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
     * 取消任务
     *
     * @param indicator 自定义参数
     */
    @Override
    protected void cancel(ProgressIndicator indicator) {
        // 按钮数组
        List<IDEMessageDialogUtil.ButtonName> buttonNames = new ArrayList<>(BUTTON_NUM);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.OK);
        buttonNames.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        // 给出是否确定删除弹框
        String exitCode = IDEMessageDialogUtil.showDialog(new MessageDialogBean(
            I18NServer.toLocale("plugins_porting_migration_close_task_confirm_tip"),
                SOFTWARE_MIGRATION_TITLE, buttonNames, 0,
            IDEMessageDialogUtil.getWarn()));
        // 确认删除
        if (exitCode.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            ResponseBean commonResp = invokeCommonRequest();
            deletePortingTask(commonResp);
        } else {
            // 取消删除
            MigrationProcess process = new MigrationProcess(taskId);
            process.setMessageBean(messageBean);
            process.processForCommon(null, SOFTWARE_MIGRATION_TITLE, process);
        }
    }

    /**
     * 分析成功, 过程失败或迁移完成都会调用
     *
     * @param indicator 自定义参数
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
        // 专项软件迁移yum运行失败处理
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
     * 实时刷新迁移进度文本和WebView状态
     *
     * @param progress     任务进度值
     * @param responseBean 响应实体
     * @param messageData  页面发送的消息集合
     * @param progressData 迁移进度数据集合
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
        // 进度小于100的时候，实时刷新进度条文字
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
        // 回调页面刷新进度
        CommonHandler.invokeCallback(messageBean.getCmd(), messageBean.getCbid(), responseBean.getResponseJsonStr());
    }

    /**
     * 发送请求设置URL
     *
     * @return 进度响应实体
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
     * 启用或禁用WebView
     *
     * @param messageBean 消息体
     * @param enableFlag  启用标识
     */
    public void enableWebView(MessageBean messageBean, boolean enableFlag) {
        Map<String, Object> freshData = new HashMap<>();
        freshData.put("enableView", enableFlag);
        CommonHandler.invokeCallback(messageBean.getCmd(),
            messageBean.getCbid(), JsonUtil.getJsonStrFromJsonObj(freshData));
    }

    /**
     * 根据返回的任务状态信息获取进度值
     *
     * @param progress     迁移任务状态值
     * @param progressData 响应data的集合
     * @return currentValue 当前进度
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
     * 软件迁移任务的消息通知
     *
     * @param processCurrentValue 当前任务进度
     * @param message             消息体对象
     * @param progressData        接口返回的集合
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
     * 获取outName
     *
     * @param messageData  响应数据集合
     * @param progressData 接口返回的集合
     * @return outName 包路径
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
     * 提示下载迁移软件包
     *
     * @param noticeMessage 提示消息
     * @param outName       文件名称
     */
    public void sendMessageAndDownloadFile(String noticeMessage, String outName) {
        StringBuffer notify = new StringBuffer();
        notify.append("<html><span>").append(noticeMessage).append("</span><br />").append("<a href=\"#\">")
            .append(I18NServer.toLocale("plugins_common_migration_success_file_download"))
            .append(PortingIDEConstant.HTML_FOOT);
        IDENotificationUtil.notificationForHyperlink(
            new NotificationBean("", notify.toString(), NotificationType.INFORMATION), data -> {
                // 打开本地资源管理器
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
     * 请求接口并保存迁移文件到本地
     *
     * @param fileWrapper fileWrapper
     * @param outName     outName
     * @throws IOException IO异常
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
            // 保存的本地路径
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
            // 保存文件提示
            IDENotificationUtil.notificationCommon(new NotificationBean("", message, notificationType));
        } catch (IOException e) {
            Logger.error("IOException occurred when executing method of sendReqAndSaveFile.");
        }
    }

    /**
     * 取消PortingTask
     *
     * @param commonResp 最近一次任务的返回信息
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

        // 需要刷新页面
        Map<String, Object> freshData = JsonUtil.getJsonObjectFromJsonStr(commonResp.getResponseJsonStr());
        freshData.put(IDEA_NOTIFY, Boolean.TRUE);
        commonResp.setResponseJsonStr(JsonUtil.getJsonStrFromJsonObj(freshData));
        CommonHandler.invokeCallback(messageBean.getCmd(), messageBean.getCbid(), commonResp.getResponseJsonStr());
        enableWebView(messageBean, Boolean.TRUE);
    }

    /**
     * 任务过程错误信息的枚举，根据类型区分
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
         * 获取类型名称
         *
         * @return typeName
         */
        public String typeName() {
            return typeName;
        }

        /**
         * 获取任务信息资源key
         *
         * @return i18nKey
         */
        public String i18nKey() {
            return i18nKey;
        }

        /**
         * 通过延伸信息functionName获取ERRInfo类的一个枚举实例
         *
         * @param typeName 类型名
         * @return ERRInfo 枚举实例
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

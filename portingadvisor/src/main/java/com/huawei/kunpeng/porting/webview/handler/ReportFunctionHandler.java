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

package com.huawei.kunpeng.porting.webview.handler;

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
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;
import com.huawei.kunpeng.porting.bean.SourceFileBean;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedReportPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.PortingSourceEditor;

import com.intellij.notification.NotificationType;

import java.io.File;
import java.util.Map;

/**
 * porting报告详情页面的function处理器
 *
 * @since 2020-11-13
 */
public class ReportFunctionHandler extends FunctionHandler {
    private static final String HTML = "1";

    /**
     * 下载csv离线报告
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadReport(MessageBean message, String module) {
        Logger.info("downloadReport start.");

        // 查询csv离线报告内容
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("reportId");
        String reportType = String.valueOf(data.get("reportType"));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(
            PortingIDEConstant.TOOL_NAME_PORTING,
            "/portadv/tasks/" + taskId + "/download/?report_type=" + reportType, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        // 文件名称
        String fileName = HTML.equals(reportType) ? taskId + ".html" : taskId + ".csv";

        // 弹出保存文件选择框
        NotificationBean notification = new NotificationBean("",
            I18NServer.toLocale("plugins_porting_report_download_success", fileName),
            NotificationType.INFORMATION);
        notification.setProject(CommonUtil.getDefaultProject());
        UIUtils.saveTXTFileToLocalForDialog(responseBean.getData(), fileName, notification);

        Logger.info("downloadReport end: {}", fileName);
    }

    /**
     * 下载html离线报告
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadReportHtml(MessageBean message, String module) {
        Logger.info("downloadReportHtml start.");

        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String fileNameKey = "fileName";
        String info = I18NServer.toLocale("plugins_porting_report_download_success", data.get(fileNameKey));
        if (data.get("is_file") != null) {
            info = I18NServer.toLocale("plugins_porting_file_download_success", data.get(fileNameKey));
        }
        // 弹出保存文件选择框
        NotificationBean notification = new NotificationBean("", info, NotificationType.INFORMATION);
        notification.setProject(CommonUtil.getDefaultProject());
        UIUtils.saveTXTFileToLocalForDialog(data.get("htmlContent"), data.get(fileNameKey), notification);

        Logger.info("downloadReportHtml end: {}", data.get(fileNameKey));
    }

    /**
     * 查看源码迁移建议
     *
     * @param message 数据
     * @param module  模块
     */
    public void codeSuggestingOpt(MessageBean message, String module) {
        if (StringUtil.stringIsEmpty(message.getMessageJsonStr())) {
            return;
        }

        openCodeFile(message, TaskType.SOURCE_SCAN.value());
    }

    /**
     * 打开源码文件
     *
     * @param message  数据
     * @param taskType 任务类型
     */
    private void openCodeFile(MessageBean message, String taskType) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if (data != null) {
            String taskId = data.get("reportId");
            String remoteFilePath = data.get("remoteFilePath");
            String filePath = data.get("filepath");
            boolean localFile = true;
            if (filePath.equals(remoteFilePath)) {
                localFile = false;
            }

            // Mindstudio兼容 将路径中"\" 转换为 "/"
            filePath = filePath.replaceAll("\\\\", IDEConstant.PATH_SEPARATOR);
            File file = new File(filePath);
            if (!file.exists()) {
                localFile = false;
            }
            String localFilePath = filePath;
            if (!localFile) {
                String workspaceFilePath =
                    CommonUtil.getPluginInstalledPathFile(IDEConstant.PORTING_WORKSPACE_TEMP);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(workspaceFilePath).append(File.separator);
                if (CommonUtil.getDefaultWindow() != null) {
                    stringBuilder.append(CommonUtil.getDefaultWindow().getName()).append(File.separator);
                }
                stringBuilder.append(taskId).append(File.separator);
                stringBuilder.append(remoteFilePath);
                localFilePath = stringBuilder.toString();
            }
            String url = data.get("url");
            SourceFileBean sourceFileBean =
                new SourceFileBean(url, taskId, taskType, localFile, localFilePath, remoteFilePath);
            EditorSourceFileHandle.getEditorSourceFileHandle()
                .openSourceFile(sourceFileBean);
        }
    }

    /**
     * 弱内存不能下载报告的提示
     *
     * @param message 数据
     * @param module  模块
     */
    public void unableDownInfoBox(MessageBean message, String module) {
        Map<String, Object> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Object resp = messageData.get("resp");
        if (resp == null) {
            return;
        }
        ResponseBean response = JsonUtil.jsonToDataModel(resp.toString(), ResponseBean.class);
        if (response != null
            && RespondStatus.REPORT_FILE_LOCKED_MEMORY_CONSISTENCY.value().equals(response.getRealStatus())) {
            Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(response.getData());
            // 最新报告id
            String newReportId = data.get("id");
            if (newReportId != null) {
                Object type = messageData.get("btnType");
                String notificationMessage = "";
                if (type != null && "view".equals(type.toString())) {
                    notificationMessage = I18NServer.toLocale("download_the_latest_analysis_view_suggestion",
                        StringUtil.formatCreatedId(newReportId));
                } else {
                    notificationMessage = I18NServer.toLocale("download_the_latest_analysis_report",
                        StringUtil.formatCreatedId(newReportId));
                }
                // 带打开新报告操作的通知
                IDENotificationUtil.notificationForHyperlink(new NotificationBean("", notificationMessage,
                        NotificationType.INFORMATION),
                    op -> EnhancedReportPageEditor.openPage(newReportId, TaskType.WEAK_CHECK.value()));
            }
        }
        if (response != null && RespondStatus.REPORT_NOT_NEW.value().equals(response.getRealStatus())) {
            // 带打开创建任务首页的通知
            IDENotificationUtil.notificationForHyperlink(
                new NotificationBean(I18NServer.toLocale("common_term_operate_lockedTitle"),
                    I18NServer.toLocale("create_a_new_analysis_task"),
                    NotificationType.INFORMATION), op -> EnhancedFunctionPageEditor.openPage());
        }
    }

    /**
     * 源码迁移不能下载报告的提示
     *
     * @param message 数据
     * @param module  模块
     */
    public void sourceInfoBox(MessageBean message, String module) {
        Map<String, Object> messageData = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Object resp = messageData.get("resp");
        if (resp == null) {
            return;
        }
        ResponseBean response = JsonUtil.jsonToDataModel(resp.toString(), ResponseBean.class);
        if (response != null && RespondStatus.REPORT_FILE_LOCKED.value().equals(response.getRealStatus())) {
            Object type = messageData.get("type");
            SourcePortingHandler.executeNotification(response, type == null ? "" : type.toString());
        }
        if (response != null && RespondStatus.REPORT_NOT_NEW.value().equals(response.getRealStatus())) {
            // 带打开创建任务首页的通知
            IDENotificationUtil.notificationForHyperlink(
                new NotificationBean(I18NServer.toLocale("common_term_operate_lockedTitle"),
                    I18NServer.toLocale("create_a_new_analysis_task"),
                    NotificationType.INFORMATION), op -> PortingSourceEditor.openPage());
        }
    }
}

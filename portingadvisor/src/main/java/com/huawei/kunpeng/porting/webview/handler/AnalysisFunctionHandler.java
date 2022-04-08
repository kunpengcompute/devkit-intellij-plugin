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
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.http.module.pkgrebuild.PkgRebuildingHandler;
import com.huawei.kunpeng.porting.process.WorkingProcess;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisEditor;

import com.intellij.notification.NotificationType;

import java.util.Map;
import java.util.Objects;

/**
 * 软件包构建_详情页面
 *
 * @since 2020-12-29
 */
public class AnalysisFunctionHandler extends FunctionHandler {
    private static final String TASK_TYPE = "1";

    private static final String TITLE = I18NServer.toLocale("plugins_porting_software_rebuilding_title");

    private static final String TASK_ID_KEY = "taskID";

    /**
     * 软件包构建进度条。
     *
     * @param message message
     * @param module  module
     */
    public void analysisProcess(MessageBean message, String module) {
        Logger.info("analysisProcess start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get(TASK_ID_KEY);
        if (taskId == null || Objects.equals(taskId, "")) {
            String msg = I18NServer.dataToLocale(data);
            msg = msg + I18NServer.toLocale("plugins_porting_faq_tips");
            IDENotificationUtil.notificationForHyperlink(new NotificationBean(TITLE, msg, NotificationType.ERROR),
                op -> CommonUtil.openURI(I18NServer.toLocale("plugins_porting_rebuild_failed_faq_url")));
            AnalysisEditor.openPage(I18NServer.toLocale("plugins_porting_software_rebuilding_title"));
            return;
        }
        if (AnalysisEditor.getIsCanOpen()) {
            WorkingProcess analysisProcess = new WorkingProcess(TASK_TYPE, taskId);
            analysisProcess.processForCommon(null, TITLE, analysisProcess);
        } else {
            Logger.info("analysisProcess  task has running");
        }
    }

    /**
     * 下载软件件重构包
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadRebuildPkg(MessageBean message, String module) {
        Logger.info("downloadReport start.");
        // 查询csv离线报告内容
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("path");
        String packageName = data.get("name");
        PkgRebuildingHandler.downloadPackage(taskId, packageName);
    }

    /**
     * 下载html离线报告
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadRebuildHTML(MessageBean message, String module) {
        Logger.info("downloadReportHtml start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String fileNameKey = "fileName";
        // 弹出保存文件选择框
        NotificationBean notification = new NotificationBean("",
            I18NServer.toLocale("plugins_porting_report_download_success", data.get(fileNameKey)),
            NotificationType.INFORMATION);
        notification.setProject(CommonUtil.getDefaultProject());
        UIUtils.saveTXTFileToLocalForDialog(data.get("htmlContent"), data.get(fileNameKey), notification);
        Logger.info("downloadReportHtml end: {}", data.get(fileNameKey));
    }
}

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
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.assesment.SoftwareAssessmentHandler;

import com.intellij.notification.NotificationType;

import java.util.Map;

/**
 * porting软件迁移评估详情页面function处理器
 *
 * @since 2021-01-05
 */
public class DepReportFunctionHandler extends FunctionHandler {
    private static final String HTML = "1";

    private static final String CSV = "0";

    /**
     * 下载depReport csv离线报告
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadDepReport(MessageBean message, String module) {
        Logger.info("downloadDepReport start.");
        // 查询csv离线报告内容
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("reportId");
        String reportType = String.valueOf(data.get("reportType"));
        ResponseBean responseBean =
            PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/portadv/binary/" + taskId + "/?report_type=" + reportType, HttpMethod.GET.vaLue(), ""));
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
        Logger.info("downloadDepReport end: {}", fileName);
    }

    /**
     * 下载depReport html离线报告
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadDepReportHtml(MessageBean message, String module) {
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Object reportType = data.get("reportType");
        if (reportType instanceof Integer) {
            SoftwareAssessmentHandler.downLoadReport((Integer) reportType, data.get("reportId").toString());
        }
    }
}

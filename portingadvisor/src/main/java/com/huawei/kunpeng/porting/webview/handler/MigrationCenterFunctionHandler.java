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
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.PortingCommonUtil;
import com.huawei.kunpeng.porting.process.MigrationProcess;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import java.util.Map;

/**
 * 专项软件迁移页面的function处理器
 *
 * @since 2021-01-04
 */
public class MigrationCenterFunctionHandler extends FunctionHandler {
    private static MigrationProcess migrationProcess = null;

    /**
     * 显示磁盘通知信息
     *
     * @param message 数据
     * @param module  模块
     */
    public void showDiskMessage(MessageBean message, String module) {
        Logger.info("showDiskMessage start");

        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        if (Double.parseDouble(data.getString("workRemain")) < (PortingIDEConstant.THRESHOLD_VALUE)
            || Double.parseDouble(data.getString("diskRemain")) < PortingIDEConstant.THRESHOLD_VALUE) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_porting_message_diskNotice"),
                    data.getString("content"), NotificationType.ERROR));
        } else {
            IDENotificationUtil.notificationCommon(
                new NotificationBean(I18NServer.toLocale("plugins_porting_message_diskNotice"),
                    data.getString("content"), NotificationType.WARNING));
        }
        Logger.info("showDiskMessage end.");
    }

    /**
     * 进度条显示迁移状态
     *
     * @param message 数据
     * @param module  模块
     */
    public void showProgress(MessageBean message, String module) {
        Logger.info("showProgress start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if (PortingCommonUtil.isTaskRunning(I18NServer.toLocale("plugins_porting_software_migration_title"))) {
            if (migrationProcess != null && migrationProcess.getTaskId().equals(data.get("taskID"))) {
                migrationProcess.setMessageBean(message);
            }
            return;
        }

        // 调用迁移任务
        MigrationProcess process = new MigrationProcess(data.get("taskID"));
        process.setMessageBean(message); // 后续重新请求的时候需要
        int runningStatus = process.queryMigrationProgress();

        // 查询迁移进度
        if (ValidateUtils.isEmptyString(process.getTaskId()) ||
            runningStatus == Integer.parseInt(RespondStatus.PROCESS_STATUS_ERROR.value())) {
            return;
        }
        process.enableWebView(message, Boolean.FALSE);
        process.processForCommon(null, I18NServer.toLocale("plugins_porting_software_migration_title"), process);
        migrationProcess = process;
        Logger.info("showProgress end.");
    }
}

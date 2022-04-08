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

package com.huawei.kunpeng.porting.action;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.DiskUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;

import java.io.File;

/**
 * ActionHelper
 *
 * @since 2021-01-28
 */
public class ActionHelper {
    private static final String SUCCESS = "0";

    private static final String INSUFFICIENT_SPACE = RespondStatus.DISK_NOT_ENOUGH.value();

    /**
     * upload package check
     *
     * @param message 请求信息
     * @param file    文件
     * @return if success return true
     */
    public static boolean uploadPackage(RequestDataBean message, File file) {
        message.setFile(new File[] {file});
        message.setNeedUploadFile(true);
        message.setNeedProcess(true);
        message.setNeedCancel(true);
        Language.getRegisteredLanguages();
        ResponseBean responseData = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return false;
        }
        if (SUCCESS.equals(responseData.getStatus())) {
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(responseData), NotificationType.INFORMATION));
            return true;
        } else if (INSUFFICIENT_SPACE.equals(responseData.getStatus())) {
            DiskUtil.sendDiskAlertMessage();
        } else {
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(responseData), NotificationType.ERROR));
        }
        return false;
    }
}

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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;

import com.intellij.notification.NotificationType;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Map;

/**
 * 磁盘空间管理工具类
 *
 * @since 1.0.0
 */
public class DiskUtil {
    /**
     * 红色预警信号
     */
    public static final int DISK_STATUS_RED_ALARM = 5;

    /**
     * 状态告警值 无告警
     */
    private static final int DISK_STATUS_NO_ALARM = 0;

    /**
     * 工作空间状态告警值 1
     */
    private static final int DISK_STATUS_WORK_RED_ALARM = 1;

    /**
     * 磁盘空间状态告警值 2
     */
    private static final int DISK_STATUS_DISK_RED_ALARM = 2;

    /**
     * 工作空间状态告警值 3
     */
    private static final int DISK_STATUS_WORK_YELLOW_ALARM = 3;

    /**
     * 磁盘空间状态告警值 4
     */
    private static final int DISK_STATUS_DISK_YELLOW_ALARM = 4;

    /**
     * 告警百分比
     */
    private static final float THRESHOLD_PERCENT = 0.2F;

    /**
     * 阈值
     */
    private static final float THRESHOLD_VALUE = 1.0F;

    /**
     * 属性值alarm_status
     */
    private static final String PROPERTY_ALARM_STATUS = "alarm_status";

    /**
     * 属性值softNeeded
     */
    private static final String PROPERTY_SOFT_NEEDED = "softNeeded";

    /**
     * 属性值softRemain
     */
    private static final String PROPERTY_SOFT_REMAIN = "softRemain";

    /**
     * 属性值partitionTotal
     */
    private static final String PROPERTY_PARTITION_TOTAL = "partitionTotal";

    /**
     * 属性值partRemain
     */
    private static final String PROPERTY_PART_REMAIN = "partRemain";

    /**
     * 公共获取磁盘空间容量信息
     *
     * @return 磁盘状态
     */
    public static int queryDiskState() {
        String token = null;
        Object tokenObj =
                IDEContext.getValueFromGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.TOKEN.vaLue());
        if (tokenObj instanceof String) {
            token = (String) tokenObj;
        }
        RequestDataBean data =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, "/space/", HttpMethod.GET.vaLue(), token);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(data);
        Map<String, Object> diskData = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        if (handlerDiskData(diskData) == DISK_STATUS_DISK_RED_ALARM
                || handlerWorkDiskData(diskData) == DISK_STATUS_WORK_RED_ALARM) {
            return DISK_STATUS_RED_ALARM;
        } else {
            return DISK_STATUS_NO_ALARM;
        }
    }

    /**
     * 处理返回的总体磁盘数据值
     *
     * @param diskData 磁盘数据map集合
     * @return 整体磁盘状态
     */
    private static int handlerDiskData(Map<String, Object> diskData) {
        int alarmStatus = (int) diskData.get(PROPERTY_ALARM_STATUS);
        int ans = DISK_STATUS_NO_ALARM;
        if (alarmStatus == DISK_STATUS_DISK_RED_ALARM || alarmStatus == DISK_STATUS_DISK_YELLOW_ALARM) {
            double diskTotal = Math.round(Double.parseDouble(diskData.get(PROPERTY_PARTITION_TOTAL).toString()));
            double diskRemain = Double.parseDouble(diskData.get(PROPERTY_PART_REMAIN).toString());
            diskRemain = diskRemain > 0 ? Double.valueOf(new DecimalFormat("#.00").format(diskRemain)) : 0;
            double diskRecommand = Double.parseDouble(new DecimalFormat("#.00").format(diskTotal * THRESHOLD_PERCENT));
            ans = sendNotifyMessage(Boolean.FALSE, diskTotal, diskRemain, diskRecommand);
        }
        return ans;
    }

    /**
     * 处理返回的磁盘工作空间数据值
     *
     * @param diskData 磁盘数据map集合
     * @return 工作空间磁盘状态
     */
    private static int handlerWorkDiskData(Map<String, Object> diskData) {
        int alarmStatus = (int) diskData.get(PROPERTY_ALARM_STATUS);
        int ans = DISK_STATUS_NO_ALARM;
        if (alarmStatus == DISK_STATUS_WORK_RED_ALARM || alarmStatus == DISK_STATUS_WORK_YELLOW_ALARM) {
            int workTotal = (int) Math.round(Double.parseDouble(String.valueOf(diskData.get(PROPERTY_SOFT_NEEDED))));
            double workRemain = Double.parseDouble(diskData.get(PROPERTY_SOFT_REMAIN).toString());
            workRemain = workRemain > 0 ? Double.valueOf(new DecimalFormat("#.00").format(workRemain)) : 0;
            double workRecommand = workTotal * THRESHOLD_PERCENT;
            ans = sendNotifyMessage(Boolean.TRUE, workTotal, workRemain, workRecommand);
        }
        return ans;
    }

    /**
     * 根据计算值发送通知消息
     *
     * @param isWorkNotify  是否工作空间通知
     * @param workTotal     空间总量
     * @param workRemain    空间剩余容量
     * @param workRecommand 建议空间容量
     * @return 磁盘状态
     */
    private static int sendNotifyMessage(boolean isWorkNotify,
        double workTotal, double workRemain, double workRecommand) {
        String noticeMessage = null;
        NotificationType notificationType = NotificationType.INFORMATION;
        int res = DISK_STATUS_NO_ALARM;
        if (isWorkNotify) {
            if (workRemain < THRESHOLD_VALUE) {
                noticeMessage = CommonI18NServer.toLocale("plugins_porting_message_workWarn");
                notificationType = NotificationType.WARNING;
                Logger.warn("The free workspace of " +
                        "the Porting Advisor is less than 1 GB. Recommended free workspace: > 20% .");
                res = DISK_STATUS_WORK_RED_ALARM;
            } else {
                noticeMessage = CommonI18NServer.toLocale("plugins_porting_message_workInfo");
                Logger.info("Insufficient workspace of the Porting Advisor. Please delete some reports.");
                res = DISK_STATUS_WORK_YELLOW_ALARM;
            }
        } else {
            if (workRemain < THRESHOLD_VALUE) {
                noticeMessage = CommonI18NServer.toLocale("plugins_porting_message_diskWarn");
                notificationType = NotificationType.WARNING;
                Logger.warn("The free drive space of " +
                        "the Porting Advisor is less than 1 GB. Recommended free drive space: > 20% .");
                res = DISK_STATUS_DISK_RED_ALARM;
            } else {
                noticeMessage = CommonI18NServer.toLocale("plugins_porting_message_diskInfo");
                Logger.info("Insufficient drive space of the Porting Advisor. Please release the drive space.");
                res = DISK_STATUS_DISK_YELLOW_ALARM;
            }
        }
        noticeMessage = MessageFormat.format(noticeMessage, workTotal, workRemain, workRecommand);
        IDENotificationUtil.notificationCommon(new NotificationBean(
                CommonI18NServer.toLocale("plugins_porting_message_diskNotice"), noticeMessage, notificationType));
        return res;
    }

    /**
     * 磁盘空间不足1G通知
     */
    public static void sendDiskAlertMessage() {
        IDENotificationUtil.notificationCommon(new NotificationBean(
                CommonI18NServer.toLocale("plugins_porting_message_diskNotice"),
                CommonI18NServer.toLocale("plugins_porting_message_workDiskError"), NotificationType.ERROR));
        Logger.error("The remaining drive space is less than 1 GB. " +
                "Please release the drive space and perform the next step.");
    }
}

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

package com.huawei.kunpeng.hyper.tuner.action.javaperf;

import com.huawei.kunpeng.hyper.tuner.action.JavaPerfTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;

import com.intellij.notification.NotificationType;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 功能描述
 *
 * @since 2021-08-13
 */
public class DataListAction {
    /**
     * 数据列表导出
     *
     * @param path 路径
     * @param fileName 文件名
     * @param id id
     * @param url url
     */
    public void exportDataListFile(String path, String fileName, String id, String url) {
        if (!FileUtil.validateFileName(fileName)) {
            return;
        }
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                url + id, HttpMethod.GET.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadFileName(fileName);
        message.setDownloadPtah(path);
        message.setUrlParams(path + "/" + fileName);
        File[] files = {new File(fileName)};
        message.setFile(files);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        typeTip(responseBean, url, path);
    }

    private void typeTip(ResponseBean responseBean, String url, String path) {
        if (responseBean == null) {
            if (StringUtils.contains(url, "threadDump")) {
                IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_THREAD_DUMP,
                        JavaperfContent.DATA_LIST_THREAD_DUMP + " " + I18NServer.toLocale
                                ("plugins_hyper_tuner_download_report_fail"), NotificationType.ERROR));
                return;
            }
            if (StringUtils.contains(url, "heap")) {
                IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_MEMORY_DUMP,
                        JavaperfContent.DATA_LIST_MEMORY_DUMP + " " + I18NServer.toLocale
                                ("plugins_hyper_tuner_download_report_fail"), NotificationType.ERROR));
                return;
            }
            if (StringUtils.contains(url, "gcLog")) {
                IDENotificationUtil.notificationCommon(new NotificationBean(JavaperfContent.DATA_LIST_GC_LOGS,
                        JavaperfContent.DATA_LIST_GC_LOGS + " " + I18NServer.toLocale
                                ("plugins_hyper_tuner_download_report_fail"), NotificationType.ERROR));
                return;
            }
        }
        if (StringUtils.contains(url, "threadDump")) {
            NotificationBean notificationBean = new NotificationBean(JavaperfContent.DATA_LIST_THREAD_DUMP,
                    JavaperfContent.DATA_LIST_THREAD_DUMP + " "
                            + I18NServer.toLocale("plugins_hyper_tuner_download_report_success") + " "
                            + IDEConstant.HTML_HEAD + path + IDEConstant.HTML_FOOT,
                    NotificationType.INFORMATION);
            IDENotificationUtil.notificationForHyperlink
                    (notificationBean, obj -> CommonUtil.showFileDirOnDesktop(path));
            return;
        }
        if (StringUtils.contains(url, "heap")) {
            NotificationBean notificationBean = new NotificationBean(JavaperfContent.DATA_LIST_MEMORY_DUMP,
                    JavaperfContent.DATA_LIST_MEMORY_DUMP + " "
                            + I18NServer.toLocale("plugins_hyper_tuner_download_report_success") + " "
                            + IDEConstant.HTML_HEAD + path + IDEConstant.HTML_FOOT,
                    NotificationType.INFORMATION);
            IDENotificationUtil.notificationForHyperlink
                    (notificationBean, obj -> CommonUtil.showFileDirOnDesktop(path));
            return;
        }
        if (StringUtils.contains(url, "gcLog")) {
            NotificationBean notificationBean = new NotificationBean(JavaperfContent.DATA_LIST_GC_LOGS,
                    JavaperfContent.DATA_LIST_GC_LOGS + " "
                            + I18NServer.toLocale("plugins_hyper_tuner_download_report_success") + " "
                            + IDEConstant.HTML_HEAD + path + IDEConstant.HTML_FOOT, NotificationType.INFORMATION);
            IDENotificationUtil.notificationForHyperlink
                    (notificationBean, obj -> CommonUtil.showFileDirOnDesktop(path));
            return;
        }
    }

    /**
     * 数据列表文件导入
     *
     * @param file 文件
     */
    public void importDataListFile(File file) {
        JavaPerfTreeAction.instance().uploadFile(null, file);
    }
}

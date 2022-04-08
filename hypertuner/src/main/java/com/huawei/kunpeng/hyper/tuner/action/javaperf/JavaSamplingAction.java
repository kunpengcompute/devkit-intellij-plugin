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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
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

import java.io.File;

/**
 * JavaSampling 采样分析实现类
 *
 * @since 2021-07-28
 */
public class JavaSamplingAction {
    /**
     * 下载采样分析报告
     *
     * @param path     选择的存储路径
     * @param fileName 选择的文件名称
     * @param id       选择的文件id
     */
    public void downloadSampling(String path, String fileName, String id) {
        if (!FileUtil.validateFileName(fileName)) {
            return;
        }

        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/records/actions/download/" + id, HttpMethod.GET.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadFileName(fileName);
        message.setDownloadPtah(path);
        message.setUrlParams(path + "/" + fileName);
        File[] files = {new File(fileName)};
        message.setFile(files);
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_sampling_analysis"),
                            TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_sampling_analysis")
                                    + I18NServer.toLocale("plugins_hyper_tuner_download_report_fail"),
                            NotificationType.ERROR));
            return;
        }
        NotificationBean notificationBean =
                new NotificationBean(TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_sampling_analysis"),
                        TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_sampling_analysis")
                                + I18NServer.toLocale("plugins_hyper_tuner_download_report_success") + " "
                                + IDEConstant.HTML_HEAD + path + IDEConstant.HTML_FOOT,
                        NotificationType.INFORMATION);
        IDENotificationUtil.notificationForHyperlink
                (notificationBean, obj -> CommonUtil.showFileDirOnDesktop(path));
    }

    /**
     * 删除采样分析报告
     *
     * @param id id
     */
    public static void deleteSampling(String id) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "/java-perf/api/records/" + id, HttpMethod.DELETE.vaLue(), "");
        TuningHttpsServer.INSTANCE.requestData(message);
    }
}

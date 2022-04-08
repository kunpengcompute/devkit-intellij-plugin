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

package com.huawei.kunpeng.hyper.tuner.common.constant;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.LogManageConstant;

/**
 * 弱口令与部署声明相关常量定义
 *
 * @since 2020-10-15
 */
public class TuningLogManageConstant extends LogManageConstant {
    /**
     * 国际化: 运行日志标题
     */
    public static final String RUN_LOG_TITLE = TuningI18NServer.toLocale("plugins_hyper_tuner_runlog_dialog_title");

    /**
     * 国际化 下载运行日志
     */
    public static final String DOWNLOG_RUN_LOG = TuningI18NServer.toLocale("plugins_hyper_tuner_download_run_log");


    /**
     * 国际化 下载操作日志
     */
    public static final String DOWNLOG_OPER_LOG = TuningI18NServer.toLocale("plugins_hyper_tuner_download_oper_log");

    /**
     * 国际化 用户
     */
    public static final String LOG_USERNAME = TuningI18NServer.toLocale("plugins_hyper_tuner_log_userName");

    /**
     * 国际化 操作名称
     */
    public static final String LOG_EVENT = TuningI18NServer.toLocale("plugins_hyper_tuner_log_event");

    /**
     * 国际化 操作结果
     */
    public static final String LOG_RESULT = TuningI18NServer.toLocale("plugins_hyper_tuner_log_result");

    /**
     * 国际化 操作主机IP
     */
    public static final String LOG_IP = TuningI18NServer.toLocale("plugins_hyper_tuner_log_ip");

    /**
     * 国际化 操作时间
     */
    public static final String LOG_TIME = TuningI18NServer.toLocale("plugins_hyper_tuner_log_time");

    /**
     * 国际化 操作详情
     */
    public static final String LOG_DETAIL = TuningI18NServer.toLocale("plugins_hyper_tuner_log_Detail");

    /**
     * 国际化 确认是否下载运行日志？
     */
    public static final String RUNLOG_DOWNLOAD_TIP = TuningI18NServer.toLocale("plugins_hyper_tuner_runlog_dialog_tip");

    /**
     * 国际化 日志文件名称
     */
    public static final String LOG_FILENAME = TuningI18NServer.toLocale("plugins_hyper_tuner_log_filename_title");

    /**
     * 国际化 文件大小
     */
    public static final String LOG_FILESIZE = TuningI18NServer.toLocale("plugins_hyper_tuner_log_filesize_title");

    /**
     * 国际化 下载日志
     */
    public static final String DOWNLOAD_LOG = TuningI18NServer.toLocale("plugins_hyper_tuner_button_download_log");

    /**
     * 国际化 请选择下载的文件
     */
    public static final String DOWNLOAD_SELECT =
            TuningI18NServer.toLocale("plugins_hyper_tuner_button_download_select");
}

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

package com.huawei.kunpeng.hyper.tuner.common.constant.sysperf;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 日志下载常量定义类
 *
 * @since 2021-4-25
 */
public class SysPrefLogContent {
    /**
     * 运行日志类型常量 Web_Server(默认类型)
     */
    public static String LOG_TYPE_WEB_SERVER = "";
    /**
     * 运行日志类型常量 分析日志
     */
    public static String LOG_TYPE_ANALYZER = "analyzer";
    /**
     * 运行日志类型常量 收集日志
     */
    public static String LOG_TYPE_COLLECTOR = "collector";

    /**
     * 国际化-性能日志
     */
    public static String DISPLAY_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_sysPrefLogDic");
    /**
     * 国际化-下载操作日志
     */
    public static String DOWNLOG_OPER_LOG = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_sysPrefLog_download_opera");
    /**
     * 国际化-下载运行日志
     */
    public static String DOWNLOG_RUN_LOG = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_sysPrefLog_download_run");
    /**
     * 国际化- Web_Server运行日志
     */
    public static String WEB_SERVER_LOG = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_sysPrefLog_webserver");
    /**
     * 国际化- 数据分析运行日志
     */
    public static String DATA_ANALYSIS_LOG = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_sysPrefLog_data_analysis");
    /**
     * 国际化- 数据采集运行日志
     */
    public static String DATA_COLLECTION_LOG = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_sysPrefLog_data_collection");

}
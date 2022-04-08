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

package com.huawei.kunpeng.hyper.tuner.common.constant.javaperf;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * Java性能分析 模块
 * 日志 相关常量定义
 *
 * @since 2021-07-07
 */
public class JavaPerfLogsConstant {
    /**
     * 日志 面板展示名称
     */
    public static final String DISPLAY_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_log");

    /**
     * 国际化- Java性能分析运行日志
     */
    public static String JAVA_PERF_RUN_LOG = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_log_javaRun");

    /**
     * 国际化-表格-状态：成功
     */
    public static String JAVA_PERF_TABLE_STATUS_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_log_table_status_success");
    /**
     * 国际化-表格-状态：失败
     */
    public static String JAVA_PERF_TABLE_STATUS_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_log_table_status_fail");
    /**
     * 国际化-表格-状态：超时
     */
    public static String JAVA_PERF_TABLE_STATUS_TIMEOUT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_log_table_status_timeout");
}

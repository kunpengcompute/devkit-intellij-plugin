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

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;

/**
 * Java性能分析 模块
 * java性能分析配置 相关常量定义
 *
 * @since 2021-07-07
 */
public class JavaProviderSettingConstant {
    /**
     * java性能分析配置 面板展示名称
     */
    public static final String JAVA_PROFILER_SETTINGS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_java_profiler_settings");

    /**
     * 国际化: 运行日志级别
     */
    public static final String TERM_RUNLOGLEVEL = TuningI18NServer.toLocale("plugins_hyper_tuner_log_run_level");

    /**
     * 国际化: 内部通信证书自动告警时间（天）
     */
    public static final String MAX_ALARM_DAYS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_max_alarm_days");

    /**
     * 国际化: 输入整数(java性能分析配置)告警天数
     */
    public static final String TIP_NUM_MODIFY_ALARM = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_tips_num_modify_alarm");

    /**
     * 国际化: 栈深度配置   TIP_STACK_DEPTH
     */
    public static final String STACK_DEPTH = TuningI18NServer.toLocale("plugins_hyper_tuner_stack_depth");

    /**
     * 国际化: 输入整数 （栈深度配置）   TIP_STACK_DEPTH
     */
    public static final String TIP_STACK_DEPTH = TuningI18NServer.toLocale("plugins_hyper_tuner_tips_stack_depth");

    /**
     * 国际化: 运行日志级别
     */
    public static final String RUN_LOG_LEVEL = TuningI18NServer.toLocale("plugins_hyper_tuner_log_run_level");

    /**
     * 国际化: 工作密钥
     */
    public static final String WORKING_KEY = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaPerf_working_key");
    /**
     * 国际化： 刷新工作密钥
     */
    public static final String REFRESH_WORKING_KEY = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaPerf_refresh_working_key");
    /**
     * 国际化： 刷新工作密钥-成功
     */
    public static final String UPDATE_SUCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaPerf_refresh_working_key_success");
    /**
     * 国际化： 刷新工作密钥-失败
     */
    public static final String UPDATE_FAILD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaPerf_refresh_working_key_faild");
    /**
     * 操作列：执行线程转储
     */
    public static final String DUMPHANDLE_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_dumpHandle_title");

    /**
     * 国际化：保存快照
     */
    public static final String SNAPSHOT_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_snapshot_title");

    /**
     * 国际化：实时数据限定修改
     */
    public static final String DATA_LIMIT_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_dataLimit_title");

    /**
     * 国际化：导入分析记录
     */
    public static final String IMPORT_RECORD_NAME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_profiling_import_record_name");

    /**
     * 国际化：在线分析关闭提示
     */
    public static final String PROFILING_LIMIT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaPerf_profiling_limit");

    /**
     * 国际化：保存报告成功
     */
    public static final String SAVE_REPORT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_profiling_save_report");
}

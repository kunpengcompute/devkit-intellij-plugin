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
import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * javaPerf常量类
 *
 * @since 2021-03-25
 */
public class JavaperfContent {
    /**
     * 目标环境列表
     */
    public static final String MEMBERS_LIST = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_members_list");

    /**
     * 目标环境管理
     */
    public static final String MEMBERS_MANAGE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_members_manage");

    /**
     * 在线分析
     */
    public static final String ONLINE_ANALYSIS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_online_analysis");

    /**
     * 在线分析-导出报告
     */
    public static final String ONLINE_ANALYSIS_EXPORT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_online_analysis_exportReport");
    /**
     * 在线分析-导出报告-成功
     */
    public static final String ONLINE_EXPORT_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_online_analysis_export_success");
    /**
     * 在线分析-导出报告-失败
     */
    public static final String ONLINE_EXPORT_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_online_analysis_export_fail");

    /**
     * 采样分析
     */
    public static final String SAMPLING_ANALYSIS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_sampling_analysis");

    /**
     * 数据列表
     */
    public static final String DATA_LIST = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_data_list");

    /**
     * 数据列表-用户
     */
    public static final String DATA_LIST_USER = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_data_user");

    /**
     * 数据列表-内存转储
     */
    public static final String DATA_LIST_MEMORY_DUMP = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_data_list_memory_dump");
    /**
     * 数据列表-线程转储
     */
    public static final String DATA_LIST_THREAD_DUMP = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_data_list_thread_dump");
    /**
     * 数据列表-GC 日志
     */
    public static final String DATA_LIST_GC_LOGS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_data_list_gc_logs");

    /**
     * 创建时间
     */
    public static final String DATA_LIST_CREATE_TIME = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_data_list_create_time");

    /**
     * 导入时间
     */
    public static final String DATA_LIST_IMPORT_TIME = I18NServer.toLocale(
            "plugins_hyper_tuner_profiling_import_time");

    /**
     * 停止分析
     */
    public static final String STOP_ANALYSIS = I18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_stop_task");

    /**
     * 停止分析
     */
    public static final String OK_STOP_ANALYSIS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_ok_stop_analysis");

    /**
     * 导入分析记录
     */
    public static final String IMPORT_ANALYSIS_RECORDS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_import_analysis_records");

    /**
     * 导出分析记录
     */
    public static final String EXPORT_ANALYSIS_RECORDS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_export_analysis_records");

    /**
     * 分析记录管理
     */
    public static final String ANALYSIS_RECORD_MANAGEMENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_analysis_record_management");

    /**
     * 删除
     */
    public static final String DELETE = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_delete");

    /**
     * 导出数据列表
     */
    public static final String EXPORT_DATA_LIST = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_export_data_list");

    /**
     * 导入数据列表
     */
    public static final String IMPORT_DATA_LIST = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_import_data_list");

    /**
     * 重启
     */
    public static final String RESTART = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_restart");

    /**
     * 停止分析记录
     */
    public static final String STOP_ANALYZING_RECORDS = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_stop_analyzing_records");

    /**
     * 添加目标环境
     */
    public static final String ADD_TARGET_ENVIRONMENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_add_target_environment");

    /**
     * 数据列表文件校验提示
     */
    public static final String DATALIST_UPDATE_FILENAME_TIP = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_datalist_update_filename_tip");

    /**
     * 采样分析-状态
     */
    public static final String SAMPLING_STATUS_FINISHED = "FINISHED";

    /**
     * 采样分析提示阈值提醒
     */
    public static final String SAMPLING_TIPS_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_sampling_tips_content");

    /**
     * 采样分析最大阈值提醒
     */
    public static final String SAMPLING_WARN_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_sampling_warn_content");

    /**
     * 内存转储提示阈值提醒
     */
    public static final String HEAPDUMP_TIPS_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_heapdump_tips_content");

    /**
     * 内存转储最大阈值提醒
     */
    public static final String HEAPDUMP_WARN_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_heapdump_warn_content");

    /**
     * 线程转储提示阈值提醒
     */
    public static final String THREADDUMP_TIPS_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_threaddump_tips_content");

    /**
     * 线程转储最大阈值提醒
     */
    public static final String THREADDUMP_WARN_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_threaddump_warn_content");

    /**
     * gclog提示阈值提醒
     */
    public static final String GCLOG_TIPS_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_gclog_tips_content");

    /**
     * gclog最大阈值提醒
     */
    public static final String GCLOG_WARN_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_gclog_warn_content");

    /**
     * 线程转储最大文件值(普通成员)
     */
    public static final String THREADDUMP_MAX_SIZE_NORMAL = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_heapdump_max_size_normal");

    /**
     * 线程转储最大文件值(admin)
     */
    public static final String HEAPDUMP_MAX_SIZE_ADMIN = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_heapdump_max_size_admin");

    /**
     * 线程转储最大文件size判断标志
     */
    public static final String UPDATE_MAXSIZE_FLAG = I18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_update_maxsize_flag");

}

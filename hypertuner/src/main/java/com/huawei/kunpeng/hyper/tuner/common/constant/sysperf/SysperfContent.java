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

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;

/**
 * sysperf常量类
 *
 * @since 2021-03-25
 */
public class SysperfContent {
    /**
     * 文件路径分割符
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * 国际化: 导入、导出
     */
    public static final String IMPANDEXP_TASK_DIC = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_Title_impandexptaskDic");

    /**
     * 国际化: 节点管理
     */
    public static final String NODE_MANAGER_DIC = TuningI18NServer.toLocale("plugins_hyper_tuner_node_Title_nodeDic");

    /**
     * 国际化: 预约任务
     */
    public static final String SCH_TASK_DIC = TuningI18NServer.toLocale("plugins_hyper_tuner_Title_schtaskDic");

    /**
     * 国际化: 任务模板
     */
    public static final String TASK_TEMPLETE_DIC = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_Title_tesktempleteDic");

    /**
     * 创建项目
     */
    public static final String CREATE_PROJECT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_create_project");
    /**
     * 创建项目成功
     */
    public static final String CREATE_PROJECT_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_create_project_success");
    /**
     * 创建项目失败
     */
    public static final String CREATE_PROJECT_FAIL = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_create_project_fail");

    /**
     * 删除项目
     */
    public static final String DELETE_PROJECT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_project");

    /**
     * 删除项目确认
     */
    public static final String DELETE_PROJECT_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_project_tip");

    /**
     * 删除项目成功
     */
    public static final String DELETE_PROJECT_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_project_success");

    /**
     * 删除项目失败
     */
    public static final String DELETE_PROJECT_ERROR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_project_error");

    /**
     * 修改项目
     */
    public static final String MODIFY_PROJECT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_modify_project");
    /**
     * 修改项目成功
     */
    public static final String MODIFY_PROJECT_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_modify_project_success");
    /**
     * 修改项目失败
     */
    public static final String MODIFY_PROJECT_FAIL = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_modify_project_fail");
    /**
     * 展示项目
     */
    public static final String SHOW_PROJECT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_task");

    /**
     * 创建任务
     */
    public static final String CREATE_TASK = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_create_task");


    /**
     * 创建任务成功
     */
    public static final String CREATE_TASK_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_create_task_success");

    /**
     * 创建任务失败
     */
    public static final String CREATE_TASK_FAIL = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_create_task_fail");
    /**
     * 修改任务成功
     */
    public static final String MODIFY_TASK_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_modify_task_success");

    /**
     * 修改任务失败
     */
    public static final String MODIFY_TASK_FAIL = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_modify_task_fail");
    /**
     * 删除任务
     */
    public static final String DELETE_TASK = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_task");

    /**
     * 删除任务确认
     */
    public static final String DELETE_TASK_TIP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_task_tip");

    /**
     * 删除任务成功
     */
    public static final String DELETE_TASK_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_task_success");

    /**
     * 删除任务失败
     */
    public static final String DELETE_TASK_ERROR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_delete_task_error");

    /**
     * 停止分析
     */
    public static final String STOP_TASK = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_stop_task");

    /**
     * 停止分析成功
     */
    public static final String STOP_TASK_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_stop_task_success");

    /**
     * 停止分析失败
     */
    public static final String STOP_TASK_ERROR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_stop_task_error");


    /**
     * 修改任务
     */
    public static final String MODIFY_TASK = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_modify_task");

    /**
     * 导入任务
     */
    public static final String IMPORT_TASK = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_import_task");

    /**
     * 导入任务
     */
    public static final String IMPORT_TASK_SUCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_import_task_success");

    /**
     * 导出任务
     */
    public static final String EXPORT_TASK = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task");

    /**
     * 国际化：弹窗提示
     * <p> 即将导出{0}工程的{1}任务，导出数据只包含采集的系统性能数据和分析结果，是否确认继续？</p>
     */
    public static final String EXPORT_TASK_TIP_CONTENT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task_tip_content");

    /**
     * 国际化：继续导出
     */
    public static final String EXPORT_TASK_TIP_CONFIRM = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task_tip_confirm");

    /**
     * 国际化：取消
     */
    public static final String EXPORT_TASK_TIP_CANCEL = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task_tip_cancel");
    /**
     * 国际化：进度条
     *
     * @implSpec 参数顺序：工程名，任务名，进度（百分比数字），文本展示
     */
    public static final String EXPORT_TASK_PROGRESS_BAR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_progress_bar");

    /**
     * 国际化：准备中。。。
     */
    public static final String EXPORT_TASK_BAR_PERPARING = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_progress_bar_preparing");

    /**
     * 国际化：导出中。。。
     */
    public static final String EXPORT_TASK_BAR_EXPORTING = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_progress_bar_exporting");

    /**
     * 国际化：导出成功
     */
    public static final String EXPORT_TASK_BAR_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_progress_bar_success");


    /**
     * {0}/{1}导出任务成功
     * 下载
     *
     * @implSpec 参数顺序：工程名，任务名
     */
    public static final String EXPORT_TASK_SUCCESS_DOWNLOAD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task_success_download");

    /**
     * 导出任务成功
     */
    public static final String EXPORT_TASK_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task_success");

    /**
     * 导出任务失败
     */
    public static final String EXPORT_TASK_ERROR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_export_task_error");

    /**
     * 保存模板
     */
    public static final String SAVE_TEMPLETE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_save_templete");

    /**
     * 保存模板-成功
     */
    public static final String SAVE_TEMPLETE_SUCCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_save_templete_success");

    /**
     * 保存模板-失败
     */
    public static final String SAVE_TEMPLETE_FAILED = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_save_templete_error");

    /**
     * 国际化：参数错误
     */
    public static final String PARAM_ERROR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_sysperf_param_error");
    /**
     * 国际化：在线分析
     */
    public static final String ONLINE_PROFILING = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaPerf_online_profiling");
}

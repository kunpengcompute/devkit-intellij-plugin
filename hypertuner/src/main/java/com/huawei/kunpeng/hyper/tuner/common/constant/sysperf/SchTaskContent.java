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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 预约任务 常量定义类
 *
 * @since 2021-4-25
 */
public class SchTaskContent {
    /**
     * 预约任务
     */
    public static final String SCH_TASK_DIC = I18NServer.toLocale("plugins_hyper_tuner_Title_schtaskDic");
    /**
     * 删除预约任务
     */
    public static final String SCH_TASK_DIALOG_DELETE_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_delete");
    /**
     * 删除预约任务 失败
     */
    public static final String SCH_TASK_DIALOG_DELETE_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_delete_success");
    /**
     * 删除预约任务 成功
     */
    public static final String SCH_TASK_DIALOG_DELETE_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_delete_fail");

    /**
     * 确认删除预约任务
     */
    public static final String SCH_TASK_DIALOG_DELETE_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_delete_content");
    /**
     * 确认删除 以下 预约任务
     */
    public static final String SCH_TASK_DIALOG_MULTI_DELETE_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_multi_delete_content");
    /**
     * 确认
     */
    public static final String SCH_TASK_OPERATE_OK = CommonI18NServer.toLocale("common_term_operate_ok");
    /**
     * 取消
     */
    public static final String SCH_TASK_OPERATE_CANCEL = CommonI18NServer.toLocale("common_term_operate_cancel");
    /**
     * 更新预约任务
     */
    public static final String SCH_TASK_DIALOG_UPDATE_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_update");
    /**
     * 无法更新已完成的预约任务
     */
    public static final String SCH_TASK_DIALOG_UPDATE_CANT = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_update_cant");
    /**
     * 更新预约任务 成功
     */
    public static final String SCH_TASK_DIALOG_UPDATE_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_update_success");
    /**
     * 更新预约任务 失败
     */
    public static final String SCH_TASK_DIALOG_UPDATE_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_update_fail");
    /**
     * 预约任务详情
     */
    public static final String SCH_TASK_DIALOG_DETAIL_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_schTask_detail");
    /**
     * 表格标题- 任务编号
     */
    public static final String TABLE_TASK_ID = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_id");
    /**
     * 表格标题- 任务名称
     */
    public static final String TABLE_TASK_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_taskName");
    /**
     * 表格标题- 任务状态
     */
    public static final String TABLE_TASK_STATUS = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_processStatus");
    /**
     * 表格标题- 分析对象
     */
    public static final String TABLE_TASK_ANALYSIS_TARGET = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_analysisTarget");
    /**
     * 表格标题- 分析类型
     */
    public static final String TABLE_TASK_ANALYSIS_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_analysisType");
    /**
     * 表格标题- 工程名称
     */
    public static final String TABLE_TASK_PROJECT_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_projectName");
    /**
     * 表格标题- 操作
     */
    public static final String TABLE_TASK_OPERATE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_table_operate");
    /**
     * 预约任务状态- 预约/Scheduled
     */
    public static final String STATUS_RESERVE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_status_reserve");
    /**
     * 预约任务状态- 完成/Completed
     */
    public static final String STATUS_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_status_success");
    /**
     * 预约任务状态- 下发中/Delivering
     */
    public static final String STATUS_RUNNING = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_status_running");
    /**
     * 预约任务状态- 失败/Failed
     */
    public static final String STATUS_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_status_fail");
    /**
     * 工具栏-筛选框标题- 筛选/screening
     */
    public static final String SCREENING_TITLE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_tool_screening");
    /**
     * 操作栏-查看详情
     */
    public static final String OPERATE_DETAIL = I18NServer.toLocale(
            "plugins_common_term_operate_task_detail");
    /**
     * 数据格式错误提示-采样时间必须是1~300中的数字
     */
    public static final String DURATION_NOTICE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_format_err_duration");
    /**
     * 数据格式错误提示-日期格式必须为 YYYY-MM-DD
     */
    public static final String DATE_FORMAT_NOTICE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_format_err_date");
    /**
     * 数据格式错误提示-日期格式必须为 HH:mm:ss
     */
    public static final String TIME_FORMAT_NOTICE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_format_err_time");
    /**
     * 数据格式错误提示-开始日期必须大于服务器时间并在当前日期的7天内
     */
    public static final String DATE_RANGE_START_NOTICE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_range_err_date_start");
    /**
     * 数据格式错误提示-结束日期为开始日期的30天内
     */
    public static final String DATE_RANGE_STOP_NOTICE = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_range_err_date_stop");
    /**
     * 数据格式错误提示-此数据的值应该位于 {0}-{1} 之间。
     */
    public static final String INPUT_PARAM_ERROR = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_range_err_input");

    /**
     * 数据格式错误提示-输入数据格式错误 /Incorrect format
     */
    public static final String INPUT_PARAM_INCORRECT_FORMAT = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_incorrect_format_input");
    /**
     * 数据校验错误提示-数据不能为空
     */
    public static final String INPUT_PARAM_NOT_EMPTY = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_input_not_empty");
    /**
     * 数据校验错误提示-带采样CPU核输入错误
     */
    public static final String INPUT_PARAM_ERROR_CPU = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_incorrect_format_input_cpu");
    /**
     * 数据校验错误提示-路径错误
     */
    public static final String INPUT_PARAM_ERROR_LOCATION = I18NServer.toLocale(
            "plugins_hyper_tuner_schTask_param_incorrect_format_input_location");
}

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
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 导入导出任务 管理相关常量定义
 *
 * @since 2021-4-25
 */
public class ImpAndExpTaskContent {
    /**
     * 国际化 任务描述
     */
    public static final String TASK_DIC = I18NServer.toLocale("plugins_hyper_tuner_Title_impandexptaskDic");
    /**
     * 国际化 确认。
     */
    public static final String OPERATE_OK = CommonI18NServer.toLocale("common_term_operate_ok");

    /**
     * 国际化 取消。
     */
    public static final String OPERATE_CANCEL = CommonI18NServer.toLocale("common_term_operate_cancel");
    /**
     * 国际化: 操作成功
     */
    public static final String OPERATE_SUCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_operate_success");

    /**
     * 国际化: 操作失败
     */
    public static final String OPERATE_FAILD = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_common_faild");

    /**
     * 国际化下载全部 任务记录
     */
    public static final String DOWNLOAD_ALL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_download_all");
    /**
     * 国际化下载导出任务提示：导入数据分析时请确保压缩文件完整
     */
    public static final String DOWNLOAD_TIPS = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_download_tips");
    /**
     * 国际化下载导出任务提示 文件名
     */
    public static final String DOWNLOAD_FILE_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_download_fileName");
    /**
     * 国际化下载导出任务提示 文件大小
     */
    public static final String DOWNLOAD_FILE_SIZE = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_download_fileSize");
    /**
     * 国际化 下载-文件存在提示
     */
    public static final String DOWNLOG_REPLACE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_download_replace_tips");

    /**
     * 国际化 删除选中记录
     */
    public static final String DELETE_ITEM = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_delete_item");
    /**
     * 国际化 确认删除选中记录
     */
    public static final String DELETE_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_delete_content");
    /**
     * 国际化 删除成功
     */
    public static final String DELETE_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_delete_success");
    /**
     * 国际化 删除失败
     */
    public static final String DELETE_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_delete_fail");
    /**
     * 国际化 重试选中的导入失败记录
     */
    public static final String RETRY_ITEM = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_retry_item");
    /**
     * 国际化 重试选中的导入失败记录
     */
    public static final String RETRY_ITEM_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_retry_item_content");
    /**
     * 国际化   重试成功
     */
    public static final String RETRY_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_retry_item_fail");
    /**
     * 国际化   重试失败
     */
    public static final String RETRY_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_retry_item_success");
    /**
     * 国际化 表头 任务编号
     */
    public static final String ID = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_id");
    /**
     * 国际化 表头 任务名称
     */
    public static final String TASK_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_taskName");
    /**
     * 国际化 表头 任务项目名称
     */
    public static final String PROJECT_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_projectName");
    /**
     * 国际化 表头 任务操作类型
     */
    public static final String OPERATE_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_operationType");
    /**
     * 国际化 表头 任务操作状态
     */
    public static final String PROCESS_STATUS = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_processStatus");
    /**
     * 国际化 表头 任务状态详细信息
     */
    public static final String DETAIL_INFO = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_statusDetail");
    /**
     * 国际化 表头 任务导出文件大小
     */
    public static final String FILE_SIZE = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_taskFilesize");
    /**
     * 国际化 表头 任务开始时间
     */
    public static final String START_TIME = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_startTime");
    /**
     * 国际化 表头 任务结束时间
     */
    public static final String END_TIME = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_table_endTime");

    /**
     * 国际化 任务类型 导出
     */
    public static final String TYPE_EXP = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_type_exp");
    /**
     * 国际化 任务类型 导入
     */
    public static final String TYPE_IMP = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_type_imp");
    /**
     * 国际化 出错
     */
    public static final String ERROR = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_error");
    /**
     * 国际化 任务状态  导出成功
     */
    public static final String STATUS_EXP_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_exp_success");
    /**
     * 国际化 任务状态 导出失败
     */
    public static final String STATUS_EXP_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_exp_fail");
    /**
     * 国际化 任务状态 导入成功
     */
    public static final String STATUS_IMP_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_success");
    /**
     * 国际化 任务状态  导入失败
     */
    public static final String STATUS_IMP_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_fail");

    /**
     * 国际化 任务状态  导入-上传中
     */
    public static final String STATUS_IMP_UPLOADING = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_uploading");
    /**
     * 国际化 任务状态  导入-导入中
     */
    public static final String STATUS_IMP_IMPORTING = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_uploading");
    /**
     * 国际化 任务状态  导入-导入失败
     */
    public static final String STATUS_IMP_IMPORT_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_import_fail");
    /**
     * 国际化 任务状态  导入-上传失败
     */
    public static final String STATUS_IMP_UPLOAD_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_upload_fail");
    /**
     * 国际化 任务状态  导入-导入任务启动失败
     */
    public static final String STATUS_IMP_UPLOAD_START_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_upload_start_fail");
    /**
     * 国际化 任务状态  导入-导入校验失败
     */
    public static final String STATUS_IMP_UPLOAD_CHECK_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_impAndExp_task_status_imp_upload_check_fail");
}

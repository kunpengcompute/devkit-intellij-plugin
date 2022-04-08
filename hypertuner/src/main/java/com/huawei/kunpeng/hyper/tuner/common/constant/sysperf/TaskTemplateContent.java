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
 * 任务模板 管理相关常量定义
 *
 * @since 2021-4-25
 */
public class TaskTemplateContent {
    /**
     * 国际化-标题 - 任务模板
     */
    public static final String TASK_TEMPLATE_DIC = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_tesktempleteDic");
    /**
     * 国际化-详细情况弹窗标题
     */
    public static final String TASK_TEMPLATE_DIALOG_TITLE_DETAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_taskTemplate_detail");
    /**
     * 国际化-删除弹窗标题
     */
    public static final String TASK_TEMPLATE_DIALOG_TITLE_DELETE = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_taskTemplate_delete");
    /**
     * 国际化-删除弹窗 提示内容
     */
    public static final String TASK_TEMPLATE_DIALOG_CONTENT_DELETE = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_taskTemplate_delete_content");
    /**
     * 国际化-批量删除弹窗 提示内容
     */
    public static final String TASK_TEMPLATE_DIALOG_MULTI_DELETE_CONTENT = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_taskTemplate_multi_delete_content");
    /**
     * 国际化-删除成功
     */
    public static final String TASK_TEMPLATE_DELETE_SUCCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_taskTemplate_delete_success");
    /**
     * 国际化-删除成功
     */
    public static final String TASK_TEMPLATE_DELETE_FAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_Title_taskTemplate_delete_fail");

    /**
     * 国际化-确认操作
     */
    public static final String OK = CommonI18NServer.toLocale("common_term_operate_ok");
    /**
     * 国际化-取消操作
     */
    public static final String CANCEL = CommonI18NServer.toLocale("common_term_operate_cancel");
    /**
     * 表头-编号
     */
    public static final String ID = I18NServer.toLocale(
            "plugins_hyper_tuner_taskTemplate_table_id");
    /**
     * 国际化-删除弹窗标题
     */
    public static final String TEMPLATE_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_taskTemplate_table_templateName");
    /**
     * 国际化 -操作
     */
    public static final String TABLE_OPERATE = I18NServer.toLocale(
            "plugins_common_term_operate");
    /**
     * 国际化 -详情
     */
    public static final String OPERATE_DETAIL = I18NServer.toLocale(
            "plugins_common_term_operate_task_detail");
}

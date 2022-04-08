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

/**
 * 节点管理常量定义
 *
 * @since 2020-10-15
 */
public class NodeManagerContent {
    /**
     * 国际化: 操作成功
     */
    public static final String RESPONSE_CODE = "SysPerf.Success";

    /**
     * ID列下标
     */
    public static final int ID_COLUMN_INDEX = 0;

    /**
     * 国际化: 节点管理
     */
    public static final String NODE_MANAGER_DIC = TuningI18NServer.toLocale("plugins_hyper_tuner_node_Title_nodeDic");

    /**
     * ID
     */
    public static final String USER_LABEL_USER_ID = "ID";

    /**
     * 国际化: 节点名称
     */
    public static final String NODE_TABLE_NODENAME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_nodename");

    /**
     * 国际化: 节点状态
     */
    public static final String NODE_TABLE_NODESTATUS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_nodestatus");

    /**
     * 国际化: 节点IP
     */
    public static final String NODE_TABLE_NODEIP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_nodeip");

    /**
     * 国际化: 节点PORT
     */
    public static final String NODE_TABLE_NODEPORT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_port");

    /**
     * 国际化: 用户名
     */
    public static final String NODE_TABLE_USERNAME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_username");

    /**
     * 国际化: 安装路径
     */
    public static final String NODE_TABLE_INSTALLPATH = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_installpath");

    /**
     * 国际化: 运行目录
     */
    public static final String NODE_TABLE_RUNDIR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_rundir");

    /**
     * 国际化: 日志目录
     */
    public static final String NODE_TABLE_LOGDIR = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_logdir");

    /**
     * 国际化: 安装路径
     */
    public static final String NODE_TABLE_OPER = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_oper");

    /**
     * 国际化: 表格提示
     */
    public static final String NODE_TABLE_TIPS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_tips");

    /**
     * 国际化: 表格提示
     */
    public static final String NODE_TABLE_TIPS1 = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_tips1");

    /**
     * 国际化: 修改
     */
    public static final String NODE_TABLE_UPDATE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_update");

    /**
     * 国际化: 删除
     */
    public static final String NODE_TABLE_DELETE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_delete");

    /**
     * 国际化: 安装日志
     */
    public static final String NODE_TABLE_INSTALL_LOG = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_table_installLog");

    /**
     * 国际化: 添加节点
     */
    public static final String NODE_MANAGER_ADD = TuningI18NServer.toLocale("plugins_hyper_tuner_node_add");

    /**
     * 国际化: 修改节点
     */
    public static final String NODE_MANAGER_UPDATE = TuningI18NServer.toLocale("plugins_hyper_tuner_node_update");

    /**
     * 国际化: 删除节点
     */
    public static final String NODE_MANAGER_DELETE = TuningI18NServer.toLocale("plugins_hyper_tuner_node_delete");

    /**
     * 国际化: 安装日志
     */
    public static final String NODE_INSTALL_LOG = TuningI18NServer.toLocale("plugins_hyper_tuner_node_install_log");

    /**
     * 国际化 确认。
     */
    public static final String TERM_OPERATE_OK = CommonI18NServer.toLocale("common_term_operate_ok");

    /**
     * 国际化 取消。
     */
    public static final String TERM_OPERATE_CANCEL = CommonI18NServer.toLocale("common_term_operate_cancel");
    /**
     * 国际化 确认。
     */
    public static final String NODE_MANAGER_FINGERPRINTS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_add_fingerprints");

    /**
     * 国际化: 节点安装日志
     */
    public static final String NODE_INSTALL_DIALOG = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_install_dialog");

    /**
     * 国际化: 节点安装日志
     */
    public static final String NODE_INSTALL_DIADUP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_install_diaDup");

    /**
     * 国际化: 节点安装日志
     */
    public static final String NODE_INSTALL_DIADOWN = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_install_diaDown");

    /**
     * 国际化: 节点安装日志
     */
    public static final String NODE_INSTALL_DIACANCEL = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_install_diaCancel");

    /**
     * 国际化: 操作成功
     */
    public static final String OPER_SUCESS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_oper_success");

    /**
     * 国际化: 操作失敗
     */
    public static final String OPER_FAILED = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_node_oper_failed");
}

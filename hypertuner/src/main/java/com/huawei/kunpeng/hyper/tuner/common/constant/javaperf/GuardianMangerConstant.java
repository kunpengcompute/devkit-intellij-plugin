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
 * Java性能分析
 * 目标环境管理 相关常量定义
 *
 * @since 2021-07-10
 */
public class GuardianMangerConstant {
    /**
     * 内部通信证书 面板展示名称
     */
    public static final String DISPLAY_NAME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage");
    /**
     * ID列下标
     */
    public static final int ID_COLUMN_INDEX = 0;
    /**
     * 表头：ID
     */
    public static final String TABLE_COL_ID = "ID";
    /**
     * 表头：名称
     */
    public static final String TABLE_COL_NAME = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_name");
    /**
     * 表头：状态
     */
    public static final String TABLE_COL_STATUS = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_status");
    /**
     * 表头：状态-在线
     */
    public static final String TABLE_COL_STATUS_CONNECTED = "CONNECTED";
    /**
     * 表头：IP
     */
    public static final String TABLE_COL_IP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_ip");
    /**
     * 表头：PORT
     */
    public static final String TABLE_COL_PORT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_port");
    /**
     * 表头：USER
     */
    public static final String TABLE_COL_USER = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_owner");
    /**
     * 表头：OPERATION
     */
    public static final String TABLE_COL_OPERATION = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_oper");
    /**
     * 操作列：重启
     */
    public static final String TABLE_COL_RECONNECT = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_reconnect");
    /**
     * 操作列：删除
     */
    public static final String TABLE_COL_DELETE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_delete");
    /**
     * 状态：创建中
     */
    public static final String TABLE_COL_CREATEING = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_status_creating");
    /**
     * 操作列：在线
     */
    public static final String TABLE_COL_CONNECTED = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_status_connected");
    /**
     * 操作列：离线
     */
    public static final String TABLE_COL_DISCONNECTED = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_status_disconnected");
    /**
     * 操作列：添加目标环境Title
     */
    public static final String GUARDIAN_ADD_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_add_title");
    /**
     * 操作列：添加目标环境Title
     */
    public static final String GUARDIAN_ADD_TITLE_HELP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_add_title_help");
    /**
     * 操作列：重启目标环境Title
     */
    public static final String GUARDIAN_RESTART_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_restart_title");
    /**
     * 操作列：删除目标环境Title
     */
    public static final String GUARDIAN_DELETE_TITLE = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardian_delete_title");

    /**
     * 删除目标环境提示
     */
    public static final String GUARDIAN_PARTIAL_DELETE_TIP = TuningI18NServer.toLocale(
            "plugins_hyper_tuner_javaperf_guardianManage_partial_deleteTip");
}

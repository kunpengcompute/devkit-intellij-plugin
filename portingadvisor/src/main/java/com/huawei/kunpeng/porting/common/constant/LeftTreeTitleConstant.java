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

package com.huawei.kunpeng.porting.common.constant;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 左侧面板列表常量
 *
 * @since 2021-01-20
 */
public class LeftTreeTitleConstant {
    /**
     * 软件迁移评估
     */
    public static final String SOFTWARE_PORTING_ASSESSMENT = I18NServer.toLocale("plugins_port_migration_appraise");

    /**
     * 源码迁移
     */
    public static final String SOURCE_CODE_PORTING = I18NServer.toLocale("plugins_porting_source_code_title");

    /**
     * 软件包重构
     */
    public static final String SOFTWARE_REBUILDING = I18NServer.toLocale("plugins_porting_software_rebuilding_title");

    /**
     * 专项软件迁移
     */
    public static final String DEDICATED_SOFTWARE_PORTING = I18NServer.toLocale(
            "plugins_porting_tip_software_migration");

    /**
     * 增强功能
     */
    public static final String ENHANCED_FUNCTION = I18NServer.toLocale("plugins_porting_enhanced_function");

    /**
     * 新建任务
     */
    public static final String COMMON_NEW_TASK = I18NServer.toLocale("plugins_porting_common_menu_new_task");

    /**
     * 情况历史报告
     */
    public static final String CLEAR_REPORTS = I18NServer.toLocale("plugins_porting_common_menu_clear_report");

    /**
     * 无报告词条
     */
    public static final String NO_REPORTS = I18NServer.toLocale("plugins_porting_common_menu_no_report");

}

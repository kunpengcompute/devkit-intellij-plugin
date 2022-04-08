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

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;

/**
 * porting constant
 *
 * @since 2.3.T10
 */
public class PortingIDEConstant extends IDEConstant {
    /**
     * 插件名称
     */
    public static final String PLUGIN_NAME = "Kunpeng-DevKit-IDE-porting-advisor-plugin";

    /**
     * 模块
     */
    public static final String TOOL_NAME_DEP = "dep";

    /**
     * 模块
     */
    public static final String TOOL_NAME_PORTING = "porting";

    /**
     * porting页面包地址
     */
    public static final String PORTING_PLUGIN_NAME = "/webview/porting.zip";

    /**
     * porting插件软件包重构报告页面模板地址
     */
    public static final String WEB_VIEW_ANALYSIS_REPORT_TEMPLATE_HTML = "analysis_report_template.html";

    /**
     * porting扫描支持的文件类型
     */
    public static final String[] SOURCE_EXTENSIONS = new String[] {"zip", "tar", "tar.gz", "tar.bz2"};

    /**
     * JCEF的大版本
     */
    public static final int JCEF_MAJOR_VERSION = 202;

    /**
     * JCEF的小版本
     */
    public static final int JCEF_MINOR_VERSION = 2;

    /**
     * 左侧树基础Panel
     */
    public static final String BASE_PORTING_PANEL = "BasePortingPanel";

    /**
     * 该用户的目前历史报告书
     */
    public static final String REPORTS_NUM = "ReportsNum";

    /**
     * 左侧树面板id
     */
    public static final String PORTING_ADVISOR_TOOL_WINDOW_ID = "Porting Advisor";

    /**
     * 磁盘空间提示阈值
     */
    public static final double THRESHOLD_VALUE = 1.0;

    /**
     * 需要兼容的server版本
     */
    public static final String COMPATIBLE_SERVER_VERSION = "Porting Advisor 2.2.T2";

    /**
     * url文件urlConfig.json路径
     */
    public static final String URL_CONFIG_PATH = "/assets/urlConfig.json";

    /**
     * 吊销列表Map key值： cert name
     */
    public static final String CRL_MAP_KEY = "CRL Cert Name";
}

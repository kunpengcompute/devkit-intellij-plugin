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

package com.huawei.kunpeng.hyper.tuner.common.constant;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;

/**
 * 全局常量
 *
 * @since 2020-09-25
 */
public class TuningIDEConstant<T> extends IDEConstant {
    /**
     * 插件名称
     */
    public static final String PLUGIN_NAME = "Kunpeng-DevKit-IDE-hyper-tuner-plugin";

    /**
     * 工具名称
     */
    public static final String TOOL_NAME_TUNING = "tuning";

    /**
     * 工具名称
     */
    public static final String SYS = "sys";

    /**
     * 工具名称
     */
    public static final String JAVA = "java";

    /**
     * 请求成功返回
     */
    public static final String SUCCESS_CODE = "UserManage.Success";

    /**
     * 窗口名称
     */
    public static final String HYPER_TUNER_TOOL_WINDOW_ID = "Hyper Tuner";

    /**
     * url文件urlConfig.json路径
     */
    public static final String URL_CONFIG_PATH = "/assets/urlConfig.json";

    /**
     * tuning插件webview页面地址
     */
    public static final String TUNING_WEB_VIEW_PATH = "webview";

    /**
     * tuning插件webview页面地址
     */
    public static final String TUNING_WEB_VIEW_INDEX_HTML = "index.html";

    /**
     * tuning页面包地址
     */
    public static final String TUNING_PLUGIN_NAME = "/webview/tuning.zip";

    /**
     * tuning插件webview页面地址
     */
    public static final String JAVA_WEB_VIEW_INDEX_HTML = "java.index.html";

    /**
     * tuning页面包地址
     */
    public static final String JAVA_PLUGIN_NAME = "/webview/javaPerf.zip";

    /**
     * 插件名称
     */
    public static final String TUNING_NAME = "Kunpeng-DevKit-IDE-hyper-tuner-plugin";

    /**
     * tuning插件webview页面地址
     */
    public static final String JAVA_WEB_VIEW_GLOBLE_STATE = "javaWebViewGlobleState";

    /**
     * 系统性能分析
     */
    public static final String SYSTEM_PROFILER = TuningI18NServer.toLocale("plugins_hyper_tuner_sysperf_system");

    /**
     * java性能分析
     */
    public static final String JAVA_PROFILER = TuningI18NServer.toLocale("plugins_hyper_tuner_sysperf_javaperf");
}

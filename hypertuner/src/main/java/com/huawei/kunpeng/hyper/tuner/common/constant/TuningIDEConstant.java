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
     * 请求成功返回
     */
    public static final String SUCCESS_CODE = "UserManage.Success";

    /**
     * 窗口名称
     */
    public static final String HYPER_TUNER_TOOL_WINDOW_ID = "Hyper Tuner";

    /**
     * tuning插件webview页面地址
     */
    public static final String TUNING_WEB_VIEW_PATH = "webview";

    /**
     * tuning插件集成nginx目录
     */
    public static final String TUNING_NGINX_PATH = "nginx";

    /**
     * 工具名称
     */
    public static final String SYS = "sys";

    /**
     * 工具名称
     */
    public static final String JAVA = "java";

    /**
     * tuning插件登录页面webview页面地址
     */
    public static final String TUNING_LOGIN_WEB_VIEW_INDEX_HTML = "/webview/index.html";

    /**
     * tuning插件静态webview代理html文件地址
     */
    public static final String WEB_VIEW_INDEX_HTML = "/webview/tuning/sys/index.html";

    /**
     * nginx 资源包地址
     */
    public static final String NGINX_PLUGIN_NAME = "/nginx/nginx-1.18.0.zip";

    /**
     * nginx mac资源包地址
     */
    public static final String NGINX_MAC_PLUGIN_NAME = "/nginx/nginx-mac.zip";


    /**
     * 插件名称
     */
    public static final String TUNING_NAME = "Kunpeng-DevKit-IDE-hyper-tuner-plugin";


    /**
     * tuning页面包地址
     */
    public static final String TUNING_PLUGIN_NAME= "/webview/tuning.zip";

    /**
     * url文件urlConfig.json路径
     */
    public static final String URL_CONFIG_PATH = "/assets/urlConfig.json";
}

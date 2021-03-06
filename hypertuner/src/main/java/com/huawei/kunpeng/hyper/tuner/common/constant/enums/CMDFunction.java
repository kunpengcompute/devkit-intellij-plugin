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

package com.huawei.kunpeng.hyper.tuner.common.constant.enums;

import com.huawei.kunpeng.hyper.tuner.webview.tuning.handler.CommonHandler;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.handler.JavaPerfHandler;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionManager;

/**
 * js与java交互的函数
 *
 * @since 2020-11-13
 */
public enum CMDFunction {
    NULL("null", null),
    GET_DATA("getData", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CHECK_UPLOAD_FILE("checkUploadFileIntellij", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UPLOAD_FILE("uploadFileIntellij", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SHOW_INFO_BOX("showInfoBox", FunctionManager.getFunctionHandler(CommonHandler.class)),
    NAVIGATE_TO_PANEL("navigateToPanel", FunctionManager.getFunctionHandler(CommonHandler.class)),
    READ_URL_CONFIG("readURLConfig", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CLOSE_PANEL("closePanel", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CLOSE_PAGE("closePage", FunctionManager.getFunctionHandler(CommonHandler.class)),
    INTELLIJ_EXECUTES_UPLOAD("intellijExcuteUpload", FunctionManager.getFunctionHandler(CommonHandler.class)),
    OPEN_FILE_INFO("intellijDepPackage", FunctionManager.getFunctionHandler(CommonHandler.class)),
    OPEN_SOME_NODE("openSomeNode", FunctionManager.getFunctionHandler(CommonHandler.class)),
    OPEN_NEW_PAGE("openNewPage", FunctionManager.getFunctionHandler(CommonHandler.class)),
    GET_GLOBLE_STATE("getGlobleState", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    SET_GLOBLE_STATE("setGlobleState", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    READ_CONFIG("readConfig", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    CHECK_THREAD_DUMP_REPORT_THRESHOLD("checkThreaddumpReportThreshold",
            FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    CHECK_HEAPDUMP_REPORT_THRESHOLD("checkHeapdumpReportThreshold",
            FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    CHECK_GC_LOG_REPORT_THRESHOLD("checkGclogReportThreshold",
            FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    STOP_PROFILING_INTELLIJ("stopProfilingIntellij", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    STOP_PROFILING("stopProfiling", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    CHECK_PROFILING_CURRENT_STATA("checkProfilingCurrentStata",
            FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    EXPORT_PROFILING_INTELLIJ("exportProfilingIntellij", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    EXPORT_PROFILING("exportProfiling", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    SHOW_JAVAINFO_BOX("showJavaPerfInfoBox", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    UPDATE_REPORT_CONFIG("updateReportConfig", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    OPEN_JAVA_PROFILING_PAGE("openJavaProfilingPage", FunctionManager.getFunctionHandler(JavaPerfHandler.class)),
    OPEN_HYPERLINKS("openHyperlinks", FunctionManager.getFunctionHandler(CommonHandler.class)),
    DOWNLOAD_FILE("downloadFile", FunctionManager.getFunctionHandler(JavaPerfHandler.class));
    private final String functionName;

    private final FunctionHandler functionHandler;

    CMDFunction(String functionName, FunctionHandler functionHandler) {
        this.functionName = functionName;
        this.functionHandler = functionHandler;
    }

    /**
     * 通过延伸信息functionName获取CMDFunction类的一个枚举实例
     *
     * @param functionName 函数名
     * @return CMDFunction
     */
    public static CMDFunction getStatusByValue(String functionName) {
        for (CMDFunction function : CMDFunction.values()) {
            if (function.functionName().equals(functionName)) {
                return function;
            }
        }

        return NULL;
    }

    /**
     * 获取函数名
     *
     * @return String
     */
    public String functionName() {
        return functionName;
    }

    /**
     * 获取函数对象实例
     *
     * @return FunctionHandler
     */
    public FunctionHandler functionHandler() {
        return functionHandler;
    }
}

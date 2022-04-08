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

package com.huawei.kunpeng.porting.common.constant.enums;

import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionManager;
import com.huawei.kunpeng.porting.webview.handler.AnalysisFunctionHandler;
import com.huawei.kunpeng.porting.webview.handler.CacheLineAlignmentFunctionHandler;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;
import com.huawei.kunpeng.porting.webview.handler.DepReportFunctionHandler;
import com.huawei.kunpeng.porting.webview.handler.EnhancedFunctionHandler;
import com.huawei.kunpeng.porting.webview.handler.MigrationAppraiseHandler;
import com.huawei.kunpeng.porting.webview.handler.MigrationCenterFunctionHandler;
import com.huawei.kunpeng.porting.webview.handler.PortSourceFunctionHandler;
import com.huawei.kunpeng.porting.webview.handler.ReportFunctionHandler;

/**
 * js与java交互的函数
 *
 * @since 2020-11-13
 */
public enum CMDFunction {
    NULL("null", null),
    GET_DATA("getData", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UPLOAD_PROCESS("uploadProcess", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CHECK_UPLOAD_FILE("checkUploadFileIntellij", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UPLOAD_FILE("uploadFileIntellij", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UPLOAD_MULTIPLE_FILE("uploadMultipleFiles", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SHOW_INFO_BOX("showInfoBox", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SHOW_NO_PERMISSION_FAQ_TIP("noPermissionFaqTip", FunctionManager.getFunctionHandler(CommonHandler.class)),
    OPEN_HYPERLINKS("openHyperlinks", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SHOW_INTELLIJ_DIALOG("showIntellijDialog", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SHOW_PROGRESS("showProgress", FunctionManager.getFunctionHandler(MigrationCenterFunctionHandler.class)),
    DOWNLOAD_REPORT("downloadReport", FunctionManager.getFunctionHandler(ReportFunctionHandler.class)),
    UNABLE_DOWN_INFO_BOX("unableDownInfoBox", FunctionManager.getFunctionHandler(ReportFunctionHandler.class)),
    SOURCE_INFO_BOX("sourceInfoBox", FunctionManager.getFunctionHandler(ReportFunctionHandler.class)),
    ANALYSIS_PROCESS("analysisProcess", FunctionManager.getFunctionHandler(AnalysisFunctionHandler.class)),
    ANALYSIS_DOWNLOAD_PACKAGE("downloadRebuildPkg", FunctionManager.getFunctionHandler(AnalysisFunctionHandler.class)),
    ANALYSIS_DOWNLOAD_HTML("downloadRebuildHTML", FunctionManager.getFunctionHandler(AnalysisFunctionHandler.class)),
    DOWNLOAD_REPORT_HTML("downloadReportHtml", FunctionManager.getFunctionHandler(ReportFunctionHandler.class)),
    CODE_SUGGESTING_OPT("codeSuggestingOpt", FunctionManager.getFunctionHandler(ReportFunctionHandler.class)),
    SHOW_DISK_MESSAGE("showDiskMessage", FunctionManager.getFunctionHandler(MigrationCenterFunctionHandler.class)),
    GET_GLOBLE_STATE("getGlobleState", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SET_GLOBLE_STATE("setGlobleState", FunctionManager.getFunctionHandler(CommonHandler.class)),
    READ_URL_CONFIG("readUrlConfig", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CLOSE_PANEL("closePanel", FunctionManager.getFunctionHandler(CommonHandler.class)),
    WEAK_COMPILE_PROGRESS("weakCompileProgress", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    BYTE_ALIGN_PROGRESS("byteAlignProgress", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    CLEAR_ENHANCE_REPORT("clearEnhanceReport", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    WEAK_CHECK_PROGRESS("weakCheckProgress", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    BC_CHECK_PROGRESS("bcCheckProgress", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    DOWNLOAD_FILE("downloadFile", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    DOWNLOAD_DEP_REPORT("downloadDepReport",
        FunctionManager.getFunctionHandler(DepReportFunctionHandler.class)),
    DOWNLOAD_DEP_REPORT_HTML("downloadDepReportHtml",
        FunctionManager.getFunctionHandler(DepReportFunctionHandler.class)),
    OPEN_NEW_PAGE("openNewPage", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    CREATE_PORT_CHECK_TREE("createPortCheckTree",
        FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    OPEN_WEAK_REPORT("openWeakReport", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    GO_ENHANCED_REPORT_DETAIL("goEnhancedReportDetail",
        FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    PRECHECK_PROGRESS("precheckProgress", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    DOWNLOAD_BC_FILES("downloadBcFiles", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    SHOW_LOCK_BOX("showLockBox", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    PORT_SOURCE_SCAN("analsysProgress", FunctionManager.getFunctionHandler(PortSourceFunctionHandler.class)),
    RIGHT_CLICK_UPLOAD_PORTING_FILE("rightClickUploadPortingFile",
            FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    GET_GCC("getGCC", FunctionManager.getFunctionHandler(PortSourceFunctionHandler.class)),
    OPEN_FILE_INFO("intellijDepPackage", FunctionManager.getFunctionHandler(CommonHandler.class)),
    MIGRATION_SCAN_PROCESS("scanProcess", FunctionManager.getFunctionHandler(MigrationAppraiseHandler.class)),
    SOURCE_REPORT_DOWNLOAD("sourceReportDownload", FunctionManager.getFunctionHandler(PortSourceFunctionHandler.class)),
    CACHE_LINE_PROGRESS("cacheLineProgress",
        FunctionManager.getFunctionHandler(CacheLineAlignmentFunctionHandler.class)),
    CREATE_CACHE_CHECK_TREE("createCacheCheckTree", FunctionManager.getFunctionHandler(EnhancedFunctionHandler.class)),
    READ_VERSION_CONFIG("readVersionConfig", FunctionManager.getFunctionHandler(CommonHandler.class));

    private final String functionName;

    private final FunctionHandler functionHandler;

    CMDFunction(String functionName, FunctionHandler functionHandler) {
        this.functionName = functionName;
        this.functionHandler = functionHandler;
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
}

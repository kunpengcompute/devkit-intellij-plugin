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
    OPEN_NEW_PAGE("openNewPage", FunctionManager.getFunctionHandler(CommonHandler.class)),
    OPEN_HYPERLINKS("openHyperlinks", FunctionManager.getFunctionHandler(CommonHandler.class)),
    DOWNLOAD_BASE64_PNG("downloadBase64Png", FunctionManager.getFunctionHandler(CommonHandler.class)),
    DOWNLOAD_FILE_BY_BLOB("downloadFileByBlob", FunctionManager.getFunctionHandler(CommonHandler.class)),
    DOWNLOAD_FILE_BY_JSON("downloadFileByJson", FunctionManager.getFunctionHandler(CommonHandler.class)),
    DOWNLOAD_JAVA_OPER_LOG("downloadJavaOperLog", FunctionManager.getFunctionHandler(CommonHandler.class)),
    DOWNLOAD_CERTIFICATE("downloadCertificate", FunctionManager.getFunctionHandler(CommonHandler.class)),
    READ_URL_CONFIG("readUrlConfig", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CLOSE_PANEL("closePanel", FunctionManager.getFunctionHandler(CommonHandler.class)),
    READ_FINGER("readFinger", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SAVE_FINGER("saveFinger", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CHECK_CONN("checkConn", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SHOW_INFO_BOX("showInfoBox", FunctionManager.getFunctionHandler(CommonHandler.class)),
    READ_CONFIG("readConfig", FunctionManager.getFunctionHandler(CommonHandler.class)),
    SAVE_CONFIG("saveConfig", FunctionManager.getFunctionHandler(CommonHandler.class)),
    OPEN_URL_IN_BROWSER("openUrlInBrowser", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UPGRADE("upgrade", FunctionManager.getFunctionHandler(CommonHandler.class)),
    HIDE_TERMINAL("hideTerminal", FunctionManager.getFunctionHandler(CommonHandler.class)),
    LOGIN_SUCCESS("loginSuccess", FunctionManager.getFunctionHandler(CommonHandler.class)),
    INSTALL("install", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UNINSTALL("uninstall", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CLEAN_CONFIG("cleanConfig", FunctionManager.getFunctionHandler(CommonHandler.class)),
    CLOSE_ALL_PANEL("closeAllPanel", FunctionManager.getFunctionHandler(CommonHandler.class)),
    UPLOAD_PRIVATE_KEY("uploadPrivateKey", FunctionManager.getFunctionHandler(CommonHandler.class));

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

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

package com.huawei.kunpeng.intellij.ui.enums;

/**
 * IDE自定义的所有panel
 *
 * @since 2020-09-25
 */
public enum Panels {
    LEFT_TREE_CONFIG("LEFT_TREE_CONFIG"),
    LEFT_TREE_LOGIN("LEFT_TREE_LOGIN"),
    LEFT_TREE_REPORTS("LEFT_TREE_REPORTS"),
    LEFT_TREE_LOADING("LEFT_TREE_LOADING"),
    FILE_SAVE_AS("FILE_SAVE_AS"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    SERVER_CONFIG("SERVER_CONFIG"),
    INSTALL("INSTALL"),
    UNINSTALL("UNINSTALL"),
    UPGRADE("UPGRADE"),
    WHITELIST_MANAGEMENT("WHITELIST_MANAGEMENT"),
    PORTING_RIGHT_CLICK("PORTING_RIGHT_CLICK"),
    PORTING("PORTING"),
    WEB_SERVER_CERTIFICATE("WEB_SERVER_CERTIFICATE"),
    EXPORT_WEB_SERVER_CERTIFICATE("EXPORT_WEB_SERVER_CERTIFICATE"),
    IMPORT_WEB_SERVER_CERTIFICATE("IMPORT_WEB_SERVER_CERTIFICATE"),
    ADD_WEAK_PWD("ADD_WEAK_PWD"),
    WEAK_PWD_SET("WEAK_PWD_SET"),
    ERROR_GUIDE("ERROR_GUIDE"),
    DEL_WEAK_PWD("DEL_WEAK_PWD"),
    CHANGE_PWD("CHANGE_PWD"),
    SAVE_CONFIG("SAVE_CONFIG"),
    INSTALL_SERVER_CONFIRM("INSTALL_SERVER_CONFIRM"),
    DELETE_PROJECT("DELETE_PROJECT"),
    DELETE_TASK("DELETE_TASK"),
    LEFT_TREE_SYSPERF("LEFT_TREE_SYSPERF"),
    CRL_DETAIL("CRL_DETAIL");
    private final String panelName;

    Panels(String panelName) {
        this.panelName = panelName;
    }

    /**
     * 获取面板名称
     *
     * @return 返回面板名称
     */
    public String panelName() {
        return panelName;
    }
}

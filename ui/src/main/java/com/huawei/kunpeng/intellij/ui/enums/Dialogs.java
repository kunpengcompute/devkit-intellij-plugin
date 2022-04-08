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
 * IDE自定义的所有弹框
 *
 * @since 2020-09-25
 */
public enum Dialogs {
    FILE_SAVE_AS("FILE_SAVE_AS"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    INSTALL_DISCLAIMER("INSTALL_DISCLAIMER"),
    SERVER_CONFIG("SERVER_CONFIG"),
    INSTALL("INSTALL"),
    UNINSTALL("UNINSTALL"),
    UPGRADE("UPGRADE"),
    ADD_WEAK_PWD("ADD_WEAK_PWD"),
    EXPORT_WEB_SERVER_CERTIFICATE("EXPORT_WEB_SERVER_CERTIFICATE"),
    IMPORT_WEB_SERVER_CERTIFICATE("IMPORT_WEB_SERVER_CERTIFICATE"),
    PORTING("PORTING"),
    ERROR_GUIDE("ERROR_GUIDE"),
    CHANGE_PWD("CHANGE_PWD"),
    FINGER_TIP("FINGER_TIP"),
    CONFIG_SAVE_CONFIRM("CONFIG_SAVE_CONFIRM"),
    INSTALL_SERVER_CONFIRM("INSTALL_SERVER_CONFIRM"),
    SOURCE_PORTING_ENV_CHECK("SOURCE_PORTING_ENV_CHECK"),
    CRL_DETAIL("CRL_DETAIL"),
    DELETE_CRL_FILE("DELETE_CRL_FILE"),
    DUPLICATE_CRL_FILE("DUPLICATE_CRL_FILE");

    private final String dialogName;

    Dialogs(String dialogName) {
        this.dialogName = dialogName;
    }

    /**
     * 获取弹框名称
     *
     * @return 返回弹框名称
     */
    public String dialogName() {
        return dialogName;
    }
}

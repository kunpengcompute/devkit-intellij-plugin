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
    SERVER_CONFIG("SERVER_CONFIG"),
    INSTALL("INSTALL"),
    UNINSTALL("UNINSTALL"),
    UPGRADE("UPGRADE"),
    CHANGE_PWD("CHANGE_PWD"),
    SAVE_CONFIG("SAVE_CONFIG"),
    INSTALL_SERVER_CONFIRM("INSTALL_SERVER_CONFIRM");
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

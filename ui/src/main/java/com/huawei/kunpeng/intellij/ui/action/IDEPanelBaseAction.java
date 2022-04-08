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

package com.huawei.kunpeng.intellij.ui.action;

/**
 * IDE面板事件基类，用于面板和事件分离
 *
 * @since 1.0.0
 */
public class IDEPanelBaseAction {
    /**
     * 插件安装升级时的路径
     */
    protected static final String TMP_PATH = "/tmp/intellij_";

    /**
     * 文件夹名称
     */
    protected static final String TMP_FORMAT = "yy_MM_dd_HH_mm_ss";
}


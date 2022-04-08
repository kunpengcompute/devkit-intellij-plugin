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

package com.huawei.kunpeng.intellij.ui.dialog;

/**
 * IDE弹框基类
 *
 * @since 1.0.0
 */
public interface IDEBaseDialog {
    /**
     * 弹框是否有效
     *
     * @return boolean
     */
    boolean isValid();

    /**
     * 销毁弹框
     */
    void dispose();

    /**
     * 显示弹框
     */
    void displayPanel();

    /**
     * 获取弹框名称
     *
     * @return String
     */
    String getDialogName();

    /**
     * 刷新弹框
     */
    void updateDialog();
}

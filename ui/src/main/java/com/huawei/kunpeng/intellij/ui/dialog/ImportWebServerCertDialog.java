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

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * ImportWebServerCertDialog
 *
 * @since 2021-09-08
 */
public class ImportWebServerCertDialog extends IdeaDialog {
    /**
     * MB_TO_KB
     */
    protected static final int MB_TO_KB = 1024;

    @Override
    protected void onOKAction() {
    }

    @Override
    protected void onCancelAction() {
    }

    /**
     * 添加弱口令弹框内容
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }
}

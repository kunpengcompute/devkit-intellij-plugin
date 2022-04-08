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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 操作日志实现面板
 *
 * @since 2020-10-23
 */
public class CancelDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param dialogName 弹框名称
     * @param panel 需要展示的面板之一
     */
    public CancelDialog(IDEBasePanel panel, String dialogName) {
        this.title =  CommonI18NServer.toLocale("common_title_whitelistManage");
        this.dialogName = dialogName;
        this.mainPanel = panel;
        // 无位置信息时居中显示
        this.rectangle = rectangle;
        this.resizable = true;
        // 初始化弹框内容
        initDialog();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return null;
    }

    @Override
    protected void onOKAction() {
    }

    @Override
    protected void onCancelAction() {
    }
}

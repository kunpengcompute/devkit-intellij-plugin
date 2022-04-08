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
import com.huawei.kunpeng.intellij.ui.panel.FileSaveAsPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.swing.JComponent;

/**
 * The class FileSaveAsDialog: 上传文件重复时，弹出另存为对应的窗口
 *
 * @since v1.0
 */
public class FileSaveAsDialog extends IdeaDialog {
    private static String fileSaveAsDialogName = "FILE_SAVE_AS";

    private String fileName;

    /**
     * FileSaveAsDialog
     *
     * @param title title
     * @param panel panel
     * @param resizable resizable
     * @param fileName fileName
     */
    public FileSaveAsDialog(String title, IDEBasePanel panel, boolean resizable, String fileName) {
        this.title = CommonI18NServer.toLocale("common_save_as");
        this.dialogName = fileSaveAsDialogName;
        this.mainPanel = panel;
        this.resizable = resizable;
        this.fileName = fileName;
        setOKAndCancelName(CommonI18NServer.toLocale("common_term_operate_confirm"),
                CommonI18NServer.toLocale("common_term_operate_cancel"));
        // 初始化弹框内容
        initDialog();
    }

    /**
     * FileSaveAsDialog
     *
     * @param title title
     * @param panel panel
     */
    public FileSaveAsDialog(String title, IDEBasePanel panel) {
        this(title, panel, false, "file");
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    protected void onOKAction() {
        if (mainPanel instanceof FileSaveAsPanel) {
            this.fileName = ((FileSaveAsPanel) mainPanel).getFileName().getText();
        }
    }

    /**
     *  异常信息集中处理
     *
     * @return 异常集合
     */
    protected List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = this.mainPanel.doValidateAll();
        return result;
    }

    @Override
    protected void onCancelAction() {
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}

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

package com.huawei.kunpeng.porting.action.setting.crl;

import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;
import com.huawei.kunpeng.porting.ui.dialog.crl.DeleteCRLDialog;
import com.huawei.kunpeng.porting.ui.panel.CertRovListPanel;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * The class: DeleteCRLAction
 *
 * @since 2021-8-12
 */
public class DeleteCRLAction extends IDETableCommonAction {
    /**
     * 用户ID列下标
     */
    private static final int CERT_NAME_INDEX = 0;

    private CertRovListPanel srcPanel;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public DeleteCRLAction(JTable targetTable, CertRovListPanel srcPanel) {
        super(targetTable);
        this.srcPanel = srcPanel;
    }

    /**
     * 点击删除CRL文件响应
     *
     * @param anActionButton anActionButton
     */
    @Override
    public void run(AnActionButton anActionButton) {
        int row = targetTable.getSelectedRow();
        String certName = targetTable.getValueAt(row, CERT_NAME_INDEX).toString();
        DeleteCRLDialog deleteCRLDialog = new DeleteCRLDialog(certName, srcPanel);
        deleteCRLDialog.setSize(570, 140);
        deleteCRLDialog.displayPanel();
    }
}

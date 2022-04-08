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
import com.huawei.kunpeng.porting.ui.dialog.crl.ImportCRLDialog;
import com.huawei.kunpeng.porting.ui.panel.CertRovListPanel;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.ImportCRLPanel;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * The class: ImportCRLAction
 *
 * @since 2021-8-12
 */
public class ImportCRLAction extends IDETableCommonAction {
    private CertRovListPanel srcPanel;

    /**
     * Constructor
     *
     * @param targetTable targetTable
     * @param srcPanel    srcPanel
     */
    public ImportCRLAction(JTable targetTable, CertRovListPanel srcPanel) {
        super(targetTable);
        this.srcPanel = srcPanel;
    }

    @Override
    public void run(AnActionButton anActionButton) {
        ImportCRLPanel importCRLPanel = new ImportCRLPanel();
        ImportCRLDialog importDialog = new ImportCRLDialog(importCRLPanel, srcPanel);
        importDialog.setSize(570, 140);
        importDialog.displayPanel();
    }
}

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

package com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd;

import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.user.UseManagerDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.weakpwdpanel.TuningWeakPwdDelPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.weakpwdpanel.TuningWeakPwdSetPanel;
import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表给修改操作Action
 *
 * @since 2020-11-2
 */
public class WeakPwdTableDeletedAction extends IDETableCommonAction {
    /**
     * 弱口令下标
     */
    private static final int PWD_INDEX = 1;

    /**
     * ID下标
     */
    private static final int PWD_ID_INDEX = 0;

    /**
     * 主内容面板WeakPwdSetPanel
     */
    private TuningWeakPwdSetPanel srcPanel;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public WeakPwdTableDeletedAction(JTable targetTable, TuningWeakPwdSetPanel srcPanel) {
        super(targetTable);
        this.srcPanel = srcPanel;
    }

    @Override
    public void run(AnActionButton anActionButton) {
        Logger.info("Start Delete Weak PassWord.");
        int row = targetTable.getSelectedRow();
        // 获取选中行对应的弱口令
        String selectWeakPwd = targetTable.getValueAt(row, PWD_INDEX).toString();
        String selectWeakPwdID = targetTable.getValueAt(row, PWD_ID_INDEX).toString();
        UseManagerDialog dialog =
                new UseManagerDialog(
                        WeakPwdConstant.WEAK_PASSWORD_DEL_TITLE,
                        new TuningWeakPwdDelPanel(selectWeakPwd, selectWeakPwdID));
        dialog.setSize(570, 190);
        dialog.displayPanel();
        if (srcPanel != null) {
            srcPanel.updateTable();
        } else {
            Logger.error("The delWeakPwdPanel is null.");
        }
    }
}

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

package com.huawei.kunpeng.intellij.common.action;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表上下移动操作Action
 *
 * @since 2020-10-30
 */
public class IDETableMoveAction extends IDETableCommonAction {
    /**
     * 移动位移
     */
    private int offset = -1;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public IDETableMoveAction(JTable targetTable, boolean isMoveUp) {
        super(targetTable);
        if (!isMoveUp) {
            offset = 1;
        }
    }

    @Override
    public void run(AnActionButton anActionButton) {
        int oldLow = targetTable.getSelectedRow();
        targetTable.setRowSelectionInterval(oldLow + offset, oldLow + offset);
    }
}

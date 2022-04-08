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

package com.huawei.kunpeng.hyper.tuner.action.panel.user;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.user.UseManagerDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.AddAndModifyUserPanel;
import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表给修改操作Action
 *
 * @since 2020-10-30
 */
public class UserAddAction extends IDETableCommonAction {
    /**
     * 添加用户
     *
     * @param targetTable 目标表格
     * @param workSpacePath 目标表格
     */
    public UserAddAction(JTable targetTable, String workSpacePath) {
        super(targetTable);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        UseManagerDialog createUserDialog =
                new UseManagerDialog(
                        TuningUserManageConstant.TITLE_CREATE_USER,
                        new AddAndModifyUserPanel(true, TuningUserManageConstant.TITLE_CREATE_USER));
        createUserDialog.displayPanel();
    }
}

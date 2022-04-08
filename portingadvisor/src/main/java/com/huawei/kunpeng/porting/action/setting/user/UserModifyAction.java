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

package com.huawei.kunpeng.porting.action.setting.user;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.OPERATE_RESET;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.TITLE_CHANGE_USER;

import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.ui.dialog.user.UseManagerDialog;
import com.huawei.kunpeng.porting.ui.panel.userpanel.AddAndModifyUserPanel;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表给修改操作Action
 *
 * @since 2020-10-30
 */
public class UserModifyAction extends IDETableCommonAction {
    /**
     * 角色列下标
     */
    private static final int ROLE_INDEX = 2;

    /**
     * 用户ID列下标
     */
    private static final int ID_INDEX = 0;

    /**
     * 用户名称列下标
     */
    private static final int USER_NAME_INDEX = 1;

    /**
     * 工作空间
     */
    private String workSpacePath;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     * @param workSpacePath 工作空间
     */
    public UserModifyAction(JTable targetTable, String workSpacePath) {
        super(targetTable);
        this.workSpacePath = workSpacePath;
    }

    @Override
    public void run(AnActionButton anActionButton) {
        int row = targetTable.getSelectedRow();
        Object userID = targetTable.getValueAt(row, ID_INDEX);
        Object userName = targetTable.getValueAt(row, USER_NAME_INDEX);
        Object role = targetTable.getValueAt(row, ROLE_INDEX);

        String title = OPERATE_RESET;
        if (PortingUserManageConstant.USER_ROLE_ADMIN_FOR_I18N.equals(role)) {
            title = (TITLE_CHANGE_USER);
        }
        UseManagerDialog dialog = new UseManagerDialog(title,
            new AddAndModifyUserPanel(false, userName, userID, role, this.workSpacePath));
        dialog.displayPanel();
    }
}

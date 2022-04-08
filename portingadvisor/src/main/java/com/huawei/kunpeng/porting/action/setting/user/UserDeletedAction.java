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

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.OPERATE_DELETE_TITLE;

import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.ui.dialog.user.UseManagerDialog;
import com.huawei.kunpeng.porting.ui.panel.userpanel.DelUserPanel;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表给修改操作Action
 *
 * @since 2020-10-30
 */
public class UserDeletedAction extends IDETableCommonAction {
    /**
     * 用户ID列下标
     */
    private static final int ID_INDEX = 0;

    /**
     * 角色列下标
     */
    private static final int ROLE_INDEX = 2;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public UserDeletedAction(JTable targetTable) {
        super(targetTable);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        int row = targetTable.getSelectedRow();
        Object userID = targetTable.getValueAt(row, ID_INDEX);
        Object role = targetTable.getValueAt(row, ROLE_INDEX);
        if (PortingUserManageConstant.USER_ROLE_ADMIN_FOR_I18N.equals(role)) {
            Logger.info("admin is not to be delete!!");
            return;
        }
        UseManagerDialog dialog = new UseManagerDialog(OPERATE_DELETE_TITLE,
            new DelUserPanel(userID));
        dialog.displayPanel();
    }
}

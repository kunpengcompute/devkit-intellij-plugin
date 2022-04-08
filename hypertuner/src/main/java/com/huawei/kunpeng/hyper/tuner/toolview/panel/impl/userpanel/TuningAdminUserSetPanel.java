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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel;

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.OPERATE_RESET;
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_LABEL_NAME;
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_LABEL_ROLE;
import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_LABEL_USER_ID;

import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserAddAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserDeletedAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserModifyAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.userpanel.AdminUserSetPanel;

import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.TableUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * 系统性能分析-管理员用户管理界面
 *
 * @since 2020-10-6
 */
public class TuningAdminUserSetPanel extends AdminUserSetPanel {
    /**
     * 构造函数
     */
    public TuningAdminUserSetPanel() {
        // 初始化数据
        initPanel(new JPanel());
        registerComponentAction();
    }

    @Override
    public void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (action == null) {
            action = new UserManagerAction();
        }
        selectUserList(); // 查询用户数据。
        setTableModel(); // 设置表头模型。
        userTable = new JBTable(tableModel);
        // 创建表头
        createUserTableColName();
        createTableInfo();
        addListenerToTable();
        toolbarDecorator = ToolbarDecorator.createDecorator(userTable);
        addActionToToolBar(toolbarDecorator);
        toolbarDecorator.setPreferredSize(new Dimension(300, 300));
        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder();
        mainFormBuilder.addComponent(toolbarDecorator.createPanel());
        mainPanel = mainFormBuilder.addComponentFillVertically(panel, 0).getPanel();
    }

    /**
     * 处理工具列表事件。
     *
     * @param toolbarDecorator 工具栏
     */
    private void addActionToToolBar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator
                .setAddActionName(TuningUserManageConstant.TITLE_CREATE_USER)
                .setAddAction(new UserAddAction(userTable, workSpacePath))
                .setRemoveAction(new UserDeletedAction(userTable))
                .setRemoveActionName(TuningUserManageConstant.OPERATE_DEL)
                .setEditAction(new UserModifyAction(userTable))
                .setEditActionName(OPERATE_RESET)
                .setMoveUpAction(new IDETableMoveAction(userTable, true))
                .setMoveDownAction(new IDETableMoveAction(userTable, false));
    }

    private void createUserTableColName() {
        // 创建表头
        columnNamesList = new Vector<>();
        columnNamesList.add(USER_LABEL_USER_ID);
        columnNamesList.add(USER_LABEL_NAME);
        columnNamesList.add(USER_LABEL_ROLE);
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof UserManagerAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 查询用户列表。
     */
    private void selectUserList() {
        if (action instanceof UserManagerAction) {
            userList = ((UserManagerAction) action).selectUserList(new HashMap<String, String>());
        }
    }

    /**
     * 更新表格数据。
     */
    public void updateUserTale() {
        selectUserList(); // 查询一次用户数据
        createTableInfo(); // 将查询到的最新用户数据更新到表格中
        IdeFocusManager.getGlobalInstance()
                .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(userTable, true));
        TableUtil.updateScroller(userTable);
        addActionToToolBar(toolbarDecorator);
    }

    /**
     * 事件处理。
     */
    public void apply() {
        updateUserTale(); // 增加表格数据重置。
    }
}

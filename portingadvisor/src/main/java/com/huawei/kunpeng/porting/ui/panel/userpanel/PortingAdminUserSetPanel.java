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

package com.huawei.kunpeng.porting.ui.panel.userpanel;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.OPERATE_RESET;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_NAME;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_ROLE;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_USER_ID;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_LABEL_WORKSPACE;

import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.userpanel.AdminUserSetPanel;
import com.huawei.kunpeng.porting.action.setting.user.UserAddAction;
import com.huawei.kunpeng.porting.action.setting.user.UserDeletedAction;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.action.setting.user.UserModifyAction;

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
 * ??????????????????-???????????????????????????
 *
 * @since 2020-10-6
 */
public class PortingAdminUserSetPanel extends AdminUserSetPanel {
    /**
     * ????????????
     */
    public PortingAdminUserSetPanel() {
        // ???????????????
        initPanel(new JPanel());
        registerComponentAction();
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        if (action == null) {
            action = new UserManagerAction();
        }
        selectUserList(); // ?????????????????????
        setTableModel(); // ?????????????????????
        userTable = new JBTable(tableModel);
        // ????????????
        createUserTableColName();
        createTableInfo();
        selectWorkSpaceForWeb();
        addListenerToTable();
        toolbarDecorator = ToolbarDecorator.createDecorator(userTable);
        addActionToToolBar(toolbarDecorator);
        toolbarDecorator.setPreferredSize(new Dimension(300, 300));
        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder();
        mainFormBuilder.addComponent(toolbarDecorator.createPanel());
        mainPanel = mainFormBuilder.addComponentFillVertically(panel, 0).getPanel();
    }

    /**
     * ???????????????????????????
     *
     * @param toolbarDecorator ?????????
     */
    private void addActionToToolBar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.setAddActionName(UserManageConstant.TITLE_CREATE_USER)
                .setAddAction(new UserAddAction(this.userTable, this.workSpacePath))
                .setRemoveAction(new UserDeletedAction(this.userTable))
                .setRemoveActionName(UserManageConstant.OPERATE_DEL)
                .setEditAction(new UserModifyAction(this.userTable, this.workSpacePath))
                .setEditActionName(OPERATE_RESET)
                .setMoveUpAction(new IDETableMoveAction(this.userTable, true))
                .setMoveDownAction(new IDETableMoveAction(this.userTable, false));
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof UserManagerAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * ????????????
     */
    private void createUserTableColName() {
        // ????????????
        columnNamesList = new Vector<>();
        columnNamesList.add(USER_LABEL_USER_ID);
        columnNamesList.add(USER_LABEL_NAME);
        columnNamesList.add(USER_LABEL_ROLE);
        columnNamesList.add(USER_LABEL_WORKSPACE);
    }

    /**
     * ?????????????????????
     */
    private void selectUserList() {
        if (action instanceof UserManagerAction) {
            userList = ((UserManagerAction) action).selectUserList(new HashMap<>());
        }
    }

    /**
     * ?????????????????????
     */
    private void selectWorkSpaceForWeb() {
        if (action instanceof UserManagerAction) {
            workSpacePath = ((UserManagerAction) action).selectWorkSpaceForWeb();
        }
    }

    /**
     * ?????????????????????
     */
    public void updateUserTale() {
        selectUserList(); // ????????????????????????
        createTableInfo(); // ???????????????????????????????????????????????????
        IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() ->
                IdeFocusManager.getGlobalInstance().requestFocus(userTable, true));
        TableUtil.updateScroller(userTable);
        selectWorkSpaceForWeb(); // ??????????????????
        addActionToToolBar(toolbarDecorator);
    }

    /**
     * ???????????????
     */
    public void apply() {
        updateUserTale(); // ???????????????????????????
    }
}

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

package com.huawei.kunpeng.intellij.ui.panel.userpanel;

import com.huawei.kunpeng.intellij.common.bean.UserBean;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * 管理员用户管理界面
 *
 * @since 2021-9-6
 */
public class AdminUserSetPanel extends IDEBasePanel {
    /**
     * ID列下标
     */
    public static final int ID_COLUMN_INDEX = 0;

    /**
     * 用户列表
     */
    protected transient ArrayList<UserBean> userList;

    /**
     * 工作空间目录
     */
    protected String workSpacePath;

    /**
     * 主面板
     */
    protected JPanel mainPanel;

    /**
     * 用户表格
     */
    protected JBTable userTable;

    /**
     * 创建表头
     */
    protected Vector<String> columnNamesList;

    /**
     * 用户模板
     */
    protected DefaultTableModel tableModel;

    /**
     * 表格工具栏
     */
    protected ToolbarDecorator toolbarDecorator;

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JComponent getPreferredFocusedComponent() {
        return mainPanel;
    }

    /**
     *  界面是否修改
     *
     * @return 结果
     */
    public boolean isModified() {
        return false;
    }

    /**
     * 重置界面方法
     */
    public void reset() {
    }

    /**
     * 事件处理。
     */
    public void apply() {
    }

    @Override
    protected void initPanel(JPanel panel) {
    }

    /**
     * 添加用户数据到表格
     */
    protected void createTableInfo() {
        // 表格所有行数据
        Vector<Vector<String>> users = new Vector<>();
        if (userList != null) {
            for (UserBean userBean : userList) {
                users.add(userBean.toVector());
            }
        }
        tableModel.setDataVector(users, columnNamesList);
        // 隐藏用户ID列
        hideColumn(userTable, ID_COLUMN_INDEX);

        // 这只表格只可以监听一行
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 不重排
        userTable.getTableHeader().setReorderingAllowed(false);
    }

    /**
     *  设置表格模式。
     */
    protected void setTableModel() {
        // 创建 表格模型，指定 所有行数据 和 表头
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 不可编辑
                return false;
            }
        };
    }

    /**
     * 隐藏列
     *
     * @param table table
     * @param columnIndex columnIndex
     */
    protected void hideColumn(JTable table, int columnIndex) {
        CommonTableUtil.hideColumn(table, columnIndex);
    }

    /**
     * 添加表格监视事件。
     */
    protected void addListenerToTable() {
        userTable
                .getSelectionModel()
                .addListSelectionListener(
                        uListener -> {
                            int row = userTable.getSelectedRow();
                            final CommonActionsPanel actionsPanel = toolbarDecorator.getActionsPanel();
                            // 判断是管理员，将删除按钮置灰
                            if (row == 0) {
                                actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, false);
                            }
                        });
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

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

package com.huawei.kunpeng.porting.ui.panel.weakpwdpanel;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.bean.WeakPwdBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;
import com.huawei.kunpeng.porting.action.setting.weakpwd.WeakPwdManageAction;
import com.huawei.kunpeng.porting.action.setting.weakpwd.WeakPwdTableAddAction;
import com.huawei.kunpeng.porting.action.setting.weakpwd.WeakPwdTableDeletedAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.FilterComponent;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * 添加弱口令设置面板
 *
 * @since 2012-10-12
 */
public class WeakPwdSetPanel extends IDEBasePanel {
    /**
     * 定义常量
     */
    public static final String COLLATERAL = "0";

    /**
     * 弱口令表格
     */
    public static JBTable weakPwdTable;

    /**
     * 数据weakPwdList
     */
    public static List<WeakPwdBean> weakPwdList;

    /**
     * 操作列下标
     */
    private static final int OPERATION_INDEX = 2;

    /**
     * ID列下标
     */
    private static final int ID_COLUMN_INDEX = 0;

    /**
     * 弱口令ID
     */
    private static final String LABELID = "ID";

    /**
     * 主面板
     */
    private JPanel mainPanel = new JPanel();

    /**
     * 表格Model
     */
    private DefaultTableModel tableModel;

    /**
     * 添加弱口令Button
     */
    private JButton weakPwdButton;

    /**
     * 主容器mainFormBuider
     */
    private FormBuilder mainFormBuider;

    /**
     * 主内容面板WeakPwdSetPanel
     */
    private WeakPwdSetPanel that;

    /**
     * 弱口令搜索框
     */
    private FilterComponent filterWeakPwds;

    /**
     * 搜索框内的文本，用作关键字对弱口令进行筛选操作
     */
    private String key = "";

    /**
     * 表格列名
     */
    private Vector<String> columnNamesList;

    /**
     * 表格所有行数据
     */
    private Vector<Vector<String>> weakPwds;

    /**
     * 总条数
     */
    private JLabel jTextFieldTotalRow;

    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarDecorator;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow  依托的toolWindow窗口，可为空
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public WeakPwdSetPanel(ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        that = this;
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.WEAK_PWD_SET.panelName() : panelName;

        // 初始化面板
        initPanel(new JPanel());

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, displayName, isLockable);

        // 装载面板数据
        loadPanelData();
    }

    /**
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public WeakPwdSetPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        // 初始化搜索框
        initWeakPwdsSearch();
        mainFormBuider = FormBuilder.createFormBuilder();

        // 表格数据
        weakPwdTable = new JBTable();
        weakPwdTable.setPreferredScrollableViewportSize(new Dimension(570, 500));
        weakPwdTable.setFillsViewportHeight(true);
        weakPwdTable.getEmptyText().setText(PortingWeakPwdConstant.WEAK_PWD_NO_DATA);
        // 工具栏创建
        toolbarDecorator = ToolbarDecorator.createDecorator(weakPwdTable);
        addActionToToolBar(toolbarDecorator);
        toolbarDecorator.setPreferredSize(new Dimension(300, 500));

        // 显示总条数面板
        JPanel jPanelTotalRow = new JPanel(new FlowLayout());
        JLabel jLabelTotalRow = new JLabel(PortingWeakPwdConstant.TOTAL_NUM);
        jTextFieldTotalRow = new JLabel();
        jPanelTotalRow.add(jLabelTotalRow);
        jPanelTotalRow.add(jTextFieldTotalRow);
        JPanel jPanelBackTotal = new JPanel(new BorderLayout());
        jPanelBackTotal.add(jPanelTotalRow, BorderLayout.WEST);
        // 空白panel用于格式占位
        JPanel empty = new JPanel();
        empty.setPreferredSize(new Dimension(400, 30));
        // 将空白panel放置搜索框右侧
        filterWeakPwds.add(empty, BorderLayout.EAST);
        // 依次拼接组件
        mainFormBuider.addComponent(filterWeakPwds);
        mainFormBuider.addComponent(toolbarDecorator.createPanel());
        mainFormBuider.addComponent(jPanelBackTotal);

        // 设置主面板
        mainPanel = mainFormBuider.addComponentFillVertically(panel, 0).getPanel();
    }

    /**
     * 初始化搜索框
     */
    private void initWeakPwdsSearch() {
        filterWeakPwds = new FilterComponent("weakPwd", 5) {
            @Override
            public void filter() {
                // 获取搜索框内容即关键字
                key = filterWeakPwds.getFilter();
                // 每次筛选前先清空缓存
                weakPwds.clear();
                // 通过关键字刷新表格数据显示
                refreshWeakPwdsData();
            }
        };
    }

    /**
     * 通过搜索框关键字刷新弱口令表格数据
     */
    private void refreshWeakPwdsData() {
        if (weakPwdList != null) {
            for (WeakPwdBean bean : weakPwdList) {
                if (bean.getWeakPassword().contains(key)) {
                    // 只显示关键字匹配的弱口令信息
                    weakPwds.add(bean.toWeakPwdVector());
                }
            }
        }
        tableModel.setDataVector(weakPwds, columnNamesList);
        // 隐藏弱口令ID列
        hideColumn(weakPwdTable, ID_COLUMN_INDEX);

        if (ValidateUtils.isNotEmptyString(key)) {
            // 无弱密码列表处理逻辑
            if (ValidateUtils.isEmptyCollection(weakPwds)) {
                jTextFieldTotalRow.setText(COLLATERAL);
            } else {
                jTextFieldTotalRow.setText(String.valueOf(weakPwds.size()));
            }
        } else {
            // 无弱密码列表处理逻辑
            if (ValidateUtils.isEmptyCollection(weakPwdList)) {
                jTextFieldTotalRow.setText(COLLATERAL);
            } else {
                jTextFieldTotalRow.setText(String.valueOf(weakPwdList.size()));
            }
        }
    }

    private void addActionToToolBar(ToolbarDecorator toolbarDecorator) {
        if (!Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole())) {
            return;
        }
        toolbarDecorator.setAddActionName(PortingWeakPwdConstant.ADD_WEAK_PASSWORD_NAME);
        toolbarDecorator.setAddAction(new WeakPwdTableAddAction(weakPwdTable, that));

        toolbarDecorator.setRemoveAction(new WeakPwdTableDeletedAction(weakPwdTable, that));
        toolbarDecorator.setRemoveActionName(PortingWeakPwdConstant.WEAK_PASSWORD_DEL);

        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(weakPwdTable, true));

        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(weakPwdTable, false));
    }

    /**
     * 装载面板数据
     */
    private void loadPanelData() {
        updateTable();
    }

    /**
     * 设置和更新表格数据
     */
    public void updateTable() {
        // 设置表头
        columnNamesList = new Vector<>();
        columnNamesList.add(LABELID);
        columnNamesList.add(PortingWeakPwdConstant.WEAK_PASSWORD);
        weakPwds = new Vector<>();
        // 获取和设置表格行数据
        weakPwdList = (action instanceof WeakPwdManageAction) ? ((WeakPwdManageAction) action).selectWeakPwdList(this,
            null) : new ArrayList<>();
        // 无弱密码列表处理逻辑
        if (ValidateUtils.isEmptyCollection(weakPwdList)) {
            Logger.warn("weak Password list is null");
            jTextFieldTotalRow.setText(COLLATERAL);
        } else {
            jTextFieldTotalRow.setText(String.valueOf(weakPwdList.size()));
        }
        // 创建 表格模型，指定 所有行数据和表头
        tableModel = new DefaultTableModel(weakPwds, columnNamesList) {
            /**
             * 只有操作列才能被编辑
             *
             * @param row 表格的行
             * @param column 表格的列
             * @return False
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        weakPwdTable.setModel(tableModel);
        // 表格所有行数据 根据搜索框内关键字进行筛选显示内容
        refreshWeakPwdsData();
        // 表格只可以监听一行。
        weakPwdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 不可拉动表格
        weakPwdTable.getTableHeader().setResizingAllowed(false);
        weakPwdTable.getTableHeader().setReorderingAllowed(false);
    }

    /**
     * 隐藏列
     *
     * @param table       table
     * @param columnIndex columnIndex
     */
    private void hideColumn(JTable table, int columnIndex) {
        CommonTableUtil.hideColumn(table, columnIndex);
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = WeakPwdManageAction.getInstance();
        }
    }

    /**
     * 判断是否修改参数值且输入了用户密码
     * 若是，设置界面enable Apply按钮
     * 否则，Apply按钮不可点击
     *
     * @return boolean
     */
    public boolean isModified() {
        return ValidateUtils.isNotEmptyString(key);
    }

    /**
     * 点击apply按钮提交修改请求
     */
    public void apply() {
    }

    /**
     * 点击reset按钮提交修改请求
     */
    public void reset() {
        key = "";
        filterWeakPwds.setFilter("");
    }

    /**
     * 设置事件处理器
     *
     * @param action 处理事件
     */
    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof WeakPwdManageAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * getPreferredFocusedComponent
     *
     * @return weakPwdButton
     */
    public JComponent getPreferredFocusedComponent() {
        return weakPwdButton;
    }
}

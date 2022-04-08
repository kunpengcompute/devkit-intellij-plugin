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

package com.huawei.kunpeng.intellij.ui.panel;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.bean.WeakPwdBean;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.FilterComponent;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * 添加弱口令设置面板
 *
 * @since 2012-10-12
 */
public abstract class WeakPwdSetPanel extends IDEBasePanel {
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
     * 弱口令ID
     */
    protected static final String LABELID = "ID";
    /**
     * ID列下标
     */
    private static final int ID_COLUMN_INDEX = 0;
    /**
     * 主面板
     */
    protected JPanel mainPanel = new JPanel();

    /**
     * 表格Model
     */
    protected DefaultTableModel tableModel;

    /**
     * 添加弱口令Button
     */
    protected JButton weakPwdButton;

    /**
     * 主容器mainFormBuider
     */
    protected FormBuilder mainFormBuider;

    /**
     * 主内容面板WeakPwdSetPanel
     */
    protected WeakPwdSetPanel that;

    /**
     * 弱口令搜索框
     */
    protected FilterComponent filterWeakPwds;

    /**
     * 搜索框内的文本，用作关键字对弱口令进行筛选操作
     */
    protected String key = "";

    /**
     * 表格列名
     */
    protected Vector columnNamesList;

    /**
     * 表格所有行数据
     */
    protected Vector weakPwds;

    /**
     * 总条数
     */
    protected JLabel jTextFieldTotalRow;

    /**
     * 表格工具栏
     */
    protected ToolbarDecorator toolbarDecorator;

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
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, displayName, isLockable);

        // 装载面板数据
        loadPanelData();
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

        // 工具栏创建
        toolbarDecorator = ToolbarDecorator.createDecorator(weakPwdTable);
        addActionToToolBar(toolbarDecorator);
        toolbarDecorator.setPreferredSize(new Dimension(300, 500));

        // 显示总条数面板
        JPanel jPanelTotalRow = new JPanel(new FlowLayout());
        JLabel jLabelTotalRow = new JLabel(WeakPwdConstant.TOTAL_NUM);
        jTextFieldTotalRow = new JLabel();
        jPanelTotalRow.add(jLabelTotalRow);
        jPanelTotalRow.add(jTextFieldTotalRow);
        JPanel jPanelBackTotal = new JPanel(new BorderLayout());
        jPanelBackTotal.add(jPanelTotalRow, BorderLayout.WEST);
        // 空白panel用于格式占位
        JPanel empty = new JPanel();
        empty.setPreferredSize(new Dimension(450, 30));
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
     * 带toolWindow的构造参数,代理生成时会使用
     *
     * @param toolWindow toolWindow
     */
    public WeakPwdSetPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * 初始化搜索框
     */
    protected void initWeakPwdsSearch() {
        filterWeakPwds =
                new FilterComponent("weakPwd", 5) {
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
     * addActionToToolBar
     *
     * @param toolbarDecorator toolbarDecorator
     */
    protected void addActionToToolBar(ToolbarDecorator toolbarDecorator) {
        if (!Objects.equals(UserManageConstant.USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole())) {
            return;
        }
        toolbarDecorator.setAddActionName(WeakPwdConstant.ADD_WEAK_PASSWORD_NAME);
        setActionToToolBar(toolbarDecorator);
        toolbarDecorator.setRemoveActionName(WeakPwdConstant.WEAK_PASSWORD_DEL);

        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(this.weakPwdTable, true));

        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(this.weakPwdTable, false));
    }

    /**
     * setActionToToolBar
     *
     * @param toolbarDecorator toolbarDecorator
     */
    protected void setActionToToolBar(ToolbarDecorator toolbarDecorator) {
    }

    /**
     * 通过搜索框关键字刷新弱口令表格数据
     */
    protected void refreshWeakPwdsData() {
        if (weakPwdList != null) {
            for (WeakPwdBean bean : weakPwdList) {
                if (bean.getWeakPassword().contains(key)) {
                    // 只显示关键字匹配的弱口令信息
                    weakPwds.add(bean.toWeakPwdVector());
                }
            }
        }
        tableModel.setDataVector(weakPwds, columnNamesList);
        hideColumn();
        setJtextFieldTotalRow();
    }

    private void setJtextFieldTotalRow() {
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

    /**
     * hideColumn
     */
    protected void hideColumn() {
        // 隐藏弱口令ID列
        CommonTableUtil.hideColumn(weakPwdTable, ID_COLUMN_INDEX);
    }

    /**
     * 装载面板数据
     */
    protected void loadPanelData() {
        updateTable();
    }

    /**
     * 设置和更新表格数据
     */
    public void updateTable() {
        // 设置表头
        columnNamesList = new Vector();
        columnNamesList.add(LABELID);
        columnNamesList.add(WeakPwdConstant.WEAK_PASSWORD);
        weakPwds = new Vector();
        // 获取和设置表格行数据
        weakPwdList = getWeakPwdList();
        // 无弱密码列表处理逻辑
        if (ValidateUtils.isEmptyCollection(weakPwdList)) {
            Logger.warn("weak Password list is null");
            jTextFieldTotalRow.setText(COLLATERAL);
        } else {
            jTextFieldTotalRow.setText(String.valueOf(weakPwdList.size()));
        }
        // 创建 表格模型，指定 所有行数据和表头
        tableModel =
                new DefaultTableModel(weakPwds, columnNamesList) {
                    /**
                     * 只有操作列才能被编辑
                     *
                     * @param row 表格的行
                     * @param column 表格的列
                     * @return False
                     */
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        setWeakPwdTable();
    }

    private void setWeakPwdTable() {
        weakPwdTable.setModel(tableModel);
        // 表格所有行数据 根据搜索框内关键字进行筛选显示内容
        refreshWeakPwdsData();
        // 表格只可以监听一行。
        weakPwdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 不可拉动表格
        weakPwdTable.getTableHeader().setResizingAllowed(false);
        weakPwdTable.getTableHeader().setReorderingAllowed(false);
    }

    protected List<WeakPwdBean> getWeakPwdList() {
        return weakPwdList;
    };

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

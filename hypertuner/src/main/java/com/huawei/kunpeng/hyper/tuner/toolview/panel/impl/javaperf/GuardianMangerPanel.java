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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.action.javaperf.GuardianAddAction;
import com.huawei.kunpeng.hyper.tuner.action.javaperf.GuardianManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.GuardianManagerBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.javaperf.JavaPerfTableRenderer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.TableUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * 目标环境管理面板
 *
 * @since 2021-07-10
 */
public class GuardianMangerPanel extends SettingCommonConfigPanel {
    private JPanel mainPanel;

    private GuardianManagerAction guardianManagerAction;

    private transient List<GuardianManagerBean> guardianList;

    private JPanel guardianPanel;

    private JTable guardianTable;

    private DefaultTableModel tableModel;

    private Object[][] tableDate;

    private ToolbarDecorator toolbarForTable;

    private List<String> columnNameList = new ArrayList<String>();

    /**
     * 构造函数
     */
    public GuardianMangerPanel() {
        initPanel(mainPanel);
        // 初始化面板内组件事件
        registerComponentAction();
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(mainPanel);
        if (guardianManagerAction == null) {
            guardianManagerAction = new GuardianManagerAction();
        }
        selectGuardianList(); // 获取表格数据
        setTableModel(); // 设置表头模型。
        guardianTable = new JBTable(tableModel);
        initTable();
        toolbarForTable = ToolbarDecorator.createDecorator(guardianTable);
        addToToolBarForTable(toolbarForTable);
        toolbarForTable.setPreferredSize(new Dimension(300, 300));
        guardianPanel.add(toolbarForTable.createPanel());
    }

    /**
     * 表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     */
    private void addToToolBarForTable(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.setAddActionName(GuardianMangerConstant.GUARDIAN_ADD_TITLE);
        toolbarDecorator.setAddAction(new GuardianAddAction(this.guardianTable));
        // 刷新表格
        AnActionButton guardianUpdate =
            AnActionButton.fromAction(ActionManager.getInstance().getAction("javaperf.guardianUpdate"));
        toolbarDecorator.addExtraAction(guardianUpdate);
    }

    /**
     * 设置表格模式。
     */
    private void setTableModel() {
        // 创建 表格模型，指定 所有行数据 和 表头
        tableModel =
            new DefaultTableModel(this.tableDate, columnNameList.toArray()) {
                private static final long serialVersionUID = 1529594828549047055L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    if (column == 6 && row < tableDate.length) {
                        return true;
                    }
                    return false;
                }
            };
    }

    /**
     * 监听事件
     */
    @Override
    protected void registerComponentAction() {
    }

    /**
     * 处理事件
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 获取对应面板
     *
     * @return mainPanel
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 初始化操作日志
     */
    private void initTable() {
        // 表格所有行数据
        this.tableDate = getArray(guardianList.size());
        for (int i = 0; i < guardianList.size(); i++) {
            String[] item = new String[10];
            item[0] = guardianList.get(i).getId();
            item[1] = guardianList.get(i).getName();
            item[2] = guardianList.get(i).getState();
            item[3] = guardianList.get(i).getIp();
            item[4] =
                guardianList.get(i).getSshPort() == "" || guardianList.get(i).getSshPort() == null
                    ? "--"
                    : guardianList.get(i).getSshPort();
            item[5] = guardianList.get(i).getOwner().getString("username");
            item[6] = "...";
            tableDate[i] = item;
        }
        createNodeTableColName();
        tableModel.setDataVector(tableDate, columnNameList.toArray());
        guardianTable.setModel(tableModel);
        // 不可以重排列。
        guardianTable.getTableHeader().setReorderingAllowed(false);
        addListener();
        CommonTableUtil.hideColumn(guardianTable, GuardianMangerConstant.ID_COLUMN_INDEX);

        // 给状态列设置渲染器。
        guardianTable.getColumnModel().getColumn(2).setCellRenderer(new JavaPerfTableRenderer("guardianTable"));
    }

    private void createNodeTableColName() {
        columnNameList.clear();
        columnNameList.add(GuardianMangerConstant.TABLE_COL_ID);
        columnNameList.add(GuardianMangerConstant.TABLE_COL_NAME);
        columnNameList.add(GuardianMangerConstant.TABLE_COL_STATUS);
        columnNameList.add(GuardianMangerConstant.TABLE_COL_IP);
        columnNameList.add(GuardianMangerConstant.TABLE_COL_PORT);
        columnNameList.add(GuardianMangerConstant.TABLE_COL_USER);
        columnNameList.add(GuardianMangerConstant.TABLE_COL_OPERATION);
    }

    /**
     * 设置和更新表格数据
     */
    public void updateGuardianTable() {
        selectGuardianList();
        initTable(); // 将查询到的最新用户数据更新到表格中
        IdeFocusManager.getGlobalInstance()
            .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(guardianTable, true));
        TableUtil.updateScroller(guardianTable);
        toolbarForTable = ToolbarDecorator.createDecorator(guardianTable);
        addToToolBarForTable(toolbarForTable);
        guardianPanel.removeAll();
        toolbarForTable.setPreferredSize(new Dimension(300, 300));
        guardianPanel.add(toolbarForTable.createPanel());
    }

    /**
     * 列监听时间
     */
    private void addListener() {
        this.guardianTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * 鼠标点击事件
                 *
                 *  @param event 事件
                 */
                @Override
                public void mouseClicked(MouseEvent event) {
                    int col = guardianTable.columnAtPoint(event.getPoint());
                    int row = guardianTable.rowAtPoint(event.getPoint());
                    if (col == 6 && row < tableDate.length && row >= 0) {
                        showPopupMenu(event.getComponent(), event.getX(), event.getY());
                    }
                }
            });
    }

    private void showPopupMenu(Component invoker, int xPostion, int yPostion) {
        int row = guardianTable.getSelectedRow();
        String id = guardianTable.getValueAt(row, 0).toString();
        String name = guardianTable.getValueAt(row, 1).toString();
        String status = guardianTable.getValueAt(row, 2).toString();
        String ip = guardianTable.getValueAt(row, 3).toString();
        String port = guardianTable.getValueAt(row, 4).toString();
        String user = guardianTable.getValueAt(row, 5).toString();
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();

        // 菜单添加删除
        JMenuItem delete = new JMenuItem(GuardianMangerConstant.TABLE_COL_DELETE);
        popupMenu.add(delete);
        delete.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    guardianManagerAction.deleteGuardian(name, status, id, user);
                }
            });
        // 菜单添加重启
        JMenuItem reconnect = new JMenuItem(GuardianMangerConstant.TABLE_COL_RECONNECT);
        if (status.equals(GuardianMangerConstant.TABLE_COL_DISCONNECTED)
            && ValidateUtils.equals(user, UserInfoContext.getInstance().getUserName())) {
            popupMenu.add(reconnect);
            reconnect.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        guardianManagerAction.restartGuardian(id, ip, port, name);
                    }
                });
        }
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xPostion, yPostion);
    }

    /**
     * 查询目标环境列表。
     */
    private void selectGuardianList() {
        guardianList = guardianManagerAction.getGuardianList();
    }

    /**
     * 返回数组
     *
     * @param i i
     * @return 返回数组
     */
    public Object[][] getArray(int i) {
        return new Object[i][];
    }
}

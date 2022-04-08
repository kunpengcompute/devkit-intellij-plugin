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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.CheckBoxHeaderRender;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.project.Project;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * 预约任务 更新
 * 设置锁与等待分析类型分析函数字段 面板
 *
 * @since 2021-07-14
 */
public class SchTaskUpdateFunctionPanel extends IDEBasePanel {
    /**
     * 表格- 多选框索引
     */
    private static final int SELECT_INDEX = 0;

    private JPanel mainPanel;
    private JTable functionTable;
    private JTextField customFunTextField;
    private JLabel customFunLabel;

    private Project project;
    private DefaultTableModel tableModel;
    private List<String> columnNamesList;
    private Object[][] tableData;
    private CheckBoxHeaderRender checkBoxHeader;
    private HashSet<String> allFunctionSet;
    private HashSet<String> selectFunctionSet;
    private List<String> customerFunList;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param panelName   面板名称
     * @param displayName 面板显示title
     */
    public SchTaskUpdateFunctionPanel(
            String panelName, String displayName, HashSet<String> functionNameSet, List<String> customerFunList) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "SchTaskUpdateFunctionPanel" : panelName;
        this.selectFunctionSet = functionNameSet;
        this.customerFunList = customerFunList;
        initPanel(mainPanel); // 初始化面板
    }

    @Override
    protected void initPanel(JPanel panel) {
        mainPanel.setPreferredSize(new Dimension(500, -1));
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);
        initTable();
        customFunLabel.setText(TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION_CUSTOMIZED);
        if (customerFunList.size() > 0) {
            customFunTextField.setText(customerFunList.get(0));
        }
    }

    private void initTable() {
        // 创建表格模型，指定单元格格式
        tableModel =
                new DefaultTableModel() {
                    private static final long serialVersionUID = -2579019475997889830L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        // 只允许多选框 可编辑
                        return column == SELECT_INDEX;
                    }

                    @Override
                    public Class getColumnClass(int column) {
                        // SELECT_INDEX 为多选框
                        if (column == SELECT_INDEX) {
                            return Boolean.class;
                        }
                        return String.class;
                    }
                };
        checkBoxHeader = new CheckBoxHeaderRender(); // 生成表头渲染类
        checkBoxHeader.setBackground(null);

        // 生成表头
        createfunctionTableColName();
        allFunctionSet = getAllFunctionSet();
        createFunctionTableDate(allFunctionSet, selectFunctionSet);
        updateTableByObjData();
        addHeaderListener(); // 添加【表格多选框】监听事件
    }

    private void updateTableByObjData() {
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        functionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 这只表格只可以监听一行。
        functionTable.getTableHeader().setReorderingAllowed(false); // 不可以重排列。
        functionTable.setModel(tableModel);
        // 获取多选框所在列，设置【表头-多选框】渲染器
        TableColumn selectColumn = functionTable.getColumnModel().getColumn(SELECT_INDEX);
        selectColumn.setHeaderRenderer(checkBoxHeader);
        selectColumn.setMaxWidth(30);
    }

    /**
     * 添加表头多选框监听事件
     */
    private void addHeaderListener() {
        functionTable
                .getTableHeader()
                .addMouseListener(
                        new MouseAdapter() {
                            /**
                             * 鼠标点击事件
                             *
                             * @param event 事件
                             */
                            public void mouseClicked(MouseEvent event) {
                                int col = functionTable.columnAtPoint(event.getPoint());
                                if (col == SELECT_INDEX) {
                                    // 获取当前表头是否选择
                                    checkBoxHeader.changeAllSelect(tableData);
                                    updateTableByObjData();
                                    // 多选激活删除按钮
                                    boolean haveSele = getHaveSelect();
                                }
                            }
                        });
    }

    // 获取是否有选中行
    private boolean getHaveSelect() {
        int taskCount = tableData.length;
        for (int row = 0; row < taskCount; row++) {
            Object selectObj = functionTable.getValueAt(row, SELECT_INDEX);
            if (selectObj instanceof Boolean) {
                boolean select = (boolean) selectObj;
                if (select) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 创建表头
     */
    private void createfunctionTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(""); // 多选
        columnNamesList.add(TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION); // 标准函数
    }

    private HashSet<String> getAllFunctionSet() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("pthread_mutex_lock;");
        hashSet.add("pthread_mutex_trylock;");
        hashSet.add("pthread_mutex_unlock;");
        hashSet.add("pthread_cond_wait;");
        hashSet.add("pthread_cond_timedwait;");
        hashSet.add("pthread_cond_reltimedwait_np;");
        hashSet.add("pthread_cond_signal;");
        hashSet.add("pthread_cond_broadcast;");
        hashSet.add("pthread_rwlock_rdlock;");
        hashSet.add("pthread_rwlock_tryrdlock;");
        hashSet.add("pthread_rwlock_wrlock;");
        hashSet.add("pthread_rwlock_trywrlock;");
        hashSet.add("pthread_rwlock_unlock;");
        hashSet.add("sem_post;");
        hashSet.add("sem_wait;");
        hashSet.add("sem_trywait;");
        hashSet.add("pthread_spin_lock;");
        hashSet.add("pthread_spin_trylock;");
        hashSet.add("pthread_spin_unlock;");
        hashSet.add("sleep;");
        hashSet.add("usleep;");
        return hashSet;
    }

    /**
     * 创建表格数据二维数组
     *
     * @param allFunctionSet    全部带选项
     * @param selectFunctionSet 选中项
     */
    private void createFunctionTableDate(HashSet<String> allFunctionSet, HashSet<String> selectFunctionSet) {
        tableData = new Object[allFunctionSet.size()][];
        Iterator<String> it = allFunctionSet.iterator();
        int length = 0;
        while (it.hasNext()) {
            List<Object> itemList = new ArrayList<>();
            String oneOfAllItem = it.next();
            if (selectFunctionSet.contains(oneOfAllItem)) {
                itemList.add(Boolean.TRUE);
            } else {
                itemList.add(Boolean.FALSE);
            }
            itemList.add(oneOfAllItem);
            tableData[length++] = itemList.toArray();
        }
    }

    /**
     * 获取新的函数列表
     */
    public void getNewFunctionSet() {
        selectFunctionSet.clear();
        int taskCount = tableData.length;
        for (int row = 0; row < taskCount; row++) {
            Object selectObj = functionTable.getValueAt(row, SELECT_INDEX);
            if (selectObj instanceof Boolean) {
                boolean select = (boolean) selectObj;
                if (select) {
                    Object funNameObj = functionTable.getValueAt(row, 1);
                    if (funNameObj instanceof String) {
                        String funNameStr = (String) funNameObj;
                        selectFunctionSet.add(funNameStr);
                    }
                }
            }
        }
        customerFunList.clear();
        customerFunList.add(customFunTextField.getText());
    }

    /**
     * 确认操作
     *
     * @return 是否选择下载文件
     */
    public Boolean onOK() {
        return false;
    }

    @Override
    protected void registerComponentAction() {}

    @Override
    protected void setAction(IDEPanelBaseAction action) {}
}

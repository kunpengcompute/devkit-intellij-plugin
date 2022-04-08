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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.SchTaskFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.CheckBoxHeaderRender;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.SchTaskTableRenderer;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * 批量删除预约任务面板
 * 以表格形式展示待删除的任务列表
 *
 * @since 2021-5-13
 */
public class SchTaskMultiDeletePanel extends IDEBasePanel {
    private static final String WARN_PATH = "/assets/img/common/icon_warn.png";
    /**
     * 表格- 多选框索引
     */
    private static final int SELECT_INDEX = 0;
    /**
     * 表格-编号索引
     */
    private static final int ID_INDEX = 1;
    /**
     * 表格-名称索引
     */
    private static final int NAME_INDEX = 2;
    /**
     * 表格-状态索引
     */
    private static final int STATUS_INDEX = 3;
    /**
     * 表格-分析对象索引
     */
    private static final int ANALYSIS_TARGET_INDEX = 4;
    /**
     * 表格-分析类型索引
     */
    private static final int ANALYSIS_TYPE_INDEX = 5;
    /**
     * 表格-工程名称索引
     */
    private static final int PRO_NAME_INDEX = 6;

    private JPanel mainPanel;
    private JLabel noticeLabel;
    private JTable delTable;
    private JPanel delTablePanel;

    private List<SchTaskBean> delBeanList;
    private DefaultTableModel tableModel;
    private ToolbarDecorator toolbarForDelTable;
    /**
     * 表头
     */
    private CheckBoxHeaderRender checkBoxHeader;

    private List<String> columnNamesList;
    private Object[][] tableData;

    public SchTaskMultiDeletePanel(List<SchTaskBean> beanList) {
        this.delBeanList = beanList;
        initPanel(mainPanel); // 初始化面板
        registerComponentAction(); // 初始化面板内组件事件
        createContent(mainPanel, SchTaskContent.SCH_TASK_DIALOG_DELETE_TITLE, false); // 初始化content实例
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        String noticeContent = SchTaskContent.SCH_TASK_DIALOG_DELETE_CONTENT;
        Icon warnIcon = BaseIntellijIcons.load(WARN_PATH);
        Dimension labelDim = this.noticeLabel.getPreferredSize();
        labelDim.height = 30;
        this.noticeLabel.setPreferredSize(labelDim);
        this.noticeLabel.setIcon(warnIcon);
        this.noticeLabel.setText(noticeContent);
        mainPanel.setPreferredSize(new Dimension(600, 200));
        delTable = new JBTable();
        initTable();
    }

    @Override
    protected void registerComponentAction() {}

    @Override
    protected void setAction(IDEPanelBaseAction action) {}

    private void initTable() {
        // 生成表头
        this.columnNamesList = createOperaTableColName();
        // 生成数据
        createOperaTableDate(delBeanList);
        checkBoxHeader = new CheckBoxHeaderRender(true); // 生成表头渲染类
        tableModel =
                new DefaultTableModel(tableData, columnNamesList.toArray()) {
                    private static final long serialVersionUID = -2579019475997889830L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        // 只允许多选框 可编辑
                        return column == SELECT_INDEX;
                    }

                    @Override
                    public Class getColumnClass(int column) {
                        // SELECT_INDEX 为多选框列
                        if (column == SELECT_INDEX) {
                            return Boolean.class;
                        }
                        return String.class;
                    }
                };
        updateTableByObjData();
        addHeaderListener(); // 添加【表格多选框】监听事件
        addSelectListener(); // 添加【表格多选框】监听事件
        toolbarForDelTable = ToolbarDecorator.createDecorator(delTable);
        delTablePanel.add(toolbarForDelTable.createPanel());
    }

    private List<String> createOperaTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(""); // 多选框
        columnNamesList.add(SchTaskContent.TABLE_TASK_ID); // 任务编号
        columnNamesList.add(SchTaskContent.TABLE_TASK_NAME); // 任务名称
        columnNamesList.add(SchTaskContent.TABLE_TASK_STATUS); // 任务状态
        columnNamesList.add(SchTaskContent.TABLE_TASK_ANALYSIS_TARGET); // 分析对象
        columnNamesList.add(SchTaskContent.TABLE_TASK_ANALYSIS_TYPE); // 分析类型
        columnNamesList.add(SchTaskContent.TABLE_TASK_PROJECT_NAME); // 工程名称
        return columnNamesList;
    }

    private DefaultTableCellRenderer getRender(int column) {
        return new DefaultTableCellRenderer() {
            /**
             * 重写setValue方法，从而可以动态设置列单元字体颜色
             *
             * @param value 显示的值
             */
            @Override
            public void setValue(Object value) {
                if (column == ANALYSIS_TYPE_INDEX) {
                    setText((value == null) ? "" : SchTaskFormatUtil.analysisTypeFormat(value.toString()));
                } else if (column == ANALYSIS_TARGET_INDEX) {
                    setText((value == null) ? "" : SchTaskFormatUtil.analysisTargetFormat(value.toString()));
                } else {
                    setText((value == null) ? "" : value.toString());
                }
            }
        };
    }

    /**
     * 创建表格数据二维数组
     *
     * @param schTaskBeanList 表格所有行数据
     */
    private void createOperaTableDate(List<SchTaskBean> schTaskBeanList) {
        tableData = new Object[schTaskBeanList.size()][];
        for (int i = 0; i < schTaskBeanList.size(); i++) {
            SchTaskBean bean = schTaskBeanList.get(i);
            List<Object> itemList = new ArrayList<>();
            itemList.add(new Boolean(true)); // 多选框
            itemList.add(bean.getTaskId() + ""); // 编号
            itemList.add(bean.getTaskName()); // 任务名称
            itemList.add(bean.getScheduleStatus()); // 任务状态
            itemList.add(bean.getTaskInfo().getAnalysisTarget()); // 分析对象
            itemList.add(bean.getAnalysisType()); // 分析类型
            itemList.add(bean.getProjectName()); // 工程名称
            tableData[i] = itemList.toArray();
        }
    }

    /**
     * 添加表头多选框监听事件
     */
    private void addHeaderListener() {
        delTable.getTableHeader()
                .addMouseListener(
                        new MouseAdapter() {
                            /**
                             * 鼠标点击事件
                             *
                             *  @param event 事件
                             */
                            @Override
                            public void mouseClicked(MouseEvent event) {
                                int col = delTable.columnAtPoint(event.getPoint());
                                if (col == SELECT_INDEX) {
                                    checkBoxHeader.changeAllSelect(tableData);
                                    updateTableByObjData();
                                }
                            }
                        });
    }

    private void addSelectListener() {
        this.delTable.addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件
                     *
                     *  @param event 事件
                     */
                    public void mouseReleased(MouseEvent event) {
                        int row = delTable.rowAtPoint(event.getPoint());
                        int col = delTable.columnAtPoint(event.getPoint());
                        if (col == SELECT_INDEX) {
                            // 及时更新tableData
                            Object selectObj = delTable.getValueAt(row, SELECT_INDEX);
                            tableData[row][SELECT_INDEX] = selectObj;
                            checkBoxHeader.updateByTableData(tableData);
                            updateTableByObjData();
                        }
                    }
                });
    }

    /**
     * 根据全局变量 tableData 加载表格
     */
    private void updateTableByObjData() {
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        delTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 这只表格只可以监听一行。
        delTable.getTableHeader().setReorderingAllowed(false); // 不可以重排列。
        delTable.setModel(tableModel);

        // 获取多选框所在列，设置【表头-多选框】渲染器
        TableColumn selectColumn = delTable.getColumnModel().getColumn(SELECT_INDEX);
        selectColumn.setMaxWidth(30);
        checkBoxHeader.setBackground(null);
        selectColumn.setHeaderRenderer(checkBoxHeader);

        // 给各个列设置渲染器
        delTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new SchTaskTableRenderer());
        delTable.getColumnModel().getColumn(ANALYSIS_TARGET_INDEX).setCellRenderer(getRender(ANALYSIS_TARGET_INDEX));
        delTable.getColumnModel().getColumn(ANALYSIS_TYPE_INDEX).setCellRenderer(getRender(ANALYSIS_TYPE_INDEX));
        CommonTableUtil.hideColumn(delTable, ID_INDEX);
    }

    /**
     * 获取最终选中的id数组
     * 从表格获取，而非tableData，因为tableData数据并非是最新的数据
     *
     * @return Integer[] id数组
     */
    public Integer[] getSelectIdArr() {
        int count = tableData.length; // 初次选中的数量
        int selectCount = 0; // 最终选择的数量
        for (int row = 0; row < count; row++) {
            Object selectObj = delTable.getValueAt(row, SELECT_INDEX);
            if (selectObj instanceof Boolean) {
                boolean select = (boolean) selectObj;
                if (select) {
                    selectCount++;
                }
            }
        }
        Integer[] idList = new Integer[selectCount];
        for (int row = 0, selectRow = 0; row < count; row++) {
            Object selectObj = delTable.getValueAt(row, SELECT_INDEX);
            if (selectObj instanceof Boolean) {
                boolean select = (boolean) selectObj;
                if (select) {
                    Object idObj = tableData[row][ID_INDEX];
                    if (idObj instanceof String) {
                        idList[selectRow] = Integer.parseInt((String) idObj);
                        selectRow++;
                    }
                }
            }
        }
        return idList;
    }

    /**
     * 获取 当前面板的表格
     *
     * @return JTable
     */
    public JTable getTable() {
        return delTable;
    }
}

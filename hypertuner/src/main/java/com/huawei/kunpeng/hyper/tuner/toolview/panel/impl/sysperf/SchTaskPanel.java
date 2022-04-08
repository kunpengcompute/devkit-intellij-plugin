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

import com.huawei.kunpeng.hyper.tuner.action.sysperf.SchTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.SchTaskFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.CheckBoxHeaderRender;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.SchTaskTableRenderer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * 预约任务 设置面板
 *
 * @since 2012-10-12
 */
public class SchTaskPanel extends SettingCommonConfigPanel {
    /**
     * 刷新按钮图标路径
     */
    private static final String REFRESH_PATH = "/assets/img/settings/refresh.svg";
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
    /**
     * 表格-操作栏索引
     */
    private static final int OPERATE_INDEX = 7;

    private static final long serialVersionUID = -1582031749154967508L;
    private JPanel mainPanel;
    private SchTaskAction schTaskAction;
    private List<SchTaskBean> schTaskBeanList;
    private List<SchTaskBean> showList;
    private JTable operaTable;
    private JPanel operationPanel;
    private JPanel comboBoxPanel;
    private JComboBox analysisTargetComBox;
    private String targetStr = "";
    private JComboBox taskStatusComBox;
    private String taskStatusStr = "";
    private JComboBox analysisTypeComBox;
    private String typeStr = "";
    private JLabel comboBoxLabel;
    private DefaultTableModel tableModel;
    private Project project;
    private Icon icon;
    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarForOperaTable;
    /**
     * 表头
     */
    private List<String> columnNamesList;

    private Object[][] tableData;
    private CheckBoxHeaderRender checkBoxHeader;

    /**
     * 构造函数
     */
    public SchTaskPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        registerComponentAction(); // 初始化面板内组件事件
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (schTaskAction == null) {
            schTaskAction = new SchTaskAction();
        }
        initSearch();
        schTaskBeanList = schTaskAction.getSchTaskList();
        operaTable = new JBTable();
        initTable();
        toolbarForOperaTable = ToolbarDecorator.createDecorator(operaTable);
        addActionToToolBarOperaTable(toolbarForOperaTable, operaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        operationPanel.removeAll();
        operationPanel.add(toolbarForOperaTable.createPanel());
    }

    /**
     * 重新获取数据，更新表格
     */
    public void updateTable() {
        showList.clear();
        schTaskBeanList = schTaskAction.getSchTaskList();
        if (schTaskBeanList != null) {
            showList.addAll(schTaskBeanList);
        }
        checkBoxHeader.setSelect(false);
        refreshTableData(); // 重新获取数据后，使用之前保存的筛选数据，再次筛选列表
        createOperaTableDate(showList); // 重新获取数据
        updateTableByObjData();
    }

    /**
     * 监听事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new SchTaskAction();
        }
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
     * 初始化 筛选框
     */
    private void initSearch() {
        comboBoxLabel.setText(SchTaskContent.SCREENING_TITLE + ":");

        // 任务状态
        taskStatusStr = TaskManageContent.TASK_STATUS;
        taskStatusComBox.addItem(TaskManageContent.TASK_STATUS);
        taskStatusComBox.addItem(SchTaskContent.STATUS_RESERVE);
        taskStatusComBox.addItem(SchTaskContent.STATUS_SUCCESS);
        taskStatusComBox.addItem(SchTaskContent.STATUS_RUNNING);
        taskStatusComBox.addItem(SchTaskContent.STATUS_FAIL);

        // 分析对象
        targetStr = TaskManageContent.ANALYSIS_TARGET;
        analysisTargetComBox.addItem(TaskManageContent.ANALYSIS_TARGET);
        analysisTargetComBox.addItem(TaskManageContent.ANALYSIS_TARGET_SYS);
        analysisTargetComBox.addItem(TaskManageContent.ANALYSIS_TARGET_APP);

        // 分析类型
        typeStr = TaskManageContent.ANALYSIS_TYPE;
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_OVERALL);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_MICRO);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_IO);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_PROCESS);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_CPP);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_RES_SCH);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_LOCK);
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_HPC); // HPC 分析类型
        addSearchListener();
    }

    /**
     * 通过搜索框关键字刷新 表格数据
     */
    private void refreshTableData() {
        showList.clear();
        if (schTaskBeanList != null) {
            for (SchTaskBean bean : schTaskBeanList) {
                String beanStatus = SchTaskFormatUtil.stateFormat(bean.getScheduleStatus());
                String beanType = SchTaskFormatUtil.analysisTypeFormat(bean.getAnalysisType());
                String beanTarget = SchTaskFormatUtil.analysisTargetFormat(bean.getTaskInfo().getAnalysisTarget());
                if ((taskStatusStr.equals(TaskManageContent.TASK_STATUS) || beanStatus.equals(taskStatusStr))
                    && (targetStr.equals(TaskManageContent.ANALYSIS_TARGET) || beanTarget.equals(targetStr))
                    && (typeStr.equals(TaskManageContent.ANALYSIS_TYPE) || beanType.equals(typeStr))) {
                    showList.add(bean); // 只显示关键字匹配的信息
                }
            }
        }
        createOperaTableDate(showList); // 搜索框
        updateTableByObjData();
    }

    /**
     * 初始化表格
     */
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
        createOperaTableColName();
        showList = new ArrayList<>();
        if (schTaskBeanList != null) {
            showList.addAll(schTaskBeanList);
        }
        createOperaTableDate(showList);
        updateTableByObjData();
        addDetailListener(); // 添加【查看详情】监听事件
        addHeaderListener(); // 添加【表格多选框】监听事件
    }

    /**
     * 给操作日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param table            操作日志表格
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        // 添加工具栏
        toolbarDecorator.setEditAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    editItem();
                }
            });

        toolbarDecorator.setRemoveAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    deleteSchTask();
                }
            });

        // 刷新表格
        toolbarDecorator.setAddActionName(TuningI18NServer.toLocale("plugins_hyper_tuner_refresh"));
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(REFRESH_PATH));
        toolbarDecorator.setAddAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    updateTable();
                }
            });
    }

    /**
     * 使用新的数据，更新表格
     * 如：筛选框调价刷新后，根据条件筛选后生成新数据，更新表格
     */
    private void updateTableByObjData() {
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        operaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 这只表格只可以监听一行。
        operaTable.getTableHeader().setReorderingAllowed(false); // 不可以重排列。
        operaTable.setModel(tableModel);
        // 获取多选框所在列，设置【表头-多选框】渲染器
        TableColumn selectColumn = operaTable.getColumnModel().getColumn(SELECT_INDEX);
        selectColumn.setHeaderRenderer(checkBoxHeader);
        selectColumn.setMaxWidth(30);
        // 给对应列设置渲染器。
        operaTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new SchTaskTableRenderer());
        operaTable.getColumnModel().getColumn(ANALYSIS_TARGET_INDEX).setCellRenderer(getRender(ANALYSIS_TARGET_INDEX));
        operaTable.getColumnModel().getColumn(ANALYSIS_TYPE_INDEX).setCellRenderer(getRender(ANALYSIS_TYPE_INDEX));
        operaTable.getColumnModel().getColumn(OPERATE_INDEX).setCellRenderer(getRender(OPERATE_INDEX));
        CommonTableUtil.hideColumn(operaTable, ID_INDEX);
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
                if (column == OPERATE_INDEX) {
                    setForeground(new JBColor(new Color(68, 127, 245), new Color(68, 127, 245)));
                    setText((value == null) ? "" : value.toString());
                } else if (column == ANALYSIS_TYPE_INDEX) {
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
     * 添加筛选框监听事件
     */
    private void addSearchListener() {
        taskStatusComBox.addActionListener(
            new ActionListener() {
                /**
                 * 监听【任务状态】筛选框变化
                 *
                 * @param event 事件
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    Object selectItem = taskStatusComBox.getSelectedItem();
                    if (!(selectItem instanceof String)) {
                        return;
                    }
                    taskStatusStr = comBoxHandle(event.getActionCommand(), (String) selectItem);
                    refreshTableData(); // 监听【任务状态】筛选框变化
                }
            });
        analysisTargetComBox.addActionListener(
            new ActionListener() {
                /**
                 * 监听【分析对象】筛选框变化
                 *
                 * @param event 事件
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    Object selectItem = analysisTargetComBox.getSelectedItem();
                    if (!(selectItem instanceof String)) {
                        return;
                    }
                    targetStr = comBoxHandle(event.getActionCommand(), (String) selectItem);
                    refreshTableData(); // 监听【分析对象】筛选框变化
                }
            });
        analysisTypeComBox.addActionListener(
            new ActionListener() {
                /**
                 * 监听【分析类型】筛选框变化
                 *
                 * @param event 事件
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    Object selectItem = analysisTypeComBox.getSelectedItem();
                    if (!(selectItem instanceof String)) {
                        return;
                    }
                    typeStr = comBoxHandle(event.getActionCommand(), (String) selectItem);
                    refreshTableData(); // 监听【分析类型】筛选框变化
                }
            });
    }

    /**
     * 顶部筛选框监听处理函数
     *
     * @param actionCommand comBox 监听到的事件名称
     * @param selectedItem  选择的对象
     * @return 选择对象的值
     */
    private String comBoxHandle(String actionCommand, String selectedItem) {
        String comBoxStr = "";
        if ("comboBoxChanged".equals(actionCommand)) {
            comBoxStr = selectedItem;
        }
        return comBoxStr;
    }

    /**
     * 添加表头多选框监听事件
     */
    private void addHeaderListener() {
        operaTable
            .getTableHeader()
            .addMouseListener(
                new MouseAdapter() {
                    /**
                     * 鼠标点击事件
                     *
                     *  @param event 事件
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        int col = operaTable.columnAtPoint(event.getPoint());
                        if (col == SELECT_INDEX) {
                            // 获取当前表头是否选择
                            checkBoxHeader.changeAllSelect(tableData);
                            updateTableByObjData();

                            // 多选激活删除按钮
                            boolean haveSele = getHaveSelect();
                            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
                            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSele);
                        }
                    }
                });
    }

    /**
     * 列监听点击事件-查看详情
     */
    private void addDetailListener() {
        this.operaTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * 鼠标点击事件
                 *
                 *  @param event 事件
                 */
                @Override
                public void mouseReleased(MouseEvent event) {
                    mouseEventShowDetail(event);
                }
            });
    }

    private void mouseEventShowDetail(MouseEvent event) {
        int row = operaTable.rowAtPoint(event.getPoint());
        if (operaTable.getRowCount() < 1 || row < 0) {
            return;
        }
        int col = operaTable.columnAtPoint(event.getPoint());
        if (col == OPERATE_INDEX) {
            // 添加菜单项的点击监听器
            Object schTaskId = operaTable.getValueAt(row, ID_INDEX);
            int schTaskIdInt = 0;
            if (schTaskId instanceof String) {
                schTaskIdInt = Integer.parseInt((String) schTaskId);
            }
            for (SchTaskBean bean : schTaskBeanList) {
                if (bean.getTaskId() == schTaskIdInt) {
                    schTaskAction.showDetail(bean);
                }
            }

        } else if (col == SELECT_INDEX) {
            // 及时更新tableData
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
            tableData[row][SELECT_INDEX] = selectObj;
            checkBoxHeader.updateByTableData(tableData);

            // 刷新表格
            updateTableByObjData();

            // 多选激活删除按钮
            boolean haveSele = getHaveSelect();
            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSele);
        } else {
            Logger.info("other column click");
        }
    }

    /**
     * 获取是否有选中行
     *
     * @return 获取是否有选中行
     */
    private boolean getHaveSelect() {
        int taskCount = tableData.length;
        for (int row = 0; row < taskCount; row++) {
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
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
     * 编辑任务
     */
    private void editItem() {
        int row = operaTable.getSelectedRow();
        Object statusObj = operaTable.getValueAt(row, STATUS_INDEX);
        String statusStr = "";
        if (statusObj instanceof String) {
            statusStr = (String) statusObj;
        }
        if ("reserve".equals(statusStr)) {
            Object schTaskIdObj = operaTable.getValueAt(row, ID_INDEX);
            int schTaskIdInt = 0;
            if (schTaskIdObj instanceof String) {
                schTaskIdInt = Integer.parseInt((String) schTaskIdObj);
            }
            schTaskAction.editTaskItem(schTaskIdInt);
            return;
        }
        Object taskNameObj = operaTable.getValueAt(row, NAME_INDEX);
        String taskNameStr = "";
        if (taskNameObj instanceof String) {
            taskNameStr = (String) taskNameObj;
        }
        schTaskAction.notice(taskNameStr);
    }

    /**
     * 删除任务
     */
    private void deleteSchTask() {
        int row = operaTable.getSelectedRow();
        List<SchTaskBean> multiSelectIdList = getSelectedTask();
        if (multiSelectIdList.size() > 0) {
            // 多选框 至少 选中一个任务
            schTaskAction.deleteMultiTask(multiSelectIdList);
        } else {
            // 一个都没有选,只删除单个
            deleteItem(row);
        }
        updateTable();
    }

    /**
     * 获取已选中的任务编号
     *
     * @return idList
     */
    private List<SchTaskBean> getSelectedTask() {
        int taskCount = schTaskBeanList.size();
        List<SchTaskBean> schList = new ArrayList<>();
        for (int row = 0; row < taskCount; row++) {
            if (row >= this.showList.size()) {
                break;
            }
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
            if (selectObj instanceof Boolean) {
                boolean select = (boolean) selectObj;
                if (select) {
                    SchTaskBean oneSch = new SchTaskBean();
                    Object schTaskIdObj = operaTable.getValueAt(row, ID_INDEX);
                    int schTaskIdInt = 0;
                    if (schTaskIdObj instanceof String) {
                        schTaskIdInt = Integer.parseInt((String) schTaskIdObj);
                    }
                    oneSch.setTaskId(schTaskIdInt);
                    oneSch.setTaskName(getValueAtCol(row, NAME_INDEX));
                    oneSch.setScheduleStatus(getValueAtCol(row, STATUS_INDEX));
                    oneSch.getTaskInfo().setAnalysisTarget(getValueAtCol(row, ANALYSIS_TARGET_INDEX));
                    oneSch.setAnalysisType(getValueAtCol(row, ANALYSIS_TYPE_INDEX));
                    oneSch.setProjectName(getValueAtCol(row, PRO_NAME_INDEX));
                    schList.add(oneSch);
                }
            }
        }
        return schList;
    }

    /**
     * 根据 行号列号获取 表格元素的值
     *
     * @param rowNum 行
     * @param colNum 列
     * @return String类型的 值
     */
    private String getValueAtCol(int rowNum, int colNum) {
        Object valueObj = operaTable.getValueAt(rowNum, colNum);
        String valueStr = "";
        if (valueObj instanceof String) {
            valueStr = (String) valueObj;
        }
        return valueStr;
    }

    private void deleteItem(int row) {
        Object schTaskIdObj = operaTable.getValueAt(row, ID_INDEX);
        Object schTaskNameObj = operaTable.getValueAt(row, NAME_INDEX);
        int schTaskIdInt = 0;
        String taskNameStr = "";
        if (schTaskIdObj instanceof String) {
            schTaskIdInt = Integer.parseInt((String) schTaskIdObj);
        }
        if (schTaskNameObj instanceof String) {
            taskNameStr = (String) schTaskNameObj;
        }
        schTaskAction.deleteTaskItem(schTaskIdInt, taskNameStr);
    }

    /**
     * 悬浮显示单元格内容
     *
     * @param event 点击事件
     */
    private void mouse(MouseEvent event) {
        int row = operaTable.rowAtPoint(event.getPoint());
        int col = operaTable.columnAtPoint(event.getPoint());
        if (row > -1 && col > -1) {
            Object value = operaTable.getValueAt(row, col);
            if (value != null && !"".equals(value)) {
                operaTable.setToolTipText(value.toString()); // 悬浮显示单元格内容
            }
        }
    }

    /**
     * 创建表头
     */
    private void createOperaTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(""); // 多选
        columnNamesList.add(SchTaskContent.TABLE_TASK_ID); // 任务编号
        columnNamesList.add(SchTaskContent.TABLE_TASK_NAME); // 任务名称
        columnNamesList.add(SchTaskContent.TABLE_TASK_STATUS); // 任务状态
        columnNamesList.add(SchTaskContent.TABLE_TASK_ANALYSIS_TARGET); // 分析对象
        columnNamesList.add(SchTaskContent.TABLE_TASK_ANALYSIS_TYPE); // 分析类型
        columnNamesList.add(SchTaskContent.TABLE_TASK_PROJECT_NAME); // 工程名称
        columnNamesList.add(SchTaskContent.TABLE_TASK_OPERATE); // 操作
    }

    /**
     * 创建表格数据二维数组
     *
     * @param schTaskBeanList 表格所有行数据
     * @return Object[][] 二位数组
     */
    private void createOperaTableDate(List<SchTaskBean> schTaskBeanList) {
        tableData = new Object[schTaskBeanList.size()][];
        for (int i = 0; i < schTaskBeanList.size(); i++) {
            List<Object> itemList = new ArrayList<>();
            itemList.add(new Boolean(false));
            itemList.add(schTaskBeanList.get(i).getTaskId() + "");
            itemList.add(schTaskBeanList.get(i).getTaskName()); // 任务名称
            itemList.add(schTaskBeanList.get(i).getScheduleStatus()); // 任务状态
            itemList.add(schTaskBeanList.get(i).getTaskInfo().getAnalysisTarget()); // 分析对象
            itemList.add(schTaskBeanList.get(i).getAnalysisType()); // 分析类型
            itemList.add(schTaskBeanList.get(i).getProjectName()); // 工程名称
            itemList.add(SchTaskContent.OPERATE_DETAIL); // 操作
            tableData[i] = itemList.toArray();
        }
    }
}

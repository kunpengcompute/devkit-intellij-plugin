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

import com.huawei.kunpeng.hyper.tuner.action.sysperf.TaskTemplateAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskTemplateContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.TaskTemplateFormatUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.CheckBoxHeaderRender;
import com.huawei.kunpeng.intellij.common.log.Logger;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * 任务模板设置面板
 *
 * @since 2012-10-12
 */
public class TaskTemplatePanel extends SettingCommonConfigPanel {
    private static final long serialVersionUID = -1582031749154967508L;
    /**
     * 表格索引-多选框
     */
    private static final int SELECT_INDEX = 0;
    /**
     * 表格索引-编号
     */
    private static final int ID_INDEX = 1;
    /**
     * 表格索引-模板名称
     */
    private static final int TEMP_NAME_INDEX = 2;
    /**
     * 表格索引-分析对象
     */
    private static final int ANALYSIS_TARGET_INDEX = 3;
    /**
     * 表格索引-分析类型
     */
    private static final int ANALYSIS_TYPE_INDEX = 4;
    /**
     * 表格索引-操作栏
     */
    private static final int OPERATE_INDEX = 5;
    /**
     * 表头-自定义多选框
     */
    private CheckBoxHeaderRender checkBoxHeader;

    private JPanel mainPanel;

    private TaskTemplateAction taskTemplateAction;

    private List<TaskTemplateBean> taskTemplateBeans;

    private JTable operaTable;

    private JPanel operationPanel;

    private DefaultTableModel tableModel;

    private Project project;

    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarForOperaTable;

    /**
     * 创建表头
     */
    private List<String> columnNamesList;

    private Object[][] tableData;

    /**
     * 构造函数
     */
    public TaskTemplatePanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (taskTemplateAction == null) {
            taskTemplateAction = new TaskTemplateAction();
        }
        taskTemplateBeans = taskTemplateAction.getTaskList();

        checkBoxHeader = new CheckBoxHeaderRender();
        checkBoxHeader.setBackground(null);
        operaTable = new JBTable();
        createOperaTableColName();
        initOperat();
        addListener();
        addHeaderListener(); // 添加【表格多选框】监听事件
        toolbarForOperaTable = ToolbarDecorator.createDecorator(operaTable);
        addActionToToolBarOperaTable(toolbarForOperaTable, operaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        operationPanel.removeAll();
        operationPanel.add(toolbarForOperaTable.createPanel());
    }

    /**
     * 监听事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new TaskTemplateAction();
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
     * 给表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param table            操作日志表格
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setRemoveAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    multiDelete();
                }
            });
    }

    /**
     * 删除任务
     */
    private void multiDelete() {
        int row = operaTable.getSelectedRow();
        List<TaskTemplateBean> multiSelectIdList = getSelectedTemp();
        if (multiSelectIdList.size() > 0) {
            // 多选框 至少 选中一个任务
            taskTemplateAction.deleteMultiTask(multiSelectIdList);
        } else {
            // 一个都没有选,只删除单个
            deleteItem(row);
        }
        checkBoxHeader.setSelect(false);
        updateTable();
    }

    private void deleteItem(int row) {
        Object taskId = operaTable.getValueAt(row, ID_INDEX);
        Object tempNameObj = operaTable.getValueAt(row, TEMP_NAME_INDEX);
        int taskIdInt = 0;
        if (taskId instanceof String) {
            taskIdInt = Integer.parseInt((String) taskId);
        }
        String tempNameStr = "";
        if (tempNameObj instanceof String) {
            tempNameStr = (String) tempNameObj;
        }
        taskTemplateAction.deleteTempItem(taskIdInt, tempNameStr);
    }

    /**
     * 获取已选中的任务模板编号
     *
     * @return idList
     */
    private List<TaskTemplateBean> getSelectedTemp() {
        int taskCount = taskTemplateBeans.size();
        List<TaskTemplateBean> schList = new ArrayList<>();
        for (int row = 0; row < taskCount; row++) {
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
            if (selectObj instanceof Boolean) {
                boolean select = (boolean) selectObj;
                if (select) {
                    TaskTemplateBean oneSch = new TaskTemplateBean();
                    Object idObj = operaTable.getValueAt(row, ID_INDEX);
                    int idInt = 0;
                    if (idObj instanceof String) {
                        idInt = Integer.parseInt((String) idObj);
                    }
                    oneSch.setId(idInt);
                    oneSch.setTemplateName(getValueAtCol(row, TEMP_NAME_INDEX));
                    oneSch.setAnalysisTarget(getValueAtCol(row, ANALYSIS_TARGET_INDEX));
                    oneSch.setAnalysisType(getValueAtCol(row, ANALYSIS_TYPE_INDEX));

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

    /**
     * 设置和更新表格数据
     */
    private void updateTable() {
        taskTemplateBeans = taskTemplateAction.getTaskList();
        initOperat();
    }

    /**
     * 初始化操作日志
     */
    private void initOperat() {
        // 创建表格模型，指定所有行数据 和 表头
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

        // 这只表格只可以监听一行。
        operaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 不可以重排列。
        operaTable.getTableHeader().setReorderingAllowed(false);
        createOperaTableDate(taskTemplateBeans);
        updateTableByData();
    }

    private void updateTableByData() {
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        operaTable.setModel(tableModel);
        // 获取多选框所在列，设置【表头-多选框】渲染器
        TableColumn selectColumn = operaTable.getColumnModel().getColumn(SELECT_INDEX);
        selectColumn.setHeaderRenderer(checkBoxHeader);
        selectColumn.setMaxWidth(30);
        CommonTableUtil.hideColumn(operaTable, ID_INDEX);
        // 给操作列设置渲染器。
        operaTable
            .getColumnModel()
            .getColumn(OPERATE_INDEX)
            .setCellRenderer(
                new DefaultTableCellRenderer() {
                    /**
                     * 重写setValue方法，从而可以动态设置列单元字体颜色
                     * 将 查看详情 设置为蓝色
                     *
                     * @param value 值
                     */
                    @Override
                    public void setValue(Object value) {
                        setForeground(new JBColor(new Color(68, 127, 245), new Color(68, 127, 245)));
                        setText((value == null) ? "" : value.toString());
                    }
                });
    }

    /**
     * 列监听时间
     */
    private void addListener() {
        this.operaTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * 鼠标点击事件 #447ff5
                 *
                 *  @param event 事件
                 */
                @Override
                public void mouseReleased(MouseEvent event) {
                    mouseEventShowDetail(event);
                }
            });
    }

    private void mouseEventShowDetail(MouseEvent mouseEvent) {
        int row = operaTable.rowAtPoint(mouseEvent.getPoint());
        if (operaTable.getRowCount() < 1 || row < 0) {
            return;
        }
        int col = operaTable.columnAtPoint(mouseEvent.getPoint());
        if (col == OPERATE_INDEX) {
            // 添加菜单项的点击监听器
            Object schTaskId = operaTable.getValueAt(row, ID_INDEX);
            int schTaskIdInt = 0;
            if (schTaskId instanceof String) {
                schTaskIdInt = Integer.parseInt((String) schTaskId);
            }
            for (TaskTemplateBean bean : taskTemplateBeans) {
                if (bean.getId() == schTaskIdInt) {
                    taskTemplateAction.showDetail(bean);
                }
            }
        } else if (col == SELECT_INDEX) {
            // 及时更新tableData
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
            tableData[row][SELECT_INDEX] = selectObj;
            checkBoxHeader.updateByTableData(tableData);
            updateTableByData();

            // 多选激活删除按钮
            boolean haveSelect = getHaveSelect();
            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSelect);
        } else {
            Logger.info("other column click");
        }
    }

    /**
     * 获取是否有选中行
     *
     * @return 是否有选中行
     */
    private boolean getHaveSelect() {
        int taskCount = tableData.length;
        for (int row = 0; row < taskCount; row++) {
            Object selectObject = operaTable.getValueAt(row, SELECT_INDEX);
            if (selectObject instanceof Boolean) {
                boolean select = (boolean) selectObject;
                if (select) {
                    return true;
                }
            }
        }
        return false;
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
                            updateTableByData();

                            // 多选激活删除按钮
                            boolean haveSele = getHaveSelect();
                            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
                            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSele);
                        }
                    }
                });
    }

    private void createOperaTableColName() {
        // 创建表头
        columnNamesList = new ArrayList<>();
        columnNamesList.add(""); // 多选框
        columnNamesList.add(TaskTemplateContent.ID);
        columnNamesList.add(TaskTemplateContent.TEMPLATE_NAME);
        columnNamesList.add(TaskManageContent.ANALYSIS_TARGET);
        columnNamesList.add(TaskManageContent.ANALYSIS_TYPE);
        columnNamesList.add(TaskTemplateContent.TABLE_OPERATE);
    }

    /**
     * 创建表格数据二维数组
     *
     * @param templateBeans 表格所有行数据
     * @return Object[][] 二位数组
     */
    private void createOperaTableDate(List<TaskTemplateBean> templateBeans) {
        tableData = new Object[templateBeans.size()][];
        for (int i = 0; i < templateBeans.size(); i++) {
            TaskTemplateBean bean = templateBeans.get(i);
            List<Object> itemList = new ArrayList<>();
            itemList.add(new Boolean(false)); // 多选框
            itemList.add(bean.getId() + ""); // 编号
            itemList.add(bean.getTemplateName()); // 模板名称
            itemList.add(TaskTemplateFormatUtil.analysisTargetFormat(bean.getAnalysisTarget())); // 分析对象
            itemList.add(TaskTemplateFormatUtil.analysisTypeFormat(bean.getAnalysisType())); // 分析类型
            itemList.add(SchTaskContent.OPERATE_DETAIL); // 操作
            tableData[i] = itemList.toArray();
        }
    }
}

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
 * ???????????? ????????????
 *
 * @since 2012-10-12
 */
public class SchTaskPanel extends SettingCommonConfigPanel {
    /**
     * ????????????????????????
     */
    private static final String REFRESH_PATH = "/assets/img/settings/refresh.svg";
    /**
     * ??????- ???????????????
     */
    private static final int SELECT_INDEX = 0;
    /**
     * ??????-????????????
     */
    private static final int ID_INDEX = 1;
    /**
     * ??????-????????????
     */
    private static final int NAME_INDEX = 2;
    /**
     * ??????-????????????
     */
    private static final int STATUS_INDEX = 3;
    /**
     * ??????-??????????????????
     */
    private static final int ANALYSIS_TARGET_INDEX = 4;
    /**
     * ??????-??????????????????
     */
    private static final int ANALYSIS_TYPE_INDEX = 5;
    /**
     * ??????-??????????????????
     */
    private static final int PRO_NAME_INDEX = 6;
    /**
     * ??????-???????????????
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
     * ???????????????
     */
    private ToolbarDecorator toolbarForOperaTable;
    /**
     * ??????
     */
    private List<String> columnNamesList;

    private Object[][] tableData;
    private CheckBoxHeaderRender checkBoxHeader;

    /**
     * ????????????
     */
    public SchTaskPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        registerComponentAction(); // ??????????????????????????????
    }

    /**
     * ???????????????
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
     * ?????????????????????????????????
     */
    public void updateTable() {
        showList.clear();
        schTaskBeanList = schTaskAction.getSchTaskList();
        if (schTaskBeanList != null) {
            showList.addAll(schTaskBeanList);
        }
        checkBoxHeader.setSelect(false);
        refreshTableData(); // ??????????????????????????????????????????????????????????????????????????????
        createOperaTableDate(showList); // ??????????????????
        updateTableByObjData();
    }

    /**
     * ????????????
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new SchTaskAction();
        }
    }

    /**
     * ??????????????????
     *
     * @return mainPanel
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * ????????? ?????????
     */
    private void initSearch() {
        comboBoxLabel.setText(SchTaskContent.SCREENING_TITLE + ":");

        // ????????????
        taskStatusStr = TaskManageContent.TASK_STATUS;
        taskStatusComBox.addItem(TaskManageContent.TASK_STATUS);
        taskStatusComBox.addItem(SchTaskContent.STATUS_RESERVE);
        taskStatusComBox.addItem(SchTaskContent.STATUS_SUCCESS);
        taskStatusComBox.addItem(SchTaskContent.STATUS_RUNNING);
        taskStatusComBox.addItem(SchTaskContent.STATUS_FAIL);

        // ????????????
        targetStr = TaskManageContent.ANALYSIS_TARGET;
        analysisTargetComBox.addItem(TaskManageContent.ANALYSIS_TARGET);
        analysisTargetComBox.addItem(TaskManageContent.ANALYSIS_TARGET_SYS);
        analysisTargetComBox.addItem(TaskManageContent.ANALYSIS_TARGET_APP);

        // ????????????
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
        analysisTypeComBox.addItem(TaskManageContent.ANALYSIS_TYPE_HPC); // HPC ????????????
        addSearchListener();
    }

    /**
     * ?????????????????????????????? ????????????
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
                    showList.add(bean); // ?????????????????????????????????
                }
            }
        }
        createOperaTableDate(showList); // ?????????
        updateTableByObjData();
    }

    /**
     * ???????????????
     */
    private void initTable() {
        // ??????????????????????????????????????????
        tableModel =
            new DefaultTableModel() {
                private static final long serialVersionUID = -2579019475997889830L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    // ?????????????????? ?????????
                    return column == SELECT_INDEX;
                }

                @Override
                public Class getColumnClass(int column) {
                    // SELECT_INDEX ????????????
                    if (column == SELECT_INDEX) {
                        return Boolean.class;
                    }
                    return String.class;
                }
            };
        checkBoxHeader = new CheckBoxHeaderRender(); // ?????????????????????
        checkBoxHeader.setBackground(null);

        // ????????????
        createOperaTableColName();
        showList = new ArrayList<>();
        if (schTaskBeanList != null) {
            showList.addAll(schTaskBeanList);
        }
        createOperaTableDate(showList);
        updateTableByObjData();
        addDetailListener(); // ????????????????????????????????????
        addHeaderListener(); // ???????????????????????????????????????
    }

    /**
     * ????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        // ???????????????
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

        // ????????????
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
     * ?????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????
     */
    private void updateTableByObjData() {
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        operaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ????????????????????????????????????
        operaTable.getTableHeader().setReorderingAllowed(false); // ?????????????????????
        operaTable.setModel(tableModel);
        // ??????????????????????????????????????????-?????????????????????
        TableColumn selectColumn = operaTable.getColumnModel().getColumn(SELECT_INDEX);
        selectColumn.setHeaderRenderer(checkBoxHeader);
        selectColumn.setMaxWidth(30);
        // ??????????????????????????????
        operaTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new SchTaskTableRenderer());
        operaTable.getColumnModel().getColumn(ANALYSIS_TARGET_INDEX).setCellRenderer(getRender(ANALYSIS_TARGET_INDEX));
        operaTable.getColumnModel().getColumn(ANALYSIS_TYPE_INDEX).setCellRenderer(getRender(ANALYSIS_TYPE_INDEX));
        operaTable.getColumnModel().getColumn(OPERATE_INDEX).setCellRenderer(getRender(OPERATE_INDEX));
        CommonTableUtil.hideColumn(operaTable, ID_INDEX);
    }

    private DefaultTableCellRenderer getRender(int column) {
        return new DefaultTableCellRenderer() {
            /**
             * ??????setValue??????????????????????????????????????????????????????
             *
             * @param value ????????????
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
     * ???????????????????????????
     */
    private void addSearchListener() {
        taskStatusComBox.addActionListener(
            new ActionListener() {
                /**
                 * ???????????????????????????????????????
                 *
                 * @param event ??????
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    Object selectItem = taskStatusComBox.getSelectedItem();
                    if (!(selectItem instanceof String)) {
                        return;
                    }
                    taskStatusStr = comBoxHandle(event.getActionCommand(), (String) selectItem);
                    refreshTableData(); // ???????????????????????????????????????
                }
            });
        analysisTargetComBox.addActionListener(
            new ActionListener() {
                /**
                 * ???????????????????????????????????????
                 *
                 * @param event ??????
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    Object selectItem = analysisTargetComBox.getSelectedItem();
                    if (!(selectItem instanceof String)) {
                        return;
                    }
                    targetStr = comBoxHandle(event.getActionCommand(), (String) selectItem);
                    refreshTableData(); // ???????????????????????????????????????
                }
            });
        analysisTypeComBox.addActionListener(
            new ActionListener() {
                /**
                 * ???????????????????????????????????????
                 *
                 * @param event ??????
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    Object selectItem = analysisTypeComBox.getSelectedItem();
                    if (!(selectItem instanceof String)) {
                        return;
                    }
                    typeStr = comBoxHandle(event.getActionCommand(), (String) selectItem);
                    refreshTableData(); // ???????????????????????????????????????
                }
            });
    }

    /**
     * ?????????????????????????????????
     *
     * @param actionCommand comBox ????????????????????????
     * @param selectedItem  ???????????????
     * @return ??????????????????
     */
    private String comBoxHandle(String actionCommand, String selectedItem) {
        String comBoxStr = "";
        if ("comboBoxChanged".equals(actionCommand)) {
            comBoxStr = selectedItem;
        }
        return comBoxStr;
    }

    /**
     * ?????????????????????????????????
     */
    private void addHeaderListener() {
        operaTable
            .getTableHeader()
            .addMouseListener(
                new MouseAdapter() {
                    /**
                     * ??????????????????
                     *
                     *  @param event ??????
                     */
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        int col = operaTable.columnAtPoint(event.getPoint());
                        if (col == SELECT_INDEX) {
                            // ??????????????????????????????
                            checkBoxHeader.changeAllSelect(tableData);
                            updateTableByObjData();

                            // ????????????????????????
                            boolean haveSele = getHaveSelect();
                            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
                            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSele);
                        }
                    }
                });
    }

    /**
     * ?????????????????????-????????????
     */
    private void addDetailListener() {
        this.operaTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * ??????????????????
                 *
                 *  @param event ??????
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
            // ?????????????????????????????????
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
            // ????????????tableData
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
            tableData[row][SELECT_INDEX] = selectObj;
            checkBoxHeader.updateByTableData(tableData);

            // ????????????
            updateTableByObjData();

            // ????????????????????????
            boolean haveSele = getHaveSelect();
            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSele);
        } else {
            Logger.info("other column click");
        }
    }

    /**
     * ????????????????????????
     *
     * @return ????????????????????????
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
     * ????????????
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
     * ????????????
     */
    private void deleteSchTask() {
        int row = operaTable.getSelectedRow();
        List<SchTaskBean> multiSelectIdList = getSelectedTask();
        if (multiSelectIdList.size() > 0) {
            // ????????? ?????? ??????????????????
            schTaskAction.deleteMultiTask(multiSelectIdList);
        } else {
            // ??????????????????,???????????????
            deleteItem(row);
        }
        updateTable();
    }

    /**
     * ??????????????????????????????
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
     * ?????? ?????????????????? ??????????????????
     *
     * @param rowNum ???
     * @param colNum ???
     * @return String????????? ???
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
     * ???????????????????????????
     *
     * @param event ????????????
     */
    private void mouse(MouseEvent event) {
        int row = operaTable.rowAtPoint(event.getPoint());
        int col = operaTable.columnAtPoint(event.getPoint());
        if (row > -1 && col > -1) {
            Object value = operaTable.getValueAt(row, col);
            if (value != null && !"".equals(value)) {
                operaTable.setToolTipText(value.toString()); // ???????????????????????????
            }
        }
    }

    /**
     * ????????????
     */
    private void createOperaTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(""); // ??????
        columnNamesList.add(SchTaskContent.TABLE_TASK_ID); // ????????????
        columnNamesList.add(SchTaskContent.TABLE_TASK_NAME); // ????????????
        columnNamesList.add(SchTaskContent.TABLE_TASK_STATUS); // ????????????
        columnNamesList.add(SchTaskContent.TABLE_TASK_ANALYSIS_TARGET); // ????????????
        columnNamesList.add(SchTaskContent.TABLE_TASK_ANALYSIS_TYPE); // ????????????
        columnNamesList.add(SchTaskContent.TABLE_TASK_PROJECT_NAME); // ????????????
        columnNamesList.add(SchTaskContent.TABLE_TASK_OPERATE); // ??????
    }

    /**
     * ??????????????????????????????
     *
     * @param schTaskBeanList ?????????????????????
     * @return Object[][] ????????????
     */
    private void createOperaTableDate(List<SchTaskBean> schTaskBeanList) {
        tableData = new Object[schTaskBeanList.size()][];
        for (int i = 0; i < schTaskBeanList.size(); i++) {
            List<Object> itemList = new ArrayList<>();
            itemList.add(new Boolean(false));
            itemList.add(schTaskBeanList.get(i).getTaskId() + "");
            itemList.add(schTaskBeanList.get(i).getTaskName()); // ????????????
            itemList.add(schTaskBeanList.get(i).getScheduleStatus()); // ????????????
            itemList.add(schTaskBeanList.get(i).getTaskInfo().getAnalysisTarget()); // ????????????
            itemList.add(schTaskBeanList.get(i).getAnalysisType()); // ????????????
            itemList.add(schTaskBeanList.get(i).getProjectName()); // ????????????
            itemList.add(SchTaskContent.OPERATE_DETAIL); // ??????
            tableData[i] = itemList.toArray();
        }
    }
}

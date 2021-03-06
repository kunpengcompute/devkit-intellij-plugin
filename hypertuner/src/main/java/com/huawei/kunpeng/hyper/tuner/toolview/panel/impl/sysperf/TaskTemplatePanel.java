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
 * ????????????????????????
 *
 * @since 2012-10-12
 */
public class TaskTemplatePanel extends SettingCommonConfigPanel {
    private static final long serialVersionUID = -1582031749154967508L;
    /**
     * ????????????-?????????
     */
    private static final int SELECT_INDEX = 0;
    /**
     * ????????????-??????
     */
    private static final int ID_INDEX = 1;
    /**
     * ????????????-????????????
     */
    private static final int TEMP_NAME_INDEX = 2;
    /**
     * ????????????-????????????
     */
    private static final int ANALYSIS_TARGET_INDEX = 3;
    /**
     * ????????????-????????????
     */
    private static final int ANALYSIS_TYPE_INDEX = 4;
    /**
     * ????????????-?????????
     */
    private static final int OPERATE_INDEX = 5;
    /**
     * ??????-??????????????????
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
     * ???????????????
     */
    private ToolbarDecorator toolbarForOperaTable;

    /**
     * ????????????
     */
    private List<String> columnNamesList;

    private Object[][] tableData;

    /**
     * ????????????
     */
    public TaskTemplatePanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // ??????????????????????????????
        registerComponentAction();
    }

    /**
     * ???????????????
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
        addHeaderListener(); // ???????????????????????????????????????
        toolbarForOperaTable = ToolbarDecorator.createDecorator(operaTable);
        addActionToToolBarOperaTable(toolbarForOperaTable, operaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        operationPanel.removeAll();
        operationPanel.add(toolbarForOperaTable.createPanel());
    }

    /**
     * ????????????
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new TaskTemplateAction();
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
     * ????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
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
     * ????????????
     */
    private void multiDelete() {
        int row = operaTable.getSelectedRow();
        List<TaskTemplateBean> multiSelectIdList = getSelectedTemp();
        if (multiSelectIdList.size() > 0) {
            // ????????? ?????? ??????????????????
            taskTemplateAction.deleteMultiTask(multiSelectIdList);
        } else {
            // ??????????????????,???????????????
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
     * ????????????????????????????????????
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

    /**
     * ???????????????????????????
     */
    private void updateTable() {
        taskTemplateBeans = taskTemplateAction.getTaskList();
        initOperat();
    }

    /**
     * ?????????????????????
     */
    private void initOperat() {
        // ?????????????????????????????????????????? ??? ??????
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

        // ????????????????????????????????????
        operaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ?????????????????????
        operaTable.getTableHeader().setReorderingAllowed(false);
        createOperaTableDate(taskTemplateBeans);
        updateTableByData();
    }

    private void updateTableByData() {
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        operaTable.setModel(tableModel);
        // ??????????????????????????????????????????-?????????????????????
        TableColumn selectColumn = operaTable.getColumnModel().getColumn(SELECT_INDEX);
        selectColumn.setHeaderRenderer(checkBoxHeader);
        selectColumn.setMaxWidth(30);
        CommonTableUtil.hideColumn(operaTable, ID_INDEX);
        // ??????????????????????????????
        operaTable
            .getColumnModel()
            .getColumn(OPERATE_INDEX)
            .setCellRenderer(
                new DefaultTableCellRenderer() {
                    /**
                     * ??????setValue??????????????????????????????????????????????????????
                     * ??? ???????????? ???????????????
                     *
                     * @param value ???
                     */
                    @Override
                    public void setValue(Object value) {
                        setForeground(new JBColor(new Color(68, 127, 245), new Color(68, 127, 245)));
                        setText((value == null) ? "" : value.toString());
                    }
                });
    }

    /**
     * ???????????????
     */
    private void addListener() {
        this.operaTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * ?????????????????? #447ff5
                 *
                 *  @param event ??????
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
            // ?????????????????????????????????
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
            // ????????????tableData
            Object selectObj = operaTable.getValueAt(row, SELECT_INDEX);
            tableData[row][SELECT_INDEX] = selectObj;
            checkBoxHeader.updateByTableData(tableData);
            updateTableByData();

            // ????????????????????????
            boolean haveSelect = getHaveSelect();
            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSelect);
        } else {
            Logger.info("other column click");
        }
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
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
                            updateTableByData();

                            // ????????????????????????
                            boolean haveSele = getHaveSelect();
                            final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
                            actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, haveSele);
                        }
                    }
                });
    }

    private void createOperaTableColName() {
        // ????????????
        columnNamesList = new ArrayList<>();
        columnNamesList.add(""); // ?????????
        columnNamesList.add(TaskTemplateContent.ID);
        columnNamesList.add(TaskTemplateContent.TEMPLATE_NAME);
        columnNamesList.add(TaskManageContent.ANALYSIS_TARGET);
        columnNamesList.add(TaskManageContent.ANALYSIS_TYPE);
        columnNamesList.add(TaskTemplateContent.TABLE_OPERATE);
    }

    /**
     * ??????????????????????????????
     *
     * @param templateBeans ?????????????????????
     * @return Object[][] ????????????
     */
    private void createOperaTableDate(List<TaskTemplateBean> templateBeans) {
        tableData = new Object[templateBeans.size()][];
        for (int i = 0; i < templateBeans.size(); i++) {
            TaskTemplateBean bean = templateBeans.get(i);
            List<Object> itemList = new ArrayList<>();
            itemList.add(new Boolean(false)); // ?????????
            itemList.add(bean.getId() + ""); // ??????
            itemList.add(bean.getTemplateName()); // ????????????
            itemList.add(TaskTemplateFormatUtil.analysisTargetFormat(bean.getAnalysisTarget())); // ????????????
            itemList.add(TaskTemplateFormatUtil.analysisTypeFormat(bean.getAnalysisType())); // ????????????
            itemList.add(SchTaskContent.OPERATE_DETAIL); // ??????
            tableData[i] = itemList.toArray();
        }
    }
}

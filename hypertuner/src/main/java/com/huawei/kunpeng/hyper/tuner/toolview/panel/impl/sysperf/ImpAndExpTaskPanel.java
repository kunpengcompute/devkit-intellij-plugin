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

import com.huawei.kunpeng.hyper.tuner.action.panel.LogAction;
import com.huawei.kunpeng.hyper.tuner.action.sysperf.ImpAndExpTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.ImpAndExpTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.SysperfTableRenderer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * ????????????????????????
 *
 * @since 2012-10-12
 */
public class ImpAndExpTaskPanel extends SettingCommonConfigPanel {
    /**
     * ??????????????????
     */
    private static final String DOWNLOAD_PATH = "/assets/img/settings/download.svg";

    // ????????????
    private static final String DELETE_PATH = "/assets/img/delete.png";
    /**
     * ???????????????-??????
     */
    private static final int ID_INDEX = 0;
    /**
     * ???????????????-????????????
     */
    private static final int TASK_NAME_INDEX = 1;
    /**
     * ???????????????-????????????
     */
    private static final int PROJECT_NAME_INDEX = 2;
    /**
     * ???????????????-??????????????????/??????
     */
    private static final int TYPE_INDEX = 3;
    /**
     * ???????????????-????????????
     */
    private static final int STATUS_INDEX = 4;
    /**
     * ???????????????-????????????
     */
    private static final int STATUS_DETAIL_INDEX = 5;
    /**
     * ???????????????-????????????
     */
    private static final int SIZE_INDEX = 6;
    /**
     * ???????????????-????????????
     */
    private static final int START_TIME_INDEX = 7;
    /**
     * ???????????????-????????????
     */
    private static final int END_TIME_INDEX = 8;
    /**
     * ???????????????-??????????????????
     */
    private static final int USER_ID_INDEX = 9;

    /**
     * ????????????????????????????????????key???
     */
    private static final String OPERA_LOG_NAME = "/ImpAndExpTask.csv";

    private static final int BUTTON_LENGTH = 2;

    private static final long serialVersionUID = -1582031749154967508L;

    // ????????????????????????????????????
    private boolean isAdmin = TuningUserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole());
    private String loginUserId;
    private JPanel panel1;

    private JPanel mainPanel;

    private ImpAndExpTaskAction impAndExpTaskAction;

    private List<ImpAndExpTaskBean> impAndExpTaskBeans;

    private JTable impAndExpTaskTable;

    private JPanel ImpAndExpTaskPanel;

    private JPanel runPanel;

    private DefaultTableModel tableModel;

    private Project project;

    private Icon icon;

    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarForImpAndExpTaskTable;

    private AnActionButton reTryBtn;
    /**
     * ????????????
     */
    private Vector<String> columnNamesList;

    private Object[][] tableData;

    /**
     * ????????????
     */
    public ImpAndExpTaskPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // ??????????????????????????????
        registerComponentAction();
    }

    /**
     * ???????????????
     */
    public void initPanel() {
        loginUserId = UserInfoContext.getInstance().getLoginId();
        super.initPanel(mainPanel);
        if (impAndExpTaskAction == null) {
            impAndExpTaskAction = new ImpAndExpTaskAction();
        }
        impAndExpTaskBeans = impAndExpTaskAction.getTaskList();
        impAndExpTaskTable = new JBTable();

        // ?????????????????????????????????
        createOperaTableColName();

        // ?????? ????????????????????? ??????????????? ??? ??????
        tableModel =
            new DefaultTableModel() {
                private static final long serialVersionUID = -2579019475997889830L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        initTable();
        addListenerToTable(); // ???????????????????????????????????? ??????????????????????????????????????????
        toolbarForImpAndExpTaskTable = ToolbarDecorator.createDecorator(impAndExpTaskTable);
        addActionToToolBarImpAndExpTaskTable(toolbarForImpAndExpTaskTable, impAndExpTaskTable);
        toolbarForImpAndExpTaskTable.setPreferredSize(new Dimension(300, 300));
        ImpAndExpTaskPanel.removeAll();
        ImpAndExpTaskPanel.add(toolbarForImpAndExpTaskTable.createPanel());
    }

    /**
     * ???????????????????????????
     */
    public void updateTable() {
        impAndExpTaskBeans.clear();
        impAndExpTaskBeans = impAndExpTaskAction.getTaskList();
        initTable();
    }

    /**
     * ??? ???????????? ?????????????????????,?????????????????????????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
     */
    private void addActionToToolBarImpAndExpTaskTable(ToolbarDecorator toolbarDecorator, JTable table) {
        reTryBtn =
            new AnActionButton() {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    reTryTask();
                }
            };
        reTryBtn.setEnabled(false);
        toolbarDecorator.addExtraAction(reTryBtn);
        toolbarDecorator.setAddActionName(ImpAndExpTaskContent.DOWNLOAD_ALL);
        toolbarDecorator.setAddAction(anActionButton -> downloadTaskItemFile(impAndExpTaskTable));
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(DOWNLOAD_PATH));
        toolbarDecorator.setRemoveActionName(ImpAndExpTaskContent.DELETE_ITEM);
        toolbarDecorator.setRemoveAction(anA -> deleteImpAndExpTask(impAndExpTaskTable));
        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(table, true));
        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(table, false));
    }

    /**
     * ???????????????????????????
     */
    private void addListenerToTable() {
        impAndExpTaskTable
            .getSelectionModel()
            .addListSelectionListener(
                impAndExp -> {
                    int row = impAndExpTaskTable.getSelectedRow();
                    final CommonActionsPanel actionsPanel = toolbarForImpAndExpTaskTable.getActionsPanel();
                    Integer userIdInt = getIntegerValueAtTable(row, USER_ID_INDEX);
                    String status = getStringValueAtTable(row, STATUS_INDEX);
                    if (!loginUserId.equals(userIdInt + "")) {
                        // ???????????????????????????????????????????????????????????????????????????????????????
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, false);
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, false); // ????????????
                        reTryBtn.setEnabled(false);
                    } else {
                        // ???????????????????????????????????? ????????????
                        boolean isCheckFail = status.equals(ImpAndExpTaskContent.STATUS_IMP_UPLOAD_CHECK_FAIL);
                        reTryBtn.setEnabled(isCheckFail);

                        // ??????????????????????????????
                        boolean isExpSuccess = status.equals(ImpAndExpTaskContent.STATUS_EXP_SUCCESS);
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, isExpSuccess);
                    }
                    if (isAdmin) {
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, true);
                        // ???????????????????????????????????? ????????????
                        boolean isCheckFail = status.equals(ImpAndExpTaskContent.STATUS_IMP_UPLOAD_CHECK_FAIL);
                        reTryBtn.setEnabled(isCheckFail);

                        // ??????????????????????????????
                        boolean isExpSuccess = status.equals(ImpAndExpTaskContent.STATUS_EXP_SUCCESS);
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, isExpSuccess);
                    }
                });
    }

    /**
     * ???????????????????????????
     */
    public void cancelGrayed() {
    }

    /**
     * ????????????
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new LogAction();
        }
    }

    private void reTryTask() {
        int row = impAndExpTaskTable.getSelectedRow();
        int idInt = getIntegerValueAtTable(row, ID_INDEX);
        String taskNameStr = getStringValueAtTable(row, TASK_NAME_INDEX);
        String projectNameStr = getStringValueAtTable(row, PROJECT_NAME_INDEX);
        impAndExpTaskAction.reTryTask(projectNameStr, taskNameStr, idInt);
        updateTable();
    }

    private void downloadTaskItemFile(JTable impAndExpTaskTable) {
        int row = impAndExpTaskTable.getSelectedRow();
        int idInt = getIntegerValueAtTable(row, ID_INDEX);
        int fileSectionQty = 1;
        String fileName = "";
        String fileSize = "";
        for (ImpAndExpTaskBean bean : impAndExpTaskBeans) {
            if (bean.getId() == idInt) {
                fileSectionQty = bean.getFileSectionQty();
                fileName = bean.getFileName();
                fileSize = bean.getTaskFilesize();
            }
        }
        impAndExpTaskAction.downloadTaskItemFile(idInt, fileSectionQty, fileName, fileSize);
    }

    private void deleteImpAndExpTask(JTable impAndExpTaskTable) {
        int row = impAndExpTaskTable.getSelectedRow();
        int taskIdInt = getIntegerValueAtTable(row, ID_INDEX);
        impAndExpTaskAction.deleteTaskItem(taskIdInt);
        updateTable();
    }

    /**
     * ???????????????
     *
     * @param row row
     * @param col col
     * @return ????????????
     */
    private Integer getIntegerValueAtTable(int row, int col) {
        Object intObj = impAndExpTaskTable.getValueAt(row, col);
        int intInt = 0;
        if (intObj instanceof String) {
            intInt = Integer.parseInt((String) intObj);
        }
        if (intObj instanceof Integer) {
            intInt = (Integer) intObj;
        }
        return intInt;
    }

    private String getStringValueAtTable(int row, int col) {
        Object strObj = impAndExpTaskTable.getValueAt(row, col);
        String strStr = "";
        if (strObj instanceof String) {
            strStr = (String) strObj;
        }
        return strStr;
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
     * ????????? ????????????????????????
     */
    private void initTable() {
        createOperaTableDate(impAndExpTaskBeans);
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        impAndExpTaskTable.setModel(tableModel);

        // ????????????????????????????????????
        impAndExpTaskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // ?????????????????????
        impAndExpTaskTable.getTableHeader().setReorderingAllowed(false);

        // ??????????????????????????????
        impAndExpTaskTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new SysperfTableRenderer(
            "ImpAndExpTable"));

        // ???????????? ??????
        impAndExpTaskTable.getColumnModel().getColumn(TYPE_INDEX).setPreferredWidth(80); // ??????
        impAndExpTaskTable.getColumnModel().getColumn(STATUS_INDEX).setPreferredWidth(100); // ??????
        impAndExpTaskTable.getColumnModel().getColumn(STATUS_DETAIL_INDEX).setPreferredWidth(100); // ?????? ??????
        impAndExpTaskTable.getColumnModel().getColumn(SIZE_INDEX).setPreferredWidth(50); // ????????????
        impAndExpTaskTable.getColumnModel().getColumn(START_TIME_INDEX).setPreferredWidth(120); // ????????????
        impAndExpTaskTable.getColumnModel().getColumn(END_TIME_INDEX).setPreferredWidth(120); // ????????????
        CommonTableUtil.hideColumn(impAndExpTaskTable, ID_INDEX);
        CommonTableUtil.hideColumn(impAndExpTaskTable, USER_ID_INDEX);
    }

    private void createOperaTableColName() {
        // ????????????
        columnNamesList = new Vector<>();
        columnNamesList.add(ImpAndExpTaskContent.ID);
        columnNamesList.add(ImpAndExpTaskContent.TASK_NAME);
        columnNamesList.add(ImpAndExpTaskContent.PROJECT_NAME);
        columnNamesList.add(ImpAndExpTaskContent.OPERATE_TYPE);
        columnNamesList.add(ImpAndExpTaskContent.PROCESS_STATUS);
        columnNamesList.add(ImpAndExpTaskContent.DETAIL_INFO);
        columnNamesList.add(ImpAndExpTaskContent.FILE_SIZE);
        columnNamesList.add(ImpAndExpTaskContent.START_TIME);
        columnNamesList.add(ImpAndExpTaskContent.END_TIME);
        columnNamesList.add("ownerId");
    }

    /**
     * ??????????????????????????????
     *
     * @param schTaskBeanList ?????????????????????
     * @return Object[][] ????????????
     */
    private void createOperaTableDate(List<ImpAndExpTaskBean> schTaskBeanList) {
        tableData = new Object[schTaskBeanList.size()][];
        for (int i = 0; i < schTaskBeanList.size(); i++) {
            ImpAndExpTaskBean bean = schTaskBeanList.get(i);
            List<Object> itemList = new ArrayList<>();
            itemList.add(bean.getId() + ""); // ??????
            itemList.add(bean.getTaskname()); // ????????????
            itemList.add(bean.getProjectname()); // ????????????
            itemList.add(bean.getOperationType()); // ????????????
            itemList.add(bean.getProcessStatus()); // ??????
            itemList.add(bean.getDetailInfo()); // ????????????
            itemList.add(bean.getTaskFilesize()); // ????????????
            itemList.add(bean.getStartTime()); // ????????????
            itemList.add(bean.getEndTime()); // ????????????
            itemList.add(bean.getOwnerId()); // ?????????????????????
            tableData[i] = itemList.toArray();
        }
    }
}

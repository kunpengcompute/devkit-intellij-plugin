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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.SysPrefLogAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysPrefLogContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SysPerfOperateLogBean;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * ?????????????????? ??????
 *
 * @since 2021-4-25
 */
public class SysPrefLogPanel extends SettingCommonConfigPanel {
    /**
     * ??????????????????
     */
    private static final String DOWNLOAD_PATH = "/assets/img/settings/download.svg";
    /**
     * ????????????
     */
    private static final int STATUS_INDEX = 2;

    private static final int RUN_LOG_WEB_INDEX = 2;
    private static final int RUN_LOG_ANALYSER_INDEX = 2;
    private static final int RUN_LOG_COLLECTOR_INDEX = 2;

    /**
     * ???????????????
     */
    private static final String STATUS_SUCCEEDED = "0";

    /**
     * ????????????????????????????????????key???
     */
    private static final String OPERA_LOG_NAME = "/log.csv";

    private static final int BUTTON_LENGTH = 2;
    private static final long serialVersionUID = -1582031749154967508L;
    private JPanel mainPanel;
    private JTable runTable;
    private JLabel operaTitle;
    private JLabel runTitle;
    private SysPrefLogAction sysPrefLogAction;
    private List<SysPerfOperateLogBean> operaLogList;
    private List<SysPerfOperateLogBean> showList;
    private JTable operaTable;
    private JPanel operationPanel;
    private JPanel runPanel;
    private DefaultTableModel tableModel;
    private Project project;
    private Icon icon;
    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarForRunTable;

    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarForOperaTable;

    /**
     * ????????????
     */
    private List<String> columnNamesList;

    /**
     * ????????????
     */
    public SysPrefLogPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        registerComponentAction(); // ??????????????????????????????
    }

    /**
     * ???????????????
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (sysPrefLogAction == null) {
            sysPrefLogAction = new SysPrefLogAction();
        }
        operaLogList = sysPrefLogAction.getSysPerfLogList();
        operaTable = new JBTable();
        runTable = new JBTable();
        createOperaTableColName();

        initOperateTable();
        if (Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole())) {
            // ????????????????????????
            initRunTable();
            toolbarForRunTable = ToolbarDecorator.createDecorator(runTable);
            addActionToToolBarRunTable(toolbarForRunTable, runTable);
            toolbarForRunTable.setPreferredSize(new Dimension(300, 200));
            runTitle.setText(I18NServer.toLocale("plugins_common_title_run_log"));
            runPanel.removeAll();
            runPanel.add(toolbarForRunTable.createPanel());
        } else {
            runTitle.setVisible(false);
        }
        toolbarForOperaTable = ToolbarDecorator.createDecorator(operaTable);
        addActionToToolBarOperaTable(toolbarForOperaTable, operaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        operationPanel.removeAll();
        operationPanel.add(toolbarForOperaTable.createPanel());
        operaTitle.setText(I18NServer.toLocale("plugins_common_title_opt_log"));
    }

    /**
     * ????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setAddActionName(SysPrefLogContent.DOWNLOG_OPER_LOG);
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(DOWNLOAD_PATH));
        toolbarDecorator.setAddAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    downloadAllLog();
                }
            });
        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(table, true));
        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(table, false));
    }

    /**
     * ????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param runTable         ??????????????????
     */
    private void addActionToToolBarRunTable(ToolbarDecorator toolbarDecorator, JTable runTable) {
        toolbarDecorator.setAddActionName(SysPrefLogContent.DOWNLOG_RUN_LOG);
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(DOWNLOAD_PATH));
        toolbarDecorator.setAddAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    showLogListDialog();
                }
            });
    }

    /**
     * ???????????????????????????
     */
    public void cancelGrayed() {
        final CommonActionsPanel actionsPanel = toolbarForRunTable.getActionsPanel();
        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, true);
    }

    /**
     * ????????????
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new SysPrefLogAction();
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
     * ?????????????????????
     */
    private void initOperateTable() {
        // ?????????????????????
        showList = new ArrayList<>();
        if (operaLogList != null) {
            showList.addAll(operaLogList);
        }

        // ????????????????????????????????????
        operaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ?????????????????????
        operaTable.getTableHeader().setReorderingAllowed(false);

        // ????????????
        Object[][] tableDate = createOperaTableDate(showList);
        operaTable.setModel(
            new DefaultTableModel(tableDate, columnNamesList.toArray()) {
                private static final long serialVersionUID = -6152279736099789261L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        operaTable.addMouseMotionListener(
            new MouseAdapter() {
                /**
                 * ???????????????????????????
                 *
                 * @param event ??????
                 */
                @Override
                public void mouseMoved(MouseEvent event) {
                    mouse(event);
                }
            });
    }

    /**
     * ?????????????????????
     */
    private void initRunTable() {
        String[] columns = {TuningLogManageConstant.LOG_FILENAME};
        String[][] rowDatas = {
            {SysPrefLogContent.WEB_SERVER_LOG},
            {SysPrefLogContent.DATA_ANALYSIS_LOG},
            {SysPrefLogContent.DATA_COLLECTION_LOG}
        };
        runTable.setModel(
            new DefaultTableModel(rowDatas, columns) {
                private static final long serialVersionUID = -8006127608455954903L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        runTable.setRowSelectionAllowed(false); // ?????????????????????
    }

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
        columnNamesList.add(TuningLogManageConstant.LOG_USERNAME);
        columnNamesList.add(TuningLogManageConstant.LOG_EVENT);
        columnNamesList.add(TuningLogManageConstant.LOG_RESULT);
        columnNamesList.add(TuningLogManageConstant.LOG_TIME);
        columnNamesList.add(TuningLogManageConstant.LOG_DETAIL);
    }

    /**
     * ??????????????????????????????
     *
     * @param logBeans ?????????????????????
     * @return Object[][] ????????????
     */
    private Object[][] createOperaTableDate(List<SysPerfOperateLogBean> logBeans) {
        Object[][] tableDate = new Object[logBeans.size()][];
        for (int i = 0; i < logBeans.size(); i++) {
            String[] item = new String[10];
            item[0] = logBeans.get(i).getUsername(); // ????????????
            item[1] = logBeans.get(i).getModuleType(); // ????????????
            item[2] = logBeans.get(i).getResult(); // ????????????
            item[3] = logBeans.get(i).getTime(); // ????????????
            item[4] = logBeans.get(i).getInformation(); // ????????????
            tableDate[i] = item;
        }
        return tableDate;
    }

    /**
     * ?????????????????????????????????
     */
    private void showLogListDialog() {
        int row = runTable.getSelectedRow();
        String type = "";
        if (row == 0) {
            type = SysPrefLogContent.LOG_TYPE_WEB_SERVER;
        } else if (row == 1) {
            type = SysPrefLogContent.LOG_TYPE_ANALYZER;
        } else if (row == 2) {
            type = SysPrefLogContent.LOG_TYPE_COLLECTOR;
        } else {
            type = SysPrefLogContent.LOG_TYPE_WEB_SERVER;
        }
        sysPrefLogAction.showLogListDialog(type);
    }

    /**
     * ??????????????????
     */
    private void downloadAllLog() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(TuningLogManageConstant.OPT_LOG_TITLE);
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile != null) {
            // ??????????????????????????????
            String path = virtualFile.getPath();
            if (ExportToFileUtil.isExistNotToContinue(path + OPERA_LOG_NAME, I18NServer.toLocale(
                "plugins_common_title_opt_log"))) {
                return;
            }
            sysPrefLogAction.export(path);
        }
    }
}

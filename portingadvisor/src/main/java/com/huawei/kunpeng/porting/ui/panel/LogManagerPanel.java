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

package com.huawei.kunpeng.porting.ui.panel;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.SETTINGS_COMPRESSING_LOGS;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.render.LogTableRenderer;
import com.huawei.kunpeng.intellij.ui.utils.IDEMessageDialogUtil;
import com.huawei.kunpeng.porting.action.setting.syssetting.SystemConfigAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.process.LogTaskProcess;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * ????????????????????????
 *
 * @since 2020-10-23
 */
public class LogManagerPanel extends IDEBasePanel {
    /**
     * ??????????????????
     */
    private static final String DOWNLOAD_PATH = "/assets/img/settings/download.svg";

    /**
     * ????????????
     */
    private static final int STATUS_INDEX = 2;

    /**
     * ???????????????
     */
    private static final String STATUS_SUCCEEDED = "0";

    /**
     * ??????????????????
     */
    private static final String TASK_NAME_PARAMS = "task_id";

    /**
     * ????????????????????????????????????key???
     */
    private static final String USER_LOCAL_DIR_PARAMS = "download_path";

    private static final String RUN_LOG_NAME = "/log.zip";

    private static final String OPERA_LOG_NAME = "/log.csv";

    private static final int BUTTON_LENGTH = 2;

    private JPanel panel1;

    private JPanel mainPanel;

    private JTable runTable;

    private JLabel operaTitle;

    private JLabel runTitle;

    private SystemConfigAction systemConfigAction;

    private List<OperateLogBean> operaLogList;

    private JTable operaTable;

    private JPanel operationPanel;

    private JPanel runPanel;

    private JLabel tipLabel;

    private DefaultTableModel tableModel;

    private Project project;


    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarForRunTable;

    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarForOperaTable;

    /**
     * ?????????????????????
     */
    private String taskName;

    /**
     * ????????????
     */
    private Vector<String> columnNamesList;

    /**
     * ????????????
     */
    public LogManagerPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // ??????????????????????????????
        registerComponentAction();

        if (Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole())) {
            // ??????????????????????????????????????????
            selectTask();
        }
    }

    /**
     * ???????????????
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (systemConfigAction == null) {
            systemConfigAction = new SystemConfigAction();
        }

        operaTable = new JBTable();
        runTable = new JBTable();
        // ?????????????????????????????????
        createOperaTableColName();

        // ?????? ????????????????????? ??????????????? ??? ??????
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initOperate();

        if (Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole())) {
            initRunTable();
            toolbarForRunTable = ToolbarDecorator.createDecorator(runTable);
            addActionToToolBarRunTable(toolbarForRunTable, runTable);
            toolbarForRunTable.setPreferredSize(new Dimension(300, 200));
            runPanel.removeAll();
            runPanel.add(toolbarForRunTable.createPanel());
            runTitle.setText(I18NServer.toLocale("plugins_common_title_run_log"));
        } else {
            runTitle.setVisible(false);
        }

        toolbarForOperaTable = ToolbarDecorator.createDecorator(operaTable);
        addActionToToolBarOperaTable(toolbarForOperaTable, operaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        operationPanel.removeAll();
        operationPanel.add(toolbarForOperaTable.createPanel());
        operaTitle.setText(I18NServer.toLocale("plugins_common_title_opt_log"));
        tipLabel.setText(I18NServer.toLocale("plugins_common_title_opt_log_tip"));
    }

    /**
     * ??????????????????????????????????????????
     */
    private void selectTask() {
        ResponseBean responseBean = systemConfigAction.selectCompressesTask();
        if (responseBean != null) {
            Map<String, String> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            if (jsonMessage != null) {
                taskName = jsonMessage.get(TASK_NAME_PARAMS);
                String localDir = jsonMessage.get(USER_LOCAL_DIR_PARAMS);
                if (ValidateUtils.isEmptyString(taskName)) {
                    Logger.info("the user has not tasks!!");
                    return;
                }
                // ??????????????????????????????
                showTaskProcess(localDir);
            }
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setAddActionName(PortingUserManageConstant.TERM_DOWNLOG);
        toolbarDecorator.setAddAction(anActionButton -> createProperty());
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load((DOWNLOAD_PATH)));
    }

    /**
     * ????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
     */
    private void addActionToToolBarRunTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setAddActionName(PortingUserManageConstant.TERM_DOWNLOG);
        toolbarDecorator.setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                descriptor.setTitle(I18NServer.toLocale("plugins_common_title_run_log"));
                final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
                if (virtualFile == null) {
                    return;
                }
                String path = virtualFile.getPath();
                // ??????????????????????????????
                if (isExistNotToContinue(path + RUN_LOG_NAME,
                    I18NServer.toLocale("plugins_common_title_run_log"))) {
                    return;
                }

                taskName = systemConfigAction.createCompressesTask(path);
                // ?????????????????????
                if (ValidateUtils.isEmptyString(taskName)) {
                    return;
                }
                // ??????????????????????????????
                showTaskProcess(path);
            }
        });
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load((DOWNLOAD_PATH)));
    }

    private boolean isExistNotToContinue(String filePath, String title) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>(BUTTON_LENGTH);
        button.add(IDEMessageDialogUtil.ButtonName.OK);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String select = IDEMessageDialogUtil.showDialog(
            new MessageDialogBean(I18NServer.toLocale("plugins_porting_download_replace_tips"),
                title,
                button,
                0,
                IDEMessageDialogUtil.getWarn()
            ));
        if (select.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            return false;
        }
        return true;
    }

    /**
     * ???????????????
     *
     * @param path path
     */
    private void showTaskProcess(String path) {
        if (ValidateUtils.isEmptyString(path)) {
            Logger.info("the task download path is null,Run log will be downloaded to plugin installed path");
        }
        // ?????????????????????
        final CommonActionsPanel actionsPanel = toolbarForRunTable.getActionsPanel();
        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, false);

        // ??????????????????
        final Task.Backgroundable taskProcess = new LogTaskProcess(systemConfigAction, taskName, path,
            SETTINGS_COMPRESSING_LOGS, this);
        final ProgressManager progress = ProgressManager.getInstance();
        progress.run(taskProcess);
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
            action = new SystemConfigAction();
        }
    }

    private void createProperty() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(I18NServer.toLocale("plugins_common_title_opt_log"));
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            // ??????????????????????????????
            if (isExistNotToContinue(path + OPERA_LOG_NAME,
                I18NServer.toLocale("plugins_common_title_opt_log"))) {
                return;
            }
            try {
                systemConfigAction.exportCsvFile(path);
            } catch (IOException e) {
                Logger.error("export file error.");
            }
        }
    }

    /**
     * ????????????
     *
     * @param action ????????????
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * apply??????????????????
     */
    public void apply() {
    }

    /**
     * ????????????apply
     *
     * @return ??????
     */
    public boolean isModified() {
        initOperate();
        return false;
    }

    /**
     * ??????????????????
     *
     * @return mainPanel
     */

    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * ????????????
     */
    public void reset() {
    }

    /**
     * ?????????????????????
     */
    private void initOperate() {
        operaLogList = systemConfigAction.getOperateLogList("false");
        // ?????????????????????
        Vector<Vector<String>> operator = new Vector<>();
        if (operaLogList != null) {
            for (OperateLogBean operateLogBean : operaLogList) {
                operator.add(operateLogBean.toVector());
            }
        }
        tableModel.setDataVector(operator, columnNamesList);
        // ??????????????????????????????
        operaTable.setRowSelectionAllowed(false);

        // ?????????????????????
        operaTable.getTableHeader().setReorderingAllowed(false);
        this.operaTable.setModel(new DefaultTableModel(operator, columnNamesList) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        operaTable.addMouseMotionListener(new MouseAdapter() {
            /**
             * ???????????????????????????
             *
             * @param event ??????
             */
            @Override
            public void mouseMoved(MouseEvent event) {
            }
        });

        // ??????????????????????????????
        operaTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new LogTableRenderer());

        // ????????????????????????
        operaTable.getTableHeader().setResizingAllowed(false);
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

    private void createOperaTableColName() {
        // ????????????
        columnNamesList = new Vector<>();
        columnNamesList.add(I18NServer.toLocale("plugins_porting_log_userName"));
        columnNamesList.add(I18NServer.toLocale("plugins_porting_log_event"));
        columnNamesList.add(I18NServer.toLocale("plugins_porting_log_result"));
        columnNamesList.add(I18NServer.toLocale("plugins_porting_log_time"));
        columnNamesList.add(I18NServer.toLocale("plugins_porting_log_Detail"));
    }

    /**
     * ?????????????????????
     */
    private void initRunTable() {
        String[] column = {I18NServer.toLocale("plugins_porting_log_filename")};
        String[][] rowData = {{"log.zip"}};
        this.runTable.setModel(new DefaultTableModel(rowData, column) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        this.runTable.setRowSelectionAllowed(false); // ?????????????????????
    }
}

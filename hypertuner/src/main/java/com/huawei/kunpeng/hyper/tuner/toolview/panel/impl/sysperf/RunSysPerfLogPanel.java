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

import com.huawei.kunpeng.hyper.tuner.action.sysperf.SysPrefLogAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.SysPerfOperateLogBean;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * ????????????????????????
 *
 * @since 2021-5-6
 */
public class RunSysPerfLogPanel extends IDEBasePanel {
    private static final long serialVersionUID = 5678649090321044594L;

    private static final String RUN_LOG_NAME = "/log.zip";

    private static final int BUTTON_LENGTH = 2;

    private String logFileName;

    private JPanel mainPanel;

    private JLabel downLoadTip;

    private JLabel iconLabel;

    private SysPrefLogAction sysPrefLogAction;

    /**
     * ????????????
     */
    private List<String> columnNamesList;

    private List<SysPerfOperateLogBean> runLogList;

    private JPanel runLogPanel;

    private DefaultTableModel tableModel;

    private JTable runLogTable;

    private Project project;

    private ToolbarDecorator toolbarForRunTable;

    // ?????? ???????????????????????? ???  Web_Server????????????   ????????????????????????
    private String type = "";

    /**
     * ??????????????????????????????????????????
     *
     * @param panelName   ????????????
     * @param displayName ????????????title
     * @param isLockable  isLockable
     */
    public RunSysPerfLogPanel(String panelName, String displayName, boolean isLockable, String type) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "RunSysPerfLogPanel" : panelName;
        this.type = type;
        initPanel(mainPanel); // ???????????????
    }

    @Override
    protected void initPanel(JPanel panel) {
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);

        // ????????????
        downLoadTip.setText(I18NServer.toLocale("plugins_hyper_tuner_runlog_dialog_tip"));
        downLoadTip.setPreferredSize(new Dimension(545, 30));
        if (sysPrefLogAction == null) {
            sysPrefLogAction = new SysPrefLogAction();
        }
        createOperaTableColName(); // ????????????
        tableModel =
                new DefaultTableModel() {
                    private static final long serialVersionUID = -2579019475997889830L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        // ????????????????????????
        runLogList = sysPrefLogAction.getRunlogList(type);
        runLogTable = new JBTable();
        // ???????????????
        initRunlogTable();
        // ????????????
        // ??????panel??????
        runLogPanel.setPreferredSize(new Dimension(545, 200));
        toolbarForRunTable = ToolbarDecorator.createDecorator(runLogTable);
        runLogPanel.add(toolbarForRunTable.createPanel());
    }

    // ????????????
    private void createOperaTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(TuningLogManageConstant.LOG_FILENAME);
        columnNamesList.add(TuningLogManageConstant.LOG_FILESIZE);
    }

    /**
     * ??????????????????????????????
     *
     * @param operateLogBeans ?????????????????????
     * @return Object[][] ????????????
     */
    private Object[][] createOperaTableDate(List<SysPerfOperateLogBean> operateLogBeans) {
        Object[][] tableDate = new Object[operateLogBeans.size()][];
        for (int i = 0; i < operateLogBeans.size(); i++) {
            String[] item = new String[10];
            item[0] = operateLogBeans.get(i).getFilename(); // ??????????????????
            item[1] = operateLogBeans.get(i).getFilesize(); // ??????????????????
            tableDate[i] = item;
        }
        return tableDate;
    }

    /**
     * ???????????????
     */
    private void initRunlogTable() {
        // ?????????????????????
        List<SysPerfOperateLogBean> showList = new ArrayList<>();
        if (runLogList != null) {
            showList.addAll(runLogList);
        }
        // ????????????????????????????????????
        runLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // ?????????????????????
        runLogTable.getTableHeader().setReorderingAllowed(false);
        Object[][] obj = createOperaTableDate(showList);
        runLogTable.setModel(
                new DefaultTableModel(obj, columnNamesList.toArray()) {
                    private static final long serialVersionUID = -6152279736099789121L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
    }

    /**
     * ????????????
     *
     * @return ????????????????????????
     */
    public Boolean onOK() {
        sysPrefLogAction = new SysPrefLogAction();

        // ??????????????????????????????
        if (runLogTable.getSelectedRow() >= 0) {
            final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            // ??????????????????
            descriptor.setTitle(I18NServer.toLocale("plugins_common_title_run_log"));
            final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (virtualFile == null) {
                return false;
            }
            String path = virtualFile.getPath();
            logFileName = runLogTable.getValueAt(runLogTable.getSelectedRow(), 0).toString();

            // ??????????????????????????????
            if (ExportToFileUtil.isExistNotToContinue(
                    path + File.separator + logFileName, I18NServer.toLocale("plugins_common_title_run_log"))) {
                return false;
            }
            sysPrefLogAction.downloadSelectLog(path, logFileName);
            return true;
        } else {
            IDENotificationUtil.notificationCommon(new NotificationBean(TuningLogManageConstant.DOWNLOAD_LOG,
                    TuningLogManageConstant.DOWNLOAD_SELECT, NotificationType.WARNING));
        }
        return false;
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    @Override
    protected void registerComponentAction() {
    }

    /**
     * ????????????????????????
     *
     * @return JTable
     */
    public JTable getRunLogTable() {
        return runLogTable;
    }
}

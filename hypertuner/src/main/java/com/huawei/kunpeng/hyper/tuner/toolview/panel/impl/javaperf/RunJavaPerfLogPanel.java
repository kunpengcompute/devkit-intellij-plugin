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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf;

import com.huawei.kunpeng.hyper.tuner.action.javaperf.JavaPrefLogAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.JavaPerfRunLogBean;
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

import java.awt.BorderLayout;
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
 * JavaPerf ??????
 * ??????????????????
 *
 * @since 2021-5-6
 */
public class RunJavaPerfLogPanel extends IDEBasePanel {
    private static final long serialVersionUID = 5678649090321044594L;

    private static final String RUN_LOG_NAME = "/log.zip";

    private static final int BUTTON_LENGTH = 2;

    private String logFileName;

    private JPanel mainPanel;

    private JLabel downLoadTip;

    private JLabel iconLabel;

    private JavaPrefLogAction javaPrefLogAction;

    /**
     * ????????????
     */
    private List<String> columnNamesList;

    private List<JavaPerfRunLogBean> runLogList;

    private JPanel runLogPanel;

    private DefaultTableModel tableModel;

    private JTable runLogTable;

    private Project project;

    private ToolbarDecorator toolbarForRunTable;

    /**
     * ??????????????????????????????????????????
     *
     * @param panelName   ????????????
     * @param displayName ????????????title
     * @param isLockable  isLockable
     */
    public RunJavaPerfLogPanel(String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "RunJavaPerfLogPanel" : panelName;

        initPanel(mainPanel); // ???????????????
    }

    @Override
    protected void initPanel(JPanel panel) {
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);

        // ????????????
        downLoadTip.setText(I18NServer.toLocale("plugins_hyper_tuner_runlog_dialog_tip"));
        downLoadTip.setPreferredSize(new Dimension(545, 30));
        if (javaPrefLogAction == null) {
            javaPrefLogAction = new JavaPrefLogAction();
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
        runLogList = javaPrefLogAction.getRunLogList();
        // ???????????????
        initRunLogTable();
        // ????????????
        // ??????panel??????
        runLogPanel.setPreferredSize(new Dimension(545, 200));
        toolbarForRunTable = ToolbarDecorator.createDecorator(runLogTable);
        runLogTable.setLayout(new BorderLayout());
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
    private Object[][] createOperaTableDate(List<JavaPerfRunLogBean> operateLogBeans) {
        Object[][] tableDate = new Object[operateLogBeans.size()][];
        for (int i = 0; i < operateLogBeans.size(); i++) {
            String[] item = new String[2];
            item[0] = operateLogBeans.get(i).getFileName(); // ??????????????????
            item[1] = operateLogBeans.get(i).getFileSize(); // ??????????????????
            tableDate[i] = item;
        }
        return tableDate;
    }

    /**
     * ???????????????
     */
    private void initRunLogTable() {
        // ?????????????????????
        List<JavaPerfRunLogBean> showList = new ArrayList<>();
        if (runLogList != null) {
            showList.addAll(runLogList);
        }
        // ????????????????????????????????????
        runLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // ?????????????????????
        runLogTable.getTableHeader().setReorderingAllowed(false);
        Object[][] aa = createOperaTableDate(showList);
        runLogTable.setModel(
                new DefaultTableModel(aa, columnNamesList.toArray()) {
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
        javaPrefLogAction = new JavaPrefLogAction();
        // ??????????????????????????????
        if (runLogTable.getSelectedRow() >= 0) {
            final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            // ??????????????????
            descriptor.setTitle(I18NServer.toLocale("plugins_common_title_run_log"));
            final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (virtualFile == null) {
                return false;
            }
            logFileName = runLogTable.getValueAt(runLogTable.getSelectedRow(), 0).toString();
            String path = virtualFile.getPath();
            // ??????????????????????????????
            if (ExportToFileUtil.isExistNotToContinue(
                    path + File.separator + logFileName, I18NServer.toLocale("plugins_common_title_run_log"))) {
                return false;
            }
            javaPrefLogAction.downloadRunLog(path, logFileName);
            return true;
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            TuningLogManageConstant.DOWNLOAD_LOG,
                            TuningLogManageConstant.DOWNLOAD_SELECT,
                            NotificationType.WARNING));
        }
        return false;
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
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

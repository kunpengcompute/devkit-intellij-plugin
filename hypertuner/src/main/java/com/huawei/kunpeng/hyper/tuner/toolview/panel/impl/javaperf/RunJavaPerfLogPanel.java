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
 * JavaPerf 模块
 * 运行日志下载
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
     * 创建表头
     */
    private List<String> columnNamesList;

    private List<JavaPerfRunLogBean> runLogList;

    private JPanel runLogPanel;

    private DefaultTableModel tableModel;

    private JTable runLogTable;

    private Project project;

    private ToolbarDecorator toolbarForRunTable;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param panelName   面板名称
     * @param displayName 面板显示title
     * @param isLockable  isLockable
     */
    public RunJavaPerfLogPanel(String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "RunJavaPerfLogPanel" : panelName;

        initPanel(mainPanel); // 初始化面板
    }

    @Override
    protected void initPanel(JPanel panel) {
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);

        // 设置提示
        downLoadTip.setText(I18NServer.toLocale("plugins_hyper_tuner_runlog_dialog_tip"));
        downLoadTip.setPreferredSize(new Dimension(545, 30));
        if (javaPrefLogAction == null) {
            javaPrefLogAction = new JavaPrefLogAction();
        }
        createOperaTableColName(); // 生成表头
        tableModel =
                new DefaultTableModel() {
                    private static final long serialVersionUID = -2579019475997889830L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        // 查询下载日志列表
        runLogList = javaPrefLogAction.getRunLogList();
        // 初始化表格
        initRunLogTable();
        // 设置表头
        // 设置panel大小
        runLogPanel.setPreferredSize(new Dimension(545, 200));
        toolbarForRunTable = ToolbarDecorator.createDecorator(runLogTable);
        runLogTable.setLayout(new BorderLayout());
        runLogPanel.add(toolbarForRunTable.createPanel());
    }

    // 生成表头
    private void createOperaTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(TuningLogManageConstant.LOG_FILENAME);
        columnNamesList.add(TuningLogManageConstant.LOG_FILESIZE);
    }

    /**
     * 创建表格数据二维数组
     *
     * @param operateLogBeans 表格所有行数据
     * @return Object[][] 二位数组
     */
    private Object[][] createOperaTableDate(List<JavaPerfRunLogBean> operateLogBeans) {
        Object[][] tableDate = new Object[operateLogBeans.size()][];
        for (int i = 0; i < operateLogBeans.size(); i++) {
            String[] item = new String[2];
            item[0] = operateLogBeans.get(i).getFileName(); // 日志文件名称
            item[1] = operateLogBeans.get(i).getFileSize(); // 日志文件大小
            tableDate[i] = item;
        }
        return tableDate;
    }

    /**
     * 初始化表格
     */
    private void initRunLogTable() {
        // 表格所有行数据
        List<JavaPerfRunLogBean> showList = new ArrayList<>();
        if (runLogList != null) {
            showList.addAll(runLogList);
        }
        // 这只表格只可以监听一行。
        runLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 不可以重排列。
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
     * 确认操作
     *
     * @return 是否选择下载文件
     */
    public Boolean onOK() {
        javaPrefLogAction = new JavaPrefLogAction();
        // 判断是否选择下载文件
        if (runLogTable.getSelectedRow() >= 0) {
            final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            // 运行日志表头
            descriptor.setTitle(I18NServer.toLocale("plugins_common_title_run_log"));
            final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (virtualFile == null) {
                return false;
            }
            logFileName = runLogTable.getValueAt(runLogTable.getSelectedRow(), 0).toString();
            String path = virtualFile.getPath();
            // 判断下载文件是否存在
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
     * 获取运行日志表格
     *
     * @return JTable
     */
    public JTable getRunLogTable() {
        return runLogTable;
    }
}

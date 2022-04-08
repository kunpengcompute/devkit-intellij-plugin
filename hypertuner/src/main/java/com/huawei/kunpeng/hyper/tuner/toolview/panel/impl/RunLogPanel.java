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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.hyper.tuner.action.panel.LogAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * 用户运行日志下载
 *
 * @since 2020-10-23
 */
public class RunLogPanel extends IDEBasePanel {
    private static final long serialVersionUID = 5678649090321044594L;

    private static final int BUTTON_LENGTH = 2;

    private String logFileName;

    private JPanel mainPanel;

    private JLabel downLoadTip;

    private JLabel iconLabel;

    private LogAction logAction;

    /**
     * 创建表头
     */
    private Vector columnNamesList;

    private List<OperateLogBean> runLogList;

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
    public RunLogPanel(String panelName, String displayName, boolean isLockable) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? "RunLog" : panelName;
        // 初始化面板
        initPanel(mainPanel);
    }

    @Override
    protected void initPanel(JPanel panel) {
        project = CommonUtil.getDefaultProject();
        super.initPanel(mainPanel);
        // 设置提示
        downLoadTip.setText(TuningLogManageConstant.RUNLOG_DOWNLOAD_TIP);
        downLoadTip.setPreferredSize(new Dimension(545, 30));
        if (logAction == null) {
            logAction = new LogAction();
        }
        // 生成表头列
        createOperaTableColName();
        // 创建 表格模型，指定 所有行数据 和 表头
        tableModel =
                new DefaultTableModel() {
                    private static final long serialVersionUID = -2579019475997889830L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        // 查询下载日志列表
        runLogList = logAction.getRunlogList();
        runLogTable = new JBTable();
        // 初始化表格
        initRunlogTable();
        // 设置表头
        // 设置panel大小
        runLogPanel.setPreferredSize(new Dimension(545, 200));
        toolbarForRunTable = ToolbarDecorator.createDecorator(runLogTable);
        runLogPanel.add(toolbarForRunTable.createPanel());
    }

    // 生成表头
    private void createOperaTableColName() {
        columnNamesList = new Vector();
        columnNamesList.add(TuningLogManageConstant.LOG_FILENAME);
        columnNamesList.add(TuningLogManageConstant.LOG_FILESIZE);
    }

    /**
     * 初始化操作日志
     */
    private void initRunlogTable() {
        // 表格所有行数据
        Vector operator = new Vector();
        if (runLogList != null) {
            for (int i = 0; i < runLogList.size(); i++) {
                Vector row = new Vector();
                row.add(runLogList.get(0).getFilename());
                row.add(runLogList.get(0).getFilesize());
                operator.add(row);
            }
        }
        tableModel.setDataVector(operator, columnNamesList);
        // 这只表格只可以监听一行。
        runLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 不可以重排列。
        runLogTable.getTableHeader().setReorderingAllowed(false);
        runLogTable.setModel(
                new DefaultTableModel(operator, columnNamesList) {
                    private static final long serialVersionUID = -6152279736099789121L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
    }

    /**
     * qingqiuhout
     *
     * @return qingqiujieguo
     */
    public Boolean onOK() {
        logAction = new LogAction();
        // 判断是否选择下载文件
        if (runLogTable.getSelectedRow() >= 0) {
            final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            // 运行日志表头
            descriptor.setTitle(TuningLogManageConstant.RUN_LOG_TITLE);
            final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (virtualFile == null) {
                return false;
            }
            String path = virtualFile.getPath();
            logFileName = runLogTable.getValueAt(runLogTable.getSelectedRow(), 0).toString();
            // 判断下载文件是否存在
            if (CommonTableUtil.isExistNotToContinue(path + "/" + logFileName, TuningLogManageConstant.RUN_LOG_TITLE)) {
                return false;
            }
            logAction.downloadSelectLog(path, logFileName);
            return true;
        } else {
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            TuningLogManageConstant.DOWNLOAD_LOG,
                            TuningLogManageConstant.DOWNLOAD_SELECT,
                            NotificationType.INFORMATION));
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

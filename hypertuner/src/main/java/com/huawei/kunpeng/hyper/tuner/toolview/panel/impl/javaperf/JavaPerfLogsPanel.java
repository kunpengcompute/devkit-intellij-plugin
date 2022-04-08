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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.hyper.tuner.action.javaperf.JavaPrefLogAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaPerfLogsConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysPrefLogContent;
import com.huawei.kunpeng.hyper.tuner.common.utils.ExportToFileUtil;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.JavaPerfOperateLogBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.javaperf.JavaPerfTableRenderer;
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
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Java 性能测试 模块
 * 日志 管理面板
 *
 * @since 2021-07-07
 */
public class JavaPerfLogsPanel extends SettingCommonConfigPanel {
    /**
     * 下载按钮图标
     */
    private static final String DOWNLOAD_PATH = "/assets/img/settings/download.svg";

    private static final int BUTTON_LENGTH = 2;
    private static final String OPERA_LOG_NAME = "log.csv";
    private static final int OPERATE_TABLE_INDEX_STATUS = 2; // 操作日志表格 状态栏
    private JPanel mainPanel;
    private Project project;
    private JavaPrefLogAction javaPrefLogAction;
    /**
     * 操作日志面板
     */
    private JPanel operateLogPanel;

    private JLabel operateLogLabel;
    private JTable operateLogTable;
    private List<JavaPerfOperateLogBean> operateLogBeans;
    private List<String> columnNamesList;
    private ToolbarDecorator toolbarForOperate; // 操作工具栏
    private DefaultTableModel tableModel;
    /**
     * 运行日志面板
     */
    private JPanel runLogPanel;

    private JLabel runLogLabel;
    private JTable runLogTable;
    private JPanel operateLogTablePanel;
    private JPanel runLogTablePanel;
    private ToolbarDecorator toolbarForRun; // 操作工具栏

    /**
     * 构造函数
     */
    public JavaPerfLogsPanel() {
        initPanel();
        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        project = CommonUtil.getDefaultProject();
        javaPrefLogAction = new JavaPrefLogAction();
        operateLogBeans = javaPrefLogAction.getOperateLogList();
        operateLogTable = new JBTable(tableModel);
        initOperateTable();
        toolbarForOperate = ToolbarDecorator.createDecorator(operateLogTable);
        addActionToToolBarOperaTable(toolbarForOperate, operateLogTable);
        toolbarForOperate.setPreferredSize(new Dimension(300, 300));
        operateLogTablePanel.removeAll();
        operateLogTablePanel.setLayout(new BorderLayout());
        operateLogTablePanel.add(toolbarForOperate.createPanel());
        operateLogLabel.setText(I18NServer.toLocale("plugins_common_title_opt_log"));

        if (Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole())) {
            // 当前用户为管理员
            initRunTable();
            toolbarForRun = ToolbarDecorator.createDecorator(runLogTable);
            addActionToToolBarRunTable(toolbarForRun, runLogTable);
            toolbarForRun.setPreferredSize(new Dimension(300, 200));
            runLogTablePanel.removeAll();
            runLogTablePanel.setLayout(new BorderLayout());
            runLogTablePanel.add(toolbarForRun.createPanel());
            runLogLabel.setText(I18NServer.toLocale("plugins_common_title_run_log"));
        } else {
            runLogPanel.setVisible(false);
        }
    }

    /**
     * 初始化操作日志
     */
    private void initOperateTable() {
        createOperaTableColName(); // 创建表头
        // 这只表格只可以监听一行。
        operateLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 不可以重排列。
        operateLogTable.getTableHeader().setReorderingAllowed(false);

        // 生成数据
        Object[][] tableDate = createOperaTableDate(operateLogBeans);
        operateLogTable.setModel(
            new DefaultTableModel(tableDate, columnNamesList.toArray()) {
                private static final long serialVersionUID = -6152279736099789261L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

        // 设置表头内容居中
        TableCellRenderer tableCellRenderer = operateLogTable.getTableHeader().getDefaultRenderer();
        if (tableCellRenderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) tableCellRenderer).setHorizontalAlignment(SwingConstants.LEFT);
        }

        // 国际化状态列
        operateLogTable
            .getColumnModel()
            .getColumn(OPERATE_TABLE_INDEX_STATUS)
            .setCellRenderer(new JavaPerfTableRenderer("operateLogTable"));
    }

    /**
     * 初始化运行日志
     */
    private void initRunTable() {
        String[] column = {" " + TuningLogManageConstant.LOG_FILENAME};
        String[][] rowData = {{JavaPerfLogsConstant.JAVA_PERF_RUN_LOG}};
        runLogTable.setModel(
            new DefaultTableModel(rowData, column) {
                private static final long serialVersionUID = -8006127608455954903L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        runLogTable.setRowSelectionAllowed(false); // 行不能被选中。

        TableCellRenderer tableCellRenderer = runLogTable.getTableHeader().getDefaultRenderer();
        if (tableCellRenderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) tableCellRenderer).setHorizontalAlignment(SwingConstants.LEFT);
        }
    }

    /**
     * 给操作日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param table            操作日志表格
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setAddActionName(SysPrefLogContent.DOWNLOG_OPER_LOG);
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(DOWNLOAD_PATH));
        toolbarDecorator.setAddAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    downloadOperateLog();
                }
            });
        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(table, true));
        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(table, false));
    }

    /**
     * 给运行日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param runTable         操作日志表格
     */
    private void addActionToToolBarRunTable(ToolbarDecorator toolbarDecorator, JTable runTable) {
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(DOWNLOAD_PATH));
        toolbarDecorator.setAddActionName(SysPrefLogContent.DOWNLOG_RUN_LOG);
        toolbarDecorator.setAddAction(
            new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {
                    downloadRunLog();
                }
            });
    }

    /**
     * 创建表头
     */
    private void createOperaTableColName() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(" " + TuningLogManageConstant.LOG_USERNAME); // 操作用户
        columnNamesList.add(" " + TuningLogManageConstant.LOG_EVENT); // 操作名称
        columnNamesList.add(" " + TuningLogManageConstant.LOG_RESULT); // 操作结果
        columnNamesList.add(" " + TuningLogManageConstant.LOG_IP); // 操作主机IP
        columnNamesList.add(" " + TuningLogManageConstant.LOG_TIME); // 操作时间
        columnNamesList.add(" " + TuningLogManageConstant.LOG_DETAIL); // 操作详情
    }

    /**
     * 创建表格数据二维数组
     *
     * @param logBeans 表格所有行数据
     * @return Object[][] 二位数组
     */
    private Object[][] createOperaTableDate(List<JavaPerfOperateLogBean> logBeans) {
        Object[][] tableDate = new Object[logBeans.size()][];
        for (int i = 0; i < logBeans.size(); i++) {
            String[] item = new String[6];
            item[0] = logBeans.get(i).getUsername(); // 操作用户
            item[1] = logBeans.get(i).getOperation(); // 操作名称
            item[2] = logBeans.get(i).getSucceed(); // 操作结果
            item[3] = logBeans.get(i).getClientIp(); // 操作主机IP
            String createTime = logBeans.get(i).getCreateTime();
            item[4] = JavaPrefLogAction.getTimeFromTimeStampStr(createTime); // 操作时间
            item[5] = logBeans.get(i).getResource(); // 操作详情
            tableDate[i] = item;
        }
        return tableDate;
    }

    /**
     * 下载操作日志
     */
    private void downloadOperateLog() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(TuningLogManageConstant.OPT_LOG_TITLE);
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile != null) {
            // 文件存在且选择不继续
            String path = virtualFile.getPath();
            if (ExportToFileUtil.isExistNotToContinue(
                path + File.separator + OPERA_LOG_NAME, I18NServer.toLocale("plugins_common_title_opt_log"))) {
                return;
            }
            javaPrefLogAction.export(path);
        }
    }

    /**
     * 下载运行日志
     */
    private void downloadRunLog() {
        // 弹窗展示带下载日志列表
        javaPrefLogAction.showLogListDialog();
    }

    /**
     * 获取当前面板
     *
     * @return JPanel
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}

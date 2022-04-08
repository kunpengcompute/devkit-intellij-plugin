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

package com.huawei.kunpeng.intellij.ui.panel;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.constant.LogManageConstant;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

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
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * 操作日志实现面板
 *
 * @since 2020-10-23
 */
public abstract class LogManagerPanel extends IDEBasePanel {
    /**
     * 下载按钮图标
     */
    protected static final String DOWNLOAD_PATH = "/assets/img/settings/download.svg";
    /**
     * 状态索引
     */
    protected static final int STATUS_INDEX = 2;
    /**
     * 状态索引
     */
    protected static final String OPERA_LOG_NAME = "/log.csv";
    /**
     * serialVersionUID
     */
    protected static final long serialVersionUID = -1582031749154967508L;
    /**
     * 用户本地日志下载路径参数key。
     */
    protected String USER_ROLE_ADMIN = UserManageConstant.USER_ROLE_ADMIN;
    /**
     * panel1
     */
    protected JPanel panel1;
    /**
     * logManagerMainPanel
     */
    protected JPanel logManagerMainPanel;
    /**
     * runTable
     */
    protected JTable runTable;
    /**
     * runTable
     */
    protected JLabel operaTitle;
    /**
     * runTitle
     */
    protected JLabel runTitle;
    /**
     * operaLogList
     */
    protected List<OperateLogBean> operaLogList;
    /**
     * operaTable
     */
    protected JTable operaTable;
    /**
     * operationPanel
     */
    protected JPanel operationPanel;
    /**
     * runTable
     */
    protected JPanel runPanel;
    /**
     * tableModel
     */
    protected DefaultTableModel tableModel;
    /**
     * project
     */
    protected Project project;
    /**
     * icon
     */
    protected Icon icon;
    /**
     * 表格工具栏
     */
    protected ToolbarDecorator toolbarForRunTable;
    /**
     * 表格工具栏
     */
    protected ToolbarDecorator toolbarForOperaTable;
    /**
     * 创建表头
     */
    protected Vector columnNamesList;

    /**
     * 构造函数
     */
    public LogManagerPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        super.initPanel(logManagerMainPanel);
        operaLogList = getOperalogList();
        operaTable = new JBTable();
        runTable = new JBTable();
        // 添加滚动面板到内容面板
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
        initOperat();

        if (Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole())) {
            initRunTable();
            toolbarForRunTable = ToolbarDecorator.createDecorator(runTable);
            addActionToToolBarRunTable(toolbarForRunTable, runTable);

            toolbarForRunTable.setPreferredSize(new Dimension(300, 200));
            runPanel.removeAll();
            runPanel.add(toolbarForRunTable.createPanel());
            runTitle.setText(LogManageConstant.TITLE_RUN_LOG);
        } else {
            runTitle.setVisible(false);
        }

        toolbarForOperaTable = ToolbarDecorator.createDecorator(operaTable);
        addActionToToolBarOperaTable(toolbarForOperaTable, operaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        operationPanel.removeAll();
        operationPanel.add(toolbarForOperaTable.createPanel());
        operaTitle.setText(LogManageConstant.OPT_LOG_TITLE);
    }

    protected List<OperateLogBean> getOperalogList() {
        return operaLogList;
    }

    /**
     * 给操作日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param table            操作日志表格
     */
    protected void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        setAddActionName(toolbarDecorator);
        toolbarDecorator.setAddAction(
                new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        createProperty();
                    }
                });
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load((DOWNLOAD_PATH)));
        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(table, true));
        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(table, false));
    }

    /**
     * setAddActionName
     *
     * @param toolbarDecorator toolbarDecorator
     */
    protected void setAddActionName(ToolbarDecorator toolbarDecorator) {
    }

    /**
     * 给运行日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param runTable         操作日志表格
     */
    protected void addActionToToolBarRunTable(ToolbarDecorator toolbarDecorator, JTable runTable) {
    }

    /**
     * 取消操作按钮置灰。
     */
    public void cancelGrayed() {
        final CommonActionsPanel actionsPanel = toolbarForRunTable.getActionsPanel();
        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, true);
    }

    /**
     * createProperty。
     */
    protected void createProperty() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(LogManageConstant.OPT_LOG_TITLE);
        final VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            // 文件存在且选择不继续
            if (CommonTableUtil.isExistNotToContinue(path + OPERA_LOG_NAME, LogManageConstant.OPT_LOG_TITLE)) {
                return;
            }
            export(path);
        }
    }

    /**
     * export。
     *
     * @param path path
     */
    protected void export(String path) {
    }

    /**
     * 处理事件
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * apply按钮添加事件
     */
    public void apply() {
    }

    /**
     * 是否添加apply
     *
     * @return 结果
     */
    public boolean isModified() {
        return false;
    }

    /**
     * 获取对应面板
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return logManagerMainPanel;
    }

    /**
     * 重置界面
     */
    public void reset() {
    }

    /**
     * 初始化操作日志
     */
    protected void initOperat() {
    }

    /**
     * 获取对应面板
     *
     * @param event event
     */
    protected void mouse(MouseEvent event) {
        int row = operaTable.rowAtPoint(event.getPoint());
        int col = operaTable.columnAtPoint(event.getPoint());
        if (row > -1 && col > -1) {
            Object value = operaTable.getValueAt(row, col);
            if (value != null && !"".equals(value)) {
                operaTable.setToolTipText(value.toString()); // 悬浮显示单元格内容
            }
        }
    }

    /**
     * 获取对应面板
     *
     */
    protected void createOperaTableColName() {
    }

    /**
     * 初始化运行日志
     */
    protected void initRunTable() {
    }
}

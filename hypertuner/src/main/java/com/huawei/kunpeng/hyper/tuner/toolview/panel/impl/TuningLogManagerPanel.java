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

import com.huawei.kunpeng.hyper.tuner.action.IDERunLogAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.LogAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.panel.LogManagerPanel;
import com.huawei.kunpeng.intellij.ui.render.LogTableRenderer;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ToolbarDecorator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * 操作日志实现面板
 *
 * @since 2020-10-23
 */
public class TuningLogManagerPanel extends LogManagerPanel {
    private static final long serialVersionUID = -1582031749154967508L;
    private LogAction logAction;

    /**
     * 构造函数
     */
    public TuningLogManagerPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // 初始化面板内组件事件
        registerComponentAction();
    }

    @Override
    protected List<OperateLogBean> getOperalogList() {
        if (logAction == null) {
            logAction = new LogAction();
        }
        return logAction.getOperalogList();
    }

    @Override
    protected void setAddActionName(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.setAddActionName(TuningLogManageConstant.DOWNLOG_OPER_LOG);
    }

    /**
     * 给运行日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param runTable         操作日志表格
     */
    @Override
    protected void addActionToToolBarRunTable(ToolbarDecorator toolbarDecorator, JTable runTable) {
        toolbarDecorator.setAddActionName(TuningLogManageConstant.DOWNLOG_RUN_LOG);
        toolbarDecorator.setAddAction(new IDERunLogAction(runTable));
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load((DOWNLOAD_PATH)));
    }

    /**
     * 监听事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new LogAction();
        }
    }

    @Override
    protected void export(String path) {
        logAction.export(path);
    }

    /**
     * 初始化操作日志
     */
    @Override
    protected void initOperat() {
        // 表格所有行数据
        Vector operator = new Vector();
        if (operaLogList != null) {
            for (int i = 0; i < operaLogList.size(); i++) {
                Vector vector = new Vector();
                vector.add(operaLogList.get(i).getUsername());
                vector.add(operaLogList.get(i).getModuleType());
                vector.add(operaLogList.get(i).getResult());
                vector.add(operaLogList.get(i).getIpaddr());
                vector.add(operaLogList.get(i).getTime());
                vector.add(operaLogList.get(i).getInformation());
                operator.add(vector);
            }
        }
        tableModel.setDataVector(operator, columnNamesList);
        // 这只表格只可以监听一行。
        operaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 不可以重排列。
        operaTable.getTableHeader().setReorderingAllowed(false);
        operaTable.setModel(
                new DefaultTableModel(operator, columnNamesList) {
                    private static final long serialVersionUID = -6152279736099789261L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
        operaTable.addMouseMotionListener(
                new MouseAdapter() {
                    /**
                     * 悬浮提示单元格的值
                     *
                     * @param mouseEvent 事件
                     */
                    @Override
                    public void mouseMoved(MouseEvent mouseEvent) {
                        mouse(mouseEvent);
                    }
                });

        // 给状态列设置渲染器。
        operaTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new LogTableRenderer());
    }

    @Override
    protected void createOperaTableColName() {
        // 创建表头
        columnNamesList = new Vector();
        columnNamesList.add(TuningLogManageConstant.LOG_USERNAME);
        columnNamesList.add(TuningLogManageConstant.LOG_EVENT);
        columnNamesList.add(TuningLogManageConstant.LOG_RESULT);
        columnNamesList.add(TuningLogManageConstant.LOG_IP);
        columnNamesList.add(TuningLogManageConstant.LOG_TIME);
        columnNamesList.add(TuningLogManageConstant.LOG_DETAIL);
    }

    /**
     * 初始化运行日志
     */
    @Override
    protected void initRunTable() {
        String[] column = {I18NServer.toLocale("plugins_hyper_tuner_log_filename_title")};
        String[][] rowData = {{I18NServer.toLocale("plugins_hyper_tuner_log_filename")}};
        runTable.setModel(
                new DefaultTableModel(rowData, column) {
                    private static final long serialVersionUID = -8006127608455954903L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
        runTable.setRowSelectionAllowed(false); // 行不能被选中。
    }
}

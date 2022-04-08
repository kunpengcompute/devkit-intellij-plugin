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

import com.huawei.kunpeng.hyper.tuner.action.javaperf.InternalCommCertAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.InternalCommCertConstant;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.InternalCommCertBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.javaperf.JavaPerfTableRenderer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;

import com.intellij.ui.ToolbarDecorator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * 内部通信证书 管理面板
 *
 * @since 2021-07-07
 */
public class InternalCommCertPanel extends SettingCommonConfigPanel {
    /**
     * 证书名称 索引
     */
    private static final int CERT_NAME_INDEX = 0;
    /**
     * 证书状态 索引
     */
    private static final int CERT_STATUS_INDEX = 1;

    private InternalCommCertAction action;
    private List<InternalCommCertBean> internalCommCertBeans;
    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarDecorator;
    /**
     * 创建表头
     */
    private List<String> columnNamesList;
    /**
     * 表格数据
     */
    private Object[][] tableData;

    private JPanel mainPanel;
    private JTable table;
    private JPanel tablePanel;

    /**
     * 构造函数
     */
    public InternalCommCertPanel() {
        setToolWindow(toolWindow);
        this.panelName = ValidateUtils.isEmptyString(panelName) ? InternalCommCertConstant.DISPLAY_NAME : panelName;
        // 初始化面板内组件事件
        registerComponentAction();
        initPanel();
        // 初始化content实例
        createContent(mainPanel, null, false);
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (action == null) {
            action = new InternalCommCertAction();
        }

        // 初始化表格
        initAgentTable();
        tablePanel.setLayout(new BorderLayout());

        // 添加操作按钮
        toolbarDecorator = ToolbarDecorator.createDecorator(table);

        toolbarDecorator.setPreferredSize(new Dimension(300, 300));
        tablePanel.removeAll();
        tablePanel.add(toolbarDecorator.createPanel());
    }

    /**
     * 初始化 表格
     */
    private void initAgentTable() {
        internalCommCertBeans = action.getInternalCommCertList();

        // 创建表头
        createTableColList();

        // 生成表格数据
        createTableData(internalCommCertBeans);

        // 创建 表格模型，指定 所有行数据 和 表头
        DefaultTableModel tableModel =
            new DefaultTableModel(this.tableData, columnNamesList.toArray()) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        // 设置表格模型
        this.table.setModel(tableModel);

        // 不可以重排列。
        this.table.getTableHeader().setReorderingAllowed(false);

        // 设置表头内容居左
        TableCellRenderer defaultRenderer = this.table.getTableHeader().getDefaultRenderer();
        if (defaultRenderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) defaultRenderer).setHorizontalAlignment(SwingConstants.LEFT);
        }
        table.getColumnModel().getColumn(CERT_NAME_INDEX).setPreferredWidth(200);
        table.getColumnModel()
            .getColumn(CERT_STATUS_INDEX)
            .setCellRenderer(new JavaPerfTableRenderer("internalCommCertTable"));
    }

    /**
     * 获取对应面板
     *
     * @return mainPanel
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 创建表头
     */
    private void createTableColList() {
        columnNamesList = new ArrayList<>();
        columnNamesList.add(" " + InternalCommCertConstant.TABLE_COL_NAME_NAME); // 证书名称
        columnNamesList.add(" " + InternalCommCertConstant.TABLE_COL_NAME_STATUS); // 状态
        columnNamesList.add(" " + InternalCommCertConstant.TABLE_COL_NAME_EXPIRE_TIME); // 证书到期时间
        columnNamesList.add(" " + InternalCommCertConstant.TABLE_COL_NAME_TYPE); // 证书类型
    }

    /**
     * 创建表格数据二维数组
     *
     * @param schTaskBeanList 表格所有行数据
     */
    private void createTableData(List<InternalCommCertBean> schTaskBeanList) {
        tableData = new Object[schTaskBeanList.size()][];
        for (int i = 0; i < schTaskBeanList.size(); i++) {
            List<Object> itemList = new ArrayList<>();
            itemList.add(" " + schTaskBeanList.get(i).getCertificateName()); // 证书名称
            itemList.add(schTaskBeanList.get(i).getVerify()); // 状态
            String format = DateFormatUtils.format(schTaskBeanList.get(i).getNotAfter(), "yyyy/MM/dd HH:mm:ss");
            itemList.add(" " + format); // 证书到期时间
            itemList.add(" " + schTaskBeanList.get(i).getCertificateType()); // 证书类型
            tableData[i] = itemList.toArray();
        }
    }
}

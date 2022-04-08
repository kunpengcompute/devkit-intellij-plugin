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

import static com.huawei.kunpeng.intellij.common.constant.UserManageConstant.USER_ROLE_ADMIN;

import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.bean.CertRevListInfoBean;
import com.huawei.kunpeng.intellij.common.constant.CertificateConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.crl.DeleteCRLAction;
import com.huawei.kunpeng.porting.action.setting.crl.ImportCRLAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.ui.dialog.crl.CRLDetailDialog;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.CRLDetailPanel;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.CertificationRevocationListAction;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * The class: CertRovListPanel 证书吊销列表主面板
 *
 * @since 2021-8-10
 */
public class CertRovListPanel extends IDEBasePanel {
    /**
     * 证书吊销列表操作类
     */
    protected IDEPanelBaseAction action;
    private final int DETAIL_INDEX = 5;
    private JPanel crlPanel;
    private JPanel mainPanel;

    /**
     * 证书吊销列表数据结构
     */
    private JTable crlTable;
    private JLabel crlDesLabel;

    /**
     * 表头
     */
    private Vector columnNamesList = new Vector() {
        {
            add(CertificateConstant.CERT_NAME);
            add(CertificateConstant.ISSUER);
            add(CertificateConstant.EFFECTIVE_DATE);
            add(CertificateConstant.NEXT_UPDATE_TIME);
            add(CertificateConstant.CERT_STATUS);
            add(CertificateConstant.OPERATE);
        }
    };

    /**
     * 用户模板
     */
    private DefaultTableModel tableModel;

    /**
     * 证书吊销列表
     */
    private List<CertRevListInfoBean> certRevList;

    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarDecorator;


    /**
     * 证书吊销列表面板构造函数
     */
    public CertRovListPanel() {
        super.initPanel(mainPanel);
        // 设置证书吊销列表Action
        action = CertificationRevocationListAction.getInstance();
        // 展示证书吊销列表
        crlDesLabel.setText(I18NServer.toLocale("plugins_porting_setting_crl_des"));
        paintCrlTable();
    }

    /**
     * 证书吊销列表表格
     */
    private void paintCrlTable() {
        // 设置表格模式
        setTableMode();
        // 初始化表格
        crlTable = new JBTable(tableModel);
        fillCrlIntoTable();
        crlDetailColumnAddMouseListener();
        registerToolBarDecorator();
    }

    /**
     * 通过查询后台结果填充到表格中
     */
    private void fillCrlIntoTable() {
        // 查询证书吊销列表
        showCertRevList();
        Vector crl = certRevList.stream().map(element -> element.toVector())
            .collect(Collectors.toCollection(Vector::new));
        tableModel.setDataVector(crl, columnNamesList);
        crlTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 设置查看详情的颜色
        setTableColumnColor();
    }

    private void setTableColumnColor() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                super.setValue("<html><u>" + value.toString() + "</u></html>");
                setForeground(new Color(68, 127, 245));
            }
        };
        crlTable.getColumnModel().getColumn(DETAIL_INDEX).setCellRenderer(renderer);
    }

    /**
     * CRL查看详情添加监听事件
     */
    private void crlDetailColumnAddMouseListener() {
        crlTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                Object source = event.getSource();
                if (source instanceof JBTable) {
                    JBTable table = (JBTable) source;
                    showDetails(table, event);
                }
            }
        });
    }

    private void showDetails(JBTable table, MouseEvent event) {
        int row = table.rowAtPoint(event.getPoint());
        int col = table.columnAtPoint(event.getPoint());
        if (col != DETAIL_INDEX) {
            return;
        }
        String certName = crlTable.getModel().getValueAt(row, 0).toString();
        CRLDetailDialog crlDetailDialog = new CRLDetailDialog(new CRLDetailPanel(certName));
        crlDetailDialog.displayPanel();
    }

    /**
     * 增加表格操作（导入CRL/删除CRL）
     */
    private void registerToolBarDecorator() {
        toolbarDecorator = ToolbarDecorator.createDecorator(crlTable);
        if (Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole())) {
            addActionToToolBar(toolbarDecorator);
        }
        toolbarDecorator.setPreferredSize(new Dimension(300, 300));
        crlPanel.removeAll();
        crlPanel.add(toolbarDecorator.createPanel());
    }

    /**
     * 在CRL表格添加操作按钮（Import/Delete CRL）
     *
     * @param toolbarDecorator toolbarDecorator
     */
    private void addActionToToolBar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.setAddActionName(CertificateConstant.TITLE_IMPORT_CRL);
        toolbarDecorator.setAddAction(new ImportCRLAction(this.crlTable, this));
        toolbarDecorator.setRemoveActionName(CertificateConstant.TITLE_DELETE_CRL);
        toolbarDecorator.setRemoveAction(new DeleteCRLAction(this.crlTable, this));
        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(this.crlTable, true));
        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(this.crlTable, false));
    }

    /**
     * 查询CRL后端列表
     */
    private void showCertRevList() {
        if (action instanceof CertificationRevocationListAction) {
            certRevList = ((CertificationRevocationListAction) action).showCertRevList();
        }
    }

    /**
     * 创建表格模型，指定所有行数据和表头
     */
    private void setTableMode() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 不可编辑
                return false;
            }
        };
    }

    /**
     * 获取对应面板
     *
     * @return mainPanel
     */

    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 更新证书吊销列表显示
     */
    public void updateTable() {
        fillCrlIntoTable();
        crlTable.updateUI();
        crlPanel.updateUI();
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}

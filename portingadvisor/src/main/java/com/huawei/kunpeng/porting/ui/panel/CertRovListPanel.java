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
 * The class: CertRovListPanel ???????????????????????????
 *
 * @since 2021-8-10
 */
public class CertRovListPanel extends IDEBasePanel {
    /**
     * ???????????????????????????
     */
    protected IDEPanelBaseAction action;
    private final int DETAIL_INDEX = 5;
    private JPanel crlPanel;
    private JPanel mainPanel;

    /**
     * ??????????????????????????????
     */
    private JTable crlTable;
    private JLabel crlDesLabel;

    /**
     * ??????
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
     * ????????????
     */
    private DefaultTableModel tableModel;

    /**
     * ??????????????????
     */
    private List<CertRevListInfoBean> certRevList;

    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarDecorator;


    /**
     * ????????????????????????????????????
     */
    public CertRovListPanel() {
        super.initPanel(mainPanel);
        // ????????????????????????Action
        action = CertificationRevocationListAction.getInstance();
        // ????????????????????????
        crlDesLabel.setText(I18NServer.toLocale("plugins_porting_setting_crl_des"));
        paintCrlTable();
    }

    /**
     * ????????????????????????
     */
    private void paintCrlTable() {
        // ??????????????????
        setTableMode();
        // ???????????????
        crlTable = new JBTable(tableModel);
        fillCrlIntoTable();
        crlDetailColumnAddMouseListener();
        registerToolBarDecorator();
    }

    /**
     * ??????????????????????????????????????????
     */
    private void fillCrlIntoTable() {
        // ????????????????????????
        showCertRevList();
        Vector crl = certRevList.stream().map(element -> element.toVector())
            .collect(Collectors.toCollection(Vector::new));
        tableModel.setDataVector(crl, columnNamesList);
        crlTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // ???????????????????????????
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
     * CRL??????????????????????????????
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
     * ???????????????????????????CRL/??????CRL???
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
     * ???CRL???????????????????????????Import/Delete CRL???
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
     * ??????CRL????????????
     */
    private void showCertRevList() {
        if (action instanceof CertificationRevocationListAction) {
            certRevList = ((CertificationRevocationListAction) action).showCertRevList();
        }
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void setTableMode() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // ????????????
                return false;
            }
        };
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
     * ??????????????????????????????
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

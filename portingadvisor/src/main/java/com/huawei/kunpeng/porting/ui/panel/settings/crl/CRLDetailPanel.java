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

package com.huawei.kunpeng.porting.ui.panel.settings.crl;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.CertRevListInfoBean;
import com.huawei.kunpeng.intellij.common.constant.CertificateConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * The class: CRLDetailPanel
 *
 * @since 2021-8-13
 */
public class CRLDetailPanel extends IDEBasePanel {
    private static final String CRL_DETAIL = "CRL_DETAIL";

    private JPanel mainPanel;
    private JTable detailTable;
    private JTextField certNameTextField;
    private JTextField issuerTextField;
    private JTextField effectiveDateTextField;
    private JTextField nextUpdateTimeTextField;
    private JLabel certNameLabel;
    private JLabel issuerLabel;
    private JLabel effectiveDateLabel;
    private JLabel nextUpdateTimeLabel;
    private JPanel detailPanel;
    private JLabel totalNumLabel;

    private String crlName;

    /**
     * 表格列名称
     */
    private Vector columnNameList = new Vector() {
        {
            add(CertificateConstant.SERIAL_NUMBER);
            add(CertificateConstant.REVOCATION_DATE);
        }
    };

    /**
     * 用户模板
     */
    private DefaultTableModel tableModel;

    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarForOperaTable;


    /**
     * 存放证书吊销列表 key: cert_name
     */
    private HashMap<String, CertRevListInfoBean.CertRevListDetailBean> crlMap = new HashMap<>(3);


    /**
     * Constructor: CRLDetailPanel
     *
     * @param crlName crlName
     */
    public CRLDetailPanel(String crlName) {
        this.panelName = CRL_DETAIL;
        this.crlName = crlName;
        initLabels();
        initCrlMap();
        showTextFieldValue();
        showDetailTable();
    }

    /**
     * 初始化标签
     */
    private void initLabels() {
        certNameLabel.setText(I18NServer.toLocale("plugins_porting_setting_crl_cert_name"));
        issuerLabel.setText(I18NServer.toLocale("plugins_porting_setting_crl_issuer"));
        effectiveDateLabel.setText(I18NServer.toLocale("plugins_porting_setting_crl_effective_date"));
        nextUpdateTimeLabel.setText(I18NServer.toLocale("plugins_porting_setting_crl_next_update_time"));
    }

    /**
     * 初始化CRL Map
     */
    private void initCrlMap() {
        crlMap = IDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
                PortingIDEConstant.CRL_MAP_KEY);
    }

    /**
     * 设置取值
     */
    private void showTextFieldValue() {
        // set text
        certNameTextField.setText(crlName);
        issuerTextField.setText(crlMap.get(crlName).getIssuer());
        effectiveDateTextField.setText(crlMap.get(crlName).getEffectiveDate());
        nextUpdateTimeTextField.setText(crlMap.get(crlName).getNextUpdateDate());

        // set border
        certNameTextField.setBorder(null);
        issuerTextField.setBorder(null);
        effectiveDateTextField.setBorder(null);
        nextUpdateTimeTextField.setBorder(null);
        certNameTextField.setEditable(false);
        issuerTextField.setEditable(false);
        effectiveDateTextField.setEditable(false);
        nextUpdateTimeTextField.setEditable(false);
    }

    /**
     * 表格展示
     */
    private void showDetailTable() {
        // 设置表格模式
        setTableMode();
        // 初始化表格
        fillCrlDetailIntoTable();
    }

    private void setTableMode() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }


    /**
     * 表格数据初始化
     */
    private void fillCrlDetailIntoTable() {
        Vector details = crlMap.get(crlName).getCrlDetail()
                .stream().map(element -> element.toVector()).collect(Collectors.toCollection(Vector::new));
        tableModel.setDataVector(details, columnNameList);
        detailTable = new JBTable(tableModel);
        detailTable.getTableHeader().setReorderingAllowed(false);
        detailTable.setModel(new DefaultTableModel(details, columnNameList) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        toolbarForOperaTable = ToolbarDecorator.createDecorator(detailTable);
        detailPanel.remove(detailTable);
        detailPanel.add(toolbarForOperaTable.createPanel());
        totalNumLabel.setText(I18NServer.toLocale("plugins_porting_Tip_TotalNum") + details.size());
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 返回主面板
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return mainPanel;
    }
}

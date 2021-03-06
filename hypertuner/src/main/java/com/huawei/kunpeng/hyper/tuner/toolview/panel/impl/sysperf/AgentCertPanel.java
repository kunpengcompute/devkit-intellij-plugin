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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.AgentCertAction;
import com.huawei.kunpeng.hyper.tuner.action.sysperf.AgentCertAddAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.AgentCertBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.AgentUpdateDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.AgentCertNameRenderer;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.AgentCertStatusRenderer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;

import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Agent??????????????????
 *
 * @since 2012-10-12
 */
public class AgentCertPanel extends SettingCommonConfigPanel {
    /**
     * IP??????
     */
    private static final int IP_INDEX = 0;
    /**
     * ??????????????????
     */
    private static final int NODE_NAME_INDEX = 1;
    /**
     * ??????????????????
     */
    private static final int CERT_NAME_INDEX = 2;
    /**
     * ????????????????????????
     */
    private static final int CERT_EXPIRE_INDEX = 3;
    /**
     * ??????????????????
     */
    private static final int NODE_STATUS_INDEX = 4;
    /**
     * ???????????????
     */
    private static final int OPERATE_INDEX = 5;

    private boolean isAdmin = UserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole());

    private JPanel mainPanel;

    private AgentCertAction agentCertAction;

    private List<AgentCertBean> agentCertList;
    private HashMap<String, String> ipLocalMap = new HashMap<>();

    private JTable agentTable;

    private JPanel agentPanel;

    private DefaultTableModel tableModel;

    /**
     * ???????????????
     */
    private ToolbarDecorator toolbarForAgentCertTable;

    /**
     * ????????????
     */
    private Vector columnNamesList;

    /**
     * ????????????
     */
    private Object[][] tableDate;

    /**
     * ????????????
     */
    public AgentCertPanel() {
        initPanel();
        // ??????????????????????????????
        registerComponentAction();
    }

    /**
     * ???????????????
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (agentCertAction == null) {
            agentCertAction = new AgentCertAction();
        }

        agentCertList = agentCertAction.getAgentCertList();
        generalIpLocalMap(agentCertList);
        agentTable = new JBTable();

        // ?????????????????????????????????
        createOperaTableColName();

        // ?????? ????????????????????? ??????????????? ??? ??????
        this.tableModel =
            new DefaultTableModel() {
                private static final long serialVersionUID = -2579019475997889830L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        initAgentTable();

        // ??????????????????
        toolbarForAgentCertTable = ToolbarDecorator.createDecorator(agentTable);
        if (isAdmin) {
            addActionToToolBarOperaTable(toolbarForAgentCertTable, agentTable);
        }
        toolbarForAgentCertTable.setPreferredSize(new Dimension(300, 300));
        agentPanel.removeAll();
        agentPanel.add(toolbarForAgentCertTable.createPanel());
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
     * ?????????????????????
     */
    private void initAgentTable() {
        // ?????????????????????
        this.tableDate = getArray(agentCertList.size());
        for (int i = 0; i < agentCertList.size(); i++) {
            String[] item = new String[6];
            item[0] = agentCertList.get(i).getNodeIp();
            item[1] = agentCertList.get(i).getNodeName();
            String col2 = "";
            String col3 = "";
            String col4 = "";
            if (agentCertList.get(i).getCertInfo() == null) {
                return;
            }
            for (int j = 0; j < agentCertList.get(i).getCertInfo().size(); j++) {
                JSONObject obj = agentCertList.get(i).getCertInfo().getObject(j, JSONObject.class);
                col2 += obj.getString("certName") + "  ";
                col3 += obj.getString("certExpTime") + "  ";
                col4 += obj.getString("certStatus") + "  ";
            }
            item[2] = col2;
            item[3] = col3;
            item[4] = col4;
            item[5] = "...";
            this.tableDate[i] = item;
        }

        // ?????????????????????
        this.agentTable.setModel(
            new DefaultTableModel(this.tableDate, columnNamesList.toArray()) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // ????????????????????????
                    if (column == OPERATE_INDEX && row < tableDate.length && row >= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        addListener();
        // ?????????????????????
        this.agentTable.getTableHeader().setReorderingAllowed(false);
        // ??????????????????????????????
        agentTable.getColumnModel().getColumn(CERT_NAME_INDEX).setPreferredWidth(150);
        agentTable.getColumnModel().getColumn(CERT_NAME_INDEX).setCellRenderer(new AgentCertNameRenderer());
        agentTable.getColumnModel().getColumn(CERT_EXPIRE_INDEX).setCellRenderer(new AgentCertNameRenderer());
        agentTable.getColumnModel().getColumn(NODE_STATUS_INDEX).setCellRenderer(new AgentCertStatusRenderer());
    }

    private void createOperaTableColName() {
        // ????????????
        columnNamesList = new Vector();
        columnNamesList.add(AgentCertContent.AGENT_TABLE_NODE_IP);
        columnNamesList.add(AgentCertContent.AGENT_TABLE_NODE_NAME);
        columnNamesList.add(AgentCertContent.AGENT_TABLE_CRET_NAME);
        columnNamesList.add(AgentCertContent.AGENT_TABLE_CRET_TIME);
        columnNamesList.add(AgentCertContent.AGENT_TABLE_NODE_STATUS);
        if (isAdmin) {
            columnNamesList.add(AgentCertContent.AGENT_TABLE_OPER);
        }
    }

    /**
     * ??????????????????
     *
     * @param i i
     * @return ????????????
     */
    public Object[][] getArray(int i) {
        return new Object[i][];
    }

    /**
     * ????????????????????????????????????
     *
     * @param toolbarDecorator ?????????
     * @param table            ??????????????????
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(AgentCertContent.AGENTCERT_ADD_PATH));
        toolbarDecorator.setAddActionName(AgentCertContent.AGENT_CERT_ADD_TITLE);
        toolbarDecorator.setAddAction(new AgentCertAddAction(this.agentTable));
    }

    /**
     * ???????????????
     */
    private void addListener() {
        this.agentTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * ??????????????????
                 *
                 *  @param event ??????
                 */
                public void mouseClicked(MouseEvent event) {
                    int col = agentTable.columnAtPoint(event.getPoint());
                    int row = agentTable.rowAtPoint(event.getPoint());
                    if (col == OPERATE_INDEX && row < tableDate.length && row >= 0) {
                        showPopupMenu(event.getComponent(), event.getX(), event.getY());
                    }
                }
            });
    }

    private void generalIpLocalMap(List<AgentCertBean> list) {
        for (AgentCertBean item : list) {
            if (!ipLocalMap.containsKey(item.getNodeIp())) {
                ipLocalMap.put(item.getNodeIp(), item.getIsLocal());
            }
        }
    }

    private void showPopupMenu(Component invoker, int xPostion, int yPostion) {
        // ?????? ???????????? ??????
        JPopupMenu popupMenu = new JPopupMenu();

        // ?????? ????????????
        JMenuItem updateAgentCert = new JMenuItem(AgentCertContent.AGENT_CERT_CHANGE_TITLE);
        JMenuItem agentWorkKey = new JMenuItem(AgentCertContent.WORK_KRY_CHANGE_TITLE);

        // ?????? ???????????? ??? ????????????
        popupMenu.add(updateAgentCert);
        popupMenu.add(agentWorkKey);

        // ?????????????????????
        int row = agentTable.getSelectedRow();
        String ip = agentTable.getValueAt(row, IP_INDEX).toString();
        String nodeName = agentTable.getValueAt(row, NODE_NAME_INDEX).toString();

        // ?????????????????????????????????
        updateAgentCert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isLocal = ipLocalMap.get(ip);
                if (!Boolean.parseBoolean(isLocal)) {
                    // ???????????????????????????????????????
                    AgentUpdateDialog dialog = new AgentUpdateDialog(AgentCertContent.AGENT_CERT_CHANGE_TITLE,
                        "UpdateAgent", "certificates", new AgentUpdatePanel(ip, nodeName));
                    dialog.displayPanel();
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("ip", ip);
                    obj.put("node_name", nodeName);
                    agentCertAction.updateAgentCert(obj);
                }
            }
        });
        agentWorkKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isLocal = ipLocalMap.get(ip);
                if (!Boolean.parseBoolean(isLocal)) {
                    // ???????????????????????????????????????
                    AgentUpdateDialog dialog = new AgentUpdateDialog(AgentCertContent.WORK_KRY_CHANGE_TITLE,
                        "UpdateWorkKeys", "work-keys", new AgentUpdatePanel(ip, nodeName));
                    dialog.displayPanel();
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("ip", ip);
                    obj.put("node_name", nodeName);
                    agentCertAction.agentWorkKey(obj);
                }
            }
        });
        // ?????????????????????????????????
        popupMenu.show(invoker, xPostion, yPostion);
    }
}

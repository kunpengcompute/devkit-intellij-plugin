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
 * Agent服务证书面板
 *
 * @since 2012-10-12
 */
public class AgentCertPanel extends SettingCommonConfigPanel {
    /**
     * IP索引
     */
    private static final int IP_INDEX = 0;
    /**
     * 节点名称索引
     */
    private static final int NODE_NAME_INDEX = 1;
    /**
     * 证书名称索引
     */
    private static final int CERT_NAME_INDEX = 2;
    /**
     * 证书过期时间索引
     */
    private static final int CERT_EXPIRE_INDEX = 3;
    /**
     * 节点状态索引
     */
    private static final int NODE_STATUS_INDEX = 4;
    /**
     * 操作列索引
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
     * 表格工具栏
     */
    private ToolbarDecorator toolbarForAgentCertTable;

    /**
     * 创建表头
     */
    private Vector columnNamesList;

    /**
     * 表格数据
     */
    private Object[][] tableDate;

    /**
     * 构造函数
     */
    public AgentCertPanel() {
        initPanel();
        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        super.initPanel(mainPanel);
        if (agentCertAction == null) {
            agentCertAction = new AgentCertAction();
        }

        agentCertList = agentCertAction.getAgentCertList();
        generalIpLocalMap(agentCertList);
        agentTable = new JBTable();

        // 添加滚动面板到内容面板
        createOperaTableColName();

        // 创建 表格模型，指定 所有行数据 和 表头
        this.tableModel =
            new DefaultTableModel() {
                private static final long serialVersionUID = -2579019475997889830L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        initAgentTable();

        // 添加操作按钮
        toolbarForAgentCertTable = ToolbarDecorator.createDecorator(agentTable);
        if (isAdmin) {
            addActionToToolBarOperaTable(toolbarForAgentCertTable, agentTable);
        }
        toolbarForAgentCertTable.setPreferredSize(new Dimension(300, 300));
        agentPanel.removeAll();
        agentPanel.add(toolbarForAgentCertTable.createPanel());
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
     * 初始化操作日志
     */
    private void initAgentTable() {
        // 表格所有行数据
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

        // 设置表格编辑行
        this.agentTable.setModel(
            new DefaultTableModel(this.tableDate, columnNamesList.toArray()) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // 操作行才能被编辑
                    if (column == OPERATE_INDEX && row < tableDate.length && row >= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        addListener();
        // 不可以重排列。
        this.agentTable.getTableHeader().setReorderingAllowed(false);
        // 给状态列设置渲染器。
        agentTable.getColumnModel().getColumn(CERT_NAME_INDEX).setPreferredWidth(150);
        agentTable.getColumnModel().getColumn(CERT_NAME_INDEX).setCellRenderer(new AgentCertNameRenderer());
        agentTable.getColumnModel().getColumn(CERT_EXPIRE_INDEX).setCellRenderer(new AgentCertNameRenderer());
        agentTable.getColumnModel().getColumn(NODE_STATUS_INDEX).setCellRenderer(new AgentCertStatusRenderer());
    }

    private void createOperaTableColName() {
        // 创建表头
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
     * 获取数组长度
     *
     * @param i i
     * @return 数组长度
     */
    public Object[][] getArray(int i) {
        return new Object[i][];
    }

    /**
     * 给操作日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     * @param table            操作日志表格
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator, JTable table) {
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(AgentCertContent.AGENTCERT_ADD_PATH));
        toolbarDecorator.setAddActionName(AgentCertContent.AGENT_CERT_ADD_TITLE);
        toolbarDecorator.setAddAction(new AgentCertAddAction(this.agentTable));
    }

    /**
     * 列监听时间
     */
    private void addListener() {
        this.agentTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * 鼠标点击事件
                 *
                 *  @param event 事件
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
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();

        // 创建 一级菜单
        JMenuItem updateAgentCert = new JMenuItem(AgentCertContent.AGENT_CERT_CHANGE_TITLE);
        JMenuItem agentWorkKey = new JMenuItem(AgentCertContent.WORK_KRY_CHANGE_TITLE);

        // 添加 一级菜单 到 弹出菜单
        popupMenu.add(updateAgentCert);
        popupMenu.add(agentWorkKey);

        // 获取所选行数据
        int row = agentTable.getSelectedRow();
        String ip = agentTable.getValueAt(row, IP_INDEX).toString();
        String nodeName = agentTable.getValueAt(row, NODE_NAME_INDEX).toString();

        // 添加菜单项的点击监听器
        updateAgentCert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isLocal = ipLocalMap.get(ip);
                if (!Boolean.parseBoolean(isLocal)) {
                    // 不是主节点，则弹窗然后返回
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
                    // 不是主节点，则弹窗然后返回
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
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xPostion, yPostion);
    }
}

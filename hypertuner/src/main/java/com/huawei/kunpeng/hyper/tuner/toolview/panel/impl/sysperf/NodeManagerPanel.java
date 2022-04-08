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

import com.huawei.kunpeng.hyper.tuner.action.panel.LogAction;
import com.huawei.kunpeng.hyper.tuner.action.sysperf.NodeAddAction;
import com.huawei.kunpeng.hyper.tuner.action.sysperf.NodeManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeManagerBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.TableHeaderRender;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.SysperfTableRenderer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.render.LogTableRenderer;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.TableUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * 节点管理面板
 *
 * @since 2012-10-12
 */
public class NodeManagerPanel extends SettingCommonConfigPanel {
    private JPanel mainPanel;

    private NodeManagerAction nodeManagerAction;

    private transient List<NodeManagerBean> nodeList;

    private JPanel nodePanel;

    private JTable nodeTable;

    private DefaultTableModel tableModel;

    private Object[][] tableDate;

    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarForOperaTable;

    private List<String> columnNameList = new ArrayList<String>();
    private boolean isAdmin;

    /**
     * 构造函数
     */
    public NodeManagerPanel() {
        initPanel(mainPanel);
        // 初始化面板内组件事件
        registerComponentAction();
    }

    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(mainPanel);
        if (nodeManagerAction == null) {
            nodeManagerAction = new NodeManagerAction();
        }
        isAdmin = UserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole());
        selectNodeList();
        setTableModel(); // 设置表头模型。
        nodeTable = new JBTable(tableModel);
        initTable();
        toolbarForOperaTable = ToolbarDecorator.createDecorator(nodeTable);
        addActionToToolBarOperaTable(toolbarForOperaTable);
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        nodePanel.add(toolbarForOperaTable.createPanel());
    }

    /**
     * 给操作日志表格添加工具栏
     *
     * @param toolbarDecorator 工具栏
     */
    private void addActionToToolBarOperaTable(ToolbarDecorator toolbarDecorator) {
        if (isAdmin) {
            toolbarDecorator.setAddActionName(NodeManagerContent.NODE_MANAGER_ADD);
            toolbarDecorator.setAddAction(new NodeAddAction(this.nodeTable));
        }
        // 刷新表格
        AnActionButton nodeUpdate =
            AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.nodeUpdate"));
        toolbarDecorator.addExtraAction(nodeUpdate);
    }

    /**
     * 设置表格模式。
     */
    private void setTableModel() {
        // 创建 表格模型，指定 所有行数据 和 表头
        tableModel =
            new DefaultTableModel(this.tableDate, columnNameList.toArray()) {
                private static final long serialVersionUID = 1529594828549047055L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    if (column == 9 && row < tableDate.length) {
                        return true;
                    }
                    return false;
                }
            };
    }

    /**
     * 取消操作按钮置灰。
     */
    public void cancelGrayed() {
        final CommonActionsPanel actionsPanel = toolbarForOperaTable.getActionsPanel();
        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, true);
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
     * 初始化操作日志
     */
    private void initTable() {
        // 表格所有行数据
        this.tableDate = getArray(nodeList.size());
        for (int i = 0; i < nodeList.size(); i++) {
            String[] item = new String[10];
            item[0] = nodeList.get(i).getId();
            item[1] = nodeList.get(i).getNickName();
            item[2] = nodeList.get(i).getNodeStatus();
            item[3] = nodeList.get(i).getNodeIp();
            item[4] =
                nodeList.get(i).getNodePort() == "" || nodeList.get(i).getNodePort() == null
                    ? "--"
                    : nodeList.get(i).getNodePort();
            item[5] = nodeList.get(i).getUserName().length() == 0 ? "--" : nodeList.get(i).getUserName();
            item[6] = nodeList.get(i).getInstallPath().length() == 0 ? "--" : nodeList.get(i).getInstallPath();
            item[7] = getRemainingSpace(nodeList.get(i).getId(), nodeList.get(i).getNodeIp() + ".Installation");
            item[8] = getRemainingSpace(nodeList.get(i).getId(), nodeList.get(i).getNodeIp() + ".Log");
            item[9] = "...";
            tableDate[i] = item;
        }
        createNodeTableColName();
        tableModel.setDataVector(tableDate, columnNameList.toArray());
        nodeTable.setModel(tableModel);
        // 不可以重排列。
        nodeTable.getTableHeader().setReorderingAllowed(false);
        addListener();
        CommonTableUtil.hideColumn(nodeTable, NodeManagerContent.ID_COLUMN_INDEX);

        // 给状态列设置渲染器。
        nodeTable.getTableHeader().setDefaultRenderer(new TableHeaderRender());
        nodeTable.getColumnModel().getColumn(2).setCellRenderer(new LogTableRenderer());
        nodeTable.getColumnModel().getColumn(7).setCellRenderer(new SysperfTableRenderer("nodeTable"));
        nodeTable.getColumnModel().getColumn(8).setCellRenderer(new SysperfTableRenderer("nodeTable"));

        nodeTable.addMouseMotionListener(
            new MouseAdapter() {
                /**
                 * 悬浮提示单元格的值
                 *
                 * @param event 事件
                 */
                @Override
                public void mouseMoved(MouseEvent event) {
                    mouse(event);
                }
            });
    }

    private void createNodeTableColName() {
        columnNameList.clear();
        columnNameList.add(NodeManagerContent.USER_LABEL_USER_ID);
        columnNameList.add(NodeManagerContent.NODE_TABLE_NODENAME);
        columnNameList.add(NodeManagerContent.NODE_TABLE_NODESTATUS);
        columnNameList.add(NodeManagerContent.NODE_TABLE_NODEIP);
        columnNameList.add(NodeManagerContent.NODE_TABLE_NODEPORT);
        columnNameList.add(NodeManagerContent.NODE_TABLE_USERNAME);
        columnNameList.add(NodeManagerContent.NODE_TABLE_INSTALLPATH);
        columnNameList.add(NodeManagerContent.NODE_TABLE_RUNDIR);
        columnNameList.add(NodeManagerContent.NODE_TABLE_LOGDIR);
        if (isAdmin) {
            columnNameList.add(NodeManagerContent.NODE_TABLE_OPER);
        }
    }

    private void mouse(MouseEvent event) {
        int row = nodeTable.rowAtPoint(event.getPoint());
        int col = nodeTable.columnAtPoint(event.getPoint());
        if (row < tableDate.length && row >= 0) {
            Object value = nodeTable.getValueAt(row, col);
            if ((col == 7 || col == 8) && !"--".equals(value)) {
                String colText = Integer.valueOf(value.toString()) > 1024 ? "2GB" : "150MB";
                if (col == 7) {
                    colText = MessageFormat.format(NodeManagerContent.NODE_TABLE_TIPS, colText);
                    nodeTable.setToolTipText(colText); // 悬浮显示单元格内容
                }
                if (col == 8) {
                    colText = MessageFormat.format(NodeManagerContent.NODE_TABLE_TIPS1, colText);
                    nodeTable.setToolTipText(colText); // 悬浮显示单元格内容
                }
            }
        } else {
            nodeTable.setToolTipText("");
        }
    }

    /**
     * 设置和更新表格数据
     */
    public void updateNodeTable() {
        isAdmin = UserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole());
        selectNodeList();
        initTable(); // 将查询到的最新用户数据更新到表格中
        IdeFocusManager.getGlobalInstance()
            .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(nodeTable, true));
        TableUtil.updateScroller(nodeTable);
        toolbarForOperaTable = ToolbarDecorator.createDecorator(nodeTable);
        addActionToToolBarOperaTable(toolbarForOperaTable);
        nodePanel.removeAll();
        toolbarForOperaTable.setPreferredSize(new Dimension(300, 300));
        nodePanel.add(toolbarForOperaTable.createPanel());
    }

    /**
     * 列监听时间
     */
    private void addListener() {
        this.nodeTable.addMouseListener(
            new MouseAdapter() {
                /**
                 * 鼠标点击事件
                 *
                 *  @param event 事件
                 */
                @Override
                public void mouseClicked(MouseEvent event) {
                    int col = nodeTable.columnAtPoint(event.getPoint());
                    int row = nodeTable.rowAtPoint(event.getPoint());
                    if (col == 9 && row < tableDate.length && row >= 0) {
                        showPopupMenu(event.getComponent(), event.getX(), event.getY());
                    }
                }
            });
    }

    private void showPopupMenu(Component invoker, int xPostion, int yPostion) {
        int row = nodeTable.getSelectedRow();
        String nodeId = nodeTable.getValueAt(row, 0).toString();
        String status = nodeTable.getValueAt(row, 2).toString();
        String ip = nodeTable.getValueAt(row, 3).toString();
        String port = nodeTable.getValueAt(row, 4).toString();

        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem update = new JMenuItem(NodeManagerContent.NODE_TABLE_UPDATE);
        if (!"--".equals(port) && port != null) {
            JMenuItem delete = new JMenuItem(NodeManagerContent.NODE_TABLE_DELETE);
            JMenuItem installLog = new JMenuItem(NodeManagerContent.NODE_TABLE_INSTALL_LOG);

            // 添加 一级菜单 到 弹出菜单
            popupMenu.add(installLog);
            popupMenu.add(delete);
            installLog.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        nodeManagerAction.installLog(nodeId.toString(), ip.toString());
                    }
                });
            // 添加菜单项的点击监听器
            delete.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        nodeManagerAction.deleteNode(nodeId, ip, port);
                    }
                });
        }
        if (!(TuningI18NServer.toLocale("plugins_hyper_tuner_fail")).equals(status)) {
            popupMenu.add(update);
        }

        // 添加菜单项的点击监听器
        update.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    nodeManagerAction.updateNode(nodeId, ip);
                }
            });

        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xPostion, yPostion);
    }

    /**
     * 剩余内存
     *
     * @param nodeId nodeId
     * @param key    key
     * @return 剩余内存
     */
    public String getRemainingSpace(String nodeId, String key) {
        String resp = "";
        RequestDataBean message =
            new RequestDataBean(
                TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/projects/"
                    + nodeId
                    + "/alarm/?auto-flag=on&date="
                    + System.currentTimeMillis(),
                HttpMethod.GET.vaLue(),
                "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return resp;
        }
        Map<String, JSONObject> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());

        JSONObject spaceObject = jsonMessage.get("agent_alarm_data");

        if (spaceObject.get(key) == null) {
            return resp;
        }
        JSONObject valueObject = new JSONObject();
        if (spaceObject.get(key) instanceof JSONObject) {
            valueObject = (JSONObject) spaceObject.get(key);
        }
        return valueObject.get("tool_value_free").toString();
    }

    /**
     * 查询用户列表。
     */
    private void selectNodeList() {
        nodeList = nodeManagerAction.getNodeList();
    }

    /**
     * 返回数组
     *
     * @param i i
     * @return 返回数组
     */
    public Object[][] getArray(int i) {
        return new Object[i][];
    }
}

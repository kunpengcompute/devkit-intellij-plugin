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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.sysperf.AgentCertAction;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.FingerprintsBean;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import org.jetbrains.annotations.Nullable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * 更新 Agent 证书时提示的弹窗
 *
 * @since 2021-06-17
 */
public class AgentTipsDialog extends IdeaDialog {
    /**
     * 提示图片
     */
    private static final String DISCLAIMER_INFO_PNG = "/assets/img/settings/disclaimer_dialog.png";

    private String messages = "";

    private Map<String, String> params;

    private ActionOperate actionOperate;

    private String userRole;

    private String nodeIp;

    private boolean isRoot;

    private String operateType;

    // 指纹表格数据
    private List<FingerprintsBean> fingerprintsBeanList;

    // 表头
    private Vector columnNamesList;

    private JPanel fingerprintsPanel;

    private DefaultTableModel tableModel;

    private JTable fingerprintsTable;

    private Project project;

    private ToolbarDecorator toolbarTable;

    private AgentCertAction agentCertAction;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public AgentTipsDialog(String title, IDEBasePanel panel, Map<String, String> params,
        List<FingerprintsBean> fingerprintsBeans, ActionOperate actionOperate, String operateType) {
        this.title = title;
        this.dialogName = title;
        this.mainPanel = panel;
        this.params = params;
        this.actionOperate = actionOperate;
        this.userRole = params.get("user");
        this.nodeIp = params.get("ip");
        this.operateType = operateType;
        this.fingerprintsBeanList = fingerprintsBeans;

        if (agentCertAction == null) {
            agentCertAction = new AgentCertAction();
        }
        if (Objects.equals(userRole, "root")) {
            isRoot = true;
        }
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 主面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(780, 220));
        JLabel test = new JLabel("For Tips");
        test.setEnabled(false);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(780, 40));
        mainPanel.add(panelWithHtmlListener(messages, test), BorderLayout.NORTH);
        centerPanel.add(mainPanel, BorderLayout.NORTH);
        fingerprintsPanel = new JPanel(new BorderLayout());
        fingerprintsTable = createMd5Table();
        toolbarTable = ToolbarDecorator.createDecorator(fingerprintsTable);
        fingerprintsPanel.add(toolbarTable.createPanel());
        centerPanel.add(fingerprintsPanel);
        return centerPanel;
    }

    /**
     * 安装部署外部超链接
     *
     * @param msg          链接内容
     * @param installTitle installTitle
     * @return jPanel 主面板
     */
    private JPanel panelWithHtmlListener(String msg, JLabel installTitle) {
        Color color = installTitle.getForeground();
        Font font = installTitle.getFont();
        messages = MessageFormat.format(msg, color.getRed(), color.getGreen(), color.getBlue(),
                font.getFontName(), font.getSize(), this.nodeIp);

        JEditorPane jEditorPane = new JEditorPane("text/html", messages);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JLabel icon = new JLabel(new ImageIcon(AgentTipsDialog.class.getResource(DISCLAIMER_INFO_PNG)));

        tip(jEditorPane, jPanel, icon);
        return jPanel;
    }

    /**
     * 提醒
     *
     * @param jEditorPane jEditorPane
     * @param jPanel jPanel
     * @param icon icon
     */
    public void tip(JEditorPane jEditorPane, JPanel jPanel, JLabel icon) {
        if (!isRoot) {
            String userTips =
                    MessageFormat.format(
                            TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_fingerprints_user"), this.nodeIp);
            icon.setText(userTips);
            jPanel.add(icon, BorderLayout.WEST);
            jPanel.add(jEditorPane, BorderLayout.SOUTH);
        } else {
            String rootTips =
                    MessageFormat.format(
                            TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_fingerprints_root"), this.nodeIp);
            icon.setText(rootTips);
            jPanel.add(icon, BorderLayout.LINE_START);
            jPanel.add(jEditorPane, BorderLayout.CENTER);
        }
    }

    /**
     * createMd5Table
     *
     * @return JTable
     */
    public JTable createMd5Table() {
        fingerprintsTable = new JBTable();
        // 生成表头列
        createOperaTableColName();
        // 表格所有行数据
        Vector operator = new Vector();
        if (fingerprintsBeanList != null) {
            for (int i = 0; i < fingerprintsBeanList.size(); i++) {
                Vector row = new Vector();
                row.add(fingerprintsBeanList.get(i).getHashType());
                row.add(fingerprintsBeanList.get(i).getKeyType());
                row.add(fingerprintsBeanList.get(i).getFingerPrint());
                operator.add(row);
            }
        }
        // 创建 表格模型，指定 所有行数据 和 表头
        tableModel =
                new DefaultTableModel() {
                    private static final long serialVersionUID = -2579019475997889830L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        tableModel.setDataVector(operator, columnNamesList);
        // 不可以重排列。
        fingerprintsTable.getTableHeader().setReorderingAllowed(false);
        fingerprintsTable.setModel(
                new DefaultTableModel(operator, columnNamesList) {
                    private static final long serialVersionUID = -6152279736099789121L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
        fingerprintsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        fingerprintsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        fingerprintsTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        return fingerprintsTable;
    }

    /**
     * 生成表头
     *
     */
    public void createOperaTableColName() {
        columnNamesList = new Vector();
        columnNamesList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_node_fingerprints_table_col1"));
        columnNamesList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_node_fingerprints_table_col2"));
        columnNamesList.add(TuningI18NServer.toLocale("plugins_hyper_tuner_node_fingerprints_table_col3"));
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start AccountTipsDialog.");
        JSONObject bodyJSONData = getMessageBodyJSONData();
        if ("certificates".equals(this.operateType)) {
            agentCertAction.updateAgentCert(bodyJSONData);
        } else {
            agentCertAction.agentWorkKey(bodyJSONData);
        }
    }

    private JSONObject getMessageBodyJSONData() {
        JSONObject obj = new JSONObject();
        obj.put("ip", params.get("ip"));
        if (params.get("nodeName").equals(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_nodeip_tip"))) {
            obj.put("node_name", params.get(""));
        } else {
            obj.put("node_name", params.get("nodeName"));
        }
        obj.put("user_name", params.get("user"));
        obj.put("verification_method", params.get("varifyType"));
        if ("password".equals(params.get("varifyType"))) {
            obj.put("password", params.get("password"));
        } else {
            obj.put("identity_file", params.get("privateKey"));
            obj.put("passphrase", params.get("privateKeyPwd"));
        }
        if (!"root".equals(params.get("user"))) {
            obj.put("root_password", params.get("rootPwd"));
        }
        return obj;
    }

    /**
     * onCancelAction 取消安装
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel AccountTipsDialog.");
    }
}

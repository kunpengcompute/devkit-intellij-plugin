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

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.FingerprintsBean;
import com.huawei.kunpeng.hyper.tuner.setting.sysperf.nodemanager.NodeMangerConfigurable;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;

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
 * 安装账户判断提示的弹窗
 *
 * @since 2020-12-21
 */
public class NodeTipsDialog extends IdeaDialog {
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

    private String type;

    // 指纹表格数据
    private List<FingerprintsBean> fingerprintsBeanList;

    // 表头
    private Vector columnNamesList;

    private JPanel fingerprintsPanel;

    private DefaultTableModel tableModel;

    private JTable fingerprintsTable;

    private Project project;

    private ToolbarDecorator toolbarTable;

    private AgentTipsDialog agentTipsDialog;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public NodeTipsDialog(
            String title,
            IDEBasePanel panel,
            Map<String, String> params,
            List<FingerprintsBean> fingerprintsBeanList,
            ActionOperate actionOperate,
            String type) {
        this.title = title;
        this.dialogName = title;
        this.mainPanel = panel;
        this.params = params;
        this.actionOperate = actionOperate;
        this.userRole = params.get("user");
        this.nodeIp = params.get("ip");
        this.type = type;
        this.fingerprintsBeanList = fingerprintsBeanList;
        this.agentTipsDialog = new AgentTipsDialog(title, panel, params, fingerprintsBeanList, actionOperate, type);

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
        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.setPreferredSize(new Dimension(780, 40));
        JLabel test = new JLabel("For Tips");
        test.setEnabled(false);

        mainPanel.add(panelWithHtmlListener(messages, test), BorderLayout.NORTH);
        centerPanel.add(mainPanel, BorderLayout.NORTH);
        fingerprintsPanel = new JPanel(new BorderLayout());
        fingerprintsTable = agentTipsDialog.createMd5Table();
        toolbarTable = ToolbarDecorator.createDecorator(fingerprintsTable);
        fingerprintsPanel.add(toolbarTable.createPanel());
        centerPanel.add(fingerprintsPanel);
        return centerPanel;
    }

    /**
     * 跳转到Install面板
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start AccountTipsDialog.");
        RequestDataBean message;
        JSONObject obj = new JSONObject();
        String tip = "";
        if ("add".equals(this.type)) {
            message =
                    new RequestDataBean(
                            TuningIDEConstant.TOOL_NAME_TUNING,
                            "sys-perf/api/v2.2/nodes/",
                            HttpMethod.POST.vaLue(),
                            "");
            addObj(obj);
            tip = NodeManagerContent.NODE_MANAGER_ADD;
        } else {
            message =
                    new RequestDataBean(
                            TuningIDEConstant.TOOL_NAME_TUNING,
                            "sys-perf/api/v2.2/nodes/" + params.get("id") + "/",
                            HttpMethod.DELETE.vaLue(),
                            "");
            delObj(obj);
            tip = NodeManagerContent.NODE_TABLE_DELETE;
        }
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseData = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return;
        }
        processNodeRep(tip, responseData.getCode(), responseData.getMessage());
    }

    private JSONObject addObj(JSONObject obj) {
        if (params.get("nodeName").equals(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_nodeip_tip"))) {
            obj.put("node_name", params.get(""));
        } else {
            obj.put("node_name", params.get("nodeName"));
        }
        obj.put("ip", params.get("ip"));
        obj.put("port", params.get("port"));
        obj.put("user_name", params.get("user"));
        obj.put("verification_method", params.get("varifyType"));
        obj.put("agent_install_path", params.get("installPath"));
        objPutPassword(obj);
        return obj;
    }

    private JSONObject delObj(JSONObject obj) {
        obj.put("ip", params.get("ip"));
        obj.put("user_name", params.get("user"));
        obj.put("verification_method", params.get("varifyType"));
        objPutPassword(obj);
        return obj;
    }

    private void objPutPassword(JSONObject obj) {
        if ("password".equals(params.get("varifyType"))) {
            obj.put("password", params.get("password"));
        } else {
            obj.put("passphrase", params.get("privateKeyPwd"));
            obj.put("identity_file", params.get("privateKey"));
        }
        if (!"root".equals(params.get("user"))) {
            obj.put("root_password", params.get("rootPwd"));
        }
    }

    // 处理接口返回
    private void processNodeRep(String tip, String resCode, String messages) {
        String message = messages;
        switch (resCode) {
            case "SysPerf.NodeManage.Add.ParameterSuccess":
                NodeMangerConfigurable.getNodeManageSettingsComponent().updateNodeTable();
                message = TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_success");
                message(tip, message, NotificationType.INFORMATION);
                break;
            case "SysPerf.NodeManage.Del.Success":
                NodeMangerConfigurable.getNodeManageSettingsComponent().updateNodeTable();
                message(tip, message, NotificationType.INFORMATION);
                break;
            default:
                NodeMangerConfigurable.getNodeManageSettingsComponent().updateNodeTable();
                message(tip, message, NotificationType.ERROR);
        }
    }

    /**
     * onCancelAction 取消安装
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel AccountTipsDialog.");
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
        messages =
                MessageFormat.format(
                        msg,
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        font.getFontName(),
                        font.getSize(),
                        this.nodeIp);

        JEditorPane jEditorPane = new JEditorPane("text/html", messages);
        jEditorPane.setEditable(false);
        jEditorPane.setOpaque(false);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JLabel icon = new JLabel(new ImageIcon(NodeTipsDialog.class.getResource(DISCLAIMER_INFO_PNG)));

        agentTipsDialog.tip(jEditorPane, jPanel, icon);
        return jPanel;
    }

    // 提示信息
    private void message(String tip, String message, NotificationType type) {
        IDENotificationUtil.notificationCommon(new NotificationBean(tip, message, type));
    }
}

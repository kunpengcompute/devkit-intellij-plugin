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

import com.huawei.kunpeng.hyper.tuner.action.sysperf.NodeManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.util.CheckedUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 新增修改用户
 *
 * @since 2020-10-13
 */
public class NodeUpdatePanel extends IDEBasePanel {
    private static final long serialVersionUID = 5266605925139711671L;

    /**
     * 用户名正则表达式
     */
    private static final String USER_NAME_REG_EXP = "^[a-zA-Z][a-zA-Z0-9_-]{5,31}$";

    private JPanel mainPanel;

    private JPanel ipPanel;

    private JLabel ipLable;

    private JTextField ipField;

    private JPanel nodeNamePanel;

    private JLabel nodeNameLabel;

    private JTextField nodeNameField;

    private JPanel idPanel;
    private JLabel idLabel;
    private JTextField idField;

    /**
     * nodeIp
     */
    private String nodeId;

    /**
     * nodeIP
     */
    private String nodeIp;

    /**
     * 修改用户创建实例
     *
     * @param nodeId nodeId
     * @param nodeIp userId
     */
    public NodeUpdatePanel(Object nodeId, Object nodeIp) {
        this.nodeId = nodeId.toString();
        this.nodeIp = nodeIp.toString();

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, NodeManagerContent.NODE_MANAGER_UPDATE, false);
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    @Override
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        // 创建用户时用户名校验处理
        if (!CheckedUtils.checkUserName(nodeNameField.getText())) {
            vi = new ValidationInfo(UserManageConstant.TERM_CHECK_USERNAME, nodeNameField);
        }

        return vi;
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        panel.setPreferredSize(new Dimension(545, 20));
        idField.setText(nodeId);
        idPanel.hide();
        ipLable.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_nodeip"));
        ipField.setText(nodeIp);
        ipField.disable();
        nodeNameLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_node_table_nodename"));
    }

    public JTextField getIdField() {
        return idField;
    }

    public JTextField getIpField() {
        return ipField;
    }

    public JTextField getNodeNameField() {
        return nodeNameField;
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new NodeManagerAction();
        }
    }

    /**
     * setAction
     *
     * @param baseAction 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction baseAction) {
        if (action instanceof NodeManagerAction) {
            this.action = baseAction;
            registerComponentAction();
        }
    }
}

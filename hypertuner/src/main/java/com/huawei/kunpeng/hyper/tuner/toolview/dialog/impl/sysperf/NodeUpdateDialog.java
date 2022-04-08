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
import com.huawei.kunpeng.hyper.tuner.setting.sysperf.nodemanager.NodeMangerConfigurable;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.NodeUpdatePanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 修改节点
 *
 * @since 2012-10-12
 */
public class NodeUpdateDialog extends IdeaDialog {
    private NodeUpdatePanel nodeUpdatePanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public NodeUpdateDialog(String title, String dialogName, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? NodeManagerContent.NODE_MANAGER_UPDATE : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? NodeManagerContent.NODE_MANAGER_UPDATE : dialogName;
        this.mainPanel = panel;
        if (panel instanceof NodeUpdatePanel) {
            nodeUpdatePanel = (NodeUpdatePanel) panel;
        }
        setOKAndCancelName(
                CommonI18NServer.toLocale("common_term_operate_ok"),
                CommonI18NServer.toLocale("common_term_operate_cancel"));
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 添加弱口令弹框内容
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认修改节点
     */
    @Override
    protected void onOKAction() {
        // 新增数据后进行查询
        if (mainPanel instanceof NodeUpdatePanel) {
            nodeUpdatePanel = (NodeUpdatePanel) mainPanel;
            String id = nodeUpdatePanel.getIdField().getText();
            String ip = nodeUpdatePanel.getIpField().getText();
            String nodeName = nodeUpdatePanel.getNodeNameField().getText();

            String url = "sys-perf/api/v2.2/nodes/" + id + "/";
            RequestDataBean message =
                    new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.PUT.vaLue(), "");
            JSONObject obj = new JSONObject();
            obj.put("ipaddr", ip);
            obj.put("nickName", nodeName);
            message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
            ResponseBean rsp = TuningHttpsServer.INSTANCE.requestData(message);
            if (rsp == null) {
                return;
            }
            respUpdatMsg(rsp);
        }
    }

    private void respUpdatMsg(ResponseBean rsp) {
        String msg;
        if (rsp.getCode().equals(NodeManagerContent.RESPONSE_CODE)) {
            msg = TuningI18NServer.toLocale("plugins_hyper_tuner_node_update_success");
            NodeMangerConfigurable.getNodeManageSettingsComponent().updateNodeTable();
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(
                            NodeManagerContent.NODE_MANAGER_UPDATE, msg, NotificationType.INFORMATION));
            return;
        } else if ("SysPerf.NodeManage.Modify.Conflict".equals(rsp.getCode())) {
            msg = TuningI18NServer.toLocale("plugins_hyper_tuner_node_update_conflict");
        } else if ("SysPerf.Common.Check.AppNoRight".equals(rsp.getCode())) {
            msg = TuningI18NServer.toLocale("plugins_hyper_tuner_node_update_appnoright");
        } else if ("SysPerf.NodeManage.node.parameter.Error".equals(rsp.getCode())) {
            msg = TuningI18NServer.toLocale("plugins_hyper_tuner_node_add_parameterError");
        } else {
            msg = TuningI18NServer.toLocale("plugins_hyper_tuner_node_oper_failed");
        }
        IDENotificationUtil.notificationCommon(
                new NotificationBean(NodeManagerContent.NODE_MANAGER_UPDATE, msg, NotificationType.ERROR));
    }

    /**
     * 确认删除弱口令
     */
    @Override
    protected void onCancelAction() {
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected ValidationInfo doValidate() {
        return this.mainPanel.doValidate();
    }
}

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

package com.huawei.kunpeng.hyper.tuner.action.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.FingerprintsBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.NodeManagerBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.NodeDeleteDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.NodeInstallLogDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.NodeUpdateDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.NodeDeletePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.NodeUpdatePanel;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.intellij.notification.NotificationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 节点管理Action
 *
 * @since 2012-10-12
 */
public class NodeManagerAction extends IDEPanelBaseAction {
    /**
     * 查询节点信息列表
     *
     * @return 节点信息列表
     */
    public List<NodeManagerBean> getNodeList() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/nodes/?auto-flag=on&page=1&per-page=10", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<NodeManagerBean> respBean = new ArrayList<>();
        if (responseBean == null) {
            return respBean;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object loglist = jsonMessage.get("nodeList");
        if (loglist instanceof JSONArray) {
            JSONArray nodeJson = (JSONArray) loglist;
            NodeManagerBean nodeManagerBean;
            for (int mun = 0; mun < nodeJson.size(); mun++) {
                nodeManagerBean = nodeJson.getObject(mun, NodeManagerBean.class);
                getStatus(nodeManagerBean);
                respBean.add(nodeManagerBean);
            }
        }
        return respBean;
    }

    private void getStatus(NodeManagerBean nodeManagerBean) {
        String nodeStatus = nodeManagerBean.getNodeStatus();
        if ("on".equals(nodeStatus)) {
            nodeManagerBean.setNodeStatus(TuningI18NServer.toLocale("plugins_hyper_tuner_on"));
        } else if ("init".equals(nodeStatus)) {
            nodeManagerBean.setNodeStatus(TuningI18NServer.toLocale("plugins_hyper_tuner_init"));
        } else if ("off".equals(nodeStatus)) {
            nodeManagerBean.setNodeStatus(TuningI18NServer.toLocale("plugins_hyper_tuner_off"));
        } else {
            nodeManagerBean.setNodeStatus(TuningI18NServer.toLocale("plugins_hyper_tuner_fail"));
        }
    }

    /**
     * 修改节点
     *
     * @param nodeId panel
     * @param nodeIp actionOperate
     */
    public void updateNode(String nodeId, String nodeIp) {
        NodeUpdateDialog dialog = new NodeUpdateDialog(NodeManagerContent.NODE_MANAGER_UPDATE,
                NodeManagerContent.NODE_MANAGER_UPDATE,
                new NodeUpdatePanel(nodeId, nodeIp));
        dialog.displayPanel();
    }

    /**
     * 删除节点
     *
     * @param nodeId   panel
     * @param nodeIp   panel
     * @param nodePort nodePort
     */
    public void deleteNode(String nodeId, String nodeIp, String nodePort) {
        NodeDeleteDialog dialog = new NodeDeleteDialog(NodeManagerContent.NODE_MANAGER_DELETE,
                NodeManagerContent.NODE_MANAGER_DELETE,
                new NodeDeletePanel(nodeId, nodeIp, nodePort));
        dialog.displayPanel();
    }

    /**
     * 获取指纹列表
     *
     * @param ip   ip
     * @param port port
     * @return 指纹列表
     */
    public List<FingerprintsBean> fingerPrintList(String ip, String port) {
        List<FingerprintsBean> fingerprintsBeans = new ArrayList<>();
        // 获取节点返回指纹
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                        "/sys-perf/api/v2.2/nodes/None/finger-print/?node_ip=" + ip + "&ssh_port=" + port,
                        HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return fingerprintsBeans;
        }
        if (responseBean.getCode().equals(NodeManagerContent.RESPONSE_CODE)) {
            Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            Object md5List = jsonMessage.get("md5");
            Object sha256List = jsonMessage.get("sha256");
            if (md5List instanceof JSONArray) {
                JSONArray md5Json = (JSONArray) md5List;
                FingerprintsBean fingerprintsBean;
                for (int mun = 0; mun < md5Json.size(); mun++) {
                    fingerprintsBean = md5Json.getObject(mun, FingerprintsBean.class);
                    fingerprintsBeans.add(fingerprintsBean);
                }
            }
            if (sha256List instanceof JSONArray) {
                JSONArray sha256Json = (JSONArray) sha256List;
                FingerprintsBean fingerprintsBean;
                for (int mun = 0; mun < sha256Json.size(); mun++) {
                    fingerprintsBean = sha256Json.getObject(mun, FingerprintsBean.class);
                    fingerprintsBeans.add(fingerprintsBean);
                }
            }
            return fingerprintsBeans;
        } else {
            processNodeRep(responseBean.getMessage());
            return fingerprintsBeans;
        }
    }

    private void processNodeRep(String message) {
        String msg = message;
        IDENotificationUtil.notificationCommon(
                new NotificationBean(NodeManagerContent.NODE_MANAGER_FINGERPRINTS, msg,
                        NotificationType.ERROR));
    }

    ;

    /**
     * 查看安装日志
     *
     * @param nodeId nodeId
     * @param ip     ip
     */
    public void installLog(String nodeId, String ip) {
        NodeInstallLogDialog dialog = new NodeInstallLogDialog(nodeId, ip, null);
        if (dialog != null) {
            dialog.displayPanel();
        }
    }
}
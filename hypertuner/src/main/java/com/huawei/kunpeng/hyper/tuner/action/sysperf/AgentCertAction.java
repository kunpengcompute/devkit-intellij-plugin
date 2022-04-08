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
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.AgentCertBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.FingerprintsBean;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 弱口令设置实现类
 *
 * @since 2012-10-12
 */
public class AgentCertAction extends IDEPanelBaseAction {
    private static AgentCertAction instance = new AgentCertAction();

    public static AgentCertAction getInstance() {
        return instance;
    }

    /**
     * 查询证书列表
     *
     * @return 日志列表
     */
    public List<AgentCertBean> getAgentCertList() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/certificates/", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<AgentCertBean> agentCertBeans = new ArrayList<>();
        if (responseBean == null) {
            return agentCertBeans;
        }
        JSONArray jsonArray = JSONArray.parseArray(responseBean.getData());

        AgentCertBean agentCertBean;
        if (CollectionUtils.isNotEmpty(jsonArray)) {
            for (int i = 0; i < jsonArray.size(); i++) {
                agentCertBean = jsonArray.getObject(i, AgentCertBean.class);
                agentCertBeans.add(agentCertBean);
            }
        }
        return agentCertBeans;
    }

    /**
     * 更换证书
     * 分为本地节点和 非本地节点
     * 参数不相同，但是同一个接口
     *
     * @param obj 带传递的参数
     */
    public void updateAgentCert(JSONObject obj) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/certificates/", HttpMethod.PUT.vaLue(), "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseData = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            IDENotificationUtil.notificationCommon(new NotificationBean(AgentCertContent.AGENT_CERT_CHANGE_TITLE,
                    AgentCertContent.AGENT_CERT_CHANGE_FAILD, NotificationType.ERROR));
            return;
        } else {
            HashMap<String, String> codeTipsMap = AgentCertContent.getTipCodeMap();
            String rspCode = responseData.getCode();
            String title = AgentCertContent.AGENT_CERT_CHANGE_TITLE;
            String tips = codeTipsMap.get(rspCode) == null ?
                    AgentCertContent.AGENT_CERT_CHANGE_FAILD : codeTipsMap.get(rspCode);
            NotificationType noticeType = NotificationType.ERROR;

            // 返回成功，则修改提示类型为 INFORMATION
            if (AgentCertContent.RESPONSE_CODE.endsWith(rspCode)) {
                noticeType = NotificationType.INFORMATION;
            }
            IDENotificationUtil.notificationCommon(new NotificationBean(title, tips, noticeType));
        }
    }

    /**
     * 更换工作密钥
     * 分为本地节点和 非本地节点
     * 参数不相同，但是同一个接口
     *
     * @param obj 接口所需的参数
     */
    public void agentWorkKey(JSONObject obj) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "sys-perf/api/v2.2/work-keys/", HttpMethod.PUT.vaLue(), "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseData = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            TuningHttpsServer.handleCert();
            return;
        } else {
            String title = AgentCertContent.WORK_KRY_CHANGE_TITLE;
            if (responseData.getCode().equals(AgentCertContent.RESPONSE_CODE)) {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(title,
                                AgentCertContent.WORK_KRY_CHANGE_SUCCESS,
                                NotificationType.INFORMATION));
            } else {
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(title,
                                AgentCertContent.WORK_KRY_CHANGE_FAILD,
                                NotificationType.ERROR));
            }
        }
    }

    /**
     * 查询节点指纹
     * * ssh链接之前需要查询服务器的ssh指纹信息，提供给用户以确认是否是自己想要链接 的服务器。
     *
     * @param ip 需要查询的 ip
     * @return FingerprintsBean 返回指纹信息列表
     */
    public List<FingerprintsBean> fingerPrintList(String ip) {
        List<FingerprintsBean> fingerprintsBeans = new ArrayList<>();
        // 获取节点返回指纹
        String url = "/sys-perf/api/v2.2/nodes/None/finger-print/?node_ip=" + ip;
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.GET.vaLue(), "");
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
            String msg = responseBean.getMessage();
            IDENotificationUtil.notificationCommon(
                    new NotificationBean(NodeManagerContent.NODE_MANAGER_FINGERPRINTS, msg,
                            NotificationType.ERROR));
            return fingerprintsBeans;
        }
    }


}
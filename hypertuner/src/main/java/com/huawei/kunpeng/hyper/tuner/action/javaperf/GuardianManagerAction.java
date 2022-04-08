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

package com.huawei.kunpeng.hyper.tuner.action.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.GuardianManagerBean;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf.GuardianDeleteDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf.GuardianRestartDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianDeletePanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianRestartPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianTipsPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 目标环境管理Action
 *
 * @since 2021-07-10
 */
public class GuardianManagerAction extends IDEPanelBaseAction {
    /**
     * 目标环境列表URL
     */
    public static final String GUARDIANS_LIST_URL = "java-perf/api/guardians";

    /**
     * 查询节点信息列表
     *
     * @return 节点信息列表
     */
    public List<GuardianManagerBean> getGuardianList() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                GUARDIANS_LIST_URL, HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<GuardianManagerBean> respBean = new ArrayList<>();
        if (responseBean == null) {
            return respBean;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object guardianList = jsonMessage.get("members");
        if (guardianList instanceof JSONArray) {
            JSONArray nodeJson = (JSONArray) guardianList;
            GuardianManagerBean guardianManagerBean;
            for (int mun = 0; mun < nodeJson.size(); mun++) {
                guardianManagerBean = nodeJson.getObject(mun, GuardianManagerBean.class);
                getStatus(guardianManagerBean);
                respBean.add(guardianManagerBean);
            }
        }
        return respBean;
    }

    private void getStatus(GuardianManagerBean guardianManagerBean) {
        String guardianStatus = guardianManagerBean.getState();
        if ("CONNECTED".equals(guardianStatus)) {
            guardianManagerBean.setState(GuardianMangerConstant.TABLE_COL_CONNECTED);
        } else if ("DISCONNECTED".equals(guardianStatus)) {
            guardianManagerBean.setState(GuardianMangerConstant.TABLE_COL_DISCONNECTED);
        } else {
            guardianManagerBean.setState(GuardianMangerConstant.TABLE_COL_CREATEING);
        }
    }

    /**
     * 重启目标环境
     *
     * @param nodeId nodeId
     * @param nodeIp nodeIp
     * @param port   port
     * @param name   name
     */
    public void restartGuardian(String nodeId, String nodeIp, String port, String name) {
        GuardianRestartDialog dialog = new GuardianRestartDialog(GuardianMangerConstant.GUARDIAN_RESTART_TITLE,
                GuardianMangerConstant.GUARDIAN_RESTART_TITLE,
                new GuardianRestartPanel(nodeId, nodeIp, port, name, GuardianMangerConstant.GUARDIAN_RESTART_TITLE));
        dialog.displayPanel();
    }

    /**
     * 删除目标环境
     *
     * @param name   name
     * @param status status
     * @param id     id
     * @param user   user
     */
    public void deleteGuardian(String name, String status, String id, String user) {
        // 判断当前登录用户是否与添加目标环境用户相同，不相同则为管理员，在title后面添加（user）
        String title = UserInfoContext.getInstance().getUserName().equals(user) ?
                GuardianMangerConstant.TABLE_COL_DELETE + " " + name :
                GuardianMangerConstant.TABLE_COL_DELETE + " " + name + "(" + user + ")";
        String contentMsg = UserInfoContext.getInstance().getUserName().equals(user) ? name : name + "(" + user + ")";
        // 将titie添加到提示内容中
        String content = MessageFormat.format(TuningI18NServer.toLocale(
                "plugins_hyper_tuner_javaperf_guardianManage_delete_tip"), title);
        IDEBaseDialog dialog;
        IDEBasePanel panel;
        // 判断是否为在线目标环境，在线的目标环境，直接弹框删除
        if (status.equals(GuardianMangerConstant.TABLE_COL_CONNECTED)
                || status.equals(GuardianMangerConstant.TABLE_COL_STATUS_CONNECTED)) {
            // delete图标表warn类型
            panel = new GuardianTipsPanel(null, content, "delete");
            dialog = new GuardianDeleteDialog(title, id, panel, true, null);
        } else {
            dialog = new GuardianDeleteDialog(title, id, new GuardianDeletePanel(title), false, contentMsg);
        }
        dialog.displayPanel();
    }

    /**
     * 查询节点指纹
     * * ssh链接之前需要查询服务器的ssh指纹信息，提供给用户以确认是否是自己想要链接 的服务器。
     *
     * @param host     需要查询的 host
     * @param port     需要查询的 port
     * @param username 需要查询的 username
     * @return FingerprintsBean 返回指纹信息列表
     */
    public ResponseBean fetchFingerPrint(String host, String port, String username) {
        String requestUrl = "java-perf/api/tools/fetch-fingerprint";
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, requestUrl,
                HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("host", host);
        obj.put("port", port);
        obj.put("username", username);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        return responseBean;
    }
}
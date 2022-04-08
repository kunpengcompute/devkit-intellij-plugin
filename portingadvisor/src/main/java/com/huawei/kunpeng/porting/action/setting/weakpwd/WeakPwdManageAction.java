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

package com.huawei.kunpeng.porting.action.setting.weakpwd;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.bean.WeakPwdBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.dialog.weakpwd.PortingAddWeakPwdDialog;
import com.huawei.kunpeng.porting.ui.panel.weakpwdpanel.PortingAddWeakPwdPanel;
import com.huawei.kunpeng.porting.ui.panel.weakpwdpanel.WeakPwdSetPanel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 弱口令设置实现类
 *
 * @since 2012-10-12
 */
public class WeakPwdManageAction extends IDEPanelBaseAction {
    private static WeakPwdManageAction instance = new WeakPwdManageAction();

    public static WeakPwdManageAction getInstance() {
        return instance;
    }

    /**
     * WeakPwdSetPanel
     *
     * @param panel         面板
     * @param actionOperate actionOperate
     * @return result
     */
    public List<WeakPwdBean> selectWeakPwdList(WeakPwdSetPanel panel, ActionOperate actionOperate) {
        String token = PortingIDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.TOKEN.vaLue());

        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                "/weak-passwords/?keyword=&page=1&per-page=100",
                HttpMethod.GET.vaLue(), token));
        if (responseBean == null) {
            return new ArrayList<>();
        }
        return parseWeakPwdData(responseBean.getData());
    }

    /**
     * 弹出创建弱口令弹框
     *
     * @param panel         panel
     * @param actionOperate actionOperate
     */
    public void createWeakPwd(WeakPwdSetPanel panel, ActionOperate actionOperate) {
        Map<String, Object> params = new HashMap<>();
        params.put(panel.getPanelName(), panel);
        IDEBasePanel createWeakPwdPanel = new PortingAddWeakPwdPanel(null, params);
        PortingAddWeakPwdDialog dialog = new PortingAddWeakPwdDialog(
            PortingWeakPwdConstant.ADD_WEAK_PASSWORD, createWeakPwdPanel);
        dialog.setSize(570, 190);
        dialog.displayPanel();
    }

    /**
     * 解析弱口令列表
     *
     * @param data 服务端反馈的参数。
     * @return 弱口令列表
     */
    private ArrayList<WeakPwdBean> parseWeakPwdData(String data) {
        ArrayList<WeakPwdBean> weakPwdList = new ArrayList<>();
        if (data == null) {
            Logger.warn("the weakPassword Data returned null!");
            return weakPwdList;
        }

        // 解析弱口令列表
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
        if (jsonMessage.get("weak_passwords") instanceof JSONArray) {
            JSONArray weakPwdJson = (JSONArray) jsonMessage.get("weak_passwords");
            for (int i = 0; i < weakPwdJson.size(); i++) {
                if (weakPwdJson.get(i) instanceof JSONObject) {
                    JSONObject weakPwdMap = (JSONObject) weakPwdJson.get(i);
                    weakPwdList.add(new WeakPwdBean(weakPwdMap.get("id").toString(),
                        weakPwdMap.get("weak_password").toString()));
                }
            }
        }
        return weakPwdList;
    }
}
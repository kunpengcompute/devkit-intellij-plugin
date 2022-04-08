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
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.InternalCommCertBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 内部通信证书 Action
 *
 * @since 2021-07-07
 */
public class InternalCommCertAction extends IDEPanelBaseAction {
    private static InternalCommCertAction instance = new InternalCommCertAction();

    public static InternalCommCertAction getInstance() {
        return instance;
    }

    /**
     * 查询证书列表
     *
     * @return 证书列表
     */
    public List<InternalCommCertBean> getInternalCommCertList() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                "java-perf/api/tools/certificates", HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        List<InternalCommCertBean> internalCommCertBeans = new ArrayList<>();
        if (responseBean == null) {
            return internalCommCertBeans;
        }
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
        Object members = jsonMessage.get("members");
        if (!(members instanceof JSONArray)) {
            return internalCommCertBeans;
        }
        JSONArray membersJSONArr = (JSONArray) members;
        if (CollectionUtils.isNotEmpty(membersJSONArr)) {
            InternalCommCertBean internalCommCertBean;
            for (int i = 0; i < membersJSONArr.size(); i++) {
                internalCommCertBean = membersJSONArr.getObject(i, InternalCommCertBean.class);
                internalCommCertBeans.add(internalCommCertBean);
            }
        }
        return internalCommCertBeans;
    }
}
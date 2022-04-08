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
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONObject;

/**
 * 内部通信证书 Action
 *
 * @since 2021-07-07
 */
public class ReportThresholdAction extends IDEPanelBaseAction {
    private static ReportThresholdAction instance = new ReportThresholdAction();

    public static ReportThresholdAction getInstance() {
        return instance;
    }


    /**
     * 获取报告阈值数据
     *
     * @param url 获取报告阈值数据接口
     * @return getReportObj
     */
    public JSONObject getReportConfig(String url) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                url, HttpMethod.GET.vaLue(), "");
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        JSONObject getReportObj = new JSONObject();
        return responseBean == null ? getReportObj : JSONObject.parseObject(responseBean.getData());
    }

    /**
     * 修改采样分析报告阈值数据
     *
     * @param obj           入参
     * @param actionOperate 回调
     * @param url           修改报告阈值数据接口
     */
    public void changeReportConfig(String url, JSONObject obj, ActionOperate actionOperate) {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
                url, HttpMethod.POST.vaLue(), "");
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            Logger.error("responseBean is null");
            return;
        }
        actionOperate.actionOperate(responseBean);
    }

}

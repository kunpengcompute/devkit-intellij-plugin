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

package com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningWeakPwdConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.weakpwdpanel.TuningAddWeakPwdPanel;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加弱口令事件处理器
 *
 * @since 2020-10-13
 */
public class TuningAddWeakPwdAction extends IDEPanelBaseAction {
    /**
     * 创建弱口令
     *
     * @param weakPassword    输入的弱口令
     * @param addWeakPwdPanel addWeakPwdPanel
     * @return boolean
     */
    public boolean createWeakPwd(String weakPassword, TuningAddWeakPwdPanel addWeakPwdPanel) {
        Logger.info("create WeakPwd.");
        // 开始创建新弱口令
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/weak-passwords/",
                        HttpMethod.POST.vaLue(),
                        "");
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("weak_password", weakPassword);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(message);
        Logger.info("responseBean", responseBean);
        switch (responseBean.getCode()) {
            case "UserManage.Success":
                responseBean.setInfo(TuningWeakPwdConstant.ADDWEAKPWD_SUCCESS);
                responseBean.setInfochinese(TuningWeakPwdConstant.ADDWEAKPWD_SUCCESS);
                responseBean.setStatus("0");
                break;
            default:
                responseBean.setInfo(responseBean.getMessage());
                responseBean.setInfochinese(responseBean.getMessage());
                responseBean.setStatus("1");
                break;
        }
        IDENotificationUtil.notifyCommonForResponse(
                TuningWeakPwdConstant.ADD_WEAK_PASSWORD, responseBean.getStatus(), responseBean);
        return TuningIDEConstant.SUCCESS_CODE.equals(responseBean.getCode());
    }
}

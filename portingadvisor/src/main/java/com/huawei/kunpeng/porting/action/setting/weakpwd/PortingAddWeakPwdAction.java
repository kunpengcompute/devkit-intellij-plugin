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

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.panel.weakpwdpanel.PortingAddWeakPwdPanel;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加弱口令事件处理器
 *
 * @since 2020-10-13
 */
public class PortingAddWeakPwdAction extends IDEPanelBaseAction {
    /**
     * 创建弱口令
     *
     * @param weakPassword    输入的弱口令
     * @param addWeakPwdPanel addWeakPwdPanel
     * @return boolean
     */
    public boolean createWeakPwd(String weakPassword, PortingAddWeakPwdPanel addWeakPwdPanel) {
        Logger.info("create WeakPwd.");
        // 开始创建新弱口令
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/weak-passwords/",
            HttpMethod.POST.vaLue(), "");
        Map<String, String> obj = new HashMap<>();
        obj.put("weak_password", weakPassword);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return false;
        }
        IDENotificationUtil.notifyCommonForResponse(PortingWeakPwdConstant.ADD_WEAK_PASSWORD, responseBean.getStatus(),
            responseBean);

        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }
}

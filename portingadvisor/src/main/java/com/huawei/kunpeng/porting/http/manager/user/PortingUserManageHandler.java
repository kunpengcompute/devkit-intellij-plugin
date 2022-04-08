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

package com.huawei.kunpeng.porting.http.manager.user;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.alibaba.fastjson.JSONObject;

/**
 * The class: PortingUserManageHandler
 *
 * @since 2021-08-30
 */
public class PortingUserManageHandler {
    /**
     * 登录
     *
     * @param userName userName
     * @param password password
     * @return ResponseBean
     */
    public static ResponseBean doLoginRequest(char[] userName, char[] password) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/login/",
            HttpMethod.POST.vaLue(), false);
        JSONObject obj = new JSONObject();
        obj.put("username", new String(userName));
        obj.put("password", new String(password));
        message.setBodyData(obj.toJSONString());
        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 设置密码
     *
     * @param pwd        pwd
     * @param confirmPwd confirmPwd
     * @return ResponseBean
     */
    public static ResponseBean doSetAdminPwdRequest(char[] pwd, char[] confirmPwd) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            "/users/admin/configuration/", HttpMethod.POST.vaLue(), false);
        JSONObject obj = new JSONObject();
        obj.put("new_password", new String(pwd));
        obj.put("confirm_password", new String(confirmPwd));
        message.setBodyData(obj.toJSONString());
        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 获取工具当前版本
     *
     * @return ResponseBean
     */
    public static ResponseBean doGetToolVersionRequest() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/tools/version/",
            HttpMethod.GET.vaLue(), "");
        return PortingHttpsServer.INSTANCE.requestData(message);
    }
}

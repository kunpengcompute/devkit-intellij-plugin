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

package com.huawei.kunpeng.hyper.tuner.http.manager.user;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;

import com.alibaba.fastjson.JSONObject;

/**
 * The class: TuningUserManagerHandler
 *
 * @since 2021年08月30日
 */
public class TuningUserManagerHandler {
    /**
     * 登录
     *
     * @param userName userName
     * @param password password
     * @return ResponseBean
     */
    public static ResponseBean doLoginRequest(char[] userName, char[] password) {
        RequestDataBean message =
            new RequestDataBean(
                TuningIDEConstant.TOOL_NAME_TUNING,
                "user-management/api/v2.2/users/session/",
                HttpMethod.POST.vaLue(),
                false);
        JSONObject obj = new JSONObject();
        obj.put("username", new String(userName));
        obj.put("password", new String(password));
        message.setBodyData(obj.toJSONString());
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 获取后端版本
     *
     * @return ResponseBean
     */
    public static ResponseBean doGetToolVersionRequest() {
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING,
            "user-management/api/v2.2/users/version/",
            HttpMethod.GET.vaLue(),
            "");
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 管理设置密码
     *
     * @param pwd        pwd
     * @param confirmPwd confirmPwd
     * @return ResponseBean
     */
    public static ResponseBean doSetAdminPwdRequest(char[] pwd, char[] confirmPwd) {
        RequestDataBean message =
            new RequestDataBean(
                TuningIDEConstant.TOOL_NAME_TUNING,
                "user-management/api/v2.2/users/admin-password/",
                HttpMethod.POST.vaLue(),
                false);
        JSONObject obj = new JSONObject();
        obj.put("username", "tunadmin");
        obj.put("password", new String(pwd));
        obj.put("confirm_password", new String(confirmPwd));
        message.setBodyData(obj.toJSONString());
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 磁盘监控
     *
     * @return ResponseBean
     */
    public static ResponseBean getDiscAlarm() {
        String url = "sys-perf/api/v2.2/projects/1/alarm/?auto-flag=on&date=" + System.currentTimeMillis();
        RequestDataBean message =
            new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.GET.vaLue(), "");
        return TuningHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 免责声明检查
     *
     * @return ResponseBean
     */
    public static ResponseBean checkDisclaimer() {
        String url = "user-management/api/v2.2/users/user-extend/";
        RequestDataBean message =
            new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.GET.vaLue(), "");
        return TuningHttpsServer.INSTANCE.requestData(message);
    }
}

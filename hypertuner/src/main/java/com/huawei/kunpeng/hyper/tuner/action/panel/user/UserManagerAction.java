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

package com.huawei.kunpeng.hyper.tuner.action.panel.user;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.bean.UserBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Map;

/**
 * 用户管理控制类
 *
 * @since 2020-10-6
 */
public class UserManagerAction extends IDEPanelBaseAction {
    /**
     * 用户管理请求URL
     */
    public static final String USER_ULR_PART = "user-management/api/v2.2/users/";

    /**
     * 文件夹分割符。
     */
    public static final String FOLDER_SEPARATOR = "/";

    /**
     * 用户数据列表key
     */
    private static final String USER_KEY = "users";

    /**
     * 登录
     *
     * @param map 面板
     * @return 用户列表
     */
    public ArrayList<UserBean> selectUserList(Map<String, String> map) {
        Logger.info("admin user select users begin!");
        ResponseBean responseBean = sendRequestToWEB(map, USER_ULR_PART, HttpMethod.GET.vaLue());
        Logger.info("admin user select users end!");
        return parseUserData(responseBean.getData());
    }

    /**
     * 发送用户请求数据给Web端
     *
     * @param map    请求参数
     * @param url    请求url
     * @param method 请求method
     * @return 返回响应消息
     */
    public ResponseBean sendRequestToWEB(Map<String, String> map, String url, String method) {
        if (map == null) { // map为空不处理
            Logger.warn("request data info is null");
            return new ResponseBean();
        }

        String token = null;
        Object tokenObj =
                IDEContext.getValueFromGlobalContext(TuningIDEConstant.TOOL_NAME_TUNING, BaseCacheVal.TOKEN.vaLue());
        if (tokenObj instanceof String) {
            token = (String) tokenObj;
        }
        RequestDataBean message = new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, method, token);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));
        ResponseBean res = TuningHttpsServer.INSTANCE.requestData(message);
        return res;
    }

    /**
     * 解析用户列表
     *
     * @param data 服务端反馈的参数。
     * @return 用户列表
     */
    private ArrayList<UserBean> parseUserData(String data) {
        ArrayList<UserBean> userBeanList = new ArrayList();
        if (ValidateUtils.isEmptyString(data)) {
            Logger.warn("the server returned data is null!!");
            return userBeanList;
        }

        // 解析用户列表
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
        Object jsonArrObj = jsonMessage.get(USER_KEY);
        if (jsonArrObj instanceof JSONArray) {
            JSONArray usersJson = (JSONArray) jsonArrObj;
            UserBean tempUserBean;
            for (int i = 0; i < usersJson.size(); i++) {
                tempUserBean = usersJson.getObject(i, UserBean.class);
                userBeanList.add(tempUserBean);
            }
        }
        return userBeanList;
    }
}

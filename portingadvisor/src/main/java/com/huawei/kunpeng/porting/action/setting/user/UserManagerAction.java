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

package com.huawei.kunpeng.porting.action.setting.user;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.bean.UserBean;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.alibaba.fastjson.JSONArray;
import com.intellij.notification.NotificationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户管理控制类
 *
 * @since 2020-10-6
 */
public class UserManagerAction extends IDEPanelBaseAction {
    /**
     * 用户管理请求URL
     */
    public static final String USER_ULR_PART = "/users/";

    /**
     * 文件夹分割符。
     */
    public static final String FOLDER_SEPARATOR = "/";

    /**
     * 用户数据列表key
     */
    private static final String USER_KEY = "users";

    /**
     * 工作空间请求URL
     */
    private static final String CUSTOMIZE = "/customize/";

    /**
     * 工作空间请求URL
     */
    private static final String CUSTOMIZE_PATH = "customize_path";

    /**
     * 查询工作空间路径
     *
     * @return 查询结果。
     */
    public String selectWorkSpaceForWeb() {
        Logger.info("admin user select WorkSpace begin!");
        ResponseBean responseBean = sendRequestToWEB(new HashMap<>(), CUSTOMIZE, HttpMethod.GET.vaLue());
        if (responseBean != null) {
            Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            Object jsonArrObj = jsonMessage.get(CUSTOMIZE_PATH);
            if (jsonArrObj != null) {
                Logger.info("admin user select WorkSpace OK!");
                return jsonArrObj.toString();
            }
            Logger.info("admin user select WorkSpace no OK!");
        }
        return "";
    }

    /**
     * 查询当前用户是否签署了免责声明
     *
     * @return 是否签署
     */
    public static boolean selectIsSignDisclaimer() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/disclaimercounts/",
            HttpMethod.GET.vaLue(), true);
        ResponseBean rsp = PortingHttpsServer.INSTANCE.requestData(message);
        if (rsp == null) {
            Logger.info("selectIsSignDisclaimer is  error!");
            return false;
        }
        // 解析用户列表
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        String disclDeadCounts = jsonMessage.get("disclreadcounts").toString();
        if (Objects.equals(disclDeadCounts, "0")) {
            Logger.info("the user has not sign disclaimer!");
            return false;
        }
        return true;
    }

    /**
     * 设置免责声明签署情况。
     */
    public static void setSignDisclaimer() {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/users/firstdisclaimer/",
            HttpMethod.POST.vaLue(), true);
        ResponseBean rsp = PortingHttpsServer.INSTANCE.requestData(message);
        Logger.info("the user has sign disclaimer!");
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", CommonUtil.getRspTipInfo(rsp), NotificationType.INFORMATION));
    }

    /**
     * 登录
     *
     * @param map 面板
     * @return 用户列表
     */
    public ArrayList<UserBean> selectUserList(Map<String, String> map) {
        Logger.info("admin user select users begin!");
        ResponseBean responseBean = sendRequestToWEB(map, USER_ULR_PART, HttpMethod.GET.vaLue());
        if (responseBean == null) {
            return new ArrayList<>();
        }
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
        if (map == null) {
            // map为空不处理
            Logger.warn("request data info is null");
            return new ResponseBean();
        }

        String token = null;
        Object tokenObj = PortingIDEContext.getValueFromGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
            BaseCacheVal.TOKEN.vaLue());
        if (tokenObj instanceof String) {
            token = (String) tokenObj;
        }
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            method, token);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));

        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 解析用户列表
     *
     * @param data 服务端反馈的参数。
     * @return 用户列表
     */
    private ArrayList<UserBean> parseUserData(String data) {
        ArrayList<UserBean> userList = new ArrayList<>();
        if (ValidateUtils.isEmptyString(data)) {
            Logger.warn("the server returned data is null!!");
            return userList;
        }

        // 解析用户列表
        Map<String, Object> jsonMessage = JsonUtil.getJsonObjFromJsonStr(data);
        Object jsonArrObj = jsonMessage.get(USER_KEY);
        if (jsonArrObj instanceof JSONArray) {
            JSONArray usersJson = (JSONArray) jsonArrObj;
            UserBean tempUserBean;
            for (int i = 0; i < usersJson.size(); i++) {
                tempUserBean = usersJson.getObject(i, UserBean.class);
                userList.add(tempUserBean);
            }
        }
        return userList;
    }
}

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

package com.huawei.kunpeng.intellij.common;

import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.bean.WebSessionBean;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import lombok.Data;

import java.util.Map;

/**
 * 用户信息
 *
 * @since 2020-09-25
 */
@Data
public class UserInfoContext {
    /**
     * 常量实例
     */
    protected static UserInfoContext instance = new UserInfoContext();

    /**
     * 用户是否已签署免责声明
     */
    public boolean isSignDisclaimer;

    /**
     * 用戶角色
     */
    protected String role;

    /**
     * 用戶名称
     */
    protected String userName;

    /**
     * 用戶id
     */
    protected String loginId;

    /**
     * 用戶工作控件
     */
    protected String workspace;

    /**
     * 是否记住密码
     */
    protected boolean isSavePassword;

    /**
     * 是否自动登录
     */
    protected boolean isAutoLogin;

    /**
     * 用户自定义安装路径
     */
    protected String customPath;

    /**
     * 获取当前的用户上下文实例
     *
     * @return UserInfoContext
     */
    public static UserInfoContext getInstance() {
        return instance;
    }

    /**
     * 返回用户是否勾选记住密码
     *
     * @return IsSavePassword 是否记住密码
     */
    public boolean isIsSavePassword() {
        return isSavePassword;
    }

    /**
     * 返回用户是否勾选记住密码
     *
     * @return IsSavePassword 是否记住密码
     */
    public boolean isIsAutoLogin() {
        return isAutoLogin;
    }

    public boolean isSignDisclaimer() {
        return isSignDisclaimer;
    }

    /**
     * 清除用户信息
     */
    public void clearUserInfo() {
        setRole(null);
        setLoginId(null);
        setUserName(null);
        setWorkspace(null);
    }

    /**
     * 设置用户信息
     *
     * @param rsp 用户信息
     */
    public void setUserInfo(ResponseBean rsp) {
        Map<String, String> map = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        setRole(map.get("role"));
        setLoginId(String.valueOf(map.get("id")));
        setUserName(map.get("username"));
    }

    /**
     * 返回默认的 WebviewSession
     *
     * @return WebSessionBean
     */
    public WebSessionBean getDefaultWebSessionBean() {
        Integer loginIdInteger = null;
        if (!StringUtil.stringIsEmpty(getLoginId())) {
            loginIdInteger = Integer.valueOf(getLoginId());
        }
        return new WebSessionBean(getRole(), getUserName(), loginIdInteger, null, I18NServer.getCurrentLanguage(),
                false, "");
    }
}

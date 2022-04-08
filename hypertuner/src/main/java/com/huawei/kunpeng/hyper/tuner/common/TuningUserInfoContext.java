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

package com.huawei.kunpeng.hyper.tuner.common;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.bean.WebSessionBean;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 用户信息
 *
 * @since 2020-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TuningUserInfoContext extends UserInfoContext {
    /**
     * 获取当前的用户上下文实例
     *
     * @return UserInfoContext
     */
    public static TuningUserInfoContext getInstance() {
        return new TuningUserInfoContext();
    }

    /**
     * 设置用户信息
     *
     * @param rsp 用户信息
     */
    public static void putUserInfo(ResponseBean rsp) {
        Map<String, String> map = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        // 设置 是否签署免责声明
        instance.setRole(map.get("role"));
        instance.setLoginId(String.valueOf(map.get("id")));
        instance.setUserName(map.get("username"));
    }

    /**
     * 返回默认的 WebviewSession
     *
     * @return WebSessionBean
     */
    @Override
    public WebSessionBean getDefaultWebSessionBean() {
        Integer loginIdInteger = null;
        if (!StringUtil.stringIsEmpty(getLoginId())) {
            loginIdInteger = Integer.valueOf(UserInfoContext.instance.getLoginId());
        }
        return new WebSessionBean(UserInfoContext.instance.getRole(), UserInfoContext.instance.getUserName(),
            loginIdInteger, null, I18NServer.getCurrentLanguage(),
                false, "hypertuner");
    }
}

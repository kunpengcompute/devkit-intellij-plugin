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

package com.huawei.kunpeng.porting.common;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.process.PortingIDETask;
import com.huawei.kunpeng.porting.webview.pageeditor.AnalysisEditor;

import java.util.HashSet;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息
 *
 * @since 2020-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PortingUserInfoContext extends UserInfoContext {
    /**
     * 源码迁移当前用户当前会话是否需要检测gcc版本标志(存在需要检测)
     */
    public static HashSet<String> sourceCodeCheckGcc = new HashSet<>();

    /**
     * 清除用户信息
     */
    public static void clearStatus() {
        PortingIDETask.deleteAllRunTaskByUser();
        // 用户切换软件包重构标志
        instance.clearUserInfo();
        AnalysisEditor.clearStatus();
    }

    /**
     * 设置用户信息
     *
     * @param rsp 用户信息
     */
    public static void putUserInfo(ResponseBean rsp) {
        Map<String, String> map = JsonUtil.getJsonObjFromJsonStr(rsp.getData());
        // 设置 是否签署免责声明
        instance.setSignDisclaimer(UserManagerAction.selectIsSignDisclaimer());
        instance.setRole(map.get("role"));
        instance.setLoginId(String.valueOf(map.get("id")));
        instance.setUserName(map.get("username"));
        instance.setWorkspace(map.get("workspace"));
        // 登录默认需要检测源码迁移gcc版本
        sourceCodeCheckGcc.add(instance.getUserName());
    }
}

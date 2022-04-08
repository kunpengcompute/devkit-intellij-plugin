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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.panel.LoginPanel;

import com.intellij.openapi.wm.ToolWindow;

import java.util.Map;

/**
 * 登录面板
 *
 * @since 2020-09-25
 */
public class TuningLoginPanel extends LoginPanel {
    /**
     * 首次登录接口返回值
     */
    private static final boolean IS_FIRST_LOGIN = true;

    public TuningLoginPanel(ToolWindow toolWindow, boolean isLockable) {
        super.createLoginPanel(toolWindow, isLockable);
    }

    public TuningLoginPanel(ToolWindow toolWindow) {
        this(toolWindow, false);
    }

    @Override
    public void checkAdminFirstLogin() {
        RequestDataBean message =
                new RequestDataBean(
                        TuningIDEConstant.TOOL_NAME_TUNING,
                        "user-management/api/v2.2/users/admin-status/",
                        HttpMethod.GET.vaLue(),
                        false);
        ResponseBean obj = TuningHttpsServer.INSTANCE.requestData(message);
        if (obj == null) {
            return;
        }
        Map map = JsonUtil.getJsonObjFromJsonStr(obj.getData());

        // 操作员首次登录，需要设置账号密码
        Boolean isFirstLogin = JsonUtil.getValueIgnoreCaseFromMap(map, "is_firstlogin", Boolean.class);
        if (isFirstLogin != null && isFirstLogin == IS_FIRST_LOGIN) {
            this.isAdminFirstLogin = true;
        }
    }

    @Override
    protected String userName() {
        return "tunadmin";
    }

    @Override
    protected String displayName() {
        return TuningI18NServer.toLocale("plugins_hyper_tuner_login_title");
    }
}

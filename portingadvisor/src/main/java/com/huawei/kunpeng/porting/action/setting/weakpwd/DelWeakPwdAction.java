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

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 删除弱口令事件处理器
 *
 * @since 2020-10-13
 */
public class DelWeakPwdAction extends IDEPanelBaseAction {
    /**
     * 删除弱口令
     *
     * @param delRow        需要删除的数据行
     * @param actionOperate actionOperate
     * @return boolean
     */
    public boolean deleteWeakPwd(String delRow, ActionOperate actionOperate) {
        Logger.info("delete WeakPwd.");

        // 开始删除数据
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, "/weak-passwords/",
            HttpMethod.DELETE.vaLue(), "");
        Map<String, String> obj = new HashMap<>();
        obj.put("weak_password_id", delRow);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return false;
        }
        IDENotificationUtil.notifyCommonForResponse(PortingWeakPwdConstant.WEAK_PASSWORD_DEL_TITLE,
            responseBean.getStatus(), responseBean);
        return RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
    }
}

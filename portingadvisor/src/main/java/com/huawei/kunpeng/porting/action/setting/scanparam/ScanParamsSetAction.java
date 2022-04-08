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

package com.huawei.kunpeng.porting.action.setting.scanparam;

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.panel.ScanParameterPanel;

import com.intellij.notification.NotificationType;

import java.util.Locale;
import java.util.Map;

/**
 * ScanParamsSetAction
 *
 * @since 2010-10-13
 */
public class ScanParamsSetAction extends IDEPanelBaseAction {
    private static final String OK_STATUS_CODE = "0";

    /**
     * 获取当前参数值
     *
     * @return boolean
     */
    public ResponseBean queryCurScanParam() {
        String requestUrl = String.format(Locale.ROOT, "/users/%s/config/",
            PortingUserInfoContext.getInstance().getLoginId());
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(
            new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl, HttpMethod.GET.vaLue(), ""));
        return responseBean;
    }

    /**
     * 更改扫描参数
     *
     * @param panel panel
     * @param usrID usrID
     * @param body  body
     * @return 更改是否成功。
     */
    public boolean setScanParam(ScanParameterPanel panel, String usrID, Map body) {
        String requestUrl = String.format(Locale.ROOT, "/users/%s/config/", usrID);
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, requestUrl,
            HttpMethod.POST.vaLue(), "");

        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(body));
        ResponseBean responseData = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseData == null) {
            return false;
        }
        return parseSetScanParamsResp(panel, responseData);
    }

    private boolean parseSetScanParamsResp(ScanParameterPanel panel, ResponseBean responseData) {
        String data = responseData.getStatus();
        if (!ValidateUtils.isEmptyString(data)) {
            if (data.equals(OK_STATUS_CODE)) {
                IDENotificationUtil.notificationCommon(
                    new NotificationBean("", CommonUtil.getRspTipInfo(responseData), NotificationType.INFORMATION));
                Logger.info("Change scan parameter successfully.");
                return true;
            } else {
                IDENotificationUtil.notificationCommon(
                    new NotificationBean("", CommonUtil.getRspTipInfo(responseData), NotificationType.ERROR));
                Logger.info("Failed to change Scan Parameter.");
            }
        }
        return false;
    }
}

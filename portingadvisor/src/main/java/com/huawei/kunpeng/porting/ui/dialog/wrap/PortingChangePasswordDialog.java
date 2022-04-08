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

package com.huawei.kunpeng.porting.ui.dialog.wrap;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.RespondStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.ChangePasswordDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UILoginUtils;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.ui.panel.PortingLoginPanel;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

/**
 * 修改密码的弹窗
 *
 * @since 2021-04-10
 */
public class PortingChangePasswordDialog extends ChangePasswordDialog {
    public PortingChangePasswordDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected void customizeChangePwdOKAction() {
        LoginUtils.refreshLogin();
        if (changePasswdStatus.equals(RespondStatus.PROCESS_STATUS_NORMAL.value())) {
            IDEBasePanel loginPanel = new PortingLoginPanel(null);
            IDEBaseDialog loginWrapDialog = new PortingLoginWrapDialog(loginPanel);
            loginWrapDialog.displayPanel();
        }
        changePasswdStatus = RespondStatus.PROCESS_STATUS_ERROR.value();
        Logger.info("change password onOKAction");
        mainPanel.clearPwd();
    }

    @Override
    protected boolean resetUserPwd(String pwd, String newPwd, String confirmPwd) {
        // 普通用户首次登录设置密码
        String url = "/users/" + PortingUserInfoContext.getInstance().getLoginId() + "/resetpassword/";
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.POST.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("old_password", pwd);
        obj.put("new_password", newPwd);
        obj.put("confirm_password", confirmPwd);
        message.setBodyData(obj.toJSONString());
        ResponseBean rsp = PortingHttpsServer.INSTANCE.requestData(message);
        if (rsp == null) {
            return false;
        }

        // 修改密码成功
        if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(rsp.getStatus())) {
            changePasswdStatus = RespondStatus.PROCESS_STATUS_NORMAL.value();
            // 如果当前用户记住密码，需要更新秘钥
            if (isNeedUpdateKey()) {
                UILoginUtils.encrypt(newPwd);
            }
            IDENotificationUtil.notificationCommon(
                new NotificationBean("", CommonUtil.getRspTipInfo(rsp), NotificationType.INFORMATION));
            return true;
        }
        // 修改密码失败
        IDENotificationUtil.notificationCommon(
            new NotificationBean("", CommonUtil.getRspTipInfo(rsp), NotificationType.ERROR));
        return false;
    }

    private boolean isNeedUpdateKey() {
        return ConfigUtils.isContainsCurUser(PortingUserInfoContext.getInstance().getUserName()) &&
            ConfigUtils.isSavePassword(PortingUserInfoContext.getInstance().getUserName()) && !isAdmin();
    }

    private boolean isAdmin() {
        return ValidateUtils.equals(UserInfoContext.getInstance().getRole(), "Admin");
    }
}

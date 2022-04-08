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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.TuningLoginPanel;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.RespondStatus;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ConfigUtils;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.wrap.ChangePasswordDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.intellij.ui.utils.UILoginUtils;

import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;

/**
 * 修改密码的弹窗
 *
 * @since 2020-11-13
 */
public class TuningChangePasswordDialog extends ChangePasswordDialog {
    /**
     * 请求成功返回
     */
    private static final String SUCCESS_CODE = "UserManage.Success";

    private static final String CHANGE_PWD_TITLE =
            TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_change_password");

    public TuningChangePasswordDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    @Override
    protected void customizeChangePwdOKAction() {
        TuningLoginUtils.refreshLogin();
        if (changePasswdStatus.equals(
                com.huawei.kunpeng.intellij.common.enums.RespondStatus.PROCESS_STATUS_NORMAL.value())) {
            IDEBasePanel loginPanel = new TuningLoginPanel(null);
            IDEBaseDialog loginWrapDialog = new TuningLoginWrapDialog(loginPanel);
            loginWrapDialog.displayPanel();
        }
        changePasswdStatus = com.huawei.kunpeng.intellij.common.enums.RespondStatus.PROCESS_STATUS_ERROR.value();
        Logger.info("change password onOKAction");
        mainPanel.clearPwd();
    }

    @Override
    protected boolean resetUserPwd(String pwd, String newPwd, String confirmPwd) {
        String url = "user-management/api/v2.2/users/" + UserInfoContext.getInstance().getLoginId() + "/password/";
        RequestDataBean message =
                new RequestDataBean(TuningIDEConstant.TOOL_NAME_TUNING, url, HttpMethod.PUT.vaLue(), "");
        JSONObject obj = new JSONObject();
        obj.put("old_password", pwd);
        obj.put("new_password", newPwd);
        obj.put("confirm_password", confirmPwd);
        message.setBodyData(obj.toJSONString());
        ResponseBean rsp = TuningHttpsServer.INSTANCE.requestData(message);
        if (rsp == null) {
            return false;
        }
        // 修改密码成功
        if (SUCCESS_CODE.equals(rsp.getCode())) {
            changePasswdStatus = RespondStatus.PROCESS_STATUS_NORMAL.value();
            // 如果当前用户记住密码，需要更新秘钥
            if (isNeedUpdateKey()) {
                UILoginUtils.encrypt(newPwd);
            }
            return true;
        }
        // 修改密码失败
        IDENotificationUtil.notificationCommon(
                new NotificationBean(CHANGE_PWD_TITLE, rsp.getMessage(), NotificationType.ERROR));
        return false;
    }

    private boolean isNeedUpdateKey() {
        return ConfigUtils.isContainsCurUser(UserInfoContext.getInstance().getUserName())
                && ConfigUtils.isSavePassword(UserInfoContext.getInstance().getUserName())
                && !isAdmin();
    }

    private boolean isAdmin() {
        return ValidateUtils.equals(UserInfoContext.getInstance().getRole(), "Admin");
    }
}

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

package com.huawei.kunpeng.porting.ui.panel.userpanel;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.user.UserManagerAction;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.utils.LoginUtils;

import com.intellij.notification.NotificationType;

import java.util.HashMap;

import javax.swing.JPanel;

/**
 * 管理员用户管理界面
 *
 * @since 2020-10-10
 */
public abstract class UserDialogAbstractPanel extends IDEBasePanel implements ActionOperate {
    /**
     * 成功状态0，
     */
    public static final String STATUS_SUCCEEDED = "0";

    /**
     * 参数异常错误码
     */
    public static final String PARAM_ERROR = "-1";

    /**
     * 登出界面
     */
    public static final String LOGIN_OUT = "/users/logout/";

    /**
     * 重置密码接口
     */
    protected static final String RESET_PRAMS = "resetpassword";

    /**
     * 管理员密码
     */
    protected static final String CREATE_PRAMS_ADMIN = "admin_password";

    /**
     * 密码
     */
    protected static final String CREATE_PRAMS_PD = "password";

    /**
     * 确认密码
     */
    protected static final String CREATE_PRAMS_CONFIRM = "confirm_password";

    /**
     * 角色
     */
    protected static final String ROLE = "role";

    /**
     * 用户名
     */
    protected static final String USER_NAME = "username";

    /**
     * 工作空间
     */
    protected static final String WORK_SPACE = "workspace";

    /**
     * 用户
     */
    protected static final String USER = "User";

    /**
     * 旧密码
     */
    protected static final String CREATE_PRAMS_OLD = "old_password";

    /**
     * 新密码
     */
    protected static final String CREATE_PRAMS_NEW = "new_password";

    /**
     * 主面板
     */
    protected JPanel mainPanel;

    /**
     * 登出
     */
    public void logOut() {
        if (action instanceof UserManagerAction) {
            // 1、发送登出请求 需要清掉状态。
            ResponseBean rsp = ((UserManagerAction) action).sendRequestToWEB(new HashMap<String, String>(), LOGIN_OUT,
                HttpMethod.POST.vaLue());
            if (ValidateUtils.equals(rsp.getStatus(), STATUS_SUCCEEDED)) {
                // 2 清除所有状态
                PortingUserInfoContext.clearStatus();

                // 3、跳转到登录界面。关闭用户管理界面。
                LoginUtils.gotoLogin();
            }
        }
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new UserManagerAction();
        }
    }

    /**
     * 设置组件事件
     *
     * @param action action
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof UserManagerAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 统一处理弹框。
     *
     * @param result      result
     * @param dialogTitle dialogTitle
     * @return 返回结果。
     */
    protected boolean handlerResult(ResponseBean result, String dialogTitle) {
        String resultStr;
        if (result == null) {
            return false;
        }
        resultStr = CommonUtil.getRspTipInfo(result);

        boolean isSucceeded;
        NotificationType messageType = NotificationType.INFORMATION;
        if (!STATUS_SUCCEEDED.equals(result.getStatus())) {
            messageType = NotificationType.ERROR;
            isSucceeded = false;
        } else {
            isSucceeded = true;
        }

        // 消息对话框无返回, 仅做通知作用
        NotificationBean notificationBean = new NotificationBean(dialogTitle, resultStr, messageType);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
        return isSucceeded;
    }
}

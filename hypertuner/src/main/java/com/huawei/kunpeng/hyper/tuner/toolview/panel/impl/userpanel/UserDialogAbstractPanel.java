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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel;

import com.huawei.kunpeng.hyper.tuner.action.panel.user.UserManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.utils.TuningLoginUtils;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.Language;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

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
     * 用户名正则表达式
     */
    public static final String USER_NAME_REG_EXP = "^[a-zA-Z][a-zA-Z0-9_-]{5,31}$";

    /**
     * 参数异常错误码
     */
    public static final String PARAM_ERROR = "-1";

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

    private static final String UPDATE_SUCESS = I18NServer.toLocale("plugins_hyper_tuner_common_sucess");

    private static final String UPDATE_FAILD = I18NServer.toLocale("plugins_hyper_tuner_common_faild");

    /**
     * 国际化: 密码为弱口令，请重新设置
     */
    private static final String NOTICE_TITLE_PWD_WEAK = I18NServer.toLocale("plugins_hyper_tuner_login_pwd_weaktype");

    /**
     * 国际化：密码错误，请重新输入
     */
    private static final String NOTICE_TITLE_PWD_ERR = I18NServer.toLocale("plugins_hyper_tuner_login_pwd_error_admin");

    /**
     * 国际化：修改用户密码失败，新旧密码一致
     */
    private static final String CHANGE_PWD_EQUAL = I18NServer.toLocale("plugins_common_term_title_change_equal");

    /**
     * 国际化：创建目标已存在
     */
    private static final String NOTICE_TITLE_NAME_EXIT = I18NServer.toLocale("rescode_409_message");

    /**
     * 成功状态0，
     */
    private static final String STATUS_SUCCEEDED = "UserManage.Success";

    /**
     * 密 码错误
     */
    private static final String STATUS_FAILED_ERR = "UserManage.Common.PwdErr";

    /**
     * 弱类型密 码
     */
    private static final String STATUS_FAILED_WEAK_TYPE = "UserManage.WeakPassword.Post.WeakTypePwd";

    /**
     * 返回 新旧密 码一致
     */
    private static final String NO_CHANGE = "UserManage.Pwd.Put.PwdNoChange";

    /**
     * 国际化：创建对象已存在
     */
    private static final String STATUS_FAILED_NAME_EXIT = "UserManage.User.Post.NameExist";

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
            ResponseBean rsp =
                    ((UserManagerAction) action)
                            .sendRequestToWEB(
                                    new HashMap<>(),
                                    "user-management/api/v2.2/users/session/"
                                            + UserInfoContext.getInstance().getLoginId()
                                            + "/",
                                    HttpMethod.DELETE.vaLue());
            if (ValidateUtils.equals(rsp.getCode(), STATUS_SUCCEEDED)) {
                // 2 清除所有状态
                UserInfoContext.getInstance().clearUserInfo();

                // 3、跳转到登录界面。关闭用户管理界面。
                TuningLoginUtils.gotoLogin();
            }
        }
    }

    /**
     * 设置组件事件
     *
     * @param baseAction action
     */
    @Override
    protected void setAction(IDEPanelBaseAction baseAction) {
        if (baseAction instanceof UserManagerAction) {
            this.action = baseAction;
            registerComponentAction();
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
     * 统一处理弹框。
     *
     * @param result      result
     * @param dialogTitle dialogTitle
     * @return 返回结果。
     */
    protected boolean handlerResult(ResponseBean result, String dialogTitle) {
        String resultStr = "";
        if (result == null) {
            return false;
        }
        boolean isSucceeded;
        NotificationType messageType = NotificationType.INFORMATION;
        String rspCode = result.getCode();
        if (STATUS_SUCCEEDED.equals(rspCode)) {
            resultStr = UPDATE_SUCESS;
            isSucceeded = true;
        } else if (STATUS_FAILED_ERR.equals(rspCode)) {
            resultStr = NOTICE_TITLE_PWD_ERR;
            messageType = NotificationType.ERROR;
            isSucceeded = false;
        } else if (STATUS_FAILED_WEAK_TYPE.equals(rspCode)) {
            resultStr = NOTICE_TITLE_PWD_WEAK;
            messageType = NotificationType.ERROR;
            isSucceeded = false;
        } else if (STATUS_FAILED_NAME_EXIT.equals(rspCode)) {
            resultStr = NOTICE_TITLE_NAME_EXIT;
            messageType = NotificationType.ERROR;
            isSucceeded = false;
        } else if (NO_CHANGE.equals(rspCode)) {
            resultStr = CHANGE_PWD_EQUAL;
            messageType = NotificationType.ERROR;
            isSucceeded = false;
        } else {
            resultStr = UPDATE_FAILD;
            messageType = NotificationType.ERROR;
            isSucceeded = false;
        }

        if (!I18NServer.getCurrentLanguage().equals(Language.EN.code())) {
            resultStr =
                    StringUtil.getStrFromDiffCharset(
                            resultStr, TuningIDEConstant.CHARSET_UTF8, TuningIDEConstant.CHARSET_UTF8);
        }
        // 消息对话框无返回, 仅做通知作用
        NotificationBean notificationBean = new NotificationBean(dialogTitle, resultStr, messageType);
        notificationBean.setProject(CommonUtil.getDefaultProject());
        IDENotificationUtil.notificationCommon(notificationBean);
        return isSucceeded;
    }
}

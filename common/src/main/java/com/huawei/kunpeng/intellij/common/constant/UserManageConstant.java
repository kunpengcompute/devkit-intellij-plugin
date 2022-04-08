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

package com.huawei.kunpeng.intellij.common.constant;

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;

import java.util.Objects;

/**
 * 用户管理相关常量定义
 *
 * @since 2021-04-08
 */
public class UserManageConstant {
    /**
     * 国际化: 用户管理
     */
    public static final String MANAGE_USER = CommonI18NServer.toLocale("common_term_user_info_manage_user");

    /**
     * 国际化: 修改密码
     */
    public static final String TITLE_CHANGE_USER = CommonI18NServer.toLocale("common_term_title_change");

    /**
     * 国际化： 确认密码
     */
    public static final String USER_LABEL_CONFIRM_PWD = CommonI18NServer.toLocale("common_term_user_label_confirmPwd");

    /**
     * 国际化：创建用户
     */
    public static final String TITLE_CREATE_USER = CommonI18NServer.toLocale("common_term_create_user");

    /**
     * ID
     */
    public static final String USER_LABEL_USER_ID = "ID";

    /**
     * 国际化： 用户名
     */
    public static final String USER_LABEL_NAME = CommonI18NServer.toLocale("common_term_user_label_name");

    /**
     * 国际化：角色
     */
    public static final String USER_LABEL_ROLE = CommonI18NServer.toLocale("common_term_user_label_role");

    /**
     * 管理员-国际化常量
     */
    public static final String USER_ROLE_ADMIN_FOR_I18N = CommonI18NServer.toLocale("common_term_user_role_admin");

    /**
     * 常量 用户角色普通用户
     */
    public static final String USER_ROLE_USER = "User";

    /**
     * 常量 用户角色管理员
     */
    public static final String USER_ROLE_ADMIN = "Admin";


    /**
     * 国际化 删除
     */
    public static final String OPERATE_DEL = CommonI18NServer.toLocale("common_term_operate_del");

    /**
     * 国际化 重置密码
     */
    public static final String OPERATE_RESET = CommonI18NServer.toLocale("common_term_operate_reset");

    /**
     * 国际化 '删除用户'
     */
    public static final String OPERATE_DELETE_TITLE = CommonI18NServer.toLocale("common_term_operate_delete_title");

    /**
     * 国际化 '管理员密码'
     */
    public static final String USER_LABEL_ADMIN_PWD = CommonI18NServer.toLocale("common_term_user_label_adminPwd");

    /**
     * 国际化 '密码'
     */
    public static final String USER_LABEL_PASSWORD = CommonI18NServer.toLocale("common_term_user_label_password");

    /**
     * 国际化 '新密码'
     */
    public static final String USER_LABEL_NEW_PASSWORD = CommonI18NServer.toLocale("common_term_user_label_newPwd");

    /**
     * 国际化 '旧密码'
     */
    public static final String USER_LABEL_OLD_PASSWORD = CommonI18NServer.toLocale("common_term_user_label_oldPwd");

    /**
     * 国际化 '为保证帐号安全，请修改密码后重新登录。'
     */
    public static final String USER_TIPS_PASSWORD_INIT = CommonI18NServer.toLocale("common_tips_passwordInit");

    /**
     * 国际化 '用户名不能为空'
     */
    public static final String USER_NAME_EMPTY = CommonI18NServer.toLocale("common_login_userNameEmpty");

    /**
     * 国际化 '新密码不能是旧密码的逆序，必须包含大写字母、小写字母、
     * 数字以及特殊字符（`~!@#$%^&*()-_=+\\|[{}];:\&#39;&quot;,&lt;.&gt;/?）
     * 中两种及以上类型的组合，长度为8~32个字符。'
     */
    public static final String USER_NEW_PWD_VALIDATE_TIPS = CommonI18NServer.toLocale(
        "common_login_newPwdValidateTips");

    /**
     * 国际化 删除用户提示语的内容。
     */
    public static final String TERM_OPERATE_DELETE_TIP1 = CommonI18NServer.toLocale("common_term_operate_delete_tip1");

    /**
     * 国际化 确认。
     */
    public static final String TERM_OPERATE_OK = CommonI18NServer.toLocale("common_term_operate_ok");

    /**
     * 国际化 取消。
     */
    public static final String TERM_OPERATE_CANCEL = CommonI18NServer.toLocale("common_term_operate_cancel");

    /**
     * 国际化 请输入管理员密码。
     */
    public static final String TERM_ADMIN_PWD_CHECK = CommonI18NServer.toLocale("common_term_adminpwd_check");

    /**
     * 国际化 新密码与确认密码必须相同。
     */
    public static final String TERM_NO_SAME = CommonI18NServer.toLocale("common_term_no_samepwd");

    /**
     * 国际化 名称长度为6到32位，至少包含数字，字母，符号两种字符。
     */
    public static final String TERM_CHECK_USERNAME = CommonI18NServer.toLocale("common_term_validation_username");

    /**
     * 国际化 名称为空时。
     */
    public static final String TERM_CHECK_USERNAME_NULL = CommonI18NServer.toLocale(
        "common_term_validation_username_null");

    /**
     * 国际化 删除用户提示语的英文部分1。
     */
    public static final String TERM_OPERATE_DELETE_TIP_EN_PART1 = "After a user is deleted,"
        + " the Historical reports of the user will be deleted.";

    /**
     * 国际化 删除用户提示语的英文部分2。
     */
    public static final String TERM_OPERATE_DELETE_TIP1_EN_PART2 = "Exercise caution when performing this operation.";

    /**
     * 国际化 免责声明
     */
    public static final String USER_DISCLAIMER_TITLE = CommonI18NServer.toLocale("common_user_disclaimer_title");

    /**
     * 国际化 必须包含大写字母、小写字母、数字以及特殊字符
     * （`~!@#$%^&*()-_=+\\|[{}];:\'",<.>/?）
     * 中两种及以上类型的组合，长度为8~32个字符。
     */
    public static final String TERM_VALIDATION_PASSWORD = CommonI18NServer.toLocale("common_term_validation_password");

    /**
     * 当输入用户密码为空时的校验
     */
    public static final String TERM_VALIDATION_NULL_PASSWORD =
        CommonI18NServer.toLocale("common_term_validation_null_password");

    /**
     * 当输入管理员密码为空时的校验
     */
    public static final String TERM_VALIDATION_NULL_ADMIN_PASSWORD =
        CommonI18NServer.toLocale("common_term_validation_null_admin_password");

    /**
     * 国际化 用户创建后名称不允许修改。
     */
    public static final String TERM_CREATE_TIPS = CommonI18NServer.toLocale("common_term_create_tip");

    /**
     * 国际化 弱密码必须包含大写字母、小写字母、数字以及特殊字符
     * （`~!@#$%^&*()-_=+\\|[{}];:\'",<.>/?）
     * 中两种及以上类型的组合，长度为8~32个字符。
     */
    public static final String WEAK_PWD_RULE = CommonI18NServer.toLocale("common_weak_pwd_rule");

    /**
     * 系统设置
     */
    public static final String SYSTEM_SETTING_TITLE = CommonI18NServer.toLocale("common_system_setting_title");

    /**
     * 国际化 当前密码不符合要求。。
     */
    public static final String TERM_USER_PWD_TIPS = CommonI18NServer.toLocale("common_term_user_pwd_tips");


    /**
     * 国际化 请输入旧密码。
     */
    public static final String TERM_OLD_PWD_CHECK = CommonI18NServer.toLocale("common_term_oldpwd_check");

    /**
     * 国际化 确认。
     */
    public static final String TERM_OPERATE_CONFIRM = CommonI18NServer.toLocale("common_term_operate_confirm");

    /**
     * 国际化 关闭。
     */
    public static final String TERM_OPERATE_CLOSE = CommonI18NServer.toLocale("common_term_operate_close");

    /**
     * 国际化：修改
     */
    public static final String TITLE_MODIFY = CommonI18NServer.toLocale("common_term_operate_modify");

    /**
     * 国际化 操作
     */
    public static final String TERM_OPERATE = CommonI18NServer.toLocale("common_term_operate");

    /**
     * 配置面板title
     */
    public static final String CONFIG_TITLE = CommonI18NServer.toLocale("common_config_title");

    /**
     * 登录成功
     */
    public static final String LOGIN_OK = "0";

    /**
     * tuning管理员设置密码成功
     */
    public static final String TUNING_PWDSETSUCCESS = "UserManage.User.Post.PwdSetSuccess";

    /**
     * 返回日志管理标题
     *
     * @return 标题
     */
    public static String getLogTitle() {
        boolean isAdminUser = Objects.equals(USER_ROLE_ADMIN, UserInfoContext.getInstance().getRole());
        if (isAdminUser) {
            return CommonI18NServer.toLocale("plugins_common_term_user_info_manage_log");
        } else {
            return CommonI18NServer.toLocale("plugins_common_title_opt_log");
        }
    }
}

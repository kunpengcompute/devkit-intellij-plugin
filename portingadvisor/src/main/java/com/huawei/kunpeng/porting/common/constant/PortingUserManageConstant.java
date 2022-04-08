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

package com.huawei.kunpeng.porting.common.constant;

import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import java.util.Objects;

/**
 * 用户管理相关常量定义
 *
 * @since 2020-10-6
 */
public class PortingUserManageConstant extends UserManageConstant {
    /**
     * 国际化 ： 工作空间
     */
    public static final String USER_LABEL_WORKSPACE = I18NServer.toLocale("plugins_common_term_user_label_workspace");

    /**
     * 工作空间固定目录
     */
    public static final String WORKSPACE_HEAD_PATH = "/portadv/";

    /**
     * 管理员-国际化常量
     */
    public static final String USER_ROLE_ADMIN_FOR_I18N = I18NServer.toLocale("plugins_common_term_user_role_admin");

    /**
     * 常量
     */
    public static final String USER_ROLE_USER = "User";

    /**
     * 国际化 '普通用户'
     */
    public static final String USER_LABEL_ROLE_TYPE = I18NServer.toLocale("plugins_common_term_user_label_role_type");

    /**
     * 国际化 该字段不能为空
     */
    public static final String TERM_NO_BLANK = I18NServer.toLocale("plugins_common_term_no_blank");

    /**
     * 国际化 下载日志
     */
    public static final String TERM_DOWNLOG = I18NServer.toLocale("plugins_porting_button_download_log");

    /**
     * 国际化 日志级别
     */
    public static final String TERM_LOGLEVEL = I18NServer.toLocale("plugins_porting_log_level");

    /**
     * 国际化 您一旦确认本声明，即被视为您理解并同意了本声明的全部内容：
     */
    public static final String USER_DISCLAIMER_LIST1
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list1");

    /**
     * 国际化 您一旦确认本声明，即被视为您理解并同意了本声明的全部内容：
     */
    public static final String USER_DISCLAIMER_CONFIRMED
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_confirmed");

    /**
     * 国际化 1、 您上传及查阅源码前已确认您为源码所有者或者已获得源码所有者的充足授权同意。
     */
    public static final String USER_DISCLAIMER_LIST2
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list2");

    /**
     * 国际化 2、 未经所有者授权，任何个人或组织均不得使用该源码从事任何活动。华为公司不对由此造成的一切后果负责，
     * 亦不承担任何法律责任。必要时，将追究其法律责任。
     */
    public static final String USER_DISCLAIMER_LIST3
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list3");

    /**
     * 国际化 3、 未经所有者授权，任何个人或组织均不得私自传播该源码。华为公司不对由此造成的一切后果负责，
     * 亦不承担任何法律责任。必要时，将追究其法律责任。
     */
    public static final String USER_DISCLAIMER_LIST4
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list4");

    /**
     * 国际化 4、 该源码及相关迁移报告、迁移建议等，仅做查阅参考，
     * 不具有法律效力，也不以任何方式或形式构成特定指引和法律建议。
     */
    public static final String USER_DISCLAIMER_LIST5
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list5");

    /**
     * 国际化 5、 除非法律法规或双方合同另有规定，华为对本迁移建议及相关内容不做任何明示或暗示的声明或保证，
     * 不对本迁移建议及相关内容的适销性、满意度、非侵权性或特定用途适用性等做出任何保证或承诺。
     */
    public static final String USER_DISCLAIMER_LIST6
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list6");

    /**
     * 国际化 6、 您根据本迁移建议及相关内容所采取的任何行为均由您自行承担风险，
     * 华为在任何情况下均不对任何性质的损害或损失负责。
     */
    public static final String USER_DISCLAIMER_LIST7
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list7");

    /**
     * 国际化 8、 点击确认将上传源码至当前服务器工作目录下，用于源码扫描分析。
     * 本工具不会将这些源码用于其他目的，通过其他用户名登录同一台服务器的用户无权查看您工作目录的代码。
     */
    public static final String USER_DISCLAIMER_LIST8
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list8");

    /**
     * 国际化 1、 建议您在非生产环境使用本工具，避免影响生产业务运行。
     */
    public static final String USER_DISCLAIMER_LIST9
            = I18NServer.toLocale("plugins_common_porting_user_disclaimer_list9");

    /**
     * 国际化 用户免责证明内容。
     */
    public static final String PORTING_USER_DISCLAIMER = I18NServer.toLocale("plugins_common_porting_user_disclaimer");

    /**
     * 国际化 压缩日志。
     */
    public static final String SETTINGS_COMPRESSING_LOGS =
            I18NServer.toLocale("plugins_porting_settings_compressing_logs");

    /**
     * 国际化 压缩中...
     */
    public static final String SETTINGS_COMPRESSING = I18NServer.toLocale("plugins_porting_settings_compressing");

    /**
     * 国际化 日志压缩失败
     */
    public static final String SETTINGS_COMPRESS_LOGS_FAILED =
            I18NServer.toLocale("plugins_porting_settings_compress_logs_failed");

    /**
     * 国际化 任务取消成功
     */
    public static final String SETTINGS_TASK_CANCELED_SUCCESS =
            I18NServer.toLocale("plugins_porting_settings_task_canceled_success");

    /**
     * 国际化 任务取消失败
     */
    public static final String SETTINGS_TASK_CANCELED_FAILED =
            I18NServer.toLocale("plugins_porting_settings_task_canceled_failed");

    /**
     * 登录面板title
     */
    public static final String LOGIN_TITLE = I18NServer.toLocale("plugins_ui_common_login_title");

    /**
     * 更新面板title
     */
    public static final String UPGRADE_TITLE = I18NServer.toLocale("plugins_porting_upgrade_title");

    /**
     * 安装面板title
     */
    public static final String INSTALL_TITLE = I18NServer.toLocale("plugins_porting_install_title");

    /**
     * 返回日志管理标题
     *
     * @return 标题
     */
    public static String getLogTitle() {
        boolean isAdminUser = Objects.equals(USER_ROLE_ADMIN, PortingUserInfoContext.getInstance().getRole());
        if (isAdminUser) {
            return I18NServer.toLocale("plugins_common_term_user_info_manage_log");
        } else {
            return I18NServer.toLocale("plugins_common_title_opt_log");
        }
    }
}

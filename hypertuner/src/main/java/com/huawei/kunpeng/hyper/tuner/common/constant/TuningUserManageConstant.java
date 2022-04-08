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

package com.huawei.kunpeng.hyper.tuner.common.constant;

import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 用户管理相关常量定义
 *
 * @since 2020-10-6
 */
public class TuningUserManageConstant extends UserManageConstant {
    /**
     * 国际化 日志级别
     */
    public static final String TERM_LOGLEVEL = TuningI18NServer.toLocale("plugins_hyper_tuner_log_level");

    /**
     * 国际化 删除用户提示语的英文部分1。
     */
    public static final String TERM_OPERATE_DELETE_TIP_EN_PART1 =
            "After a user is deleted," + " the Historical reports of the user will be deleted.";

    /**
     * 国际化 删除用户提示语的英文部分2。
     */
    public static final String TERM_OPERATE_DELETE_TIP1_EN_PART2 = "Exercise caution when performing this operation.";

    /**
     * 国际化-标题-免责声明
     */
    public static final String USER_DISCLAIMER_TITLE =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_title");

    /**
     * 国际化-内容-首行：您一旦确认本声明，即视为您理解并同意了本声明的全部内容：
     */
    public static final String USER_DISCLAIMER_HEAD =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_head");

    /**
     * 国际化-免责声明-内容： 1. 建议您在非生产环境使用本工具，避免影响生产业务运行。。。。
     */
    public static final String USER_DISCLAIMER_CONTENT =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_content");

    /**
     * 国际化-确认内容：我已阅读以上内容
     */
    public static final String USER_DISCLAIMER_CONFIRM =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_confirm");

    /**
     * 国际化-确认内容：我已阅读以上内容
     */
    public static final String USER_DISCLAIMER_TIPS_TITLE =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_tips_title");

    /**
     * 国际化-警告：不同意免责声明将退出性能分析工具，请谨慎选择！
     */
    public static final String USER_DISCLAIMER_REFUSE_WARNING =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_refuse_warning");

    /**
     * 国际化-再想想
     */
    public static final String USER_DISCLAIMER_THINK_AGAIN =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_refuse_think_again");

    /**
     * 国际化-退出
     */
    public static final String USER_DISCLAIMER_LOG_OUT =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_refuse_logout");

    /**
     * 国际化 您一旦确认本声明，即被视为您理解并同意了本声明的全部内容：
     */
    public static final String USER_DISCLAIMER_LIST1 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list1");

    /**
     * 国际化 1、 您上传及查阅源码前已确认您为源码所有者或者已获得源码所有者的充足授权同意。
     */
    public static final String USER_DISCLAIMER_LIST2 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list2");

    /**
     * 国际化 2、 未经所有者授权，任何个人或组织均不得使用该源码从事任何活动。华为公司不对由此造成的一切后果负责，
     * 亦不承担任何法律责任。必要时，将追究其法律责任。
     */
    public static final String USER_DISCLAIMER_LIST3 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list3");

    /**
     * 国际化 3、 未经所有者授权，任何个人或组织均不得私自传播该源码。华为公司不对由此造成的一切后果负责，
     * 亦不承担任何法律责任。必要时，将追究其法律责任。
     */
    public static final String USER_DISCLAIMER_LIST4 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list4");

    /**
     * 国际化 4、 该源码及相关迁移报告、迁移建议等，仅做查阅参考，不具有法律效力，也不以任何方式或形式构成特定指引和法律建议。
     */
    public static final String USER_DISCLAIMER_LIST5 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list5");

    /**
     * 国际化 5、 除非法律法规或双方合同另有规定，华为对本迁移建议及相关内容不做任何明示或暗示的声明或保证，
     * 不对本迁移建议及相关内容的适销性、满意度、非侵权性或特定用途适用性等做出任何保证或承诺。
     */
    public static final String USER_DISCLAIMER_LIST6 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list6");

    /**
     * 国际化 6、 您根据本迁移建议及相关内容所采取的任何行为均由您自行承担风险，华为在任何情况下均不对任何性质的损害或损失负责。
     */
    public static final String USER_DISCLAIMER_LIST7 =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_list7");

    /**
     * 国际化 必须包含大写字母、小写字母、数字以及特殊字符（`~!@#$%^&*()-_=+\\|[{}];:\'",<.>/?）中两种及以上类型的组合，
     * 长度为8~32个字符。
     */
    public static final String TERM_VALIDATION_PASSWORD =
            TuningI18NServer.toLocale("plugins_common_term_valition_password");

    /**
     * 登录面板title
     */
    public static final String LEFTTREE_LOGIN_TITLE = I18NServer.toLocale("plugins_hyper_tuner_lefttree_login");

    /**
     * 国际化: 修改密码
     */
    public static final String LEFTTREE_CHANGE_PASSWORD =
            TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_change_password");

    /**
     * 国际化: 注销
     */
    public static final String LEFTTREE_LOGOUT = TuningI18NServer.toLocale("plugins_hyper_tuner_lefttree_logout");
}

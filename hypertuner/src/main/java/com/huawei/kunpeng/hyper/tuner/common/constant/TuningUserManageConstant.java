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

/**
 * 用户管理相关常量定义
 *
 * @since 2020-10-6
 */
public class TuningUserManageConstant extends UserManageConstant {
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
     * 国际化-提示
     */
    public static final String USER_DISCLAIMER_TIPS_TITLE =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_tips_title");

    /**
     * 国际化-警告：不同意免责声明将退出性能分析工具，请谨慎选择！
     */
    public static final String USER_DISCLAIMER_REFUSE_WARNING =
            TuningI18NServer.toLocale("plugins_common_hyper_tuner_user_disclaimer_refuse_warning");
}

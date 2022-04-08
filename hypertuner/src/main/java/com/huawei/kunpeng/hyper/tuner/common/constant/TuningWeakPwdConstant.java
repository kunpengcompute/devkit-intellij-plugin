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
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;

/**
 * 弱口令与部署声明相关常量定义
 *
 * @since 2020-10-15
 */
public class TuningWeakPwdConstant extends WeakPwdConstant {
    /**
     * 国际化: 部署声明内容
     */
    public static final String DEPLOY_CONTENT =
            TuningI18NServer.toLocale("plugins_hyper_tuner_message_beforeInstallDsc");

    /**
     * 国际化: 部署必读标题
     */
    public static final String FIRST_CONFIG_TITLE =
            TuningI18NServer.toLocale("plugins_hyper_tuner_message_beforeInstallTitle");

    /**
     * 国际化: 确认保存配置标题
     */
    public static final String CONFIG_SAVE_CONFIRM_TITLE =
            TuningI18NServer.toLocale("plugins_hyper_tuner_config_saveConfirm_title");

    /**
     * 国际化: 增加弱密码成功
     */
    public static final String ADDWEAKPWD_SUCCESS = TuningI18NServer.toLocale("plugins_hyper_tuner_addWeakPwd_sucess");
    /**
     * 国际化: 增加弱密码存在
     */
    public static final String ADD_WEAK_PWD_EXIST = TuningI18NServer.toLocale("plugins_hyper_tuner_addWeakPwd_exist");
    /**
     * 国际化: 增加弱密码
     */
    public static final String ADD_WEAK_PWD_INVAILD =
            TuningI18NServer.toLocale("plugins_hyper_tuner_addWeakPwd_invaild");
    /**
     * 国际化: 增加弱密码最大数
     */
    public static final String ADD_WEAK_PWD_MAXMUNBER =
            TuningI18NServer.toLocale("plugins_hyper_tuner_addWeakPwd_maxmunber");
    /**
     * 国际化: 增加弱密码失败
     */
    public static final String ADD_WEAK_PWD_FAILD = TuningI18NServer.toLocale("plugins_hyper_tuner_addWeakPwd_faild");
    /**
     * 国际化: 提交成功
     */
    public static final String RESPONSE_SUCESS = TuningI18NServer.toLocale("plugins_hyper_tuner_common_sucess");
    /**
     * 国际化: 提交失败
     */
    public static final String RESPONSE_FAILD = TuningI18NServer.toLocale("plugins_hyper_tuner_common_faild");
}

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

import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 弱口令与部署声明相关常量定义
 *
 * @since 2020-10-15
 */
public class PortingWeakPwdConstant extends WeakPwdConstant {
    /**
     * 国际化: 部署声明内容
     */
    public static final String DEPLOY_CONTENT = I18NServer.toLocale("plugins_porting_message_beforeInstallDsc");

    /**
     * 国际化: 总条数
     */
    public static final String TOTAL_NUM = I18NServer.toLocale("plugins_porting_Tip_TotalNum");

    /**
     * 国际化: 部署必读标题
     */
    public static final String FIRST_CONFIG_TITLE = I18NServer.toLocale("plugins_porting_message_beforeInstallTitle");

}

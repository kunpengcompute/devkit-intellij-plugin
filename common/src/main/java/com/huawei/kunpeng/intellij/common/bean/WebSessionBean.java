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

package com.huawei.kunpeng.intellij.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * webview-WebSession信息
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WebSessionBean extends DataBean {
    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户登录ID
     */
    private Integer loginId;

    /**
     * 是否是第一次登录
     */
    private Integer isFirst;

    /**
     * 当前显示语言
     */
    private String language;

    private boolean isMigrationTip;

    private String tuningOperation;
}

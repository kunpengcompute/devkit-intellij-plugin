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

import lombok.Data;

import java.util.Vector;

/**
 * 用户对象
 *
 * @since 2020-10-6
 */
@Data
public class UserBean {
    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户工作空间
     */
    private String workspace;

    /**
     * 用户工作ID
     */
    private int id;

    /**
     * 用户登录状态。
     */
    private String loginStatus;

    /**
     * 用户信息列表
     *
     * @return Vector
     */
    public Vector<String> toVector() {
        Vector<String> userArr = new Vector<>();
        userArr.add(String.valueOf(id));
        userArr.add(username);
        userArr.add(role);
        userArr.add(workspace);
        return userArr;
    }
}

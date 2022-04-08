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
 * 弱口令对象
 *
 * @since 2020-10-09
 */
@Data
public class WeakPwdBean {
    /**
     * 弱口令ID
     */
    private String id;

    /**
     * 弱口令名称
     */
    private String weakPassword;

    /**
     * 推荐构造函数
     *
     * @param weakPassword 弱口令名称
     * @param id id
     */
    public WeakPwdBean(String id, String weakPassword) {
        this.id = id;
        this.weakPassword = weakPassword;
    }

    /**
     * toVector
     *
     * @return Vector
     */
    public Vector<String> toWeakPwdVector() {
        Vector<String> weakPwdVec = new Vector<>();
        weakPwdVec.add(getId());
        weakPwdVec.add(getWeakPassword());
        weakPwdVec.add("");
        return weakPwdVec;
    }
}

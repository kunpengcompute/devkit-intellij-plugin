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

package com.huawei.kunpeng.intellij.js2java.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * js与java交互信息体
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class MessageBean {
    /**
     * js回调函数id
     */
    private String cbid;

    /**
     * java端对接函数名
     */
    private String cmd;

    /**
     * 模块
     */
    private String module;

    /**
     * 数据
     */
    private String data;

    /**
     * json 数据
     */
    private String messageJsonStr;

    /**
     * 默认构造函数
     */
    public MessageBean() {
        this(null, null, null, null);
    }

    /**
     * 推荐构造函数
     *
     * @param cbid js回调函数id
     * @param cmd java端对接函数名
     * @param module 模块
     * @param data 数据
     */
    public MessageBean(String cbid, String cmd, String module, String data) {
        this.cbid = cbid;
        this.cmd = cmd;
        this.module = module;
        this.data = data;
    }
}

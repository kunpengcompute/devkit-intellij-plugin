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
 * 操作日志实体类
 *
 * @since 1.0.0
 */
@Data
public class OperateLogBean {
    private String detail;

    private String event;

    private String result;

    private String time;

    private String username;

    private String information;

    private String ipaddr;

    private String module_type;

    private String filename;

    private String filesize;

    public String getModuleType() {
        return module_type;
    }

    public void setModuleType(String module_type) {
        this.module_type = module_type;
    }

    /**
     * 用户信息列表
     *
     * @return Vector operaArr
     */
    public Vector<String> toVector() {
        Vector<String> operaArr = new Vector<String>(5);
        operaArr.add(username);
        operaArr.add(event);
        operaArr.add(result);
        operaArr.add(time);
        operaArr.add(detail); // 使用文本
        return operaArr;
    }
}

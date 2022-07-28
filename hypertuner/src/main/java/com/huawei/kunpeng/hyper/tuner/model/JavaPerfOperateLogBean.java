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

package com.huawei.kunpeng.hyper.tuner.model;

import lombok.Data;

/**
 * JavaPerf 模块
 * 操作日志实体类
 *
 * @since 2021-07-12
 */
@Data
public class JavaPerfOperateLogBean {
    private Integer id; // 主键ID
    private String userId; // 用户ID

    // 操作用户
    private String username; // 用户名
    private String request; // 网络请求URL

    // 操作名称
    private String operation; // 操作内容

    // 操作详情
    private String resource; // 操作的补充说明

    // 操作主机IP
    private String clientIp; // 客户端的IP地址

    // 操作结果
    private String succeed; // 操作结果

    // 操作时间
    private String createTime; // 创建时间
}
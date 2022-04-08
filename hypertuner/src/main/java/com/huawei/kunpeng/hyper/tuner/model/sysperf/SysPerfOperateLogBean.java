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

package com.huawei.kunpeng.hyper.tuner.model.sysperf;

import lombok.Data;

/**
 * 操作日志
 * 包括
 * 用户管理 操作日志
 * sys-perf操作日志
 *
 * @since 2021-4-25
 */
@Data
public class SysPerfOperateLogBean {
    private String information;
    private String userId;
    private String username;
    private String moduleType;
    private String ipaddr;
    private String result;
    private String time;
    private String filename;
    private String filesize;
}

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

package com.huawei.kunpeng.porting.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 源码文件建议查看数据参数体
 *
 * @since 2021/1/12
 */
@Data
@AllArgsConstructor
public class SourceFileBean {
    /**
     * http url
     */
    private String url;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 是否本地文件
     */
    private boolean isLocalFile;

    /**
     * 本地文件路径
     */
    private String localFilePath;

    /**
     * 远程文件路径
     */
    private String remoteFilePath;
}

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
 * 任务导入导出 实体类
 *
 * @since 2021/04/25
 */
@Data
public class ImpAndExpTaskBean {
    private Integer id; // 任务编号
    private Integer ownerId; // 创建者编号
    private String taskname; // 任务名称
    private Integer taskId;  // 任务编号
    private String projectname;  // 工程名称
    private Integer projectId;  // 工程编号
    private String operationType; // 类型 （导入/导出）
    private String processStatus; // 状态
    private String detailInfo;  // 状态详情
    private String taskFilesize; // 大小
    private String startTime; // 开始时间
    private String endTime; // 结束时间
    private String completeStatus; // 完成状态：（成功/失败）
    private String FileName; // 文件名称
    private Integer fileSectionQty; // 文件分片
    private Boolean isDelete; // 是否删除
}
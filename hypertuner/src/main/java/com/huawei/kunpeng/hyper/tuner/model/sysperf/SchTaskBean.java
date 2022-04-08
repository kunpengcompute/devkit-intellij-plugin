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
 * 预约任务实体类
 *
 * @since 2020/12/03
 */
@Data
public class SchTaskBean {
    private String projectName; // 工程名称 projectname
    private Integer projectId;    // 工程 ID
    private Integer taskId; // 任务ID
    private String taskName;  // 任务名 taskname
    private String analysisType;   // 分析类型 analysis-type
    private String createdTime; // 创建时间
    private boolean cycle; // 采集方式 是为周期采集，否为单次采集
    private String appointment; // 单次执行日期
    private String cycleStart; // 采集开始日期
    private String cycleStop;  // 采集结束日期
    private String targetTime;   // 采集时间
    private String fileSize; // 采集文件大小
    private String scheduleStatus;    // 任务状态 reserve：预约
    private boolean switchResponse;
    private TaskInfoBean taskInfo;
    private String userId;  // 用户ID
    private String userName; // 用户 姓名

    public SchTaskBean() {
        this.taskInfo = new TaskInfoBean();
    }
}

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
 * 任务模板实体类 内部参数 task_param
 * 预约任务实体类 内部对象 taskInfo 的内部对象 taskParam 对象
 *
 * @since 2021/6/22
 */
@Data
public class TaskParamBean {
    private String config;
    private Integer duration;
    private String kcore; // 内核函数关联汇编代码
    /**
     * 任务模板：访存Miss事件分析 指标类型
     */
    private String metrics;
    private String perfDataLimit;
    /**
     * 任务模板：访存Miss事件分析 采样间隔（指令数）
     */
    private String period;
    /**
     * 任务模板：访存Miss事件分析 采样范围
     */
    private String space;
    /**
     * 任务模板：访存Miss事件分析  延迟采样时长 （毫秒）
     */
    private String startDelay;
    private String target; // 分析对象 (app/sys)
    /**
     * 任务模板：进程线程分析 采样类型 （JSON数组）
     */
    private String type;
    private String pid;
    private String processName;
    private String app; // 应用路径 miss
    private String appArgs; // 应用参数 miss

    private String cpu; // 待采样CPU核 miss
    private String srcDir; // c++源文件 miss

    private String preset; // miss
    private String openMpParam; // miss
    private String mpiEnvDir; // miss
    private String rank; // miss
}

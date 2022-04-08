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
 * 预约任务 和 任务模板 中的内部对象
 * nodeConfig 节点实体类 嵌套对象 任务参数实体类 的嵌套对象
 * 任务参数实体类
 *
 * @since 2021-4-25
 */
@Data
public class NodeConfigTaskParamBean {
    // 分析对象
    private String analysisType;

    // 分析类型 analysisTarget/analysis-target
    private String analysisTarget;
    private String projectName;
    private String taskName;

    // 采样时长
    private Integer duration;
    private Integer statistical;
    private Integer size;
    private Boolean stack;
    private Boolean cycle;

    // 请求返回值为关键字 switch
    private Boolean switchResponse;
    private String appDir;
    private String cycleStart;
    private String cycleStop;
    private String targetTime;
    private String appointment;
    private String appParameters;

    // 采样间隔（毫秒）
    private String interval;

    // 二进制文件路径
    private String assemblyLocation;

    // C/C++源文件路径
    private String sourceLocation;
    private boolean kcore;
    private boolean status;
    private Integer perfDataLimit;
    private boolean topCheck;
    private String cpuMask;
    private String pid;
    private String process;
    private String straceAnalysis;
    private String thread;
    private String samplingMode;
    private String analysisIndex;
    private String samplingSpace;
    private String samplingDelay;
    private String profilingMode;
    private String targetPid;
    private String processName;
    private String appWorkingDir;
    private String disCallstack;
    private Integer filesize; // 采集文件大小; false
    private Integer collectFileSize; // 采集文件大小;
    private String preset; // 采样类型  HPC：总览，top-down，指令分布
    private Boolean mpiStatus; // MPI HPC
    private Integer rank; // HPC
    private String mpiEnvDir; // HPC
    private String openMpParam; // HPC openMp参数
    /**
     * 进程线程分析采样类型    "task_param"  "type": cpu, mem, disk, context
     * miss event 类型： 为存储的各字段数据 appArgs app duration 等
     */
    private String taskParam;
}
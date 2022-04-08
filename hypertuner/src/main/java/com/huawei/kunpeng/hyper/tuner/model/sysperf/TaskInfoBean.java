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

import java.util.ArrayList;
import java.util.List;

/**
 * 预约任务 内部对象 taskInfo 定义类
 *
 * @since 2021-04-25
 */
@Data
public class TaskInfoBean {
    private String analysisType; // 分析对象
    private String analysisTarget; // analysis-target
    private String projectName;
    private String taskName;
    private Integer duration; // 采样时长
    private Integer statistical; // 统计周期（秒）
    private Integer size; // 采样文件大小 （C/C++/热点函数）
    private Integer collectFileSize; // lock 采集文件大小
    private Integer filesize; // miss 采集文件大小
    private String collectRange; // lock
    private Boolean stack; // 采集调用栈
    private Boolean cycle;
    private Boolean switchResponse;
    private String appDir; // 应用路径
    private String app; // 应用路径 MissEvent分析
    private String cycleStart;
    private String cycleStop;
    private String targetTime;
    private String appointment;
    private String appParameters; // 应用参数
    private String interval; // 采样间隔（毫秒）
    private String assemblyLocation; // 二进制文件路径
    private String sourceLocation; // C/C++源文件路径
    private String srcDir; // C/C++源文件路径 MissEvent分析
    private Boolean kcore; // 内核函数关联代码
    private Boolean status;
    private Integer perfDataLimit; // 采样文件大小
    private Boolean topCheck; // 采集Top活跃进程 Collect top active
    private String configDir; // 进程线程分析 大数据采集路径
    private String cpuMask; // 待采样CPU核
    private String pid; // PID
    private String process; // 进程名
    private String profilingMode; // lock and wait 字段但是不展示
    private String straceAnalysis; // 跟踪系统调用 进程线程分析
    private String thread; // 进程/线程分析 是否采集线程信息
    private String targetPid; // 热点函数类型/IO分析类型/锁与等待类型 PID
    private String samplingMode; // 采样范围 微架构
    private String analysisIndex; // 分析指标  微架构
    private String samplingSpace; // 采集范围采样范围
    private String samplingDelay; // 延时采样时长
    private String processName;
    private String appWorkingDir; // 不展示
    private Boolean disCallstack; // 采集调用栈 资源调度分析
    private String functionname; // 分析函数
    private String preset; // 采样类型  HPC：总览，top-down，指令分布
    private String openMpParam; // HPC OpenMP参数
    private Boolean mpiStatus; // HPC MPI 开关
    private String mpiEnvDir; // MPI MPI 命令所在目录
    private Integer rank; // HPC MPI Rank
    private Integer period; // FalseSharing 采样间隔 （指令数）
    private TaskParamBean taskParam;

    // 节点信息
    private List<NodeConfigBean> nodeConfig;

    public TaskInfoBean() {
        this.taskParam = new TaskParamBean();
        this.nodeConfig = new ArrayList<>();
    }

}
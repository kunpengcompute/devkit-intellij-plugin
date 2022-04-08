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

import java.util.List;

/**
 * 任务模板 实体类
 *
 * @since 2020/12/03
 */
@Data
public class TaskTemplateBean {
    private int id;
    private String analysisType;
    private String analysisTarget;
    private String appointment;
    private Boolean cycle;
    private String cycleStart;
    private String cycleStop;
    private String projectName;
    private String taskName;
    private String templateName;
    private String targetTime;
    private String pid;
    private String appDir; // 应用路径（必填参数）
    private String appParameters; // 应用参数
    private String targetPid;
    private String processName;
    private String stack; // 采集调用栈
    private Integer duration; // 采样时长
    private String interval;  // 采样间隔
    private String kcore; // 内核函数关联汇编代码
    private String topCheck; // 采集Top活跃进程 （全景分析）
    private String assemblyLocation; // 二进制/符号文件路径
    private String cpuMask; // 待采样CPU核
    private String analysisIndex; // 分析指标
    private String size; // 采集文件大小
    private String fileSize; // 采集文件大小
    private String samplingSpace; // 采样范围
    private String collectRange; // 采样范围
    private String samplingDelay; // 延时采样时长
    private String sourceLocation; // C/C++源文件路径
    private String statistical; // 统计周期（秒）
    private String functionname; // 分析函数
    private String thread; // 采集线程信息 （进程/线程分析）
    private String straceAnalysis; // 跟踪系统调用 （进程线程分析）
    private String configDir; // 全景分析 大数据类型
    private String Switch;

    // HPC 分析任务
    private Boolean mpiStatus; // MPI
    private String preset; // 采样类型 default/ top-down /instruction-mix
    private Integer rank;
    private String mpiEnvDir; // MPI命令所在目录
    private String openMpParam; // openMp参数
    private String disCallstack; // 采集调用栈
    private String perfDataLimit; // 采集文件大小
    private String samplingMode; // 采样模式

    private String period; // 采样间隔（指令数） false/miss
    private TaskParamBean taskParam;
    private List<NodeConfigBean> nodeConfig;
}

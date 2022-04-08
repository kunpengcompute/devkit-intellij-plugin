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

package com.huawei.kunpeng.hyper.tuner.common.constant.sysperf;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

/**
 * 任务常量定义类
 *
 * @since 2021-4-25
 */
public class TaskManageContent {
    /**
     * ****** 分析对象 ******
     */
    public static final String ANALYSIS_TARGET = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_analysisTarget");
    /**
     * 分析模式
     */
    public static final String ANALYSIS_MODE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_analysisMode");
    /**
     * 分析对象:应用： Launch Application
     */
    public static final String TARGET_APP_LAUNCH_APPLICATION = "Launch Application";
    /**
     * 分析对象:应用： Attach to Process
     */
    public static final String TARGET_APP_ATTACH_TO_PROCESS = "Attach to Process";
    /**
     * 分析对象:系统：Profile System
     */
    public static final String TARGET_APP_PROFILE_SYSTEM = "Profile System";
    /**
     * 分析对象:系统：system
     */
    public static final String TARGET_APP_SYSTEM = "system";
    /**
     * 分析对象 应用
     */
    public static final String ANALYSIS_TARGET_APP = I18NServer.toLocale(
            "plugins_hyper_tuner_task_target_application");
    /**
     * 分析对象 系统
     */
    public static final String ANALYSIS_TARGET_SYS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_target_system");
    /**
     * ****** 分析类型 ******
     */
    public static final String ANALYSIS_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_analysisType");

    /**
     * 分析类型-全景分析
     */
    public static final String ANALYSIS_TYPE_OVERALL = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_overall");
    /**
     * 分析类型-微架构分析
     */
    public static final String ANALYSIS_TYPE_MICRO = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_micro");

    /**
     * 访存分析类型 （仅访存分析类型有该字段）
     */
    public static final String ACCESS_ANALYSIS_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_analysisType_mem");
    /**
     * 分析类型-访存分析-访存分析
     */
    public static final String ANALYSIS_TYPE_MEM_ACCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_mem_access");
    /**
     * 分析类型-访存分析-访存统计分析
     */
    public static final String ANALYSIS_TYPE_MEM_ACCESS_STATISTICS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_mem_access_statistics");
    /**
     * 分析类型-访存分析-Miss 事件分析
     */
    public static final String ANALYSIS_TYPE_MISS_EVENT = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_miss_event");
    /**
     * 分析类型-访存分析-伪共享分析
     */
    public static final String ANALYSIS_TYPE_FALSE_SHARE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_false_share");
    /**
     * 分析类型-io性能分析
     */
    public static final String ANALYSIS_TYPE_IO = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_io");
    /**
     * 分析类型-线程/进程分析
     */
    public static final String ANALYSIS_TYPE_PROCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_process");
    /**
     * 分析类型-c/c++分析
     */
    public static final String ANALYSIS_TYPE_CPP = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_cpp");
    /**
     * 分析类型-资源调度分析
     */
    public static final String ANALYSIS_TYPE_RES_SCH = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_res_sch");
    /**
     * 分析类型-锁与等待分析
     */
    public static final String ANALYSIS_TYPE_LOCK = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_lock");
    /**
     * 分析类型-锁与等待分析
     */
    public static final String ANALYSIS_TYPE_HPC = I18NServer.toLocale(
            "plugins_hyper_tuner_task_type_hpc");
    /**
     * 格式化错误
     */
    public static final String FORMAT_ERROR = I18NServer.toLocale(
            "plugins_hyper_tuner_task_format_error");

    /*
     * ********** 任务参数 **********
     */
    /**
     * 任务参数 - 任务编号
     */
    public static final String TASK_ID = I18NServer.toLocale(
            "plugins_hyper_tuner_project_param_taskId");
    /**
     * 任务参数 - 任务名称
     */
    public static final String TASK_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_project_param_taskName");
    /**
     * 任务参数 - 项目名称
     */
    public static final String PARAM_PROJECT_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_projectName");
    /**
     * 任务参数 - 所属项目
     */
    public static final String PARAM_BELONG_PROJECT = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_belong_project");
    /**
     * 任务参数 - 任务状态
     */
    public static final String TASK_STATUS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_taskStatus");

    /**
     * 任务参数 - 采集间隔（秒）
     */
    public static final String PARAM_INTERVAL_S = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_interval_s");
    /**
     * 任务参数 - 采集间隔（毫秒）
     */
    public static final String PARAM_INTERVAL_MS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_interval_ms");
    /**
     * 任务参数 - 采集间隔（微秒）
     */
    public static final String PARAM_INTERVAL_US = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_interval_us");
    /**
     * 任务参数 - 采集间隔 自定义
     */
    public static final String PARAM_INTERVAL_CUSTOM =
            I18NServer.toLocale("plugins_hyper_tuner_task_param_interval_custom");
    /**
     * 任务参数 - 采集间隔 高精度
     */
    public static final String PARAM_INTERVAL_HIGH =
            I18NServer.toLocale("plugins_hyper_tuner_task_param_interval_high");
    /**
     * 任务参数-持续时间（秒）
     */
    public static final String PARAM_DURATION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_duration_s");
    /**
     * 任务参数-持续时间（秒） sampling
     */
    public static final String PARAM_DURATION_SAMPLING = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_duration_s_sampling");
    /**
     * 任务参数-采集方式
     */
    public static final String PARAM_OPERATE_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_operateType");
    /**
     * 任务参数-采集方式-周期采集
     */
    public static final String PARAM_OPERATE_TYPE_CYCLE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_operateType_cycle");
    /**
     * 任务参数-采集方式-单次采集
     */
    public static final String PARAM_OPERATE_TYPE_ONCE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_operateType_once");
    /**
     * 任务参数-采集时间
     */
    public static final String PARAM_TARGET_TIME = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_targetTime");
    /**
     * 任务参数-采集日期
     */
    public static final String PARAM_SAMPLE_DATE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_appointment");
    /**
     * 任务参数-立即执行
     */
    public static final String PARAM_START_NOW = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_targetTime_startNow");
    /**
     * 任务参数-节点名称
     */
    public static final String PARAM_NODE_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_nodeConfig_nodeName");
    /**
     * 任务参数-节点编号
     */
    public static final String PARAM_NODE_ID = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_nodeConfig_nodeId");

    /**
     * 任务参数-二进制/符号文件路径 assemblyLocation
     */
    public static final String PARAM_ASSEMBLY_LOCATION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_assemblyLocation");

    /**
     * 任务参数-C/C++源文件路径 sourceLocation
     */
    public static final String PARAM_SOURCE_LOCATION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sourceLocation");

    /**
     * 任务参数-应用路径
     */
    public static final String PARAM_APPLICATION_PATH = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_application_path");

    /**
     * 任务参数-采样模式
     */
    public static final String PARAM_SAMPLING_MODE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_mode");

    /**
     * 任务参数- 采样模式：Detail模式
     */
    public static final String PARAM_SAMPLING_MODE_DETAIL = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_mode_detail");
    /**
     * 任务参数- 采样模式：Summary模式
     */
    public static final String PARAM_SAMPLING_MODE_SUMMARY = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_mode_summary");
    /**
     * 任务参数-采集文件大小
     */
    public static final String PARAM_FILE_SIZE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_collected_file_size");

    /**
     * 任务参数- Miss事件分析： 指标类型
     */
    public static final String PARAM_ANALYSIS_METRICS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_mem_miss_metrics");
    /**
     * 任务参数- Miss事件分析： 指标类型:LLC Miss => llcMiss
     */
    public static final String PARAM_MEM_MISS_METRICS_LLC = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_mem_miss_metrics_llc");
    /**
     * 任务参数- Miss事件分析： 指标类型:TLB Miss => tlbMiss
     */
    public static final String PARAM_MEM_MISS_METRICS_TLB = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_mem_miss_metrics_tlb");
    /**
     * 任务参数- Miss事件分析： 指标类型:Remote access => remoteAccess
     */
    public static final String PARAM_MEM_MISS_METRICS_REMOTE_ACCESS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_mem_miss_metrics_remote");
    /**
     * 任务参数- Miss事件分析： 指标类型:Long Latency Load => longLatencyLoad
     */
    public static final String PARAM_MEM_MISS_METRICS_LONG_LATENCY_LOAD = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_mem_miss_metrics_long");

    /**
     * 任务参数-应用参数
     */
    public static final String PARAM_APPLICATION_PARAM = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_application_param");
    /**
     * 任务参数-待采样CPU核
     */
    public static final String PARAM_CPU_CORE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_cpu_core");
    /**
     * 任务参数-内核函数关联汇编代码 kcore
     */
    public static final String PARAM_ASSOCIATE_LOCATION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_associate_core");
    /**
     * 任务参数-延迟采样时长（s）
     */
    public static final String PARAM_SAMPLING_DELAY = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_delay");
    /**
     * 任务参数-采集Top活跃进程 topCheck
     */
    public static final String PARAM_TOP_CHECK = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_topCheck");
    /**
     * 任务参数-大数据采集路径 configDir
     */
    public static final String PARAM_CONFIG_DIR = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_topCheck");
    /**
     * 任务参数-进程线程/采样类型 Sampling Type => type
     */
    public static final String PARAM_SAMPLE_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType");
    /**
     * 任务参数-采集类型 CPU
     */
    public static final String PARAM_SAMPLE_TYPE_CPU = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_cpu");

    /**
     * 任务参数-采集类型 内存
     */
    public static final String PARAM_SAMPLE_TYPE_MEM = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_mem");
    /**
     * 任务参数-采集类型 存储IO
     */
    public static final String PARAM_SAMPLE_TYPE_DISK = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_disk");
    /**
     * 任务参数-采集类型 上下文切换
     */
    public static final String PARAM_SAMPLE_TYPE_CONTEXT = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_context");
    /**
     * 任务参数-采集类型 缓存访问
     */
    public static final String PARAM_SAMPLE_TYPE_CACHE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_cache");
    /**
     * 任务参数-采集类型  DDR访问
     */
    public static final String PARAM_SAMPLE_TYPE_DDR = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_ddr");
    /**
     * 任务参数-采集类型 全部
     */
    public static final String PARAM_SAMPLE_TYPE_SUMMARY = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_summary");


    /**
     * 任务参数-采样间隔（指令数） period
     */
    public static final String PARAM_PERIOD = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_period");

    /**
     * 任务参数-采样范围  space
     */
    public static final String PARAM_SPACE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_space");
    /**
     * 任务参数- 采集调用栈 stack
     */
    public static final String PARAM_STACK = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_stack");
    /**
     * 任务参数-统计周期 statistical
     */
    public static final String PARAM_STATISTICAL = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_statistical");
    /**
     * 任务参数-分析函数
     */
    public static final String PARAM_ANALYSIS_FUNCTION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_analysis_functionName");
    /**
     * 任务参数-开启
     */
    public static final String PARAM_TRUE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_true");
    /**
     * 任务参数-关闭
     */
    public static final String PARAM_FALSE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_false");
    /**
     * 任务参数-全部
     */
    public static final String PARAM_ALL = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_all");
    /**
     * 任务参数-用户
     */
    public static final String PARAM_USER = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_user");
    /**
     * 任务参数-进程线程- 采集线程信息
     */
    public static final String PARAM_THREAD = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_thread");
    /**
     * 任务参数-进程线程-  跟踪系统调用 System Call Tracing
     */
    public static final String PARAM_STRACE_ANALYSIS = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_straceAnalysis");
    /**
     * 任务参数- 分析指标
     */
    public static final String PARAM_ANALYSIS_INDEX = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_analysisIndex");
    /**
     * 任务参数- 分析指标 Bad Speculation => badSpeculation
     */
    public static final String PARAM_ANALYSIS_INDEX_BAD = "Bad Speculation";
    /**
     * 任务参数- 分析指标 Front-End Bound  => frontEndBound
     */
    public static final String PARAM_ANALYSIS_INDEX_FRONT = "Front-End Bound";
    /**
     * 任务参数- 分析指标 Back-End Bound->Resource => Bound resourceBound
     */
    public static final String PARAM_ANALYSIS_INDEX_RESOURCE = "Back-End Bound->Resource Bound";
    /**
     * 任务参数- 分析指标 Back-End Bound->Core Bound => coreBound
     */
    public static final String PARAM_ANALYSIS_INDEX_CORE = "Back-End Bound->Core Bound";
    /**
     * 任务参数- 分析指标 Back-End Bound->Memory Bound => memoryBound
     */
    public static final String PARAM_ANALYSIS_INDEX_MEMORY = "Back-End Bound->Memory Bound";
    /**
     * 任务参数-采样范围
     */
    public static final String PARAM_SAMPLING_RANGE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_range");

    /**
     * 任务参数- 采样范围：全部 space
     */
    public static final String PARAM_SAMPLING_RANGE_ALL = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_range_all");
    /**
     * 任务参数- 采样范围：用户态
     */
    public static final String PARAM_SAMPLING_RANGE_USER = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_range_user");
    /**
     * 任务参数- 采样范围：内核态  Sampling Range => samplingSpace
     */
    public static final String PARAM_SAMPLING_RANGE_KERNEL = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampling_range_kernel");
    /**
     * 任务参数- PID
     */
    public static final String PARAM_PID = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_pid");
    /**
     * 任务参数- 进程名称
     */
    public static final String PARAM_PROCESS_NAME = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_processName");
    /**
     * 任务参数- 带采样CPU核
     */
    public static final String PARAM_CPU_MASK = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_cpuMask");
    /**
     * 任务参数- HPC 采样类型 preset
     * 采样类型 default/ top-down /instruction-mix
     */
    public static final String PARAM_HPC_SAMPLE_TYPE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType");
    /**
     * 任务参数- HPC 采样类型-总览
     */
    public static final String PARAM_SAMPLE_TYPE_HPC_SUMMARY = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_summary");
    /**
     * 任务参数- HPC 采样类型-HPC Top-Down
     */
    public static final String PARAM_SAMPLE_TYPE_HPC_TOP_DOWN = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_topDown");
    /**
     * 任务参数- HPC 采样类型-指令分布
     */
    public static final String PARAM_SAMPLE_TYPE_HPC_INSTRUCT = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_instruct");
    /**
     * 任务参数- HPC OpenMP参数  openMpParam
     */
    public static final String PARAM_HPC_OPENMP_PARAM = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_openMpParam");
    /**
     * 任务参数- HPC MPI
     */
    public static final String PARAM_HPC_MPI = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_mpi");
    /**
     * 任务参数- HPC MPI命令所在目录
     */
    public static final String PARAM_HPC_MPI_DIR = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_mpiDir");
    /**
     * 任务参数- HPC MPI Rank
     */
    public static final String PARAM_HPC_MPI_RANK = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_sampleType_hpc_mpiRank");
    /**
     * 任务参数- 配置指定节点参数 Configure Node Parameters
     */
    public static final String PARAM_CONFIG_NODE_PARAM = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_configNodeParam");
    /**
     * 任务参数- 配置指定节点参数 配置参数
     */
    public static final String PARAM_CONFIG_NODE_PARAM_CONFIG = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_configNodeParam_config");

    /**
     * 任务参数- 配置指定节点参数 已配置
     */
    public static final String PARAM_CONFIG_NODE_PARAM_STATUS_TRUE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_configNodeParam_statusTrue");
    /**
     * 任务参数- 配置指定节点参数 未配置
     */
    public static final String PARAM_CONFIG_NODE_PARAM_STATUS_FALSE = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_configNodeParam_statusFalse");
    /**
     * 任务参数- 锁与等待-标准函数
     */
    public static final String PARAM_PARAM_STANDARD_FUNCTION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_standard_function");
    /**
     * 任务参数- 锁与等待-选择函数
     */
    public static final String PARAM_PARAM_STANDARD_FUNCTION_SELECT = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_standard_function_select");

    /**
     * 任务参数- 锁与等待-选择函数
     */
    public static final String PARAM_PARAM_STANDARD_FUNCTION_CUSTOMIZED = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_standard_function_customized");

    /**
     * 任务参数-  采集线程信息
     */
    public static final String PARAM_THREAD_COLLECTION = I18NServer.toLocale(
            "plugins_hyper_tuner_task_param_process_threadCollection");

    /**
     * 任务参数-路径校验 正则
     */
    public static final String PARAM_PATH_REGEX = "^([/][^/]+)*$";

    /**
     * 任务参数 PID 校验正则
     */
    public static final String PARAM_PID_REGEX = "^([0-9]+,?)+$";
    /**
     * 任务参数-应用路径 校验正则
     */
    public static final String PARAM_APP_DIR_REGEX = "^/opt/[\\S]+";

    /**
     * 任务参数 -待采样CPU核 校验正则1
     */
    public static final String PARAM_CPU_MASK_REGEX = "^\\d+([\\-,]\\d+)*$";
    /**
     * 任务参数 -待采样CPU核 校验正则2
     */
    public static final String PARAM_CPU_MASK_REGEX2 = "^\\d+-\\d+-\\d$";

}

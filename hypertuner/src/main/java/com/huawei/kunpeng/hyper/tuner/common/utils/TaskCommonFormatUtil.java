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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;

import java.util.HashMap;

/**
 * 任务格式化公共工具类
 *
 * @since 2021-5-12
 */
public class TaskCommonFormatUtil {
    /**
     * 访存统计分析
     */
    protected static final String MEM_ACCESS = "mem_access";
    /**
     * Miss事件统计
     */
    protected static final String MISS_EVENT = "miss_event";
    /**
     * 伪共享分析
     */
    protected static final String FALSE_SHARING = "falsesharing";
    /**
     * 分析对象：系统
     */
    protected static final String TARGET_SYSTEM = "Profile System";
    /**
     * 分析对象：应用1(Launch Application)
     */
    protected static final String TARGET_APP_LAUNCH = "Launch Application";
    /**
     * 分析对象：应用2(Attach to Process)
     */
    protected static final String TARGET_APP_PROCESS = "Attach to Process";
    /**
     * 内部对象分析对象字段：系统(sys)
     */
    protected static final String TASK_PARAM_TARGET_SYS = "sys";
    /**
     * 部对象分析对象字段：应用(app)
     */
    protected static final String TASK_PARAM_TARGET_APP = "app";


    /**
     * 访存分析任务类型 analysisTarget 判断
     * 包含 预约任务；任务模板；导入导出任务类型
     *
     * @param beanAnaType         分析类型
     * @param taskParamBeanTarget 分析对象 (sys/app)
     * @return analysisTarget 分析对象
     */
    public static String getAnalysisTarget(String beanAnaType, String taskParamBeanTarget) {
        switch (beanAnaType) {
            case MEM_ACCESS:
                // 访存分析类型
                return TARGET_SYSTEM;
            case MISS_EVENT:
                // Miss 事件分析
                return getTargetByTaskParamTarget(taskParamBeanTarget);
            case FALSE_SHARING:
                // 伪共享分析
                return getTargetByTaskParamTarget(taskParamBeanTarget);
            default:
                return beanAnaType;
        }
    }

    private static String getTargetByTaskParamTarget(String taskParamBeanTarget) {
        if (TASK_PARAM_TARGET_SYS.equals(taskParamBeanTarget)) {
            return TARGET_SYSTEM;
        } else if (TASK_PARAM_TARGET_APP.equals(taskParamBeanTarget)) {
            return TARGET_APP_LAUNCH;
        } else {
            return TARGET_APP_PROCESS;
        }
    }


    /**
     * 分析对象 格式化：
     * 应用
     * 系统
     *
     * @param analysisTarget 带格式化的分析对象
     * @return 格式化后的分析对象
     */
    public static String analysisTargetFormat(String analysisTarget) {
        if (analysisTarget == null) {
            return TaskManageContent.FORMAT_ERROR;
        }
        String analysisTargetFormat = "";
        switch (analysisTarget) {
            case TaskManageContent.TARGET_APP_LAUNCH_APPLICATION:
                // $FALL-THROUGH$
            case TaskManageContent.TARGET_APP_ATTACH_TO_PROCESS:
                analysisTargetFormat = TaskManageContent.ANALYSIS_TARGET_APP;
                break;
            case TaskManageContent.TARGET_APP_PROFILE_SYSTEM:
                // $FALL-THROUGH$
            case TaskManageContent.TARGET_APP_SYSTEM:
                analysisTargetFormat = TaskManageContent.ANALYSIS_TARGET_SYS;
                break;
            default:
                analysisTargetFormat = analysisTarget;
        }
        return analysisTargetFormat;
    }


    /**
     * 分析类型 格式化：
     * 全景分析
     * 微架构分析
     * 访存分析（包含：访存统计；miss事件；伪共享；）
     * I/O分析
     * 进程/进程性能分析
     * 热点函数分析（原为 C/C++性能分析）
     * 资源调度分析
     * 锁与等待分析
     * HPC分析
     *
     * @param analysisType 分析类型
     * @return 分析类型
     */
    public static String analysisTypeFormat(String analysisType) {
        if (analysisType == null) {
            return TaskManageContent.FORMAT_ERROR;
        }
        String analysisTypeFormat;
        switch (analysisType) {
            // 全景分析
            case "system":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_OVERALL;
                break;
            case "microarchitecture":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_MICRO;
                break;
            case "mem_access":
                // $FALL-THROUGH$
            case "miss_event":
                // $FALL-THROUGH$
            case "falsesharing":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS;
                break;
            case "ioperformance":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_IO;
                break;
            case "process-thread-analysis":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_PROCESS;
                break;
            case "C/C++ Program":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_CPP;
                break;
            case "resource_schedule":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_RES_SCH;
                break;
            case "system_lock":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_LOCK;
                break;
            case "hpc_analysis":
                analysisTypeFormat = TaskManageContent.ANALYSIS_TYPE_HPC;
                break;
            default:
                analysisTypeFormat = analysisType;
        }
        return analysisTypeFormat;
    }

    /**
     * 获取 访存分析类型下的访存分析类型
     *
     * @param analysisType 接口返回的访存分析类型
     * @return 格式化后的值
     */
    public static String getAccessAnalysisType(String analysisType) {
        if (MEM_ACCESS.equals(analysisType)) {
            return TaskManageContent.ANALYSIS_TYPE_MEM_ACCESS_STATISTICS;
        } else if (MISS_EVENT.equals(analysisType)) {
            return TaskManageContent.ANALYSIS_TYPE_MISS_EVENT;
        } else if (FALSE_SHARING.equals(analysisType)) {
            return TaskManageContent.ANALYSIS_TYPE_FALSE_SHARE;
        } else {
            return analysisType;
        }
    }

    /**
     * 将true false 格式化为 开启关闭
     *
     * @param boolStr bool
     * @return 国际化的值
     */
    public static String booleFormat(String boolStr) {
        if ("true".equals(boolStr)) {
            return TaskManageContent.PARAM_TRUE;
        } else if ("false".equals(boolStr)) {
            return TaskManageContent.PARAM_FALSE;
        } else if ("enable".equals(boolStr)) {
            return TaskManageContent.PARAM_TRUE;
        } else if ("disable".equals(boolStr)) {
            return TaskManageContent.PARAM_FALSE;
        } else {
            return boolStr;
        }
    }

    /**
     * 获取 任务信息 字段名称 国际化Map
     *
     * @return TaskInfoParamFiledI18NMap
     */
    public static HashMap<String, String> getTaskInfoParamFiledI18NMap() {
        HashMap<String, String> i18NMap = new HashMap<>();
        i18NMap.put("interval", TaskManageContent.PARAM_INTERVAL_S);
        i18NMap.put("duration", TaskManageContent.PARAM_DURATION);
        i18NMap.put("kcore", TaskManageContent.PARAM_ASSOCIATE_LOCATION);
        i18NMap.put("thread", TaskManageContent.PARAM_THREAD); // 采集线程信息 （进程/线程分析）
        i18NMap.put("straceAnalysis", TaskManageContent.PARAM_STRACE_ANALYSIS); // 跟踪系统调用 （进程线程分析）

        // 延迟采样时长（s）
        i18NMap.put("samplingDelay", TaskManageContent.PARAM_SAMPLING_DELAY);
        i18NMap.put("startDelay", TaskManageContent.PARAM_SAMPLING_DELAY);
        i18NMap.put("applicationPath", TaskManageContent.PARAM_APPLICATION_PATH);
        i18NMap.put("appDir", TaskManageContent.PARAM_APPLICATION_PATH); // TaskInfoBean 应用路径
        i18NMap.put("appParameters", TaskManageContent.PARAM_APPLICATION_PARAM); // TaskInfoBean 应用参数
        i18NMap.put("statistical", TaskManageContent.PARAM_STATISTICAL);
        i18NMap.put("functionname", TaskManageContent.PARAM_ANALYSIS_FUNCTION);
        i18NMap.put("assemblyLocation", TaskManageContent.PARAM_ASSEMBLY_LOCATION);
        i18NMap.put("cpuMask", TaskManageContent.PARAM_CPU_CORE);
        i18NMap.put("sourceLocation", TaskManageContent.PARAM_SOURCE_LOCATION);
        i18NMap.put("metrics", TaskManageContent.PARAM_ANALYSIS_METRICS);
        i18NMap.put("application", TaskManageContent.PARAM_APPLICATION_PARAM);
        i18NMap.put("space", TaskManageContent.PARAM_SPACE);
        i18NMap.put("samplingSpace", TaskManageContent.PARAM_SPACE);
        i18NMap.put("topCheck", TaskManageContent.PARAM_TOP_CHECK);
        i18NMap.put("analysisIndex", TaskManageContent.PARAM_ANALYSIS_INDEX); // 分析指标
        i18NMap.put("type", TaskManageContent.PARAM_SAMPLE_TYPE);
        i18NMap.put("period", TaskManageContent.PARAM_PERIOD); // 采样间隔（指令数）
        i18NMap.put("pid", TaskManageContent.PARAM_PID);
        i18NMap.put("targetPid", TaskManageContent.PARAM_PID);
        i18NMap.put("processName", TaskManageContent.PARAM_PROCESS_NAME);

        // 采集调用栈
        i18NMap.put("disCallstack", TaskManageContent.PARAM_STACK);
        i18NMap.put("stack", TaskManageContent.PARAM_STACK);

        // 采样范围
        i18NMap.put("samplingMode", TaskManageContent.PARAM_SAMPLING_MODE);
        i18NMap.put("collectRange", TaskManageContent.PARAM_SAMPLING_MODE);

        // 采样文件大小
        i18NMap.put("size", TaskManageContent.PARAM_FILE_SIZE);
        i18NMap.put("fileSize", TaskManageContent.PARAM_FILE_SIZE);
        i18NMap.put("filesize", TaskManageContent.PARAM_FILE_SIZE);
        i18NMap.put("perfDataLimit", TaskManageContent.PARAM_FILE_SIZE);
        i18NMap.put("collectFileSize", TaskManageContent.PARAM_FILE_SIZE);

        // HPC
        i18NMap.put("preset", TaskManageContent.PARAM_HPC_SAMPLE_TYPE); // 采样类型
        i18NMap.put("openMpParam", TaskManageContent.PARAM_HPC_OPENMP_PARAM); // openMp参数
        i18NMap.put("mpiStatus", TaskManageContent.PARAM_HPC_MPI); // MPI
        i18NMap.put("mpiEnvDir", TaskManageContent.PARAM_HPC_MPI_DIR); // MPI命令所在目录
        i18NMap.put("rank", TaskManageContent.PARAM_HPC_MPI_RANK); // Rank
        return i18NMap;
    }

    /**
     * 获取多选框 选项国际化 和接口数据的映射，用于将用户选择的值转化为接口传递的值
     * 注意：当变量名重复时，不能在此处添加，应单独处理
     *
     * @return 多选框 选项国际化map
     */
    public static HashMap<String, String> getSelectMap() {
        HashMap<String, String> i18NMap = new HashMap<>();
        i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_CPU, "cpu");
        i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_MEM, "mem");
        i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_DISK, "disk");
        i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_CONTEXT, "context");
        i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_DDR, "ddr_access");
        i18NMap.put(TaskManageContent.PARAM_SAMPLE_TYPE_CACHE, "cache_access");

        // Miss事件 指令类型
        i18NMap.put(TaskManageContent.PARAM_MEM_MISS_METRICS_LLC, "llcMiss");
        i18NMap.put(TaskManageContent.PARAM_MEM_MISS_METRICS_TLB, "tlbMiss");
        i18NMap.put(TaskManageContent.PARAM_MEM_MISS_METRICS_REMOTE_ACCESS, "remoteAccess");
        i18NMap.put(TaskManageContent.PARAM_MEM_MISS_METRICS_LONG_LATENCY_LOAD, "longLatencyLoad");

        // 微架构分析 分析指标 analysisIndex
        i18NMap.put(TaskManageContent.PARAM_ANALYSIS_INDEX_BAD, "badSpeculation");
        i18NMap.put(TaskManageContent.PARAM_ANALYSIS_INDEX_FRONT, "frontEndBound");
        i18NMap.put(TaskManageContent.PARAM_ANALYSIS_INDEX_RESOURCE, "resourceBound");
        i18NMap.put(TaskManageContent.PARAM_ANALYSIS_INDEX_CORE, "coreBound");
        i18NMap.put(TaskManageContent.PARAM_ANALYSIS_INDEX_MEMORY, "memoryBound");

        // 微架构分析 采样模式 sampleMode   detail/summary  与hpc 的采样类型 summary 冲突，请注意
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_MODE_SUMMARY, "summary");
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_MODE_DETAIL, "detail");
        return i18NMap;
    }

    /**
     * 获取 任务信息 字段值 国际化Map
     *
     * @return TaskInfoParamValueI18NMap
     */
    public static HashMap<String, String> getTaskInfoParamValueI18NMap() {
        HashMap<String, String> i18NMap = new HashMap<>();
        i18NMap.put("cpu", TaskManageContent.PARAM_SAMPLE_TYPE_CPU);
        i18NMap.put("mem", TaskManageContent.PARAM_SAMPLE_TYPE_MEM);
        i18NMap.put("disk", TaskManageContent.PARAM_SAMPLE_TYPE_DISK);
        i18NMap.put("context", TaskManageContent.PARAM_SAMPLE_TYPE_CONTEXT);
        i18NMap.put("ddr_access", TaskManageContent.PARAM_SAMPLE_TYPE_DDR);
        i18NMap.put("cache_access", TaskManageContent.PARAM_SAMPLE_TYPE_CACHE);
        i18NMap.put("true", TaskManageContent.PARAM_TRUE);
        i18NMap.put("enable", TaskManageContent.PARAM_TRUE);
        i18NMap.put("summary", TaskManageContent.PARAM_SAMPLE_TYPE_SUMMARY);
        i18NMap.put("detail", TaskManageContent.PARAM_SAMPLING_MODE_DETAIL);

        // 伪共享 采样范围 Sampling Range => samplingSpace
        i18NMap.put("all-user", TaskManageContent.PARAM_SAMPLING_RANGE_ALL); // 用户态
        i18NMap.put("all-kernel", TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL); // 内核态

        // 锁与等待 采样范围  Sampling Range => collectRange
        i18NMap.put("ALL", TaskManageContent.PARAM_SAMPLING_RANGE_ALL); // 全部
        i18NMap.put("USER", TaskManageContent.PARAM_SAMPLING_RANGE_USER); // 用户态
        i18NMap.put("SYS", TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL); // 内核态

        // C/C++ 采样范围 Sampling Range => samplingSpace
        i18NMap.put("all", TaskManageContent.PARAM_SAMPLING_RANGE_ALL); // 全部
        i18NMap.put("user", TaskManageContent.PARAM_SAMPLING_RANGE_USER); // 用户态 id
        i18NMap.put("kernel", TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL); // 内核态 id

        // Miss事件 指令类型
        i18NMap.put("llcMiss", TaskManageContent.PARAM_MEM_MISS_METRICS_LLC);
        i18NMap.put("tlbMiss", TaskManageContent.PARAM_MEM_MISS_METRICS_TLB);
        i18NMap.put("remoteAccess", TaskManageContent.PARAM_MEM_MISS_METRICS_REMOTE_ACCESS);
        i18NMap.put("longLatencyLoad", TaskManageContent.PARAM_MEM_MISS_METRICS_LONG_LATENCY_LOAD);

        // HPC 采样类型
        i18NMap.put("default", TaskManageContent.PARAM_SAMPLE_TYPE_HPC_SUMMARY);
        i18NMap.put("top-down", TaskManageContent.PARAM_SAMPLE_TYPE_HPC_TOP_DOWN);
        i18NMap.put("instruction-mix", TaskManageContent.PARAM_SAMPLE_TYPE_HPC_INSTRUCT);

        // 微架构分析 分析指标 analysisIndex
        i18NMap.put("badSpeculation", TaskManageContent.PARAM_ANALYSIS_INDEX_BAD);
        i18NMap.put("frontEndBound", TaskManageContent.PARAM_ANALYSIS_INDEX_FRONT);
        i18NMap.put("resourceBound", TaskManageContent.PARAM_ANALYSIS_INDEX_RESOURCE);
        i18NMap.put("coreBound", TaskManageContent.PARAM_ANALYSIS_INDEX_CORE);
        i18NMap.put("memoryBound", TaskManageContent.PARAM_ANALYSIS_INDEX_MEMORY);
        i18NMap.put("process", TaskManageContent.PARAM_THREAD_COLLECTION);
        return i18NMap;
    }

    /**
     * 获取多选框 选项国际化 和接口数据的映射，用于将用户选择的值转化为接口传递的值
     * 伪共享 采样范围 Sampling Range => samplingSpace
     * Miss
     *
     * @return 多选框 选项国际化map
     */
    public static HashMap<String, String> getSelectMapOf1() {
        HashMap<String, String> i18NMap = new HashMap<>();
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_ALL, "ALL"); // 用户态
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_USER, "all-user"); // 用户态
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL, "all-kernel"); // 内核态
        return i18NMap;
    }

    /**
     * 获取多选框 选项国际化 和接口数据的映射，用于将用户选择的值转化为接口传递的值
     * 锁与等待 采样范围  Sampling Range => collectRange
     *
     * @return 多选框 选项国际化map
     */
    public static HashMap<String, String> getSelectMapOf2() {
        HashMap<String, String> i18NMap = new HashMap<>();
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_ALL, "ALL"); // 全部
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_USER, "USER"); // 用户态
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL, "SYS"); // 内核态
        return i18NMap;
    }

    /**
     * 获取多选框 选项国际化 和接口数据的映射，用于将用户选择的值转化为接口传递的值
     * C/C++  采样范围 Sampling Range => samplingSpace
     * 微架构   Sampling Range
     *
     * @return 多选框 选项国际化map
     */
    public static HashMap<String, String> getSelectMapOf3() {
        HashMap<String, String> i18NMap = new HashMap<>();
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_ALL, "all"); // 所有
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_USER, "user"); // 用户态 id
        i18NMap.put(TaskManageContent.PARAM_SAMPLING_RANGE_KERNEL, "kernel"); // 内核态 id
        return i18NMap;
    }


}
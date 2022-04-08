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

package com.huawei.kunpeng.hyper.tuner.common.constant.enums;

import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.AddTargetEnvironmentEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ProfilingTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowGcLogEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowGuardianProcessEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowMemoryDumpEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowSamplingTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowThreadDumpEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.CreateProjectEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.CreateTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ImportTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ModifyProjectEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.OpenNewPageEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ReanalyzeTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ShowNodeEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ShowProjectEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.UpdataTaskEditor;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;

import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 页面类型
 *
 * @since 2020-11-16
 */
public enum PageType {
    NULL("null") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 创建工程
     */
    CREATE_PROJECT("create_project") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new CreateProjectEditor(file));
        }
    },
    CREATE_PROJECT_REPORT("create_project_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 修改工程
     */
    MODIFY_PROJECT("modify_project") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ModifyProjectEditor(file));
        }
    },
    MODIFY_PROJECT_REPORT("modify_project_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示工程
     */
    SHOW_PROJECT("show_project") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowProjectEditor(file));
        }
    },
    SHOW_PROJECT_REPORT("show_project_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示工程
     */
    SHOW_NODE("show_node") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowNodeEditor(file));
        }
    },
    SHOW_NODE_REPORT("show_node_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 创建任务
     */
    CREATE_TASK("create_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new CreateTaskEditor(file));
        }
    },
    CREATE_TASK_REPORT("modify_task_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 修改任务
     */
    MODIFY_TASK("modify_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new UpdataTaskEditor(file));
        }
    },
    MODIFY_TASK_REPORT("modify_task_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 重新分析任务
     */
    REANALYZE_TASK("reanalyze_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ReanalyzeTaskEditor(file));
        }
    },
    REANALYZE_TASK_REPORT("reanalyze_task_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 导入任务
     */
    IMPORT_TASK("import_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ImportTaskEditor(file));
        }
    },
    IMPORT_TASK_REPORT("import_task_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 导出任务
     */
    EXPORT_TASK("export_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },
    EXPORT_TASK_REPORT("MODIFY_PROJECT_REPORT") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 函数详细信息
     */
    FUNCTION_INFO("Function_Info") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new OpenNewPageEditor(file));
        }
    },
    FUNCTION_INFO_REPORT("Function_Info_REPORT") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示目标环境进程
     */
    SHOW_GUARDIAN_PROCESS("show_guardian_process") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowGuardianProcessEditor(file));
        }
    },
    SHOW_GUARDIAN_PROCESS_REPORT("show_guardian_process_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示采样分析任务结果
     */
    SHOW_SAMPLING_TASK("show_sampling_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowSamplingTaskEditor(file));
        }
    },
    SHOW_SAMPLING_TASK_REPORT("show_sampling_task_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示采样分析任务结果
     */
    SHOW_PROFILING_TASK("show_profiling_task") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ProfilingTaskEditor(file));
        }
    },
    SHOW_PROFILING_TASK_REPORT("show_profiling_task_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示内存转储
     */
    SHOW_MEMORY_DUMP("show_memory_dump") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowMemoryDumpEditor(file));
        }
    },
    SHOW_MEMORY_DUMP_REPORT("show_memory_dump_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示GC LOG
     */
    SHOW_GC_LOG("show_gc_log") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowGcLogEditor(file));
        }
    },
    SHOW_GC_LOG_REPORT("show_gc_log_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 展示线程转储
     */
    SHOW_THREAD_DUMP("show_thread_dump") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new ShowThreadDumpEditor(file));
        }
    },
    SHOW_THREAD_DUMP_REPORT("show_thread_dump_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    },

    /**
     * 添加目标环境
     */
    ADD_TARGET_ENVIRONMENT("add_target_environment") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.of(new AddTargetEnvironmentEditor(file));
        }
    },
    ADD_TARGET_ENVIRONMENT_REPORT("add_target_environment_report") {
        @Override
        public Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file) {
            return Optional.empty();
        }
    };

    private final String type;

    PageType(String type) {
        this.type = type;
    }

    /**
     * 通过延伸信息value获取PageType类的一个枚举实例
     *
     * @param value 值
     * @return String
     */
    public static PageType getStatusByValue(String value) {
        for (PageType pageType : PageType.values()) {
            if (pageType.value().equals(value)) {
                return pageType;
            }
        }

        return PageType.NULL;
    }

    /**
     * 抽象函数 获取WebFileEditor
     *
     * @param file VirtualFile
     * @return WebFileEditor
     */
    public abstract Optional<WebFileEditor> getWebFileEditor(@NotNull VirtualFile file);

    /**
     * 获取函数名
     *
     * @return String
     */
    public String value() {
        return type;
    }
}

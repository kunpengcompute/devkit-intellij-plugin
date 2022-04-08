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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.base;

import com.huawei.kunpeng.hyper.tuner.model.sysperf.SchTaskBean;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.TaskTemplateBean;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.CppHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.FalseHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.HpcHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.IoHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.LockHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MemAccessHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MicroHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.MissEventHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.OverAllHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.ProcessHandle;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.schtaskutil.ResHandle;

import javax.swing.JPanel;

/**
 * 分析任务类型 枚举
 *
 * @since 2021-9-9
 */
public enum SchTaskAnalysisTypeEnum {
    NULL("null") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new SchTaskPanelHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new SchTaskPanelHandle(otherInfoPanel, templateItem);
        }
    },
    OVERALL("system") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new OverAllHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new OverAllHandle(otherInfoPanel, templateItem);
        }
    },
    MICRO("microarchitecture") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new MicroHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new MicroHandle(otherInfoPanel, templateItem);
        }
    },
    MEM_ACCESS("mem_access") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new MemAccessHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new MemAccessHandle(otherInfoPanel, templateItem);
        }
    },
    MISS_EVENT("miss_event") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new SchTaskPanelHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new MissEventHandle(otherInfoPanel, templateItem);
        }
    },
    FALSE_SHARE("falsesharing") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new FalseHandle(otherInfoPanel, schTaskItem, null, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new FalseHandle(otherInfoPanel, templateItem);
        }
    },
    IO_PREFORM("ioperformance") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new IoHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new IoHandle(otherInfoPanel, templateItem);
        }
    },
    PROCESS("process-thread-analysis") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new ProcessHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new ProcessHandle(otherInfoPanel, templateItem);
        }
    },
    CPP("C/C++ Program") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new CppHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new CppHandle(otherInfoPanel, templateItem);
        }
    },
    RESOURCE("resource_schedule") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new ResHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new ResHandle(otherInfoPanel, templateItem);
        }
    },
    LOCK("system_lock") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new LockHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new LockHandle(otherInfoPanel, templateItem);
        }
    },
    HPC("hpc_analysis") {
        @Override
        public SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem) {
            return new HpcHandle(otherInfoPanel, schTaskItem, null);
        }

        @Override
        public SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem) {
            return new HpcHandle(otherInfoPanel, templateItem);
        }
    };

    private final String analysisType;

    SchTaskAnalysisTypeEnum(String analysisType) {
        this.analysisType = analysisType;
    }

    /**
     * 通过延伸信息value获取 SchTaskAnalysisTypeEnum 类的一个枚举实例
     *
     * @param value 值
     * @return SchTaskAnalysisTypeEnum  的 一个枚举实例
     */
    public static SchTaskAnalysisTypeEnum getType(String value) {
        for (SchTaskAnalysisTypeEnum panelTypeEnum : SchTaskAnalysisTypeEnum.values()) {
            if (panelTypeEnum.value().equals(value)) {
                return panelTypeEnum;
            }
        }
        return SchTaskAnalysisTypeEnum.NULL;
    }

    /**
     * 获取函数名
     *
     * @return String
     */
    public String value() {
        return analysisType;
    }

    /**
     * 获取枚举类型对应的 panel 添加工具类 （预约任务详情页面）
     *
     * @param otherInfoPanel 要添加的目标面板
     * @param schTaskItem    待展示的参数
     * @return panel 添加工具类
     */
    public abstract SchTaskPanelHandle getPanelHandle(JPanel otherInfoPanel, SchTaskBean schTaskItem);

    /**
     * 获取枚举类型对应的 panel 添加工具类 （任务模板详情页面）
     *
     * @param otherInfoPanel 要添加的目标面板
     * @param templateItem   任务模板待展示的参数
     * @return panel 添加工具类
     */
    public abstract SchTaskPanelHandle getTempDetailPanelHandle(JPanel otherInfoPanel, TaskTemplateBean templateItem);
}

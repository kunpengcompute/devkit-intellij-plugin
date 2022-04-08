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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.AgentCertContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysPrefLogContent;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskTemplateContent;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 显示工具窗口 (ToolWindows)  菜单栏(Options Menu) 中的
 * 设置栏下属子菜单 鲲鹏性能分析栏 下属子菜单 枚举
 *
 * @since 2021-6-26
 */
public enum SysPerfActionEnum {
    /**
     * 节点管理
     */
    MODE_MANAGE(NodeManagerContent.NODE_MANAGER_DIC, true, false,
            new AnAction(NodeManagerContent.NODE_MANAGER_DIC) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), NodeManagerContent.NODE_MANAGER_DIC);
                }
            }),

    /**
     * 预约任务管理
     */
    SCHEDULE_TASK_MANAGE(SchTaskContent.SCH_TASK_DIC, true, false,
            new AnAction(SchTaskContent.SCH_TASK_DIC) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), SchTaskContent.SCH_TASK_DIC);
                }
            }),

    /**
     * 导入导出任务管理
     */
    IMP_AND_EXP_TASK_MANAGE(ImpAndExpTaskContent.TASK_DIC, true, false,
            new AnAction(ImpAndExpTaskContent.TASK_DIC) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), ImpAndExpTaskContent.TASK_DIC);
                }
            }),

    /**
     * 日志管理
     */
    TASK_TEMPLATE_MANAGE(TaskTemplateContent.TASK_TEMPLATE_DIC, true, false,
            new AnAction(TaskTemplateContent.TASK_TEMPLATE_DIC) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), TaskTemplateContent.TASK_TEMPLATE_DIC);
                }
            }),

    /**
     * 日志管理
     */
    SYS_PERF_LOG_MANAGE(SysPrefLogContent.DISPLAY_NAME, true, false,
            new AnAction(SysPrefLogContent.DISPLAY_NAME) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), SysPrefLogContent.DISPLAY_NAME);
                }
            }),

    /**
     * Agent 任务模板管理
     */
    AGENT_CERT_MANAGE(
            AgentCertContent.AGENT_CERT_DIC, true, true,
            new AnAction(AgentCertContent.AGENT_CERT_DIC) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), AgentCertContent.AGENT_CERT_DIC);
                }
            });

    /**
     * 显示名称
     */
    private String name;

    /**
     * 显示动作
     */
    private AnAction anAction;

    /**
     * 是否普通用户需要
     */
    private boolean isComUserNeed;

    /**
     * 是否需要添加分隔符
     */
    private boolean isAddSeparator = false;

    /**
     * 是否管理员用户需要
     */
    private boolean isAdminUserNeed = true;

    /**
     * 构造函数
     *
     * @param name            显示名称
     * @param isComUserNeed   是否普通用户需要
     * @param isAddSeparator  是否需要添加分隔符
     * @param isAdminUserNeed 是否管理员用户需要
     * @param anAction        显示动作
     */
    SysPerfActionEnum(
            String name, boolean isComUserNeed, boolean isAddSeparator, boolean isAdminUserNeed, AnAction anAction) {
        this.name = name;
        this.isComUserNeed = isComUserNeed;
        this.isAddSeparator = isAddSeparator;
        this.isAdminUserNeed = isAdminUserNeed;
        this.anAction = anAction;
    }

    /**
     * 构造函数 （默认管理员用户需要，不添加分隔符）
     *
     * @param name          显示名称
     * @param isComUserNeed 是否普通用户需要
     * @param anAction      显示动作
     */
    SysPerfActionEnum(String name, boolean isComUserNeed, AnAction anAction) {
        this(name, isComUserNeed, false, true, anAction);
    }

    /**
     * 构造函数 （默认管理员用户需要）
     *
     * @param name           显示名称
     * @param isComUserNeed  是否普通用户需要
     * @param isAddSeparator 是否需要添加分隔符
     * @param anAction       显示动作
     */
    SysPerfActionEnum(String name, boolean isComUserNeed, boolean isAddSeparator, AnAction anAction) {
        this(name, isComUserNeed, isAddSeparator, true, anAction);
    }

    /**
     * Name
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * anAction
     *
     * @return anAction
     */
    public AnAction getAnAction() {
        return anAction;
    }

    /**
     * anAction
     *
     * @param anAction anAction
     */
    public void setAnAction(AnAction anAction) {
        this.anAction = anAction;
    }

    /**
     * isComUserNeed
     *
     * @return isComUserNeed
     */
    public boolean isComUserNeed() {
        return isComUserNeed;
    }

    /**
     * comUserNeed
     *
     * @param comUserNeed comUserNeed
     */
    public void setComUserNeed(boolean comUserNeed) {
        isComUserNeed = comUserNeed;
    }

    /**
     * isAddSeparator
     *
     * @return isAddSeparator
     */
    public boolean isAddSeparator() {
        return isAddSeparator;
    }

    /**
     * addSeparator
     *
     * @param addSeparator addSeparator
     */
    public void setAddSeparator(boolean addSeparator) {
        isAddSeparator = addSeparator;
    }

    /**
     * isAdminUserNeed
     *
     * @return isAdminUserNeed
     */
    public boolean isAdminUserNeed() {
        return isAdminUserNeed;
    }

    /**
     * adminUserNeed
     *
     * @param adminUserNeed adminUserNeed
     */
    public void setAdminUserNeed(boolean adminUserNeed) {
        isAdminUserNeed = adminUserNeed;
    }
}

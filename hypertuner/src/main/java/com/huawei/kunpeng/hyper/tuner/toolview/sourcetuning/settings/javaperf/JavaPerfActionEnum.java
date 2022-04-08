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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.InternalCommCertConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaPerfLogsConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.ReportThresholdConstant;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 显示工具窗口 (ToolWindows)  菜单栏(Options Menu) 中的
 * 设置栏下属子菜单 Java性能分析栏 下属子菜单 枚举
 *
 * @since 2021-7-7
 */
public enum JavaPerfActionEnum {
    /**
     * 目标环境管理
     */
    DISPLAY_NAME(GuardianMangerConstant.DISPLAY_NAME, true, false,
            new AnAction(GuardianMangerConstant.DISPLAY_NAME) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), GuardianMangerConstant.DISPLAY_NAME);
                }
            }),
    /**
     * 内部通信证书
     */
    INTERNAL_COMM_CERT(InternalCommCertConstant.DISPLAY_NAME, false, false,
            new AnAction(InternalCommCertConstant.DISPLAY_NAME) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), InternalCommCertConstant.DISPLAY_NAME);
                }
            }),
    /**
     * 日志
     */
    JAVA_PERF_LOGS(JavaPerfLogsConstant.DISPLAY_NAME, true, false,
            new AnAction(JavaPerfLogsConstant.DISPLAY_NAME) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(CommonUtil.getDefaultProject(), JavaPerfLogsConstant.DISPLAY_NAME);
                }
            }),
    /**
     * java性能分析配置
     */
    JAVA_PROFILER_SETTINGS(JavaProviderSettingConstant.JAVA_PROFILER_SETTINGS, true, false,
            new AnAction(JavaProviderSettingConstant.JAVA_PROFILER_SETTINGS) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(
                                    CommonUtil.getDefaultProject(), JavaProviderSettingConstant.JAVA_PROFILER_SETTINGS);
                }
            }),
    /**
     * 工作密钥
     */
    WORKING_KEY(JavaProviderSettingConstant.WORKING_KEY, false, false,
            new AnAction(JavaProviderSettingConstant.WORKING_KEY) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(
                                    CommonUtil.getDefaultProject(), JavaProviderSettingConstant.WORKING_KEY);
                }
            }),
    /**
     * 报告阈值
     */
    REPORT_THRESHOLD_TITLE(ReportThresholdConstant.REPORT_THRESHOLD_TITLE, true, false,
            new AnAction(ReportThresholdConstant.REPORT_THRESHOLD_TITLE) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    ShowSettingsUtil.getInstance()
                            .showSettingsDialog(
                                    CommonUtil.getDefaultProject(), ReportThresholdConstant.REPORT_THRESHOLD_TITLE);
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
    JavaPerfActionEnum(
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
    JavaPerfActionEnum(String name, boolean isComUserNeed, AnAction anAction) {
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
    JavaPerfActionEnum(String name, boolean isComUserNeed, boolean isAddSeparator, AnAction anAction) {
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

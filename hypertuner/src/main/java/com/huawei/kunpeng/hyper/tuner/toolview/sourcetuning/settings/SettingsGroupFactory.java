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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.settings;

import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.JavaPerfActionGroup;
import com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning.SysPerfActionGroup;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置项中action工程类
 *
 * @since v2.3.t20
 */
public class SettingsGroupFactory {
    /**
     * 根据Hyper Tuner安装信息提供action列表
     *
     * @param installation 安装信息
     * @return action列表
     */
    @NotNull
    public static List<AnAction> createActionsByInstallation(String installation) {
        List<AnAction> list = new ArrayList<>();
        if ("all".equals(installation)) {
            list.add(new SysPerfActionGroup());
            list.add(new JavaPerfActionGroup());
        } else if ("sys_perf".equals(installation)) {
            list.add(new SysPerfActionGroup());
        } else if ("java_perf".equals(installation)) {
            list.add(new JavaPerfActionGroup());
        } else {
            Logger.info("No Install Info");
        }
        return list;
    }
}

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

package com.huawei.kunpeng.porting.process;

import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;

import java.util.ArrayList;

/**
 * IDE插件任务进度条基类
 *
 * @since 2020-09-25
 */
public class PortingIDETask {
    /**
     * 请求任务进度接口地址
     */
    public static final String PROCESS_URL = "/task/progress/?task_type=%s&task_id=%s";

    /**
     * 白名单管理任务类型
     */
    public static final String WHITE_LIST_MANAGE_TASK_TYPE = "2";

    /**
     * 软件迁移模板任务类型
     */
    public static final String PORTING_TEMPLATE_TASK_TYPE = "4";

    /**
     * 任务进度字段
     */
    public static final String PROGRESS = "progress";

    /**
     * 任务状态字段
     */
    public static final String RUNNING_STATUS = "runningstatus";

    /**
     * 任务数量
     */
    public static final int TASK_NUM = 8;

    /**
     * 判断当前用户是否可以分析指定类型的任务
     *
     * @param taskType 任务类型
     * @return true:没有指定类型任务，可以分析
     */
    public static synchronized boolean isRunTask(String taskType) {
        ArrayList<String> tasks = IDEBaseTask.getTaskInfo()
            .computeIfAbsent(PortingUserInfoContext.getInstance().getUserName(),
                function -> new ArrayList<>(TASK_NUM));
        for (String task : tasks) {
            if (taskType.equals(task)) {
                return false;
            }
        }
        tasks.add(taskType);
        return true;
    }

    /**
     * 删除当前用户指定类型的任务
     *
     * @param taskType 任务类型
     */
    public static synchronized void deleteRunTask(String taskType) {
        ArrayList<String> tasks = IDEBaseTask.getTaskInfo().get(PortingUserInfoContext.getInstance().getUserName());
        if (tasks == null) {
            return;
        }
        for (String task : tasks) {
            if (taskType.equals(task)) {
                tasks.remove(task);
                return;
            }
        }
    }

    /**
     * 清除当前用户所有任务（当用户退出时调用）
     */
    public static void deleteAllRunTaskByUser() {
        IDEBaseTask.getTaskInfo().remove(PortingUserInfoContext.getInstance().getUserName());
    }
}

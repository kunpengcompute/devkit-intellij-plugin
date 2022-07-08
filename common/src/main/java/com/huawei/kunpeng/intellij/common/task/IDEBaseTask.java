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

package com.huawei.kunpeng.intellij.common.task;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.DumbModeTask;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * IDE插件任务进度条基类
 *
 * @since 1.0.0
 */
public abstract class IDEBaseTask {
    /**
     * 任务数量
     */
    private static int taskNum = 8;

    /**
     * 当前正在进行中的任务
     */
    private static final HashMap<String, ArrayList<String>> TASK_INFO = new HashMap<>();

    /**
     * 任务是否成功，退出登录或其他处登录导致未登录，在运行的任务isTaskSuccess=false
     */
    protected boolean isTaskSuccess = true;

    /**
     * 任务执行时的方法
     *
     * @param indicator 自定义参数
     */
    protected abstract void runTask(ProgressIndicator indicator);

    /**
     * 任务取消后的动作
     *
     * @param indicator 自定义参数
     */
    protected abstract void cancel(ProgressIndicator indicator);

    /**
     * 任务执行成功后的动作
     *
     * @param indicator 自定义参数
     */
    protected abstract void success(ProgressIndicator indicator);

    /**
     * 带暂停的后台任务，右下角
     *
     * @param project 所属项目
     * @param title 进度条名称
     * @param task 需要执行的具体任务
     */
    public void progressForPauseInBack(Project project, String title, IDEBaseTask task) {
        Project projectDef = (project == null) ? ProjectManager.getInstance().getDefaultProject() : project;
        DumbServiceImpl.getInstance(projectDef).queueTask(new DumbModeTask(title) {
            @Override
            public void performInDumbMode(@NotNull ProgressIndicator indicator) {
                try {
                    // 执任务
                    task.runTask(indicator);
                    Logger.info("task is running");
                } catch (ProcessCanceledException exception) {
                    // 任务非正常退出
                    task.cancel(indicator);
                }
            }
        });
    }

    /**
     * 悬浮在窗口中央的进度条
     *
     * @param project 所属项目
     * @param title 进度条名称
     * @param task 需要执行的具体任务
     * @param canBeCanceled canBeCanceled
     */
    public void processForONFore(Project project, String title, IDEBaseTask task, boolean canBeCanceled) {
        Project projectDef = (project == null) ? ProjectManager.getInstance().getDefaultProject() : project;
        final ProgressManager progress = ProgressManager.getInstance();
        progress.runProcessWithProgressSynchronously(() -> {
            taskProcessInfo(task, progress);
        }, title, canBeCanceled, projectDef);
    }

    /**
     * 依赖在某个组件上的进度条
     *
     * @param project 所属项目
     * @param parentComponent 依赖的父组件
     * @param title 进度条名称
     * @param task 需要执行的具体任务
     */
    public void processForComponent(final Project project, JComponent parentComponent, String title, IDEBaseTask task) {
        Project projectDef = (project == null) ? ProjectManager.getInstance().getDefaultProject() : project;
        final ProgressManager progress = ProgressManager.getInstance();
        progress.runProcessWithProgressSynchronously(() -> {
            taskProcessInfo(task, progress);
        }, title, true, projectDef, parentComponent);
    }

    private void taskProcessInfo(IDEBaseTask task, ProgressManager progress) {
        ProgressIndicator progressIndicator = progress.getProgressIndicator();
        try {
            // 执任务
            task.runTask(progressIndicator);
            Logger.info("running");
        } catch (ProcessCanceledException exception) {
            // 任务非正常退出
            task.cancel(progressIndicator);
        }
    }

    /**
     * 常用的执行任务的进度条，默认时在右下角
     *
     * @param project 所属项目
     * @param title 进度条名称
     * @param task 需要执行的具体任务
     */
    public void processForCommon(Project project, String title, IDEBaseTask task) {
        Project projectDef = (project == null) ? CommonUtil.getDefaultProject() : project;
        final ProgressManager progress = ProgressManager.getInstance();
        Window window = CommonUtil.getDefaultWindow();
        CommonUtil.setBackGroundProcessWindowOpen(true, window);
        progress.run(new Task.Backgroundable(projectDef, title, true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ProgressIndicator progressIndicator = progress.getProgressIndicator();
                try {
                    // 执任务
                    task.runTask(progressIndicator);
                    Logger.info("task is running");
                } catch (ProcessCanceledException exception) {
                    // 任务非正常退出
                    CommonUtil.setBackGroundProcessWindowOpen(false, window);
                    task.cancel(progressIndicator);
                    return;
                }
            }

            /**
             * 执行成功后
             */
            @Override
            public void onSuccess() {
                super.onSuccess();
                // 退出登录或其他处登录导致任务未成功完成，不执行自定义success方法。用户退出时已经清除全部任务
                if (isTaskSuccess) {
                    task.success(progress.getProgressIndicator());
                }
                CommonUtil.setBackGroundProcessWindowOpen(false, window);
                Logger.info("Runnable task execute success.");
            }

            /**
             * 取消任务后
             */
            @Override
            public void onCancel() {
                super.onCancel();
                CommonUtil.setBackGroundProcessWindowOpen(false, window);
                task.cancel(progress.getProgressIndicator());
                Logger.info("Cancel runnable task success.");
            }
        });
    }

    /**
     * 获取所有任务列表
     *
     * @return key userName, value 任务信息
     */
    public static Map<String, ArrayList<String>> getTaskInfo() {
        return TASK_INFO;
    }

    /**
     * 获取总任务数
     *
     * @return TaskNum
     */
    public static int getTaskNum() {
        return taskNum;
    }

    /**
     * 设置总任务数
     *
     * @param taskNumber taskNumber
     */
    public static void setTaskNum(int taskNumber) {
        taskNum = taskNumber;
    }
}

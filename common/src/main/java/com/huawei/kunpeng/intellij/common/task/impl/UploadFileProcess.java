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

package com.huawei.kunpeng.intellij.common.task.impl;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.UploadUtil;

import com.intellij.openapi.progress.ProgressIndicator;

import java.util.Map;

/**
 * 文件上传进度条
 *
 * @since 1.0.0
 */
public class UploadFileProcess extends IDEBaseTask {
    /**
     * 缓冲大小
     */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * 运行时参数
     */
    private Map<String, Object> paramMap;

    /**
     * 默认构造函数
     */
    public UploadFileProcess() {
        this(null);
    }

    /**
     * 推荐构造函数
     *
     * @param paramMap 运行时参数
     */
    public UploadFileProcess(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 任务执行时的方法
     *
     * @param indicator 自定义参数
     */
    @Override
    public void runTask(ProgressIndicator indicator) {
        UploadUtil.uploadFileWithProgress(paramMap, indicator);
    }

    /**
     * 任务取消后的动作
     *
     * @param indicator 自定义参数
     */
    @Override
    public void cancel(ProgressIndicator indicator) {
        Logger.error("UploadFileProcess error!");
    }

    /**
     * 任务执行成功后的动作
     *
     * @param indicator 自定义参数
     */
    @Override
    public void success(ProgressIndicator indicator) {
        Logger.error("UploadFileProcess success!");
    }
}


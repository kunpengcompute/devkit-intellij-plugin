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

package com.huawei.kunpeng.porting.webview.handler;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.process.CacheLineProgress;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * The class: CacheLineAlignmentFunctionHandler
 *
 * @since 2021/10/11
 */
public class CacheLineAlignmentFunctionHandler extends FunctionHandler {
    /**
     * show cache line alignment tasks progress
     *
     * @param message the dataBean from webView
     * @param module  module
     */
    public void cacheLineProgress(@NotNull MessageBean message, String module) {
        Map<String, String> beanMap = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = beanMap.get("taskId");
        String status = beanMap.get("status");
        if (taskId == null) {
            return;
        }

        Logger.info("Start show cache line alignment task progress, taskId = {}", taskId);
        CacheLineProgress progress = new CacheLineProgress(TaskType.CACHE_LINE_ALIGNMENT.value(), taskId,
            message.getCbid(), status);
        progress.processForCommon(null,
                I18NServer.toLocale("plugins_enhanced_functions_cacheline_alignment_title"), progress);
    }
}

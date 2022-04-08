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
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.porting.process.AnalysisProcess;
import com.huawei.kunpeng.porting.process.PortingIDETask;

import java.util.Map;

/**
 * 软件迁移评估与js交互处理类
 *
 * @since 2.2.T4
 */
public class MigrationAppraiseHandler extends FunctionHandler {
    private final String migrationAppraiseTaskType = "7";

    /**
     * 软件迁移评估分析进度条
     *
     * @param message 数据
     * @param module  模块
     */
    public void scanProcess(MessageBean message, String module) {
        Logger.info("MigrationAppraiseHandler scanProcess start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId");
        if (taskId != null && PortingIDETask.isRunTask(migrationAppraiseTaskType)) {
            AnalysisProcess analysisProcess = new AnalysisProcess(migrationAppraiseTaskType, taskId,
                message.getCbid(), null);
            analysisProcess.processForCommon(CommonUtil.getDefaultProject(),
                I18NServer.toLocale("plugins_dependency_message_analysising"), analysisProcess);
        }
    }
}

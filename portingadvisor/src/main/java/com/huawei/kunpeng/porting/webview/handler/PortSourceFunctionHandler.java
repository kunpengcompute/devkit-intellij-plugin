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

import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.porting.common.PortingUserInfoContext;
import com.huawei.kunpeng.porting.common.constant.LeftTreeTitleConstant;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.module.codeanalysis.SourcePortingHandler;
import com.huawei.kunpeng.porting.process.AnalysisProcess;
import com.huawei.kunpeng.porting.process.PortingIDETask;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;
import java.util.Map;

/**
 * 源码分析任务交互处理器
 *
 * @since 2021-01-04
 */
public class PortSourceFunctionHandler extends FunctionHandler {
    /**
     * 关闭源码扫描页面
     */
    public static void closeTaskView() {
        // 获取所有打开的文件列表
        List<VirtualFile> openFiles = IDEFileEditorManager.getInstance().getOpenFiles();
        VirtualFile taskFile = null;
        // 获取源码扫描的虚拟文件
        for (VirtualFile file : openFiles) {
            if (file.getName().contains(LeftTreeTitleConstant.SOURCE_CODE_PORTING)) {
                taskFile = file;
                break;
            }
        }
        // 执行关闭
        if (taskFile != null) {
            IDEFileEditorManager.getInstance().closeFile(taskFile);
        }
    }

    /**
     * 源码分析任务进度条
     * 其中包含：
     * 1. 源码扫描进度查询
     * 2. 任务完成后左侧树历史报告刷新
     * 3. 任务完成后自动打开源码迁移报告
     *
     * @param message js回调的消息
     * @param module  port模块
     */
    public void analsysProgress(MessageBean message, String module) {
        // 从回调消息中获取任务Id并转成Map格式
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("reportId");
        String status = data.get("status");
        if (taskId == null) {
            return;
        }

        if (PortingIDETask.isRunTask(TaskType.SOURCE_SCAN.value())) {
            if (CommonHandler.getWorkerStatus(I18NServer.toLocale("plugins_porting_tip_analysis_title"),
                TaskType.SOURCE_SCAN.value(), taskId, message.getCbid(), status)) {
                return;
            }

            // 轮询任务执行进度并更新进度条，若查询到已完成则刷新左侧树+打开报告
            AnalysisProcess analysisProcess = new AnalysisProcess(
                TaskType.SOURCE_SCAN.value(), taskId, message.getCbid(), status);
            analysisProcess.processForCommon(CommonUtil.getDefaultProject(),
                I18NServer.toLocale("plugins_porting_tip_analysis_title"), analysisProcess);
        }
    }


    /**
     * 点击关闭gcc检查提示则代表不再对该服务器下的该用户进行检查
     *
     * @param message js回调的消息
     * @param module  port模块
     */
    public void getGCC(MessageBean message, String module) {
        PortingUserInfoContext.sourceCodeCheckGcc.remove(UserInfoContext.getInstance().getUserName());
    }

    /**
     * 源码迁移报告页面内下载html报告
     *
     * @param message 回调消息
     * @param module  模块
     */
    public void sourceReportDownload(MessageBean message, String module) {
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Object reportType = data.get("reportType");
        if (reportType instanceof Integer) {
            SourcePortingHandler.downLoadReport((Integer) reportType, data.get("reportId").toString());
        }
    }
}

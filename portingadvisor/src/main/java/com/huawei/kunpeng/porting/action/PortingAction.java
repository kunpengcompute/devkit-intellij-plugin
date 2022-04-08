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

package com.huawei.kunpeng.porting.action;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.action.rightclick.PortingSourceAction;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.process.AnalysisProcess;
import com.huawei.kunpeng.porting.ui.panel.PortingPanel;
import com.huawei.kunpeng.porting.webview.handler.CommonHandler;

import java.io.File;
import java.util.Map;

/**
 * 登录事件处理器
 *
 * @since 2020-10-08
 */
public class PortingAction extends IDEPanelBaseAction {
    /**
     * 取消分析源代码
     *
     * @param params        分析参数数据
     * @param actionOperate 自定义操作
     */
    public void onCancelAction(Map params, ActionOperate actionOperate) {
        // 创建分析进度条，打开报告面板
        if (actionOperate != null) {
            actionOperate.actionOperate("");
        }
    }

    /**
     * 响应执行迁移源码
     *
     * @param params porting task params
     */
    public void onOKAction(Map params) {
        // 上传需要分析的源码压缩包
        File zipFile = FileUtil.fileToZip(
            JsonUtil.getValueIgnoreCaseFromMap(params, PortingPanel.UPLOAD_FILE, File.class));
        if (zipFile != null) {
            boolean isSuccess = !StringUtil.stringIsEmpty(PortingSourceAction.uploadFile(zipFile));
            FileUtil.deleteDir(null,
                CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WORKSPACE_TEMP));
            if (!isSuccess) {
                return;
            }
        }

        // 去除请求无关参数
        params.keySet().removeIf(PortingPanel.UPLOAD_FILE::equals);

        // 当上传任务完成时， 调用分析任务
        AnalysisProcess process = new AnalysisProcess("0", null);
        process.setTaskId(process.executeAnalysis(params));

        // 查询分析进度，并以进度条显示
        if (StringUtil.stringIsEmpty(process.getTaskId())) {
            return;
        }
        // 无资源等待中任务
        if (CommonHandler.getWorkerStatus(I18NServer.toLocale("plugins_porting_tip_analysis_title"),
            TaskType.SOURCE_SCAN.value(), process.getTaskId(), null, process.getStatus())) {
            // 显示进度条。
            return;
        }

        process.processForCommon(null, I18NServer.toLocale("plugins_porting_tip_analysis_title"), process);
    }
}

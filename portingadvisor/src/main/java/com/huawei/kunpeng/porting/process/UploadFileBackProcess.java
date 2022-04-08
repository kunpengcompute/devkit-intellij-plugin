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

import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.porting.common.constant.enums.CMDFunction;
import com.huawei.kunpeng.porting.common.utils.PortingUploadUtil;

import com.intellij.openapi.progress.ProgressIndicator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传进度条
 *
 * @since 2020-10-12
 */
public class UploadFileBackProcess extends IDEBaseTask {
    private static final int STATUS_FAIL = 1;

    private File file;

    private Map<String, String> data;

    private String cbid;

    private File[] files;

    /**
     * 默认构造函数
     */
    public UploadFileBackProcess(File file, Map<String, String> data, String cbid) {
        this.file = file;
        this.data = data;
        this.cbid = cbid;
    }

    /**
     * UploadFileBackProcess
     *
     * @param files files
     * @param data  data
     * @param cbid  cbid
     */
    public UploadFileBackProcess(File[] files, Map<String, String> data, String cbid) {
        this.files = files;
        this.data = data;
        this.cbid = cbid;
    }

    /**
     * 任务执行时的方法
     *
     * @param indicator 自定义参数
     */
    @Override
    public void runTask(ProgressIndicator indicator) {
        ResponseBean responseBean;
        if (files != null) {
            responseBean = PortingUploadUtil.uploadProcess(files, data, indicator);
        } else {
            responseBean = PortingUploadUtil.uploadProcess(file, data, indicator);
        }
        if (responseBean != null) {
            // 回调将上传的文件名显示到webview上
            FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                    cbid, responseBean.getResponseJsonStr());
        }
    }

    /**
     * 任务取消后的动作
     *
     * @param indicator 自定义参数
     */
    @Override
    public void cancel(ProgressIndicator indicator) {
        Logger.info("UploadFileBackProcess error!");
        PortingUploadUtil.deleteZipFile();
        Map<String, Object> message = new HashMap<>();
        message.put("status", STATUS_FAIL);
        FunctionHandler.invokeCallback(CMDFunction.GET_DATA.functionName(),
                cbid, JsonUtil.getJsonStrFromJsonObj(message));
    }

    /**
     * 任务执行成功后的动作
     *
     * @param indicator 自定义参数
     */
    @Override
    public void success(ProgressIndicator indicator) {
        FileUtil.deleteDir(null, CommonUtil.getPluginInstalledPathFile(IDEConstant.PORTING_WORKSPACE_TEMP));
        Logger.info("UploadFileBackProcess success!");
    }
}


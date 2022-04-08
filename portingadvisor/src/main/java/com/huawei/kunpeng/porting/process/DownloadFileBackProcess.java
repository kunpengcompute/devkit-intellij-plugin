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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.IDEBaseTask;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 文件下载进度条任务：
 * 支持多文件、单文件下载。
 * 进度条无百分比
 *
 * @since T20
 */
public class DownloadFileBackProcess extends IDEBaseTask {
    /**
     * 下载URL。
     */
    private static final String DOWNLOAD_URL = "/portadv/weakconsistency/tasks/";
    private static final String DOWNLOAD_URL2 = "/bcfilelist/";
    private static final String FILE_PATH_PARAMS = "bcFileName";
    private static final String HREF_BEFORE = "<html> <a href=\"#\">";
    private static final String HREF_END = "</a></html>";

    /**
     * 单个文件名称
     */
    private String fileName;

    /**
     * 本地下载路径
     */
    private String downLoadLockPath;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 多文件列表
     */
    private ArrayList<String> fileNames;

    /**
     * 重复执行列表。
     */
    private ArrayList<String> newFileNames;

    /**
     * 是否是多文件
     */
    private boolean isMulFiles;

    /**
     * 单文件下载构造函数
     *
     * @param taskId           任务ID
     * @param downLoadLockPath 下载路径
     * @param fileName         文件名称
     */
    public DownloadFileBackProcess(String taskId, String downLoadLockPath, String fileName) {
        this.taskId = taskId;
        this.downLoadLockPath = downLoadLockPath;
        this.fileName = fileName;
        this.isMulFiles = false;
    }

    /**
     * 多文件下载构造函数
     *
     * @param taskId           任务ID
     * @param downLoadLockPath 下载路径
     * @param fileNames        多为年检列表
     */
    public DownloadFileBackProcess(String taskId, String downLoadLockPath, ArrayList<String> fileNames) {
        this.taskId = taskId;
        this.downLoadLockPath = downLoadLockPath;
        this.fileNames = fileNames;
        this.isMulFiles = true;
    }

    @Override
    protected void runTask(ProgressIndicator indicator) {
        if (isMulFiles) {
            // 多文件上传
            downloadMulFiles(indicator);
        } else {
            // 单文件上传
            downloadSingleFile(indicator, fileName);
        }
    }

    private void downloadMulFiles(ProgressIndicator indicator) {
        if (fileNames != null) {
            String fileNameTemp = "";
            this.newFileNames = new ArrayList<>();
            for (int i = 0; i < fileNames.size(); i++) {
                fileNameTemp = fileNames.get(i);
                if (indicator.isCanceled()) {
                    this.newFileNames.add(fileNameTemp);
                } else {
                    downloadSingleFile(indicator, fileNameTemp);
                }
            }
        }
    }

    private void downloadSingleFile(ProgressIndicator indicator, String fileName) {
        if (isMulFiles) {
            // 多文件上传
            indicator.setText(fileName);
            indicator.setText2(I18NServer.toLocale("plugins_porting_files_download"));
        } else {
            indicator.setText(I18NServer.toLocale("plugins_porting_files_download"));
        }
        download(downLoadLockPath, fileName, taskId, indicator);
    }

    @Override
    protected void cancel(ProgressIndicator indicator) {
        CommonUtil.setBackGroundProcessWindowOpen(false, CommonUtil.getDefaultWindow());
        Logger.warn("{} download task  is canceled.", taskId);
        // 提示信息
        NotificationBean notificationBean = new NotificationBean(
            I18NServer.toLocale("plugins_porting_download_bc_files_title"),
            I18NServer.toLocale("plugins_porting_settings_task_canceled_success"),
            NotificationType.INFORMATION);
        IDENotificationUtil.notificationCommon(notificationBean);
    }

    @Override
    protected void success(ProgressIndicator indicator) {
        CommonUtil.setBackGroundProcessWindowOpen(false, CommonUtil.getDefaultWindow());
        newFileNames = null;
    }

    /**
     * 下载BC文件
     *
     * @param path      下载文件的本地路径
     * @param name      下载文件名称
     * @param taskId    任务ID
     * @param indicator indicator
     */
    private void download(String path, String name, String taskId, ProgressIndicator indicator) {
        boolean isCanCreateTask = true;
        // 自旋： 监控任务是否被用户取消。
        Future<?> taskTreadFuture = null;
        while (true) {
            if (indicator.isCanceled()) {
                // 中断线程, 停止下载。
                if (taskTreadFuture != null && !taskTreadFuture.isCancelled()) {
                    taskTreadFuture.cancel(true);
                }
                break;
            }
            if (isCanCreateTask) {
                isCanCreateTask = false;
                // 启动下载任务
                taskTreadFuture = ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    setRequestMsg(path, name, taskId);
                });
            }
            if (taskTreadFuture != null && taskTreadFuture.isDone()) {
                break;
            }
        }
    }

    private void setRequestMsg(String path, String name, String taskId) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            DOWNLOAD_URL + taskId + DOWNLOAD_URL2, HttpMethod.POST.vaLue(), "");
        message.setNeedDownloadFile(true);
        message.setNeedDownloadZip(false);
        message.setDownloadPtah(path);
        message.setDownloadFileName(name);
        message.setAutoRename(true);
        Map<String, String> map = new HashMap<>();
        map.put(FILE_PATH_PARAMS, name);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(map));
        ResponseBean response = PortingHttpsServer.INSTANCE.requestData(message);
        if (response == null) {
            Logger.error("response is null");
            return;
        }
        doNotify(path, name, response, message);
    }

    private void doNotify(String path, String name, ResponseBean response, RequestDataBean message) {
        // 不为空判断文件是否失败
        if (response.getRealStatus() == null) {
            if (!message.isCancel()) {
                Logger.info(name + " download success！！");
                NotificationBean notificationBean = new NotificationBean(
                    I18NServer.toLocale("plugins_porting_download_bc_files_title"),
                    name + " " + I18NServer.toLocale("plugins_porting_download_report_success")
                        + HREF_BEFORE + path + HREF_END,
                    NotificationType.INFORMATION);
                IDENotificationUtil.notificationForHyperlink(notificationBean,
                    data -> CommonUtil.showFileDirOnDesktop(path));
            }
        } else {
            Logger.error(name + " download failed：" + response.getInfo());
            NotificationBean notificationBean = new NotificationBean(
                I18NServer.toLocale("plugins_porting_download_bc_files_title"),
                CommonUtil.getRspTipInfo(response), NotificationType.ERROR);
            IDENotificationUtil.notificationCommon(notificationBean);
        }
    }
}

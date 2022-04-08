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

import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;
import com.huawei.kunpeng.intellij.js2java.webview.pageditor.WebFileEditor;
import com.huawei.kunpeng.intellij.ui.utils.UIUtils;
import com.huawei.kunpeng.porting.action.report.handle.EditorSourceFileHandle;
import com.huawei.kunpeng.porting.bean.SourceFileBean;
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.common.utils.PortingUploadUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.http.module.enhance.CacheLineAlignmentHandler;
import com.huawei.kunpeng.porting.process.ByteAlignmentProcess;
import com.huawei.kunpeng.porting.process.DownloadFileBackProcess;
import com.huawei.kunpeng.porting.process.PortingIDETask;
import com.huawei.kunpeng.porting.process.PreCheckProcess;
import com.huawei.kunpeng.porting.process.UploadFileBackProcess;
import com.huawei.kunpeng.porting.process.WeakConsistencyProcess;
import com.huawei.kunpeng.porting.webview.WebFileProvider;
import com.huawei.kunpeng.porting.webview.pageeditor.ByteShowPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedFunctionPageEditor;
import com.huawei.kunpeng.porting.webview.pageeditor.EnhancedReportPageEditor;
import com.huawei.kunpeng.porting.webview.pagewebview.EnhancedReportWebView;

import com.alibaba.fastjson.JSONArray;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 增强功能的function处理器
 *
 * @since 2021/1/5
 */
public class EnhancedFunctionHandler extends FunctionHandler {
    private static final String MIGRATION_SCAN_INFO_URL = "/portadv/tasks/migrationscaninfo/";

    private static final String WEAK_PORTING_INFO_URL = "/portadv/weakconsistency/tasks/%s/portinginfo/";

    /**
     * 弱内存序编译文件进度查询
     *
     * @param message 数据
     * @param module  模块
     */
    public void weakCompileProgress(MessageBean message, String module) {
        Logger.info("Start weak compile progress.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId");
        String status = data.get("status");
        if (taskId == null) {
            return;
        }

        if (PortingIDETask.isRunTask(TaskType.WEAK_COMPILE.value())) {
            if (CommonHandler.getWorkerStatus(
                I18NServer.toLocale("plugins_porting_weak_check_compile_file") + "······",
                TaskType.WEAK_COMPILE.value(), taskId, message.getCbid(), status)) {
                return;
            }

            WeakConsistencyProcess process = new WeakConsistencyProcess(TaskType.WEAK_COMPILE.value(), taskId,
                message.getCbid(), status);
            process.processForCommon(null,
                I18NServer.toLocale("plugins_porting_weak_check_compile_file") + "······", process);
        }
    }

    /**
     * 字节对齐任务进度查询
     *
     * @param message 数据
     * @param module  模块
     */
    public void byteAlignProgress(MessageBean message, String module) {
        Logger.info("Start byte alignment progress.");
        // 从回调消息中获取任务Id并转成Map格式
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId");
        String status = data.get("status");
        if (taskId == null) {
            return;
        }

        if (PortingIDETask.isRunTask(TaskType.BYTE_ALIGN.value())) {
            if (CommonHandler.getWorkerStatus(
                I18NServer.toLocale("plugins_porting_enhance_function_byte_align_processing") + "······",
                TaskType.BYTE_ALIGN.value(), taskId, message.getCbid(), status)) {
                return;
            }

            ByteAlignmentProcess process = new ByteAlignmentProcess(taskId, message.getCbid(), status);
            process.processForCommon(null,
                I18NServer.toLocale("plugins_porting_enhance_function_byte_align_processing") + "······",
                process);
        } else {
            // 更新字节对齐任务进度回调id
            ByteAlignmentProcess.setByteAlignmentCallBackId(message.getCbid());
        }
    }

    /**
     * 弱内存序检查任务进度查询
     *
     * @param message 数据
     * @param module  模块
     */
    public void weakCheckProgress(MessageBean message, String module) {
        Logger.info("Start weak check progress.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId");
        String status = data.get("status");
        if (taskId == null) {
            return;
        }

        if (PortingIDETask.isRunTask(TaskType.WEAK_CHECK.value())) {
            if (CommonHandler.getWorkerStatus(
                I18NServer.toLocale("plugins_porting_weak_check_progress_label"),
                TaskType.WEAK_CHECK.value(), taskId, message.getCbid(), status)) {
                return;
            }

            WeakConsistencyProcess process = new WeakConsistencyProcess(TaskType.WEAK_CHECK.value(), taskId,
                message.getCbid(), status);
            process.processForCommon(null,
                I18NServer.toLocale("plugins_porting_weak_check_progress_label"), process);
        }
    }

    /**
     * 弱内存序BC检查任务进度查询
     *
     * @param message 数据
     * @param module  模块
     */
    public void bcCheckProgress(MessageBean message, String module) {
        Logger.info("Start bc check progress.");
        String taskId = JsonUtil.getJsonObjFromJsonStr(message.getData()).get("taskId").toString();
        if (PortingIDETask.isRunTask(TaskType.BC_CHECK.value())) {
            WeakConsistencyProcess process = new WeakConsistencyProcess(TaskType.BC_CHECK.value(), taskId,
                message.getCbid(), null);
            process.processForCommon(null,
                I18NServer.toLocale("plugins_porting_enhance_function_checking") + "······", process);
        }
    }

    /**
     * 关闭增强功能报告页面
     *
     * @param message 数据
     * @param module  模块
     */
    public void clearEnhanceReport(MessageBean message, String module) {
        EditorSourceFileHandle.getEditorSourceFileHandle().closeEnhancedSourceFile();
        ByteShowPageEditor.closeAllPage();
        Project project = CommonUtil.getDefaultProject();
        VirtualFile file = IDEFileEditorManager.getInstance(CommonUtil.getDefaultProject()).getSelectFile();
        WebFileEditor webViewPage = WebFileProvider.getWebViewPage(project, file);
        if (webViewPage != null) {
            webViewPage.dispose();
        }
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        HashMap<String, String> paramMap = new HashMap<>();
        String filePath = data.get("localFilePath");
        if (!StringUtil.stringIsEmpty(filePath)) {
            paramMap.put("filePath", filePath);
            paramMap.put("fileName", PathUtil.getFileName(filePath));
            paramMap.put("isSingle", "true");
        }
        EnhancedFunctionPageEditor.openPageContainsParam(paramMap);
    }

    /**
     * 下载编译文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadFile(MessageBean message, String module) {
        Logger.info("downloadFile start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId");
        String fileName = data.get("fileName");
        ResponseBean responseBean =
            PortingHttpsServer.INSTANCE.requestData(new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                PortingIDEConstant.PATH_SEPARATOR + "portadv"
                    + PortingIDEConstant.PATH_SEPARATOR + "weakconsistency"
                    + PortingIDEConstant.PATH_SEPARATOR + taskId + PortingIDEConstant.PATH_SEPARATOR + "compilefile"
                    + PortingIDEConstant.PATH_SEPARATOR, HttpMethod.GET.vaLue(), ""));
        if (responseBean == null) {
            return;
        }
        // 弹出保存文件选择框
        NotificationBean notification = new NotificationBean("",
            I18NServer.toLocale("plugins_porting_weak_compile_download_success", fileName),
            NotificationType.INFORMATION);
        notification.setProject(CommonUtil.getDefaultProject());
        UIUtils.saveTXTFileToLocalForDialog(responseBean.getData(), fileName, notification);

        Logger.info("downloadFile end: {}", fileName);
    }

    /**
     * 64位迁移预检任务进度查询
     *
     * @param message 数据
     * @param module  模块
     */
    public void precheckProgress(MessageBean message, String module) {
        Logger.info("Start pre-check progress.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId");
        String status = data.get("status");
        if (taskId == null) {
            return;
        }

        if (PortingIDETask.isRunTask(TaskType.MIGRATION_PRE_CHECK.value())) {
            if (CommonHandler.getWorkerStatus(
                I18NServer.toLocale("plugins_porting_precheck_tip") + "······",
                TaskType.MIGRATION_PRE_CHECK.value(), taskId, message.getCbid(), status)) {
                return;
            }
            final PreCheckProcess taskProcess = new PreCheckProcess(taskId, message.getCbid(), status);
            final ProgressManager progress = ProgressManager.getInstance();
            progress.run(taskProcess);
        } else {
            // 更新64位迁移预检任务进度回调id
            PreCheckProcess.setPreCheckCallBackId(message.getCbid());
        }
    }

    /**
     * 打开字节对齐建议源码界面
     *
     * @param message 数据
     * @param module  模块
     */
    public void openNewPage(MessageBean message, String module) {
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if ("bytereport".equals(String.valueOf(data.get("router")))) {
            Map<String, String> params = JsonUtil.getJsonObjFromJsonStr(data.get("message").toString());
            ByteShowPageEditor.openPage(params.get("reportId"), params.get("diffPath"));
        }
    }

    /**
     * 打开64位迁移预检建议源码界面
     *
     * @param message 数据
     * @param module  模块
     */
    public void createPortCheckTree(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if (data.size() != 0) {
            openCodeFile(data, MIGRATION_SCAN_INFO_URL, TaskType.MIGRATION_PRE_CHECK.value());
        }
    }

    /**
     * 打开Cache Line 建议源码界面
     *
     * @param message 数据
     * @param module  模块
     */
    public void createCacheCheckTree(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if (data.size() != 0) {
            openCodeFile(data, CacheLineAlignmentHandler.CACHE_LINE_TASK_RESULT_URL,
                TaskType.CACHE_LINE_ALIGNMENT.value());
        }
    }

    /**
     * 打开弱内存序建议源码界面
     *
     * @param message 数据
     * @param module  模块
     */
    public void openWeakReport(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if (data.size() != 0) {
            String taskId = data.get("id");
            openCodeFile(data, String.format(Locale.ROOT, WEAK_PORTING_INFO_URL, taskId), TaskType.WEAK_CHECK.value());
        }
    }

    /**
     * 打开增强功能报告
     *
     * @param message 数据
     * @param module  模块
     */
    public void goEnhancedReportDetail(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskType = data.get("taskType");
        if (TaskType.MIGRATION_PRE_CHECK.value().equals(taskType)
            || TaskType.BYTE_ALIGN.value().equals(taskType) || TaskType.CACHE_LINE_ALIGNMENT.value().equals(taskType)) {
            PortingIDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
                EnhancedReportWebView.WORKSPACE_KEY, data.get(EnhancedReportWebView.WORKSPACE_KEY));
            if (TaskType.BYTE_ALIGN.value().equals(taskType)) {
                PortingIDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
                    EnhancedReportWebView.COMMAND_KEY, data.get(EnhancedReportWebView.COMMAND_KEY));
                PortingIDEContext.setValueForGlobalContext(PortingIDEConstant.TOOL_NAME_PORTING,
                    EnhancedReportWebView.SELECTED_KEY, data.get(EnhancedReportWebView.SELECTED_KEY));
            }
            EnhancedFunctionPageEditor.closePage();
        }
        EnhancedReportPageEditor.openPage(data.get("taskId"), taskType);
    }

    /**
     * 打开源码文件
     *
     * @param data     数据
     * @param url      url
     * @param taskType 任务类型
     */
    private void openCodeFile(Map<String, String> data, String url, String taskType) {
        String taskId = data.get("id");
        String remoteFilePath = data.get("remoteFilePath");
        String filePath = data.get("filePath");
        String workspaceFilePath = CommonUtil.getPluginInstalledPathFile(PortingIDEConstant.PORTING_WORKSPACE_TEMP);
        String localFilePath = workspaceFilePath + File.separator + taskId + File.separator + remoteFilePath;
        boolean localFile = filePath != null && !filePath.equals(remoteFilePath);
        if (filePath != null) {
            // Mindstudio兼容 将路径中"\" 转换为 "/"
            filePath = filePath.replace("\\\\", PortingIDEConstant.PATH_SEPARATOR);
            // 判断本地文件是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                localFile = false;
            }
        }
        if (localFile) {
            localFilePath = filePath;
        }
        SourceFileBean sourceFileBean =
            new SourceFileBean(url, taskId, taskType, localFile, localFilePath, remoteFilePath);
        EditorSourceFileHandle.getEditorSourceFileHandle().openSourceFile(sourceFileBean);
    }

    /**
     * 显示内存一致性信息
     *
     * @param message 数据
     * @param module  模块
     */
    public void showLockBox(MessageBean message, String module) {
        Logger.info("showLockBox start.");
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Object taskIdObj = data.get("taskId").toString();
        Object taskTypeObj = data.get("taskType").toString();
        if (taskIdObj != null && taskTypeObj != null) {
            String taskId = taskIdObj.toString();
            String taskType = taskTypeObj.toString();
            Object lockInfoObj = data.get("info").toString();
            String lockInfo = "";
            if (lockInfoObj != null) {
                lockInfo = lockInfoObj.toString();
            }
            NotificationBean notificationBean = new NotificationBean("",
                lockInfo + I18NServer.toLocale("plugins_porting_view_report"),
                NotificationType.WARNING);
            IDENotificationUtil.notificationForHyperlink(notificationBean,
                actionOperate -> EnhancedReportPageEditor.openPage(taskId, taskType));
        }
    }

    /**
     * 下载BC文件列表
     *
     * @param message 数据
     * @param module  模块
     */
    public void downloadBcFiles(MessageBean message, String module) {
        Logger.info("downloadBcFiles start.");
        Map<String, Object> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String taskId = data.get("taskId").toString();
        Object fileNameObj = data.get("fileName");
        if (fileNameObj != null) {
            String fileName = fileNameObj.toString();
            downloadSingleBCFile(taskId, fileName);
        }

        Object fileNamesObj = data.get("fileNames");
        if (fileNamesObj instanceof JSONArray) {
            downloadMulBCFiles(taskId, (JSONArray) fileNamesObj);
        }
    }

    /**
     * 下载多个BC文件
     *
     * @param taskId       taskId
     * @param fileNamesObj fileNamesObj
     */
    private void downloadMulBCFiles(String taskId, JSONArray fileNamesObj) {
        List<String> fileNamesList = JSONArray.parseArray(fileNamesObj.toJSONString(), String.class);
        ArrayList<String> fileNames = null;
        if (fileNamesList instanceof ArrayList) {
            fileNames = (ArrayList<String>) fileNamesList;
        }
        if (fileNames == null) {
            return;
        }
        // 多文件下, 先选目录。
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(I18NServer.toLocale("plugins_porting_download_bc_files_title"));
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, CommonUtil.getDefaultProject(), null);

        if (virtualFile == null) {
            return;
        }
        // 开始下载（进度条）
        String path = virtualFile.getPath();
        if (fileNames != null) {
            final DownloadFileBackProcess taskProcess = new DownloadFileBackProcess(taskId, path, fileNames
            );
            taskProcess.processForCommon(null, I18NServer.toLocale("plugins_porting_download_bc_files_title"),
                taskProcess);
        }
    }

    /**
     * 下载单个BC文件
     *
     * @param taskId   taskId
     * @param fileName fileName
     */
    private void downloadSingleBCFile(String taskId, String fileName) {
        // 多文件下, 先选目录。
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(I18NServer.toLocale("plugins_porting_download_bc_files_title"));
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, CommonUtil.getDefaultProject(), null);

        if (virtualFile == null) {
            return;
        }
        // 开始下载（进度条）
        String path = virtualFile.getPath();
        if (fileName != null) {
            final DownloadFileBackProcess taskProcess = new DownloadFileBackProcess(taskId, path, fileName
            );
            taskProcess.processForCommon(null, fileName,
                taskProcess);
        }
    }

    /**
     * 亲和检查，在创建报告之前上传文件
     *
     * @param message 页面传过来的消息
     * @param module 模块
     */
    public void rightClickUploadPortingFile(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String filePath = data.get("filePath");
        File zipFile = FileUtil.fileToZip(Paths.get(filePath).toFile());
        if (zipFile != null) {
            ResponseBean responseBean = PortingUploadUtil.preUpload(zipFile, data, filePath);
            if (RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
                UploadFileBackProcess process = new UploadFileBackProcess(zipFile, data, message.getCbid());
                process.processForCommon(null, zipFile.getName(), process);
            }
            invokeCallback(message.getCmd(), message.getCbid(), responseBean.getResponseJsonStr());
        }
    }
}

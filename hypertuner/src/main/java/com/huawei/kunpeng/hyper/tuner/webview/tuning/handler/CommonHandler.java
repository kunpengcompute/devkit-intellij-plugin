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

package com.huawei.kunpeng.hyper.tuner.webview.tuning.handler;

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaProviderSettingConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.DiskUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.WebViewUtil;
import com.huawei.kunpeng.hyper.tuner.http.SysperfProjectServer;
import com.huawei.kunpeng.hyper.tuner.http.TuningHttpsServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.NodeList;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.Tasklist;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningLoginWrapDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.JavaPerfToolWindowPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting.LeftTreeSysperfPanel;
import com.huawei.kunpeng.hyper.tuner.webview.java.pageeditor.ShowSamplingTaskEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.OpenNewPageEditor;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.pageeditor.ShowNodeEditor;
import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.BaseCacheVal;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.exception.IDEException;
import com.huawei.kunpeng.intellij.common.http.HttpAPIServiceTrust;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ByteBufferInputStreamAc;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.FileSplitUtils;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.js2java.webview.handler.FunctionHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 公共的function处理器
 *
 * @since 2020-11-18
 */
public class CommonHandler extends FunctionHandler {
    private static final int MAX_FILE_SIZE = 500 << 20;

    private static final String NAME_FILTER = "nameFilter";

    private static final String SYSPERF_URL = "sys-perf/api/v2.2";

    private static final String JAVAPERF_URL = "java-perf/api";

    private boolean importFinshed = false;

    /**
     * webview通用请求函数
     *
     * @param message 数据
     * @param module  模块
     */
    public void getData(MessageBean message, String module) {
        Logger.info("getData start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String dataUrl = data.get("url");
        if (dataUrlHandle(message, module, dataUrl)) {
            return;
        }
        String fullUrl = generateFullUrl(message, dataUrl);
        RequestDataBean request = new RequestDataBean(module, fullUrl, data.get("method"), null);
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        if (HttpMethod.POST.vaLue().equals(method)
            || HttpMethod.DELETE.vaLue().equals(method)
            || HttpMethod.PUT.vaLue().equals(method)) {
            request.setMethod(method);
            request.setBodyData(JSONObject.toJSONString(data.get("params")));
        }
        ResponseBean responseBean = TuningHttpsServer.INSTANCE.requestData(request);
        if (responseBean == null) {
            return;
        }
        if (RespondStatus.DISK_NOT_ENOUGH.value().equals(responseBean.getStatus())) {
            DiskUtil.sendDiskAlertMessage();
            return;
        }
        // 回调
        invokeCallback(message.getCmd(), message.getCbid(), responseBean.getResponseJsonStr());
        Logger.info("getData end.");
    }

    /**
     * 对传来的参数URl 进行判断，若符合条件，则执行相应的操作并返回
     *
     * @param message message
     * @param module  module
     * @param dataUrl dataUrl
     * @return 是否中断后续操作，直接返回
     */
    private boolean dataUrlHandle(MessageBean message, String module, String dataUrl) {
        if ("checkUploadFileIntellijHyper".equals(dataUrl)) {
            checkUploadFileIntellijHyper(message, module);
            return true;
        }
        if ("intellijExcuteUpload".equals(dataUrl)) {
            intellijExcuteUpload(message, module);
            return true;
        }
        if ("qryGuardianCurrentStata".equals(dataUrl)) {
            Map<String, Object> map = new HashMap<>();
            if ("running".equals(JavaPerfToolWindowPanel.stopProfiling)) {
                Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(
                        JavaPerfToolWindowPanel.profilingMessage.getData());
                map.put("status", "true");
                map.put("id", String.valueOf(JsonUtil.getJsonObjFromJsonStr(
                        String.valueOf(data.get("message"))).get("jvmId")));
            } else {
                map.put("status", "false");
                map.put("id", "");
            }
            invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(map));
            return true;
        }
        if ("checkDiscAlarm".equals(dataUrl)) {
            boolean flag = TuningLoginWrapDialog.getDiscAlarm(false);
            invokeCallback(message.getCmd(), message.getCbid(), String.valueOf(flag));
            return true;
        }
        if ("getIntellijImportData".equals(dataUrl)) {
            Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
            String path = data.get("param");
            File file = new File(path);
            String str = null;
            try {
                str = Optional.ofNullable(IOUtils.toString(file.toURI(), StandardCharsets.UTF_8)).orElse("{}");
            } catch (IOException e) {
                Logger.info("Read IOException: {}", path);
            }
            invokeCallback(message.getCmd(), message.getCbid(), str);
            return true;
        }
        return false;
    }

    /**
     * 根据 message 相关信息，生成完整的url
     *
     * @param message message
     * @param dataUrl dataUrl
     * @return 完整的url
     */
    @NotNull
    private String generateFullUrl(MessageBean message, String dataUrl) {
        String fullUrl;
        if ("javaPerf".equals(message.getModule())) {
            fullUrl = JAVAPERF_URL + dataUrl;
        } else {
            fullUrl = SYSPERF_URL + dataUrl;
        }
        if (dataUrl.contains("projects/undefined")) {
            fullUrl = fullUrl.replaceAll("projects/undefined",
                "projects/" + LeftTreeSysperfPanel.getSelectProject().getProjectId());
        }
        if (dataUrl.contains("tasks/undefined") && dataUrl.contains("nodeId=undefined")) {
            fullUrl = fullUrl.replaceAll("tasks/undefined",
                "tasks/" + LeftTreeSysperfPanel.getSelectNode().getId());
            fullUrl = fullUrl.replaceAll("nodeId=undefined",
                "nodeId=" + LeftTreeSysperfPanel.getSelectNode().getNodeId());
        }
        if (dataUrl.contains("tasks/undefined") && dataUrl.contains("node-id=undefined")) {
            fullUrl = fullUrl.replaceAll("tasks/undefined",
                "tasks/" + LeftTreeSysperfPanel.getSelectNode().getId());
            fullUrl = fullUrl.replaceAll("node-id=undefined",
                "node-id=" + LeftTreeSysperfPanel.getSelectNode().getNodeId());
        }
        if (dataUrl.contains("node-id=undefined")) {
            fullUrl = fullUrl.replaceAll("node-id=undefined",
                "node-id=" + LeftTreeSysperfPanel.getSelectNode().getNodeId());
        }
        if (dataUrl.contains("certificates/download-ca")) {
            fullUrl = "user-management/api/v2.2/certificates/download-ca/";
        }
        return fullUrl;
    }

    /**
     * webview显示右下角提示消息处理
     *
     * @param message 显示信息
     * @param module  模块
     */
    public void showInfoBox(MessageBean message, String module) {
        Logger.info("showInfoBox start");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        switch (data.get("type")) {
            case "error":
                showFailInfo(data);
                break;
            case "warn":
                showWarnInfo(data);
                break;
            default:
                if ("cancel".equals(data.get("info"))) {
                    WebViewUtil.closeCurrentPage();
                } else {
                    showSuccessInfo(data);
                }
                break;
        }
        Logger.info("showInfoBox end.");
    }

    private void showSuccessInfo(Map<String, String> data) {
        String operation = data.get("operation");
        if (operation == null) {
            operation = "";
            Logger.warn("showSuccessInfo operation is null.", data);
        }
        switch (operation) {
            case "createProject":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.CREATE_PROJECT,
                    SysperfContent.CREATE_PROJECT_SUCCESS, NotificationType.INFORMATION));
                WebViewUtil.closePage(SysperfContent.CREATE_PROJECT + "." + TuningIDEConstant.TUNING_KPHT);
                break;
            case "modifyProject":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.MODIFY_PROJECT,
                    SysperfContent.MODIFY_PROJECT_SUCCESS, NotificationType.INFORMATION));
                WebViewUtil.closePage(SysperfContent.MODIFY_PROJECT + "." + TuningIDEConstant.TUNING_KPHT);
                break;
            case "createTask":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.CREATE_TASK,
                    SysperfContent.CREATE_TASK_SUCCESS, NotificationType.INFORMATION));
                break;
            case "impTemplete":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.SAVE_TEMPLETE,
                    SysperfContent.SAVE_TEMPLETE_SUCCESS, NotificationType.INFORMATION));
                break;
            case "modifyTask":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.MODIFY_TASK,
                    SysperfContent.MODIFY_TASK_SUCCESS, NotificationType.INFORMATION));
                break;
            case "importTask":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                    SysperfContent.IMPORT_TASK_SUCESS, NotificationType.INFORMATION));
                break;
            case "addNode":
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(CommonUtil.getDefaultProject(), SysperfContent.NODE_MANAGER_DIC);
                break;
            default:
                break;
        }
    }

    private void showWarnInfo(Map<String, String> data) {
        String warnInfo = data.get("info");
        IDENotificationUtil.notificationCommon(new NotificationBean("", warnInfo, NotificationType.WARNING));
    }

    private void showFailInfo(Map<String, String> data) {
        String dataOperation = data.get("operation");
        if (dataOperation == null) {
            dataOperation = "";
            Logger.warn("showFailInfo dataOperation is null.", data);
        }
        switch (dataOperation) {
            case "createProject":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.CREATE_PROJECT,
                    SysperfContent.CREATE_PROJECT_FAIL, NotificationType.ERROR));
                break;
            case "modifyProject":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.MODIFY_PROJECT,
                    data.get("info"), NotificationType.ERROR));
                break;
            case "impTemplete":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.SAVE_TEMPLETE,
                    data.get("info"), NotificationType.ERROR));
                break;
            case "createTask":
                String message = data.get("info");
                if (message.contains("Falsesharing")) {
                    message = message.replaceAll("Falsesharing", TuningI18NServer.toLocale(
                        "plugins_hyper_tuner_task_type_false_share"));
                }
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.CREATE_TASK,
                    message, NotificationType.ERROR));
                break;
            case "modifyTask":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.MODIFY_TASK,
                    data.get("info"), NotificationType.ERROR));
                break;
            case "importTask":
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                    data.get("info"), NotificationType.ERROR));
                break;
            case "paramError":
                String infoStr = data.get("info");
                JSONObject infoObj = JsonUtil.getJsonObjectFromJsonStr(infoStr);
                String operation = infoObj.getString("operation");
                IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.PARAM_ERROR,
                    operation, NotificationType.ERROR));
                break;
            default:
                break;
        }
    }

    /**
     * webview显示右下角提示消息处理
     *
     * @param message 显示信息
     * @param module  模块
     */
    public void closePage(MessageBean message, String module) {
        Logger.info("closePage start");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if ("true".equals(String.valueOf(data.get("isopen")))) {
            LeftTreeSysperfPanel.isCreateTask = true;
            if (null == LeftTreeSysperfPanel.getSelectProject() || null == LeftTreeSysperfPanel.getSelectTask()) {
                WebViewUtil.closePage(SysperfContent.CREATE_TASK + "." + TuningIDEConstant.TUNING_KPHT);
            } else {
                WebViewUtil.closePage(SysperfContent.CREATE_TASK + "." + TuningIDEConstant.TUNING_KPHT);
                WebViewUtil.closePage(SysperfContent.MODIFY_TASK + '-' +
                    LeftTreeSysperfPanel.getSelectProject().getProjectName() + '-' +
                    LeftTreeSysperfPanel.getSelectTask().getTaskname() + "." + TuningIDEConstant.TUNING_KPHT);
            }
            openTaskNodePage(data);
            LeftTreeSysperfPanel.isCreateTask = false;
        }
        Logger.info("closePage end.");
    }

    private void openTaskNodePage(Map<String, String> data) {
        String taskStr = JSONObject.toJSONString(data.get("task"));
        String projectName = data.get("projectName");
        NodeList nodeList = JSON.parseObject(taskStr, NodeList.class);
        LeftTreeSysperfPanel.refreshTaskContant(nodeList.getTaskParam().getTaskname(), projectName);
        LeftTreeAction.instance().showTaskNode();
    }

    /**
     * 读取url
     *
     * @param message 数据
     * @param module  模块
     */
    public void readURLConfig(MessageBean message, String module) {
        Map urlConfig = FileUtil.ConfigParser.parseJsonConfigFromFile(TuningIDEConstant.URL_CONFIG_PATH);
        invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(urlConfig));
    }

    /**
     * 跳转到修改工程页面
     *
     * @param message 数据
     * @param module  模块
     */
    public void navigateToPanel(MessageBean message, String module) {
        LeftTreeAction.instance().modifyProject();
    }

    /**
     * 打开字节对齐建议源码界面
     *
     * @param message 数据
     * @param module  模块
     */
    public void openNewPage(MessageBean message, String module) {
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        if (String.valueOf(data.get("router")).contains("sampling")) {
            JavaPerfToolWindowPanel.refreshProfilingNode();
            String pageName = data.get("viewTitle").replaceAll(" Create Time:", "")
                .replaceAll(" ", "-").replaceAll(":", "-");
            // 检查当前webView是否已经打开
            List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains(pageName))
                .collect(Collectors.toList());

            if (collect.isEmpty()) {
                ShowSamplingTaskEditor.openPage(message, true, pageName);
            } else {
                instance.openFile(collect.get(0), true);
            }
        } else if (String.valueOf(data.get("router")).contains("/javaperfsetting")) {
            ShowSettingsUtil.getInstance()
                .showSettingsDialog(CommonUtil.getDefaultProject(),
                    JavaProviderSettingConstant.JAVA_PROFILER_SETTINGS);
        } else {
            // 检查当前webView是否已经打开

            List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
                .contains("函数信息"))
                .collect(Collectors.toList());

            if (collect.isEmpty()) {
                OpenNewPageEditor.openPage(message);
            } else {
                instance.openFile(collect.get(0), true);
            }
        }
    }

    /**
     * 检测上传文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void checkUploadFileIntellijHyper(MessageBean message, String module) {
        FileChooserDialog fileChooser = FileChooserFactory.getInstance()
            .createFileChooser(
                new FileChooserDescriptor(true, true, true, false, false, false).withFileFilter(file -> {
                    if (file.getExtension() == null) {
                        return false;
                    }
                    // 过滤掉大于1G的文件
                    if (file.getLength() > MAX_FILE_SIZE) {
                        return false;
                    }

                    return !file.isDirectory();
                }), CommonUtil.getDefaultProject(), null);
        // virtualFile对于jar,zip文件路径获取异常，重新拼接path
        String uploadFilePath = getVirtualFilePath(fileChooser);
        if (uploadFilePath.isEmpty()) {
            return;
        }
        if (!uploadFilePath.isEmpty()) {
            File file = new File(uploadFilePath);
            // 回调
            JSONObject obj = new JSONObject();
            obj.put("fileName", file.getName());
            obj.put("filePath", file.getPath());
            obj.put("fileSize", file.length());
            obj.put("id", "");
            invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(obj));
        }
    }

    private String getVirtualFilePath(FileChooserDialog fileChooser) {
        final VirtualFile[] virtualFiles = fileChooser.choose(CommonUtil.getDefaultProject(),
            VirtualFile.EMPTY_ARRAY);
        VirtualFile virtualFile = virtualFiles.length > 0 ? virtualFiles[0] : null;
        if (virtualFile == null) {
            return "";
        }
        return virtualFile.getPath().substring(0, virtualFile.getPath().lastIndexOf(virtualFile.getName()))
            + virtualFile.getName();
    }

    /**
     * 上传文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void intellijExcuteUpload(MessageBean message, String module) {
        Project project = CommonUtil.getDefaultProject();
        String title = SysperfContent.IMPORT_TASK;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText2("");
                indicator.setText(SysperfContent.EXPORT_TASK_BAR_PERPARING);
                indicator.setFraction(0.0D);
                Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
                Map<String, String> uoloadData =
                    JsonUtil.getJsonObjFromJsonStr(String.valueOf(data.get("data")));
                String taskname = uoloadData.get("taskname");
                String projectname = uoloadData.get("projectname");
                indicator.setText(SysperfContent.IMPORT_TASK);
                Map<String, String> paramMap = new HashMap(2);
                paramMap.put("projectName", projectname);
                paramMap.put("taskName", taskname);
                String uploadFilePath = uoloadData.get("uploadFilePath");
                File file = new File(uploadFilePath);
                String uploadType = uoloadData.get("upload_type");
                int uploadFileSize = Integer.parseInt(String.valueOf(file.length()));
                ResponseBean responseBean = SysperfProjectServer.uploadTaskFile(paramMap, 1, uploadType,
                    uploadFilePath, uploadFileSize);
                if (JsonUtil.getJsonObjFromJsonStr(responseBean.getData()).get("id") == null) {
                    return;
                }
                int index = Integer.parseInt(String.valueOf(JsonUtil.getJsonObjFromJsonStr(responseBean.getData()
                ).get("id")));
                String uploadFileName = uoloadData.get("uploadFileName");
                indicator.setFraction(0.1D);
                ResponseBean getChunkNumber = SysperfProjectServer.getChunkNumber(index);
                indicator.setFraction(0.2D);
                if ("web".equals(uploadType) && "SysPerf.Success".equals(getChunkNumber.getCode())) {
                    ResponseBean restTempResponse = getRestTempResponse(file, uploadFileName, index, indicator);
                    if ("SysPerf.Success".equals(responseBean.getCode())) {
                        SysperfProjectServer.uploadSuccess(index, uploadFileName);
                    }
                    if (restTempResponse != null && !"SysPerf.Success".equals(restTempResponse.getCode())) {
                        IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                            TuningI18NServer.toLocale("plugins_hyper_tuner_sysperf_import_task_chunk_error"),
                            NotificationType.ERROR));
                        return;
                    }
                }
                indicator.setFraction(0.9D);
                initTimerUploadTask(index);
                indicator.setFraction(1.0D);
            }
        });
    }

    private ResponseBean getRestTempResponse(File file, String uploadFileName, int index,
            @NotNull ProgressIndicator indicator) {
        String url = null;
        String token = null;
        Object ctxObj = IDEContext.getValueFromGlobalContext(null, TuningIDEConstant.TOOL_NAME_TUNING);
        if (ctxObj instanceof Map) {
            Map<String, Object> context = (Map<String, Object>) ctxObj;
            String ip = Optional.ofNullable(context.get(BaseCacheVal.IP.vaLue()))
                .map(Object::toString).orElse(null);
            String port = Optional.ofNullable(context.get(BaseCacheVal.PORT.vaLue()))
                .map(Object::toString).orElse(null);
            if (ValidateUtils.isEmptyString(ip) || ValidateUtils.isEmptyString(port)) {
                IDENotificationUtil.notificationCommon(
                    new NotificationBean("", I18NServer.toLocale("plugins_common_message_configServer"),
                        NotificationType.WARNING));
                throw new IDEException();
            }
            url = IDEConstant.URL_PREFIX + ip + ":" + port + context.get(BaseCacheVal.BASE_URL.vaLue())
                + "sys-perf/api/v2.2/import_export_tasks/index/";
            token = Optional.ofNullable(context.get(BaseCacheVal.TOKEN.vaLue())).map(Object::toString).orElse(null);
        }
        try {
            FileSplitUtils fileSplitUtils = FileSplitUtils.getInstance();
            // 分块大小 25MB --> 26214400B --> Hex:0x1900000
            long blockLength = fileSplitUtils.getBlockLength(file.toPath(), 0x1900000L);
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", index);
            jsonParam.put("fileName", uploadFileName);
            jsonParam.put("fileSize", fileSplitUtils.getFileLength());
            double progressValue = 0.3D;
            String result = null;
            long stateCounter = 0L;
            indicator.setFraction(progressValue);
            for (long i = 1; i <= blockLength; i++) {
                jsonParam.put("chunk", String.valueOf(i - 1));
                ByteBufferInputStreamAc inputStream = fileSplitUtils.getInputStream(fileSplitUtils.split(i));
                jsonParam.put("inputStream", inputStream);
                if (StringUtil.stringIsEmpty(url)) {
                    break;
                }
                result = HttpAPIServiceTrust.getResponseString(url, jsonParam, "POST", token, "TUNING");
                stateCounter += 1L;
                indicator.setFraction(progressValue + (((double) i / (double) blockLength) * 0.5D));
            }
            if (index == stateCounter) {
                return JsonUtil.jsonToDataModel(result, ResponseBean.class);
            }
        } catch (IOException e) {
            Logger.error("upload failed. detail message: {}.", e.getMessage());
        }
        return JsonUtil.jsonToDataModel("", ResponseBean.class);
    }

    /**
     * 检测上传状态
     *
     * @param index index
     */
    private void initTimerUploadTask(int index) {
        boolean status = true;
        while (status) {
            ResponseBean responseBean = SysperfProjectServer.importTaskStu(index);
            Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
            String completeStatus = data.get("complete_status");
            if (completeStatus != null) {
                switch (completeStatus) {
                    case "running":
                        status = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                                data.get("detail_info"),
                                NotificationType.ERROR));
                            status = false;
                        }
                        break;
                    case "upload_success":
                    case "success":
                        IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                            SysperfContent.IMPORT_TASK_SUCESS,
                            NotificationType.INFORMATION));
                        status = false;
                        break;
                    default:
                        IDENotificationUtil.notificationCommon(new NotificationBean(SysperfContent.IMPORT_TASK,
                            data.get("detail_info"),
                            NotificationType.ERROR));
                        status = false;
                }
            } else {
                status = false;
            }
        }
    }

    /**
     * 打开新节点
     *
     * @param message message
     * @param module  module
     */
    public void openSomeNode(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String projectName = data.get("projectName");
        int taskId = Integer.parseInt(JSONObject.toJSONString(data.get("taskId")));
        String nodeIp = JSONObject.toJSONString(data.get("nodeIp"));
        if ("null".equals(nodeIp)) {
            nodeIp = getNodeId(projectName, taskId);
        } else {
            nodeIp = nodeIp.substring(1, nodeIp.length() - 1);
        }
        LeftTreeSysperfPanel.isCreateTask = true;
        LeftTreeSysperfPanel.refreshTaskContant(taskId, projectName, nodeIp);
        showTaskNode();
        LeftTreeSysperfPanel.isCreateTask = false;
    }

    private void showTaskNode() {
        // 检查当前webView是否已经打开
        IDEFileEditorManager instance = IDEFileEditorManager.getInstance();
        String pageName = LeftTreeSysperfPanel.getNewProject().getProjectName() + '-'
            + LeftTreeSysperfPanel.getNewTask().getTaskname() + '-' +
            LeftTreeSysperfPanel.getNewNode().getNodeIP();
        List<VirtualFile> collect = instance.getOpenFiles().stream().filter(virtualFile -> virtualFile.getName()
            .contains(pageName))
            .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            instance.closeFile(collect.get(0));
        }
        ShowNodeEditor.openPage();
    }

    private String getNodeId(String projectName, int taskId) {
        List<Tasklist> taskList = SysperfProjectServer.getAllSysperfTasks(projectName);
        for (Tasklist task : taskList) {
            if (task.getId() == taskId) {
                return task.getNodeList().get(0).getNodeIP();
            }
        }
        return "";
    }
}


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

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.ConfigProperty;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.enums.Language;
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
import com.huawei.kunpeng.intellij.ui.action.FeedBackAction;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.utils.DiskUtil;
import com.huawei.kunpeng.porting.common.utils.PortingUploadUtil;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;
import com.huawei.kunpeng.porting.process.NoFractionWaitingProcess;
import com.huawei.kunpeng.porting.process.UploadFileBackProcess;
import com.huawei.kunpeng.porting.ui.dialog.wrap.PortingServerConfigWrapDialog;
import com.huawei.kunpeng.porting.ui.panel.PortingServerConfigPanel;
import com.huawei.kunpeng.porting.webview.WebFileProvider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 公共的function处理器
 *
 * @since 2020-11-18
 */
public class CommonHandler extends FunctionHandler {
    /**
     * 用户指南路径。
     */
    public static final String USER_GUIDE_PATH
        = I18NServer.toLocale("common_term_users_user_guide_path");

    private static final long MAX_FILE_SIZE = IDEConstant.MAX_FILE_SIZE << 20;

    private static final String NAME_FILTER = "nameFilter";


    /**
     * webview通用请求函数
     *
     * @param message 数据
     * @param module  模块
     */
    public void getData(MessageBean message, String module) {
        Logger.info("getData start.");

        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        RequestDataBean request = new RequestDataBean(module, data.get("url"), data.get("method"), null);
        JSONObject jsonObject = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        if (HttpMethod.POST.vaLue().equals(request.getMethod()) || HttpMethod.DELETE.vaLue()
            .equals(request.getMethod())) {
            request.setBodyData(JsonUtil.getJsonStrFromJsonObj(jsonObject.get("params")));
        }
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        if (responseBean == null) {
            return;
        }

        if (RespondStatus.DISK_NOT_ENOUGH.value().equals(responseBean.getStatus())) {
            DiskUtil.sendDiskAlertMessage();
        }
        // 回调
        invokeCallback(message.getCmd(), message.getCbid(), responseBean.getResponseJsonStr());

        Logger.info("getData end.");
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
        NotificationType type;
        switch (data.get("type")) {
            case "error":
                type = NotificationType.ERROR;
                break;
            case "warn":
                type = NotificationType.WARNING;
                break;
            default:
                type = NotificationType.INFORMATION;
                break;
        }
        String realStatus = data.get("realStatus");
        if (Objects.equals(realStatus, NoFractionWaitingProcess.CREATE_TASK_NO_WORKER)) {
            NotificationBean notificationBean = new NotificationBean("",
                I18NServer.toLocale("common_term_users_create_task_no_worker_tips"),
                NotificationType.ERROR);
            IDENotificationUtil.notificationForHyperlink(notificationBean, new ActionOperate() {
                @Override
                public void actionOperate(Object data) {
                    CommonUtil.openURI(USER_GUIDE_PATH);
                }
            });
        } else {
            if (!StringUtil.stringIsEmpty(data.get("info"))) {
                IDENotificationUtil.notificationCommon(new NotificationBean("", data.get("info"), type));
            }
        }
        Logger.info("showInfoBox end.");
    }

    /**
     * 分析时文件没权限处理。
     *
     * @param message 信息
     * @param module  模块
     */
    public void noPermissionFaqTip(MessageBean message, String module) {
        Map<String, JSONObject> resp = JsonUtil.getJsonObjFromJsonStr(message.getData());
        JSONObject data = resp.get("res");
        String content = data.getString("info");
        if (I18NServer.getCurrentLanguage().equals(Language.ZH.code())) {
            content = data.getString("infochinese");
        }
        NotificationBean notificationBean = new NotificationBean("",
            content + I18NServer.toLocale("plugins_porting_faq_tips"),
            NotificationType.ERROR);
        IDENotificationUtil.notificationForHyperlink(notificationBean,
            op -> CommonUtil.openURI(I18NServer.toLocale("plugins_porting_can_not_access_file_faq_url")));
    }

    /**
     * 上传校验文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void checkUploadFileIntellij(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        boolean isValidFile = data.get("validFile") != null;
        boolean isAllMatch = data.get("AllMatch") != null;
        // webView 传来的字段中 is_file 代表是否是文件夹
        boolean isFolder = data.get("is_file") != null;
        // 判断是否允许选择多个文件
        boolean isAllowMultipleFile = data.get("isDuplicated") != null && "true".equals(data.get("isDuplicated"));
        String path = data.get("uploadFilePath");
        if (!FileUtil.validateFilePath(path)) {
            return;
        }
        if (!isAllowMultipleFile && !isContainsNameFilter(data)) {
            FileChooserDialog fileChooser = FileChooserFactory.getInstance()
                .createFileChooser(
                    new FileChooserDescriptor(true, true, true, false, false, false)
                        .withFileFilter(file -> checkedFile(data, isValidFile, isAllMatch, isFolder, file)),
                    CommonUtil.getDefaultProject(), null);
            // virtualFile对于jar,zip文件路径获取异常，重新拼接path
            path = getVirtualFilePath(fileChooser);
            if ("".equals(path)) {
                return;
            }
        }
        ResponseBean responseBean = uploadFile(data, isFolder, path);
        if (responseBean == null) {
            return;
        }
        invokeCallback("getData", message.getCbid(), responseBean.getResponseJsonStr());
    }

    private boolean isContainsNameFilter(Map<String, String> data) {
        return !Objects.isNull(data.get(NAME_FILTER)) && !Boolean.valueOf(data.get(NAME_FILTER));
    }

    @Nullable
    private ResponseBean uploadFile(Map<String, String> data, boolean isFile, String path) {
        File originFile = new File(path);
        ResponseBean responseBean = null;
        if (!FileUtil.isNotEmptyDir(originFile)) {
            errorNotifyWithNoTitle(I18NServer.toLocale("plugins_porting_tip_empty_file_upload_failed"));
            return responseBean;
        }
        if (FileUtil.getTotalSizeOfDirectory(originFile, MAX_FILE_SIZE) > MAX_FILE_SIZE) {
            errorNotifyWithNoTitle(I18NServer.toLocale("plugins_porting_message_fileExceedMaxSize"));
            return responseBean;
        }
        // 前端校验文件夹名称是否包含中文及非法字符
        responseBean = new ResponseBean();
        if (FileUtil.isContainChinese(originFile.getName()) || !FileUtil.validateFileName(originFile.getName())) {
            errorNotifyWithNoTitle(I18NServer.toLocale("plugins_port_file_name_illegal_tip"));
        } else {
            // 上传前校验文件是否存在以及是否可以上传
            File file = getUploadFile(originFile, isFile);
            if (file != null) {
                responseBean = PortingUploadUtil.preUpload(file, data, path);
            }
        }
        return responseBean;
    }

    private void errorNotifyWithNoTitle(String content) {
        IDENotificationUtil.notificationCommon(new NotificationBean("", content, NotificationType.ERROR));
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
     * 上传多文件校验文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void intellijDepPackage(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        boolean validFile = data.get("validFile") != null;
        boolean isAllMatch = data.get("AllMatch") != null;
        boolean isFile = data.get("is_file") != null;
        String path = null;
        FileChooserDialog fileChooser = FileChooserFactory.getInstance()
            .createFileChooser(new FileChooserDescriptor(true, true, true, false, false, true).withFileFilter(file -> {
                return checkedDepedFile(data, validFile, isAllMatch, isFile, file);
            }), CommonUtil.getDefaultProject(), null);
        final VirtualFile[] virtualFiles = fileChooser.choose(CommonUtil.getDefaultProject(), VirtualFile.EMPTY_ARRAY);

        List<Map<String, Object>> fileInfo = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            // virtualFile对于jar,zip文件路径获取异常，重新拼接path
            path = virtualFile.getPath().substring(0, virtualFile.getPath().lastIndexOf(virtualFile.getName()))
                + virtualFile.getName();
            File file = getUploadFile(new File(path), isFile);
            // 上传前校验文件是否存在已经是否可以上传
            if (file == null) {
                continue;
            }
            Map<String, Object> msg = new HashMap<>();
            msg.put("name", file.getName());
            msg.put("size", file.length());
            msg.put("path", path);
            fileInfo.add(msg);
        }
        invokeCallback("getData", message.getCbid(), JsonUtil.getJsonStrFromJsonObj(fileInfo));
    }

    private boolean checkedFile(Map<String, String> data, boolean validFile, boolean isAllMatch,
        boolean isFolder, VirtualFile file) {
        if (file.getExtension() == null) {
            return isAllMatch;
        }
        // 过滤掉大于1G的文件
        if (file.getLength() > MAX_FILE_SIZE) {
            return false;
        }
        if (validFile) {
            String[] suffixs = data.get("validFile").split(",");
            return StringUtil.verifyFileSuffix(file.getName(), suffixs);
        }
        if (isFolder && !file.isDirectory()) {
            return false;
        }
        return true;
    }

    private boolean checkedDepedFile(Map<String, String> data, boolean validFile, boolean isAllMatch,
        boolean isFile, VirtualFile file) {
        if (file.getExtension() == null) {
            return isAllMatch;
        }
        if (validFile) {
            String[] suffixs = data.get("validFile").split(",");
            return StringUtil.verifyFileSuffix(file.getName(), suffixs);
        }
        if (isFile && !file.isDirectory()) {
            return false;
        }
        return true;
    }

    /**
     * 上传多文件文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void uploadMultipleFiles(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        Map<String, JSONArray> paths = JsonUtil.getJsonObjFromJsonStr(message.getData());
        boolean isFile = data.get("is_file") != null;
        JSONArray uploadFilePath = paths.get("uploadFilePath");
        File[] files = new File[uploadFilePath.size()];
        for (int i = 0; i < uploadFilePath.size(); i++) {
            files[i] = new File(uploadFilePath.get(i).toString());
        }
        for (Object path : uploadFilePath) {
            File file = getUploadFile(new File(path.toString()), isFile);
            if (file == null) {
                continue;
            }
            PortingUploadUtil.setZipFile(null);
            PortingUploadUtil.copyUploadFile(file, data);
        }
        processForWaiting(I18NServer.toLocale("plugins_common_porting_upload_file"), files, data, message);
    }

    private void processForWaiting(String title, File[] files, Map<String, String> data, MessageBean message) {
        Project projectDef = CommonUtil.getDefaultProject();
        final ProgressManager progress = ProgressManager.getInstance();
        Window window = CommonUtil.getDefaultWindow();
        CommonUtil.setBackGroundProcessWindowOpen(true, window);
        progress.run(new Task.Backgroundable(projectDef, title, true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
            final UploadFileBackProcess process = new UploadFileBackProcess(files, data, message.getCbid());

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ProgressIndicator progressIndicator = progress.getProgressIndicator();
                try {
                    runTask(progressIndicator);
                } catch (ProcessCanceledException exception) {
                    // 任务非正常退出
                    process.cancel(progressIndicator);
                }
            }

            private void runTask(ProgressIndicator indicator) {
                // 执任务
                RequestDataBean requestData = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
                    PortingUploadUtil.UPLOAD_FILEDATA_URL, HttpMethod.POST.vaLue(), "");
                ResponseBean responseBean = PortingUploadUtil.showWaiting(requestData,
                    PortingUploadUtil.UPLOAD_FILEDATA_URL, indicator);
                if (!RespondStatus.UPLOAD_FILE_TOP_LIMIT.value().equals(responseBean.getRealStatus())) {
                    ApplicationManager.getApplication().executeOnPooledThread(() -> process.runTask(null));
                } else {
                    invokeCallback("getData", message.getCbid(), responseBean.getResponseJsonStr());
                    IDENotificationUtil.notificationCommon(new NotificationBean(title,
                        CommonUtil.getRspTipInfo(responseBean), NotificationType.ERROR));
                }
            }

            /**
             * 执行成功后
             */
            @Override
            public void onSuccess() {
                super.onSuccess();
                process.success(progress.getProgressIndicator());
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
                process.cancel(progress.getProgressIndicator());
                Logger.info("Cancel runnable task success.");
            }
        });
    }

    /**
     * 上传文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void uploadFileIntellij(MessageBean message, String module) {
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        boolean isFile = data.get("is_file") != null;
        String path = data.get("uploadFilePath");
        if (!FileUtil.validateFilePath(path)) {
            return;
        }
        File file = getUploadFile(new File(path), isFile);
        if (file == null) {
            return;
        }
        PortingUploadUtil.copyUploadFile(file, data);
        if (PortingUploadUtil.getZipFile() != null) {
            file = PortingUploadUtil.getZipFile();
        }
        // 上传文件
        UploadFileBackProcess process = new UploadFileBackProcess(file, data, message.getCbid());
        process.processForCommon(null, file.getName(), process);
    }

    /**
     * 源码分析任务时
     * 若上传的是文件夹则压缩为zip后返回
     *
     * @param file   上传的文件
     * @param isFile 是否文件夹
     * @return 处理后的文件
     */
    private File getUploadFile(File file, boolean isFile) {
        File uploadFile;
        if (isFile) {
            uploadFile = FileUtil.fileToZip(file);
        } else {
            uploadFile = file;
        }
        return uploadFile;
    }

    /**
     * webview 根据key获取缓存数据
     *
     * @param message 数据
     * @param module  模块
     */
    public void getGlobleState(MessageBean message, String module) {
        Logger.info("getGlobleState start.");

        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        data = data.getJSONObject("data");
        JSONArray keys = data.getJSONArray("keys");
        JSONObject dataReturn = new JSONObject();
        keys.stream().forEach((object) -> {
            Object value = IDEContext.getValueFromGlobalContext(module, object.toString());
            if (value == null && !Objects.equals(object, "anyCtaskId")) {
                dataReturn.put(object.toString(), new HashMap<>());
            } else {
                dataReturn.put(object.toString(), value);
            }
        });
        invokeCallback(message.getCmd(), message.getCbid(), dataReturn.toJSONString());
        Logger.info("getGlobleState end.");
    }

    /**
     * 根据key设置缓存数据
     *
     * @param message 数据
     * @param module  模块
     */
    public void setGlobleState(MessageBean message, String module) {
        Logger.info("setGlobleState start.");

        JSONObject data = JsonUtil.getJsonObjectFromJsonStr(message.getData());
        data = data.getJSONObject("data");
        JSONArray list = data.getJSONArray("list");
        list.stream().forEach((object) -> {
            if (object instanceof Map) {
                Map<String, Object> map = (Map) object;
                // 设置全局state的值
                Object key = map.get("key");
                Object value = map.get("value");
                IDEContext.setValueForGlobalContext(module, key.toString(), value);
            }
        });
        Logger.info("setGlobleState end.");
    }

    /**
     * 关闭文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void closePanel(MessageBean message, String module) {
        Logger.info("closePanel start.");

        Project project = CommonUtil.getDefaultProject();
        VirtualFile file = IDEFileEditorManager.getInstance(project).getSelectFile();
        WebFileEditor webViewPage = WebFileProvider.getWebViewPage(project, file);
        if (webViewPage != null) {
            webViewPage.dispose();
        }
        Logger.info("closePanel end.");
    }

    /**
     * 关闭文件
     *
     * @param message 数据
     * @param module  模块
     */
    public void showIntellijDialog(MessageBean message, String module) {
        Logger.info("showIntellijDialog start.");
        Map<String, String> data = JsonUtil.getJsonObjFromJsonStr(message.getData());
        String intellijDialogType = data.get("intellijDialogType");
        if (intellijDialogType == null) {
            Logger.warn("intellijDialogType is null.");
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            IDEBaseDialog dialogWrapper = getDialogWrapper(intellijDialogType);
            if (dialogWrapper != null) {
                dialogWrapper.displayPanel();
            }
        });
    }

    private IDEBaseDialog getDialogWrapper(String intellijDialogType) {
        IDEBaseDialog dialogWrapper = null;
        switch (intellijDialogType) {
            case "portServerConfigDialog":
                IDEBasePanel panel = new PortingServerConfigPanel(null);
                dialogWrapper = new PortingServerConfigWrapDialog(PortingUserManageConstant.CONFIG_TITLE, panel);
                break;
            case "noNetworkTipDialog":
                FeedBackAction.goFeedBack(I18NServer.toLocale("plugins_porting_feedback"));
                break;
            default:
                break;
        }
        return dialogWrapper;
    }

    /**
     * 读取url
     *
     * @param message 数据
     * @param module  模块
     */
    public void readUrlConfig(MessageBean message, String module) {
        Map urlConfig = FileUtil.ConfigParser.parseJsonConfigFromFile(PortingIDEConstant.URL_CONFIG_PATH);
        invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(urlConfig));
    }

    /**
     * 获取后台worker工作状态 及 无百分比进度条显示。
     *
     * @param title    进度条标题
     * @param taskType 任务类型
     * @param taskId   任务ID
     * @param cBid     回调ID
     * @param status   转态
     * @return 是否是等待中状态。
     */
    public static boolean getWorkerStatus(String title, String taskType, String taskId, String cBid, String status) {
        String taskStatus = getStatus(taskType, taskId);
        if (Objects.equals(taskStatus, NoFractionWaitingProcess.PROGRESS_WAITE_WORKER)) { // 无资源等待中任务
            // 显示进度条。
            final Task.Backgroundable taskProcess = new NoFractionWaitingProcess(
                title,
                taskType, taskId, cBid, status);
            CommonUtil.setBackGroundProcessWindowOpen(true, CommonUtil.getDefaultWindow());
            final ProgressManager progress = ProgressManager.getInstance();
            progress.run(taskProcess);
            return true;
        }
        return false;
    }

    /**
     * 获取状态
     *
     * @param taskType taskType
     * @param taskId   taskId
     * @return 状态
     */
    public static String getStatus(String taskType, String taskId) {
        String url = "/task/progress/?task_type=" + taskType + "&" + "task_id=" + taskId;
        RequestDataBean data = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
            HttpMethod.GET.vaLue(), "");
        data.setNeedProcess(true);
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(data);
        if (responseBean == null) {
            return "";
        }
        return StringUtil.stringIsEmpty(responseBean.getRealStatus())
            ? responseBean.getStatus() : responseBean.getRealStatus();
    }

    /**
     * 返回前端版本号
     *
     * @param message 信息
     * @param module 模块
     */
    public static void readVersionConfig(MessageBean message, String module) {
        Map config = FileUtil.ConfigParser.parseJsonConfigFromFile(IDEConstant.CONFIG_PATH);
        if (config.get(ConfigProperty.PORT_VERSION.vaLue()) instanceof List) {
            List<?> list = (List<?>) config.get(ConfigProperty.PORT_VERSION.vaLue());
            invokeCallback(message.getCmd(), message.getCbid(), JsonUtil.getJsonStrFromJsonObj(list));
        }
    }
}


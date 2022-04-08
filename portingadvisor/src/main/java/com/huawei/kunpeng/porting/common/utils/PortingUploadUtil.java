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

package com.huawei.kunpeng.porting.common.utils;

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
import com.huawei.kunpeng.porting.common.PortingIDEContext;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.ScanType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 上传文件
 *
 * @since v1.0
 */
public class PortingUploadUtil {
    /**
     * 多文件上传接口
     */
    public static final String UPLOAD_FILEDATA_URL = "/portadv/autopack/data/";

    private static final String UPLOAD_URL = "/portadv/tasks/upload/";

    private static final String UPLOAD_FILEPACK_URL = "/portadv/autopack/package/";

    private static final String SCAN_TYPE_F = "4";

    private static final String SCAN_TYPE_T = "3";

    private static final String NAME_FILTER = "nameFilter";

    private static final String A_ARCH_64 = "aarch64";

    private static final String ARM_64 = "arm64";

    private static File zipFile;

    private static String fileName;

    private static final String SCAN_TYPE = "scan_type";

    private static final String CHOICE = "choice";

    private static final String NEW_FILE_NAME = "newFileName";

    private static final String NEED_UNZIP = "need_unzip";

    private static final String CODE_PATH = "code_path";

    /**
     * 缓冲大小
     */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param data 上传参数
     * @param indicator indicator
     * @return ResponseBean
     */
    public static ResponseBean uploadProcess(File[] file, Map<String, String> data, ProgressIndicator indicator) {
        // 弹出上传文件进度条
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, UPLOAD_FILEDATA_URL,
                HttpMethod.POST.vaLue(), "");
        String scanType = data.get(SCAN_TYPE);
        message.setFile(file);
        message.setNeedUploadFile(true);
        message.setNeedProcess(false);
        message.setNeedBackground(true);
        message.setIndicator(indicator);
        message.setScanType(scanType);
        if (ScanType.WEAK_UPLOAD_COMPILED_FILE.value().equals(scanType)) {
            message.setConnRequestProperty(new HashMap<>() {
                {
                    put("code-path", message.getCodePath());
                }
            });
        }
        setMessage(data, message);
        return PortingHttpsServer.INSTANCE.requestData(message);
    }

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param data 上传参数
     * @param indicator indicator
     * @return ResponseBean
     */
    public static ResponseBean uploadProcess(File file, Map<String, String> data, ProgressIndicator indicator) {
        String url = null;
        String scanType = data.get(SCAN_TYPE);
        if (scanType.equals(SCAN_TYPE_F)) {
            url = UPLOAD_FILEDATA_URL;
        } else if (scanType.equals(SCAN_TYPE_T)) {
            url = UPLOAD_FILEPACK_URL;
        } else {
            url = UPLOAD_URL;
        }
        // 组装请求参数
        RequestDataBean message = createRequestData(file, url, scanType, indicator, data);
        // 上传前判断是否需要等待
        ResponseBean responseBean = showWaiting(message, url, indicator);
        if (!RespondStatus.UPLOAD_FILE_TOP_LIMIT.value().equals(responseBean.getRealStatus())) {
            message.setUrl(url);
            // 有空闲线程上传之前设置needUploadFile为true
            message.setNeedUploadFile(true);
            // 真正上传
            responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        }
        if (responseBean != null) {
            deleteFile(zipFile);
            // 显示上传结果提示
            boolean isSuccess = RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus());
            String title = "";
            if (data.get("title") != null) {
                title = data.get("title");
            }

            String content = isSuccess
                    ? file.getName() + " " + I18NServer.toLocale("plugins_porting_tip_file_upload_suc")
                    : CommonUtil.getRspTipInfo(responseBean);
            NotificationType type = isSuccess ? NotificationType.INFORMATION : NotificationType.ERROR;
            if (!(ScanType.SOURCE_CODING_SCAN.value().equals(scanType)
                    || ScanType.WEAK_UPLOAD_ZIP.value().equals(scanType)
                    || ScanType.PRE_CHECK.value().equals(scanType)
                    || ScanType.BYTE_CHECK.value().equals(scanType))) {
                IDENotificationUtil.notificationCommon(new NotificationBean(title, content, type));
            }
        }
        return responseBean;
    }

    /**
     * 上传文件前判断是否需要排队等待
     *
     * @param message 请求头信息
     * @param url 上传url
     * @param indicator 进度条
     * @return 接口返回数据
     */
    public static ResponseBean showWaiting(RequestDataBean message, String url, ProgressIndicator indicator) {
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        boolean uploadWait = RespondStatus.UPLOAD_FILE_TOP_LIMIT.value().equals(responseBean.getRealStatus());
        int i = 0;
        // 轮询20次
        while (uploadWait && i < 20) {
            i++;
            indicator.checkCanceled();
            indicator.setText(I18NServer.toLocale("plugins_common_porting_upload_task_reached"));
            try {
                // 30s查询一次
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
            }
            message.setUrl(url);
            responseBean = PortingHttpsServer.INSTANCE.requestData(message);
            uploadWait = RespondStatus.UPLOAD_FILE_TOP_LIMIT.value().equals(responseBean.getRealStatus());
        }
        return responseBean;
    }

    private static RequestDataBean createRequestData(File file, String url, String scanType,
        ProgressIndicator indicator, Map<String, String> data) {
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
                HttpMethod.POST.vaLue(), "");
        message.setFile(new File[] {file});
        message.setNeedUploadFile(false);
        message.setNeedProcess(true);
        message.setNeedBackground(true);
        message.setIndicator(indicator);
        message.setScanType(scanType);
        if (ScanType.WEAK_UPLOAD_COMPILED_FILE.value().equals(scanType)) {
            message.setConnRequestProperty(new HashMap<>() {
                {
                    put("code-path", message.getCodePath());
                }
            });
        }
        setMessage(data, message);
        // 源码分析任务下上传的文件是zip格式且服务器已存在的情况下选择另存为的操作处理
        if (ScanType.SOURCE_CODING_SCAN.value().equals(message.getScanType()) && message.getUploadFileName() != null
                && data.get("is_file") != null) {
            sourceScanFileHandle(message);
        }
        return message;
    }

    /**
     * 源码扫描任务下对zip文件另存为的额外处理
     * （生成的文件会自动被回收）
     *
     * @param message 请求信息
     */
    private static void sourceScanFileHandle(RequestDataBean message) {
        String uploadFileName = message.getUploadFileName();
        if (uploadFileName != null && uploadFileName.endsWith(".zip")) {
            String path = CommonUtil.getPluginInstalledPathFile
                    (PortingIDEConstant.PORTING_WORKSPACE_TEMP + PortingIDEConstant.PATH_SEPARATOR);
            if (!FileUtil.validateFilePath(path) || !FileUtil.validateFileName(fileName)) {
                return;
            }

            if (message.getFile() == null || message.getFile().length == 0) {
                return;
            }
            FileUtil.unzipFile(message.getFile()[0].toString(), path);
            File renameFile = new File(path + fileName);
            File newFile = new File(renameFile.getParent() + PortingIDEConstant.PATH_SEPARATOR +
                    message.getUploadFileName().replace(".zip", "") + PortingIDEConstant.PATH_SEPARATOR);
            newFile.mkdirs();
            FileUtil.copyFolder(renameFile.getPath(), newFile.getPath());
            message.setFile(new File[] {FileUtil.fileToZip(newFile)});
        }
    }

    /**
     * 上传文件前校验
     *
     * @param file 上传文件
     * @param data 上传参数
     * @param path 本地文件路径
     * @return 响应数据
     */
    public static ResponseBean preUpload(File file, Map<String, String> data, String path) {
        zipFile = null;
        // 设置上传模式为 normal
        PortingIDEContext.setValueForGlobalContext(null, "fileModel", data.get(CHOICE));
        boolean isNeedNewName = !"normal".equals(data.get(CHOICE));
        String newFileName = file.getName();
        if (isNeedNewName) {
            newFileName = String.valueOf(data.get("saveFileName"));
        }
        ResponseBean responseBean = new ResponseBean();
        Map<String, Object> message = new HashMap<>();
        if (Boolean.parseBoolean(data.get(NAME_FILTER))) {
            if (file.getName().toLowerCase(Locale.ROOT).contains(A_ARCH_64) || file.getName()
                    .toLowerCase(Locale.ROOT)
                    .contains(ARM_64)) {
                message.put("isCompatible", true);
                message.put("fileName", newFileName);
            } else {
                message = messageProcess(file, data, newFileName);
            }
        } else {
            message = messageProcess(file, data, newFileName);
        }
        message.put("uploadFilePath", path);
        responseBean.setResponseJsonStr(JsonUtil.getJsonStrFromJsonObj(message));
        return responseBean;
    }

    private static Map<String, Object> messageProcess(File file, Map<String, String> data, String newFileName) {
        // 检查文件是否已存在
        ResponseBean responseBean = uploadInDiffMode(file, data, newFileName);
        if (responseBean == null) {
            return  new HashMap<>();
        }
        return JsonUtil.getJsonObjectFromJsonStr(responseBean.getResponseJsonStr());
    }

    /**
     * 拷贝上传文件
     *
     * @param file 文件
     * @param data 数据参数
     */
    public static void copyUploadFile(File file, Map<String, String> data) {
        boolean isNeedSaveNewName = !"normal".equals(data.get(CHOICE));
        String newName = String.valueOf(data.get("saveFileName"));
        if (isNeedSaveNewName && !Objects.equals(newName, file.getName())) {
            if (!FileUtil.validateFileName(newName)) {
                return;
            }
            if (data.get("is_file") != null && newName.endsWith(".zip")) {
                fileName = file.getName().replace(".zip", "");
            }
            copyFile(file, newName);
            Logger.info("File already existed, save as : {}", newName);
            data.put(NEW_FILE_NAME, newName);
        }
    }

    /**
     * 采用不同模式上传源码压缩包
     *
     * @param file 源码压缩包
     * @param data 参数
     * @param newFileName 上传文件名
     * @return ResponseBean: 服务端响应
     */
    public static ResponseBean uploadInDiffMode(File file, Map<String, String> data, String newFileName) {
        Map<String, Object> obj = new HashMap<>();
        obj.put(CHOICE, data.get(CHOICE));
        obj.put("file_name", newFileName);
        obj.put("file_size", file.length());
        obj.put(NEED_UNZIP, data.get(NEED_UNZIP));
        obj.put(SCAN_TYPE, data.get(SCAN_TYPE));
        obj.put(CODE_PATH, data.get(CODE_PATH));
        String url = data.get("url");
        RequestDataBean request = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING, url,
                HttpMethod.POST.vaLue(), "");
        request.setBodyData(JsonUtil.getJsonStrFromJsonObj(obj));
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(request);
        if (responseBean != null) {
            if (RespondStatus.UPLOAD_FILE_NOT_SUPPORT.value().equals(responseBean.getStatus())
                    || RespondStatus.WRONG_FILE_TYPE.value().equals(responseBean.getStatus())) {
                Map<String, String> dataMap = JsonUtil.getJsonObjFromJsonStr(responseBean.getData());
                if (dataMap.size() == 0) {
                    IDENotificationUtil.notifyCommonForResponse("", responseBean.getStatus(), responseBean);
                    responseBean.setStatus(RespondStatus.PROCESS_STATUS_FAILED.value());
                }
            }
            if (RespondStatus.DISK_NOT_ENOUGH.value().equals(responseBean.getStatus())) {
                DiskUtil.sendDiskAlertMessage();
            }
        }
        return responseBean;
    }

    /**
     * 获取当前上传的文件
     *
     * @return 上传的文件
     */
    public static File getZipFile() {
        return zipFile;
    }

    public static void setZipFile(File zipFile) {
        PortingUploadUtil.zipFile = zipFile;
    }

    /**
     * 删除zipFile文件
     */
    public static void deleteZipFile() {
        deleteFile(zipFile);
        zipFile = null;
    }

    /**
     * 删除另存为copy的文件
     *
     * @param zipFile zipFile
     */
    private static void deleteFile(File zipFile) {
        if (zipFile != null) {
            try {
                Files.delete(Paths.get(zipFile.getPath()));
            } catch (IOException e) {
                Logger.info("File delete zipFile fail.");
            }
        }
    }

    /**
     * set RequestDataBean
     *
     * @param data data
     * @param message message
     */
    public static void setMessage(Map<String, String> data, RequestDataBean message) {
        if (data.get(NEED_UNZIP) != null) {
            message.setNeedUnzip(data.get(NEED_UNZIP));
        }
        if (data.get("not_chmod") != null) {
            message.setNotChmod(data.get("not_chmod"));
        }
        if (data.get(NEW_FILE_NAME) != null) {
            message.setUploadFileName(data.get(NEW_FILE_NAME));
        }
        if (data.get(CODE_PATH) != null) {
            message.setCodePath(data.get(CODE_PATH));
        }
    }

    /**
     * copy file
     *
     * @param file file
     * @param newName newName
     */
    public static void copyFile(File file, String newName) {
        File newFile = new File(file.getParent() + "/" + newName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFile);
            Files.copy(Paths.get(file.getPath()), out);
            zipFile = newFile;
        } catch (IOException e) {
            Logger.info("File copy fail: {}", newName);
        } finally {
            if (out != null) {
                FileUtil.closeStreams(out, null);
            }
        }
    }
}

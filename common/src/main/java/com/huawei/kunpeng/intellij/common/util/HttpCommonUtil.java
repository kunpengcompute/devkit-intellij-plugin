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

package com.huawei.kunpeng.intellij.common.util;

import com.huawei.kunpeng.intellij.common.IDEContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.task.UploadFileProcess;

import com.intellij.notification.NotificationType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 公共工具类
 *
 * @since 1.0.0
 */
public class HttpCommonUtil {
    /**
     * 单位KB。
     */
    private static final int KB = 1024;

    /**
     * 单位兆。
     */
    private static final int MB = KB * 1024;

    /**
     * "."
     */
    private static final String POINT_STR = ".";

    /**
     * ")"
     */
    private static final String RIGHT_BRACKET_STR = ")";

    /**
     * "("
     */
    private static final String LEFT_BRACKET_STR = "(";

    /**
     * 下载压缩包
     *
     * @param conn    连接
     * @param request 请求
     */
    public static void downZip(HttpURLConnection conn, RequestDataBean request) {
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(request.getDownloadPtah()));
            int len = 0;
            byte[] buffer = new byte[KB];
            zipOutputStream.putNextEntry(new ZipEntry(request.getDownloadFileName()));
            while ((len = conn.getInputStream().read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
        } catch (IOException exception) {
            Logger.error("downZip HttpUtils.sendSSLRequest IOException");
        } finally {
            FileUtil.closeStreams(zipOutputStream, null);
        }
    }

    /**
     * 设置上传类型的请求参数
     *
     * @param conn    连接
     * @param request 数据
     * @throws IOException
     */
    public static void setRequestDataForUpload(HttpURLConnection conn, RequestDataBean request) throws IOException {
        if (request.getFile() == null || request.getFile()[0] == null) {
            return;
        }
        // 设置formdata数据
        String boundary = "----WebKitFormBoundary" + new Date().getTime();
        Map<String, Object> map = fileHeader(request, boundary);
        String before = map.get("str").toString();
        int len = (int) map.get("len");
        String end = "--" + boundary + "--";
        // 设置文件上传请求参数
        // 禁掉缓存,需要设置上传数据的大小
        conn.setFixedLengthStreamingMode(before.getBytes(IDEContext.getCurrentCharset()).length + len + end.getBytes(
                IDEContext.getCurrentCharset()).length);
        connConfig(request, conn, boundary);
        conn.connect();
        // 开始上传文件
        if (request.isNeedProcess()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("before", before);
            params.put("end", end);
            params.put("file", request.getFile()[0]);
            params.put("out", new DataOutputStream(conn.getOutputStream()));
            if (request.isNeedBackground()) {
                UploadUtil.uploadFileWithProgress(params, request.getIndicator());
            } else {
                // 创建上传文件进度条
                UploadFileProcess process = new UploadFileProcess(params);
                process.processForONFore(null,
                        I18NServer.toLocale("plugins_common_file_upload"), process, false);
            }
        } else {
            // 不需要进度条的方式
            uploadFile(new DataOutputStream(conn.getOutputStream()), request.getFile(), before, end);
        }
    }

    /**
     * 设置下载类型的请求参数
     *
     * @param conn    连接
     * @param request 数据
     * @throws IOException
     */
    public static void setRequestDataForDownLoad(HttpURLConnection conn, RequestDataBean request) throws IOException {
        // 设置文件上传请求参数
        conn.setAllowUserInteraction(false);
        // 禁掉缓存,需要设置下载数据的大小
        conn.setRequestProperty("Accept-Ranges", "bytes");
        conn.connect();

        OutputStream out = conn.getOutputStream();
        out.write(StringUtil.getStrCharsetByOSToServer(request.getBodyData()).getBytes(IDEContext.getCurrentCharset()));
        out.flush();

        if (conn.getResponseCode() != 200) {
            return;
        }
        // 非FileResponse对象该字段不存在， 无需下载
        if (conn.getHeaderField("content-disposition") == null) {
            return;
        }
        // 开始下载文件
        if (request.isNeedDownloadZip()) {
            downZip(conn, request);
        } else {
            downComFile(conn, request);
        }
    }

    /**
     * 下载非压缩文件。
     *
     * @param conn    连接
     * @param request 请求
     */
    public static void downComFile(HttpURLConnection conn, RequestDataBean request) {
        String filePath = request.getDownloadPtah() + IDEConstant.PATH_SEPARATOR + request.getDownloadFileName();
        File file = new File(filePath);
        if (request.isAutoRename()) {
            file = getNewFile(filePath);
        }
        boolean isDeleteFile = false;
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            int len = 0;
            byte[] buffer = new byte[KB];
            while ((len = conn.getInputStream().read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
                if (Thread.currentThread().isInterrupted()) {
                    Logger.error("downComFile InterruptedException");
                    request.setCancel(true);
                    isDeleteFile = true;
                    break;
                }
            }
        } catch (IOException exception) {
            Logger.error("downComFile HttpUtils.sendSSLRequest IOException");
            isDeleteFile = true;
            NotificationBean notificationBean = new NotificationBean(
                    CommonI18NServer.toLocale("plugins_common_download_bc_files_title"),
                    filePath + CommonI18NServer.toLocale("plugins_common_download_files_fail"),
                    NotificationType.ERROR);
            IDENotificationUtil.notificationCommon(notificationBean);
        }
        if (isDeleteFile) {
            // 删除脏文件
            if (FileUtil.deleteDir(file, null)) {
                Logger.info("{} delete file success", file);
            } else {
                Logger.error("{} delete file fail", file);
            }
        }
    }

    /**
     * 获取新的文件名称。
     *
     * @param filePath filePath
     * @return 返回行的文件
     */
    private static File getNewFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            String fileNewName = getFileNewName(file.getName());
            file = getNewFile(file.getParent() + IDEConstant.PATH_SEPARATOR + fileNewName);
        }
        return file;
    }

    private static String getFileNewName(String name) {
        int lastIndexOfPoint = name.lastIndexOf(POINT_STR);
        // 文件名称
        String title = name.substring(0, lastIndexOfPoint);
        // 文件后缀
        String suffixName = name.substring(lastIndexOfPoint);
        int lastIndexOfRightBracket = title.lastIndexOf(RIGHT_BRACKET_STR);
        if (lastIndexOfRightBracket < 0) {
            title = title + LEFT_BRACKET_STR + 1 + RIGHT_BRACKET_STR;
        } else {
            int lastIndexOfLeftBracket = title.lastIndexOf(LEFT_BRACKET_STR);
            int num = Integer.valueOf(title.substring(lastIndexOfLeftBracket + 1, lastIndexOfRightBracket));
            title = title.substring(0, lastIndexOfLeftBracket);
            title = title + LEFT_BRACKET_STR + (num + 1) + RIGHT_BRACKET_STR;
        }
        return title + suffixName;
    }

    /**
     * 拼接报文头
     *
     * @param request  请求参数
     * @param boundary 头
     * @return 大小
     */
    private static Map<String, Object> fileHeader(RequestDataBean request, String boundary) {
        int len = 0;
        StringBuilder str = new StringBuilder();
        for (File file : request.getFile()) {
            if (request.getFile().length > 1) {
                Path path = Paths.get(file.toURI());
                String type = "application/octet-stream";
                try {
                    type = Files.probeContentType(path);
                } catch (IOException e) {
                    Logger.error("an I/O error occurs");
                }
                str.append("--" + boundary + IDEConstant.UPLOAD_FILE_LINE_END)
                        .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(StringUtil.getStrCharsetByOSToServer(file.getName()))
                        .append("\"" + IDEConstant.UPLOAD_FILE_LINE_END)
                        .append("Content-Type:")
                        .append(type)
                        .append(IDEConstant.UPLOAD_FILE_LINE_END)
                        .append(IDEConstant.UPLOAD_FILE_LINE_END + IDEConstant.UPLOAD_FILE_LINE_END);
            } else {
                str.append("--" + boundary + IDEConstant.UPLOAD_FILE_LINE_END)
                        .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(StringUtil.getStrCharsetByOSToServer(
                                StringUtil.stringIsEmpty(request.getUploadFileName()) ? file.getName() :
                                        request.getUploadFileName()))
                        .append("\"" + IDEConstant.UPLOAD_FILE_LINE_END + IDEConstant.UPLOAD_FILE_LINE_END);
                if (file.length() == 0) {
                    str.append(IDEConstant.UPLOAD_FILE_LINE_END);
                }
            }
            len += file.length();
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("len", len);
        map.put("str", str);
        return map;
    }

    /**
     * 开始上传文件
     *
     * @param out    URL连接流
     * @param files  文件集合
     * @param before formdatd信息头
     * @param end    formdatd信息尾
     * @throws IOException
     */
    public static void uploadFile(OutputStream out, File[] files, String before, String end) throws IOException {
        out.write(before.getBytes(IDEContext.getCurrentCharset()));
        for (File file : files) {
            // 逐文件上传
            doUploadFileDetail(out, file);
        }
        out.write(end.getBytes(IDEContext.getCurrentCharset()));
        out.flush();
    }

    /**
     * 上传文件
     *
     * @param out  输出流
     * @param file 文件
     * @throws IOException 异常信息
     */
    public static void doUploadFileDetail(OutputStream out, File file) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            int bytes = 0;
            byte[] buffer = new byte[MB];
            while ((bytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
            }
        } catch (IOException exception) {
            Logger.error("uploadFile error, IOException");
            throw exception;
        }
    }

    /**
     * 连接信息
     *
     * @param request  请求
     * @param conn     连接
     * @param boundary 报文头
     */
    public static void connConfig(RequestDataBean request, HttpURLConnection conn, String boundary) {
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty("Connection", "close");
        String choice = "normal";
        Object fileModel = IDEContext.getValueFromGlobalContext(null, "fileModel");
        if (fileModel != null) {
            choice = fileModel.toString();
        }
        conn.addRequestProperty("choice", choice);
        conn.addRequestProperty("scan-type", request.getScanType() == null ? "0" : request.getScanType());
        conn.addRequestProperty("need-unzip",
                request.getNeedUnzip() == null ? String.valueOf(true) : request.getNeedUnzip());
        conn.addRequestProperty("not-chmod",
                request.getNotChmod() == null ? String.valueOf(true) : request.getNotChmod());
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("filename", CommonUtil.normalizeForString(request.getFile()[0].getName()));
        if (!Objects.equals(null, request.getConnRequestProperty())) {
            request.getConnRequestProperty().forEach((key, value) -> {
                conn.addRequestProperty(key, value);
            });
        }
    }
}

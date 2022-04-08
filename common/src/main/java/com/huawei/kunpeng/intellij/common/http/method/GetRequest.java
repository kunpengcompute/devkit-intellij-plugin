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

package com.huawei.kunpeng.intellij.common.http.method;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.HttpCommonUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Get请求类型
 *
 * @since 1.0.0
 */
public class GetRequest implements MethodRequest {
    /**
     * 设置请求参数
     *
     * @param conn    连接
     * @param request 数据
     */
    @Override
    public void setRequestData(HttpURLConnection conn, RequestDataBean request) {
    }

    /**
     * 设置上传类型的请求参数
     *
     * @param conn    连接
     * @param request 数据
     * @throws IOException io异常
     */
    @Override
    public void setRequestDataForUpload(HttpURLConnection conn, RequestDataBean request) throws IOException {
    }

    /**
     * 设置下载类型的请求参数
     *
     * @param conn    连接
     * @param request 数据
     * @throws IOException io异常
     */
    @Override
    public void setRequestDataForDownLoad(HttpURLConnection conn, RequestDataBean request) throws IOException {
        if (request.getFile() == null) {
            return;
        }
        // 设置文件上传请求参数
        // 禁掉缓存,需要设置上传数据的大小
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty("Accept-Ranges", "bytes");
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.connect();

        // 开始上传文件
        if (request.isNeedDownloadZip()) {
            downZip(conn, request);
        } else {
            HttpCommonUtil.downComFile(conn, request);
        }
    }

    /**
     * 下载压缩包
     *
     * @param conn    连接
     * @param request 请求
     */
    public void downZip(HttpURLConnection conn, RequestDataBean request) {
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(request.getUrlParams()));
            int len = 0;
            byte[] buffer = new byte[1024];
            zipOutputStream.putNextEntry(new ZipEntry(request.getFile()[0].getName()));
            while ((len = conn.getInputStream().read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
        } catch (IOException exception) {
            Logger.error("invoke HttpUtils.sendSSLRequest IOException");
        } finally {
            FileUtil.closeStreams(zipOutputStream, null);
        }
    }
}

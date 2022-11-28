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

package com.huawei.kunpeng.intellij.common.bean;

import com.intellij.openapi.progress.ProgressIndicator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.File;
import java.util.Map;

/**
 * 请求数据参数体
 *
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(of = {"module","url", "method", "urlParams", "bodyData", "headers", "token"})
public class RequestDataBean extends DataBean {
    /**
     * 设置模块
     */
    private String module;

    /**
     * 设置url
     */
    private String url;

    /**
     * 获取请求方法类型
     */
    private String method;

    /**
     * 设置url路径参数
     */
    private String urlParams;

    /**
     * 获取body参数
     */
    private String bodyData;

    /**
     * 设置headers
     */
    private String headers;

    /**
     * 设置token
     */
    private String token;

    /**
     * 获取请求是否需要token
     */
    private boolean isNeedToken = true;

    /**
     * 是否需要上传文件
     */
    private boolean isNeedUploadFile = false;

    /**
     * 是否需要下载文件
     */
    private boolean isNeedDownloadFile = false;

    /**
     * 是否需要取消
     */
    private boolean isNeedProcess = false;

    /**
     * 是否需要下载压缩包
     */
    private boolean isNeedDownloadZip = false;

    /**
     * 是否需要后台运行上传任务
     */
    private boolean isNeedBackground = false;

    /**
     * 后台运行上传所需参数
     */
    private ProgressIndicator indicator;

    /**
     * 得到文件
     */
    private File[] file;

    private boolean isCancel;

    /**
     * downloadPtah下载路径
     */
    private String downloadPtah;

    /**
     * 下载文件名称
     */
    private String downloadFileName;

    /**
     * 设置编码字符集
     */
    private String charset;

    /**
     * 设置scanType
     */
    private String scanType;

    /**
     * 设置是否需要解压
     */
    private String needUnzip;

    /**
     * 获取NotChmod参数
     */
    private String notChmod;

    /**
     * save_as 模式下使用的参数，获取文件名
     */
    private String uploadFileName;

    /**
     * scan_type为7时需要上传
     */
    private String codePath;

    /**
     * 是否自动重命名下载文件
     */
    private boolean isAutoRename;

    /**
     * 自定义conn参数
     */
    private Map<String, String> connRequestProperty;

    /**
     * 默认构造函数
     */
    public RequestDataBean() {
        this(null, null, null, null);
    }

    /**
     * 完整构造函数
     *
     * @param module 模块
     * @param url    url
     * @param method 请求方法类型
     * @param token  token
     */
    public RequestDataBean(String module, String url, String method, String token) {
        this.module = module;
        this.url = url;
        this.method = method;
        this.token = token;
    }

    /**
     * 完整构造函数
     *
     * @param module      模块
     * @param url         url
     * @param method      请求方法类型
     * @param isNeedToken 是否需要token
     */
    public RequestDataBean(String module, String url, String method, boolean isNeedToken) {
        this.module = module;
        this.url = url;
        this.method = method;
        this.isNeedToken = isNeedToken;
    }

    /**
     * 是否需要token
     *
     * @return boolean
     */
    public boolean isNeedToken() {
        return isNeedToken;
    }

    /**
     * 是否需要取消
     *
     * @return boolean
     */
    public boolean isNeedCancel() {
        return isCancel;
    }

    /**
     * 是否需要上传文件
     *
     * @return boolean
     */
    public boolean isNeedUploadFile() {
        return isNeedUploadFile;
    }

    /**
     * 是否需要下载文件
     *
     * @return boolean
     */
    public boolean isNeedDownloadFile() {
        return isNeedDownloadFile;
    }

    /**
     * 是否需要下载压缩包
     *
     * @return boolean
     */
    public boolean isNeedDownloadZip() {
        return isNeedDownloadZip;
    }

    /**
     * 是否需要进度条
     *
     * @return boolean
     */
    public boolean isNeedProcess() {
        return isNeedProcess;
    }

    /**
     * 获取请求是否需要token
     *
     * @return boolean
     */
    public boolean getNeedToken() {
        return isNeedToken;
    }

    /**
     * 如果需要token标志needToken为false 则返回null
     *
     * @return String
     */
    public String getToken() {
        String result = token;
        if (!getNeedToken()) {
            result = null;
        }
        return result;
    }

    /**
     * 是否需要后台运行上传任务
     *
     * @return boolean boolean
     */
    public boolean isNeedBackground() {
        return isNeedBackground;
    }

    /**
     * 是否需要取消
     *
     * @param isCancel isCancel
     */
    public void setNeedCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }
}

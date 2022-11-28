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

package com.huawei.kunpeng.intellij.common.constant;

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;

/**
 * 文件管理相关常量定义
 *
 * @since 2022-06-21
 */
public class FileManageConstant {
    /**
     * 国际化: 替换
     */
    public static final String REPLACE = CommonI18NServer.toLocale("common_replace");
    /**
     * 国际化: 另存为
     */
    public static final String SAVE_AS = CommonI18NServer.toLocale("common_save_as");
    /**
     * 国际化: 新文件名
     */
    public static final String NEW_NAME = CommonI18NServer.toLocale("common_new_file_name");
    /**
     * 国际化: 输入不能为空
     */
    public static final String NOT_NULL = CommonI18NServer.toLocale("common_required_tip");
    /**
     * 国际化: 文件或文件夹名中不能包含中文.。。
     */
    public static final String NO_CHINESE = CommonI18NServer.toLocale("common_file_name_illegal_tip");

    /**
     * 国际化: 文件或文件夹名中不能包含中文.。。
     */
    public static final String CONFIRM_REPLACE = CommonI18NServer.toLocale("common_download_replace_tips");
    /**
     * 国际化: 下载标题
     */
    public static final String DOWNLOAD_TITLE = CommonI18NServer.toLocale("common_download_title");
    /**
     * 国际化: 下载成功
     */
    public static final String DOWNLOAD_SUCCESS = CommonI18NServer.toLocale("common_download_success");
    /**
     * 国际化: 下载成功，路径提示
     */
    public static final String DOWNLOAD_SUCCESS_TIP = CommonI18NServer.toLocale("common_download_success_tip");
    /**
     * 国际化: 下载失败
     */
    public static final String DOWNLOAD_FAIL = CommonI18NServer.toLocale("common_download_failed");
    /**
     * 国际化: 下载成功，路径提示
     */
    public static final String DOWNLOAD_FAIL_TIP = CommonI18NServer.toLocale("common_download_failed_tip");
    /**
     * 国际化: 下载内容为空
     */
    public static final String DOWNLOAD_EMPTY = CommonI18NServer.toLocale("common_download_content_empty");


}

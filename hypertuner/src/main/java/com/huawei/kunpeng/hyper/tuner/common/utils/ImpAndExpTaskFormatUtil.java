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

package com.huawei.kunpeng.hyper.tuner.common.utils;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;

import java.text.NumberFormat;

/**
 * 导入导出任务 格式化工具类
 *
 * @since 2021-4-25
 */
public class ImpAndExpTaskFormatUtil {
    private static final int KB_SIZE = 1024;
    private static final int MB_SIZE = 1024 * 1024;
    private static final int GB_SIZE = 1024 * 1024 * 1024;

    /**
     * 操作类型格式化
     *
     * @param operaType 带格式化的操作类型
     * @return 格式化之后的操作类型
     */
    public static String operaTypeFormat(String operaType) {
        if (operaType == null) {
            return ImpAndExpTaskContent.ERROR;
        }
        String operaTypeFormat;
        if ("export".equals(operaType)) {
            operaTypeFormat = ImpAndExpTaskContent.TYPE_EXP;
        } else if ("import".equals(operaType)) {
            operaTypeFormat = ImpAndExpTaskContent.TYPE_IMP;
        } else {
            operaTypeFormat = operaType;
        }
        return operaTypeFormat;
    }

    /**
     * 操作类型格式化
     *
     * @param processStatus 带格式化的状态
     * @return 格式化之后的状态
     */
    public static String processStatusFormat(String processStatus) {
        if (processStatus == null) {
            return ImpAndExpTaskContent.ERROR;
        }
        String processStatusFormat;
        if ("export_success".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_EXP_SUCCESS;
        } else if ("import_success".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_SUCCESS;
        } else if ("upload_fail".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_UPLOAD_FAIL;
        } else if ("import_start_fail".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_UPLOAD_START_FAIL;
        } else if ("import_check_fail".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_UPLOAD_CHECK_FAIL;
        } else if ("uploading".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_UPLOADING;
        } else if ("importing".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_IMPORTING;
        } else if ("import_fail".equals(processStatus)) {
            processStatusFormat = ImpAndExpTaskContent.STATUS_IMP_IMPORT_FAIL;
        } else {
            processStatusFormat = processStatus;
        }
        return processStatusFormat;
    }

    /**
     * 文件大小格式化
     *
     * @param fileSize 带格式化的文件大小
     * @return 格式化之后的文件大小
     */
    public static String fileSizeFormat(String fileSize) {
        if (fileSize == null || fileSize.isEmpty()) {
            return ImpAndExpTaskContent.ERROR;
        }
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        String fileSizeFormat = "";
        float fileSizeFloat = Float.parseFloat(fileSize);
        if (fileSizeFloat < KB_SIZE) {
            fileSizeFormat = nf.format(fileSizeFloat) + " B";
        } else if (KB_SIZE <= fileSizeFloat && fileSizeFloat < MB_SIZE) {
            fileSizeFormat = nf.format(fileSizeFloat / KB_SIZE) + " KB";
        } else if (MB_SIZE <= fileSizeFloat && fileSizeFloat < GB_SIZE) {
            fileSizeFormat = nf.format(fileSizeFloat / MB_SIZE) + " MB";
        } else {
            fileSizeFormat = nf.format(fileSizeFloat / GB_SIZE) + " GB";
        }
        return fileSizeFormat;
    }
}

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
import com.huawei.kunpeng.intellij.common.log.Logger;

import com.intellij.openapi.progress.ProgressIndicator;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 上传文件
 *
 * @since v1.0
 */
public class UploadUtil {
    /**
     * 缓冲大小
     */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * 上传任务带进度条
     *
     * @param paramMap paramMap
     * @param indicator indicator
     */
    public static void uploadFileWithProgress(Map<String, Object> paramMap, ProgressIndicator indicator) {
        DataInputStream in = null;
        OutputStream out = null;
        try {
            // 获取上传操作值
            out = JsonUtil.getValueIgnoreCaseFromMap(paramMap, "out", OutputStream.class);
            File file = JsonUtil.getValueIgnoreCaseFromMap(paramMap, "file", File.class);
            in = new DataInputStream(new FileInputStream(file));
            out.write(JsonUtil.getValueIgnoreCaseFromMap(paramMap, "before", String.class)
                .getBytes(IDEContext.getCurrentCharset()));
            int bytes = 0;
            // 总刻度
            long processTotalValue = 0L;
            processTotalValue = file.length() / BUFFER_SIZE;
            processTotalValue = (processTotalValue > 0) ? processTotalValue : 1;
            // 当前进度
            int processCurrentValue = 0;
            double processValue = 0.0d;
            byte[] buffer = new byte[BUFFER_SIZE];
            // 开始上传
            while ((bytes = in.read(buffer)) != -1) {
                indicator.checkCanceled();
                out.write(buffer, 0, bytes);
                processValue = new BigDecimal(processCurrentValue).divide(new BigDecimal(processTotalValue), 2,
                    BigDecimal.ROUND_HALF_DOWN).doubleValue();
                indicator.setFraction(processValue);
                indicator.setText(new StringBuilder(I18NServer.toLocale("plugins_porting_src_tip_uploading")).append(
                    BigDecimal.valueOf(processValue * 100).intValue()).append("%").toString());
                processCurrentValue++;
            }
            out.write(JsonUtil.getValueIgnoreCaseFromMap(paramMap, "end", String.class)
                .getBytes(IDEContext.getCurrentCharset()));
            out.flush();
        } catch (FileNotFoundException e) {
            Logger.error("runTask error, FileNotFoundException");
        } catch (IOException e) {
            Logger.error("runTask error, IOException.");
        } finally {
            FileUtil.closeStreams(out, null);
            FileUtil.closeStreams(in, null);
        }
    }
}

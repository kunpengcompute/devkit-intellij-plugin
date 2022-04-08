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

package com.huawei.kunpeng.hyper.tuner.common.utils.javaperf;

import com.huawei.kunpeng.hyper.tuner.model.javaperf.JavaPerfOperateLogBean;
import com.huawei.kunpeng.intellij.common.util.ExcelExportUtil;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

/**
 * Java perf模块
 * 操作日志导入导出 工具类
 *
 * @since 2021/07/12
 */
public class JavaPerfLogExcelExportUtil extends ExcelExportUtil<JavaPerfOperateLogBean> {
    // 需要填充的数据信息
    private List<JavaPerfOperateLogBean> data;

    @Override
    public List<JavaPerfOperateLogBean> getData() {
        return data;
    }

    public void setData(List<JavaPerfOperateLogBean> data) {
        this.data = data;
    }

    @Override
    public void formatData(HSSFSheet wbSheet, HSSFCellStyle style) {
        int rowNum = 2;
        for (JavaPerfOperateLogBean bean : data) {
            HSSFRow row = wbSheet.createRow(rowNum);
            int j = 0;
            setCell(row, style, j++, bean.getUsername()); // 操作用户
            setCell(row, style, j++, bean.getOperation()); // 操作名称
            setCell(row, style, j++, bean.getSucceed()); // 操作结果
            setCell(row, style, j++, bean.getClientIp()); // 操作主机IP
            setCell(row, style, j++, bean.getCreateTime()); // 操作时间
            setCell(row, style, j, bean.getResource()); // 操作详情
            rowNum++;
        }
    }
}
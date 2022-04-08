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

import com.huawei.kunpeng.intellij.common.bean.OperateLogBean;
import com.huawei.kunpeng.intellij.common.util.ExcelExportUtil;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

/**
 * excle导出工具类
 *
 * @since 1.0.0
 */
public class OperateLogExcelExportUtil extends ExcelExportUtil<OperateLogBean> {
    // 需要填充的数据信息
    private List<OperateLogBean> data;

    @Override
    public void setData(List<OperateLogBean> data) {
        this.data = data;
    }

    @Override
    public List<OperateLogBean> getData() {
        return data;
    }

    /**
     * 处理表格数据
     *
     * @param wbSheet wbSheet
     * @param style   wbSheet
     */
    public void formatData(HSSFSheet wbSheet, HSSFCellStyle style) {
        int rowNum = 2;
        for (OperateLogBean datum : data) {
            HSSFRow row = wbSheet.createRow(rowNum);
            int j = 0;
            setCell(row, style, j++, datum.getUsername());
            setCell(row, style, j++, datum.getModuleType());
            setCell(row, style, j++, datum.getResult());
            setCell(row, style, j++, datum.getIpaddr());
            setCell(row, style, j++, datum.getTime());
            setCell(row, style, j, datum.getInformation());
            rowNum++;
        }
    }
}
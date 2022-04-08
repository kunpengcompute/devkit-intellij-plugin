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

import com.huawei.kunpeng.hyper.tuner.model.sysperf.ImpAndExpTaskBean;
import com.huawei.kunpeng.intellij.common.util.ExcelExportUtil;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.util.List;

/**
 * 导入导出任务 记录 excle导出工具类
 *
 * @since 1.0.0
 */
public class ImpAndExpTaskExcelExportUtil extends ExcelExportUtil<ImpAndExpTaskBean> {
    // 需要填充的数据信息
    private List<ImpAndExpTaskBean> data;

    @Override
    public List<ImpAndExpTaskBean> getData() {
        return data;
    }

    public void setData(List<ImpAndExpTaskBean> data) {
        this.data = data;
    }

    @Override
    public void formatData(HSSFSheet wbSheet, HSSFCellStyle style) {
        int rowNum = 2;
        for (ImpAndExpTaskBean datum : data) {
            HSSFRow row = wbSheet.createRow(rowNum);
            int j = 0;
            setCell(row, style, j++, datum.getId() + "");
            setCell(row, style, j++, datum.getTaskname());
            setCell(row, style, j++, datum.getProjectname());
            setCell(row, style, j++, datum.getOperationType());
            setCell(row, style, j++, datum.getProcessStatus());
            setCell(row, style, j++, datum.getDetailInfo());
            setCell(row, style, j++, datum.getTaskFilesize());
            setCell(row, style, j++, datum.getStartTime());
            setCell(row, style, j, datum.getEndTime());
            rowNum++;
        }
    }
}
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

import com.huawei.kunpeng.intellij.common.log.Logger;

import lombok.Data;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * excle导出工具类
 *
 * @since 1.0.0
 */
@Data
public class ExcelExportUtil<T> {
    // 表头
    private String title;

    // 各个列的表头
    private String[] heardList;

    // 各个列的元素key值
    private String[] heardKey;

    // 需要填充的数据信息
    private List<T> data;

    // 字体大小
    private int fontSize = 12;

    // 行高
    private int rowHeight = 30;

    // 列宽
    private int columWidth = 200;

    // 工作表
    private String sheetName = "sheet1";

    /**
     * 导出excle
     *
     * @param path 路径
     * @throws IOException 异常处理
     */
    public void exportExport(String path) throws IOException {
        // 创建工作簿
        HSSFWorkbook wb = new HSSFWorkbook();

        // 创建工作表
        HSSFSheet wbSheet = wb.createSheet(this.sheetName);

        // 设置默认行宽
        wbSheet.setDefaultColumnWidth(20);

        // 标题样式（加粗，垂直居中）
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFFont fontStyle = wb.createFont();
        fontStyle.setBold(false);   // 加粗
        cellStyle.setFont(fontStyle);

        // 在第0行创建rows  (表标题)
        HSSFRow hssfRow = wbSheet.createRow(0);

        // 行高
        hssfRow.setHeightInPoints(30);
        HSSFCell cellValue = hssfRow.createCell(0);
        cellValue.setCellValue(this.title);
        cellValue.setCellStyle(cellStyle);
        wbSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (this.heardList.length - 1)));

        // 设置表头样式，表头居中
        HSSFCellStyle style = wb.createCellStyle();

        // 设置字体
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) this.fontSize);
        style.setFont(font);

        // 在第1行创建rows
        HSSFRow row = wbSheet.createRow(1);

        // 设置列头元素
        HSSFCell cellHead = null;
        for (int i = 0; i < heardList.length; i++) {
            cellHead = row.createCell(i);
            cellHead.setCellValue(heardList[i]);
            cellHead.setCellStyle(style);
        }

        // 开始写入实体数据信息
        formatData(wbSheet, style);

        // 导出数据
        File file = new File(path);

        // 文件已存在删除使用新数据覆盖。
        if (file != null && file.exists()) {
            if (!file.delete()) {
                Logger.error("Its delete File fail when exportExport logs!");
            }
        }
        boolean isFile = file.createNewFile();
        if (!isFile) {
            Logger.error("Its create new File fail when exportExport logs!");
        }
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file)
        ) {
            wb.write(fileOutputStream);
        } catch (IOException ex) {
            throw new IOException("Its IOException when exportExport logs!");
        }
    }

    /**
     * setCell
     *
     * @param row  row
     * @param style style
     * @param index  index
     * @param value  value
     */
    protected void setCell(HSSFRow row, HSSFCellStyle style, int index, String value) {
        HSSFCell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    /**
     * 处理表格数据
     *
     * @param wbSheet wbSheet
     * @param style   wbSheet
     */
    public void formatData(HSSFSheet wbSheet, HSSFCellStyle style) {
    }
}
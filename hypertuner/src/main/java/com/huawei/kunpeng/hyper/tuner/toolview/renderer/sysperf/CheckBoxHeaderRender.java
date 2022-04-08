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

package com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 自定义CheckBox 表头
 *
 * @since 2021-4-25
 */
public class CheckBoxHeaderRender extends JCheckBox implements TableCellRenderer {
    /**
     * 自定义表头
     */
    protected CheckBoxHeaderRender rendererComponent;
    /**
     * 表头所在列
     */
    protected int column;

    public CheckBoxHeaderRender() {
        this(false);
    }

    public CheckBoxHeaderRender(boolean isSelect) {
        this.setSelected(isSelect);
        rendererComponent = this;
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPaintedFlat(true);
        setBorderPainted(true);
    }

    /**
     * Returns the component used for drawing the cell
     *
     * @param table      table
     * @param value      value
     * @param isSelected isSelected
     * @param hasFocus   hasFocus
     * @param row        row
     * @param column     column
     * @return Returns the component used for drawing the cell
     */
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setColumn(column);
        return rendererComponent;
    }

    /**
     * 设置所在列
     *
     * @param column 列数
     */
    protected void setColumn(int column) {
        this.column = column;
    }

    /**
     * 获取所在列
     *
     * @return 列数
     */
    public int getColumn() {
        return column;
    }

    /**
     * 设置 表头选中状态
     *
     * @param select 新的选中状态
     */
    public void setSelect(boolean select) {
        this.setSelected(select);
    }

    /**
     * 获取当前表头选中状态
     *
     * @return 当前选中状态
     */
    public boolean getSelect() {
        return this.isSelected();
    }

    /**
     * 表头多选框 点击事件处理函数
     * 根据表头多选框是否选中，修改表头选中状态，和表格元素的选中状态
     *
     * @param tableData 表格数据
     */
    public void changeAllSelect(Object[][] tableData) {
        // 获取表头是否选中
        boolean isHeaderSelect = this.isSelected();
        this.setSelected(!isHeaderSelect);
        if (isHeaderSelect) {
            // 若表头选中，则其他行全部反选
            for (Object[] rowItem : tableData) {
                rowItem[0] = false;
            }
        } else {
            // 若表头未全选，则其他行全部选中
            for (Object[] rowItem : tableData) {
                rowItem[0] = true;
            }
        }
    }

    /**
     * 根据 TableData 变更表头状态
     *
     * @param newTableData 表格数据
     */
    public void updateByTableData(Object[][] newTableData) {
        boolean isAllSelect = true;
        for (Object[] rowItem : newTableData) {
            Object selectObj = rowItem[0];
            if (selectObj instanceof Boolean) {
                boolean isSelect = (boolean) selectObj;
                if (!isSelect) {
                    isAllSelect = false;
                    break;
                }
            }
        }
        this.setSelected(isAllSelect);
    }
}

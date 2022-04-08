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

package com.huawei.kunpeng.intellij.ui.utils;

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.constant.LogManageConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.ui.bean.MessageDialogBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * 表格工具类
 *
 * @since 2020-10-6
 */
public class CommonTableUtil {
    private static final int BUTTON_LENGTH = 2;

    /**
     * 隐藏列
     *
     * @param table table
     * @param columnIndex columnIndex
     */
    public static void hideColumn(JTable table, int columnIndex) {
        TableColumnModel tcm = table.getColumnModel();
        TableColumn htc = tcm.getColumn(columnIndex);
        htc.setMaxWidth(0);
        htc.setPreferredWidth(0);
        htc.setWidth(0);
        htc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
    }

    /**
     * 文件是否存在
     *
     * @param filePath filePath
     * @param title    title
     * @return 文件是否存在
     */
    public static boolean isExistNotToContinue(String filePath, String title) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        List<IDEMessageDialogUtil.ButtonName> button = new ArrayList<>(BUTTON_LENGTH);
        button.add(IDEMessageDialogUtil.ButtonName.OK);
        button.add(IDEMessageDialogUtil.ButtonName.CANCEL);
        String select =
                IDEMessageDialogUtil.showDialog(
                        new MessageDialogBean(
                                LogManageConstant.DOWNLOG_REPLACE, title, button, 0,
                                BaseIntellijIcons.load(IDEConstant.WARN_INFO)));
        if (select.equals(IDEMessageDialogUtil.ButtonName.OK.getKey())) {
            return false;
        }
        return true;
    }
}

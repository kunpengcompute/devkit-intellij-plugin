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

package com.huawei.kunpeng.hyper.tuner.toolview.renderer;

import com.huawei.kunpeng.hyper.tuner.common.constant.SysSettingManageConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * jtable表头提示文本
 *
 * @since 2012-10-12
 */
public class TableHeaderRender extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel nodeTipLabel = new JLabel();

        if (column == 7 || column == 8) {
            nodeTipLabel.setIcon(BaseIntellijIcons.Settings.CERT_TIPS);
            if (column == 7) {
                nodeTipLabel.setToolTipText(SysSettingManageConstant.RUN_NODE_HELP);
            }
            if (column == 8) {
                nodeTipLabel.setToolTipText(SysSettingManageConstant.LOG_NODE_HELP);
            }
        }
        nodeTipLabel.setText(value.toString());
        return nodeTipLabel;
    }
}

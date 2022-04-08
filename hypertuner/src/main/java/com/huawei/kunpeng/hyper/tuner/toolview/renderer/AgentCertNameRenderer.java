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

import java.awt.Component;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * AgentCert表格渲染器。
 *
 * @since 2020-11-5
 */
public class AgentCertNameRenderer extends DefaultTableCellRenderer {
    /**
     * 默认渲染器。
     */
    private static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer =
                DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (renderer instanceof JLabel) {
            JLabel textIconLabel = (JLabel) renderer;
            textIconLabel.removeAll();
            Object statusObj = table.getModel().getValueAt(row, column);
            if (!Objects.isNull(statusObj)) {
                String status = "<html>" + statusObj.toString().replaceAll("  ", "<br>") + "</html>";
                textIconLabel.setText(status);
            }
        }
        return renderer;
    }
}

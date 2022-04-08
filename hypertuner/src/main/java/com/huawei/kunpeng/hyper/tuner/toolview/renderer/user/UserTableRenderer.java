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

package com.huawei.kunpeng.hyper.tuner.toolview.renderer.user;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.CSSConstant;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 表格渲染器。
 *
 * @since 2020-10-10
 */
public class UserTableRenderer implements TableCellRenderer {
    private static final int ROLE_INDEX = 2;

    private JBLabel modifyLabel;

    private JBPanel mainPanel;

    private JBLabel deleteLabel;

    /**
     * 用户表格渲染器
     */
    public UserTableRenderer() {
        this.mainPanel = new JBPanel();
        this.modifyLabel = new JBLabel(TuningUserManageConstant.OPERATE_RESET);
        this.modifyLabel.setForeground(CSSConstant.RESET_DELETE_LABEL_COLOR);

        this.deleteLabel = new JBLabel(TuningUserManageConstant.OPERATE_DEL);
        this.deleteLabel.setForeground(CSSConstant.RESET_DELETE_LABEL_COLOR);
        this.mainPanel.setLayout(new FlowLayout(10, 20, 5));
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.mainPanel.removeAll();
        Object role = table.getModel().getValueAt(row, ROLE_INDEX);
        if (TuningUserManageConstant.USER_ROLE_ADMIN.equals(role)) {
            this.mainPanel.add(modifyLabel);
            modifyLabel.setText(TuningUserManageConstant.TITLE_CHANGE_USER);
        } else {
            modifyLabel.setText(TuningUserManageConstant.OPERATE_RESET);
            this.mainPanel.add(modifyLabel);
            this.mainPanel.add(deleteLabel);
        }

        return this.mainPanel;
    }

    /**
     * 返回渲染器主界面。
     *
     * @return 返回渲染器主界面
     */
    public JBPanel getComponent() {
        return this.mainPanel;
    }
}

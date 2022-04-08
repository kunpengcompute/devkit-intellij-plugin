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
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.user.UseManagerDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.AddAndModifyUserPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.CSSConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.DeleteUserPanel;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.userpanel.UserMouseListener;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 * 用户表格编辑器
 *
 * @since 2020-10-10
 */
public class UserTableCellEditor extends DefaultCellEditor {
    /**
     * 角色列下标
     */
    private static final int ROLE_INDEX = 2;

    /**
     * 用户ID列下标
     */
    private static final int ID_INDEX = 0;

    /**
     * 用户名称列下标
     */
    private static final int USER_NAME_INDEX = 1;

    /**
     * 修改按钮再在JBPanel中下标
     */
    private static final int MODIFY_LABEL_INDEX = 0;

    /**
     * 删除按钮再在JBPanel中下标
     */
    private static final int DELETE_LABEL_INDEX = 1;

    private JBPanel mainPanel;

    private boolean isFirstTime = true;

    private MouseEvent mouseEvent;

    /**
     * 构造函数。
     */
    public UserTableCellEditor() {
        super(new JTextField());
        setClickCountToStart(1);
        this.mainPanel = new JBPanel();
        this.mainPanel.setLayout(new FlowLayout(10, 20, 5));
        if (editorComponent instanceof JTextField) {
            delegate = new UserEditorDelegate();
            ((JTextField) editorComponent).addActionListener(delegate);
        }
    }

    /**
     * 重写编辑器方法，返回一个按钮给JTable
     *
     * @param table      table
     * @param value      value
     * @param isSelected isSelected
     * @param row        row
     * @param column     column
     * @return Component
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        setClickCountToStart(1);
        addLabel(table, isSelected, row, column);
        return this.mainPanel;
    }

    private void addLabel(JTable table, boolean isSelected, int row, int column) {
        if (!isFirstTime) {
            this.mainPanel.removeAll();
        }
        isFirstTime = false;

        JBLabel modifyLabel = new JBLabel(TuningUserManageConstant.OPERATE_RESET);
        modifyLabel.setForeground(CSSConstant.RESET_DELETE_LABEL_COLOR);

        JBLabel deleteLabel = new JBLabel(TuningUserManageConstant.OPERATE_DEL);
        deleteLabel.setForeground(CSSConstant.RESET_DELETE_LABEL_COLOR);

        Object role = table.getModel().getValueAt(row, ROLE_INDEX);

        // 添加修改事件
        addModifyLabelListener(table, row, modifyLabel);

        // 添加删除事件
        addDeleteLabelListener(table, row, deleteLabel);

        // 获取单元格的渲染器。
        TableCellRenderer rendererObj = table.getCellRenderer(row, column);
        UserTableRenderer renderer = null;
        if (rendererObj instanceof UserTableRenderer) {
            renderer = (UserTableRenderer) rendererObj;
        }
        // 获取单元格的位置范围。
        Rectangle rectangle = table.getCellRect(row, column, true);
        if (TuningUserManageConstant.USER_ROLE_ADMIN.equals(role)) {
            this.mainPanel.add(modifyLabel);
            modifyLabel.setText(TuningUserManageConstant.TITLE_CHANGE_USER);
            handlerFirstMouseEvent(renderer, rectangle, MODIFY_LABEL_INDEX);
        } else {
            this.mainPanel.add(modifyLabel);
            this.mainPanel.add(deleteLabel);
            handlerFirstMouseEvent(renderer, rectangle, MODIFY_LABEL_INDEX);
            handlerFirstMouseEvent(renderer, rectangle, DELETE_LABEL_INDEX);
        }
    }

    /**
     * 处理第一次鼠标触发编辑器事件。
     *
     * @param tableRenderer  单元格渲染器
     * @param rectangle 单元格坐标位置范围
     * @param index     组件在主页面的下标。
     */
    private void handlerFirstMouseEvent(UserTableRenderer tableRenderer, Rectangle rectangle, int index) {
        if (tableRenderer == null || rectangle == null) {
            return;
        }
        Component targetComponent = this.mainPanel.getComponent(index);
        JBPanel renderPanel = tableRenderer.getComponent();
        if (renderPanel == null) {
            return;
        }
        Component renderComponent = renderPanel.getComponent(index);
        // 通过渲染器后续目标组件的位置(第一次显示的是渲染器的组件位置)
        if (targetComponent != null && renderComponent != null) {
            int minX = (int) rectangle.getX() + renderComponent.getX();
            int minY = (int) rectangle.getY() + renderComponent.getY();
            int maxX = minX + renderComponent.getWidth();
            int maxY = minY + renderComponent.getHeight();
            matchLocation(targetComponent, minX, minY, maxX, maxY);
        }
    }

    /**
     * 处理第一次鼠标点击事件。
     *
     * @param targetComponent 目标组件
     * @param xCoordinate               目标组件X坐标起点
     * @param yCoordinate               目标组件y坐标起点
     * @param maxX            目标组件X坐标终点
     * @param maxY            目标组件y坐标终点
     */
    private void matchLocation(Component targetComponent, int xCoordinate, int yCoordinate, int maxX, int maxY) {
        if (mouseEvent != null) {
            int mouseX = mouseEvent.getX();
            int mouseY = mouseEvent.getY();
            if ((xCoordinate <= mouseX && mouseX <= maxX) && (yCoordinate <= mouseY && mouseY <= maxY)) {
                MouseListener[] listeners = targetComponent.getMouseListeners();
                if (listeners != null && listeners.length > 0) {
                    listeners[0].mouseClicked(null);
                }
            }
        }
    }

    /**
     * 添加表格监听事件
     *
     * @param table       table
     * @param row         row
     * @param deleteLabel deleteLabel
     */
    private void addDeleteLabelListener(JTable table, int row, JBLabel deleteLabel) {
        deleteLabel.addMouseListener(
                new UserMouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Object userID = table.getValueAt(row, ID_INDEX);
                        UseManagerDialog dialog =
                                new UseManagerDialog(
                                        TuningUserManageConstant.OPERATE_DELETE_TITLE, new DeleteUserPanel(userID));
                        dialog.displayPanel();
                    }
                });
    }

    private void addModifyLabelListener(JTable table, int row, JBLabel modifyLabel) {
        modifyLabel.addMouseListener(
                new UserMouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Object userID = table.getValueAt(row, ID_INDEX);
                        Object userName = table.getValueAt(row, USER_NAME_INDEX);
                        Object role = table.getValueAt(row, ROLE_INDEX);

                        String title = TuningUserManageConstant.OPERATE_RESET;
                        if (TuningUserManageConstant.USER_ROLE_ADMIN.equals(role)) {
                            title = (TuningUserManageConstant.TITLE_CHANGE_USER);
                        }
                        UseManagerDialog dialog =
                                new UseManagerDialog(title, new AddAndModifyUserPanel(false, userName, userID, role));
                        dialog.displayPanel();
                    }
                });
    }

    /**
     * 用户编辑器代理。重写，增加第一次鼠标事件获取。
     */
    protected class UserEditorDelegate extends EditorDelegate {
        /**
         * 是否可以编辑
         *
         * @param anEvent anEvent
         * @return 结果
         */
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                mouseEvent = (MouseEvent) anEvent;
                return mouseEvent.getClickCount() >= clickCountToStart;
            }
            return true;
        }
    }
}

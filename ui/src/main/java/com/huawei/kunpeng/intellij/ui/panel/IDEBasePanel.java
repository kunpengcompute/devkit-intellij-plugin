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

package com.huawei.kunpeng.intellij.ui.panel;

import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;

import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * IDE面板基类
 *
 * @since 1.0.0
 */
public abstract class IDEBasePanel extends JPanel {
    /**
     * 依托ToolWindow窗口,可为空，为空时,displayPanel、dispose方法将失效
     */
    protected ToolWindow toolWindow;

    /**
     * 装载面板的父容器
     */
    protected IDEBaseDialog parentComponent;

    /**
     * 面板内容
     */
    protected Content content;

    /**
     * 面板名称
     */
    protected String panelName;

    /**
     * 事件处理器
     */
    protected IDEPanelBaseAction action;

    /**
     * 面板携带参数
     */
    protected Map params;

    private ValidationInfo validationInfo;

    /**
     * 空构造函数
     */
    public IDEBasePanel() {
    }

    /**
     * 初始化面板
     *
     * @param panel 面板
     */
    protected void initPanel(final JPanel panel) {
        setLayout(new BorderLayout());

        JPanel panelDef = panel;
        if (panelDef == null) {
            panelDef = new JPanel(new BorderLayout());
        }
        add(panelDef, BorderLayout.CENTER);
    }


    /**
     * 添加editor 提示
     *
     * @param text 提示文本
     * @param jComponent 获取格式标准化组件
     * @param jPanel jPanel
     * @param jEditorPane jEditorPane
     */
    protected void addEditorPanel(String text, JComponent jComponent, JPanel jPanel, JEditorPane jEditorPane) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html> <body><div style=\"color:rgb({0},{1},{2}); font-family:{3};font-size:{4}\">");
        stringBuilder.append(text);
        stringBuilder.append("</div> </body></html>");

        Color color = jComponent.getForeground();
        Font font = jComponent.getFont();
        String messages = MessageFormat.format(stringBuilder.toString(), color.getRed(), color.getGreen(),
                color.getBlue(), font.getFontName(), font.getSize());
        JEditorPane thisEditor = jEditorPane;
        thisEditor = new JEditorPane("text/html", messages);
        thisEditor.setEditable(false);
        thisEditor.setOpaque(false);

        jPanel.removeAll(); // 需要重新覆盖。
        jPanel.add(thisEditor);
        thisEditor.updateUI();
        IdeFocusManager.getGlobalInstance()
                .doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(jPanel, true));
        jPanel.updateUI();
    }

    /**
     * 注册组件事件
     */
    protected abstract void registerComponentAction();

    /**
     * 设置自定义事件处理器
     *
     * @param action 处理事件
     */
    protected abstract void setAction(IDEPanelBaseAction action);

    /**
     * 获取面板内注册的事件处理器
     *
     * @return IDEPanelBaseAction
     */
    public IDEPanelBaseAction getAction() {
        return action;
    }

    /**
     * 设置toolWindow
     *
     * @param toolWindow 工具窗口
     */
    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    /**
     * 创建面板content
     *
     * @param component 装载内容的容器
     * @param displayName 面板显示title
     * @param isLockable isLockable
     */
    protected void createContent(JComponent component, String displayName, boolean isLockable) {
        this.content = ContentFactory.SERVICE.getInstance().createContent(component, displayName, isLockable);
    }

    /**
     * 获取面板携带参数
     *
     * @return Map
     */
    public Map getParams() {
        return params;
    }

    /**
     * 获取content
     *
     * @return Content
     */
    public Content getContent() {
        return content;
    }

    /**
     * 获取ToolWindow
     *
     * @return ToolWindow
     */
    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    /**
     * 获取父容器
     *
     * @return T
     */
    public IDEBaseDialog getParentComponent() {
        return  parentComponent;
    }

    /**
     * 设置父容器
     *
     * @param parentComponent 父容器
     */
    public void setParentComponent(IDEBaseDialog parentComponent) {
        this.parentComponent = parentComponent;
    }

    /**
     * 获取ToolWindow
     *
     * @return String
     */
    public String getPanelName() {
        return panelName;
    }

    /**
     * 单独显示面板,在toolWindow存在时生效
     */
    public void displayPanel() {
        if (toolWindow != null) {
            toolWindow.getContentManager().addContent(getContent());
        }
    }

    /**
     * 销毁面板,在toolWindow存在时生效
     */
    public void dispose() {
        getContent().dispose();
        if (toolWindow != null) {
            toolWindow.getContentManager().removeContent(getContent(), false);
        }
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    public ValidationInfo doValidate() {
        return validationInfo;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> result = new ArrayList<>();
        if (vi != null) {
            result.add(vi);
        }
        return result;
    }

    /**
     * 安全整改-清空密码。
     */
    public void clearPwd() {
    }
}

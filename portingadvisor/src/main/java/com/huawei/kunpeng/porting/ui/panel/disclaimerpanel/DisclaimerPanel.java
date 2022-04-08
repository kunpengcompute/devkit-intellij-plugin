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

package com.huawei.kunpeng.porting.ui.panel.disclaimerpanel;

import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST1;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST2;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST3;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST4;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST5;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST6;
import static com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant.USER_DISCLAIMER_LIST7;

import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.common.constant.PortingUserManageConstant;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * 免责声明面板（签署后，settings界面）
 *
 * @since 2020-09-28
 */
public class DisclaimerPanel extends IDEBasePanel {
    /**
     * 主面板
     */
    private JPanel centerPanel;

    /**
     * 构造函数
     */
    public DisclaimerPanel() {
        centerPanel = new JPanel(new BorderLayout());
        initPanel(centerPanel);

        JLabel titleLabel = new JLabel();
        titleLabel.setText(USER_DISCLAIMER_LIST1);

        // 内容声明
        JTextArea jTextArea = new JTextArea();
        jTextArea.setLineWrap(true);
        jTextArea.setEditable(false);
        jTextArea.setWrapStyleWord(true);
        jTextArea.append(PortingUserManageConstant.USER_DISCLAIMER_LIST9);
        jTextArea.append(USER_DISCLAIMER_LIST2);
        jTextArea.append(USER_DISCLAIMER_LIST3);
        jTextArea.append(USER_DISCLAIMER_LIST4);
        jTextArea.append(USER_DISCLAIMER_LIST5);
        jTextArea.append(USER_DISCLAIMER_LIST6);
        jTextArea.append(USER_DISCLAIMER_LIST7);
        jTextArea.append(PortingUserManageConstant.USER_DISCLAIMER_LIST8);
        JBScrollPane jbScrollPane = new JBScrollPane(jTextArea);
        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder().addComponent(jbScrollPane);
        centerPanel = mainFormBuilder.addComponentFillVertically(centerPanel, 20).getPanel();
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
    }

    /**
     * 设置自定义事件处理器
     *
     * @param action 处理事件
     */
    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    /**
     * 返回主面板
     *
     * @return mainPanel
     */
    public JPanel getPanel() {
        return centerPanel;
    }
}

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

import com.huawei.kunpeng.intellij.common.bean.WeakPwdBean;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import com.intellij.openapi.wm.ToolWindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 删除弱口令设置面板
 *
 * @since 2012-10-27
 */
public abstract class DelWeakPwdPanel extends IDEBasePanel {
    /**
     * 删除行数据
     */
    private static final int DELINITROW = 0;

    /**
     * Table中第一列
     */
    public int colNum = 1;

    /**
     * 弱口令名称
     */
    public String delWeakPassword;

    /**
     * 需要删除的行数据
     */
    private int delRow = 0;

    /**
     * 主Panel
     */
    private JPanel mainPanel = new JPanel(new BorderLayout());

    /**
     * 内容面板
     */
    private JPanel centerPanel = new JPanel();

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow 依托的toolWindow窗口，可为空
     */
    public DelWeakPwdPanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, null);
    }

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow 依托的toolWindow窗口，可为空
     * @param panelName 面板名称
     * @param displayName 面板显示title
     * @param params 面板携带参数
     */
    public DelWeakPwdPanel(ToolWindow toolWindow, String panelName, String displayName, Map params) {
        this.toolWindow = toolWindow;
        this.params = params;
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.DEL_WEAK_PWD.panelName() : panelName;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, WeakPwdConstant.WEAK_PASSWORD_DEL_TITLE, false);
    }

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param toolWindow 依托的toolWindow窗口，可为空
     * @param params 面板携带参数
     */
    public DelWeakPwdPanel(ToolWindow toolWindow, Map params) {
        this(toolWindow, null, null, params);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setMaximumSize(new Dimension(200, 300));
        JLabel jLabelWeakPwd = new JLabel(WeakPwdConstant.WEAK_PWD_CONFIRM_DEL);
        centerPanel.add(jLabelWeakPwd);

        if (WeakPwdSetPanel.weakPwdTable.getSelectedRow() >= DELINITROW) {
            delWeakPassword =
                    WeakPwdSetPanel.weakPwdTable
                            .getValueAt(WeakPwdSetPanel.weakPwdTable.getSelectedRow(), colNum)
                            .toString();
        }
        JLabel jLabelName = new JLabel(delWeakPassword);
        centerPanel.add(jLabelName);
        JLabel jLabelQuestion = new JLabel("？");
        centerPanel.add(jLabelQuestion);
        centerPanel.setFont(new Font("huawei sans", Font.PLAIN, 14));
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        // 装载面板数据
        loadData(delWeakPassword);
    }

    /**
     * 设置需要删除的行号，逆序
     *
     * @param delRow 需要删除的行号
     */
    public void setDelRow(int delRow) {
        this.delRow = delRow;
    }

    /**
     * 获取需要删除的行号
     *
     * @return int
     */
    public int getDelRow() {
        return delRow;
    }

    /**
     * 装载面板数据
     *
     * @param delWeakPassword 需要删除数据
     */
    private void loadData(String delWeakPassword) {
        // 定位删除选中的数据行
        for (WeakPwdBean weakPwdBean : WeakPwdSetPanel.weakPwdList) {
            if (weakPwdBean.getWeakPassword().equals(delWeakPassword)) {
                delRow = new Integer(weakPwdBean.getId());
                break;
            }
        }
    }
}

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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.fields.IntegerField;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The class CommonSettingsPanel
 *
 * @since v2.2.T4
 */
public abstract class CommonSettingsPanel extends IDEBasePanel {
    /**
     * 成功提示
     */
    protected static final String MESSAGE_SUCESS = CommonI18NServer.toLocale("plugins_ui_common_update_sucessful");
    /**
     * 失败提示
     */
    protected static final String MESSAGE_FAILD = CommonI18NServer.toLocale("plugins_ui_common_update_faild");
    /**
     * serialVersionUID
     */
    protected static final long serialVersionUID = -7492589440302113818L;
    /**
     * 最小用户数限制。
     */
    protected static final int USER_LIMIT_MIN_NUM = 1;
    /**
     * 最大用户数限制。
     */
    protected static final int USER_LIMIT_MAX_NUM = 20;
    /**
     * 最小用户数限制。
     */
    protected static final int USER_TIMEOUT_MIN_NUM = 10;
    /**
     * 最大用户数限制。
     */
    protected static final int USER_TIMEOUT_MAX_NUM = 240;
    /**
     * 閾值最小值
     */
    protected static final int MIN = 7;
    /**
     * 閾值最大值
     */
    protected static final int MAX = 180;
    /**
     * 最小期限
     */
    protected static final int USER_PWD_MIN = 7;
    /**
     * 閾值最大值
     */
    protected static final int USER_PWD_MAX = 90;
    /**
     * 最小栈深度配置。
     */
    protected static final int STACK_DEPTH_MIN_NUM = 16;
    /**
     * 最大栈深度配置。
     */
    protected static final int STACK_DEPTH_MAX_NUM = 64;
    /**
     * 主面板
     */
    protected JPanel mainPanel;
    /**
     * userLimit
     */
    protected Integer userLimit;
    /**
     * userTime
     */
    protected Integer userTime;
    /**
     * userLimitField
     */
    protected IntegerField userLimitField;
    /**
     * userTimeField
     */
    protected IntegerField userTimeField;
    /**
     * userLimitLabel
     */
    protected JLabel userLimitLabel;
    /**
     * userTimeLabel
     */
    protected JLabel userTimeLabel;
    /**
     * comboBox
     */
    protected JComboBox comboBox;
    /**
     * level
     */
    protected String level;
    /**
     * levelTip
     */
    protected JLabel levelTip;
    /**
     * currentLevel
     */
    protected String currentLevel;

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public abstract JPanel getPanel();

    /**
     * mainPanel
     *
     * @return mainPanel
     */
    public abstract JComponent getPreferredFocusedComponent();

    /**
     * 事件处理。
     *
     * @throws ConfigurationException 配置异常。
     */
    public abstract void apply() throws ConfigurationException;

    /**
     * 界面是否修改
     *
     * @return 结果
     */
    public abstract boolean isModified();

    /**
     * 重置界面方法
     */
    public abstract void reset();

    /**
     * 初始化面板
     *
     * @param panel 面板
     */
    @Override
    protected abstract void initPanel(JPanel panel);

    @Override
    protected abstract void registerComponentAction();

    @Override
    protected abstract void setAction(IDEPanelBaseAction action);
}

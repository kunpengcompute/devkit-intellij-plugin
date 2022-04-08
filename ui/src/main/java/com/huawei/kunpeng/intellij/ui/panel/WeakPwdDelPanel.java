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

import static com.huawei.kunpeng.intellij.common.constant.CSSConstant.ICON_INFO_WARN;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.constant.WeakPwdConstant;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.enums.Panels;

import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 添加弱口令面板
 *
 * @since 2021-2-3
 */
public abstract class WeakPwdDelPanel extends IDEBasePanel implements ActionOperate {
    /**
     * mainPanel
     */
    protected JPanel mainPanel;
    /**
     * contextLabel
     */
    protected JLabel contextLabel;
    /**
     * iconLabel
     */
    protected JLabel iconLabel;
    /**
     * delWeakPassword
     */
    protected String delWeakPassword;
    /**
     * delID
     */
    protected String delID;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param delContext 删除内容
     * @param delID 删除ID
     */
    public WeakPwdDelPanel(String delContext, String delID) {
        this.toolWindow = toolWindow;
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.DEL_WEAK_PWD.panelName() : panelName;
        this.delWeakPassword = delContext;
        this.delID = delID;

        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, WeakPwdConstant.WEAK_PASSWORD_DEL_TITLE, false);
    }

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        contextLabel.setText(MessageFormat.format(WeakPwdConstant.WEAK_PWD_CONFIRM_DEL, delWeakPassword));
        iconLabel.setIcon(ICON_INFO_WARN);
        iconLabel.setHorizontalTextPosition(SwingConstants.RIGHT); // 水平方向文本在图片右边
        iconLabel.setVerticalTextPosition(SwingConstants.CENTER); // 垂直方向文本在图片中心
    }

    /**
     * 自定义函数式事件操作
     *
     * @param data 操作数据
     */
    public void actionOperate(Object data) {
    }
}

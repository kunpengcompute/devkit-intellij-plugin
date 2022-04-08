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

package com.huawei.kunpeng.porting.ui.panel.weakpwdpanel;

import com.huawei.kunpeng.intellij.common.action.ActionOperate;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.weakpwd.DelWeakPwdAction;
import com.huawei.kunpeng.porting.common.constant.PortingWeakPwdConstant;

import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 添加弱口令面板
 *
 * @since 2021-2-3
 */
public class WeakPwdDelPanel extends IDEBasePanel implements ActionOperate {
    private JPanel mainPanel;

    private JLabel contextLabel;

    private JLabel iconLabel;

    private String delWeakPassword;

    private String delID;

    /**
     * 完整构造方法，不建议直接使用
     *
     * @param delContext 删除内容
     * @param delID      删除ID
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
        createContent(mainPanel, PortingWeakPwdConstant.WEAK_PASSWORD_DEL_TITLE, false);
    }


    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(panel);
        contextLabel.setText(MessageFormat.format(PortingWeakPwdConstant.WEAK_PWD_CONFIRM_DEL, delWeakPassword));
        iconLabel.setIcon(BaseIntellijIcons.load(IDEConstant.WARN_INFO));
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new DelWeakPwdAction();
        }
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof DelWeakPwdAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    /**
     * 自定义函数式事件操作
     *
     * @param data 操作数据
     */
    @Override
    public void actionOperate(Object data) {
        if (action instanceof DelWeakPwdAction) {
            ((DelWeakPwdAction) action).deleteWeakPwd(delID, null);
        }
        if (data instanceof ResponseBean) {
            ((ResponseBean) data).setStatus("0");
        }
    }
}

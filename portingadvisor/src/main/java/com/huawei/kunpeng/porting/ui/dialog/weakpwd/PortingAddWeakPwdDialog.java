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

package com.huawei.kunpeng.porting.ui.dialog.weakpwd;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.ui.dialog.AddWeakPwdDialog;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.huawei.kunpeng.porting.action.setting.weakpwd.PortingAddWeakPwdAction;
import com.huawei.kunpeng.porting.ui.panel.weakpwdpanel.PortingAddWeakPwdPanel;
import com.huawei.kunpeng.porting.ui.panel.weakpwdpanel.WeakPwdSetPanel;

/**
 * 添加弱口令弹框
 *
 * @since 2012-10-12
 */
public class PortingAddWeakPwdDialog extends AddWeakPwdDialog {
    public PortingAddWeakPwdDialog(String title, IDEBasePanel panel) {
        super(title, panel);
    }

    /**
     * 确认新增弱口令
     */
    @Override
    protected void onOKAction() {
        // 新增数据后进行查询
        if (mainPanel.getAction() instanceof PortingAddWeakPwdAction && mainPanel instanceof PortingAddWeakPwdPanel) {
            PortingAddWeakPwdPanel addWeakPwdPanel = (PortingAddWeakPwdPanel) mainPanel;
            PortingAddWeakPwdAction addWeakPwdAction = (PortingAddWeakPwdAction) mainPanel.getAction();
            WeakPwdSetPanel weakPwdSetPanel = JsonUtil.getValueIgnoreCaseFromMap(mainPanel.getParams(),
                Panels.WEAK_PWD_SET.panelName(), WeakPwdSetPanel.class);
            if (addWeakPwdAction.createWeakPwd(addWeakPwdPanel.getInputText(), addWeakPwdPanel)) {
                // 更新表格数据
                weakPwdSetPanel.updateTable();
            } else {
                Logger.warn("create Weak Password failed.");
            }
        }
    }
}


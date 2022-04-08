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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.weakpwdpanel;

import com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd.DelWeakPwdAction;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.DelWeakPwdPanel;

import com.intellij.openapi.wm.ToolWindow;

import java.util.Map;

/**
 * 删除弱口令设置面板
 *
 * @since 2012-10-27
 */
public class TuningDelWeakPwdPanel extends DelWeakPwdPanel {
    public TuningDelWeakPwdPanel(ToolWindow toolWindow) {
        super(toolWindow);
    }

    public TuningDelWeakPwdPanel(ToolWindow toolWindow, String panelName, String displayName, Map params) {
        super(toolWindow, panelName, displayName, params);
    }

    public TuningDelWeakPwdPanel(ToolWindow toolWindow, Map params) {
        super(toolWindow, params);
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
        if (action instanceof DelWeakPwdAction) {
            this.action = action;
            registerComponentAction();
        }
    }

    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new DelWeakPwdAction();
        }
    }
}

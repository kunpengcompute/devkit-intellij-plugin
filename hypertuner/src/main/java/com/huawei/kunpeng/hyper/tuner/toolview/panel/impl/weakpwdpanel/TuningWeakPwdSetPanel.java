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

import com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd.WeakPwdManageAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd.WeakPwdTableAddAction;
import com.huawei.kunpeng.hyper.tuner.action.panel.weakpwd.WeakPwdTableDeletedAction;
import com.huawei.kunpeng.intellij.common.bean.WeakPwdBean;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.WeakPwdSetPanel;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ToolbarDecorator;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加弱口令设置面板
 *
 * @since 2012-10-12
 */
public class TuningWeakPwdSetPanel extends WeakPwdSetPanel {
    public TuningWeakPwdSetPanel(ToolWindow toolWindow) {
        super(toolWindow);
    }

    @Override
    protected void setActionToToolBar(ToolbarDecorator toolbarDecorator) {
        TuningWeakPwdSetPanel weakPwdSetPanel = null;
        if (that instanceof TuningWeakPwdSetPanel) {
            weakPwdSetPanel = (TuningWeakPwdSetPanel) that;
        }
        toolbarDecorator.setAddAction(new WeakPwdTableAddAction(this.weakPwdTable, weakPwdSetPanel));
        toolbarDecorator.setRemoveAction(new WeakPwdTableDeletedAction(this.weakPwdTable, weakPwdSetPanel));
    }

    @Override
    protected List<WeakPwdBean> getWeakPwdList() {
        return (action instanceof WeakPwdManageAction)
                ? ((WeakPwdManageAction) action).selectWeakPwdList(this, null)
                : new ArrayList<>();
    }

    /**
     * 注册组件事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = WeakPwdManageAction.getInstance();
        }
    }

    /**
     * 设置事件处理器
     *
     * @param action 处理事件
     */
    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof WeakPwdManageAction) {
            this.action = action;
            registerComponentAction();
        }
    }
}

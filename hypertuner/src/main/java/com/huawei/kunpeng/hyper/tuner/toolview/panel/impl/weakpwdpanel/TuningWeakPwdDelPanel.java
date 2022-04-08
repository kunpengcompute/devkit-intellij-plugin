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
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.WeakPwdDelPanel;

/**
 * 添加弱口令面板
 *
 * @since 2021-2-3
 */
public class TuningWeakPwdDelPanel extends WeakPwdDelPanel {
    /**
     * 完整构造方法，不建议直接使用
     *
     * @param delContext 删除内容
     * @param delID      删除ID
     */
    public TuningWeakPwdDelPanel(String delContext, String delID) {
        super(delContext, delID);
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

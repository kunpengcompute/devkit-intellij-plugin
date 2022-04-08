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

package com.huawei.kunpeng.hyper.tuner.action.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.NodeAddDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.NodeAddPanel;
import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表给修改操作Action
 *
 * @since 2020-11-2
 */
public class NodeAddAction extends IDETableCommonAction {
    /**
     * 主内容面板WeakPwdSetPanel
     */
    private String title = NodeManagerContent.NODE_MANAGER_ADD;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public NodeAddAction(JTable targetTable) {
        super(targetTable);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        NodeAddDialog dialog = new NodeAddDialog(NodeManagerContent.NODE_MANAGER_ADD,
                new NodeAddPanel(title, title));
        dialog.displayPanel();
    }
}

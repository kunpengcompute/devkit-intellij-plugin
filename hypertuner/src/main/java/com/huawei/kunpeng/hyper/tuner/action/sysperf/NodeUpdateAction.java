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
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf.NodeUpdateDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.NodeUpdatePanel;
import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 修改节点Action
 *
 * @since 2020-11-2
 */
public class NodeUpdateAction extends IDETableCommonAction {
    /**
     * 弹框名称和标题
     */
    public static final String TITLE = NodeManagerContent.NODE_MANAGER_UPDATE;

    /**
     * id
     */
    public static final int ID_INDEX = 0;

    /**
     * ip
     */
    public static final int IP_INDEX = 3;


    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public NodeUpdateAction(JTable targetTable) {
        super(targetTable);
    }

    /**
     * 打开弹出框及传递参数
     *
     * @param anActionButton anActionButton
     */
    @Override
    public void run(AnActionButton anActionButton) {
        int row = targetTable.getSelectedRow();
        Object nodeid = targetTable.getValueAt(row, ID_INDEX);
        Object ip = targetTable.getValueAt(row, IP_INDEX);
        NodeUpdateDialog dialog = new NodeUpdateDialog(TITLE, TITLE, new NodeUpdatePanel(nodeid, ip));
        dialog.displayPanel();
    }
}

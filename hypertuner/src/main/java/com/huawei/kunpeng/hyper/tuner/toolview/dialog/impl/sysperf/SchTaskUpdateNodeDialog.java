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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.NodeManagerContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskUpdateNodePanel;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.CommonDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

/**
 * 预约任务 更新节点弹框
 *
 * @since 2021-07-14
 */
public class SchTaskUpdateNodeDialog extends CommonDialog {
    private SchTaskUpdateNodePanel schTaskUpdateNodePanel;

    public SchTaskUpdateNodeDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public SchTaskUpdateNodeDialog(
            String title, String dialogName, IDEBasePanel panel, Boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? NodeManagerContent.NODE_MANAGER_ADD : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? NodeManagerContent.NODE_MANAGER_ADD : dialogName;
        this.mainPanel = panel;
        if (panel instanceof SchTaskUpdateNodePanel) {
            schTaskUpdateNodePanel = (SchTaskUpdateNodePanel) panel;
        }
        // 设置弹框大小是否可变
        this.resizable = resizable;
        setOKAndCancelName(NodeManagerContent.TERM_OPERATE_OK, NodeManagerContent.TERM_OPERATE_CANCEL);
        // 无位置信息时居中显示
        this.rectangle = rectangle;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 确认新增弱口令
     */
    @Override
    protected void onOKAction() {
        schTaskUpdateNodePanel.getNewNodeParamMap();
    }
}

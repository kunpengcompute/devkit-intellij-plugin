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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskManageContent;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.SchTaskUpdateFunctionPanel;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.CommonDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

/**
 * 预约任务
 * 选择函数 弹框
 *
 * @since 2021-07-14
 */
public class SchTaskUpdateFunctionDialog extends CommonDialog {
    private SchTaskUpdateFunctionPanel schTaskUpdateFunctionPanel;

    public SchTaskUpdateFunctionDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, false);
    }

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public SchTaskUpdateFunctionDialog(
            String title, String dialogName, IDEBasePanel panel, Boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName) ? TaskManageContent.PARAM_PARAM_STANDARD_FUNCTION : dialogName;
        this.mainPanel = panel;
        if (panel instanceof SchTaskUpdateFunctionPanel) {
            schTaskUpdateFunctionPanel = (SchTaskUpdateFunctionPanel) panel;
        }

        // 设置弹框大小是否可变
        this.resizable = resizable;
        // 设置弹框中保存取消按钮的名称
        setButtonName();
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
        schTaskUpdateFunctionPanel.getNewFunctionSet();
    }
}
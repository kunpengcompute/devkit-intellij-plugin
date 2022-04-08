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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SchTaskContent;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 预约任务 无法修改 提示弹窗
 *
 * @since 2012-10-12
 */
public class SchTaskNoticeDialog extends IdeaDialog {
    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public SchTaskNoticeDialog(String title, String dialogName, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName) ? SchTaskContent.SCH_TASK_DIALOG_UPDATE_TITLE : dialogName;
        this.mainPanel = panel;
        setOKAndCancelName(SchTaskContent.SCH_TASK_OPERATE_OK, SchTaskContent.SCH_TASK_OPERATE_CANCEL);

        // 初始化弹框内容
        initDialog();
    }

    /**
     * 获取mainPanel
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认操作
     */
    @Override
    protected void onOKAction() {}

    /**
     * 取消操作
     */
    @Override
    protected void onCancelAction() {}
}

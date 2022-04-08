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
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * 预约任务详细信息弹窗
 *
 * @since 2021-5-6
 */
public class SchTaskDetailDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public SchTaskDetailDialog(String title, String dialogName, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? SchTaskContent.SCH_TASK_DIALOG_DETAIL_TITLE : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName) ? SchTaskContent.SCH_TASK_DIALOG_DETAIL_TITLE : dialogName;
        this.mainPanel = panel;

        // 设置弹框中确认取消按钮的名称
        setOKAndCancelName("", TuningI18NServer.toLocale("plugins_common_term_operate_close"));
        // 设置弹框大小是否可变
        this.resizable = true;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 安装部署内容声明
     *
     * @return centerPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认操作
     */
    @Override
    protected void onOKAction() {
        Logger.info("Start AccountTipsDialog.");
    }

    /**
     * onCancelAction 取消操作
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel AccountTipsDialog.");
    }
}

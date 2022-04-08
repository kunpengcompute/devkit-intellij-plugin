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

import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.TaskTemplateContent;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * 任务模板 详细信息弹窗
 *
 * @since 2020-04-22
 */
public class TaskTemplateDetailDialog extends IdeaDialog {
    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     */
    public TaskTemplateDetailDialog(String title, String dialogName, IDEBasePanel panel) {
        this.title = StringUtil.stringIsEmpty(title) ? TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DETAIL : title;
        this.dialogName =
                StringUtil.stringIsEmpty(dialogName)
                        ? TaskTemplateContent.TASK_TEMPLATE_DIALOG_TITLE_DETAIL
                        : dialogName;
        this.mainPanel = panel;
        this.resizable = true;
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 重写按钮显示
     *
     * @return 返回。
     */
    @Override
    protected Action[] createActions() {
        this.myOKAction = new MyOKAction(UserManageConstant.TERM_OPERATE_CLOSE);
        return new Action[]{getOKAction()};
    }

    /**
     * 自定义确认按钮
     *
     * @since 2020-10-08
     */
    protected class MyOKAction extends DialogWrapper.DialogWrapperAction {
        /**
         * 构造函数
         *
         * @param name name
         */
        protected MyOKAction(@NotNull String name) {
            super(name);
        }

        /**
         * 点击确认事件
         *
         * @param event 假如ovVerify不通过，则不会触发确定事件
         */
        @Override
        protected void doAction(ActionEvent event) {
            if (okVerify()) {
                close(CANCEL_EXIT_CODE);
            }
        }
    }

    /**
     * 获取mainPanel
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
        Logger.info("OK Action.");
    }

    /**
     * onCancelAction 取消操作
     */
    @Override
    protected void onCancelAction() {
        Logger.info("Cancel AccountTipsDialog.");
    }
}

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

package com.huawei.kunpeng.porting.ui.dialog.crl;

import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.porting.ui.panel.settings.crl.CRLDetailPanel;

import com.intellij.openapi.ui.DialogWrapper;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * The class: CRLDetailDialog: CRL 详细信息Dialog
 *
 * @since 2021-8-11
 */
public class CRLDetailDialog extends IdeaDialog {
    private static final String DIALOG_NAME = "CRL_DETAIL";
    private CRLDetailPanel mainPanel;

    public CRLDetailDialog(CRLDetailPanel panel) {
        this.title = I18NServer.toLocale("plugins_porting_setting_crl_detail");
        this.dialogName = DIALOG_NAME;
        this.mainPanel = panel;
        this.rectangle = new Rectangle(600, 360);
        initDialog();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel.getPanel();
    }

    @Override
    protected void onOKAction() {
    }

    @Override
    protected void onCancelAction() {
    }

    /**
     * 重写按钮显示
     *
     * @return Action[]
     */
    @Override
    protected Action[] createActions() {
        this.myCancelAction = new CRLDetailDialog.MyCancelAction(I18NServer.toLocale("plugins_porting_close"));
        return new Action[] {getCancelAction()};
    }

    /**
     * 自定义确认按钮
     *
     * @since 2020-10-08
     */
    protected class MyCancelAction extends DialogWrapper.DialogWrapperAction {
        /**
         * 构造函数
         *
         * @param name name
         */
        protected MyCancelAction(@NotNull String name) {
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
}

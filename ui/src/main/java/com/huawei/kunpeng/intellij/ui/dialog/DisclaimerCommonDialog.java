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

package com.huawei.kunpeng.intellij.ui.dialog;

import com.huawei.kunpeng.intellij.common.util.I18NServer;

import com.intellij.openapi.ui.DialogWrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 功能描述 DisclaimerCommonDialog
 *
 * @since 2021-09-09
 */
public class DisclaimerCommonDialog extends IdeaDialog {
    /**
     * 按钮长度
     */
    protected static final int BUTTON_LENGTH = 2;

    /**
     * Success
     */
    protected static final String SUCCESS_CODE = "UserManage.Success";
    /**
     * 是否处理事件， 默认处理
     */
    protected boolean isDoEvent = true;

    /**
     * 重写按钮显示
     *
     * @return 返回。
     */
    @Override
    protected Action[] createActions() {
        if (!this.isDoEvent) {
            return new Action[]{getCancelAction()};
        }
        this.myCancelAction = new MyCancelAction(I18NServer.toLocale("plugins_common_button_cancel"));
        return new Action[]{this.getOKAction(), getCancelAction()};
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
         * @param value value
         */
        protected MyCancelAction(@NotNull String value) {
            super(value);
        }

        /**
         * 点击确认事件
         *
         * @param event 假如ovVerify不通过，则不会触发确定事件
         */
        @Override
        protected void doAction(ActionEvent event) {
            if (!isDoEvent || okVerify()) {
                close(CANCEL_EXIT_CODE);
            }
        }
    }

    /**
     * 点×方法重写
     */
    public void doCancelAction() {
        if (!isDoEvent || okVerify()) {
            super.doCancelAction();
        }
    }

    /**
     * 勾选监听
     */
    public void addChangeListener() {
        JComponent jCheckBox = createDoNotAskCheckbox();
        if (!(jCheckBox instanceof JCheckBox)) {
            return;
        }
        setButtonCheck((JCheckBox) jCheckBox);
    }


    private void setButtonCheck(JCheckBox jCheckBox) {
        jCheckBox
                .addChangeListener(
                        new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent changeEvent) {
                                Object checkBoxObj = changeEvent.getSource();
                                if (checkBoxObj instanceof JCheckBox) {
                                    doEvent((JCheckBox) checkBoxObj);
                                }
                            }

                            private void doEvent(JCheckBox checkBoxObj) {
                                JCheckBox checkBox = checkBoxObj;
                                if (checkBox.isSelected()) {
                                    getOKAction().setEnabled(true);
                                } else {
                                    getOKAction().setEnabled(false);
                                }
                            }
                        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return null;
    }

    @Override
    protected void onOKAction() {
    }

    @Override
    protected void onCancelAction() {
    }
}

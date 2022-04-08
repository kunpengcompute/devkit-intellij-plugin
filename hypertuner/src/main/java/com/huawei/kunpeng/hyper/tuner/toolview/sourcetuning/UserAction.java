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

package com.huawei.kunpeng.hyper.tuner.toolview.sourcetuning;

import com.huawei.kunpeng.hyper.tuner.action.IDELogoutAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.wrap.TuningChangePasswordDialog;
import com.huawei.kunpeng.intellij.ui.dialog.IDEBaseDialog;
import com.huawei.kunpeng.intellij.ui.panel.ChangePasswordPanel;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;

/**
 * The class UserAction
 *
 * @since v1.0
 */
public class UserAction extends AnAction implements DumbAware {
    private static final String CHANGE_PASSWORD_TEXT = TuningUserManageConstant.LEFTTREE_CHANGE_PASSWORD;

    private static final String LOGOUT_TEXT = TuningUserManageConstant.LEFTTREE_LOGOUT;

    @NotNull
    private final UserAction.Action action;

    /**
     * 构造函数
     *
     * @param action action
     */
    public UserAction(@NotNull UserAction.Action action) {
        super(action.toString(), null, null);
        this.action = action;
    }

    /**
     * The enum: Action of user
     *
     * @since v1.0
     */
    public enum Action {
        CHANGE_PASSWORD,
        LOGOUT;

        /**
         * 根据不同动作弹出对应的Dialog
         *
         * @param project project
         * @param event   AnActionEvent
         */
        void applyTo(Project project, AnActionEvent event) {
            switch (this) {
                case CHANGE_PASSWORD:
                    // 弹出修改用户名密码Dialog
                    IDEBasePanel panel = new ChangePasswordPanel(null);
                    IDEBaseDialog changePwdDialog = new TuningChangePasswordDialog(null, panel);
                    changePwdDialog.displayPanel();
                    break;
                case LOGOUT:
                    new IDELogoutAction().actionPerformed(event);
                    break;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case CHANGE_PASSWORD:
                    return CHANGE_PASSWORD_TEXT;
                case LOGOUT:
                    return LOGOUT_TEXT;
            }
            return super.toString();
        }
    }

    /**
     * 响应
     *
     * @param event AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        action.applyTo(project, event);
    }
}

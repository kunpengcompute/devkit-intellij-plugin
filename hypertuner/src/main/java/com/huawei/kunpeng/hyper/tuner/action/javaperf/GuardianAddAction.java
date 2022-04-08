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

package com.huawei.kunpeng.hyper.tuner.action.javaperf;

import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.javaperf.GuardianAddDialog;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.javaperf.GuardianAddPanel;
import com.huawei.kunpeng.intellij.common.action.IDETableCommonAction;

import com.intellij.ui.AnActionButton;

import javax.swing.JTable;

/**
 * 用户表给修改操作Action
 *
 * @since 2020-11-2
 */
public class GuardianAddAction extends IDETableCommonAction {
    /**
     * 主内容面板WeakPwdSetPanel
     */
    private String title = GuardianMangerConstant.GUARDIAN_ADD_TITLE;

    /**
     * 构造函数
     *
     * @param targetTable 目标表格
     */
    public GuardianAddAction(JTable targetTable) {
        super(targetTable);
    }

    @Override
    public void run(AnActionButton anActionButton) {
        GuardianAddDialog dialog = new GuardianAddDialog(GuardianMangerConstant.GUARDIAN_ADD_TITLE,
                new GuardianAddPanel(title));
        dialog.displayPanel();
    }
}

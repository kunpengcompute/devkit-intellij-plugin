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

import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;

import java.awt.Dimension;

import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * The class: ReachMaxNumCRLDialog
 *
 * @since 2021-8-12
 */
public class ReachMaxNumCRLDialog extends IdeaDialog {
    public ReachMaxNumCRLDialog() {
        this.dialogName = I18NServer.toLocale("plugins_porting_using_account_title_name");
        this.title = I18NServer.toLocale("plugins_porting_using_account_title_name");
        setCancelButtonText(I18NServer.toLocale("plugins_common_term_operate_close"));
        initDialog();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JLabel label = new JLabel();
        label.setText(I18NServer.toLocale("plugins_porting_max_num_crl"));
        label.setIcon(BaseIntellijIcons.load(IDEConstant.ICON_INFO));
        label.setPreferredSize(new Dimension(509, 34));
        return label;
    }

    /**
     * 重写按钮显示
     *
     * @return 返回。
     */
    @Override
    protected Action[] createActions() {
        return new Action[] {getCancelAction()};
    }

    @Override
    protected void onOKAction() {
    }

    @Override
    protected void onCancelAction() {
    }
}

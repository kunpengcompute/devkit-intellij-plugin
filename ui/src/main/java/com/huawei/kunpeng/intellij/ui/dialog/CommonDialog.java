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

import com.huawei.kunpeng.intellij.common.i18n.CommonI18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.List;

/**
 * 弹框
 *
 * @since 2021-09-07
 */
public class CommonDialog extends IdeaDialog {
    /**
     * 主要内容展示面板
     */
    protected IDEBasePanel mainPanel;

    /**
     * 添加弱口令弹框内容
     *
     * @return mainPanel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 确认删除弱口令
     */
    @Override
    protected void onCancelAction() {
    }

    @Override
    protected void onOKAction() {
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected ValidationInfo doValidate() {
        return this.mainPanel.doValidate();
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    protected List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = this.mainPanel.doValidateAll();
        if (ValidateUtils.isNotEmptyCollection(result)) {
            this.okAction.setEnabled(false);
        } else {
            this.okAction.setEnabled(true);
        }
        return result;
    }

    /**
     * 设置弹框中保存取消按钮的名称
     */
    protected void setButtonName() {
        setOKAndCancelName(
                CommonI18NServer.toLocale("common_term_operate_ok"),
                CommonI18NServer.toLocale("common_term_operate_cancel"));
    }
}
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

package com.huawei.kunpeng.hyper.tuner.toolview.dialog.impl.logger;

import com.huawei.kunpeng.hyper.tuner.common.constant.TuningLogManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.RunLogPanel;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.dialog.IdeaDialog;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 修改用户弹框。
 *
 * @since 2020-10-11
 */
public class RunLogDialog extends IdeaDialog {
    private RunLogPanel runLogPanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public RunLogDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? TuningLogManageConstant.RUN_LOG_TITLE : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? "RunLog" : dialogName;
        mainPanel = panel;
        if (panel instanceof RunLogPanel) {
            runLogPanel = (RunLogPanel) panel;
        }
        // 设置弹框大小是否可变
        this.resizable = resizable;
        // 设置按钮
        setOKAndCancelName(TuningUserManageConstant.TERM_OPERATE_OK, TuningUserManageConstant.TERM_OPERATE_CANCEL);
        // 初始化弹框内容
        initDialog();
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public RunLogDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, true);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        // 初始化面板容器
        super.initDialog();
        // 初始化置灰OK按钮
        okAction.setEnabled(false);
        addListenerToTable();
    }

    @Override
    protected void onOKAction() {
    }

    /**
     * 点击确定事件
     *
     * @return 下载结果
     */
    @Override
    protected boolean okVerify() {
        return runLogPanel.onOK();
    }

    @Override
    protected void after() {
    }

    /**
     * 点击取消或关闭事件
     */
    @Override
    protected void onCancelAction() {
        Logger.info("the user cancel action.");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    @Override
    protected List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> result = mainPanel.doValidateAll();
        if (ValidateUtils.isNotEmptyCollection(result)) {
            okAction.setEnabled(false);
        } else {
            okAction.setEnabled(true);
        }
        return result;
    }

    /**
     * 添加表格监视事件。
     */
    private void addListenerToTable() {
        runLogPanel
                .getRunLogTable()
                .getSelectionModel()
                .addListSelectionListener(
                        new ListSelectionListener() {
                            @Override
                            public void valueChanged(ListSelectionEvent e) {
                                okAction.setEnabled(true);
                            }
                        });
    }
}

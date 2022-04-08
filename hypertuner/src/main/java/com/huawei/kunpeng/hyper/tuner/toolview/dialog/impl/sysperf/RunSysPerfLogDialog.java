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

import com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf.RunSysPerfLogPanel;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
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
 * 操作日志下载弹窗
 *
 * @since 2020-10-11
 */
public class RunSysPerfLogDialog extends IdeaDialog {
    /**
     * 国际化 操作
     */
    public static final String RUN_LOG_TITLE = I18NServer.toLocale("plugins_hyper_tuner_runlog_dialog_title");

    private RunSysPerfLogPanel runLogPanel;

    /**
     * 带位置信息的完整构造函数（不推荐使用）
     *
     * @param title      弹窗标题
     * @param dialogName 弹框名称
     * @param panel      需要展示的面板之一
     * @param resizable  大小是否可变
     */
    public RunSysPerfLogDialog(String title, String dialogName, IDEBasePanel panel, boolean resizable) {
        this.title = StringUtil.stringIsEmpty(title) ? RUN_LOG_TITLE : title;
        this.dialogName = StringUtil.stringIsEmpty(dialogName) ? "RunSysPerfLogDialog" : dialogName;
        mainPanel = panel;
        if (panel instanceof RunSysPerfLogPanel) {
            runLogPanel = (RunSysPerfLogPanel) panel;
        }
        this.resizable = resizable; // 设置弹框大小是否可变
        setOKAndCancelName(UserManageConstant.TERM_OPERATE_OK, UserManageConstant.TERM_OPERATE_CANCEL); // 设置按钮
        initDialog(); // 初始化弹框内容
    }

    /**
     * 带位置信息的构造函数
     *
     * @param title 弹窗标题
     * @param panel 需要展示的面板之一
     */
    public RunSysPerfLogDialog(String title, IDEBasePanel panel) {
        this(title, null, panel, true);
    }

    /**
     * 初始化弹框
     */
    @Override
    protected void initDialog() {
        super.initDialog(); // 初始化面板容器
        okAction.setEnabled(false); // 初始化置灰OK按钮
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
